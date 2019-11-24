import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Incendio implements Serializable {

    int gravidade;
    Timestamp inicio;
    long duração;
    List<Posicao> areaAfetada;

    Incendio(int gravidade, int duração, Posicao pontoIgnicao){
        this.gravidade = gravidade;
        this.duração = duração;
        this.areaAfetada = new ArrayList<>();
        this.areaAfetada.add(pontoIgnicao);
    }

    
    Incendio(FireAlert fa){
        this.inicio = fa.inicioFogo;
        this.areaAfetada = new ArrayList<>();
        this.areaAfetada.add(fa.celulaIgnicao);
    }

    public void registaExpansao(FireAlert fa) {
        this.areaAfetada.add(fa.celulaIgnicao);
    }
}
