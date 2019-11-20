import java.util.List;

public class App {

    static int nDrones = 10;
    static int nCamioes = 5;
    static int nAeronaves = 2;
    static int tamanhoMapa = 100;

    public static void main(String[] args) throws Exception{
        Mapa mapa = new Mapa(tamanhoMapa);
        List<Posicao> posicoesAgentes;
        int id = 0;

        MainContainer mc = new MainContainer();


        mc.startAgenteCentral(mapa);

        posicoesAgentes = mapa.getDistruicaoPosicoes(nDrones);
        for(int i = 0; i < nDrones; i++){
            mc.startAgenteDrone(id++, mapa, posicoesAgentes.get(i));
        }

        posicoesAgentes = mapa.getDistruicaoPosicoes(nCamioes);
        for(int i = 0; i < nCamioes; i++){
            mc.startAgenteCamiao(id++, mapa, posicoesAgentes.get(i));
        }

        posicoesAgentes = mapa.getDistruicaoPosicoes(nAeronaves);
        for(int i = 0; i < nAeronaves; i++){
            mc.startAgenteAeronave(id++, mapa, posicoesAgentes.get(i));
        }

        Thread.sleep(1000);
        mc.startIncendiario(mapa);


    }
}
