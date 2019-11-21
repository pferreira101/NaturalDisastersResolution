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
    int taskId;

    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.taskId = 0;

        DFManager.registerAgent(this, "Central");

        addBehaviour(new ReceiveInfo());
        this.agents = new HashMap<>();
    }

    /**
     * Behaviour destinado à receção de mensagens.
     *   - aviso de incêndios por parte do agente incendiário
     *   - aviso de status dos agentes participativos
     */
    private class ReceiveInfo extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();

            if(msg != null){
                AID sender =  msg.getSender();
                String sendersName = sender.getLocalName();

                if (sendersName.contains("Incendiario") && msg.getPerformative() == ACLMessage.INFORM) {
                    try{
                        FireAlert fa = (FireAlert) msg.getContentObject();
                        processFireAlert(fa);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if(sendersName.contains("Agente") && msg.getPerformative() == ACLMessage.INFORM ){
                    try{
                        AgentStatus st = (AgentStatus) msg.getContentObject();
                        atualizarEstadoAgente(sender, st);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            else{
                block();
            }
        }

    }


    private class AssignTask extends OneShotBehaviour {

        private AID agent;
        private LinkedList<Tarefa> tarefas; // List<Posicao> no futuro

        public AssignTask(AID agentAID, Tarefa ... tarefas) {
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

        if(this.mapa.incendios.get(fa.fireID) == null){
            this.mapa.registaIncendio(fa);
        }
        else{
            this.mapa.atualizaIncendio(fa);
        }

        alocaRecursos(this.mapa.incendios.get(fa.fireID));
    }


    private void atualizarEstadoAgente(AID agent,AgentStatus status) {
        AgentStatus as = this.agents.get(agent);
        if(as != null)
            as.atualizarEstado(status);
        else
            this.agents.put(agent,status);
    }


    private void alocaRecursos(Incendio incendio) {
        AID closestAgent = null;
        int minDistance = 1000;

        // por enquanto so temos uma celula a arder, alocar com base na distancia a essa celula
        Posicao p = incendio.areaAfetada.get(0);

        for(AgentStatus ap : this.agents.values()){
            int distance = Posicao.distanceBetween(ap.pos, p);
            if (ap.disponivel && distance < minDistance) {
                minDistance = distance;
                closestAgent = ap.aid;
            }
        }

        Tarefa t = new Tarefa(taskId++, Tarefa.APAGAR, p);
        Tarefa t2 = new Tarefa(taskId++, Tarefa.ABASTECER, p); // apenas esta aqui para testar se as tarefas no central sao marcadas como resolvidas corretamente
        this.addBehaviour(new AssignTask(closestAgent, t, t2));
    }

    private void registaTarefa(AID agent, Tarefa t) {
        this.agents.get(agent).addTarefa(t);
    }


}
