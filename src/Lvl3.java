import ea.edu.Figur;
import ea.edu.Rechteck;

public class Lvl3{
    public static enemy[] enemies;
    public Rechteck[] walls;
    public Rechteck[] boundry;

    public Lvl3(){

        //building a Room
        walls = new Rechteck[18];
        walls[0] = new Rechteck(14,.2);
        walls[0].setzeMittelpunkt(4.5, 9);
        walls[1] = new Rechteck(7,.2);
        walls[1].setzeMittelpunkt(0,6);
        walls[2] = new Rechteck(7,.2);
        walls[2].setzeMittelpunkt(8,5.5);
        walls[3] = new Rechteck(.2,4);
        walls[3].setzeMittelpunkt(11.5,7);
        walls[4] = new Rechteck(.2, 3);
        walls[4].setzeMittelpunkt(-3,7.5);
        walls[5] = new Rechteck(.2, 7);
        walls[5].setzeMittelpunkt(-15,-5);
        walls[6] = new Rechteck(.2, 7);
        walls[6].setzeMittelpunkt(5.5,-11);
        walls[7] = new Rechteck(.2, 7);
        walls[7].setzeMittelpunkt(-11.5,-5);
        walls[8] = new Rechteck(3, .2);
        walls[8].setzeMittelpunkt(-13,-8);
        walls[9] = new Rechteck(3, .2);
        walls[9].setzeMittelpunkt(-13,-1.5);
        walls[10] = new Rechteck(.2, 8);
        walls[10].setzeMittelpunkt(13,-2.5);
        walls[11] = new Rechteck(.2, 8);
        walls[11].setzeMittelpunkt(15.5,-2.5);
        walls[12] = new Rechteck(2.5, .2);
        walls[12].setzeMittelpunkt(14,1.5);
        walls[13] = new Rechteck(2.5, .2);
        walls[13].setzeMittelpunkt(14,-6);
        walls[14] = new Rechteck(6, .2);
        walls[14].setzeMittelpunkt(7.5,-10.5);
        walls[15] = new Rechteck(6, .2);
        walls[15].setzeMittelpunkt(7,-8);
        walls[16] = new Rechteck(.2, 2.5);
        walls[16].setzeMittelpunkt(4,-9);
        walls[17] = new Rechteck(.2, 2.5);
        walls[17].setzeMittelpunkt(9.5,-9);



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

        enemies = new enemy[8];
        enemies[0] = new enemy(10);
        enemies[0].setzeMittelpunkt(-10,-6);
        enemies[1] = new enemy(10);
        enemies[1].setzeMittelpunkt(3,-10);
        enemies[2] = new enemy(10);
        enemies[2].setzeMittelpunkt(11.5,-9);
        enemies[3] = new enemy(10);
        enemies[3].setzeMittelpunkt(11,-2.5);
        enemies[4] = new enemy(10);
        enemies[4].setzeMittelpunkt(13,7);
        enemies[5] = new enemy(10);
        enemies[5].setzeMittelpunkt(5,11);
        enemies[6] = new enemy(10);
        enemies[6].setzeMittelpunkt(17,-2);
        enemies[7] = new enemy(10);
        enemies[7].setzeMittelpunkt(4,6);

        for(enemy e : enemies){
            e.skaliere(1.7);
        }


        for (Rechteck bound: boundry) {
            bound.setzeEbenenposition(-1);
            bound.macheStatisch();
            bound.setzeSichtbar(false);

        }
        for (Rechteck wall : walls) {
            wall.setzeEbenenposition(-1);
            wall.macheStatisch();
            //wall.setzeSichtbar(false);

        }


        Figur background = new Figur("background", "rsc/lvl3.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(5);
        background.setzeMittelpunkt(0,0);
    }

}
