import ea.edu.Figur;
import ea.edu.Rechteck;

public class Lvl1{
    public static enemy[] enemies;
    public Rechteck[] walls;
    public Rechteck[] boundry;

    public Lvl1(){

        //building a Room
        walls = new Rechteck[15];
        walls[0] = new Rechteck(.2,25);
        walls[0].setzeMittelpunkt(-18.25, 0);
        walls[1] = new Rechteck(6,.2);
        walls[1].setzeMittelpunkt(8.25,-7.75);
        walls[2] = new Rechteck(30,.2);
        walls[2].setzeMittelpunkt(-5,11);
        walls[3] = new Rechteck(20,.2);
        walls[3].setzeMittelpunkt(15.2,10.1);
        walls[4] = new Rechteck(.2, 3);
        walls[4].setzeMittelpunkt(5.25,4.75);
        walls[5] = new Rechteck(.2, 15);
        walls[5].setzeMittelpunkt(10.75,-2);
        walls[6] = new Rechteck(9, .2);
        walls[6].setzeMittelpunkt(9.75,3.1);
        walls[7] = new Rechteck(20, .2);
        walls[7].setzeMittelpunkt(15.25,6.25);
        walls[8] = new Rechteck(20, .2);
        walls[8].setzeMittelpunkt(-3,-9.25);
        walls[9] = new Rechteck(.2, 3);
        walls[9].setzeMittelpunkt(-12.9,-10.75);
        walls[10] = new Rechteck(.2, 3);
        walls[10].setzeMittelpunkt(-17,-10.75);
        walls[11] = new Rechteck(.2, 6);
        walls[11].setzeMittelpunkt(5.25,-2.25);
        walls[12] = new Rechteck(.2, 6);
        walls[12].setzeMittelpunkt(7.25,-2.25);
        walls[13] = new Rechteck(2, .2);
        walls[13].setzeMittelpunkt(6.25,.75);
        walls[14] = new Rechteck(2, .2);
        walls[14].setzeMittelpunkt(6.25,-5.25);

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

        enemies = new enemy[3];
        enemies[0] = new enemy(10);
        enemies[0].setzeMittelpunkt(9,-2);
        enemies[1] = new enemy(10);
        enemies[1].setzeMittelpunkt(15,8);
        enemies[2] = new enemy(10);
        enemies[2].setzeMittelpunkt(-5,7);

        for (Rechteck bound: boundry) {
            bound.setzeEbenenposition(-1);
            bound.macheStatisch();

        }
        for (Rechteck wall : walls) {
            wall.setzeEbenenposition(-1);
            wall.macheStatisch();
            wall.setzeSichtbar(false);

        }

        Figur background = new Figur("background", "rsc/tutorialgif.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(5);
        background.setzeMittelpunkt(0,0);
    }

}
