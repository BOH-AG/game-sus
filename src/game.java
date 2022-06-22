import audio.MusicAudio;
import audio.SfxAudio;
import ea.edu.Kreis;
import ea.edu.Spiel;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.edu.event.MausKlickReagierbar;
import ea.edu.event.TastenReagierbar;
import ea.edu.event.Ticker;

import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import menu.*;

public class game extends Spiel {

    public MenuScene menuScene;
    tracer t1;
    player p1;
    enemy[] enemies;
    int enemyhealth;
    int kills;
    Lvl1 lvl1;
    lvl2 lvl2;
    boolean shooting;

    FileInputStream fis;
    BufferedInputStream bis;

    public game(int width, int height) {
        super();
        setzeFensterGroesse(width, height);
        titleScreen();
        new MusicAudio("radiation storm", true);
    }

     void gameScene() {
        if (Arrays.asList(nenneSzenennamen()).contains("gameScene")) { //check if gameScene exists
            setzeAktiveSzene("gameScene");
        }
        else {
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
                            fireLatency = 0;
                        }
                    });
            BildAktualisierungReagierbar frameUpdate;
            registriereBildAktualisierungReagierbar(
                    frameUpdate = new BildAktualisierungReagierbar() {
                        @Override
                        public void bildAktualisierungReagieren(double v) { // tick() but for cool kids B)
                            if (shooting) {
                                shoot(p1.fireRate, p1.bulletSpread);
                            }
                        }
                    });
            TastenReagierbar cherryMxBrown;
            registriereTastenReagierbar(
                    cherryMxBrown = new TastenReagierbar() {
                        @Override
                        public void tasteReagieren(int key) {
                            if (key == KeyEvent.VK_M) menuScene();
                            if(key == KeyEvent.VK_Y) lvl2();

                        }
                    });
            setzeSchwerkraft(0);
            p1 = new player(3, 0.6);
            kills = 0;
            enemyhealth = 1;
            p1.setzeEbenenposition(2);
            enemies = new enemy[3];
            for (int z = 0; z < enemies.length; z++) {
                enemies[z] = new enemy(enemyhealth);
              //  enemies[z].macheDynamisch();
            }
            lvl1 = new Lvl1();
            p1.macheDynamisch();
        }
    }

    void lvl2(){
        if (Arrays.asList(nenneSzenennamen()).contains("lvl2Scene")) { //check if gameScene exists
            setzeAktiveSzene("lvl2Scene");
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("lvl2Scene");
        }
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
                        fireLatency = 0;
                    }
                });
        BildAktualisierungReagierbar frameUpdate;
        registriereBildAktualisierungReagierbar(
                frameUpdate = new BildAktualisierungReagierbar() {
                    @Override
                    public void bildAktualisierungReagieren(double v) { // tick() but for cool kids B)
                        if (shooting) {
                            shoot(p1.fireRate, p1.bulletSpread);
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
        p1 = new player(3, 0.6);
        kills = 0;
        enemyhealth = 1;
        p1.setzeEbenenposition(2);
        enemies = new enemy[3];
        for (int z = 0; z < enemies.length; z++) {
            enemies[z] = new enemy(enemyhealth);
            //  enemies[z].macheDynamisch();
        }
        lvl2 = new lvl2();
        p1.macheDynamisch();
    }



    void menuScene(){
        //ersellt eine neue szene und ruft die menü szene auf
        if (Arrays.asList(nenneSzenennamen()).contains("menuScene")) {
            setzeAktiveSzene("menuScene");
        }
        else {
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
            subMenu1 sm1 = new subMenu1();
            subMenu2 sm2 = new subMenu2();
            subMenu3 sm3 = new subMenu3();

            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) ms1.subMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) ms1.subMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) ms1.subMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) ms1.subMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) ms1.subMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) ms1.subMenu6();
                            if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                        }

                    });
        }
    }

    void titleScreen(){
        //benennt die aktive szene und ruft den titleScreen auf
        if (Arrays.asList(nenneSzenennamen()).contains("title")) {
            setzeAktiveSzene("title");
        } else {
            benenneAktiveSzene("title");
            TitleScreen ts = new TitleScreen();
            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ts.playButton.beinhaltetPunkt(x, y)) {
                                gameScene();
                            }
                            if (ts.bohLogo.beinhaltetPunkt(x, y)) {

                                try {
                                    java.awt.Desktop.getDesktop().browse(new URI("https://www.theboh.de"));
                                }
                                catch (IOException | URISyntaxException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                            if (ts.settingsButton.beinhaltetPunkt(x,y)){
                                menuScene();
                            }




                        }

                    });
            /*
            BildAktualisierungReagierbar frameUpdate;
            registriereBildAktualisierungReagierbar(
                    frameUpdate = new BildAktualisierungReagierbar() {
                        @Override
                        public void bildAktualisierungReagieren(double v) { // tick() but for cool kids B)
                            if (ts.background.nenneMittelpunktX() <= -21.3) {
                                ts.background.setzeMittelpunkt(21.3, 0);
                            }
                        }
                    });

             */
            Ticker clippy;
            registriereTicker(
                    (1/30d),
                    clippy = new Ticker() {
                        @Override
                        public void tick() {
                            if (ts.background.nenneMittelpunktX() <= -21.3) {
                                ts.background.setzeMittelpunkt(21.3, 0);
                            } else {
                                ts.background.verschieben(-((42.6/1280)*4), 0);
                            }
                        }
                    }
            );
        }
    }



    int fireLatency;
    private void shoot(int fireRate, double bulletSpread) {

        if (fireLatency==fireRate) {
            double x = nenneMausPositionX();
            double y = nenneMausPositionY();
            fireLatency +=1;
            t1 = null;
            t1 = new tracer(
                    x + 0.5 + ThreadLocalRandom.current().nextDouble(-bulletSpread, bulletSpread),
                    y + 0.5 + ThreadLocalRandom.current().nextDouble(-bulletSpread, bulletSpread),
                    p1.nenneMittelpunktX() + 0.5,
                    p1.nenneMittelpunktY() + 0.5
            );

            new SfxAudio("rifle");

            for (int z = 0; z < enemies.length; z++) {
                if (t1.touching(enemies[z])) {
                    enemies[z].takeDamage(1);
                    if (enemies[z].dead) {
                        enemies[z] = new enemy(enemyhealth);
                        kills = kills + 1;
                        System.out.println("kills: " + kills);
                    }

                }
            }
        } else if (fireLatency<fireRate) {
            fireLatency +=1;
        } else fireLatency = 0;

    }

}
