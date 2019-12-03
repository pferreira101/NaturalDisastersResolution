import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.sql.Time;


public class AgenteInterface extends Agent {

    GUI gui;

    Mapa mapa;
    int requestFreq = 500; //ms
    boolean firstDraw;


    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];

        //this.centralAgent = DFManager.findAgent(this, "Central");

        this.firstDraw = true;

        this.addBehaviour(new InfoReceiver());
        this.addBehaviour(new InfoRequester(this, this.requestFreq));
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

    private void requestInfo() {
        AID central = DFManager.findAgent(this, "Central");
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
        msg.addReceiver(central);
        msg.setContent("1");
        send(msg);
    }

    private void updateGui(DeltaSimulationStatus stats) {
        if(firstDraw){
            this.gui = new GUI(mapa);
            this.gui.mapGrid.updateGridStatus(stats);
            gui.getFrame().setVisible(true);
            this.firstDraw = false;
        }
        else this.gui.mapGrid.updateGridStatus(stats);
    }
}
