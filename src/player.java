import ea.Vector;
import ea.actor.StatefulAnimation;
import ea.edu.Figur;
import ea.edu.event.TastenReagierbar;
import ea.event.MouseButton;
import ea.event.MouseClickListener;

import java.awt.event.KeyEvent;

public class player extends Figur implements TastenReagierbar {

    StatefulAnimation<String> actor;
    double delx;
    double dely;

    player() {
        super("normal", "rsc/fox2.gif");
        actor = getActor();
        actor.setSize(2.5f, 2.5f);


    }

    @Override
    public void tasteReagieren(int key) {
        if (key==KeyEvent.VK_A) {
            delx = -5;
        }if (key==KeyEvent.VK_D) {
            delx = 5;
        }if (key==KeyEvent.VK_S) {
            dely = -5;
        }if (key==KeyEvent.VK_W){
            dely = 5;
        }
        setzeGeschwindigkeit(delx, dely);
        direction();
    }

    @Override
    public void tasteLosgelassenReagieren(int key) {
        TastenReagierbar.super.tasteLosgelassenReagieren(key);
        if (key==KeyEvent.VK_W||key==KeyEvent.VK_S) dely = 0;
        if (key==KeyEvent.VK_A||key==KeyEvent.VK_D) delx = 0;
        setzeGeschwindigkeit(delx, dely);
        direction();
    }

    private void direction() {
        // rotate player in all 8 movement directions (and place it in the correct center spot)
        Vector bingy = actor.getCenter();
        if (delx>0) setzeDrehwinkel(270);
        else if (delx<0) setzeDrehwinkel(90);

        if (dely>0) {
            if (delx>0) setzeDrehwinkel(315);
            else if (delx<0) setzeDrehwinkel(45);
            else setzeDrehwinkel(0);
        } else if (dely<0) {
            if (delx>0) setzeDrehwinkel(225);
            else if (delx<0) setzeDrehwinkel(135);
            else setzeDrehwinkel(180);
        }
        actor.setCenter(bingy);
    }



}






















/*
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
*/
