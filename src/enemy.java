import ea.edu.Figur;

import java.util.concurrent.ThreadLocalRandom;

public class enemy extends Figur implements damage {

    public int health;
    public boolean dead;
     enemy(int h) {

        super("normal", "rsc/obamba.gif");
        fuegeZustandVonGifHinzu("dead", "rsc/doge.gif");
        getActor().setSize(2f,2f);
        health = h;
        dead = false;
        setzeMittelpunkt(
            ThreadLocalRandom.current().nextDouble(-25,25),
            ThreadLocalRandom.current().nextDouble(-10,10)
        );
        animiereGerade(
            ThreadLocalRandom.current().nextDouble(0.2,1),
            ThreadLocalRandom.current().nextDouble(-5,5),
            ThreadLocalRandom.current().nextDouble(-5,5),
            true
        );
    }

    @Override
    public void takeDamage(int d) {

        if (health >d){
            health = health-d;
            System.out.println("enemy health: " + health);
        }else if (health==d){
            kill();
            System.out.println("enemy dead");
        }
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
