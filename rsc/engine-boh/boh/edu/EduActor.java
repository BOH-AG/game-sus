// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import java.util.concurrent.ConcurrentHashMap;
import ea.collision.CollisionEvent;
import ea.animation.LineAnimation;
import ea.animation.CircleAnimation;
import ea.actor.BodyType;
import ea.edu.event.KollisionsReagierbar;
import ea.Vector;
import ea.internal.annotations.Internal;
import ea.FrameUpdateListener;
import ea.internal.annotations.API;
import ea.event.AggregateFrameUpdateListener;
import ea.edu.internal.EduScene;
import java.util.Map;
import ea.actor.Actor;

public abstract class EduActor<T extends Actor>
{
    private static final Map<Actor, EduActor> actorMap;
    private final T actor;
    private final EduScene eduScene;
    private boolean animationsPaused;
    private AggregateFrameUpdateListener lastAnimation;
    
    public EduActor(final T actor) {
        this.animationsPaused = false;
        this.lastAnimation = null;
        (this.actor = actor).setRotationLocked(true);
        this.actor.setRestitution(0.0f);
        this.actor.setCenter(0.0f, 0.0f);
        final EduActor<Actor> eduActor;
        this.actor.addMountListener(() -> eduActor = EduActor.actorMap.put(this.actor, this));
        final EduActor eduActor2;
        this.actor.addUnmountListener(() -> eduActor2 = EduActor.actorMap.remove(this.actor));
        EduSetup.setup((EduActor<Actor>)this, this.eduScene = EduSetup.getActiveScene());
    }
    
    @API
    protected final T getActor() {
        return this.actor;
    }
    
    @Internal
    private void addAnimation(final AggregateFrameUpdateListener animation) {
        animation.setPaused(this.animationsPaused);
        this.lastAnimation = animation;
        this.actor.addFrameUpdateListener(animation);
    }
    
    @API
    public void setzeTransparenz(final double transparenz) {
        if (transparenz < 0.0 || transparenz > 1.0) {
            throw new IllegalArgumentException("Fehlerhafte Transparenzeingabe. Muss zwischen 0 und 1 sein. War " + transparenz);
        }
        this.actor.setOpacity((float)(1.0 - transparenz));
    }
    
    @API
    public double nenneTransparenz() {
        return 1.0f - this.actor.getOpacity();
    }
    
    @API
    public void entfernen() {
        this.actor.remove();
    }
    
    @API
    public void verschieben(final double dX, final double dY) {
        this.actor.moveBy(new Vector(dX, dY));
    }
    
    @API
    public void drehen(final double grad) {
        this.actor.rotateBy((float)grad);
    }
    
    @API
    public void setzeDrehwinkel(final double grad) {
        this.actor.setRotation((float)grad);
    }
    
    @API
    public double nenneWinkelgeschwindigkeit() {
        return this.actor.getAngularVelocity();
    }
    
    @API
    public double nenneDrehwinkel() {
        return this.actor.getRotation();
    }
    
    @API
    public void setzeMittelpunkt(final double mX, final double mY) {
        this.actor.setCenter(new Vector(mX, mY));
    }
    
    @API
    public double nenneMittelpunktX() {
        return this.actor.getCenter().getX();
    }
    
    @API
    public double nenneMittelpunktY() {
        return this.actor.getCenter().getY();
    }
    
    @API
    public void setzeSichtbar(final boolean sichtbar) {
        this.actor.setVisible(sichtbar);
    }
    
    @API
    public boolean istSichtbar() {
        return this.actor.isVisible();
    }
    
    @API
    public boolean beinhaltetPunkt(final double pX, final double pY) {
        return this.actor.contains(new Vector(pX, pY));
    }
    
    @API
    public boolean schneidet(final EduActor objekt) {
        return objekt.actor.overlaps(this.getActor());
    }
    
    @API
    public <X extends EduActor> void registriereKollisionsReagierbar(final X anderer, final KollisionsReagierbar<X> kollisionsReagierbar) {
        this.actor.addCollisionListener(anderer.getActor(), collisionEvent -> {
            if (!kollisionsReagierbar.kollisionReagieren(anderer)) {
                collisionEvent.ignoreCollision();
            }
        });
    }
    
    @API
    public void registriereKollisionsReagierbar(final KollisionsReagierbar<EduActor> reagierbar) {
        final EduActor other;
        this.actor.addCollisionListener(collisionEvent -> {
            other = EduActor.actorMap.get(collisionEvent.getColliding());
            if (!reagierbar.kollisionReagieren(other)) {
                collisionEvent.ignoreCollision();
            }
        });
    }
    
    @API
    public void setzeRotationBlockiert(final boolean blockiert) {
        this.actor.setRotationLocked(blockiert);
    }
    
    @API
    public void wirkeImpuls(final double iX, final double iY) {
        this.actor.applyImpulse(new Vector(iX, iY));
    }
    
    @API
    public void setzeWinkelgeschwindigkeit(final double umdrehnungenProSekunde) {
        this.actor.setAngularVelocity((float)umdrehnungenProSekunde);
    }
    
