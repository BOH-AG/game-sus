import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.edu.Kreis;
import ea.edu.Spiel;
import ea.edu.event.MausKlickReagierbar;
import ea.event.MouseButton;
import ea.event.MouseClickListener;
import ea.Scene;



public class game extends Spiel {
    public MenuScene menuScene;

    public game(int width, int height) {
        super();
        setzeFensterGroesse(width, height);
        //setzeRasterSichtbar(false);
        gameScene();
        benenneAktiveSzene("gameScene");
        MausKlickReagierbar mouseClick;
        registriereMausKlickReagierbar(
                mouseClick = new MausKlickReagierbar() {
                    @Override
                    public void klickReagieren(double v, double v1) {
                        shoot(v, v1);
                        // player px = new player();
                        // px.setzeEbenenposition(2);
                        //enemy ex = new enemy();
                    }
                }
        );
    }

    player p1;
    enemy e1;

    void gameScene() {
        p1 = new player();
        p1.setzeEbenenposition(2);
        e1 = new enemy();
        Lvl1 lvl = new Lvl1();
        p1.macheAktiv();
        //menuScene = new MenuScene(this);
    }


    void menuScene() {


    }

    tracer traced;

    private void shoot(double x, double y) {
        traced = new tracer(
                x,
                y,
                p1.nenneMittelpunktX(),
                p1.nenneMittelpunktY()
        );
    }


    private void damage()

    {


    }

}














/*
public class game extends Scene implements MouseClickListener, FrameUpdateListener {
    public game() {
        p1 = new player();
        this.add(p1);
        p1.setCenter(0,0);
        Game.setDebug(true);
    }
    player p1;
    Polygon bruh;
    @Override
    public void onMouseDown(Vector v, MouseButton mb) {
        if (mb == MouseButton.LEFT) {
            bruh = new Polygon(
                    p1.getCenter(),
                    p1.getCenter().add(0, 0.05f),
                    v,
                    v.add(0, 0.05f)
            );
            bruh.setColor(Color.red);
            this.add(bruh);
        }

    }

    @Override
    public void onFrameUpdate(float v) {
        try {bruh.remove();}
        catch (Exception ignored) {}
    }
}
*/

