import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.nio.file.attribute.AttributeView;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AgenteCentral extends Agent {

    Mapa mapa;
    Map<AID, AgentStatus> agents;
    DeltaSimulationStatus dss;
    int taskId;

    protected void setup() {
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.taskId = 0;

        DFManager.registerAgent(this, "Central");

        addBehaviour(new ReceiveInfo());
        this.agents = new HashMap<>();
        this.dss = new DeltaSimulationStatus();
    }

    /**
     * Behaviour destinado à receção de mensagens.
     * - aviso de incêndios por parte do agente incendiário
     * - aviso de status dos agentes participativos
     */
    private class ReceiveInfo extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                AID sender = msg.getSender();
                String sendersName = sender.getLocalName();

                if (sendersName.contains("Incendiario") && msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        FireAlert fa = (FireAlert) msg.getContentObject();
                        processFireAlert(fa);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (sendersName.contains("Agente") && msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        AgentStatus st = (AgentStatus) msg.getContentObject();
                        atualizarEstadoAgente(sender, st);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (sendersName.equals("Interface") && msg.getPerformative() == ACLMessage.QUERY_REF) {
                    sendSimulationInfo(msg);
                }
            } else {
                block();
            }
        }

    }

    private class AssignTask extends OneShotBehaviour {

        private AID agent;
        private LinkedList<Tarefa> tarefas;

        public AssignTask(AID agentAID, Tarefa... tarefas) {
            this.tarefas = new LinkedList<>();
            this.agent = agentAID;

            Arrays.stream(tarefas).forEach(t -> {
                this.tarefas.add(t);
                registaTarefa(agent, t);

                //System.out.println(new Time(System.currentTimeMillis()) +  ": "+ agent.getLocalName() + " --- mandado " + t.toString());

            });
        }

        public void action() {

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(agent);
            try {
                msg.setContentObject(this.tarefas);
            } catch (IOException e) {
                e.printStackTrace();
            }
            send(msg);
        }
    }


    /**
     * Método para registar novo incendio ou expansao de um ja existente e
     * e alocar os devidos recursos
     */

    private void processFireAlert(FireAlert fa) {
        System.out.println(fa.toString());
        if(this.mapa.incendios.get(fa.fireID) == null){
            this.mapa.registaIncendio(fa);
        } else {
            this.mapa.atualizaIncendio(fa);
        }

        this.dss.novosIncendios.add(fa.celulaIgnicao);

        alocaRecursos(this.mapa.incendios.get(fa.fireID));
    }


    private void atualizarEstadoAgente(AID agent, AgentStatus status) {

        // registar celulas apagadas
        status.tarefas.stream().
                filter(tarefa -> tarefa.tipo == Tarefa.APAGAR).
                forEach(tarefa -> this.dss.celulasApagadas.add(tarefa.posicao));

        // registar novas posicoes
        this.dss.estadoAgentes.add(status);

        AgentStatus as = this.agents.get(agent);


        if (as != null)
            as.atualizarEstado(status);
        else
            this.agents.put(agent, status);
    }


    private void alocaRecursos(Incendio incendio) {
        Tarefa abaster, apagar;
        AID choosenAgent = null;
        int minTempo = 100000; // 100 segundos
        Posicao posto = null;


        // por enquanto so temos uma celula a arder, alocar com base na distancia a essa celula
        Posicao p = incendio.areaAfetada.get(0);

        for (AgentStatus ap : this.agents.values()){
                AbstractMap.SimpleEntry<Integer, Posicao> disponibilidade = checkDisponibilidadeAgente(ap, p);
                int tempoParaFicarDisponivel = disponibilidade.getKey();
                Posicao ondeAbastecer = disponibilidade.getValue();
                if ( tempoParaFicarDisponivel < minTempo) {
                    minTempo = tempoParaFicarDisponivel;
                    choosenAgent = ap.aid;
                    posto = ondeAbastecer;
                }
        }

        //System.out.println(agents.get(closestAgent).toString() + " tempoSomaTotal FINAL: " + minTempo + " " + agents.get(closestAgent).tipo);

        if(posto != null){
            abaster = new Tarefa(taskId++, Tarefa.ABASTECER, posto);
            this.addBehaviour(new AssignTask(choosenAgent, abaster));
        }

        apagar = new Tarefa(taskId++, Tarefa.APAGAR, incendio.fireId, p); // +1000 -> ação final (APAGAR)
        this.addBehaviour(new AssignTask(choosenAgent, apagar));
    }

    AbstractMap.SimpleEntry<Integer, Posicao> checkDisponibilidadeAgente(AgentStatus ap, Posicao incendio){
        Posicao ondeAbastecer = null;
        int tempo = 0;
        // calcular combustivel e posição do agente após este realizar todas as suas tarefas
        int maxFuel = 0;
        int combustivel = ap.combustivelDisponivel;
        Posicao posição = ap.posAtual;

        switch (ap.tipo){
            case "Drone":
                maxFuel = Drone.capacidadeMaxCombustivel;
            case "Firetruck":
                maxFuel = Camiao.capacidadeMaxCombustivel;
            case "plane":
                maxFuel = Aeronave.capacidadeMaxCombustivel;
        }

        for(Tarefa t : ap.tarefas){
            int distancia = Posicao.distanceBetween(posição, t.posicao);
            combustivel -= distancia;
            if(t.tipo == Tarefa.ABASTECER) combustivel = maxFuel;
            posição = t.posicao;
            tempo += distancia + 1000;
        }

        int distanciaAgenteIncendio = Posicao.distanceBetween(posição, incendio);
        int distanciaPostoMaisProxIncendio = getMinDistanceIncendioPosto(incendio);

        boolean temCombustivelSuficiente = combustivel > distanciaAgenteIncendio  && combustivel >= (distanciaAgenteIncendio + distanciaPostoMaisProxIncendio) ;

        if(!temCombustivelSuficiente){
            AbstractMap.SimpleEntry<Posicao, Integer> postoMaisProximo = this.mapa.getPostoEntreAgenteIncendio(posição, incendio);
            tempo += postoMaisProximo.getValue() + 1000;
            ondeAbastecer = postoMaisProximo.getKey();
        }
        else tempo += distanciaAgenteIncendio;

        return new AbstractMap.SimpleEntry<Integer, Posicao>(tempo, ondeAbastecer);
    }

    private int autonomiaCombustivel(AgentStatus ap, Posicao incendio){
        int combSomaTotal = 0;

        // apagar
        int distAgenteIncendio;
        if(ap.disponivel){
            //System.out.println(ap.toString() + " DIRETO");
            distAgenteIncendio = Posicao.distanceBetween(ap.posAtual, incendio);
        }
        else{
            //System.out.println(ap.toString() + " INDIRETO");
            int distAgenteTarefaFinal = ap.tempoParaFicarDisponivel/((4/this.agents.get(ap.aid).velocidade)*1000);
            //System.out.println(ap.toString() + " distAgenteTarefaFinal: " + distAgenteTarefaFinal + " " + ap.tipo);
            int distTarefaFinalIncendio = Posicao.distanceBetween(ap.tarefas.get(ap.tarefas.size()-1).posicao, incendio);
            //System.out.println(ap.toString() + " distTarefaFinalIncendio: " + distTarefaFinalIncendio + " " + ap.tipo);
            distAgenteIncendio = distAgenteTarefaFinal + distTarefaFinalIncendio;
        }

        int minTempoAgenteIncendio = distAgenteIncendio*(4/this.agents.get(ap.aid).velocidade)*1000;

        //System.out.println(ap.toString() + " distAgenteIncendio: " + distAgenteIncendio + " " + ap.tipo);
        //System.out.println(ap.toString() + " minTempoAgenteIncendio: " + minTempoAgenteIncendio + " " + ap.tipo);
        combSomaTotal += distAgenteIncendio;

        // abastecer
        int minDistIncendioPosto = getMinDistanceIncendioPosto(incendio);
        //System.out.println(ap.toString() + " distIncendioPosto: " + minDistIncendioPosto + " " + ap.tipo);

        combSomaTotal += minDistIncendioPosto;

        //System.out.println(ap.toString() + " combDisponivel " + ap.combustivelDisponivel + " combSomaTotal " + combSomaTotal + " " + ap.tipo);

        if(ap.combustivelDisponivel>combSomaTotal) return minTempoAgenteIncendio;
        else return -1;
    }

    private int getMinDistanceIncendioPosto(Posicao incendio){
        int minDistance = 1000;

        for(Posicao p : mapa.postosCombustivel){
            int distance = Posicao.distanceBetween(incendio,p);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;
    }

    private void registaTarefa(AID agent, Tarefa t) {
        this.agents.get(agent).addTarefa(t);
    }


    private void sendSimulationInfo(ACLMessage msg) {
        //this.dss.estadoAgentes = new ArrayList<>(this.agents.values()); // tem que se tirar esta linha e passar a registar apenas novas posicoes
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        try {
            reply.setContentObject(this.dss);
        } catch (IOException e) {
            e.printStackTrace();
        }
        send(reply);
        this.dss = new DeltaSimulationStatus(); // reset ao objeto para guardar apenas novas alteracoes
    }
}

