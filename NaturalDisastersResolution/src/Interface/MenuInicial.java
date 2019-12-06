import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;

import javax.swing.SwingConstants;

import javax.swing.JButton;
import javax.swing.JTextField;

public class MenuInicial {

    private JFrame frame;
    private JTextField gridWidth;
    private JTextField gridHeight;
    private JTextField nDrones;
    private JTextField nCamioes;
    private JTextField nAeronaves;
    private JTextField tamanhoMapa;
    private JTextField numPostosComb;
    private JTextField numPostosAgua;
    private JTextField numHabitacoes;
    private JTextField numPontosFlorestais;
    private JButton btnGo = new JButton("Start");
    private JButton btnStart = new JButton("Stop");
    private JButton btnStop = new JButton("Go");

    /**
     * Create the application.
     */
    public MenuInicial() {
        frameInitialize();

        titleInitalize();

        buttonIntialize();

        formInitialize();


    }

    /**
     * Initializes the form
     */
    private void formInitialize() {
        gridWidthInitialize();
        gridHeightInitialize();

    }



    /**
     * Initializes the grid height form field
     */
    private void gridHeightInitialize() {
        gridHeight = new JTextField();
        gridHeight.setHorizontalAlignment(SwingConstants.CENTER);
        gridHeight.setColumns(10);
        gridHeight.setBounds(154, 148, 120, 20);
        frame.getContentPane().add(gridHeight);

        JLabel lblGridHeight = new JLabel("Grid Height");
        lblGridHeight.setHorizontalAlignment(SwingConstants.CENTER);
        lblGridHeight.setBounds(10, 151, 134, 14);
        frame.getContentPane().add(lblGridHeight);
    }

    /**
     * Initializes the grid width form field
     */
    private void gridWidthInitialize() {
        gridWidth = new JTextField();
        gridWidth.setHorizontalAlignment(SwingConstants.CENTER);
        gridWidth.setBounds(154, 117, 120, 20);
        frame.getContentPane().add(gridWidth);
        gridWidth.setColumns(10);
        JLabel lblMaxAirplanes = new JLabel("Grid Width");
        lblMaxAirplanes.setHorizontalAlignment(SwingConstants.CENTER);
        lblMaxAirplanes.setBounds(10, 120, 134, 14);
        frame.getContentPane().add(lblMaxAirplanes);
    }

    /**
     * Initializes the go button
     */
    private void buttonIntialize() {
        btnGo.setBounds(97, 300, 89, 23);
        frame.getContentPane().add(btnGo);
    }

    /**
     * Initializes the app title label
     */
    private void titleInitalize() {
        JLabel lblAgentesInteligentesNo = new JLabel("<html><div style='text-align: center;'>Agentes Inteligentes no Combate a Inc\u00EAndios Florestais</div></html>");
        lblAgentesInteligentesNo.setBounds(0, 0, 284, 106);
        lblAgentesInteligentesNo.setHorizontalAlignment(SwingConstants.CENTER);
        lblAgentesInteligentesNo.setFont(new Font("Leelawadee", Font.BOLD, 20));
        frame.getContentPane().add(lblAgentesInteligentesNo);
    }

    /**
     * Initializes the frame
     */
    private void frameInitialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
    }



    /**
     * Returns the welcome screen frame
     * @return the welcome screen frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Returns the value of the grid width field
     * @return the value of the grid width field
     */
    public JTextField getGridWidth() {
        return gridWidth;
    }

    /**
     * Returns the value of the grid height field
     * @return the value of the grid height field
     */
    public JTextField getGridHeight() {
        return gridHeight;
    }


    /**
     * Returns the button go
     * @return the button go
     */
    public JButton getBtnGo() {
        return btnGo;
    }
}
