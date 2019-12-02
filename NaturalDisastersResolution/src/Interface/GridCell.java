import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GridCell {

    static final int GRASS = 0;
    static final int HOUSE = 1;
    static final int FOREST = 2;
    static final int FUEL_STATION = 3;
    static final int WATER_SOURCE = 4;

    Posicao p = new Posicao(-1,-1);
    JLabel gridCell;
    int mapSize;
    int tipo; // 0 erva, 1 casa, 2 floresta, 3 bomba de combustivel, 4 fonte de agua
    int state; //0 - normal, 1 - a arder, 2 - queimada
    List<String> vehicles;



    GridCell(int mapSize){
        this.gridCell = new JLabel();
        this.gridCell.setHorizontalAlignment(SwingConstants.CENTER);
        this.gridCell.setVerticalAlignment(SwingConstants.CENTER);
        this.gridCell.setOpaque(true);

        this.mapSize = mapSize;
        this.vehicles = new ArrayList<>();

        this.tipo = GRASS; // default erva
        this.state = 0;

        this.setImage();
    }

    void setText(String s){
        this.gridCell.setText(s);
    }


    void setImage(){
        String icon = ImageManager.whichImage(this);
        Icon img = scaleImage((new ImageIcon(icon)),GUIConfig.MAP_GRID_WIDTH/mapSize,GUIConfig.MAP_GRID_HEIGHT/mapSize);
        this.gridCell.setIcon(img);
    }

    private static ImageIcon scaleImage(ImageIcon srcImg, int w, int h){
        Image image = srcImg.getImage(); // transform it
        Image newimg = image.getScaledInstance(w, h,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }


    public void addAgent(String tipo) {
        System.out.println("A adicionar " + tipo +" de " + p.toString());
        this.vehicles.add(tipo);
    }

    public void removeAgent(String tipo) {
        System.out.println("A remover " + tipo +" de " + p.toString());
        this.vehicles.remove(tipo);
    }

    public void setOnFireState(){
        this.state = 1;
    }

    public void setBurntState() {
        this.state = 2;
    }

    public void setType(int objectType) {
        this.tipo = objectType;
    }
}
