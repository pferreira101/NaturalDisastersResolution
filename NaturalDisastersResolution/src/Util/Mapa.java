import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapa {

    int size;

    List<Posicao> postosCombustivel;
    List<Posicao> postosAgua;
    List<Posicao> habitacoes;
    List<Posicao> floresta;
    Map<Integer, Incendio> incendios;

    Mapa(int size){
        this.size = size;
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


    List<Posicao> getDistruicaoPosicoes(int nPosicoes){
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
