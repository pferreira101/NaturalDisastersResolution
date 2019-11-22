import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class App {

    static GUI gui ;
    static int nDrones = 10;
    static int nCamioes = 5;
    static int nAeronaves = 2;
    static int tamanhoMapa = 10;
    static int numPostosComb = 6;
    static int numPostosAgua = 10;
    static int numHabitacoes = 20;
    static int numPontosFloresta = 10;

    public static void main(String[] args) throws Exception {

        gui = new GUI();
        run();
    }


    private static void run() {

        Mapa mapa = new Mapa(tamanhoMapa, numPostosComb, numPostosAgua, numHabitacoes, numPontosFloresta);

        mapa.estabelecePosicaoPontosFixos();

        List<Posicao> posicoesAgentes;
        int id = 0;

        AgenteCentral agenteCentral = new AgenteCentral();


        gui = new GUI(mapa,agenteCentral);
        gui.getFrame().setVisible(true);

        try {
            gui.getFrame().setVisible(true);


            MainContainer mc = new MainContainer();

            mc.startAgenteCentral(mapa);

            posicoesAgentes = mapa.getDistribuicaoPosicoes(nDrones);
            for(int i = 0; i < nDrones; i++){
                mc.startAgenteDrone(id++, mapa, posicoesAgentes.get(i));
            }

            posicoesAgentes = mapa.getDistribuicaoPosicoes(nCamioes);
            for(int i = 0; i < nCamioes; i++){
                mc.startAgenteCamiao(id++, mapa, posicoesAgentes.get(i));
            }

            posicoesAgentes = mapa.getDistribuicaoPosicoes(nAeronaves);
            for(int i = 0; i < nAeronaves; i++){
                mc.startAgenteAeronave(id++, mapa, posicoesAgentes.get(i));
            }

            Thread.sleep(1000);
            mc.startIncendiario(mapa);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
