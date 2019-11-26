import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeltaSimulationStatus implements Serializable {

    List<Posicao> novosIncendios;
    List<Posicao> celulasApagadas;
    List<AgentStatus> estadoAgentes;


    public DeltaSimulationStatus(){
        this.novosIncendios = new ArrayList<>();
        this.celulasApagadas = new ArrayList<>();
        this.estadoAgentes = new ArrayList<>();
    }

    public DeltaSimulationStatus(Collection<Posicao> incendios, Collection<AgentStatus> agentes) {
        this.novosIncendios = new ArrayList<>(incendios);
        this.estadoAgentes = new ArrayList<>(agentes);
    }
}
