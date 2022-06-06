import ea.FrameUpdateListener;
import ea.edu.Figur;
import ea.edu.Spiel;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.edu.event.MausKlickReagierbar;
import ea.edu.event.TastenReagierbar;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class game extends Spiel {

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
        titleScreen();
    }

     void gameScene() {
        if (Arrays.stream(nenneSzenennamen()).anyMatch("gameScene"::equals)) { //check if gameScene exists
            setzeAktiveSzene("gameScene");
        } else {
            erzeugeNeueSzene();
            benenneAktiveSzene("gameScene");
            MausKlickReagierbar dieSendungMitDer;
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
                    });
            BildAktualisierungReagierbar frameUpdate;
            registriereBildAktualisierungReagierbar(
                    frameUpdate = new BildAktualisierungReagierbar() {
                        @Override
                        public void bildAktualisierungReagieren(double v) { // tick() but for cool kids B)
                            if (shooting) {
                                shoot();
                            }
                        }
                    });
            TastenReagierbar cherryMxBrown;
            registriereTastenReagierbar(
                    cherryMxBrown = new TastenReagierbar() {
                        @Override
                        public void tasteReagieren(int key) {
                            if (key == KeyEvent.VK_M) {
                                menuScene();
                            }
                        }
                    });
            setzeSchwerkraft(0);
            p1 = new player();
            kills = 0;
            enemyhealth = 1;
            p1.setzeEbenenposition(2);
            enemies = new enemy[3];
            for (int z = 0; z < enemies.length; z++) {
                enemies[z] = new enemy(enemyhealth);
            }
            lvl = new Lvl1();
            p1.macheDynamisch();
        }
    }

    void menuScene(){
        //ersellt eine neue szene und ruft die menü szene auf
        if (Arrays.stream(nenneSzenennamen()).anyMatch("menuScene"::equals)) {
            setzeAktiveSzene("menuScene");
        } else {
            erzeugeNeueSzene();
            benenneAktiveSzene("menuScene");
            TastenReagierbar cherryMxBrown;
            registriereTastenReagierbar(
                    cherryMxBrown = new TastenReagierbar() {
                        @Override
                        public void tasteReagieren(int key) {
                            if (key == KeyEvent.VK_M) {
                                gameScene();
                            }
                        }
                    }
            );
            MenuScene ms1 = new MenuScene();
        }
    }

    void titleScreen(){
        //benennt die aktive szene und ruft den titleScreen auf
        if (Arrays.stream(nenneSzenennamen()).anyMatch("title"::equals)) {
            setzeAktiveSzene("title");
        } else {
            benenneAktiveSzene("title");
            TitleScreen ts = new TitleScreen();
            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            if (ts.playButton.beinhaltetPunkt(x, y)) {
                                gameScene();
                            }if(ts.bohLogo.beinhaltetPunkt(x, y)){

                                try {
                                    java.awt.Desktop.getDesktop().browse(new URI("http://www.theboh.de"));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (URISyntaxException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                        }

                    });
        }
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
            if (t1.touching(enemies[z])) {
                enemies[z].takeDamage(1);
                if (enemies[z].dead) {
                    enemies[z] = new enemy(enemyhealth);
                    kills = kills + 1;
                    System.out.println("kills: " + kills);
                }

            }
        }

    }

}
