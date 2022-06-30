import ea.FrameUpdateListener;
import ea.edu.Figur;
import ea.edu.Bild;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class enemy extends Figur implements damage {

    public int health;
    public boolean dead;
    List<Bild> bloodPuddles;
     enemy(int h) {

        super("normal", "rsc/security.gif");
        fuegeZustandVonGifHinzu("dead", "rsc/doge.gif");
        getActor().setSize(2f,2f);
        setzeEbenenposition(9);
        health = h;
        dead = false;
        bloodPuddles = new ArrayList<>();
        /*setzeMittelpunkt(
            ThreadLocalRandom.current().nextDouble(-25,25),
            ThreadLocalRandom.current().nextDouble(-10,10)
        );
        animiereGerade(
            ThreadLocalRandom.current().nextDouble(0.2,1),
            ThreadLocalRandom.current().nextDouble(-5,5),
            ThreadLocalRandom.current().nextDouble(-5,5),
            true
        );*/
    }

    @Override
    public void takeDamage(int d) {

        if (health >d){
            health = health-d;
            blood();
            System.out.println("enemy health: " + health);
        }else if (health==d){
            kill();
            System.out.println("enemy dead");
        }
    }


    private void blood() {
        bloodPuddles.add(new Bild(1.5,1.5, "rsc/blood.png"));
        Bild b = bloodPuddles.get(bloodPuddles.size()-1);
        b.setzeDrehwinkel(ThreadLocalRandom.current().nextInt(360));
        b.setzeMittelpunkt((float)nenneMittelpunktX(), (float)nenneMittelpunktY());
        b.setzeEbenenposition(8);
        b.verzoegere(10, b::entfernen);
    }


    public void kill() {
        health = 0;
        dead = true;
        setzeZustand("dead");
        pausiereAnimation(true);
        setzeWinkelgeschwindigkeit(5);
        machePartikel(2);
        macheSensor();
        setzeGeschwindigkeit(
            ThreadLocalRandom.current().nextDouble(-20,20),
            ThreadLocalRandom.current().nextDouble(-20,20)
        );
    }
}
