import jade.core.AID;
import jade.core.Agent;

import javax.swing.text.Position;
import java.io.Serializable;
import java.util.*;

public class DeltaSimulationStatus implements Serializable {

    List<Posicao> novosIncendios;
    List<Posicao> celulasApagadas;
    List<Posicao> celulasArdidas;
    List<AgentStatus> estadoAgentes;
    Map<AgentStatus,Tarefa> tarefasRealizadas;


    public DeltaSimulationStatus(){
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



}

