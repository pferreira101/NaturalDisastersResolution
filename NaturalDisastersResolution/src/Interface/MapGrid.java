import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class MapGrid {

    static final String CASA = "imgs/casa.png";
    static final String FLORESTA = "imgs/arvore.png";
    static final String AGUA = "imgs/agua.png" ;
    static final String COMBUSTIVEL = "imgs/gota.png";
    static final String FOGO = "imgs/fogo.png";
    static final String AERONAVE = "imgs/aeronave.png";
    static final String CAMIAO = "imgs/camiao.png";
    static final String DRONE = "imgs/drone.png";
    static final String FOGOAPAGADO = "imgs/fogoapagado.png";


    JPanel panel;
    JLabel[][] grid;
    Mapa mapa;

    MapGrid(Mapa mapa){
        this.mapa = mapa;
        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(this.mapa.size, this.mapa.size));
        this.panel.setBounds(GUIConfig.MAP_GRID_X_POS, GUIConfig.MAP_GRID_Y_POS, GUIConfig.MAP_GRID_WIDTH, GUIConfig.MAP_GRID_HEIGHT);
        this.grid = new JLabel[this.mapa.size][this.mapa.size];

        initializeMapGrid();
    }

    private void initializeMapGrid(){
        for (int i = 0; i < this.mapa.size; i++){
            for (int j = 0; j < this.mapa.size; j++){
                grid[i][j] = new JLabel();
                grid[i][j].setBorder(new LineBorder(Color.BLACK));
                grid[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                grid[i][j].setVerticalAlignment(SwingConstants.CENTER);
                grid[i][j].setOpaque(true);
                // grid[i][j].setText("(" + i +","+ j +")"); // descomentar esta linha para mostrar indice das células
                panel.add(grid[i][j]);
            }
        }

        drawMapObjects(CASA, mapa.habitacoes);
        drawMapObjects(FLORESTA, mapa.floresta);
        drawMapObjects(COMBUSTIVEL, mapa.postosCombustivel);
        drawMapObjects(AGUA, mapa.postosAgua);
    }

    private void drawMapObjects(String objectType, List<Posicao> objectPositions){
        for(Posicao p : objectPositions){
            JLabel gridCell =  this.grid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setText("");
            gridCell.setIcon(scaleImage((new ImageIcon(objectType)),794/mapa.size,642/mapa.size));
        }
    }

    private void drawAgents(List<AgentStatus> agentStatus){
        String objectType = null;
        for(AgentStatus  as : agentStatus){
            if(as.ultimaPosicao != null){
                JLabel previousGridCell =  this.grid[(int)as.ultimaPosicao.pos_x][(int)as.ultimaPosicao.pos_y];
                previousGridCell.setIcon(scaleImage((new ImageIcon(AERONAVE)),794/mapa.size,642/mapa.size));
            }

            JLabel gridCell =  this.grid[(int)as.posAtual.pos_x][(int)as.posAtual.pos_y];
            gridCell.setText("");
            switch(as.tipo) {
                case 0: objectType=AERONAVE;
                        break;
                case 1: objectType=CAMIAO;
                    break;
                case 2: objectType=DRONE;
                    break;   
            }
            
            gridCell.setIcon(scaleImage((new ImageIcon(objectType)),794/mapa.size,642/mapa.size));
        }
        
    }



    private static ImageIcon scaleImage(ImageIcon srcImg, int w, int h){
        Image image = srcImg.getImage(); // transform it
        Image newimg = image.getScaledInstance(w, h,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }

    public void updateGrid(DeltaSimulationStatus stats) {
        drawMapObjects(FOGO, stats.novosIncendios);
        drawAgents(stats.estadoAgentes);
        drawMapObjects(FOGOAPAGADO, stats.celulasApagadas);
    }

}
