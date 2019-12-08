import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;
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
                }
                else if (sendersName.contains("Agente") && msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        AgentStatus st = (AgentStatus) msg.getContentObject();
                        atualizarEstadoAgente(sender, st);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (sendersName.contains("Interface") && msg.getPerformative() == ACLMessage.QUERY_REF) {
                    sendSimulationInfo(msg);
                }
                else if (sendersName.contains("Interface") && msg.getPerformative() == ACLMessage.REQUEST && msg.getContent().equals("STOP")) {
                    myAgent.doDelete();
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

                System.out.println(new Time(System.currentTimeMillis()) +  ": "+ agent.getLocalName() + " --- mandado " + t.toString());

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
        //System.out.println(fa.toString());
        if(this.mapa.incendios.get(fa.fireID) == null){
            this.mapa.registaIncendio(fa);
        } else {
            this.mapa.atualizaIncendio(fa);
        }

        for(Posicao p: fa.celulasFogo)
            this.dss.novosIncendios.add(p);

        alocaRecursos(this.mapa.incendios.get(fa.fireID));
    }


    private void atualizarEstadoAgente(AID agent, AgentStatus status) {

        /*
        // registar celulas apagadas
        status.tarefas.stream().
                filter(tarefa -> tarefa.tipo == Tarefa.APAGAR).
                filter(tarefa -> tarefa.tempo < 4000).
                forEach(tarefa -> this.dss.celulasApagadas.add(tarefa.posicao));*/

        List postosCombAtivos = mapa.getAllPostosCombustiveisAtivos();

        for(Tarefa t : status.tarefas){

            dss.registaTarefas(status.tipo,t);

            if(t.tipo == Tarefa.APAGAR) {
                if (t.tempoTarefa <= SimulationConfig.TEMPO_QUEIMAR_CELULA) {
                    dss.celulasApagadas.add(t.posicao);
                } else{

                    dss.celulasArdidas.add(t.posicao);
                    mapa.areaArdida.add(t.posicao);

                    if(postosCombAtivos.contains(t.posicao)){
                        for(PostoCombustivel posto : mapa.postosCombustivel){
                            if(posto.pos.equals(t.posicao))
                                posto.ativo = false;
                        }
                    }

                }
            }
        }

        // registar novas posicoes
        this.dss.estadoAgentes.add(status);

        AgentStatus as = this.agents.get(agent);


        if (as != null)
            as.atualizarEstado(status);
        else
            this.agents.put(agent, status);
    }

    private void alocaRecursos(Incendio incendio) {
        Map<AID, Object[]> dadosAgentes = new HashMap<>();

        AID choosenAgent = null;
        AID secondChoosenAgent = null;
        int minTempo = 100000; // 100 segundos
        int secondMinTempo = 200000;

        for(Posicao p: incendio.areaAfetada) {
            int i=0;

            for (AgentStatus ap : this.agents.values()) {
                Object[] dadosAgente = checkDisponibilidadeAgente(ap, p); // Tempo minimo, e Posicao de onde abastecer caso seja indicado a faze-lo
                dadosAgentes.put(ap.aid, dadosAgente);

                int tempoParaFicarDisponivel = (int) dadosAgente[0];

                if (tempoParaFicarDisponivel >= minTempo && tempoParaFicarDisponivel < secondMinTempo) {
                    secondMinTempo = tempoParaFicarDisponivel;
                    secondChoosenAgent = ap.aid;
                }
                else if (tempoParaFicarDisponivel < minTempo) {
                    secondMinTempo = minTempo;
                    minTempo = tempoParaFicarDisponivel;
                    secondChoosenAgent = choosenAgent;
                    choosenAgent = ap.aid;
                }
            }

            //System.out.println(agents.get(closestAgent).toString() + " tempoSomaTotal FINAL: " + minTempo + " " + agents.get(closestAgent).tipo);
            Object[] dadosAgenteEscolhido = dadosAgentes.get(choosenAgent);
            List<Tarefa> tarefasAgenteEscolhido = new ArrayList<>();

            if(dadosAgenteEscolhido.length == 2){
                AbstractMap.SimpleEntry<Integer, Posicao> tarefa = (AbstractMap.SimpleEntry<Integer, Posicao>) dadosAgenteEscolhido[1];
                tarefasAgenteEscolhido.add( new Tarefa(taskId++, tarefa.getKey(), tarefa.getValue()));
            }
            if(dadosAgenteEscolhido.length == 3){
                AbstractMap.SimpleEntry<Integer, Posicao> tarefa1 = (AbstractMap.SimpleEntry<Integer, Posicao>) dadosAgenteEscolhido[1];
                AbstractMap.SimpleEntry<Integer, Posicao> tarefa2 = (AbstractMap.SimpleEntry<Integer, Posicao>) dadosAgenteEscolhido[2];

                tarefasAgenteEscolhido.add(new Tarefa(taskId++, tarefa1.getKey(), tarefa1.getValue()));
                tarefasAgenteEscolhido.add(new Tarefa(taskId++, tarefa2.getKey(), tarefa2.getValue()));
            }

            tarefasAgenteEscolhido.add(new Tarefa(taskId++, Tarefa.APAGAR, incendio.fireId, p, minTempo));

            this.addBehaviour(new AssignTask(choosenAgent, tarefasAgenteEscolhido.toArray(new Tarefa[tarefasAgenteEscolhido.size()])));


            if (mapa.floresta.contains(p)) { // caso a tarefa seja numa floresta e exiga segundo agente (agente preventivo)

                List<Posicao> adjFloresta;
                Posicao pAdjacent;
                adjFloresta = mapa.posicoesFlorestaAdjacente(p);

                if(!adjFloresta.isEmpty()) {

                    pAdjacent = mapa.getRandAdjacentPositions(adjFloresta);

                    Object[] dadosAgentePreventivo = dadosAgentes.get(secondChoosenAgent);
                    List<Tarefa> tarefasAgentePreventivo = new ArrayList<>();

                    if(dadosAgentePreventivo.length == 2){
                        AbstractMap.SimpleEntry<Integer, Posicao> tarefa = (AbstractMap.SimpleEntry<Integer, Posicao>) dadosAgentePreventivo[1];
                        tarefasAgenteEscolhido.add( new Tarefa(taskId++, tarefa.getKey(), tarefa.getValue()));
                    }
                    if(dadosAgentePreventivo.length == 3){
                        AbstractMap.SimpleEntry<Integer, Posicao> tarefa1 = (AbstractMap.SimpleEntry<Integer, Posicao>) dadosAgentePreventivo[1];
                        AbstractMap.SimpleEntry<Integer, Posicao> tarefa2 = (AbstractMap.SimpleEntry<Integer, Posicao>) dadosAgentePreventivo[2];

                        tarefasAgentePreventivo.add(new Tarefa(taskId++, tarefa1.getKey(), tarefa1.getValue()));
                        tarefasAgentePreventivo.add(new Tarefa(taskId++, tarefa2.getKey(), tarefa2.getValue()));
                    }

                    tarefasAgentePreventivo.add(new Tarefa(taskId++, Tarefa.PREVENIR, incendio.fireId, pAdjacent));

                    this.addBehaviour(new AssignTask(secondChoosenAgent, tarefasAgentePreventivo.toArray(new Tarefa[tarefasAgentePreventivo.size()])));
                }
            }
        }

    }


    Object[] checkDisponibilidadeAgente(AgentStatus ap, Posicao incendio){
        Object[] dadosAgente = new Object[3];

        int tempoTotal = 0;
        // calcular combustivel e posição do agente após este realizar todas as suas tarefas
        int maxFuel = 0;
        int maxAgua = 0;
        int velocidadeAgente = 1;
        int combustivelAgente = ap.combustivelDisponivel;
        int agua = ap.aguaDisponivel;
        Posicao posicaoAgente = ap.posAtual;

        switch (ap.tipo){
            case "Drone":
                velocidadeAgente = Drone.velocidade;
                maxFuel = Drone.capacidadeMaxCombustivel;
                maxAgua = Drone.capacidadeMaxAgua;
                break;
            case "Firetruck":
                velocidadeAgente = Camiao.velocidade;
                maxFuel = Camiao.capacidadeMaxCombustivel;
                maxAgua = Camiao.capacidadeMaxAgua;
                break;
            case "Plane":
                velocidadeAgente = Aeronave.velocidade;
                maxFuel = Aeronave.capacidadeMaxCombustivel;
                maxAgua = Aeronave.capacidadeMaxAgua;
                break;
        }


        for(Tarefa tarefa : ap.tarefas){
            int distancia = Posicao.distanceBetween(posicaoAgente, tarefa.posicao);

            combustivelAgente -= distancia;

            switch(tarefa.tipo){
                case Tarefa.ABASTECERCOMB:
                    combustivelAgente = maxFuel;
                    break;
                case Tarefa.ABASTECERAGUA:
                    agua = maxAgua;
                    break;
                case Tarefa.APAGAR:
                    agua--;
                    break;
            }

            tempoTotal += (distancia*(6 / velocidadeAgente))*1000 + 1000;

            posicaoAgente = tarefa.posicao;

        }

        int distanciaAgenteIncendio = Posicao.distanceBetween(posicaoAgente, incendio);
        int distanciaPostoCombMaisProxIncendio = getMinDistanceIncendioPostoComb(incendio);

        boolean temCombustivelSuficiente = combustivelAgente >= (distanciaAgenteIncendio + distanciaPostoCombMaisProxIncendio);
        boolean temAguaSuficiente = agua > 0;

        AbstractMap.SimpleEntry<Posicao, Integer> postoCombMaisProximo = this.mapa.getPostoCombEntreAgenteIncendio(posicaoAgente, incendio); // Posicao do melhor posto, e Distancia minima do agente ao incendio, passando pelo posto
        AbstractMap.SimpleEntry<Posicao, Integer> postoAguaMaisProximo = this.mapa.getPostoAguaEntreAgenteIncendio(posicaoAgente, incendio);

        Posicao ondeAbastecerComb = postoCombMaisProximo.getKey();
        int distAgenteCombIncendio = postoCombMaisProximo.getValue();
        Posicao ondeAbastecerAgua = postoAguaMaisProximo.getKey();
        int distAgenteAguaIncendio = postoAguaMaisProximo.getValue();

        if(temCombustivelSuficiente && temAguaSuficiente){
            tempoTotal += (distanciaAgenteIncendio * (6 / velocidadeAgente)) * 1000;

            dadosAgente = new Object[] {tempoTotal};
        }
        else if(!temCombustivelSuficiente && temAguaSuficiente){
            tempoTotal += (distAgenteCombIncendio * (6 / velocidadeAgente))*1000 + 1000;

            dadosAgente = new Object[] {tempoTotal, new AbstractMap.SimpleEntry<>(Tarefa.ABASTECERCOMB, ondeAbastecerComb)};
        }
        else if (temCombustivelSuficiente && !temAguaSuficiente){
            tempoTotal += (distAgenteAguaIncendio * (6 / velocidadeAgente)) * 1000;

            dadosAgente = new Object[] {tempoTotal, new AbstractMap.SimpleEntry<>(Tarefa.ABASTECERAGUA, ondeAbastecerAgua)};
        }
        else if(!temCombustivelSuficiente && !temAguaSuficiente){
            int distAgenteCombustivel = Posicao.distanceBetween(posicaoAgente, ondeAbastecerComb);
            int distAgenteAgua = Posicao.distanceBetween(posicaoAgente, ondeAbastecerAgua);
            int distCombustivelIncendio = Posicao.distanceBetween(ondeAbastecerComb, incendio);
            int distAguaIncendio = Posicao.distanceBetween(ondeAbastecerAgua, incendio);
            int distAguaCombustivel = Posicao.distanceBetween(ondeAbastecerAgua, ondeAbastecerComb);

            int tempoAbastercerCombPrimeiro = distAgenteCombustivel + distAguaCombustivel + distAguaIncendio;
            int tempoAbastecerAguaPrimeiro = distAgenteAgua + distAguaCombustivel + distCombustivelIncendio;

            if(tempoAbastercerCombPrimeiro < tempoAbastecerAguaPrimeiro) {
                tempoTotal += tempoAbastercerCombPrimeiro;
                dadosAgente = new Object[] {tempoTotal, new AbstractMap.SimpleEntry<>(Tarefa.ABASTECERCOMB, ondeAbastecerComb), new AbstractMap.SimpleEntry<>(Tarefa.ABASTECERAGUA, ondeAbastecerAgua)};
            } else{
                tempoTotal += tempoAbastecerAguaPrimeiro;
                dadosAgente = new Object[] {tempoTotal, new AbstractMap.SimpleEntry<>(Tarefa.ABASTECERAGUA, ondeAbastecerAgua),  new AbstractMap.SimpleEntry<>(Tarefa.ABASTECERCOMB, ondeAbastecerComb)};
            }
        }

        return dadosAgente;
    }



    private int getMinDistanceIncendioPostoComb(Posicao incendio){
        int minDistance = 1000;

        for(PostoCombustivel p : mapa.postosCombustivel){
            int distance = Posicao.distanceBetween(incendio,p.pos);
            if (p.ativo == true && distance < minDistance) {
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

    protected void takeDown(){
        DFManager.deRegister(this);
    }
}

