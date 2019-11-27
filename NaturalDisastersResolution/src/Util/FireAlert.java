import java.io.Serializable;
import java.sql.Timestamp;

public class FireAlert implements Serializable {

    int fireID;
    Posicao celulaIgnicao; // por enquanto esta so uma celula, depois sera colecao de celulas
    Timestamp inicioFogo;

    FireAlert(int id, Posicao p, Timestamp t){
        this.fireID = id;
        this.celulaIgnicao = p;
        this.inicioFogo = t;
    }

    FireAlert(int id, Posicao p){
        this.fireID = id;
        this.celulaIgnicao = p;
    }

    @Override
    public String toString() {
        return "Fogo "+ fireID +" iniciado na célula " + celulaIgnicao.toString();
    }
}
