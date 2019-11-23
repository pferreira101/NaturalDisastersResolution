import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;

import java.util.List;

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
    public GUI(Mapa mapa, AgenteCentral agenteCentral) {
        GUI.agenteCentral = agenteCentral;

        this.m = mapa;

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
        initializeMapGrid(m, panel);

    }

    /**
     * Initializes the grid
     * @param panel main panel
     */
    private void initializeMapGrid(Mapa m, JPanel panel) {
        grid = new JLabel[m.size][m.size];
        for (int i = 0; i < m.size; i++){
            for (int j = 0; j < m.size; j++){
                grid[j][i] = new JLabel();
                grid[j][i].setBorder(new LineBorder(Color.BLACK));
                grid[j][i].setHorizontalAlignment(SwingConstants.CENTER);
                grid[j][i].setVerticalAlignment(SwingConstants.CENTER);
                grid[j][i].setOpaque(true);

                grid[j][i].setText("(" + i +","+ j +")");
                grid[j][i].setHorizontalTextPosition(JLabel.CENTER);
                grid[j][i].setVerticalTextPosition(JLabel.BOTTOM);

                panel.add(grid[j][i]);
            }
        }

        drawFlorests(grid, m.floresta);
        drawHouses(grid, m.habitacoes);
        drawFuelStations(grid, m.postosCombustivel);
        drawWaterSources(grid, m.postosAgua);
    }

    private static void drawFlorests(JLabel[][] mapGrid, List<Posicao> floresta){
        for(Posicao p : floresta){
            System.out.println(p);
            JLabel gridCell =  mapGrid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setBackground(Color.green);
            gridCell.setIcon(getScaledImage((new ImageIcon("imgs/arvore.png")),794/m.size,642/m.size));
        }
    }

    private static void drawHouses(JLabel[][] mapGrid, List<Posicao> casas){
        for(Posicao p : casas){
            System.out.println(p);
            JLabel gridCell =  mapGrid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setBackground(Color.green);
            gridCell.setIcon(getScaledImage((new ImageIcon("imgs/casa.png")),794/m.size,642/m.size));
        }
    }

    private static void drawFuelStations(JLabel[][] mapGrid, List<Posicao> postosAbastecimentoCombustivel){
        for(Posicao p : postosAbastecimentoCombustivel){
            System.out.println(p);
            JLabel gridCell =  mapGrid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setBackground(Color.green);
            gridCell.setIcon(getScaledImage((new ImageIcon("imgs/gota.png")),794/m.size,642/m.size));
        }
    }

    private static void drawWaterSources(JLabel[][] mapGrid, List<Posicao> postosAbastecimentoAgua){
        for(Posicao p : postosAbastecimentoAgua){
            System.out.println(p);
            JLabel gridCell =  mapGrid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setBackground(Color.green);
            gridCell.setIcon(getScaledImage((new ImageIcon("imgs/agua.png")),794/m.size,642/m.size));
        }
    }

    /**
     * Sets a given cell of the GUI grid
     * @param p cell of the worldMap being processed
     * @param gridCell cell of the grid being processed
     */
    private static void setCell(AgenteCentral a,  Mapa m, Posicao p, JLabel gridCell) {
        if (p != null) {

            /*if (a.verificaAgente(p)) {
                gridCell.setBackground(Color.pink);

            }*/


            if (m.onFire(p)) {
                gridCell.setBackground(Color.orange);
                gridCell.setIcon(getScaledImage((new ImageIcon("imgs/fire.png")),794/m.size,642/m.size));
            }

            if (m.hab(p)) {
                gridCell.setBackground(Color.gray);
                gridCell.setIcon(getScaledImage((new ImageIcon("imgs/casa.png")),794/m.size,642/m.size));

            }

            if (m.postoA(p)) {
                gridCell.setBackground(Color.cyan);
                gridCell.setIcon(getScaledImage((new ImageIcon("imgs/agua.png")),794/m.size,642/m.size));

            }

            if (m.postoC(p)) {
                gridCell.setBackground(Color.gray);
                gridCell.setIcon(getScaledImage((new ImageIcon("imgs/gota.png")),794/m.size,642/m.size));;

            }
            if (m.arvore(p)) {
                gridCell.setBackground(Color.green);
                gridCell.setIcon(getScaledImage((new ImageIcon("imgs/arvore.png")),794/m.size,642/m.size));

            }
        }
        else {
            gridCell.setBackground(null);
            gridCell.setIcon(null);
            gridCell.setText("");
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

    private static ImageIcon getScaledImage(ImageIcon srcImg, int w, int h){
        Image image = srcImg.getImage(); // transform it
        Image newimg = image.getScaledInstance(w, h,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }

    /**
     * Called on tick to fill the grid with the updated positions of the objects
     */
    public static void fillGrid(Mapa m) {
        for (int i = 0; i < m.size; i++){
            for (int j = 0; j <m.size; j++){
                grid[j][i].setOpaque(true);
                setCell(agenteCentral, m,agenteCentral.getPosicao(j,i), grid[j][i]);
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
