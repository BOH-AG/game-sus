// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.physics;

import java.util.function.Supplier;
import org.jbox2d.dynamics.Body;
import ea.actor.BodyType;
import java.util.Iterator;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.AABB;
import ea.Vector;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.List;

public class NullHandler implements PhysicsHandler
{
    private final PhysicsData physicsData;
    private final List<Consumer<PhysicsHandler>> mountCallbacks;
    
    public NullHandler(final PhysicsData physicsData) {
        this.mountCallbacks = new ArrayList<Consumer<PhysicsHandler>>();
        this.physicsData = physicsData;
    }
    
    @Override
    public void moveBy(final Vector v) {
        this.physicsData.setX(this.physicsData.getX() + v.getX());
        this.physicsData.setY(this.physicsData.getY() + v.getY());
    }
    
    @Override
    public Vector getCenter() {
        AABB bounds = null;
        final AABB shapeBounds = new AABB();
        final Transform transform = new Transform();
        for (final Shape shape : this.physicsData.getShapes().get()) {
            transform.set(this.getPosition().toVec2(), (float)Math.toRadians(this.getRotation()));
            shape.computeAABB(shapeBounds, transform, 0);
            if (bounds != null) {
                bounds.combine(shapeBounds);
            }
            else {
                bounds = new AABB();
                bounds.set(shapeBounds);
            }
        }
        return Vector.of(bounds.getCenter());
    }
    
    @Override
    public boolean contains(final Vector p) {
        return false;
    }
    
    @Override
    public Vector getPosition() {
        return new Vector(this.physicsData.getX(), this.physicsData.getY());
    }
    
    @Override
    public float getRotation() {
        return this.physicsData.getRotation();
    }
    
    @Override
    public void rotateBy(final float degree) {
        this.physicsData.setRotation(this.physicsData.getRotation() + degree);
    }
    
    @Override
    public void setRotation(final float degree) {
        this.physicsData.setRotation(degree);
    }
    
    @Override
    public void setDensity(final float density) {
        if (density <= 0.0f) {
            throw new IllegalArgumentException("Dichte kann nicht kleiner als 0 sein. Eingabe war " + density + ".");
        }
        this.physicsData.setDensity(density);
    }
    
    @Override
    public float getDensity() {
        return this.physicsData.getDensity();
    }
    
    @Override
    public void setFriction(final float friction) {
        this.physicsData.setFriction(friction);
    }
    
    @Override
    public float getFriction() {
        return this.physicsData.getFriction();
    }
    
    @Override
    public void setRestitution(final float elasticity) {
        this.physicsData.setRestitution(elasticity);
    }
    
    @Override
    public float getRestitution() {
        return this.physicsData.getRestitution();
    }
    
    @Override
    public float getMass() {
        final Float mass = this.physicsData.getMass();
        return (mass == null) ? 0.0f : mass;
    }
    
    @Override
    public void applyForce(final Vector force) {
        this.mountCallbacks.add(physicsHandler -> physicsHandler.applyForce(force));
    }
    
    @Override
    public void applyTorque(final float torque) {
        this.mountCallbacks.add(physicsHandler -> physicsHandler.applyTorque(torque));
    }
    
    @Override
    public void applyRotationImpulse(final float rotationImpulse) {
        this.mountCallbacks.add(physicsHandler -> physicsHandler.applyRotationImpulse(rotationImpulse));
    }
    
    @Override
    public void setType(final BodyType type) {
        this.physicsData.setType(type);
    }
    
    @Override
    public BodyType getType() {
        return this.physicsData.getType();
    }
    
    @Override
    public void applyForce(final Vector force, final Vector globalLocation) {
        this.mountCallbacks.add(physicsHandler -> physicsHandler.applyForce(force, globalLocation));
    }
    
    @Override
    public void applyImpluse(final Vector impulse, final Vector globalLocation) {
        this.mountCallbacks.add(physicsHandler -> physicsHandler.applyImpluse(impulse, globalLocation));
    }
    
    @Override
    public WorldHandler getWorldHandler() {
        return null;
    }
    
    @Override
    public Body getBody() {
        return null;
    }
    
    @Override
    public void resetMovement() {
        this.physicsData.setVelocity(Vector.NULL);
        this.physicsData.setAngularVelocity(0.0f);
    }
    
    @Override
    public void setVelocity(final Vector metersPerSecond) {
        this.physicsData.setVelocity(metersPerSecond);
    }
    
    @Override
    public Vector getVelocity() {
        return this.physicsData.getVelocity();
    }
    
    @Override
    public void setAngularVelocity(final float rotationsPerSecond) {
        this.physicsData.setAngularVelocity((float)Math.toRadians(rotationsPerSecond * 360.0f));
    }
    
    @Override
    public float getAngularVelocity() {
        return this.physicsData.getAngularVelocity();
    }
    
    @Override
    public void setRotationLocked(final boolean locked) {
        this.physicsData.setRotationLocked(locked);
    }
    
    @Override
    public boolean isRotationLocked() {
        return this.physicsData.isRotationLocked();
    }
    
    @Override
    public boolean isGrounded() {
        return false;
    }
    
    @Override
    public void setShapes(final Supplier<List<Shape>> shapes) {
        this.physicsData.setShapes(shapes);
    }
    
    @Override
    public PhysicsData getPhysicsData() {
        return this.physicsData;
    }
    
    @Override
    public void applyMountCallbacks(final PhysicsHandler otherHandler) {
        for (final Consumer<PhysicsHandler> mountCallback : this.mountCallbacks) {
            mountCallback.accept(otherHandler);
        }
        this.mountCallbacks.clear();
    }
}
