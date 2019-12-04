import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FireAlert implements Serializable {

    int fireID;
    List<Posicao> celulasFogo; // colecao de celulas
    Timestamp inicioFogo;

    FireAlert(int id, Posicao p, Timestamp t){
        this.fireID = id;
        this.celulasFogo = new ArrayList<>();
        this.celulasFogo.add(p);
        this.inicioFogo = t;
    }

    FireAlert(int id, List<Posicao> pos){
        this.fireID = id;
        this.celulasFogo = new ArrayList<>();
        for(Posicao p : pos) {
            this.celulasFogo.add(p);
        }
    }

    /*
    @Override
    public String toString() {
        return "Fogo "+ fireID +" iniciado na célula " + celulasFogo.toString();
    }
     */
}
