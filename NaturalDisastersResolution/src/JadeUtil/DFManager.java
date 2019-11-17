import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class DFManager {


    public static void registerAgent(Agent agent, String type) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(agent.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(agent.getLocalName());
        dfd.addServices(sd);

        try {
            DFService.register(agent, dfd);
        }
        catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public static AID findAgent(Agent a, String type) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        template.addServices(sd);

        DFAgentDescription[] result = null;

        try {
            result = DFService.search(a, template);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result[0].getName();
    }


}
