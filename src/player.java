import ea.Vector;
import ea.actor.StatefulAnimation;
import ea.edu.EduActor;
import ea.edu.Figur;
import ea.edu.Kreis;
import ea.edu.event.KollisionsReagierbar;
import ea.edu.event.TastenReagierbar;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Supplier;
import ea.internal.ShapeBuilder;

public class player extends Figur implements TastenReagierbar {

    StatefulAnimation<String> actor;
    double delx;
    double dely;
    boolean menu;
    public int fireRate;
    public double bulletSpread;

    player(int fr, double bs) {
        super("normal", "rsc/player-new.gif");
        actor = getActor();
        actor.setSize(2f, 2f);
        fireRate = fr;
        bulletSpread = bs;
        //actor.setShape(createCircleSupplier(0.1f));
        //actor.setShape(createCircleSupplier(1));
        setzeMittelpunkt(0,0);
        setzeEbenenposition(10);
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
    private Supplier<Shape> createCircleSupplier(float diameter) {
        return () -> {
            return ShapeBuilder.createCircleShape(0,0,diameter);
        };
    }

     */



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

