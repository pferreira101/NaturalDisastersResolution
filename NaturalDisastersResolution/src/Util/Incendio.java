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
        this.areaAfetada.add(fa.celulaIgnicao);
    }


    void registaExpansao(FireAlert fa) {
        this.areaAfetada.add(fa.celulaIgnicao);
    }

    void registaCelulaApagada(Posicao p){
        if(this.areaAfetada.contains(p))
            System.out.println("A registar extinsao na celula " + p.toString() + " do incendio " + this.fireId);
        else System.out.println("nao existe a celula " +p.toString()+ " neste incendio");
        this.areaAfetada.remove(p);
    }
}
