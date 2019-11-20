import java.io.Serializable;
import java.util.Random;

public class Posicao implements Serializable {
    float pos_x;
    float pos_y;


    Posicao(float x, float y){
        pos_x = x;
        pos_y = y;
    }


    public static Posicao getRandPosition(int mapSize) {
        Random rand = new Random();
        float x = rand.nextInt(mapSize);
        float y = rand.nextInt(mapSize);

        return new Posicao(x,y);
    }

    public static Posicao getRandomSidePosition(Posicao pos, int mapSize) {
        Random rand = new Random();
        float posx, posy;
        do{
            float x = rand.nextInt(3)-1;
            posx = pos.pos_x + x;
            //System.out.println("Calculos X: " + posx);
        } while(posx > mapSize || posx == -1);
        do{
            float y = rand.nextInt(3)-1;
            posy = pos.pos_y + y;
            //System.out.println("Calculos Y: " + posy);
        } while(posy > mapSize || posy == -1);
        return new Posicao(posx,posy);
    }

    public static int distanceBetween(Posicao p1, Posicao p2){
        return (int) Math.sqrt(((Math.pow((p1.pos_x - p2.pos_x), 2)) + (Math.pow((p1.pos_y - p2.pos_y), 2))));
    }

    @Override
    public boolean equals(Object p){
        if(p instanceof Posicao){
            Posicao pos = (Posicao) p;
            return this.pos_x == pos.pos_x && this.pos_y == pos.pos_y;
        }
        else return false;
    }

    @Override
    public String toString() {
        return "("+pos_x +"," + pos_y +")";
    }
}
