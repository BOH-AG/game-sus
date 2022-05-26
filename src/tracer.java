import ea.edu.Rechteck;

public class tracer extends Rechteck {

    public tracer(double mx, double my, double px, double py) {
        super(
                Math.sqrt(Math.pow(px-mx, 2)+Math.pow(py-my, 2)), //pythagora's theorem bs
                0.2
        );
        double angle = Math.asin((py-my)/Math.sqrt(Math.pow(px-mx, 2)+Math.pow(py-my, 2)))*57.296; // i hate trig (math people suck)
        if ((px-mx)>0) setzeDrehwinkel(angle);
        else setzeDrehwinkel(-angle);

        setzeMittelpunkt(
                px+((mx-px)/2-0.5),
                py+((my-py)/2-0.5)
        );
        setzeFarbe("Gelb");
        machePartikel(0.3);
        verzoegere(0.2, this::entfernen);
    }
}
