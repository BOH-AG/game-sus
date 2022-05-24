import ea.Vector;
import ea.actor.Polygon;
import ea.edu.Geometrie;

public class tracer extends Geometrie<Polygon> {
    Vector p;
    Vector p2;
    Vector m;
    Vector m2;
    public tracer(double px, double py, double mx, double my) {
        super(new Polygon(
                new Vector(px, py),
                new Vector(mx, my),
                new Vector(mx, my+0.5),
                new Vector(px, py+0.5)
        ));
        setzeFarbe("Gelb");
        machePartikel(0.3);
        verzoegere(0.2, this::entfernen);
    }
}
