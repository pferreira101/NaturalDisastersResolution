import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


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

        DFManager.registerAgent(this, "Agent");
        this.centralAgent = DFManager.findAgent(this, "Central");

        sendCurrentStatus(); // informa no setup para quartel ter conhecimento de todos os agentes antes do agente incendiario comecar a corre

        this.addBehaviour(new Receiver());
    }

    class Receiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                   try {
                       Posicao celula = (Posicao) msg.getContentObject();
                       performTask(celula);
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
            msg.setContentObject(new AgentStatus(this.getAID(), this.pos, this.aguaDisponivel, this.combustivelDisponivel, this.disponivel));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        send(msg);
    }

    private void performTask(Posicao p) throws Exception {
        int distancia = Posicao.distanceBetween(this.pos, p);
        this.disponivel = false;
        Thread.sleep(1000);
        this.pos = p;
        this.disponivel = true;
        this.aguaDisponivel--;
        this.combustivelDisponivel -= distancia;
        System.out.println("Célula " + p.toString() + " apagada (" + this.getAID().getLocalName() +" ::: agua "+this.aguaDisponivel +" ,combustivel: " + this.combustivelDisponivel + ")");
        sendCurrentStatus();
    }


}
