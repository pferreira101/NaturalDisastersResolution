import java.util.*;

public class Mapa {

    int size;
    int numPostosComb;
    int numPostosAgua;
    int numHabitacoes;
    int numPontosFloresta;

    static List<Posicao> postosCombustivel;
    static List<Posicao> postosAgua;
    static List<Posicao> habitacoes;
    static List<Posicao> floresta;
    Map<Integer, Incendio> incendios;

    Mapa(int size, int numPostosComb, int numPostosAgua, int numHabitacoes, int numPontosFloresta){
        this.size = size;
        this.numPostosComb = numPostosComb;
        this.numPostosAgua = numPostosAgua;
        this.numHabitacoes = numHabitacoes;
        this.numPontosFloresta = numPontosFloresta;
        this.postosCombustivel = new ArrayList<>();
        this.postosAgua = new ArrayList<>();
        this.habitacoes = new ArrayList<>();
        this.floresta = new ArrayList<>();
        this.incendios = new HashMap<>();
    }

    /**
     * Método que permite verificar se uma determinada celula do mapa está a arder.
     */
    boolean onFire(Posicao p){
        return this.incendios.values().stream().anyMatch((incendio) -> incendio.areaAfetada.contains(p));
    }

    boolean posicaoLivre(Posicao p){
        return !(postosCombustivel.contains(p) || postosAgua.contains(p) || habitacoes.contains(p) || floresta.contains(p));
    }

    public void estabelecePosicaoPontosFixos(){
        Random rand = new Random();


        List<Posicao> list;
        list = Posicao.getListProporcional(size,numPostosComb);
        for(Posicao p : list) {
            //System.out.println("POSX: " + p.pos_x);
            //System.out.println("POSY: " + p.pos_y);
            postosCombustivel.add(p);
        }


        for(int i=0; i<numPostosAgua;i++){
            Posicao p;
            do{
                p = Posicao.getRandPosition(size);
            }while(posicaoLivre(p)==false);
            postosAgua.add(p);
            //System.out.println("POSX " + i + ": " + p.pos_x);
            //System.out.println("POSY " + i + ": " + p.pos_y);

            int afluentes = rand.nextInt(4);
            Posicao pLine = p;
            for(int j=0; j<afluentes; j++){
                pLine = Posicao.getRandLinePosition(pLine,size);
                if(posicaoLivre(pLine)==false) break;
                postosAgua.add(pLine);
                i++;
                if(i==numPostosAgua) break;
                //System.out.println("Posx (canal): " + pLine.pos_x);
                //System.out.println("Posy (canal): " + pLine.pos_y);
            }
        }

        for(int i=0; i<numHabitacoes;i++){
            Posicao p;
            do{
                p = Posicao.getRandPosition(size);
            }while(posicaoLivre(p)==false);
            habitacoes.add(p);
            //System.out.println("POSX " + i + ": " + p.pos_x);
            //System.out.println("POSY " + i + ": " + p.pos_y);

            int vizinhos = rand.nextInt(6); // não está preparado para ter mais do que os 8 vizinhos adjacentes
            for(int j=0; j<vizinhos; j++){
                Posicao pSide;
                do{
                    pSide = Posicao.getRandSidePosition(p,size);
                }while(posicaoLivre(pSide)==false);
                habitacoes.add(pSide);
                i++;
                if(i==numHabitacoes) break;
                //System.out.println("Posx (vizinho " + i + "): " + pSide.pos_x);
                //System.out.println("Posy (vizinho " + i + "): " + pSide.pos_y);
            }
        }

        for(int i=0; i<numPontosFloresta;i++){
            Posicao p;
            do{
                p = Posicao.getRandPosition(size);
            }while(posicaoLivre(p)==false);
            floresta.add(p);
            //System.out.println("POSX " + i + ": " + p.pos_x);
            //System.out.println("POSY " + i + ": " + p.pos_y);

            int numPontosFlorestaVizinhos = rand.nextInt(8); // não está preparado para ter mais do que os 8 vizinhos adjacentes
            for(int j=0; j<numPontosFlorestaVizinhos; j++){
                Posicao pSide;
                do{
                    pSide = Posicao.getRandSidePosition(p,size);
                }while(posicaoLivre(pSide)==false);
                floresta.add(pSide);
                i++;
                if(i==numPontosFloresta) break;
                //System.out.println("Posx (vizinho " + i + "): " + pSide.pos_x);
                //System.out.println("Posy (vizinho " + i + "): " + pSide.pos_y);
            }
        }

    }

    List<Posicao> getDistribuicaoPosicoes(int nPosicoes){
        List<Posicao> posicoes = new ArrayList<>();
        // o objetivo seria distribuir homogeneamente as posicoes
        for(int i=0; i<nPosicoes; i++){
            posicoes.add(Posicao.getRandPosition(this.size));
        }

        return posicoes;
    }
    public static boolean postoC(Posicao p) {
        if (postosCombustivel.contains(p))
            return true;
        else return false;
    }
    public static boolean postoA(Posicao p) {
        if (postosAgua.contains(p))
            return true;
        else return false;
    }
    public static boolean hab(Posicao p) {
        if (habitacoes.contains(p))
            return true;
        else return false;
    }
    public static boolean arvore(Posicao p) {
        if (floresta.contains(p))
            return true;
        else return false;
    }
    public void registaIncendio(FireAlert fa) {
        this.incendios.put(fa.fireID, new Incendio(fa));
    }

    public void atualizaIncendio(FireAlert fa) {
        this.incendios.get(fa.fireID).registaExpansao(fa);
    }
}
