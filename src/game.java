import audio.MusicAudio;
import audio.SfxAudio;
import ea.edu.*;
import ea.edu.event.MausKlickReagierbar;

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
    Lvl1 level1;
    Lvl2 level2;
    Lvl3 level3;
    int lvl;
    boolean shooting;
    boolean CBT;
    boolean sound;
    boolean blood;
    MusicAudio[] m = new MusicAudio[3];


    public game(int width, int height) {
        super();
        setzeFensterGroesse(width, height);
        m[1] = new MusicAudio("lucas traene", true);
        m[0] = new MusicAudio("lucas traene", true);
        m[2] = new MusicAudio("license to kill", true);
        sound = true;
        lvl = 0;
        for (MusicAudio musicAudio : m) {
            musicAudio.pause();
        }
        titleScreen();
        blood = true;
    }

    void level1() {
        lvl ++;
        if (Arrays.asList(nenneSzenennamen()).contains("lvl1Scene")) { //check if lvl1Scene exists
            setzeAktiveSzene("lvl1Scene");
            soundHandler(0);
            setBlood(blood);
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("lvl1Scene");
            soundHandler(0);
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
                    v -> { // tick() but for cool kids B)
                        if (shooting) {
                            shoot(p1.fireRate, p1.bulletSpread);
                        }
                        if(kills == 2 && p1.nenneMittelpunktX() >= 18) level2();
                    });
            registriereTastenReagierbar(
                    key -> {
                        if (key == KeyEvent.VK_M) {
                            menuScene();
                        }
                        if(key == KeyEvent.VK_Y) level2();
                    });
            registriereTicker(0.2,
                    () -> {
                        ai();
                        playerHealTick();
                    }
            );
            setzeSchwerkraft(0);
            level1 = new Lvl1();
            p1 = new player(5, 4, 15, -15, -11);
            setBlood(blood);
            initPlayerHealthHandler();
            kills = 0;
            p1.setzeEbenenposition(10);
            p1.macheDynamisch();
            p1.skaliere(1.7);
        }
    }

    void level2() {
        lvl ++;
        if (Arrays.asList(nenneSzenennamen()).contains("lvl2Scene")) { //check if lvl1Scene exists
            setzeAktiveSzene("lvl2Scene");
            soundHandler(0);
            setBlood(blood);
        } else {
            erzeugeNeueSzene();
            benenneAktiveSzene("lvl2Scene");
            soundHandler(0);
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
                    v -> { // tick() but for cool kids B)
                        if (shooting) {
                            shoot(p1.fireRate, p1.bulletSpread);
                        }
                        if(kills == 3 && p1.nenneMittelpunktX() >= 18) level3();
                    });
            registriereTastenReagierbar(
                    key -> {
                        if (key == KeyEvent.VK_M) {
                            menuScene();
                        }
                        if (key == KeyEvent.VK_Y) level3();
                    });
            registriereTicker(0.2,
                    () -> {
                        ai();
                        playerHealTick();
                    }
            );
            setzeSchwerkraft(0);
            level2 = new Lvl2();
            p1 = new player(6, 5, 10, -18, 7);
            setBlood(blood);
            initPlayerHealthHandler();
            kills = 0;
            p1.setzeEbenenposition(10);
            p1.macheDynamisch();
            p1.skaliere(1.7);
        }
    }

    void level3() {
        lvl ++;
        if (Arrays.asList(nenneSzenennamen()).contains("lvl3Scene")) { //check if lvl1Scene exists
            setzeAktiveSzene("lvl3Scene");
            soundHandler(0);
            setBlood(blood);
        } else {
            erzeugeNeueSzene();
            benenneAktiveSzene("lvl3Scene");
            soundHandler(0);
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
                    v -> { // tick() but for cool kids B)
                        if (shooting) {
                            shoot(p1.fireRate, p1.bulletSpread);
                        }
                    });
            registriereTastenReagierbar(
                    key -> {
                        if (key == KeyEvent.VK_M) {
                            menuScene();
                        }
                        if (key == KeyEvent.VK_Y) level1();
                    });
            registriereTicker(0.2,
                    () -> {
                        ai();
                        playerHealTick();
                    }
            );
            setzeSchwerkraft(0);
            level3 = new Lvl3();
            p1 = new player(6, 5, 10, -18, 7);
            setBlood(blood);
            initPlayerHealthHandler();
            kills = 0;
            p1.setzeEbenenposition(10);
            p1.macheDynamisch();
            p1.skaliere(1.7);
        }
    }

    void menuScene(){
        //ersellt eine neue szene und ruft die menü szene auf
        if (Arrays.asList(nenneSzenennamen()).contains("menuScene")) {
            setzeAktiveSzene("menuScene");
            soundHandler(0);
        }
        else {
            erzeugeNeueSzene();
            benenneAktiveSzene("menuScene");
            soundHandler(0);
            registriereTastenReagierbar(
                    key -> {
                        if (key == KeyEvent.VK_M) {
                            level1();
                        }
                    }
            );

            MenuScene ms1 = new MenuScene();

            registriereMausKlickReagierbar(
                    (x, y) -> {
                        System.out.println(x + "     " + y);

                        if (ms1.menuButton[0].beinhaltetPunkt(x,y)) levelHandler();
                        if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                        if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                        if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                        if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                        if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                        if (ms1.menuButton[6].beinhaltetPunkt(x,y)) donations();
                        if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                    });
        }
    }

    void titleScreen(){
        lvl = 0;
        //benennt die aktive szene und ruft den titleScreen auf
        if (Arrays.asList(nenneSzenennamen()).contains("titleScene")) {
            setzeAktiveSzene("titleScene");
            soundHandler(0);

        } else {
            benenneAktiveSzene("titleScene");
            soundHandler(0);
            TitleScreen ts = new TitleScreen();
            registriereMausKlickReagierbar(
                    (x, y) -> {
                        System.out.println(x + "     " + y);

                        if (ts.playButton.beinhaltetPunkt(x, y)) {
                            level1();
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




                    });
            registriereTicker(
                    (1/30d),
                    () -> {
                        if (ts.background.nenneMittelpunktX() <= -21.3) {
                            ts.background.setzeMittelpunkt(21.3, 0);
                        } else {
                            ts.background.verschieben(-((42.6/1280)*4), 0);
                        }
                    }
            );
        }
    }


    int fireLatency;
    private void shoot(int fireRate, double bulletSpread) {
        double[] newm;
        if (fireLatency==fireRate) {
            double x = nenneMausPositionX();
            double y = nenneMausPositionY();
            double px = p1.nenneMittelpunktX();
            double py = p1.nenneMittelpunktY();
            fireLatency +=1;
            t1 = null;
            double absSpread = tracer.pyth(px-x,py-y) * Math.atan(Math.toRadians(bulletSpread));
            if(lvl == 1){
                newm = checkWalls(
                    x + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                    y + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                    px,
                    py,
                    level1.walls
            );}else{
                newm = checkWalls(
                        x + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        y + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        px,
                        py,
                        level2.walls
                );}
            t1 = new tracer(
                    newm[0],
                    newm[1],
                    newm[2],
                    newm[3]
            );

            if (sound) new SfxAudio("rifle");
            if(lvl == 1) {
                for (int z = 0; z < level1.enemies.length; z++) {
                    if (t1.touching(level1.enemies[z])) {
                        level1.enemies[z].takeDamage(1);
                        if (level1.enemies[z].dead && level1.enemies[z].health == 0) {
                            //Lvl1.enemies[z] = new enemy(enemyhealth);
                            kills++;
                            System.out.println("kills: " + kills);
                        } //else if (Lvl1.enemies[z].health < 0)

                    }
                }
            }else{
                for (int z = 0; z < level2.enemies.length; z++) {
                    if (t1.touching(level2.enemies[z])) {
                        level2.enemies[z].takeDamage(1);
                        if (level2.enemies[z].dead && level2.enemies[z].health == 0) {
                            //Lvl1.enemies[z] = new enemy(enemyhealth);
                            kills++;
                            System.out.println("kills: " + kills);
                        } //else if (Lvl1.enemies[z].health < 0)

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
        if(lvl == 1) {
            for (enemy e : level1.enemies) {
                int ran = ThreadLocalRandom.current().nextInt(4);
                if (ran <= 2 && e.health > 0) {
                    enemyShoot(e, 10);
                }
            }
        }else if(lvl == 2){
            for (enemy e : level2.enemies) {
                int ran = ThreadLocalRandom.current().nextInt(4);
                if (ran <= 2 && e.health > 0) {
                    enemyShoot(e, 10);
                }
            }
        }else{
            for (enemy e : level3.enemies) {
                int ran = ThreadLocalRandom.current().nextInt(4);
                if (ran <= 2 && e.health > 0) {
                    enemyShoot(e, 10);
                }
            }
        }
    }

    private void enemyShoot(enemy e, double spread) {
        double ex = e.nenneMittelpunktX();
        double ey = e.nenneMittelpunktY();
        double tx = p1.nenneMittelpunktX();
        double ty = p1.nenneMittelpunktY();

        if(lvl == 1) {
            if (enemyLineOfSight(level1.walls, p1.nenneMittelpunktX(), p1.nenneMittelpunktY(), ex, ey)) {

                if (sound) new SfxAudio("pistol");

                double absSpread = tracer.pyth(ex - tx, ey - ty) * Math.atan(Math.toRadians(spread));
                double[] newm = checkWalls(
                        tx + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        ty + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        ex,
                        ey,
                        level1.walls
                );
                tracer t2 = new tracer(newm[0], newm[1], newm[2], newm[3]);
                if (t2.touching(p1)) {
                    playerHealthHandler(1);
                }
            }
        }else if(lvl == 2){
            if (enemyLineOfSight(level2.walls, p1.nenneMittelpunktX(), p1.nenneMittelpunktY(), ex, ey)) {

                if (sound) new SfxAudio("pistol");

                double absSpread = tracer.pyth(ex - tx, ey - ty) * Math.atan(Math.toRadians(spread));
                double[] newm = checkWalls(
                        tx + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        ty + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        ex,
                        ey,
                        level2.walls
                );
                tracer t2 = new tracer(newm[0], newm[1], newm[2], newm[3]);
                if (t2.touching(p1)) {
                    playerHealthHandler(1);
                }
            }
        }else{
            if (enemyLineOfSight(level3.walls, p1.nenneMittelpunktX(), p1.nenneMittelpunktY(), ex, ey)) {

                if (sound) new SfxAudio("pistol");

                double absSpread = tracer.pyth(ex - tx, ey - ty) * Math.atan(Math.toRadians(spread));
                double[] newm = checkWalls(
                        tx + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        ty + ThreadLocalRandom.current().nextDouble(-absSpread, absSpread),
                        ex,
                        ey,
                        level3.walls
                );
                tracer t2 = new tracer(newm[0], newm[1], newm[2], newm[3]);
                if (t2.touching(p1)) {
                    playerHealthHandler(1);
                }
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
        new Text("You died!",5).setzeFarbe("rot");
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
            registriereMausKlickReagierbar(
                    (x, y) -> {
                        System.out.println(x + "     " + y);

                        if (ms1.menuButton[0].beinhaltetPunkt(x,y)) levelHandler();
                        if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                        if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                        if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                        if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                        if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                        if (ms1.menuButton[6].beinhaltetPunkt(x,y)) donations();
                        if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                        if (ms1.sub1Button[0].beinhaltetPunkt(x,y)) soundHandler(1);
                        if (ms1.sub1Button[1].beinhaltetPunkt(x,y)) soundHandler(2);

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
            registriereMausKlickReagierbar(
                    (x, y) -> {
                        System.out.println(x + "     " + y);

                        if (ms1.menuButton[0].beinhaltetPunkt(x,y)) levelHandler();
                        if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                        if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                        if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                        if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                        if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                        if (ms1.menuButton[6].beinhaltetPunkt(x,y)) donations();
                        if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                        if (ms1.sub2Button[0].beinhaltetPunkt(x,y)) blood = true;
                        if (ms1.sub2Button[1].beinhaltetPunkt(x,y)) blood = false;
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
            ms1.subMenu3();
            registriereMausKlickReagierbar(
                    (x, y) -> {
                        System.out.println(x + "     " + y);

                        if (ms1.menuButton[0].beinhaltetPunkt(x,y)) levelHandler();
                        if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                        if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                        if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                        if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                        if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                        if (ms1.menuButton[6].beinhaltetPunkt(x,y)) donations();
                        if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
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
            ms1.subMenu4();
            registriereMausKlickReagierbar(
                    (x, y) -> {
                        System.out.println(x + "     " + y);

                        if (ms1.menuButton[0].beinhaltetPunkt(x,y)) levelHandler();
                        if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                        if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                        if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                        if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                        if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                        if (ms1.menuButton[6].beinhaltetPunkt(x,y)) donations();
                        if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
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
            registriereMausKlickReagierbar(
                    (x, y) -> {
                        System.out.println(x + "     " + y);

                        if (ms1.menuButton[0].beinhaltetPunkt(x,y)) levelHandler();
                        if (ms1.menuButton[1].beinhaltetPunkt(x,y)) SubMenu1();
                        if (ms1.menuButton[2].beinhaltetPunkt(x,y)) SubMenu2();
                        if (ms1.menuButton[3].beinhaltetPunkt(x,y)) SubMenu3();
                        if (ms1.menuButton[4].beinhaltetPunkt(x,y)) SubMenu4();
                        if (ms1.menuButton[5].beinhaltetPunkt(x,y)) SubMenu5();
                        if (ms1.menuButton[6].beinhaltetPunkt(x,y)) donations();
                        if (ms1.menuButton[7].beinhaltetPunkt(x,y)) titleScreen();
                    });
        }

    }

    public void soundHandler(int i){

        if(i == 1) sound = false;
        if(i == 2) sound = true;
        for (MusicAudio musicAudio : m) {
            musicAudio.pause();
        }
        if(sound){
            String s = getActiveScene().getName();
            System.out.println(s);
            switch (s) {
                case "lvl1Scene", "lvl2Scene", "lvl3Scene" -> m[2].resume();
                case "menuScene", "titleScene" -> m[1].resume();
            }
        }
    }

    public void donations(){
        try {
            java.awt.Desktop.getDesktop().browse(new URI("https://www.theboh.de/donations"));
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void setBlood(boolean bld) {
        p1.blood = bld;
        for (enemy e : level1.enemies) {
            e.blood = bld;
        }
    }

    public void levelHandler(){
        switch (lvl) {
            case 2 -> level2();
            case 3 -> level3();
            default -> level1();
        }
    }

}
