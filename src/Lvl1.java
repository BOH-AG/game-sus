import ea.edu.Rechteck;
import ea.edu.Spiel;

public class Lvl1{

    Lvl1(){

        Rechteck r1 = new Rechteck(0.5, 3);
        Rechteck r2 = new Rechteck(0.5, 3);
        Rechteck r3 = new Rechteck(5, 0.5);
        Rechteck r4 = new Rechteck(5, 0.5);
        Rechteck r5 = new Rechteck(0.5, 10);
        Rechteck r6 = new Rechteck(0.5, 15);
        Rechteck r7 = new Rechteck(9, 0.5);
        Rechteck r8 = new Rechteck(0.5, 5.5);

        r8.verschieben(4.25, 4.75 );
        r7.verschieben(0, 2);
        r6.verschieben(7.5, 0);
        r5.verschieben(-4.5, -2.75);
        r4.verschieben(5.25,-7.5);
        r3.verschieben(-2.25, -7.5);
        r2.verschieben(3,-9);
        r1.verschieben(0, -9);
    }

}
