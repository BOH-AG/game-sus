import ea.edu.Figur;
import ea.edu.Rechteck;
import ea.edu.Spiel;

public class Lvl1{

    public Lvl1(){

        Rechteck r1 = new Rechteck(0.5, 3);
        Rechteck r2 = new Rechteck(0.5, 3);
        Rechteck r3 = new Rechteck(5, 0.5);
        Rechteck r4 = new Rechteck(5, 0.5);
        Rechteck r5 = new Rechteck(0.5, 10);
        Rechteck r6 = new Rechteck(0.5, 15);
        Rechteck r7 = new Rechteck(9, 0.5);
        Rechteck r8 = new Rechteck(0.5, 5.5);

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


        r8.verschieben(4.25, 4.75 );
        r7.verschieben(0, 2);
        r6.verschieben(7.5, 0);
        r5.verschieben(-4.5, -2.75);
        r4.verschieben(5.25,-7.5);
        r3.verschieben(-2.25, -7.5);
        r2.verschieben(3,-9);
        r1.verschieben(0, -9);
        r8.macheStatisch();
        r7.macheStatisch();
        r6.macheStatisch();
        r5.macheStatisch();
        r4.macheStatisch();
        r3.macheStatisch();
        r2.macheStatisch();
        r1.macheStatisch();
        r8.setzeEbenenposition(-1);
        r7.setzeEbenenposition(-1);
        r6.setzeEbenenposition(-1);
        r5.setzeEbenenposition(-1);
        r4.setzeEbenenposition(-1);
        r3.setzeEbenenposition(-1);
        r2.setzeEbenenposition(-1);
        r1.setzeEbenenposition(-1);

        Figur background = new Figur("background", "rsc/cat.gif");
        background.setzeEbenenposition(-99);
        background.skaliere(2);
        background.setzeMittelpunkt(0,0);
    }

}
