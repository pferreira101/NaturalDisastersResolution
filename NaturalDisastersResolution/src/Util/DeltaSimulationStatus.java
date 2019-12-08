import jade.core.AID;
import jade.core.Agent;

import javax.swing.text.Position;
import java.io.Serializable;
import java.util.*;

public class DeltaSimulationStatus implements Serializable {
    int simulation = SimulationConfig.SIMULATION_NUMBER;

    List<Posicao> novosIncendios;
    List<Posicao> celulasApagadas;
    List<Posicao> celulasArdidas;
    List<AgentStatus> estadoAgentes;
    Map<String, List<Tarefa>> tarefasRealizadas;


    public DeltaSimulationStatus() {
        this.novosIncendios = new ArrayList<>();
        this.celulasApagadas = new ArrayList<>();
        this.celulasArdidas = new ArrayList<>();
        this.estadoAgentes = new ArrayList<>();
        this.tarefasRealizadas = new HashMap<>();
    }

    public DeltaSimulationStatus(Collection<Posicao> incendios, Collection<AgentStatus> agentes) {
        this.novosIncendios = new ArrayList<>(incendios);
        this.estadoAgentes = new ArrayList<>(agentes);
    }

    public void registaTarefas(String tipo, Tarefa t) {
        List<Tarefa> tarefas = tarefasRealizadas.get(tipo);
        if(tarefas == null)
            tarefas = new ArrayList<>();

        tarefas.add(t);
        tarefasRealizadas.put(tipo, tarefas);
    }
}