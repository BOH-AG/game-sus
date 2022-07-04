import ea.edu.Figur;
import ea.edu.Rechteck;

public class Lvl1{
    public static enemy[] enemies;
    public Rechteck[] walls;
    public Rechteck[] boundry;

    public Lvl1(){

        //building a Room
        walls = new Rechteck[16];
        walls[0] = new Rechteck(.2,25);
        walls[0].setzeMittelpunkt(-19, 0);
        walls[1] = new Rechteck(48,.2);
        walls[1].setzeMittelpunkt(0,10);
        walls[2] = new Rechteck(15,.2);
        walls[2].setzeMittelpunkt(19,8.75);
        walls[3] = new Rechteck(15,.2);
        walls[3].setzeMittelpunkt(19,-1);
        walls[4] = new Rechteck(.2, 10);
        walls[4].setzeMittelpunkt(11.5,-6);
        walls[5] = new Rechteck(.2, 8);
        walls[5].setzeMittelpunkt(-1,-6);
        walls[6] = new Rechteck(8, .2);
        walls[6].setzeMittelpunkt(3,-2);
        walls[7] = new Rechteck(25, .2);
        walls[7].setzeMittelpunkt(5.5,-8.25);
        walls[8] = new Rechteck(.2, 5);
        walls[8].setzeMittelpunkt(-7,-11);
        walls[9] = new Rechteck(.2, 5);
        walls[9].setzeMittelpunkt(-17,-11);
        walls[10] = new Rechteck(3, .2);
        walls[10].setzeMittelpunkt(-18.25,-8.25);
        walls[11] = new Rechteck(5, .2);
        walls[11].setzeMittelpunkt(4.5,-4);
        walls[12] = new Rechteck(.2, 5);
        walls[12].setzeMittelpunkt(2,-6.5);
        walls[13] = new Rechteck(.2, 2);
        walls[13].setzeMittelpunkt(7,-3);
        walls[14] = new Rechteck(25, .2);
        walls[14].setzeMittelpunkt(-8,7.5);
        walls[15] = new Rechteck(.2, 4);
        walls[15].setzeMittelpunkt(4,9.5);



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

        enemies = new enemy[2];
        enemies[0] = new enemy(10);
        enemies[0].skaliere(2);
        enemies[0].setzeMittelpunkt(4.5,-5.5);
        enemies[1] = new enemy(10);
        enemies[1].skaliere(2);
        enemies[1].setzeMittelpunkt(18,3);


        for (Rechteck bound: boundry) {
            bound.setzeEbenenposition(-1);
            bound.macheStatisch();

        }
        for (Rechteck wall : walls) {
            wall.setzeEbenenposition(-1);
            wall.macheStatisch();
            //wall.setzeSichtbar(false);

        }

        Figur background = new Figur("background", "rsc/lobby.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(5);
        background.setzeMittelpunkt(0,0);
    }

}
