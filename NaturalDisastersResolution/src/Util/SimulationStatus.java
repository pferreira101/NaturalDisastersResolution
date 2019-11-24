import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimulationStatus implements Serializable {

    List<Incendio> incendiosAtivos;
    List<AgentStatus> estadoAgentes;


    public SimulationStatus(Collection<Incendio> incendios, Collection<AgentStatus> agentes) {
        this.incendiosAtivos = new ArrayList<>(incendios);
        this.estadoAgentes = new ArrayList<>(agentes);
    }
}
