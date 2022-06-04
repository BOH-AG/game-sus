// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.physics;

import ea.actor.Actor;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import java.util.Iterator;
import java.util.ArrayList;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.collision.shapes.Shape;
import java.util.List;
import java.util.function.Supplier;
import ea.Vector;
import ea.actor.BodyType;
import ea.internal.annotations.Internal;

@Internal
public class PhysicsData
{
    private static final float DEFAULT_DENSITY = 10.0f;
    private static final float DEFAULT_FRICTION = 0.0f;
    private static final float DEFAULT_RESTITUTION = 0.5f;
    private static final BodyType DEFAULT_BODY_TYPE;
    private boolean rotationLocked;
    private float x;
    private float y;
    private float rotation;
    private float density;
    private float friction;
    private float restitution;
    private float torque;
    private float angularVelocity;
    private Float mass;
    private Vector velocity;
    private BodyType type;
    private Supplier<List<Shape>> shapes;
    
    public static PhysicsData fromBody(final Body body, final Supplier<List<Shape>> shapes, final BodyType type) {
        final PhysicsData data = new PhysicsData(shapes);
        data.setRotationLocked(body.isFixedRotation());
        data.setDensity(body.m_fixtureList.m_density);
        data.setFriction(body.m_fixtureList.m_friction);
        data.setRestitution(body.m_fixtureList.m_restitution);
        data.setX(body.getPosition().x);
        data.setY(body.getPosition().y);
        data.setRotation((float)Math.toDegrees(body.getAngle()));
        data.setTorque(body.m_torque);
        data.setVelocity(Vector.of(body.m_linearVelocity));
        data.setAngularVelocity((float)Math.toDegrees(body.m_angularVelocity) / 360.0f);
        data.setType(type);
        data.setShapes(shapes);
        return data;
    }
    
    public PhysicsData(final Supplier<List<Shape>> shapes) {
        this.rotationLocked = false;
        this.x = 0.0f;
        this.y = 0.0f;
        this.rotation = 0.0f;
        this.density = 10.0f;
        this.friction = 0.0f;
        this.restitution = 0.5f;
        this.torque = 0.0f;
        this.angularVelocity = 0.0f;
        this.velocity = Vector.NULL;
        this.type = PhysicsData.DEFAULT_BODY_TYPE;
        this.setShapes(shapes);
    }
    
    public FixtureDef[] createFixtureDefs() {
        final List<FixtureDef> fixtureDefs = new ArrayList<FixtureDef>();
        final List<Shape> shapeList = this.getShapes().get();
        for (final Shape shape : shapeList) {
            final FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = this.getDensity();
            fixtureDef.friction = this.getFriction();
            fixtureDef.restitution = this.getRestitution();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = this.getType().isSensorType();
            fixtureDefs.add(fixtureDef);
        }
        return fixtureDefs.toArray(new FixtureDef[0]);
    }
    
    public FixtureDef createPlainFixtureDef() {
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = this.getDensity();
        fixtureDef.friction = this.getFriction();
        fixtureDef.restitution = this.getRestitution();
        fixtureDef.isSensor = this.getType().isSensorType();
        return fixtureDef;
    }
    
    public BodyDef createBodyDef() {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.angle = (float)Math.toRadians(this.getRotation());
        bodyDef.position.set(new Vec2(this.getX(), this.getY()));
        bodyDef.fixedRotation = this.isRotationLocked();
        bodyDef.linearVelocity = this.getVelocity().toVec2();
        bodyDef.angularVelocity = (float)Math.toRadians(this.getAngularVelocity() * 360.0f);
        bodyDef.type = this.getType().toBox2D();
        bodyDef.active = true;
        bodyDef.gravityScale = this.getType().getDefaultGravityScale();
        return bodyDef;
    }
    
    Body createBody(final WorldHandler world, final Actor actor) {
        final Body body = world.createBody(this.createBodyDef(), actor);
        for (final FixtureDef fixtureDef : this.createFixtureDefs()) {
            body.createFixture(fixtureDef);
        }
        return body;
    }
    
    public void setMass(final Float mass) {
        this.mass = mass;
    }
    
    public Float getMass() {
        return this.mass;
    }
    
    public boolean isRotationLocked() {
        return this.rotationLocked;
    }
    
    public void setRotationLocked(final boolean rotationLocked) {
        this.rotationLocked = rotationLocked;
    }
    
    public float getX() {
        return this.x;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public float getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final float rotation) {
        this.rotation = rotation;
    }
    
    public float getDensity() {
        return this.density;
    }
    
    public void setDensity(final float density) {
        this.density = density;
    }
    
    public float getFriction() {
        return this.friction;
    }
    
    public void setFriction(final float friction) {
        this.friction = friction;
    }
    
    public float getRestitution() {
        return this.restitution;
    }
    
    public void setRestitution(final float restitution) {
        this.restitution = restitution;
    }
    
    public float getTorque() {
        return this.torque;
    }
    
    public void setTorque(final float torque) {
        this.torque = torque;
    }
    
    public Vector getVelocity() {
        return this.velocity;
    }
    
    public void setVelocity(final Vector velocity) {
        this.velocity = velocity;
    }
    
    public BodyType getType() {
        return this.type;
    }
    
    public void setType(final BodyType type) {
        this.type = type;
    }
    
    public Supplier<List<Shape>> getShapes() {
        return this.shapes;
    }
    
    public void setShapes(final Supplier<List<Shape>> shapes) {
        this.shapes = shapes;
    }
    
    public float getAngularVelocity() {
        return this.angularVelocity;
    }
    
    public void setAngularVelocity(final float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    static {
        DEFAULT_BODY_TYPE = BodyType.SENSOR;
    }
}
