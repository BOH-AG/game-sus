import ea.FrameUpdateListener;
import ea.edu.Bild;
import ea.edu.Figur;


public class TitleScreen {

    public Figur playButton;
    public Figur settingsButton;
    public Figur bohLogo;
    public Bild background;

    TitleScreen(){

        /*Rechteck button[] = new Rechteck[2];
        button[0] = new Rechteck(6, 1.5);
        button[0].setzeMittelpunkt(0, 0);
        button[1] = new Rechteck(6, 1.5);
        button[1].setzeMittelpunkt(0, 2);*/
        playButton = new Figur("button", "rsc/play.gif");
        playButton.setzeMittelpunkt(-17.1, 6);

        settingsButton = new Figur("button","rsc/play.gif");
        settingsButton.setzeMittelpunkt(-17.1, 3);

        bohLogo = new Figur("background", "rsc/b-o-h.gif");
        bohLogo.setzeEbenenposition(-1);
        bohLogo.skaliere(0.3);
        bohLogo.setzeMittelpunkt(-17.1,9.5);

        background = new Bild(85.2, 24, "rsc/blurred-wskyline.png"); // 43.2
        background.setzeEbenenposition(-2);
        background.setzeMittelpunkt(21.3, 0);

    }

}
