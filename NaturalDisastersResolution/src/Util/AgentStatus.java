import jade.core.AID;
import jade.core.Agent;

import java.io.Serializable;
import java.util.List;

public class AgentStatus implements Serializable {

    AID aid;
    Posicao posAtual;
    Posicao ultimaPosicao;
    int aguaDisponivel;
    int combustivelDisponivel;
    boolean disponivel;
    List<Tarefa> tarefas;
    int tipo;


    AgentStatus(AgenteParticipativo agent){
        this.aid = agent.getAID();
        this.posAtual = agent.posAtual;
        this.ultimaPosicao = agent.posAnterior;
        this.aguaDisponivel = agent.aguaDisponivel;
        this.combustivelDisponivel = agent.combustivelDisponivel;
        this.disponivel = agent.disponivel;
        this.tarefas = agent.tarefasRealizadas;

        if(agent instanceof Aeronave)
            tipo = 0;
        else if(agent instanceof Camiao)
            tipo = 1;
        else if(agent instanceof Drone)
            tipo = 2;
    }

    void addTarefa(Tarefa t){
        this.tarefas.add(t);
    }

    void atualizarEstado(AgentStatus novoEstado){
        this.posAtual = novoEstado.posAtual;
        this.aguaDisponivel = novoEstado.aguaDisponivel;
        this.combustivelDisponivel = novoEstado.combustivelDisponivel;
        this.disponivel = novoEstado.disponivel;
        this.tarefas.removeAll(novoEstado.tarefas);
    }

}
