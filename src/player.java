import ea.FrameUpdateListener;
import ea.Vector;
import ea.actor.Image;
import ea.event.KeyListener;
import java.awt.event.KeyEvent;


public class player extends Image implements FrameUpdateListener, KeyListener {
    public player() {
        super("rsc/linustips.png", 16);
        mvmnt = new Vector(0, 0);

    }

    Vector mvmnt;
    int delx;
    int dely;

    @Override
    public void onFrameUpdate(float v) {
        mvmnt = new Vector(delx, dely);
        this.moveBy(mvmnt.multiply(v));
        //mvmnt = new Vector(0,0);
    }
    @Override
    public void onKeyDown(KeyEvent e) {
        int k = e.getKeyCode();
        if (k==KeyEvent.VK_A) {
            delx = -5;
            this.setRotation(90);
        }if (k==KeyEvent.VK_D) {
            delx = 5;
            this.setRotation(270);
        }if (k==KeyEvent.VK_S) {
            dely = -5;
            this.setRotation(180);
        }if (k == KeyEvent.VK_W){
            dely = 5;
            this.setRotation(0);
       }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        KeyListener.super.onKeyUp(e);
        int k = e.getKeyCode();
        if (k==KeyEvent.VK_W||k==KeyEvent.VK_S) dely = 0;
        if (k==KeyEvent.VK_A||k==KeyEvent.VK_D) delx = 0;
    }
}
