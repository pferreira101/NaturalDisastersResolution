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

    public void registaTarefas(String tipo, Tarefa t) {
        List<Tarefa> tarefas = tarefasRealizadas.get(tipo);
        if(tarefas == null)
            tarefas = new ArrayList<>();

        tarefas.add(t);
        tarefasRealizadas.put(tipo, tarefas);
    }
}