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
    int freqExpansao = 5000;
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
        }while(mapa.onFire(p) || mapa.isWaterSource(p));

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

        private void spreadFire() {
            List<Posicao> celulasIncendiadas = new ArrayList<>();

            for (Posicao p : this.ultimasCelulasIncendiadas) {
                List<Posicao> adj,adjFlo;
                Posicao pAdjacent;
                adj = mapa.posicoesAdjacentesNotOnFire(p);
                adjFlo = mapa.posicoesFlorestaAdjacenteNotOnFire(p);


                if (mapa.floresta.contains(p)) { // se é célula floresta, expande para 2 adjacentes, dando prioridade a pontos de floresta
                    int i = 0;
                    if (adjFlo.size() >= 2) {
                        while (i < 2) {
                            do {
                                pAdjacent = mapa.getRandAdjacentPositions(adjFlo);
                            } while (mapa.onFire(pAdjacent));
                            celulasIncendiadas.add(pAdjacent);
                            i++;
                        }
                    } else if (adjFlo.size() == 1) {
                        do {
                            pAdjacent = mapa.getRandAdjacentPositions(adjFlo);
                        } while (mapa.onFire(pAdjacent));
                        celulasIncendiadas.add(pAdjacent);
                        do {
                            pAdjacent = mapa.getRandAdjacentPositions(adj);
                        } while (mapa.onFire(pAdjacent) || mapa.isWaterSource(pAdjacent));
                        celulasIncendiadas.add(pAdjacent);
                    }
                } else if (!mapa.floresta.contains(p) && !adjFlo.isEmpty()){ // se não é celula floresta, expande para 1 adjacente, dando prioridade a pontos de floresta
                    do {
                        pAdjacent = mapa.getRandAdjacentPositions(adjFlo);
                    } while (mapa.onFire(pAdjacent));
                    celulasIncendiadas.add(pAdjacent);
                } else if(!adj.isEmpty()){ // caso de expansão default
                    do {
                        pAdjacent = mapa.getRandAdjacentPositions(adj);
                    } while (mapa.onFire(pAdjacent) || mapa.isWaterSource(pAdjacent));
                    celulasIncendiadas.add(pAdjacent);
                }



                // tirar para fora
                FireAlert fa = new FireAlert(this.fireId, celulasIncendiadas);

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
