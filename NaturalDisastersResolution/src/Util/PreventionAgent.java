import jade.core.AID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreventionAgent implements Serializable {

    AID aid;
    List<Posicao> posicoesDePrevencao;

    PreventionAgent(AID aid, List<Posicao> listPos){
        this.aid = aid;
        this.posicoesDePrevencao = new ArrayList<>();
        for(Posicao p : listPos)
            this.posicoesDePrevencao.add(p);
    }

}
