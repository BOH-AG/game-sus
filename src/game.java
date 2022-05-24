import ea.Vector;
import ea.edu.Spiel;
import ea.edu.event.MausKlickReagierbar;
import ea.event.MouseButton;
import ea.event.MouseClickListener;



public class game extends Spiel {


    public game(int width, int height) {
        super();
        setzeFensterGroesse(width, height);
        gameScene();
        benenneAktiveSzene("gameScene");
    }


    void gameScene() {
        player p1 = new player();
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

