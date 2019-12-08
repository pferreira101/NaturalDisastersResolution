import java.util.List;

public class App {
    static Mapa mapa;
    static MainContainer mc;

    public static Mapa generateMap(){
        mapa = new Mapa(SimulationConfig.TAMANHO_MAPA, SimulationConfig.NUM_POSTOS_COMB, SimulationConfig.NUM_POSTOS_AGUA, SimulationConfig.NUM_HABITACOES, SimulationConfig.NUM_PONTOS_FLORESTAIS);
        mapa.estabelecePosicaoPontosFixos();
        return mapa;
    }

    public static void main(String[] args){
        generateMap();
        mc = new MainContainer();
        mc.startInterface(mapa);
    }

     static void run() {

        List<Posicao> posicoesAgentes;
        int id = 0;

        try {
            mc.startAgenteCentral(mapa);

            posicoesAgentes = mapa.getPosicoesProporcional(SimulationConfig.NUM_MAX_DRONES);
            for(int i = 0; i < SimulationConfig.NUM_MAX_DRONES; i++){
                mc.startAgenteDrone(id++, mapa, posicoesAgentes.get(i));
            }

            posicoesAgentes = mapa.getPosicoesProporcional(SimulationConfig.NUM_MAX_CAMIOES);
            for(int i = 0; i < SimulationConfig.NUM_MAX_CAMIOES; i++){
                mc.startAgenteCamiao(id++, mapa, posicoesAgentes.get(i));
            }

            posicoesAgentes = mapa.getPosicoesProporcional(SimulationConfig.NUM_MAX_AERONAVES);
            for(int i = 0; i < SimulationConfig.NUM_MAX_AERONAVES; i++){
                mc.startAgenteAeronave(id++, mapa, posicoesAgentes.get(i));
            }

            Thread.sleep(1000);
            mc.startIncendiario(mapa);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
