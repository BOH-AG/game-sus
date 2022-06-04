// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.physics;

import org.jbox2d.collision.shapes.Shape;
import java.util.List;
import java.util.function.Supplier;
import org.jbox2d.dynamics.Body;
import ea.actor.BodyType;
import ea.Vector;
import ea.internal.annotations.Internal;

@Internal
public interface PhysicsHandler
{
    @Internal
    void moveBy(final Vector p0);
    
    @Internal
    Vector getCenter();
    
    @Internal
    boolean contains(final Vector p0);
    
    @Internal
    Vector getPosition();
    
    @Internal
    float getRotation();
    
    @Internal
    void rotateBy(final float p0);
    
    @Internal
    void setRotation(final float p0);
    
    @Internal
    void setDensity(final float p0);
    
    @Internal
    float getDensity();
    
    @Internal
    void setFriction(final float p0);
    
    @Internal
    float getFriction();
    
    @Internal
    void setRestitution(final float p0);
    
    @Internal
    float getRestitution();
    
    @Internal
    float getMass();
    
    @Internal
    void applyForce(final Vector p0);
    
    @Internal
    void applyTorque(final float p0);
    
    @Internal
    void applyRotationImpulse(final float p0);
    
    @Internal
    void setType(final BodyType p0);
    
    @Internal
    BodyType getType();
    
    @Internal
    void applyForce(final Vector p0, final Vector p1);
    
    @Internal
    void applyImpluse(final Vector p0, final Vector p1);
    
    @Internal
    WorldHandler getWorldHandler();
    
    @Internal
    Body getBody();
    
    @Internal
    void resetMovement();
    
    @Internal
    void setVelocity(final Vector p0);
    
    @Internal
    Vector getVelocity();
    
    @Internal
    void setAngularVelocity(final float p0);
    
    @Internal
    float getAngularVelocity();
    
    @Internal
    void setRotationLocked(final boolean p0);
    
    @Internal
    boolean isRotationLocked();
    
    @Internal
    boolean isGrounded();
    
    @Internal
    void setShapes(final Supplier<List<Shape>> p0);
    
    @Internal
    PhysicsData getPhysicsData();
    
    void applyMountCallbacks(final PhysicsHandler p0);
}
