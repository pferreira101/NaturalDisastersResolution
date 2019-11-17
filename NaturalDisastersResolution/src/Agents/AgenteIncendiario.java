import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Date;
import java.sql.Timestamp;

public class AgenteIncendiario extends Agent {

    Mapa mapa;
    int fireId;
    AID centralAgent;

    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.fireId=0;
        this.centralAgent = DFManager.findAgent(this, "Central");
        addBehaviour(new TickerBehaviour(this, 1000) {
            protected void onTick(){
                placeFire();
            }
        });
    }

    private void placeFire() {
        Posicao p =  null;

        do{
            p = Posicao.getRandPosition(mapa.size);
        }while(mapa.onFire(p) == true);

        Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        FireAlert fa = new FireAlert(this.fireId++, p, ts);

        try {
            sendAlert(centralAgent, fa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendAlert(AID central, FireAlert fa) throws Exception{
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(central);
        msg.setContentObject(fa);
        System.out.println(fa.toString());
        send(msg);
    }
}
