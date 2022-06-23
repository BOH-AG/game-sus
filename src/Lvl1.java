import ea.edu.Figur;
import ea.edu.Rechteck;

public class Lvl1{

    public Rechteck[] walls;
    public Rechteck[] boundry;

    public Lvl1(){

        //building a Room
        walls = new Rechteck[8];
        walls[0] = new Rechteck(0.2,25);//
        walls[0].setzeMittelpunkt(-18.25, 0);
        walls[1] = new Rechteck(0.2,3);
        walls[1].setzeMittelpunkt(10,-7);
        walls[2] = new Rechteck(30,0.2);//
        walls[2].setzeMittelpunkt(-5,11);
        walls[3] = new Rechteck(20,0.2);//
        walls[3].setzeMittelpunkt(15.2,10.1);
        walls[4] = new Rechteck(0.2, 10);
        walls[4].setzeMittelpunkt(0,-0.5);
        walls[5] = new Rechteck(0.2, 15);//
        walls[5].setzeMittelpunkt(10.75,-2);
        walls[6] = new Rechteck(9, 0.2);//
        walls[6].setzeMittelpunkt(9.75,3.1);
        walls[7] = new Rechteck(20, 0.2);//
        walls[7].setzeMittelpunkt(15.25,6.25);

        //Setting map boundaries
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

        Figur background = new Figur("background", "rsc/tutorialgif.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(5);
        background.setzeMittelpunkt(0,0);
    }

}
