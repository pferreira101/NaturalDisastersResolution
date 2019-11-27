import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MapGrid {


    JPanel panel;
    GridCell[][] grid;
    Mapa mapa;

    MapGrid(Mapa mapa){
        this.mapa = mapa;
        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(this.mapa.size, this.mapa.size));
        this.panel.setBounds(GUIConfig.MAP_GRID_X_POS, GUIConfig.MAP_GRID_Y_POS, GUIConfig.MAP_GRID_WIDTH, GUIConfig.MAP_GRID_HEIGHT);
        this.grid = new GridCell[this.mapa.size][this.mapa.size];

        initializeMapGrid();
    }

    private void initializeMapGrid(){
        for (int i = 0; i < this.mapa.size; i++){
            for (int j = 0; j < this.mapa.size; j++){
                grid[i][j] = new GridCell(this.mapa.size);
                //grid[i][j].setText("(" + i +","+ j +")"); // descomentar esta linha para mostrar indice das células
                panel.add(grid[i][j].gridCell);
            }
        }

        drawMapObjects(GridCell.CASA, mapa.habitacoes);
        drawMapObjects(GridCell.FLORESTA, mapa.floresta);
        drawMapObjects(GridCell.COMBUSTIVEL, mapa.postosCombustivel);
        drawMapObjects(GridCell.AGUA, mapa.postosAgua);
    }

    private void drawMapObjects(String objectType, List<Posicao> objectPositions){
        for(Posicao p : objectPositions){
            GridCell gridCell =  this.grid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setText("");
            gridCell.setBaseImage(objectType);
        }
    }

    private void drawAgents(List<AgentStatus> agentStatus){
        String objectType = null;
        for(AgentStatus  as : agentStatus){
            if(as.ultimaPosicao != null){
                GridCell previousGridCell =  this.grid[(int)as.ultimaPosicao.pos_x][(int)as.ultimaPosicao.pos_y];
                previousGridCell.restoreImage();// colocar a imagem que la estava antes da passagem do agente
            }

            GridCell gridCell =  this.grid[(int)as.posAtual.pos_x][(int)as.posAtual.pos_y];
            gridCell.setText("");
            switch(as.tipo) {
                case 0: objectType = GridCell.AERONAVE;
                        break;
                case 1: objectType = GridCell.CAMIAO;
                    break;
                case 2: objectType = GridCell.DRONE;
                    break;   
            }
            
            gridCell.setImage(objectType);
        }
        
    }

    private void drawNewFires(List<Posicao> celulas) {
        for(Posicao p : celulas){
            GridCell gridCell =  this.grid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setFireImage();
        }
    }

    private void drawExtinguishedFires(List<Posicao> celulasApagadas) {
        for(Posicao p : celulasApagadas){
            GridCell gridCell =  this.grid[(int)p.pos_x][(int)p.pos_y];
            gridCell.setBurntImage();
        }
    }



    public void updateGrid(DeltaSimulationStatus stats) {
        drawNewFires(stats.novosIncendios);
        drawAgents(stats.estadoAgentes);
        drawExtinguishedFires(stats.celulasApagadas);
    }

}
