import ea.FrameUpdateListener;
import ea.actor.Actor;
import ea.edu.Rechteck;

public class tracer extends Rechteck {

    public Actor actor;

    public tracer(double mx, double my, double px, double py) {
        super(Math.sqrt(Math.pow(px-mx, 2)+Math.pow(py-my, 2)), 0.1);
        actor = this.getActor();
        tracer actor2 = this;
        double newx = px+((mx-px)/2-0.5);
        double newy = py+((my-py)/2-0.5);
        double angle = Math.asin((py-my)/Math.sqrt(Math.pow(px-mx, 2)+Math.pow(py-my, 2)))*57.296; // i hate trig (math people suck)

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
        return true;
    }



}
