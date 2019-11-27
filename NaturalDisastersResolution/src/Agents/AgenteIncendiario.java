import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;
import java.sql.Timestamp;

public class AgenteIncendiario extends Agent {

    Mapa mapa;
    int fireId;
    AID centralAgent;
    int freqIncendio = 5000; // 1 incendio novo a cada x ms
    int freqExpansao = 90000;
    Set<Integer> incendiosAtivos;

    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.fireId=0;
        this.centralAgent = DFManager.findAgent(this, "Central");
        this.incendiosAtivos = new TreeSet<>();

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
        }while(mapa.onFire(p) /*|| !mapa.posicaoLivre(p)*/);

        Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        FireAlert fa = new FireAlert(this.fireId, p, ts);

        System.out.println("****** a iniciar fogo "+ this.fireId);

        try {
            sendAlert(centralAgent, fa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addBehaviour(new SpreadFire(this, this.freqExpansao, p, this.fireId));
        this.incendiosAtivos.add(fireId);

        fireId++;
    }

    class SpreadFire extends TickerBehaviour {

        AgenteIncendiario agenteIncendiario;
        int fireId;
        List<Posicao> ultimasCelulasIncendiadas;


        public SpreadFire(AgenteIncendiario a, long period, Posicao celulaInicial , int fireId) {
            super(a, period);
            this.agenteIncendiario = a;
            this.ultimasCelulasIncendiadas = new ArrayList<>();
            this.ultimasCelulasIncendiadas.add(celulaInicial);
            this.fireId = fireId;
        }

        @Override
        protected void onTick(){
            if(mapa.isFireActive(this.fireId)){
                spreadFire();
                System.out.println("****** a expandir fogo "+ this.fireId);
            }
            else {
                System.out.println("****** a parar de expandir fogo "+ this.fireId);
                agenteIncendiario.removeBehaviour(this);
            }
        }

        private void spreadFire(){
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
