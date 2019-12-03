import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.sql.Time;
import java.util.*;

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
        AID closestAgent = null;
        int minTempo = 100000; // 100 segundos
        int tempoAtual;

        // por enquanto so temos uma celula a arder, alocar com base na distancia a essa celula
        Posicao p = incendio.areaAfetada.get(0);

        for (AgentStatus ap : this.agents.values()) {
            tempoAtual =  autonomiaCombustivel(ap,p);
            if (tempoAtual > 0 && tempoAtual< minTempo) {
                minTempo = tempoAtual;
                closestAgent = ap.aid;
            }
        }

        System.out.println(closestAgent.toString() + " tempoSomaTotal FINAL: " + minTempo);

        Tarefa t = new Tarefa(taskId++, Tarefa.APAGAR, incendio.fireId, p);
        Tarefa t2 = new Tarefa(taskId++, Tarefa.ABASTECER, p); // apenas esta aqui para testar se as tarefas no central sao marcadas como resolvidas corretamente
        this.addBehaviour(new AssignTask(closestAgent, t, t2));
    }

    private int autonomiaCombustivel(AgentStatus ap, Posicao incendio){
        int combSomaTotal = 0;

        // apagar
        int distAgenteIncendio = Posicao.distanceBetween(ap.posAtual, incendio);
        int minTempoAgenteIncendio = distAgenteIncendio*(4/this.agents.get(ap.aid).velocidade)*1000;
        System.out.println(this.agents.get(ap.aid).toString() + " distAgenteIncendio: " + distAgenteIncendio + " " + this.agents.get(ap.aid).tipo);
        System.out.println(this.agents.get(ap.aid).toString() + " minTempoAgenteIncendio: " + minTempoAgenteIncendio + " " + this.agents.get(ap.aid).tipo);

        combSomaTotal += distAgenteIncendio;

        // abastecer
        int minDistIncendioPosto = getMinDistanceIncendioPosto(ap,incendio);
        System.out.println(this.agents.get(ap.aid).toString() + " distIncendioPosto: " + minDistIncendioPosto + " " + this.agents.get(ap.aid).tipo);

        combSomaTotal += minDistIncendioPosto;

        System.out.println(this.agents.get(ap.aid).toString() + " combSomaTotal " + combSomaTotal + " " + this.agents.get(ap.aid).tipo);

        if(ap.combustivelDisponivel>=combSomaTotal) return minTempoAgenteIncendio;
        else return 0;
    }

    private int getMinDistanceIncendioPosto(AgentStatus ap, Posicao incendio){
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

