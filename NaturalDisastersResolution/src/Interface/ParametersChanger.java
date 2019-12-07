import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Guard;

public class ParametersChanger {

    GUI gui;
    JFrame mainFrame;
    JTextField nDrones;
    JTextField nCamioes;
    JTextField nAeronaves;
    JTextField tamanhoMapa;
    JTextField numPostosComb;
    JTextField numPostosAgua;
    JTextField numHab;
    JTextField numPontosFlorestais;

    JButton btnGenerate = new JButton("Generate Map");
    JButton btnStart = new JButton("Start");
    JButton btnStop = new JButton("Stop");

    ParametersChanger(GUI gui){
        this.gui = gui;
    }

    void inputsInitializer(JFrame mainFrame){
        this.mainFrame = mainFrame;

        numAreonavesInitialze();
        numDronesInitialize();
        numCamioesInitialize();
        numPostosCombInitialize();
        numPostosAguaInitialize();
        numHabInitialize();
        numPontosFlorestaisInitialize();
        tamanhoMapaInitialize();
        buttonGenerateInitialize();
        buttonStartInitialize();
        buttonStopInitialize();
    }



    void numDronesInitialize() {
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

    void numCamioesInitialize() {
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

    void numAreonavesInitialze() {
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

    void numHabInitialize() {
        numHab = new JTextField();

        numHab.setColumns(10);
        numHab.setBounds(910, 140, 50, 20);
        mainFrame.getContentPane().add(numHab);
        numHab.setText(String.valueOf(SimulationConfig.NUM_HABITACOES));

        JLabel lblMaxHabitacoes = new JLabel("Nº Habitações");
        lblMaxHabitacoes.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxHabitacoes.setBounds(800, 140, 134, 14);
        mainFrame.getContentPane().add(lblMaxHabitacoes);
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


    void numPontosFlorestaisInitialize() {
        numPontosFlorestais = new JTextField();

        numPontosFlorestais.setColumns(10);
        numPontosFlorestais.setBounds(1130, 140, 50, 20);
        mainFrame.getContentPane().add(numPontosFlorestais);
        numPontosFlorestais.setText(String.valueOf(SimulationConfig.NUM_PONTOS_FLORESTAIS));

        JLabel lblMaxPontosFlorestais= new JLabel("Nº Pontos Florestais");
        lblMaxPontosFlorestais.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxPontosFlorestais.setBounds(980, 140, 154, 14);
        mainFrame.getContentPane().add(lblMaxPontosFlorestais);
    }



    private void buttonGenerateInitialize() {
        btnGenerate.setBounds(850, 200, 100, 100);
        mainFrame.getContentPane().add(btnGenerate);

        btnGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int mapSize = Integer.parseInt(tamanhoMapa.getText());
                int nAgua = Integer.parseInt(numPostosAgua.getText());
                int nComb = Integer.parseInt(numPostosComb.getText());
                int nHabitacoes = Integer.parseInt(numHab.getText());
                int nFloresta = Integer.parseInt(numPontosFlorestais.getText());
                SimulationConfig.changeMapSpecs(mapSize, nAgua, nComb, nHabitacoes, nFloresta);
                gui.updateMapa(App.generateMap());
            }
        });
    }

    private void buttonStartInitialize() {
        btnStart.setBounds(1000, 200, 100, 100);
        mainFrame.getContentPane().add(btnStart);

        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean sameMap = true;
                int numDrones = Integer.parseInt(nDrones.getText());
                int numAvioes = Integer.parseInt(nAeronaves.getText());
                int numCamioes = Integer.parseInt(nCamioes.getText());
                SimulationConfig.changeNumVehicles(numDrones, numCamioes, numAvioes);

                int mapSize = Integer.parseInt(tamanhoMapa.getText());
                int nAgua = Integer.parseInt(numPostosAgua.getText());
                int nComb = Integer.parseInt(numPostosComb.getText());
                int nHabitacoes = Integer.parseInt(numHab.getText());
                int nFloresta = Integer.parseInt(numPontosFlorestais.getText());

                if(mapSize != SimulationConfig.TAMANHO_MAPA
                        || nAgua != SimulationConfig.NUM_POSTOS_AGUA
                        || nComb != SimulationConfig.NUM_POSTOS_COMB
                        || nHabitacoes != SimulationConfig.NUM_HABITACOES
                        || nFloresta != SimulationConfig.NUM_PONTOS_FLORESTAIS)
                {
                    SimulationConfig.changeMapSpecs(mapSize, nAgua, nComb, nHabitacoes, nFloresta);
                    gui.updateMapa(App.generateMap());
                    sameMap = false;
                }
                App.run();
                gui.startSimulationDisplay(sameMap);
            }
        });
    }

    private void buttonStopInitialize() {
        btnStop.setBounds(1150, 200, 100, 100);
        mainFrame.getContentPane().add(btnStop);

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.stopSimulation();
            }
        });
    }
}
