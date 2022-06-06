import ea.edu.Figur;
import ea.edu.Rechteck;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TitleScreen {

    TitleScreen(){

        Rechteck button[] = new Rechteck[2];
        button[0] = new Rechteck(6, 1.5);
        button[0].setzeMittelpunkt(0, 0);
        button[1] = new Rechteck(6, 1.5);
        button[1].setzeMittelpunkt(0, 2);
        MenuItem b1 = new MenuItem(0, 2,"Play");
        MenuItem b2 = new MenuItem(0, 0,"Options");
        Figur backgroundT = new Figur("background", "rsc/b-o-h.gif");
        Figur backgroundGif = new Figur("background", "rsc/hi.gif");
        backgroundT.setzeEbenenposition(-98);
        backgroundT.skaliere(0.25);
        backgroundT.setzeMittelpunkt(0,5);
        backgroundGif.setzeEbenenposition(-99);
        backgroundGif.skaliere(2.5);
        backgroundGif.setzeMittelpunkt(0,0);

    }

}
