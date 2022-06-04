import ea.FrameUpdateListener;
import ea.edu.Spiel;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.edu.event.MausKlickReagierbar;



public class game extends Spiel {
    public MenuScene menuScene;

    public game(int width, int height) {
        super();
        setzeFensterGroesse(width, height);

        //setzeRasterSichtbar(false);

        MausKlickReagierbar dieSendungMitDer;
        BildAktualisierungReagierbar dasBild;
        registriereMausKlickReagierbar(
                dieSendungMitDer = new MausKlickReagierbar() {
                    @Override
                    public void klickReagieren(double v, double v1) {
                        shooting = true;
                    }

                    @Override
                    public void klickLosgelassenReagieren(double x, double y) {
                        shooting = false;
                    }
                }
        );
        registriereBildAktualisierungReagierbar( // tick() but for cool kids   B)
                dasBild = new BildAktualisierungReagierbar() {
                    @Override
                    public void bildAktualisierungReagieren(double v) {
                        if (shooting) {
                            shoot();
                        }
                    }
                }
        );
        Scene();
    }

    tracer traced;
    player p1;
    enemy[] enemies;
    int enemyhealth;
    int kills;
    Lvl1 lvl;
    boolean shooting;

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


    void Scene() {
        gameScene();
        benenneAktiveSzene("gameScene");




    }

    private void shoot() {
        double x = nenneMausPositionX();
        double y = nenneMausPositionY();
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
