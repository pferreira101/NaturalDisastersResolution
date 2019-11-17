import jade.core.AID;

import java.io.Serializable;

public class AgentStatus implements Serializable {

    AID aid;
    Posicao pos;
    int aguaDisponivel;
    int combustivelDisponivel;
    boolean disponivel;


    AgentStatus(AID aid, Posicao pos, int aguaDisponivel, int combustivelDisponivel, boolean disponivel){
        this.aid = aid;
        this.pos = pos;
        this.aguaDisponivel = aguaDisponivel;
        this.combustivelDisponivel = combustivelDisponivel;
        this.disponivel = disponivel;
    }

}
