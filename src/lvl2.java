import ea.edu.Figur;
import ea.edu.Rechteck;

public class Lvl2{
    public static enemy[] enemies;
    public Rechteck[] walls;
    public Rechteck[] boundry;

    public Lvl2(){

        //building a Room
        walls = new Rechteck[22];
        walls[0] = new Rechteck(48,.2);
        walls[0].setzeMittelpunkt(0, 9);
        walls[1] = new Rechteck(5,.2);
        walls[1].setzeMittelpunkt(-19,-1);
        walls[2] = new Rechteck(.2,15);
        walls[2].setzeMittelpunkt(-16.5,-8.75);
        walls[3] = new Rechteck(10,.2);
        walls[3].setzeMittelpunkt(10.5,-3);
        walls[4] = new Rechteck(.2, 10);
        walls[4].setzeMittelpunkt(15.5,-8);
        walls[5] = new Rechteck(.2, 1.5);
        walls[5].setzeMittelpunkt(5.5,-3.75);
        walls[6] = new Rechteck(.2, 1);
        walls[6].setzeMittelpunkt(5.5,-11);
        walls[7] = new Rechteck(7, .2);
        walls[7].setzeMittelpunkt(19,-1);
        walls[8] = new Rechteck(.2, 10);
        walls[8].setzeMittelpunkt(15.75,-6);
        walls[9] = new Rechteck(8, .2);
        walls[9].setzeMittelpunkt(-3.4,.75);
        walls[10] = new Rechteck(.2, 7);
        walls[10].setzeMittelpunkt(.5,-2.75);
        walls[11] = new Rechteck(.2, 7);
        walls[11].setzeMittelpunkt(-7.25,-2.75);
        walls[12] = new Rechteck(1.5, .2);
        walls[12].setzeMittelpunkt(-6.5,-6.25);
        walls[13] = new Rechteck(1.5, .2);
        walls[13].setzeMittelpunkt(-.25,-6.25);
        walls[14] = new Rechteck(.2, 8);
        walls[14].setzeMittelpunkt(-14,-8);
        walls[15] = new Rechteck(3, .2);
        walls[15].setzeMittelpunkt(-15.5,-4);
        walls[16] = new Rechteck(5, .2);
        walls[16].setzeMittelpunkt(-10,8);
        walls[17] = new Rechteck(5, .2);
        walls[17].setzeMittelpunkt(-10,7);
        walls[18] = new Rechteck(5, .2);
        walls[18].setzeMittelpunkt(-10,1);
        walls[19] = new Rechteck(5, .2);
        walls[19].setzeMittelpunkt(-10,0);
        walls[20] = new Rechteck(.2, .75);
        walls[20].setzeMittelpunkt(-12.25,.5);
        walls[21] = new Rechteck(.2, .75);
        walls[21].setzeMittelpunkt(-12.25,7.5);



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
        enemies[0].skaliere(1.7);
        enemies[0].setzeMittelpunkt(-3.25,-3.5);
        enemies[1] = new enemy(10);
        enemies[1].skaliere(1.7);
        enemies[1].setzeMittelpunkt(18,3);
        enemies[2] = new enemy(10);
        enemies[2].skaliere(1.7);
        enemies[2].setzeMittelpunkt(11,-10);

        for (Rechteck bound: boundry) {
            bound.setzeEbenenposition(-1);
            bound.macheStatisch();

        }
        for (Rechteck wall : walls) {
            wall.setzeEbenenposition(-1);
            wall.macheStatisch();
            //wall.setzeSichtbar(false);

        }

        Figur background = new Figur("background", "rsc/lvl2.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(5);
        background.setzeMittelpunkt(0,0);
    }

}
