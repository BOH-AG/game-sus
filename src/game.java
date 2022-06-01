import ea.edu.Spiel;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.edu.event.MausKlickReagierbar;

import java.util.Arrays;


public class game extends Spiel {
    public MenuScene menuScene;

    public game(int width, int height) {
        super();
        setzeFensterGroesse(width, height);
        //setzeRasterSichtbar(false);
        gameScene();
        benenneAktiveSzene("gameScene");
        MausKlickReagierbar dieSendungMitDer;
        BildAktualisierungReagierbar dasBild;
        registriereMausKlickReagierbar(
                dieSendungMitDer = new MausKlickReagierbar() {
                    @Override
                    public void klickReagieren(double v, double v1) {
                        shoot(v, v1);
                        lvl.walls[5].setzeFarbe("rot");
                    }

                    @Override
                    public void klickLosgelassenReagieren(double x, double y) {
                        System.out.println("stopped clicking");

                    }
                }
        );
    }

    tracer traced;
    player p1;
    enemy[] enemies;
    int enemyhealth;
    int kills;
    Lvl1 lvl;

    void gameScene() {
        p1 = new player();
        kills = 0;
        enemyhealth = 1;
        p1.setzeEbenenposition(2);
        enemies = new enemy[3];
        for (int z=0; z < enemies.length; z++) {
            enemies[z] = new enemy(enemyhealth);
        }
        lvl = new Lvl1();
        p1.macheDynamisch();
        //menuScene = new MenuScene(this);
    }


    void menuScene() {


    }

    private void shoot(double x, double y) {
        for (int z=0; z < enemies.length; z++) {
            if (enemies[z].beinhaltetPunkt(x,y)) {
                enemies[z].takeDamage(1);
                if (enemies[z].dead) {
                    enemies[z] = new enemy(enemyhealth);
                    kills = kills + 1;
                    System.out.println("kills: " + kills);
                }
            }
        }



        traced = new tracer(
                x+0.5,
                y+0.5,
                p1.nenneMittelpunktX(),
                p1.nenneMittelpunktY()
        );
    }

    public void alexaDisableGravity() {
        setzeSchwerkraft(0);
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

