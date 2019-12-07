import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUI {

    Mapa mapa;
    private JFrame mainFrame;
    private static JTextArea textArea;
    private JPanel panel_3;
    private JPanel panel_4;
    private JPanel panel_5;
    private JTextField nDrones;
    private JTextField nCamioes;
    private JTextField nAeronaves;
    private JTextField tamanhoMapa;
    private JTextField numPostosComb;
    private JTextField numPostosAgua;
    private JTextField numHab;
    private JTextField numPontosFlorestais;
    private JButton btnStart = new JButton("Start");
    private JButton btnStop = new JButton("Stop");
    MapGrid mapGrid;



    public GUI(Mapa mapa) {
        this.mapa = mapa;

        this.mapGrid = new MapGrid (mapa);

        panel_3 = new JPanel();
        panel_3.setBounds(9, 660, 1095, 93);
       // panel_4 = new JPanel();
        //panel_4.setBounds(810, 10, 720, 290); //parametros + play + stop
        panel_5 = new JPanel();
        panel_5.setBounds(810,315,720,290); // stats

        frameInitialize(mapGrid.panel, panel_3,panel_5);
        captionInitialize(mapa, mapGrid.panel, panel_3);
        textArea = new JTextArea(10, 20);
        textArea.setEditable(false);
        parametersInitialize();
        //parametersPaneInitialize(panel_4);
        statsPaneInitialize(panel_5);
    }
    public void parametersInitialize(){
      nDronesInitialize();
       nAeronavesInitialize();
               nCamioesInitialize();
              tamanhoMapaInitialize();
    numPostosCombInitialize();
    numPostosAguaInitialize();
    numPontosFlorestaisInitialize();
    numHabInitialize();
    buttonStartInitialize();
    buttonStopInitialize();
    }
    /**
     * Initialises the capiton area
     * @param panel main panel
     * @param panel_1 captionPanel
     */
    private void captionInitialize(Mapa m, JPanel panel, JPanel panel_1) {
        panel_3.setLayout(new BorderLayout(1000, 1000));
        JLabel lblCaption = new JLabel("");
        lblCaption.setHorizontalAlignment(SwingConstants.LEFT);
        panel_1.add(lblCaption, BorderLayout.WEST);
        panel.setLayout(new GridLayout(m.size, m.size));
    }

    /**
     * Initialises the frame
     * @param panel main panel
     * @param panel_1 caption panel
     * @param panel_2 info panel
     */
    private void frameInitialize(JPanel panel, JPanel panel_1, JPanel panel_2) {
        mainFrame = new JFrame();
        mainFrame.setTitle("NaturalDisastersResolution");
        mainFrame.getContentPane().setLayout(null);
        mainFrame.getContentPane().add(mapGrid.panel);
        mainFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        mainFrame.setBounds(100, 100, 1600, 800);
        mainFrame.getContentPane().add(panel_1);
        mainFrame.getContentPane().add(panel_2);
       // mainFrame.getContentPane().add(panel_3);
    }

    /**
     * Initalizes the info scroll panel
     * @param panel_2 info panel
     */
    private void parametersPaneInitialize(JPanel panel_2) {
        panel_4.setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel_2.add(scrollPane); //We add the scroll, since the scroll already contains the textArea


    }

    private void statsPaneInitialize(JPanel panel_2) {
        panel_5.setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel_2.add(scrollPane); //We add the scroll, since the scroll already contains the textArea

    }

    private static ImageIcon getScaledImage(ImageIcon srcImg, int w, int h){
        Image image = srcImg.getImage(); // transform it
        Image newimg = image.getScaledInstance(w, h,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }


    /**
     * Logs a message in the GUI
     * @param text Message to log
     */
    public static void log(String text) {
        textArea.append(text);
    }

    /**
     * Gets the GUI frame
     * @return the frame
     */
    public JFrame getFrame() {
        return mainFrame;
    }


    void nDronesInitialize() {
        nDrones = new JTextField();

        nDrones.setColumns(10);
        nDrones.setBounds(910, 20, 50, 20);
        mainFrame.getContentPane().add(nDrones);
        nDrones.setText(String.valueOf(SimulationConfig.NUM_MAX_DRONES));

        JLabel lblMaxDrones = new JLabel("Nº Drones");
        lblMaxDrones.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxDrones.setBounds(800, 20, 134, 14);
        mainFrame.getContentPane().add(lblMaxDrones);
    }
    void nCamioesInitialize() {
        nCamioes = new JTextField();

        nCamioes.setColumns(10);
        nCamioes.setBounds(910, 60, 50, 20);
        mainFrame.getContentPane().add(nCamioes);
        nCamioes.setText(String.valueOf(SimulationConfig.NUM_MAX_CAMIOES));

        JLabel lblMaxCamioes = new JLabel("Nº Camiões");
        lblMaxCamioes.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxCamioes.setBounds(800, 60, 134, 14);
        mainFrame.getContentPane().add(lblMaxCamioes);
    }

    void nAeronavesInitialize() {
        nAeronaves = new JTextField();

        nAeronaves.setColumns(10);
        nAeronaves.setBounds(910, 100, 50, 20);
        mainFrame.getContentPane().add(nAeronaves);
        nAeronaves.setText(String.valueOf(SimulationConfig.NUM_MAX_AERONAVES));

        JLabel lblMaxAeronaves = new JLabel("Nº Aeronaves");
        lblMaxAeronaves.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxAeronaves.setBounds(800, 100, 134, 14);
        mainFrame.getContentPane().add(lblMaxAeronaves);
    }

    void tamanhoMapaInitialize() {
        tamanhoMapa = new JTextField();

        tamanhoMapa.setColumns(10);
        tamanhoMapa.setBounds(1130, 20, 50, 20);
        mainFrame.getContentPane().add(tamanhoMapa);
        tamanhoMapa.setText(String.valueOf(SimulationConfig.TAMANHO_MAPA));

        JLabel lblTamanhoMapa = new JLabel("Tamanho Mapa");
        lblTamanhoMapa.setHorizontalAlignment(SwingConstants.CENTER);
        lblTamanhoMapa.setBounds(980, 20, 134, 14);
        mainFrame.getContentPane().add(lblTamanhoMapa);
    }


    void numPostosAguaInitialize() {
        numPostosAgua = new JTextField();

        numPostosAgua.setColumns(10);
        numPostosAgua.setBounds(1130, 60, 50, 20);
        mainFrame.getContentPane().add(numPostosAgua);
        numPostosAgua.setText(String.valueOf(SimulationConfig.NUM_POSTOS_AGUA));

        JLabel lblMaxPostosAgua = new JLabel("Nº Postos Água");
        lblMaxPostosAgua.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxPostosAgua.setBounds(980, 60, 134, 14);
        mainFrame.getContentPane().add(lblMaxPostosAgua);
    }

    void numPostosCombInitialize() {
        numPostosComb = new JTextField();

        numPostosComb.setColumns(10);
        numPostosComb.setBounds(1130, 100, 50, 20);
        mainFrame.getContentPane().add(numPostosComb);
        numPostosComb.setText(String.valueOf(SimulationConfig.NUM_POSTOS_COMB));

        JLabel lblMaxPostosComb = new JLabel("Nº Postos Combustíveis");
        lblMaxPostosComb.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxPostosComb.setBounds(980, 100, 154, 14);
        mainFrame.getContentPane().add(lblMaxPostosComb);
    }

    void numHabInitialize() {
        numHab = new JTextField();

        numHab.setColumns(10);
        numHab.setBounds(1350, 20, 50, 20);
        mainFrame.getContentPane().add(numHab);
        numHab.setText(String.valueOf(SimulationConfig.NUM_HABITACOES));

        JLabel lblMaxHabitacoes = new JLabel("Nº Habitações");
        lblMaxHabitacoes.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxHabitacoes.setBounds(1200, 20, 154, 14);
        mainFrame.getContentPane().add(lblMaxHabitacoes);
    }

    void numPontosFlorestaisInitialize() {
        numPontosFlorestais = new JTextField();

        numPontosFlorestais.setColumns(10);
        numPontosFlorestais.setBounds(1350, 60, 50, 20);
        mainFrame.getContentPane().add(numPontosFlorestais);
        numPontosFlorestais.setText(String.valueOf(SimulationConfig.NUM_PONTOS_FLORESTAIS));

        JLabel lblMaxPontosFlorestais= new JLabel("Nº Pontos Florestais");
        lblMaxPontosFlorestais.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxPontosFlorestais.setBounds(1200, 60, 154, 14);
        mainFrame.getContentPane().add(lblMaxPontosFlorestais);
    }


    private void buttonStartInitialize() {
        btnStart.setBounds(910, 140, 100, 100);
        mainFrame.getContentPane().add(btnStart);
    }
    private void buttonStopInitialize() {
        btnStop.setBounds(1100, 140, 100, 100);
        mainFrame.getContentPane().add(btnStop);
    }
    public JButton getBtnStart() {
                return btnStart;
    }







}

