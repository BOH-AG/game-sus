import ea.FrameUpdateListener;
import ea.edu.Spiel;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.edu.event.MausKlickReagierbar;



public class game extends Spiel implements FrameUpdateListener {
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


    @Override
    public void onFrameUpdate(float v) {

    }
}
