import ea.FrameUpdateListener;
import ea.actor.Actor;
import ea.edu.Kreis;
import ea.edu.Rechteck;

public class tracer extends Rechteck {

    public Actor actor;
    public double length;
    private double delx;
    private double dely;

    public tracer(double mx, double my, double px, double py) {
        super(Math.sqrt(Math.pow(px-mx, 2)+Math.pow(py-my, 2)), 0.1);
        length = Math.sqrt(Math.pow(px-mx, 2)+Math.pow(py-my, 2));
        actor = this.getActor();
        tracer actor2 = this;
        double newx = px+((mx-px)/2-0.5);
        double newy = py+((my-py)/2-0.5);
        double angle = Math.asin((py-my)/length)*57.296; // i hate trig (math people suck)

        delx = (mx-px)*0.5;
        dely = (my-py)*0.5;

        if ((px-mx)>0) setzeDrehwinkel(angle);
        else setzeDrehwinkel(-angle);

        setzeMittelpunkt(newx, newy);
        actor.setFriction(99999999999f);
        FrameUpdateListener framesWinGames;
        actor.addFrameUpdateListener(
                framesWinGames = new FrameUpdateListener() {
                    @Override
                    public void onFrameUpdate(float v) {
                        setzeMittelpunkt(newx, newy);
                    }
                }

        );
        setzeFarbe("Gelb");
        machePartikel(0.3);
        verzoegere(0.2, this::entfernen);
    }

    public boolean touching(enemy e) {
        double ex = e.nenneMittelpunktX();
        double ey = e.nenneMittelpunktY();
        int hm = (int)(length*3);
        point[] points = new point[hm*2+1];
        for (int z=0; z<hm; z++) {
            points[z] = new point((delx/hm)*(z+1),(dely/hm)*(z+1));
            points[z+hm] = new point(-(delx/hm)*(z+1),-(dely/hm)*(z+1));
        }
        points[hm*2] = new point(0,0);
        /*
        for (int z=0; z < points.length; z++) {
            Kreis k = new Kreis(0.1);
            k.setzeMittelpunkt(nenneMittelpunktX()+points[z].x, nenneMittelpunktY()+points[z].y);
            k.setzeFarbe("schwarz");
            k.machePartikel(0.1);
        }
        */
        boolean bing = false;
        for (int z=0; z < points.length; z++) {
            if (e.beinhaltetPunkt(points[z].x, points[z].y)) {
                bing = true;
            }
        }
        return bing;
    }



}
