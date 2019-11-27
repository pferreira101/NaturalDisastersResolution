import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Set;

public class GridCell {


    static final String ERVA = "imgs/grass.png";
    static final String CASA = "imgs/house2.png";
    static final String CASAQUEIMADA = "imgs/casa.png";
    static final String FLORESTA = "imgs/forest.png";
    static final String FLORESTAQUEIMADA = "imgs/burntForest.png";
    static final String AGUA = "imgs/waterSource.png" ;
    static final String COMBUSTIVEL = "imgs/gota.png";
    static final String FOGO = "imgs/fogo.png";
    static final String AERONAVE = "imgs/aeronave.png";
    static final String CAMIAO = "imgs/camiao.png";
    static final String DRONE = "imgs/drone.png";
    static final String FOGOAPAGADO = "imgs/fogoapagado.png";

    boolean burnt;
    JLabel gridCell;
    String baseImage; // serve para restaurar a imagem depois de passar por la alguma coisa
    int mapSize;


    GridCell(int mapSize){
        this.gridCell = new JLabel();
        this.gridCell.setHorizontalAlignment(SwingConstants.CENTER);
        this.gridCell.setVerticalAlignment(SwingConstants.CENTER);
        this.gridCell.setOpaque(true);
        this.burnt = false;
        this.mapSize = mapSize;
        this.setBaseImage(ERVA);
    }

    void setText(String s){
        this.gridCell.setText(s);
    }

    void setBaseImage(String icon){
        this.baseImage = icon;
        this.setImage(icon);
    }

    void setImage(String icon){
        Icon img = scaleImage((new ImageIcon(icon)),794/mapSize,642/mapSize);
        this.gridCell.setIcon(img);
    }

    void setFireImage() {
        this.burnt = true;
        this.setImage(FOGO);
    }


    void setBurntImage(){

        switch (this.baseImage){
            case FLORESTA:
                this.setBaseImage(FLORESTAQUEIMADA);
                System.out.println("#### a colocar floresta queimada");
                break;
            case CASA:
                this.setBaseImage(CASAQUEIMADA);
                break;
            case ERVA:
                this.setBaseImage(ERVA);
                break;
        }
    }


    void restoreImage(){
        this.setImage(baseImage);
    }

    private static ImageIcon scaleImage(ImageIcon srcImg, int w, int h){
        Image image = srcImg.getImage(); // transform it
        Image newimg = image.getScaledInstance(w, h,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        ImageIcon imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }


}
