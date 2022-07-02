import ea.edu.Bild;
import ea.edu.Figur;


public class TitleScreen {

    public Figur playButton;
    public Figur settingsButton;
    public Figur bohLogo;
    public Bild background;

    TitleScreen(){

        playButton = new Figur("button", "rsc/play.gif");
        playButton.setzeMittelpunkt(0, 3);

        settingsButton = new Figur("button","rsc/settings.gif");
        settingsButton.setzeMittelpunkt(0, -1.3);

        bohLogo = new Figur("background", "rsc/b-o-h.gif");
        bohLogo.setzeEbenenposition(-1);
        bohLogo.skaliere(0.3);
        bohLogo.setzeMittelpunkt(0,7.5);

        background = new Bild(85.2, 24, "rsc/blurred-wskyline.png"); // 43.2
        background.setzeEbenenposition(-2);
        background.setzeMittelpunkt(21.3, 0);

    }

}
