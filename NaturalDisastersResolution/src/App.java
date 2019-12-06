import java.awt.*;
import java.util.List;

public class App {

    static int nDrones = 1;
    static int nCamioes = 1;
    static int nAeronaves = 1;
    static int tamanhoMapa = 8;
    static int numPostosComb = 2;
    static int numPostosAgua = 1;
    static int numHabitacoes = 10;
    static int numPontosFloresta = 20;

    public static void main(String[] args) throws Exception {

        run();
    }


    private static void run() {
        Mapa mapa = new Mapa(SimulationConfig.TAMANHO_MAPA, SimulationConfig.NUM_POSTOS_COMB, SimulationConfig.NUM_POSTOS_AGUA, SimulationConfig.NUM_HABITACOES, SimulationConfig.NUM_PONTOS_FLORESTAIS);

        System.out.println("0.1");

        mapa.estabelecePosicaoPontosFixos();

        System.out.println("0.11");


        List<Posicao> posicoesAgentes;
        int id = 0;

        AgenteCentral agenteCentral = new AgenteCentral();



        try {
            System.out.println("1");

            MainContainer mc = new MainContainer();

            mc.startAgenteCentral(mapa);

            System.out.println("2");

            posicoesAgentes = mapa.getDistribuicaoPosicoes(SimulationConfig.NUM_MAX_DRONES);
            for(int i = 0; i < SimulationConfig.NUM_MAX_DRONES; i++){
                mc.startAgenteDrone(id++, mapa, posicoesAgentes.get(i));
            }

            System.out.println("3");

            posicoesAgentes = mapa.getDistribuicaoPosicoes(SimulationConfig.NUM_MAX_CAMIOES);
            for(int i = 0; i < SimulationConfig.NUM_MAX_CAMIOES; i++){
                mc.startAgenteCamiao(id++, mapa, posicoesAgentes.get(i));
            }

            System.out.println("4");

            posicoesAgentes = mapa.getDistribuicaoPosicoes(SimulationConfig.NUM_MAX_AERONAVES);
            for(int i = 0; i < SimulationConfig.NUM_MAX_AERONAVES; i++){
                mc.startAgenteAeronave(id++, mapa, posicoesAgentes.get(i));
            }

            System.out.println("5");

            mc.startInterface(mapa);

            Thread.sleep(1000);
            mc.startIncendiario(mapa);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
