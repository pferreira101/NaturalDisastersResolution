import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class AgenteParticipativo extends Agent {

    AID centralAgent;
    Mapa mapa;
    Posicao pos;
    boolean disponivel;
    int capacidadeMaxAgua;
    int capacidadeMaxCombustivel;
    int aguaDisponivel;
    int combustivelDisponivel;
    int velocidade;
    Queue<Tarefa> tarefasAgendadas;
    List<Tarefa> tarefasRealizadas;


    void initStatus(int capacidadeMaxAgua, int capacidadeMaxCombustivel, int velocidade){
        this.capacidadeMaxAgua = capacidadeMaxAgua;
        this.capacidadeMaxCombustivel = capacidadeMaxCombustivel;
        this.aguaDisponivel = capacidadeMaxAgua;
        this.combustivelDisponivel = capacidadeMaxCombustivel;
        this.velocidade = velocidade;
        this.disponivel = true;
    }


    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.pos = (Posicao)args[1];

        this.tarefasAgendadas = new LinkedList<>();
        this.tarefasRealizadas = new ArrayList<>();

        DFManager.registerAgent(this, "Agent");
        this.centralAgent = DFManager.findAgent(this, "Central");

        sendCurrentStatus(); // informa no setup para quartel ter conhecimento de todos os agentes antes do agente incendiario comecar a corre

        this.addBehaviour(new TaskReceiver());
    }

    class TaskReceiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                   try {
                       LinkedList<Tarefa> tarefas = ( LinkedList<Tarefa>) msg.getContentObject();
                       performTasks(tarefas);
                   }
                   catch (Exception e){
                       e.printStackTrace();
                   }
                }

            } else {
                block();
            }

        }

    }

    private void sendCurrentStatus() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(this.centralAgent);

        try{
            msg.setContentObject(new AgentStatus(this, this.pos, this.aguaDisponivel, this.combustivelDisponivel, this.disponivel, this.tarefasRealizadas));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        send(msg);

        // limpar lista de tarefas realizadas
        this.tarefasRealizadas = new ArrayList<>();
    }

    // este método nao pode ser assim depois
    private void performTasks(Queue<Tarefa> tarefas) throws Exception {
        while(tarefas.peek() != null) {
            Tarefa t = tarefas.poll();

            if(t.tipo == Tarefa.APAGAR)
                apagarFogo(t);
            else
                abastecer(t);
        }
        sendCurrentStatus();
    }

    private void apagarFogo(Tarefa t) throws Exception{
        Posicao p = t.posicao;

        int distancia = Posicao.distanceBetween(this.pos, p);
        this.disponivel = false;
        Thread.sleep(2000);

        this.pos = p;
        this.disponivel = true;
        this.aguaDisponivel--;
        this.combustivelDisponivel -= distancia;

        System.out.println(new Time(System.currentTimeMillis()) + ": "+this.getAID().getLocalName()  + " --- Apagou célula " + p.toString() + " (agua: " + this.aguaDisponivel + " ,combustivel: " + this.combustivelDisponivel + ")");

        this.tarefasRealizadas.add(t);
    }



    private void abastecer(Tarefa t) throws Exception{
        Posicao p = t.posicao;

        int distancia = Posicao.distanceBetween(this.pos, p);
        this.disponivel = false;
        Thread.sleep(2000);

        this.pos = p;
        this.disponivel = true;
        this.aguaDisponivel = this.capacidadeMaxAgua;
        this.combustivelDisponivel = this.capacidadeMaxCombustivel;

        System.out.println(new Time(System.currentTimeMillis()) + ": "+this.getAID().getLocalName()  + " --- Abasteceu em " + p.toString() + " (agua: " + this.aguaDisponivel + " ,combustivel: " + this.combustivelDisponivel + ")");

        this.tarefasRealizadas.add(t);
    }

    private void addOperacao(Tarefa p){
        this.tarefasAgendadas.add(p);
    }

    private Tarefa nextOperacao(){
        return this.tarefasAgendadas.poll();
    }

    private void registaTarefaRealizada(Tarefa t){
        this.tarefasRealizadas.add(t);
    }
}
