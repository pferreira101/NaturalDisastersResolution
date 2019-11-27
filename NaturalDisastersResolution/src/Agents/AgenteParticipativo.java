import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.sql.Time;
import java.util.*;


public class AgenteParticipativo extends Agent {

    AID centralAgent;
    Mapa mapa;

    Posicao posAtual;
    Posicao posAnterior;

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
        this.posAtual = (Posicao)args[1];

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

    // este método nao pode ser assim depois
    private void performTasks(Queue<Tarefa> tarefas) throws Exception {
        while(tarefas.peek() != null) {
            Tarefa t = tarefas.poll();

            moveToPosition(t);

            if(t.tipo == Tarefa.APAGAR)
                apagarFogo(t);
            else
                abastecer(t);
        }
        sendCurrentStatus();
    }

    private void apagarFogo(Tarefa t) throws Exception{
        Random rand = new Random();
        int op = rand.nextInt(2);

        Thread.sleep(1000);


        this.disponivel = true;
        this.aguaDisponivel--;


        // System.out.println(new Time(System.currentTimeMillis()) + ": "+this.getAID().getLocalName()  + " --- Apagou célula " + p.toString() + " (agua: " + this.aguaDisponivel + " ,combustivel: " + this.combustivelDisponivel + ")");

        System.out.println("Agente a registar que apagou a celula " + t.posicao + ", no fogo "+t.fireId);
        this.mapa.registaCelulaApagada(t.fireId, t.posicao);
        this.tarefasRealizadas.add(t);
    }



    private void abastecer(Tarefa t) throws Exception{

        Thread.sleep(1000);

        this.disponivel = true;
        this.aguaDisponivel = this.capacidadeMaxAgua;
        this.combustivelDisponivel = this.capacidadeMaxCombustivel;

        //System.out.println(new Time(System.currentTimeMillis()) + ": "+this.getAID().getLocalName()  + " --- Abasteceu em " + p.toString() + " (agua: " + this.aguaDisponivel + " ,combustivel: " + this.combustivelDisponivel + ")");

        this.tarefasRealizadas.add(t);
    }

    private void moveToPosition(Tarefa t) throws InterruptedException {
        this.disponivel = false;

        while(!this.posAtual.equals(t.posicao)){

            this.posAnterior  =  new Posicao(this.posAtual.pos_x, this.posAtual.pos_y);

            if(this.posAtual.pos_x == t.posicao.pos_x && this.posAtual.pos_y > t.posicao.pos_y) {
                this.posAtual.pos_y--;
            }
            if(this.posAtual.pos_x == t.posicao.pos_x && this.posAtual.pos_y < t.posicao.pos_y) {
                this.posAtual.pos_y++;
            }
            if(this.posAtual.pos_x > t.posicao.pos_x && this.posAtual.pos_y == t.posicao.pos_y) {
                this.posAtual.pos_x--;
            }
            if(this.posAtual.pos_x < t.posicao.pos_x && this.posAtual.pos_y == t.posicao.pos_y) {
                this.posAtual.pos_x++;
            }
            if(this.posAtual.pos_x > t.posicao.pos_x && this.posAtual.pos_y > t.posicao.pos_y) {
                this.posAtual.pos_x--;
                this.posAtual.pos_y--;
            }
            if(this.posAtual.pos_x < t.posicao.pos_x && this.posAtual.pos_y < t.posicao.pos_y) {
                this.posAtual.pos_x++;
                this.posAtual.pos_y++;
            }
            if(this.posAtual.pos_x > t.posicao.pos_x && this.posAtual.pos_y < t.posicao.pos_y) {
                this.posAtual.pos_x--;
                this.posAtual.pos_y++;
            }
            if(this.posAtual.pos_x < t.posicao.pos_x && this.posAtual.pos_y > t.posicao.pos_y) {
                this.posAtual.pos_x++;
                this.posAtual.pos_y--;
            }

            int tempoDeMovimento = (4/this.velocidade)*1000;
            Thread.sleep(tempoDeMovimento);
            this.combustivelDisponivel--;


            sendCurrentStatus();
        }
    }

    private void sendCurrentStatus() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(this.centralAgent);

        try{
            msg.setContentObject(new AgentStatus(this));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        send(msg);

        // limpar lista de tarefas realizadas
        this.tarefasRealizadas = new ArrayList<>();
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
