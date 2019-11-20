import java.util.*;
import java.lang.Math;

public class Mapa {

    int size;
    int numPostosComb;
    int numPostosAgua;
    int numHabitacoes;
    int numPontosFloresta;

    List<Posicao> postosCombustivel;
    List<Posicao> postosAgua;
    List<Posicao> habitacoes;
    List<Posicao> floresta;
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
        boolean flag = true;
        if(postosCombustivel.contains(p) || postosAgua.contains(p) || habitacoes.contains(p) || floresta.contains(p))
            flag = false;
        return flag;
    }

    public void estabelecePosicaoPontosFixos(){ // falta verificar posicaoLivre nos incendios
        Random rand = new Random();
        int ladoMapa = (int) Math.sqrt(size);
        for(int i=0; i<numPostosComb;i++){
            Posicao p = Posicao.getRandPosition(size);
            postosCombustivel.add(p);
        }
        for(int i=0; i<numPostosAgua;i++){
            Posicao p = Posicao.getRandPosition(size);
            postosAgua.add(p);
        }

        for(int i=0; i<numHabitacoes;i++){
            Posicao p;
            do{
                p = Posicao.getRandPosition(size);
            }while(posicaoLivre(p)==false);
            habitacoes.add(p);
            //System.out.println("POSX " + i + ": " + p.pos_x);
            //System.out.println("POSY " + i + ": " + p.pos_y);

            int vizinhos = rand.nextInt(2); // não está preparado para ter mais do que os 8 vizinhos adjacentes
            for(int j=0; j<vizinhos; j++){
                Posicao pSide;
                do{
                    pSide = Posicao.getRandomSidePosition(p,size);
                }while(posicaoLivre(pSide)==false);
                habitacoes.add(pSide);
                //System.out.println("Posx (vizinho do " + i + "): " + pSide.pos_x);
                //System.out.println("Posy (vizinho do " + i + "): " + pSide.pos_y);
                i++;
                if(i==numHabitacoes) break;
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

            int numPontosFlorestaVizinhos = rand.nextInt(2); // não está preparado para ter mais do que os 8 vizinhos adjacentes
            for(int j=0; j<numPontosFlorestaVizinhos; j++){
                Posicao pSide;
                do{
                    pSide = Posicao.getRandomSidePosition(p,size);
                }while(posicaoLivre(pSide)==false);
                floresta.add(pSide);
                //System.out.println("Posx (vizinho do " + i + "): " + pSide.pos_x);
                //System.out.println("Posy (vizinho do " + i + "): " + pSide.pos_y);
                i++;
                if(i==numPontosFloresta) break;
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

    public void registaIncendio(FireAlert fa) {
        this.incendios.put(fa.fireID, new Incendio(fa));
    }

    public void atualizaIncendio(FireAlert fa) {
        this.incendios.get(fa.fireID).registaExpansao(fa);
    }
}
