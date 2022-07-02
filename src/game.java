import audio.MusicAudio;
import audio.SfxAudio;
import ea.edu.Rechteck;
import ea.edu.Spiel;
import ea.edu.Text;
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



public class game extends Spiel {


    tracer t1;
    player p1;
    enemy[] enemies;
    int enemyhealth;
    int kills;
    Lvl1 lvl1;
    lvl2 lvl2;
    boolean shooting;
    boolean CBT;


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

            registriereMausKlickReagierbar(
                    new MausKlickReagierbar() {
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
            registriereBildAktualisierungReagierbar(
                    new BildAktualisierungReagierbar() {
                        @Override
                        public void bildAktualisierungReagieren(double v) { // tick() but for cool kids B)
                            if (shooting) {
                                shoot(p1.fireRate, p1.bulletSpread);
                            }
                        }
                    });
            registriereTastenReagierbar(
                    new TastenReagierbar() {
                        @Override
                        public void tasteReagieren(int key) {
                            if (key == KeyEvent.VK_M) menuScene();
                            if(key == KeyEvent.VK_Y) lvl2();

                        }
                    });
            registriereTicker(0.2,
                    new Ticker() {
                        @Override
                        public void tick() {
                            ai();
                            playerHealTick();
                        }
                    }
            );
            setzeSchwerkraft(0);
            lvl1 = new Lvl1();
            p1 = new player(6, 5, 10, -15, -11);
            initPlayerHealthHandler();
            kills = 0;
            p1.setzeEbenenposition(10);
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
        registriereMausKlickReagierbar(
                new MausKlickReagierbar() {
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
        registriereBildAktualisierungReagierbar(
                new BildAktualisierungReagierbar() {
                    @Override
                    public void bildAktualisierungReagieren(double v) { // tick() but for cool kids B)
                        if (shooting) {
                            shoot(p1.fireRate, p1.bulletSpread);
                        }
                    }
                });
        registriereTastenReagierbar(
                new TastenReagierbar() {
                    @Override
                    public void tasteReagieren(int key) {
                        if (key == KeyEvent.VK_M) {
                            menuScene();
                        }
                    }
                });
        setzeSchwerkraft(0);
        healCounter = 0;
        p1 = new player(3, 0.6, 10, 0 ,0);
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

            registriereTastenReagierbar(
                    new TastenReagierbar() {
                        @Override
                        public void tasteReagieren(int key) {
                            if (key == KeyEvent.VK_M) {
                                gameScene();
                            }
                        }
                    }
            );

            MenuScene ms1 = new MenuScene();

            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) SubMenu6();
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
            registriereMausKlickReagierbar(
                    new MausKlickReagierbar() {
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
            registriereTicker(
                    (1/30d),
                    new Ticker() {
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
            double px = p1.nenneMittelpunktX();
            double py = p1.nenneMittelpunktY();
            fireLatency +=1;
            t1 = null;
            /*double[] newm = checkWalls(
                    x + ThreadLocalRandom.current().nextDouble(-bulletSpread, bulletSpread),
                    y + ThreadLocalRandom.current().nextDouble(-bulletSpread, bulletSpread),
                    p1.nenneMittelpunktX(),
                    p1.nenneMittelpunktY(),
                    lvl1.walls
            );*/
            double absSpread = tracer.pyth(px-x,py-y) * Math.atan(Math.toRadians(bulletSpread));
            double[] newm = checkWalls(
                    x + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                    y + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                    px,
                    py,
                    lvl1.walls
            );
            t1 = new tracer(
                    newm[0],
                    newm[1],
                    newm[2],
                    newm[3]
            );

            new SfxAudio("rifle");

            for (int z = 0; z < Lvl1.enemies.length; z++) {
                if (t1.touching(Lvl1.enemies[z])) {
                    Lvl1.enemies[z].takeDamage(1);
                    if (Lvl1.enemies[z].dead) {
                        //Lvl1.enemies[z] = new enemy(enemyhealth);
                        kills++;
                        System.out.println("kills: " + kills);
                    }

                }
            }
        } else if (fireLatency<fireRate) {
            fireLatency +=1;
        } else fireLatency = 0;

    }

    private double[] checkWalls(double mx, double my, double px, double py, Rechteck[] wa) {
        double length = tracer.pyth(px-mx, py-my);
        double delx = (mx-px);
        double dely = (my-py);

        double newx = 99999;
        double newy = 99999;

        int hm = (int)(length*5);
        point[] points = new point[hm];
        for (int z=hm-1; z>-1; z--) {
            points[z] = new point((delx/hm)*(z+1)+px,(dely/hm)*(z+1)+py);
            for (Rechteck re : wa) {
                if (re.beinhaltetPunkt(points[z].x, points[z].y)) {
                    newx = points[z].x;
                    newy = points[z].y;
                    break;
                }
            }
        }

        if (newx==99999 && newy==99999) {
            newx = mx;
            newy = my;
        }

        return new double[] {
                newx+0.3,
                newy+0.3,
                px+0.5,
                py+0.5
        };
    }

    private void ai() {

        for (enemy e: Lvl1.enemies) {
            int ran = ThreadLocalRandom.current().nextInt(4);
            if (ran <= 2 && e.health>0) {
                enemyShoot(e, 10);
            }
        }
    }

    private void enemyShoot(enemy e, double spread) {
        double ex = e.nenneMittelpunktX();
        double ey = e.nenneMittelpunktY();
        double tx = p1.nenneMittelpunktX();
        double ty = p1.nenneMittelpunktY();

        if (enemyLineOfSight(lvl1.walls, p1.nenneMittelpunktX(), p1.nenneMittelpunktY(), ex, ey)) {

            double absSpread = tracer.pyth(ex-tx,ey-ty) * Math.atan(Math.toRadians(spread));
            double[] newm = checkWalls(
                    tx + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                    ty + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                    ex,
                    ey,
                    lvl1.walls
            );
            tracer t2 = new tracer(newm[0], newm[1], newm[2], newm[3]);
            if (t2.touching(p1)) {
                playerHealthHandler(1);
            }
        }
    }

    private boolean enemyLineOfSight(Rechteck[] re, double tx, double ty, double ex, double ey) {
        boolean aaa = false;
        tracer tr = new tracer(tx, ty, ex, ey);
        tr.setzeSichtbar(false);
        for (Rechteck aa: re) {
            if (tr.touching(aa)) {
                aaa = true;
                break;
            }
        }
        tr.entfernen();
        return !aaa;
    }

    Text healthHud;
    private void initPlayerHealthHandler() {
        healthHud = new Text(""+p1.getHealth(), 2);
        healthHud.setzeFarbe("rot");
        healthHud.setzeMittelpunkt(-20, 10);
    }

    private void playerHealthHandler(int h) {
        if (h > 0) {
            p1.takeDamage(h);
            healthHud.setzeInhalt(""+p1.getHealth());
            if (p1.getHealth()<1) {
                death();
            }
        } else if (h < 0) {
            p1.heal(1);
            healthHud.setzeInhalt(""+p1.getHealth());
        }
    }
    int healCounter;
    private void playerHealTick() {
        if (healCounter < 17) healCounter++;
        else {
            healCounter = 0;
            playerHealthHandler(-1);
        }
    }

    private void death() {
        new Text("you died",5).setzeFarbe("rot");
        p1.verzoegere(0.3, this::a);
    }
    private void a() {
        throw new RuntimeException();
    }

    void SubMenu1(){
        if (Arrays.asList(nenneSzenennamen()).contains("SM1")) {
            setzeAktiveSzene("SM1");
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("SM1");
            MenuScene ms1 = new MenuScene();
            ms1.subMenu1();
            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) SubMenu6();
                            if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                            if (ms1.sub1Button[0].beinhaltetPunkt(x,y)) ;
                        }

                    });


        }
    }
    void SubMenu2(){
        if (Arrays.asList(nenneSzenennamen()).contains("SM2")) {
            setzeAktiveSzene("SM2");
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("SM2");
            MenuScene ms1 = new MenuScene();
            ms1.subMenu2();
            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) SubMenu6();
                            if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                            if (ms1.sub2Button[0].beinhaltetPunkt(x,y)) CBT=true;
                            if (ms1.sub2Button[1].beinhaltetPunkt(x,y)) CBT=false;
                        }

                    });
        }

    }
    void SubMenu3(){
        if (Arrays.asList(nenneSzenennamen()).contains("SM3")) {
            setzeAktiveSzene("SM3");
        } else {
            erzeugeNeueSzene();
            benenneAktiveSzene("SM3");
            MenuScene ms1 = new MenuScene();
            ms1.subMenu3();            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) SubMenu6();
                            if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                            if (ms1.sub3Button[0].beinhaltetPunkt(x,y)) ;
                        }

                    });
        }
    }
    void SubMenu4(){
        if (Arrays.asList(nenneSzenennamen()).contains("SM4")) {
            setzeAktiveSzene("SM4");
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("SM4");
            MenuScene ms1 = new MenuScene();
            ms1.subMenu4();            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) SubMenu6();
                            if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                            if (ms1.sub4Button[0].beinhaltetPunkt(x,y)) ;
                        }

                    });
        }

    }
    void SubMenu5(){
        if (Arrays.asList(nenneSzenennamen()).contains("SM5")) {
            setzeAktiveSzene("SM5");
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("SM5");
            MenuScene ms1 = new MenuScene();
            ms1.subMenu5();
            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) SubMenu6();
                            if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                            if (ms1.sub5Button[0].beinhaltetPunkt(x,y)) ;
                        }

                    });
        }

    }
    void SubMenu6(){
        if (Arrays.asList(nenneSzenennamen()).contains("SM6")) {
            setzeAktiveSzene("SM6");
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("SM6");
            MenuScene ms1 = new MenuScene();
            ms1.subMenu6();
            MausKlickReagierbar dieSendungMitDer;
            registriereMausKlickReagierbar(
                    dieSendungMitDer = new MausKlickReagierbar() {
                        @Override
                        public void klickReagieren(double x, double y) {
                            System.out.println(x + "     " + y);

                            if (ms1.menuButton[0].beinhaltetPunkt(x,y)) gameScene();
                            if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                            if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                            if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                            if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                            if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                            if (ms1.menuButton[6].beinhaltetPunkt(x,y)) SubMenu6();
                            if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                            if (ms1.sub6Button[0].beinhaltetPunkt(x,y)) ;
                        }

                    });

        }

    }

}
