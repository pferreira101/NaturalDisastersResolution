import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AgenteInterface extends Agent {

    GUI gui;

    Mapa mapa;
    int requestFreq = 500; //ms
    InfoReceiver b1;
    InfoRequester b2;
    boolean firstSimulation;

    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.firstSimulation = true;
        startGUI();

    }

    public void startSimulationInfoDisplay() {
        b1 = new InfoReceiver();
        b2 = new InfoRequester(this, this.requestFreq);
        this.addBehaviour(b1);
        this.addBehaviour(b2);
    }

    class InfoReceiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String sendersName = msg.getSender().getLocalName();
                if (sendersName.equals("Central") && msg.getPerformative() == ACLMessage.INFORM) {

                    try {
                        DeltaSimulationStatus stats = (DeltaSimulationStatus) msg.getContentObject();
                        updateGui(stats);
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


    class InfoRequester extends TickerBehaviour {

        public InfoRequester(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            requestInfo();
        }
    }

    class stopSimulation extends OneShotBehaviour {

        @Override
        public void action() {
            List<AID> agentsToStop = new ArrayList<>();
            agentsToStop.add(DFManager.findSingleAgent(myAgent, "Central"));
            agentsToStop.add(DFManager.findSingleAgent(myAgent, "Incendiario"));
            agentsToStop.addAll(DFManager.findAgents(myAgent, "Agent"));

            agentsToStop.forEach(aid -> {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(aid);
                msg.setContent("STOP");
                send(msg);
            });
        }
    }

    private void requestInfo() {
        AID central = DFManager.findSingleAgent(this, "Central");
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
        msg.addReceiver(central);
        msg.setContent("1");
        send(msg);
    }

    void startGUI(){
        this.gui = new GUI(mapa, this);
        gui.getFrame().setVisible(true);
    }


    void updateGui(DeltaSimulationStatus stats) {
        this.gui.mapGrid.updateGridStatus(stats);
    }

    void sendTarefas (DeltaSimulationStatus stats) {
        this.gui.tarefas(stats.tarefasRealizadas);
    }



    void stopSimulation(){
        this.removeBehaviour(b1);
        this.removeBehaviour(b2);
        this.addBehaviour(new stopSimulation());
    }

}
