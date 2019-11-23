import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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

    public static List<Posicao> getListProporcional(int mapSize, int numPostos) {
        Random rand = new Random();

        List<Posicao> res = new ArrayList<>();

        if(numPostos == 1){
            Posicao p = new Posicao(mapSize/2,mapSize/2);
            res.add(p);
        }
        else if(numPostos == 2){
            Posicao p1 = new Posicao(mapSize/4,mapSize/4);
            Posicao p2 = new Posicao(mapSize-(mapSize/4),mapSize-(mapSize/4));
            res.add(p1);
            res.add(p2);
        }
        else{
            int tamDivisaoSide = mapSize/2;

            int i=0;
            while(i<numPostos){
                int x_1q,y_1q,x_2q,y_2q,x_3q,y_3q,x_4q,y_4q;

                do {
                    x_1q = rand.nextInt(tamDivisaoSide);
                    y_1q = rand.nextInt(tamDivisaoSide);
                }while(res.contains(new Posicao(x_1q,y_1q)));
                res.add(new Posicao(x_1q,y_1q));
                i++;
                if(i==numPostos) break;

                do {
                    x_2q = rand.nextInt(mapSize-tamDivisaoSide)+tamDivisaoSide;
                    y_2q = rand.nextInt(tamDivisaoSide);
                }while(res.contains(new Posicao(x_2q,y_2q)));
                res.add(new Posicao(x_2q,y_2q));
                i++;
                if(i==numPostos) break;

                do {
                    x_3q = rand.nextInt(tamDivisaoSide);
                    y_3q = rand.nextInt(mapSize-tamDivisaoSide)+tamDivisaoSide;
                }while(res.contains(new Posicao(x_3q,y_3q)));
                res.add(new Posicao(x_3q,y_3q));
                i++;
                if(i==numPostos) break;

                do {
                    x_4q = rand.nextInt(mapSize-tamDivisaoSide)+tamDivisaoSide;
                    y_4q = rand.nextInt(mapSize-tamDivisaoSide)+tamDivisaoSide;
                }while(res.contains(new Posicao(x_4q,y_4q)));
                res.add(new Posicao(x_4q,y_4q));
                i++;
                if(i==numPostos) break;
            }
        }

        return res;
    }

    public static Posicao getRandLinePosition(Posicao pos, int mapSize) {
        Random rand = new Random();
        int bin = rand.nextInt(2);
        if(bin==0) {
            float posx = mapSize;
            if (pos.pos_x + 1 != mapSize)
                posx = pos.pos_x + 1;
            return new Posicao(posx, pos.pos_y);
        }else{
            float posy = mapSize;
            if (pos.pos_y + 1 != mapSize)
                posy = pos.pos_y + 1;
            return new Posicao(pos.pos_x, posy);
        }
    }

    public static Posicao getRandSidePosition(Posicao pos, int mapSize) {
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
