import ea.edu.Figur;
import ea.edu.Bild;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class enemy extends Figur implements damage {

    public int health;
    public boolean dead;
    boolean blood;

    List<Bild> bloodPuddles;
     enemy(int h) {

        super("normal", "rsc/security-new.gif");
        fuegeZustandVonGifHinzu("dead", "rsc/sus.gif");
        getActor().setSize(1.8f,1.8f);
        setzeEbenenposition(9);
        health = h;
        dead = false;
        bloodPuddles = new ArrayList<>();
        blood = true;
    }

    @Override
    public void takeDamage(int d) {

        if (health >d){
            health -= d;
            blood();
        } else if (dead) {
            blood();
        } else if (health == d){
            kill();
        }
    }

    private void blood() {
            bloodPuddles.add(new Bild(
                    ThreadLocalRandom.current().nextDouble(4, 4.6),
                    ThreadLocalRandom.current().nextDouble(4, 4.6),
                    getBlood()
            ));

        Bild b = bloodPuddles.get(bloodPuddles.size()-1);
        b.setzeDrehwinkel(ThreadLocalRandom.current().nextInt(360));
        b.setzeMittelpunkt((float)nenneMittelpunktX(), (float)nenneMittelpunktY());
        b.setzeEbenenposition(2);
        b.verzoegere(10, b::entfernen);
    }

    public void kill() {
        health = 0;
        dead = true;
        setzeZustand("dead");
    }

    private String getBlood() {
        if (blood) return "rsc/blood.png";
        else return "rsc/blood_blue.png";
    }
}
