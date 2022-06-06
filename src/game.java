import ea.FrameUpdateListener;
import ea.edu.Spiel;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.edu.event.MausKlickReagierbar;
import ea.edu.event.TastenReagierbar;

import java.awt.event.KeyEvent;

public class game extends Spiel implements TastenReagierbar {

    public int timer;

    public MenuScene menuScene;
    tracer t1;
    player p1;
    enemy[] enemies;
    int enemyhealth;
    int kills;
    Lvl1 lvl;
    boolean shooting;

    public game(int width, int height) {
        super();
        setzeFensterGroesse(width, height);

        //setzeRasterSichtbar(false);
        setzeSchwerkraft(0);
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
        registriereBildAktualisierungReagierbar(
                dasBild = new BildAktualisierungReagierbar() {
                    @Override
                    public void bildAktualisierungReagieren(double v) { // tick() but for cool kids B)

                        timer++;
                        if (shooting) {
                            shoot();

                        }



                    }
                }
        );
    }

     void gameScene() {
        erzeugeNeueSzene();
        benenneAktiveSzene("gameScene");
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

    void menuScene(){
        //ersellt eine neue szene und ruft die menü szene auf
        erzeugeNeueSzene();
        benenneAktiveSzene("menuScene");
        MenuScene ms1 = new MenuScene();
    }

    void TitleScreen(){
        //benennt die aktive szene und ruft den titleScreen auf
        benenneAktiveSzene("Title");
        TitleScreen ts1 = new TitleScreen();

    }

    private void shoot() {
        double x = nenneMausPositionX();
        double y = nenneMausPositionY();

        t1 = new tracer(
                x+0.5,
                y+0.5,
                p1.nenneMittelpunktX(),
                p1.nenneMittelpunktY()
        );

        for (int z=0; z < enemies.length; z++) {
            if (t1.berührt(enemies[z])) {
                enemies[z].takeDamage(1);
                if (enemies[z].dead) {
                    enemies[z] = new enemy(enemyhealth);
                    kills = kills + 1;
                    System.out.println("kills: " + kills);
                }

            }
        }
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    //tasten
    public void tasteReagieren(int key) {
        //play taste
        if (key== KeyEvent.VK_ENTER){
            gameScene();
        }
        //menu taste
        if (key==KeyEvent.VK_M){
            menuScene();
        }
    }


}
