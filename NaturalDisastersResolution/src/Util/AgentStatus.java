import jade.core.AID;

import java.io.Serializable;
import java.util.List;

public class AgentStatus implements Serializable {

    AID aid;
    Posicao pos;
    int aguaDisponivel;
    int combustivelDisponivel;
    boolean disponivel;
    List<Tarefa> tarefas;

    Posicao getPos() {
        return this.pos;
    }
    AgentStatus(AID aid, Posicao pos, int aguaDisponivel, int combustivelDisponivel, boolean disponivel, List<Tarefa> tarefas){
        this.aid = aid;
        this.pos = pos;
        this.aguaDisponivel = aguaDisponivel;
        this.combustivelDisponivel = combustivelDisponivel;
        this.disponivel = disponivel;
        this.tarefas = tarefas;
    }

    void addTarefa(Tarefa t){
        this.tarefas.add(t);
    }

    void atualizarEstado(AgentStatus novoEstado){
        this.pos = novoEstado.pos;
        this.aguaDisponivel = novoEstado.aguaDisponivel;
        this.combustivelDisponivel = novoEstado.combustivelDisponivel;
        this.disponivel = novoEstado.disponivel;
        this.tarefas.removeAll(novoEstado.tarefas);
    }

}
