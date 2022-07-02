import ea.Vector;
import ea.actor.StatefulAnimation;
import ea.edu.Bild;
import ea.edu.Figur;
import ea.edu.event.TastenReagierbar;
import ea.internal.ShapeBuilder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class player extends Figur implements TastenReagierbar, damage {

    StatefulAnimation<String> actor;
    double delx;
    double dely;
    boolean menu;
    public int fireRate;
    public double bulletSpread;
    int hp;
    final int maxhp;
    double speed;

    player(int fr, double bs,int Hp, int px, int py) {
        super("normal", "rsc/player-new.gif");
        actor = getActor();
        actor.setSize(2f, 2f);
        fireRate = fr;
        bulletSpread = bs;
        bloodPuddles = new ArrayList<>();
        speed = 7;
        //actor.setShape(createCircleSupplier(0.1f));
        //actor.setShape(createCircleSupplier(1));
        setzeMittelpunkt(px,py);
        setzeEbenenposition(10);
        maxhp = Hp;
        hp = Hp;
    }

    @Override
    public void tasteReagieren(int key) {
        if (key==KeyEvent.VK_A) {
            delx = -speed;
        }if (key==KeyEvent.VK_D) {
            delx = speed;
        }if (key==KeyEvent.VK_S) {
            dely = -speed;
        }if (key==KeyEvent.VK_W){
            dely = speed;
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

    @Override
    public void takeDamage(int d) {
        hp -= d;
        blood();
    }

    public void heal(int h) {
        if ((hp+h)>maxhp) {
            hp = maxhp;
        } else {
            hp += h;
        }
    }

    List<Bild> bloodPuddles;
    private void blood() {
        bloodPuddles.add(new Bild(2.2,2.2, "rsc/blood.png"));
        Bild b = bloodPuddles.get(bloodPuddles.size()-1);
        b.setzeDrehwinkel(ThreadLocalRandom.current().nextInt(360));
        b.setzeMittelpunkt((float)nenneMittelpunktX(), (float)nenneMittelpunktY());
        b.setzeEbenenposition(2);
        b.verzoegere(10, b::entfernen);
    }

    public int getHealth() {
        return hp;
    }


    private Supplier<Shape> createCircleSupplier(float diameter) {
        return () -> {
            return (Shape) ShapeBuilder.createCircleShape(0,0,diameter);
        };
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

