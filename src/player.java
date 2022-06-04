import ea.Vector;
import ea.actor.StatefulAnimation;
import ea.edu.Figur;
import ea.edu.event.TastenReagierbar;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class player extends Figur implements TastenReagierbar {

    StatefulAnimation<String> actor;
    double delx;
    double dely;
    boolean menu;

    player() {
        super("normal", "rsc/fox.gif");
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
        if (key==KeyEvent.VK_TAB){
            menu = !menu;
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
        Vector v1 = actor.getCenter();
        if (delx>0) setzeDrehwinkel(270);
        else if (delx<0) setzeDrehwinkel(90);

        if (dely>0) {
            if (delx>0) setzeDrehwinkel(315);
            else if (delx<0) setzeDrehwinkel(45);
            else setzeDrehwinkel(0);
        }else if (dely<0) {
            if (delx>0) setzeDrehwinkel(225);
            else if (delx<0) setzeDrehwinkel(135);
            else setzeDrehwinkel(180);
        }
        actor.setCenter(v1);
    }

/*
    @Override
    public void onKeyUp(KeyEvent e) {
        KeyListener.super.onKeyUp(e);
        int k = e.getKeyCode();
        if (k==KeyEvent.VK_W||k==KeyEvent.VK_S) dely = 0;
        if (k==KeyEvent.VK_A||k==KeyEvent.VK_D) delx = 0;
    }
*/



}

