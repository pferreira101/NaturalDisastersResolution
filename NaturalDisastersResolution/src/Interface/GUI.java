import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class GUI {

    AgenteInterface ai;
    Mapa mapa;
    private JFrame mainFrame;
    private static JTextArea textArea;
    private JPanel panel_3;
    private JPanel panel_5;
    MapGrid mapGrid;
    ParametersChanger inputs;
    boolean firstSimulation;
    Map<String,List<Tarefa>> tarefasRealizadas;



    public GUI(Mapa mapa, AgenteInterface ai) {
        this.ai = ai;
        this.mapa = mapa;
        this.firstSimulation = true;
        this.mapGrid = new MapGrid(mapa);
        this.inputs = new ParametersChanger(this);
        this.tarefasRealizadas = new HashMap<>();

        panel_3 = new JPanel();
        panel_3.setBounds(9, 660, 1095, 93);

        panel_5 = new JPanel();
        panel_5.setBounds(810,315,720,290); // stats

        frameInitialize(mapGrid.panel, panel_3, panel_5);
        textArea = new JTextArea(10, 20);
        textArea.setEditable(false);
        parametersInitialize();
        statsPaneInitialize(panel_5);
    }

    void startSimulationDisplay(boolean isSameMap){
        if(!firstSimulation && isSameMap){
            this.mapGrid.resetMapStatus();
        }
        this.ai.startSimulationInfoDisplay();
        this.firstSimulation = false;
    }


    void updateMapa(Mapa mapa){
        this.mapa = mapa;
        this.mapGrid.changeMap(mapa);
    }

    void parametersInitialize(){
        this.inputs.inputsInitializer(mainFrame);
    }


    void stopSimulation() {
        for(String s : this.tarefasRealizadas.keySet()){
            List<Tarefa> ts = this.tarefasRealizadas.get(s);
            if(s != null) System.out.println(s + "  --  " + ts.size());
        }
        this.ai.stopSimulation();
        this.mapGrid.removeVehicles();
        var histograma = new Histograma(mapa);
        histograma.setVisible(true);
        var histograma3Barras = new Histograma3Barras(this.tarefasRealizadas);
        histograma3Barras.setVisible(true);
        SimulationConfig.incSimulationNumber();
    }

    void updateTarefasRealizadas(Map<String, List<Tarefa>> ts){
        for(String s : ts.keySet()){
            List<Tarefa> tarefas = this.tarefasRealizadas.get(s);
            if(tarefas == null) tarefas = new ArrayList<>();
            tarefas.addAll(ts.get(s));
            this.tarefasRealizadas.put(s, tarefas);
        }
    }


    private void frameInitialize(JPanel panel, JPanel panel_1, JPanel panel_2) {
        mainFrame = new JFrame();
        mainFrame.setTitle("NaturalDisastersResolution");
        mainFrame.getContentPane().setLayout(null);
        mainFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        mainFrame.setBounds(100, 100, 1600, 800);
        mainFrame.getContentPane().add(panel_1);
        mainFrame.getContentPane().add(panel_2);
        mainFrame.getContentPane().add(mapGrid.panel);
    }


    private void statsPaneInitialize(JPanel panel_2) {
        panel_5.setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel_2.add(scrollPane); //We add the scroll, since the scroll already contains the textArea

    }



    /**
     * Gets the GUI frame
     * @return the frame
     */
    public JFrame getFrame() {
        return mainFrame;
    }

}

