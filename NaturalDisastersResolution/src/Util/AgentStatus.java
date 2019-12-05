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
    boolean prevencao;
    List<Tarefa> tarefas;
    int tempoParaFicarDisponivel;
    String tipo;


    AgentStatus(AgenteParticipativo agent){
        this.aid = agent.getAID();
        this.posAtual = agent.posAtual;
        this.ultimaPosicao = agent.posAnterior;
        if ( agent.posAnterior != null) System.out.println(agent.posAnterior.toString());
        this.aguaDisponivel = agent.aguaDisponivel;
        this.combustivelDisponivel = agent.combustivelDisponivel;
        this.velocidade = agent.velocidade;
        this.disponivel = agent.disponivel;
        this.prevencao = agent.prevencao;
        this.tarefas = agent.tarefasRealizadas;

        if(agent instanceof Aeronave)
            tipo = "Drone";
        else if(agent instanceof Camiao)
            tipo = "Firetruck";
        else if(agent instanceof Drone)
            tipo = "Plane";
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
        this.prevencao = novoEstado.prevencao;
        this.tarefas.removeAll(novoEstado.tarefas);
    }

}
