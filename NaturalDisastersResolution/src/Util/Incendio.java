import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Incendio implements Serializable {

    int fireId;
    Timestamp inicio;
    List<Posicao> areaAfetada;

    Incendio(FireAlert fa){
        this.fireId = fa.fireID;
        this.inicio = fa.inicioFogo;
        this.areaAfetada = new ArrayList<>();
        for(Posicao p : fa.celulasFogo)
            this.areaAfetada.add(p);
    }

    void registaExpansao(FireAlert fa) {
        for(Posicao p : fa.celulasFogo)
            this.areaAfetada.add(p);
    }

    void registaCelulaApagada(Posicao p){
        if(this.areaAfetada.contains(p))
            this.areaAfetada.remove(p);
    }
}
