import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;

import java.sql.Time;
import java.util.*;


public class AgenteParticipativo extends Agent {
    ThreadedBehaviourFactory tbf;

    RefillTanks rt;

    AID centralAgent;
    Mapa mapa;

    Posicao posAtual;
    Posicao posAnterior;

    boolean disponivel;
    boolean isFreeModeOk;
    boolean freeModeActive;

    int capacidadeMaxAgua;
    int capacidadeMaxCombustivel;

    int aguaDisponivel;
    int combustivelDisponivel;

    int velocidade;

    Queue<Tarefa> tarefasAgendadas;
    List<Tarefa> tarefasRealizadas;

    int tempoParaFicarDisponivel;


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

        this.tempoParaFicarDisponivel = 0;

        DFManager.registerAgent(this, "Agent");
        this.centralAgent = DFManager.findSingleAgent(this, "Central");

        sendCurrentStatus(); // informa no setup para quartel ter conhecimento de todos os agentes antes do agente incendiario comecar a corre

        this.tbf = new ThreadedBehaviourFactory();
        this.isFreeModeOk = true;
        this.freeModeActive = false;

        this.addBehaviour(tbf.wrap(new TaskReceiver()));
    }

    class TaskReceiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String sendersName = msg.getSender().getLocalName();
                if (sendersName.contains("Central") && msg.getPerformative() == ACLMessage.REQUEST) {
                   try {
                       LinkedList<Tarefa> tarefas = (LinkedList<Tarefa>) msg.getContentObject();
                       isFreeModeOk = false;
                       for(Tarefa t : tarefas) {
                           tarefasAgendadas.add(t);
                       }
                       if(disponivel==true){
                           addBehaviour(tbf.wrap(new PerformTasks()));
                       }
                   }
                   catch (Exception e){
                       e.printStackTrace();
                   }
                }
                else if (sendersName.contains("Interface") && msg.getPerformative() == ACLMessage.REQUEST && msg.getContent().equals("STOP")) {
                    myAgent.doDelete();
                }
            } else {
                block();
            }
        }

    }

    class PerformTasks extends OneShotBehaviour {

        @Override
        public void action(){
            while(freeModeActive);

            while(tarefasAgendadas.peek() != null) {
                disponivel = false;

                Tarefa t = tarefasAgendadas.poll();

                boolean deslocacaoCompleta = moveToPosition(t.posicao, t, false);

                if(t.tipo == Tarefa.APAGAR)
                    apagarFogo(t);
                else if(t.tipo == Tarefa.ABASTECERCOMB)
                    abastecerComb();
                else if (t.tipo == Tarefa.ABASTECERAGUA)
                    abastecerAgua();
                else if(t.tipo == Tarefa.PREVENIR) {
                    if (deslocacaoCompleta)
                        prevenir();
                }

                tarefasRealizadas.add(t);
                sendCurrentStatus();
            }

            if(tarefasAgendadas.size() == 0){
                disponivel = true;
                isFreeModeOk = true;
                sendCurrentStatus();
                rt = new RefillTanks();
                addBehaviour(tbf.wrap(rt));
            }
        }
    }

    private void apagarFogo(Tarefa t){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.aguaDisponivel--;

        this.mapa.registaCelulaApagada(t.fireId, t.posicao);
    }



    private void abastecerComb() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.combustivelDisponivel = this.capacidadeMaxCombustivel;
    }

    private void abastecerAgua(){

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.aguaDisponivel = this.capacidadeMaxAgua;
    }

    private void prevenir(){
    }

    private boolean moveToPosition(Posicao p, Tarefa t, boolean freeMode){

        if(freeMode && !isFreeModeOk) return false;

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

            int tempoDeMovimento = (6/this.velocidade)*1000;

            try {
                Thread.sleep(tempoDeMovimento);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.combustivelDisponivel--;

            if(t != null && t.tipo == Tarefa.PREVENIR && !mapa.isFireActive(t.fireId)){
                return false;
            }

            if(freeMode && this.tarefasAgendadas.size() != 0){
                sendCurrentStatus();
                return false;
            }

            sendCurrentStatus();
        }
        return true;
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

    class RefillTanks extends OneShotBehaviour {

        public void action(){
            freeModeActive = true;
            if(aguaDisponivel < 5 || aguaDisponivel < 0.5 * capacidadeMaxAgua){
                Posicao postoAguaMaisProxAgent = getMinDistancePostoAgua(posAtual);
                Posicao postoCombusMaisProx = getMinDistancePostoComb(postoAguaMaisProxAgent);
                int distanciaTotal = Posicao.distanceBetween(posAtual, postoAguaMaisProxAgent) + Posicao.distanceBetween(postoAguaMaisProxAgent, postoCombusMaisProx);

                boolean temCombustivel = distanciaTotal <= combustivelDisponivel;

                if(temCombustivel){
                    if (moveToPosition(postoAguaMaisProxAgent,null, true)) {
                        abastecerAgua();
                        sendCurrentStatus();

                        if (moveToPosition(postoCombusMaisProx, null, true)) {
                            abastecerComb();
                            sendCurrentStatus();
                        }
                    }
                }
                else {
                    Posicao postoCombusMaisProxAgent = getMinDistancePostoComb(posAtual);
                    Posicao postoAguaMaisProx = getMinDistancePostoAgua(postoCombusMaisProxAgent);
                    Posicao postoCombMaisProx = getMinDistancePostoComb(postoAguaMaisProx);

                    if (moveToPosition(postoCombusMaisProxAgent, null, true)) {
                        abastecerComb();
                        sendCurrentStatus();

                        if (moveToPosition(postoAguaMaisProx, null, true)) {
                            abastecerAgua();
                            sendCurrentStatus();

                            if (moveToPosition(postoCombMaisProx, null, true)) {
                                abastecerComb();
                                sendCurrentStatus();
                            }
                        }
                    }
                }
            }
            else{
                Posicao postoCombusMaisProxAgent = getMinDistancePostoComb(posAtual);

                if(moveToPosition(postoCombusMaisProxAgent, null, true)) {
                    abastecerComb();
                    sendCurrentStatus();
                }
            }
            freeModeActive = false;
        }

    }

    private Posicao getMinDistancePostoComb(Posicao from){
        int minDistance = 1000;
        Posicao maisProximo = null;

        for(PostoCombustivel p : mapa.postosCombustivel){
            int distance = Posicao.distanceBetween(from, p.pos);
            if (p.ativo == true && distance < minDistance) {
                maisProximo = p.pos;
                minDistance = distance;
            }
        }

        return maisProximo;
    }

    private Posicao getMinDistancePostoAgua(Posicao from){
        int minDistance = 1000;
        Posicao maisProximo = null;

        for(Posicao p : mapa.postosAgua){
            int distance = Posicao.distanceBetween(from, p);
            if (distance < minDistance) {
                maisProximo = p;
                minDistance = distance;
            }
        }

        return maisProximo;
    }
    protected void takeDown(){
        System.out.println("A acabar " + this.getAID().getLocalName());
        DFManager.deRegister(this);
        this.isFreeModeOk = false;
        tbf.interrupt();
    }
}
