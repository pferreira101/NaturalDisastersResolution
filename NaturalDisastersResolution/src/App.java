import java.util.List;

public class App {

    static int nDrones = 10;
    static int nCamioes = 5;
    static int nAeronaves = 2;
    static int tamanhoMapa = 15;
    static int numPostosComb = 4;
    static int numPostosAgua = 20;
    static int numHabitacoes = 30;
    static int numPontosFloresta = 20;

    public static void main(String[] args) throws Exception {

        run();
    }


    private static void run() {

        Mapa mapa = new Mapa(tamanhoMapa, numPostosComb, numPostosAgua, numHabitacoes, numPontosFloresta);

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

            posicoesAgentes = mapa.getDistribuicaoPosicoes(nDrones);
            for(int i = 0; i < nDrones; i++){
                mc.startAgenteDrone(id++, mapa, posicoesAgentes.get(i));
            }

            System.out.println("3");

            posicoesAgentes = mapa.getDistribuicaoPosicoes(nCamioes);
            for(int i = 0; i < nCamioes; i++){
                mc.startAgenteCamiao(id++, mapa, posicoesAgentes.get(i));
            }

            System.out.println("4");

            posicoesAgentes = mapa.getDistribuicaoPosicoes(nAeronaves);
            for(int i = 0; i < nAeronaves; i++){
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
