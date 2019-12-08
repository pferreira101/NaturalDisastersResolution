import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.*;
import java.sql.Timestamp;

public class AgenteIncendiario extends Agent {

    Mapa mapa;
    int fireId;
    AID centralAgent;
    int freqIncendio; // 1 incendio novo a cada x ms
    int freqExpansao;

    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.fireId=0;
        this.centralAgent = DFManager.findSingleAgent(this, "Central");
        this.freqIncendio = SimulationConfig.FREQ_CRIACAO_INCENDIO;
        this.freqExpansao = SimulationConfig.FREQ_EXPANSAO_INCENDIO;

        DFManager.registerAgent(this, "Incendiario");

        addBehaviour(new PlaceFire(this, this.freqIncendio));
        addBehaviour(new MsgReceiver());
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

    class MsgReceiver extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                AID sender = msg.getSender();
                String sendersName = sender.getLocalName();

                if (sendersName.contains("Interface") && msg.getPerformative() == ACLMessage.REQUEST && msg.getContent().equals("STOP")) {
                    myAgent.doDelete();
                }
            } else {
                block();
            }
        }
    }

    private void placeFire() {
        Posicao p;

        do{
            p = mapa.getRandPosition();
        }while(mapa.onFire(p) || mapa.inAreaArdida(p)|| mapa.isWaterSource(p));

        Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        FireAlert fa = new FireAlert(this.fireId, p, ts);

        try {
            sendAlert(centralAgent, fa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addBehaviour(new SpreadFire(this, this.freqExpansao, p, this.fireId));
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
            }
            else {
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

                if (!adjFlo.isEmpty() && mapa.floresta.contains(p)) { // se é célula floresta, expande para 2 adjacentes, dando prioridade a pontos de floresta
                    int i = 0;
                    if (adjFlo.size() >= 2) {
                        while (i < 2) {
                            pAdjacent = mapa.getRandAdjacentPositions(adjFlo);
                            adjFlo.remove(pAdjacent);
                            celulasIncendiadas.add(pAdjacent);
                            i++;
                        }
                    } else if (adjFlo.size() == 1) {
                        pAdjacent = mapa.getRandAdjacentPositions(adjFlo);
                        adjFlo.remove(pAdjacent);
                        celulasIncendiadas.add(pAdjacent);
                    }
                } else if (!mapa.floresta.contains(p) && !adjFlo.isEmpty()){ // se não é celula floresta, expande para 1 adjacente, dando prioridade a pontos de floresta
                    pAdjacent = mapa.getRandAdjacentPositions(adjFlo);
                    adjFlo.remove(pAdjacent);
                    celulasIncendiadas.add(pAdjacent);
                } else if(!adj.isEmpty()){ // caso de expansão default
                    do {
                        pAdjacent = mapa.getRandAdjacentPositions(adj);
                    } while (mapa.isWaterSource(pAdjacent));
                    adj.remove(pAdjacent);
                    celulasIncendiadas.add(pAdjacent);
                }
            }

            FireAlert fa = new FireAlert(this.fireId, celulasIncendiadas);

            try {
                sendAlert(centralAgent, fa);
            } catch (Exception e) {
                e.printStackTrace();
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

    protected void takeDown(){
        DFManager.deRegister(this);
    }
}
