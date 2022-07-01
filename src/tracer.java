import ea.FrameUpdateListener;
import ea.actor.Actor;
import ea.edu.EduActor;
import ea.edu.Figur;
import ea.edu.Rechteck;

public class tracer extends Rechteck {

    public Actor actor;
    public double length;
    private double delx;
    private double dely;

    public tracer(double mx, double my, double px, double py) {
        super(pyth(px-mx, py-my), 0.1);
        length = pyth(px-mx, py-my);
        actor = this.getActor();
        tracer actor2 = this;
        double newx = px+((mx-px)/2-0.5);
        double newy = py+((my-py)/2-0.5);
        double angle = Math.asin((py-my)/length)*57.296; // i hate trig (math people suck)
        setzeEbenenposition(4);
        delx = (mx-px)*0.5;
        dely = (my-py)*0.5;

        if ((px-mx)>0) setzeDrehwinkel(angle);
        else setzeDrehwinkel(-angle);

        setzeMittelpunkt(newx, newy);
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







    public tracer(double[] pa) {
        super(pyth(pa[2]-pa[0], pa[3]-pa[1]), 0.1);
        double mx = pa[0];
        double my = pa[1];
        double px = pa[2];
        double py = pa[3];
        length = pyth(px-mx, py-my);
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











    public boolean touching(EduActor e) {
        //double ex = e.nenneMittelpunktX();
        //double ey = e.nenneMittelpunktY();
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
        for (point point : points) {
            double thisX = nenneMittelpunktX();
            double thisY = nenneMittelpunktY();
            if (e.beinhaltetPunkt(thisX + point.x, thisY + point.y)) {
                bing = true;
                break;
            }
        }
        return bing;
    }

    public static double pyth(double a, double b) {// static method to run pythagoras calculations
        return Math.sqrt(
                Math.pow(a, 2) + Math.pow(b, 2)
        );
    }




}
