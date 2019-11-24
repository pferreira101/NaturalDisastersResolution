import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;

public class GUI {

    private static Mapa mapa;
    private JFrame mainFrame;
    private static JTextArea textArea;
    private JPanel panel_3;
    private JPanel panel_4;

    MapGrid mapGrid;



    public GUI(Mapa mapa) {
        this.mapa = mapa;

        this.mapGrid = new MapGrid(mapa);

        panel_3 = new JPanel();
        panel_3.setBounds(9, 660, 1095, 93);
        panel_4 = new JPanel();
        panel_4.setBounds(810, 10, 762, 642);

        frameInitialize(mapGrid.panel, panel_3, panel_4);
        captionInitialize(mapa, mapGrid.panel, panel_3);

        textArea = new JTextArea(10, 20);
        textArea.setEditable(false);

        scrollPaneInitialize(panel_4);
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



}
