import ea.edu.Figur;
import ea.edu.Rechteck;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TitleScreen {

    public Figur playButton;

    TitleScreen(){

        /*Rechteck button[] = new Rechteck[2];
        button[0] = new Rechteck(6, 1.5);
        button[0].setzeMittelpunkt(0, 0);
        button[1] = new Rechteck(6, 1.5);
        button[1].setzeMittelpunkt(0, 2);*/
        playButton = new Figur("button", "rsc/play.gif");
        playButton.setzeMittelpunkt(0, -4.3);

        Figur backgroundT = new Figur("background", "rsc/b-o-h.gif");
        Figur backgroundGif = new Figur("background", "rsc/hi.gif");
        backgroundT.setzeEbenenposition(-1);
        backgroundT.skaliere(0.25);
        backgroundT.setzeMittelpunkt(0,5);
        backgroundGif.setzeEbenenposition(-2);
        backgroundGif.skaliere(2.5);
        backgroundGif.setzeMittelpunkt(0,0);

    }

}
