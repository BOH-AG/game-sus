import ea.edu.Figur;
import ea.edu.Rechteck;
import ea.edu.Spiel;

public class Lvl1{

    public Rechteck[] walls;
    public Rechteck[] boundry;

    public Lvl1(){


        walls = new Rechteck[8];
        walls[0] = new Rechteck(0.5,3);
        walls[0].setzeMittelpunkt(5, -7);
        walls[1] = new Rechteck(0.5,3);
        walls[1].setzeMittelpunkt(10,-7);
        walls[2] = new Rechteck(5,0.5);
        walls[2].setzeMittelpunkt(2.25,-5.75);
        walls[3] = new Rechteck(5,0.5);
        walls[3].setzeMittelpunkt(12.75,-5.75);
        walls[4] = new Rechteck(0.5, 10);
        walls[4].setzeMittelpunkt(0,-0.5);
        walls[5] = new Rechteck(0.5, 15);
        walls[5].setzeMittelpunkt(15,2);
        walls[6] = new Rechteck(9, 0.5);
        walls[6].setzeMittelpunkt(4.5,4.25);
        walls[7] = new Rechteck(0.5, 5.5);
        walls[7].setzeMittelpunkt(8.75,7.25);

        boundry = new Rechteck[4];
        boundry[0] = new Rechteck(42,1);
        boundry[0].setzeMittelpunkt(0,12);
        boundry[1] = new Rechteck(42,1);
        boundry[1].setzeMittelpunkt(0,-12);
        boundry[2] = new Rechteck(1,23);
        boundry[2].setzeMittelpunkt(21.5,0);
        boundry[3] = new Rechteck(1,23);
        boundry[3].setzeMittelpunkt(-21.5,0);

        for (Rechteck bound: boundry) {
            bound.setzeEbenenposition(-1);
            bound.macheStatisch();

        }
        for (Rechteck wall : walls) {
            wall.setzeEbenenposition(-1);
            wall.macheStatisch();

        }






        Figur background = new Figur("background", "rsc/cat.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(2);
        background.setzeMittelpunkt(0,0);
    }

}
