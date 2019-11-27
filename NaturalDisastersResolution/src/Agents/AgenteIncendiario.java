import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

public class AgenteIncendiario extends Agent {

    Mapa mapa;
    int fireId;
    AID centralAgent;
    int freqIncendio = 3000; // 1 incendio novo a cada x ms
    int freqExpansao = 2000;

    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.fireId=0;
        this.centralAgent = DFManager.findAgent(this, "Central");
        addBehaviour(new PlaceFire(this, this.freqIncendio));
    }

    class PlaceFire extends TickerBehaviour{

        public PlaceFire(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            placeFire();
        }
    }

    private void placeFire() {
        Posicao p;
        do{
            p = mapa.getRandPosition();
        }while(mapa.onFire(p) || !mapa.posicaoLivre(p));

        Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        FireAlert fa = new FireAlert(this.fireId, p, ts);
        addBehaviour(new ExpansionFire(this, this.freqExpansao, p, this.fireId));
        fireId++;
        try {
            sendAlert(centralAgent, fa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ExpansionFire extends TickerBehaviour{

        int fireId;
        List<Posicao> ultimasCelulasIncendiadas;


        public ExpansionFire(Agent a, long period, Posicao celulaInicial ,int fireId) {
            super(a, period);
            this.ultimasCelulasIncendiadas = new ArrayList<>();
            this.ultimasCelulasIncendiadas.add(celulaInicial);
            this.fireId = fireId;
        }

        @Override
        protected void onTick() {
            expansionFire();
        }

        private void expansionFire(){
            List<Posicao> celulasIncendiadas = new ArrayList<>();
            Posicao pAdjacent;

            for(Posicao p : this.ultimasCelulasIncendiadas) {
                List<Posicao> adj;
                adj = mapa.posicoesAdjacentesNotOnFire(p);
                if(adj.isEmpty()) break;
                do {
                    pAdjacent = mapa.getRandAdjacentPositions(adj);
                    //if(mapa.posicoesAdjacentesOnFire(p)==true) break;
                } while (mapa.onFire(pAdjacent)==true);

                celulasIncendiadas.add(pAdjacent);

                // tirar para fora
                FireAlert fa = new FireAlert(this.fireId, pAdjacent);

                try {
                    sendAlert(centralAgent, fa);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.ultimasCelulasIncendiadas = celulasIncendiadas;

        }
    }

    private void sendAlert(AID central, FireAlert fa) throws Exception{
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(central);
        msg.setContentObject(fa);
        send(msg);
    }
}
