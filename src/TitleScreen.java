import ea.edu.Figur;


public class TitleScreen {

    public Figur playButton;
    public Figur bohLogo;

    TitleScreen(){

        /*Rechteck button[] = new Rechteck[2];
        button[0] = new Rechteck(6, 1.5);
        button[0].setzeMittelpunkt(0, 0);
        button[1] = new Rechteck(6, 1.5);
        button[1].setzeMittelpunkt(0, 2);*/
        playButton = new Figur("button", "rsc/play.gif");
        playButton.setzeMittelpunkt(0, -4.3);

        bohLogo = new Figur("background", "rsc/b-o-h.gif");
        Figur backgroundGif = new Figur("background", "rsc/hi.gif");
        bohLogo.setzeEbenenposition(-1);
        bohLogo.skaliere(0.2);
        bohLogo.setzeMittelpunkt(0,7.5);
        backgroundGif.setzeEbenenposition(-2);
        backgroundGif.skaliere(2.5);
        backgroundGif.setzeMittelpunkt(0,0);

    }

}
