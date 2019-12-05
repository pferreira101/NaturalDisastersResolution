import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.sql.Time;
import java.util.*;


public class AgenteParticipativo extends Agent {

    AID centralAgent;
    Mapa mapa;

    Posicao posAtual;
    Posicao posAnterior;

    boolean disponivel;
    boolean prevencao;

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
        //this.addBehaviour(new RefillFreeMode(this,1000));
    }

    class TaskReceiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                   try {
                       LinkedList<Tarefa> tarefas = ( LinkedList<Tarefa>) msg.getContentObject();
                       for(Tarefa t : tarefas) {
                           tarefasAgendadas.add(t);
                       }
                       if(disponivel==true) performTasks();
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

    private void performTasks() throws Exception {
        while(this.tarefasAgendadas.peek() != null) {
            this.disponivel = false;
            Tarefa t = this.tarefasAgendadas.poll();
            moveToPosition(t.posicao);

            if(t.tipo == Tarefa.APAGAR)
                apagarFogo(t);
            else if(t.tipo == Tarefa.ABASTECER)
                abastecer(t);
            else{
                this.prevencao = true;
                sendCurrentStatus();
                this.addBehaviour(new PrevencaoFogo(this,1000,t.fireId,t.posicao));
            }
        }
        sendCurrentStatus();
    }

    private void apagarFogo(Tarefa t) throws Exception{
        Random rand = new Random();
        int op = rand.nextInt(2);

        Thread.sleep(1000);


        this.aguaDisponivel--;
        this.disponivel = true;


        // System.out.println(new Time(System.currentTimeMillis()) + ": "+this.getAID().getLocalName()  + " --- Apagou célula " + p.toString() + " (agua: " + this.aguaDisponivel + " ,combustivel: " + this.combustivelDisponivel + ")");

        System.out.println("Agente a registar que apagou a celula " + t.posicao + ", no fogo "+t.fireId);
        this.mapa.registaCelulaApagada(t.fireId, t.posicao);
        this.tarefasRealizadas.add(t);
    }



    private void abastecer(Tarefa t) throws Exception{
        System.out.println("A ABASTECER");
        Thread.sleep(1000);

        this.aguaDisponivel = this.capacidadeMaxAgua;
        this.combustivelDisponivel = this.capacidadeMaxCombustivel;
        this.disponivel = true;

        //System.out.println(new Time(System.currentTimeMillis()) + ": "+this.getAID().getLocalName()  + " --- Abasteceu em " + p.toString() + " (agua: " + this.aguaDisponivel + " ,combustivel: " + this.combustivelDisponivel + ")");

        this.tarefasRealizadas.add(t);
    }

    class PrevencaoFogo extends TickerBehaviour {

        AgenteParticipativo agenteParticipativo;
        Posicao posicaoInicialPrevencao; // necessário para calcular os adjacentes
        List<Posicao> adj;
        int fireId;


        public PrevencaoFogo(AgenteParticipativo a, long period, int f, Posicao p) {
            super(a, period);
            this.agenteParticipativo = a;
            this.posicaoInicialPrevencao = p;
            this.adj = mapa.posicoesAdjacentes(posicaoInicialPrevencao);
            this.fireId = f;
        }

        @Override
        protected void onTick(){
            if(mapa.incendios.containsKey(fireId)){
                System.out.println("A PREVENIR");
                prevencaoFogo();
            }
            else{
                prevencao = false;
                sendCurrentStatus();
                agenteParticipativo.removeBehaviour(this);
            }
        }

        private void prevencaoFogo() {

        }

    }


    private void moveToPosition(Posicao p) throws InterruptedException {

        while(!this.posAtual.equals(p)){

            this.posAnterior  =  new Posicao(this.posAtual.pos_x, this.posAtual.pos_y);

            if(this.posAtual.pos_x == p.pos_x && this.posAtual.pos_y > p.pos_y) {
                this.posAtual.pos_y--;
            }
            else if(this.posAtual.pos_x == p.pos_x && this.posAtual.pos_y < p.pos_y) {
                this.posAtual.pos_y++;
            }
            else if(this.posAtual.pos_x > p.pos_x && this.posAtual.pos_y == p.pos_y) {
                this.posAtual.pos_x--;
            }
            else if(this.posAtual.pos_x < p.pos_x && this.posAtual.pos_y == p.pos_y) {
                this.posAtual.pos_x++;
            }
            else if(this.posAtual.pos_x > p.pos_x && this.posAtual.pos_y > p.pos_y) {
                this.posAtual.pos_x--;
                this.posAtual.pos_y--;
            }
            else if(this.posAtual.pos_x < p.pos_x && this.posAtual.pos_y < p.pos_y) {
                this.posAtual.pos_x++;
                this.posAtual.pos_y++;
            }
            else if(this.posAtual.pos_x > p.pos_x && this.posAtual.pos_y < p.pos_y) {
                this.posAtual.pos_x--;
                this.posAtual.pos_y++;
            }
            else if(this.posAtual.pos_x < p.pos_x && this.posAtual.pos_y > p.pos_y) {
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

    class RefillFreeMode extends TickerBehaviour {
        public RefillFreeMode(Agent a, long period) {
            super(a, period);
        }

        public void onTick() { // nova variável free
            if(disponivel==true && combustivelDisponivel < (0.33*capacidadeMaxCombustivel)){
                disponivel = false;
                Posicao postoComb = getMinDistancePostoComb();
                try {
                    moveToPosition(postoComb);
                    System.out.println("A IR METER GOTA");
                    combustivelDisponivel = capacidadeMaxCombustivel;
                    disponivel = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Posicao getMinDistancePostoComb(){
        int minDistance = 1000;
        Posicao maisProximo = null;

        for(Posicao p : mapa.postosCombustivel){
            int distance = Posicao.distanceBetween(this.posAtual,p);
            if (distance < minDistance) {
                maisProximo = p;
                minDistance = distance;
            }
        }

        return maisProximo;
    }
}
