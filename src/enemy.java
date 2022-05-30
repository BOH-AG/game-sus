import ea.edu.Figur;

import java.util.concurrent.ThreadLocalRandom;

public class enemy extends Figur {
     enemy() {

        super("normal", "rsc/linustips.gif");
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

}
