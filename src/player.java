import ea.FrameUpdateListener;
import ea.Vector;
import ea.actor.Image;
import ea.event.KeyListener;
import java.awt.event.KeyEvent;


public class player extends Image implements FrameUpdateListener, KeyListener {
    public player() {
        super("rsc/linustips.png", 2,2);
        mvmnt = new Vector(0, 0);
    }

    Vector mvmnt;
    int delx;
    int dely;

    @Override
    public void onFrameUpdate(float v) {
        mvmnt = new Vector(delx, dely);
        moveBy(mvmnt.multiply(v));
        direction();
    }
    @Override
    public void onKeyDown(KeyEvent e) {
        int k = e.getKeyCode();
        if (k==KeyEvent.VK_A) {
            delx = -5;
        }if (k==KeyEvent.VK_D) {
            delx = 5;
        }if (k==KeyEvent.VK_S) {
            dely = -5;
        }if (k == KeyEvent.VK_W){
            dely = 5;
       }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        KeyListener.super.onKeyUp(e);
        int k = e.getKeyCode();
        if (k==KeyEvent.VK_W||k==KeyEvent.VK_S) dely = 0;
        if (k==KeyEvent.VK_A||k==KeyEvent.VK_D) delx = 0;
    }

    private void direction() {
        // rotate player in all 8 movement directions (and place it in the correct center spot)
        Vector bingy = getCenter();
        if (delx>0) setRotation(270);
        else if (delx<0) setRotation(90);

        if (dely>0) {
            if (delx>0) setRotation(315);
            else if (delx<0) setRotation(45);
            else setRotation(0);
        } else if (dely<0) {
            if (delx>0) setRotation(225);
            else if (delx<0) setRotation(135);
            else setRotation(180);
        }
        setCenter(bingy);
    }


}
