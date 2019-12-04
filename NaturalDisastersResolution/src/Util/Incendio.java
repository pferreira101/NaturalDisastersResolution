import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Incendio implements Serializable {

    int fireId;
    int gravidade;
    Timestamp inicio;
    long duracao;
    List<Posicao> areaAfetada;

    Incendio(int fireId, int gravidade, int duracao, Posicao pontoIgnicao){
        this.fireId = fireId;
        this.gravidade = gravidade;
        this.duracao = duracao;
        this.areaAfetada = new ArrayList<>();
        this.areaAfetada.add(pontoIgnicao);
    }

    
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
            System.out.println("A registar extinsao na celula " + p.toString() + " do incendio " + this.fireId);
        else System.out.println("nao existe a celula " +p.toString()+ " neste incendio");
        this.areaAfetada.remove(p);
    }
}
