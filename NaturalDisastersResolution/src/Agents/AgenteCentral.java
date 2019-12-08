import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.attribute.AttributeView;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class AgenteCentral extends Agent {

    Mapa mapa;
    Map<AID, AgentStatus> agents;
    DeltaSimulationStatus dss;
    int taskId;

    protected void setup() {
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];
        this.taskId = 0;

        DFManager.registerAgent(this, "Central");

        addBehaviour(new ReceiveInfo());
        this.agents = new HashMap<>();
        this.dss = new DeltaSimulationStatus();
    }

    /**
     * Behaviour destinado à receção de mensagens.
     * - aviso de incêndios por parte do agente incendiário
     * - aviso de status dos agentes participativos
     */
    private class ReceiveInfo extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                AID sender = msg.getSender();
                String sendersName = sender.getLocalName();

                if (sendersName.contains("Incendiario") && msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        FireAlert fa = (FireAlert) msg.getContentObject();
                        processFireAlert(fa);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (sendersName.contains("Agente") && msg.getPerformative() == ACLMessage.INFORM) {
                    try {
                        AgentStatus st = (AgentStatus) msg.getContentObject();
                        atualizarEstadoAgente(sender, st);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (sendersName.equals("Interface") && msg.getPerformative() == ACLMessage.QUERY_REF) {
                    sendSimulationInfo(msg);
                }
            } else {
                block();
            }
        }

    }

    private class AssignTask extends OneShotBehaviour {

        private AID agent;
        private LinkedList<Tarefa> tarefas;

        public AssignTask(AID agentAID, Tarefa... tarefas) {
            this.tarefas = new LinkedList<>();
            this.agent = agentAID;

            Arrays.stream(tarefas).forEach(t -> {
                this.tarefas.add(t);
                registaTarefa(agent, t);

                //System.out.println(new Time(System.currentTimeMillis()) +  ": "+ agent.getLocalName() + " --- mandado " + t.toString());

            });
        }

        public void action() {

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(agent);
            try {
                msg.setContentObject(this.tarefas);
            } catch (IOException e) {
                e.printStackTrace();
            }
            send(msg);
        }
    }


    /**
     * Método para registar novo incendio ou expansao de um ja existente e
     * e alocar os devidos recursos
     */

    private void processFireAlert(FireAlert fa) {
        //System.out.println(fa.toString());
        if(this.mapa.incendios.get(fa.fireID) == null){
            this.mapa.registaIncendio(fa);
        } else {
            this.mapa.atualizaIncendio(fa);
        }

        for(Posicao p: fa.celulasFogo)
            this.dss.novosIncendios.add(p);

        alocaRecursos(this.mapa.incendios.get(fa.fireID));
    }


    private void atualizarEstadoAgente(AID agent, AgentStatus status) {

        /*
        // registar celulas apagadas
        status.tarefas.stream().
                filter(tarefa -> tarefa.tipo == Tarefa.APAGAR).
                filter(tarefa -> tarefa.tempo < 4000).
                forEach(tarefa -> this.dss.celulasApagadas.add(tarefa.posicao));*/

        List postosCombAtivos = mapa.getAllPostosCombustiveisAtivos();

        for(Tarefa t : status.tarefas){
            if(t.tipo == Tarefa.APAGAR) {
                if (t.minTempo <= 10000) {
                    dss.celulasApagadas.add(t.posicao);
                } else{
                    dss.celulasArdidas.add(t.posicao);
                    mapa.areaArdida.add(t.posicao);

                    if(postosCombAtivos.contains(t.posicao)){
                        for(PostoCombustivel posto : mapa.postosCombustivel){
                            if(posto.pos.equals(t.posicao))
                                posto.ativo = false;
                        }
                    }

                }
            }
        }

        // registar novas posicoes
        this.dss.estadoAgentes.add(status);

        AgentStatus as = this.agents.get(agent);


        if (as != null)
            as.atualizarEstado(status);
        else
            this.agents.put(agent, status);
    }

    private void alocaRecursos(Incendio incendio) {
        Tarefa abastecercomb, apagar, prevenir, abasteceragua;
        AID choosenAgent = null;
        AID secondChoosenAgent = null;
        int minTempo = 100000; // 100 segundos
        int secondMinTempo = 200000;
        Posicao postoComb = null;
        Posicao secondPostoComb = null;
        Posicao postoAgua = null;
        Posicao secondPostoAgua = null;
        int primeiroAbastecimento = 0;
        int secondPrimeiroAbastecimento = 0;


        for(Posicao p: incendio.areaAfetada) {
            int i=0;

            for (AgentStatus ap : this.agents.values()) {
                Postos postos = checkDisponibilidadeAgente(ap, p); // Tempo minimo, e Posicao de onde abastecer caso seja indicado a faze-lo
                int tempoParaFicarDisponivel = postos.tempo;
                Posicao ondeAbastecerComb = postos.postoComb;
                Posicao ondeAbastecerAgua = postos.postoAgua;
                int option = postos.option;
                // second* -> segundo mais rápido -> vai ser o agente preventivo
                if (tempoParaFicarDisponivel >= minTempo && tempoParaFicarDisponivel < secondMinTempo) {
                    secondMinTempo = tempoParaFicarDisponivel;
                    secondChoosenAgent = ap.aid;
                    secondPostoComb = ondeAbastecerComb;
                    secondPostoAgua = ondeAbastecerAgua;
                    secondPrimeiroAbastecimento = option;
                } else if (tempoParaFicarDisponivel < minTempo) {
                    secondMinTempo = minTempo;
                    minTempo = tempoParaFicarDisponivel;
                    secondChoosenAgent = choosenAgent;
                    choosenAgent = ap.aid;
                    secondPostoComb = postoComb;
                    postoComb = ondeAbastecerComb;
                    secondPostoAgua = postoAgua;
                    postoAgua = ondeAbastecerAgua;
                    secondPrimeiroAbastecimento = primeiroAbastecimento;
                    primeiroAbastecimento = option;
                }
            }

            //System.out.println(agents.get(closestAgent).toString() + " tempoSomaTotal FINAL: " + minTempo + " " + agents.get(closestAgent).tipo);

            if (postoComb != null && postoAgua != null && primeiroAbastecimento==0) {
                abastecercomb = new Tarefa(taskId++, Tarefa.ABASTECERCOMB, postoComb);
                this.addBehaviour(new AssignTask(choosenAgent, abastecercomb));

                abasteceragua = new Tarefa(taskId++, Tarefa.ABASTECERAGUA, postoAgua);
                this.addBehaviour(new AssignTask(choosenAgent, abasteceragua));

            }else if(postoComb != null && postoAgua != null && primeiroAbastecimento==1){
                abasteceragua = new Tarefa(taskId++, Tarefa.ABASTECERAGUA, postoAgua);
                this.addBehaviour(new AssignTask(choosenAgent, abasteceragua));

                abastecercomb = new Tarefa(taskId++, Tarefa.ABASTECERCOMB, postoComb);
                this.addBehaviour(new AssignTask(choosenAgent, abastecercomb));

            }else if(postoComb != null && postoAgua == null){
                abastecercomb = new Tarefa(taskId++, Tarefa.ABASTECERCOMB, postoComb);
                this.addBehaviour(new AssignTask(choosenAgent, abastecercomb));

            }else if(postoComb == null && postoAgua != null){
                abasteceragua = new Tarefa(taskId++, Tarefa.ABASTECERAGUA, postoAgua);
                this.addBehaviour(new AssignTask(choosenAgent, abasteceragua));
            }

            apagar = new Tarefa(taskId++, Tarefa.APAGAR, incendio.fireId, p, minTempo);
            //System.out.println("-------------------------------------------------------------------------------- minTempo: " + minTempo);
            this.addBehaviour(new AssignTask(choosenAgent, apagar));


            if (mapa.floresta.contains(p)) { // caso a tarefa seja numa floresta e exiga segundo agente (agente preventivo)

                List<Posicao> adjFlo;
                Posicao pAdjacent;
                adjFlo = mapa.posicoesFlorestaAdjacente(p);

                if(!adjFlo.isEmpty()) {

                    pAdjacent = mapa.getRandAdjacentPositions(adjFlo);


                    if (secondPostoComb != null && secondPostoAgua != null && secondPrimeiroAbastecimento==0) {
                        abastecercomb = new Tarefa(taskId++, Tarefa.ABASTECERCOMB, secondPostoComb);
                        this.addBehaviour(new AssignTask(secondChoosenAgent, abastecercomb));

                        abasteceragua = new Tarefa(taskId++, Tarefa.ABASTECERAGUA, secondPostoAgua);
                        this.addBehaviour(new AssignTask(secondChoosenAgent, abasteceragua));

                    }else if(secondPostoComb != null && secondPostoAgua != null && secondPrimeiroAbastecimento==1){
                        abasteceragua = new Tarefa(taskId++, Tarefa.ABASTECERAGUA, secondPostoAgua);
                        this.addBehaviour(new AssignTask(secondChoosenAgent, abasteceragua));

                        abastecercomb = new Tarefa(taskId++, Tarefa.ABASTECERCOMB, secondPostoComb);
                        this.addBehaviour(new AssignTask(secondChoosenAgent, abastecercomb));

                    }else if(secondPostoComb != null && secondPostoAgua == null){
                        abastecercomb = new Tarefa(taskId++, Tarefa.ABASTECERCOMB, secondPostoComb);
                        this.addBehaviour(new AssignTask(secondChoosenAgent, abastecercomb));

                    }else if(secondPostoComb == null && secondPostoAgua != null){
                        abasteceragua = new Tarefa(taskId++, Tarefa.ABASTECERAGUA, secondPostoAgua);
                        this.addBehaviour(new AssignTask(secondChoosenAgent, abasteceragua));
                    }

                    prevenir = new Tarefa(taskId++, Tarefa.PREVENIR, incendio.fireId, pAdjacent);
                    this.addBehaviour(new AssignTask(secondChoosenAgent, prevenir));
                }
            }
        }

    }


    Postos checkDisponibilidadeAgente(AgentStatus ap, Posicao incendio){
        Posicao ondeAbastecerComb;
        Posicao ondeAbastecerAgua;
        Postos postos = null;
        int tempo = 0;
        // calcular combustivel e posição do agente após este realizar todas as suas tarefas
        int maxFuel = 0;
        int maxAgua = 0;
        int combustivel = ap.combustivelDisponivel;
        int agua = ap.aguaDisponivel;
        Posicao posição = ap.posAtual;

        switch (ap.tipo){
            case "Drone":
                maxFuel = Drone.capacidadeMaxCombustivel;
                break;
            case "Firetruck":
                maxFuel = Camiao.capacidadeMaxCombustivel;
                break;
            case "Plane":
                maxFuel = Aeronave.capacidadeMaxCombustivel;
                break;
        }

        switch (ap.tipo){
            case "Drone":
                maxAgua = Drone.capacidadeMaxAgua;
                break;
            case "Firetruck":
                maxAgua = Camiao.capacidadeMaxAgua;
                break;
            case "Plane":
                maxAgua = Aeronave.capacidadeMaxAgua;
                break;
        }

        for(Tarefa t : ap.tarefas){
            int distancia = Posicao.distanceBetween(posição, t.posicao);
            combustivel -= distancia;
            if(t.tipo == Tarefa.ABASTECERCOMB) combustivel = maxFuel;
            if(t.tipo == Tarefa.ABASTECERAGUA) agua = maxAgua;
            if(t.tipo == Tarefa.APAGAR) agua--;
            posição = t.posicao;
            switch (ap.tipo){
                case "Drone":
                    tempo += (distancia*(4/Drone.velocidade))*1000 + 1000;
                    break;
                case "Firetruck":
                    tempo += (distancia*(4/Camiao.velocidade))*1000 + 1000;
                    break;
                case "Plane":
                    tempo += (distancia*(4/Aeronave.velocidade))*1000 + 1000;
                    break;
            }
        }

        int distanciaAgenteIncendio = Posicao.distanceBetween(posição, incendio);
        int distanciaPostoCombMaisProxIncendio = getMinDistanceIncendioPostoComb(incendio);

        boolean temCombustivelSuficiente = combustivel >= (distanciaAgenteIncendio + distanciaPostoCombMaisProxIncendio);

        boolean temAguaSuficiente = agua > 0;

        if(!temCombustivelSuficiente){
                AbstractMap.SimpleEntry<Posicao, Integer> postoCombMaisProximo = this.mapa.getPostoCombEntreAgenteIncendio(posição, incendio); // Posicao do melhor posto, e Distancia minima do agente ao incendio, passando pelo posto
            if(temAguaSuficiente) {
                switch (ap.tipo) {
                    case "Drone":
                        tempo += (postoCombMaisProximo.getValue() * (4 / Drone.velocidade)) * 1000 + 1000;
                        break;
                    case "Firetruck":
                        tempo += (postoCombMaisProximo.getValue() * (4 / Camiao.velocidade)) * 1000 + 1000;
                        break;
                    case "Plane":
                        tempo += (postoCombMaisProximo.getValue() * (4 / Aeronave.velocidade)) * 1000 + 1000;
                        break;
                }
                ondeAbastecerComb = postoCombMaisProximo.getKey();
                postos = new Postos(tempo,ondeAbastecerComb, 2);
            }else{
                AbstractMap.SimpleEntry<Posicao, Integer> postoAguaMaisProximo = this.mapa.getPostoAguaEntreAgenteIncendio(posição, incendio);
                switch (ap.tipo) {
                    case "Drone":
                        tempo += (postoCombMaisProximo.getValue() * (4 / Drone.velocidade)) * 1000 + 1000;
                        tempo += (postoAguaMaisProximo.getValue() * (4 / Drone.velocidade)) * 1000 + 1000;
                        break;
                    case "Firetruck":
                        tempo += (postoCombMaisProximo.getValue() * (4 / Camiao.velocidade)) * 1000 + 1000;
                        tempo += (postoAguaMaisProximo.getValue() * (4 / Camiao.velocidade)) * 1000 + 1000;
                        break;
                    case "Plane":
                        tempo += (postoCombMaisProximo.getValue() * (4 / Aeronave.velocidade)) * 1000 + 1000;
                        tempo += (postoAguaMaisProximo.getValue() * (4 / Aeronave.velocidade)) * 1000 + 1000;
                        break;
                }
                ondeAbastecerComb = postoCombMaisProximo.getKey();
                ondeAbastecerAgua = postoAguaMaisProximo.getKey();
                if(Posicao.distanceBetween(posição,ondeAbastecerComb) < Posicao.distanceBetween(posição,ondeAbastecerAgua)) {
                    postos = new Postos(tempo,ondeAbastecerComb,ondeAbastecerAgua,0);
                } else{ postos = new Postos(tempo,ondeAbastecerComb,ondeAbastecerAgua,1);}
            }
        }
        else {
            if(temAguaSuficiente) {
                switch (ap.tipo) {
                    case "Drone":
                        tempo += (distanciaAgenteIncendio * (4 / Drone.velocidade)) * 1000;
                        break;
                    case "Firetruck":
                        tempo += (distanciaAgenteIncendio * (4 / Camiao.velocidade)) * 1000;
                        break;
                    case "Plane":
                        tempo += (distanciaAgenteIncendio * (4 / Aeronave.velocidade)) * 1000;
                        break;
                }
                postos = new Postos(tempo);
            }
            else{
                AbstractMap.SimpleEntry<Posicao, Integer> postoAguaMaisProximo = this.mapa.getPostoAguaEntreAgenteIncendio(posição, incendio);
                switch (ap.tipo) {
                    case "Drone":
                        tempo += (postoAguaMaisProximo.getValue() * (4 / Drone.velocidade)) * 1000 + 1000;
                        break;
                    case "Firetruck":
                        tempo += (postoAguaMaisProximo.getValue() * (4 / Camiao.velocidade)) * 1000 + 1000;
                        break;
                    case "Plane":
                        tempo += (postoAguaMaisProximo.getValue() * (4 / Aeronave.velocidade)) * 1000 + 1000;
                        break;
                }
                ondeAbastecerAgua = postoAguaMaisProximo.getKey();
                postos = new Postos(tempo,ondeAbastecerAgua,3);
            }
        }
        return postos;
    }

    public class Postos{
        int tempo;
        Posicao postoComb;
        Posicao postoAgua;
        int option;
        // 0 -> postoComb é o primeiro destino;
        // 1 -> postoAgua é o primeiro destino;
        // 2 -> só postoComb;
        // 3 -> só postoAgua;
        // 4 -> direto (não precisa de nada);

        Postos(int tempo){
            this.tempo = tempo;
            this.option = 4;
        }

        Postos(int tempo, Posicao posto, int option){
            if(option==2){
                this.postoComb = posto;
                this.option = 2;
            }
            if(option==3){
                this.postoAgua = posto;
                this.option = 3;
            }
            this.tempo = tempo;
        }

        Postos(int tempo, Posicao postoComb, Posicao postoAgua, int option){
            this.tempo = tempo;
            this.postoComb = postoComb;
            this.postoAgua = postoAgua;
            this.option = option;
        }

    }

    /*private int autonomiaCombustivel(AgentStatus ap, Posicao incendio){
        int combSomaTotal = 0;

        // apagar
        int distAgenteIncendio;
        if(ap.tarefas.size()==0){
            //System.out.println(ap.toString() + " DIRETO");
            distAgenteIncendio = Posicao.distanceBetween(ap.posAtual, incendio);
        }
        else{
            //System.out.println(ap.toString() + " INDIRETO");
            int distAgenteTarefaFinal = ap.tempoParaFicarDisponivel/((4/ap.velocidade)*1000);
            //System.out.println(ap.toString() + " distAgenteTarefaFinal: " + distAgenteTarefaFinal + " " + ap.tipo);
            int distTarefaFinalIncendio = Posicao.distanceBetween(ap.tarefas.get(ap.tarefas.size()-1).posicao, incendio);
            //System.out.println(ap.toString() + " distTarefaFinalIncendio: " + distTarefaFinalIncendio + " " + ap.tipo);
            distAgenteIncendio = distAgenteTarefaFinal + distTarefaFinalIncendio;
        }

        int minTempoAgenteIncendio = distAgenteIncendio*(4/ap.velocidade)*1000;

        //System.out.println(ap.toString() + " distAgenteIncendio: " + distAgenteIncendio + " " + ap.tipo);
        //System.out.println(ap.toString() + " minTempoAgenteIncendio: " + minTempoAgenteIncendio + " " + ap.tipo);
        combSomaTotal += distAgenteIncendio;

        // abastecer
        int minDistIncendioPosto = getMinDistanceIncendioPosto(incendio);
        //System.out.println(ap.toString() + " distIncendioPosto: " + minDistIncendioPosto + " " + ap.tipo);

        combSomaTotal += minDistIncendioPosto;

        //System.out.println(ap.toString() + " combDisponivel " + ap.combustivelDisponivel + " combSomaTotal " + combSomaTotal + " " + ap.tipo);

        if(ap.combustivelDisponivel>combSomaTotal) return minTempoAgenteIncendio;
        else return -1;
    }*/

    private int getMinDistanceIncendioPostoComb(Posicao incendio){
        int minDistance = 1000;

        for(PostoCombustivel p : mapa.postosCombustivel){
            int distance = Posicao.distanceBetween(incendio,p.pos);
            if (p.ativo == true && distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;
    }

    private void registaTarefa(AID agent, Tarefa t) {
        this.agents.get(agent).addTarefa(t);
    }


    private void sendSimulationInfo(ACLMessage msg) {
        //this.dss.estadoAgentes = new ArrayList<>(this.agents.values()); // tem que se tirar esta linha e passar a registar apenas novas posicoes
        ACLMessage reply = msg.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        try {
            reply.setContentObject(this.dss);
        } catch (IOException e) {
            e.printStackTrace();
        }
        send(reply);
        this.dss = new DeltaSimulationStatus(); // reset ao objeto para guardar apenas novas alteracoes
    }
}