    @API
    public void setzeReibung(final double reibungsKoeffizient) {
        this.actor.setFriction((float)reibungsKoeffizient);
    }
    
    @API
    public double nenneReibung() {
        return this.actor.getFriction();
    }
    
    @API
    public void setzeGeschwindigkeit(final double vX, final double vY) {
        this.actor.setVelocity(new Vector(vX, vY));
    }
    
    @API
    public double nenneGeschwindigkeitX() {
        return this.actor.getVelocity().getX();
    }
    
    @API
    public double nenneGeschwindigkeitY() {
        return this.actor.getVelocity().getY();
    }
    
    @API
    public void setzeElastizitaet(final double elastizitaetsKoeffizient) {
        this.actor.setRestitution((float)elastizitaetsKoeffizient);
    }
    
    @API
    public double nenneElastizitaet() {
        return this.actor.getRestitution();
    }
    
    @API
    public double nenneMasse() {
        return this.actor.getMass();
    }
    
    @API
    public void setzeDichte(final double dichteInKgProQM) {
        this.actor.setDensity((float)dichteInKgProQM);
    }
    
    @API
    public double nenneDichte() {
        return this.actor.getDensity();
    }
    
    @API
    public boolean steht() {
        return this.actor.isGrounded();
    }
    
    @API
    public boolean stehtAuf(final EduActor actor) {
        return this.actor.overlaps(actor.getActor()) && this.actor.isGrounded();
    }
    
    @Deprecated
    @API
    public void macheAktiv() {
        this.actor.setBodyType(BodyType.DYNAMIC);
    }
    
    @API
    public void macheDynamisch() {
        this.actor.setBodyType(BodyType.DYNAMIC);
    }
    
    @Deprecated
    @API
    public void machePassiv() {
        this.actor.setBodyType(BodyType.STATIC);
    }
    
    @API
    public void macheStatisch() {
        this.actor.setBodyType(BodyType.STATIC);
    }
    
    @Deprecated
    @API
    public void macheNeutral() {
        this.actor.setBodyType(BodyType.SENSOR);
    }
    
    @API
    public void macheSensor() {
        this.actor.setBodyType(BodyType.SENSOR);
    }
    
    @API
    public void macheKinematisch() {
        this.actor.setBodyType(BodyType.KINEMATIC);
    }
    
    @API
    public void machePartikel(final double lebenszeit) {
        this.actor.animateParticle((float)lebenszeit);
    }
    
    @API
    public void springe(final double staerke) {
        if (this.steht()) {
            this.actor.applyImpulse(new Vector(0.0, staerke * this.nenneMasse()));
        }
    }
    
    @API
    public void setzeEbenenposition(final int position) {
        this.actor.setLayerPosition(position);
    }
    
    @API
    public int nenneEbenenposition() {
        return this.actor.getLayerPosition();
    }
    
    @API
    public void animiereTransparenz(final double zeitInSekunden, final double nachTransparenz) {
        if (nachTransparenz < 0.0 || nachTransparenz > 1.0) {
            throw new IllegalArgumentException("Transparenzen m\u00fcssen stets zwischen 0 und 1 sein");
        }
        this.actor.animateOpacity((float)zeitInSekunden, (float)(1.0 - nachTransparenz));
    }
    
    @API
    public void animiereKreis(final double sekunden, final double mX, final double mY, final boolean uhrzeigersinn, final boolean rotation) {
        this.addAnimation(new CircleAnimation(this.getActor(), new Vector(mX, mY), (float)sekunden, uhrzeigersinn, rotation));
    }
    
    @API
    public void animiereGerade(final double sekunden, final double zX, final double zY, final boolean loop) {
        this.addAnimation(new LineAnimation(this.getActor(), new Vector(zX, zY), (float)sekunden, loop));
    }
    
    @API
    public void pausiereAnimation(final boolean pausiert) {
        this.animationsPaused = pausiert;
        if (this.lastAnimation != null) {
            this.lastAnimation.setPaused(pausiert);
        }
    }
    
    @API
    public boolean nennePausiert() {
        return this.animationsPaused;
    }
    
    @API
    public void setzeKollisionsformen(final String kollisionsFormenCode) {
        this.actor.setShapes(kollisionsFormenCode);
    }
    
    @API
    public void erzeugeStabverbindung(final EduActor anderer, final double aX, final double aY, final double bX, final double bY) {
        this.actor.createDistanceJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY));
    }
    
    @API
    public void erzeugeGelenkverbindung(final EduActor anderer, final double aX, final double aY) {
        this.actor.createRevoluteJoint(anderer.getActor(), new Vector(aX, aY));
    }
    
    @API
    public void erzeugeSeilverbindung(final EduActor anderer, final double laenge, final double aX, final double aY, final double bX, final double bY) {
        this.actor.createRopeJoint(anderer.getActor(), new Vector(aX, aY), new Vector(bX, bY), (float)laenge);
    }
    
    @API
    public void verzoegere(final double verzoegerungInSekunden, final Runnable runnable) {
        this.actor.delay((float)verzoegerungInSekunden, runnable);
    }
    
    static {
        actorMap = new ConcurrentHashMap<Actor, EduActor>();
    }
}
