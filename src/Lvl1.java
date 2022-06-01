import ea.edu.Figur;
import ea.edu.Rechteck;
import ea.edu.Spiel;

public class Lvl1{

    public Lvl1(){


        Rechteck[] rooms = new Rechteck[8];
        rooms[0] = new Rechteck(0.5,3);
        rooms[0].setzeMittelpunkt(5, -7);
        rooms[1] = new Rechteck(0.5,3);
        rooms[1].setzeMittelpunkt(10,-7);
        rooms[2] = new Rechteck(5,0.5);
        rooms[2].setzeMittelpunkt(2.25,-5.75);
        rooms[3] = new Rechteck(5,0.5);
        rooms[3].setzeMittelpunkt(12.75,-5.75);
        rooms[4] = new Rechteck(0.5, 10);
        rooms[4].setzeMittelpunkt(0,-0.5);
        rooms[5] = new Rechteck(0.5, 15);
        rooms[5].setzeMittelpunkt(15,2);
        rooms[6] = new Rechteck(9, 0.5);
        rooms[6].setzeMittelpunkt(4.5,4.25);
        rooms[7] = new Rechteck(0.5, 5.5);
        rooms[7].setzeMittelpunkt(8.75,7.25);


        Rechteck[] walls = new Rechteck[4];
        walls[0] = new Rechteck(42,1);
        walls[0].setzeMittelpunkt(0,12);
        walls[1] = new Rechteck(42,1);
        walls[1].setzeMittelpunkt(0,-12);
        walls[2] = new Rechteck(1,23);
        walls[2].setzeMittelpunkt(21.5,0);
        walls[3] = new Rechteck(1,23);
        walls[3].setzeMittelpunkt(-21.5,0);

        for (int x=0; x<4; x++) {
            walls[x].setzeEbenenposition(-1);
            walls[x].macheStatisch();

        }
        for (int i=0; i<rooms.length; i++) {
            rooms[i].setzeEbenenposition(-1);
            rooms[i].macheStatisch();

        }






        Figur background = new Figur("background", "rsc/cat.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(2);
        background.setzeMittelpunkt(0,0);
    }

}
