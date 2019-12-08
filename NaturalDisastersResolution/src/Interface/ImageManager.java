public class ImageManager {
    static final String IMG_DIR = "imgs/";

    static final String GRASS_DIR = "grass/";
    static final String HOUSE_DIR = "house/";
    static final String FOREST_DIR = "forest/";
    static final String FUEL_DIR = "fuelStation/";
    static final String WATER_DIR = "waterSource/";

    static final String IMG_FORMAT = ".png";

    static String whichImage(GridCell gridCell){
        String img = IMG_DIR;

        switch (gridCell.tipo){
            case 0: // erva
                img += GRASS_DIR;
                break;
            case 1: // casa
                img += HOUSE_DIR;
                break;
            case 2: // floresta
                img += FOREST_DIR;
                break;
            case 3: // combustivel
                img += FUEL_DIR;
                break;
            case 4: // agua
                img += WATER_DIR;
                break;
        }

        switch (gridCell.state){
            case 0: // normal
                img += "normal/normal";
                break;
            case 1: // a arder
                img += "burning/burning";
                break;
            case 2: // queimada
                img += "burnt/burnt";
                break;
        }

        switch (gridCell.tipo){
            case 0: // erva
                img += "Grass";
                break;
            case 1: // casa
                img += "House";
                break;
            case 2: // floresta
                img += "Forest";
                break;
            case 3: // combustivel
                img += "FuelStation";
                break;
            case 4: // agua
                img += "WaterSource";
                break;
        }

        if(gridCell.vehicles.contains("Drone")){
            img += "Drone";
        }

        if(gridCell.vehicles.contains("Firetruck")){
            img += "Firetruck";
        }

        if(gridCell.vehicles.contains("Plane")){
            img += "Plane";
        }

        img += IMG_FORMAT;

        return img;
    }



}
