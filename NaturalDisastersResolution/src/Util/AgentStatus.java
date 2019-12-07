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
    int velocidade;
    boolean disponivel;
    List<Tarefa> tarefas;
    int tempoParaFicarDisponivel;
    String tipo;


    AgentStatus(AgenteParticipativo agent){
        this.aid = agent.getAID();
        this.posAtual = agent.posAtual;
        this.ultimaPosicao = agent.posAnterior;
        this.aguaDisponivel = agent.aguaDisponivel;
        this.combustivelDisponivel = agent.combustivelDisponivel;
        this.velocidade = agent.velocidade;
        this.disponivel = agent.disponivel;
        this.tarefas = agent.tarefasRealizadas;

        this.tempoParaFicarDisponivel = agent.tempoParaFicarDisponivel;

        if(agent instanceof Aeronave)
            tipo = "Plane";
        else if(agent instanceof Camiao)
            tipo = "Firetruck";
        else if(agent instanceof Drone)
            tipo = "Drone";
    }

    void addTarefa(Tarefa t){
        this.tarefas.add(t);
    }

    void atualizarEstado(AgentStatus novoEstado){
        this.posAtual = novoEstado.posAtual;
        this.ultimaPosicao = novoEstado.ultimaPosicao;
        this.aguaDisponivel = novoEstado.aguaDisponivel;
        this.combustivelDisponivel = novoEstado.combustivelDisponivel;
        this.disponivel = novoEstado.disponivel;
        this.tarefas.removeAll(novoEstado.tarefas);
    }

}
