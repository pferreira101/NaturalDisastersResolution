import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DFManager {


    static void registerAgent(Agent agent, String type) {
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

    static AID findSingleAgent(Agent a, String type) {
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

    static List<AID> findAgents(Agent a, String type){
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

        return Arrays.stream(result).map(dfAgentDescription -> dfAgentDescription.getName()).collect(Collectors.toList());
    }

    static void deRegister(Agent agente){
        try {
            DFService.deregister(agente);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }


}
