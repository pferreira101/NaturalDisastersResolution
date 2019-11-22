import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;



public class GUI {

    private static boolean active;
    private static Mapa m;
    private static AgenteCentral agenteCentral;
    private JFrame mainFrame;
    private static JLabel[][] grid;
    private static JTextArea textArea;
    private JPanel panel_3;
    private JPanel panel_4;

    public GUI() {
        active = true;
    }

    /**
     * Interface Gráfica
     * @param agenteCentral o Agente Central
     */
    public GUI(Mapa m, AgenteCentral agenteCentral) {
        GUI.agenteCentral = agenteCentral;

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 794, 642);
        panel_3 = new JPanel();
        panel_3.setBounds(9, 660, 1095, 93);
        panel_4 = new JPanel();
        panel_4.setBounds(810, 10, 762, 642);

        frameInitialize(panel, panel_3, panel_4);

        captionInitialize(m, panel, panel_3);

        textArea = new JTextArea(10, 20);
        textArea.setEditable(false);

        scrollPaneInitialize(panel_4);
        gridInitialize(m, agenteCentral, panel);

    }

    /**
     * Initializes the grid
     * @param agenteCentral world agent
     * @param panel main panel
     */
    private void gridInitialize(Mapa m, AgenteCentral agenteCentral, JPanel panel) {
        grid= new JLabel[m.size][m.size];
        for (int i = 0; i < m.size; i++){
            for (int j = 0; j < m.size; j++){
                grid[j][i] = new JLabel();
                grid[j][i].setBorder(new LineBorder(Color.BLACK));
                grid[j][i].setHorizontalAlignment(SwingConstants.CENTER);
                grid[j][i].setVerticalAlignment(SwingConstants.CENTER);
                grid[j][i].setOpaque(true);

                setCell(m,agenteCentral.getPosicao(j,i), grid[j][i]);
                panel.add(grid[j][i]);
            }
        }
    }

    /**
     * Initialises the capiton area
     * @param panel main panel
     * @param panel_1 captionPanel
     */
    private void captionInitialize(Mapa m,JPanel panel, JPanel panel_1) {
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
        mainFrame.getContentPane().add(panel);
        mainFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        mainFrame.setBounds(100, 100, 1600, 800);
        mainFrame.getContentPane().add(panel_1);
        mainFrame.getContentPane().add(panel_2);
    }

    /**
     * Initalizes the info scroll panel
     * @param panel_2 info panel
     */
    private void scrollPaneInitialize(JPanel panel_2) {
        panel_4.setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel_2.add(scrollPane); //We add the scroll, since the scroll already contains the textArea
        panel_2.setPreferredSize(new Dimension(800,500));
    }

    /**
     * Sets a given cell of the GUI grid
     * @param p cell of the worldMap being processed
     * @param gridCell cell of the grid being processed
     */
    private static void setCell(Mapa m, Posicao p, JLabel gridCell) {
        if (p != null) {

            gridCell.setText(p.toString());
            gridCell.setHorizontalTextPosition(JLabel.CENTER);
            gridCell.setVerticalTextPosition(JLabel.BOTTOM);

            if (m.onFire(p)) {
                gridCell.setBackground(Color.orange);
            }

            if (m.hab(p)) {
                gridCell.setBackground(Color.yellow);

            }

            if (m.postoA(p)) {
                gridCell.setBackground(Color.cyan);

            }

            if (m.postoC(p)) {
                gridCell.setBackground(Color.gray);

            }
            if (m.arvore(p)) {
                gridCell.setBackground(Color.green);

            }
        }
        else {
            gridCell.setBackground(null);
            gridCell.setIcon(null);
            gridCell.setText("");
        }
    }


    /**
     * Called on tick to fill the grid with the updated positions of the objects
     */
    public static void fillGrid(Mapa m) {
        for (int i = 0; i < m.size; i++){
            for (int j = 0; j <m.size; j++){
                grid[j][i].setOpaque(true);
                setCell(m,agenteCentral.getPosicao(j,i), grid[j][i]);
            }
        }
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

    public static boolean isActive() {
        return active;
    }


}
