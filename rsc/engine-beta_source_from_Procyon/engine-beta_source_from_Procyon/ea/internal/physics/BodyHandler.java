// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.physics;

import java.util.ArrayList;
import java.util.Iterator;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.collision.shapes.Shape;
import java.util.List;
import java.util.function.Supplier;
import org.jbox2d.collision.AABB;
import org.jbox2d.dynamics.Fixture;
import ea.Vector;
import ea.internal.annotations.Internal;
import ea.actor.Actor;
import ea.actor.BodyType;
import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class BodyHandler implements PhysicsHandler
{
    private static final Vec2 NULL_VECTOR;
    private static final int DEFAULT_MASK_BITS = 65535;
    private final WorldHandler worldHandler;
    private final Body body;
    private BodyType type;
    
    @Internal
    public BodyHandler(final Actor actor, final PhysicsData physicsData, final WorldHandler worldHandler) {
        this.worldHandler = worldHandler;
        this.body = physicsData.createBody(worldHandler, actor);
        this.setType(physicsData.getType());
    }
    
    @Override
    public Body getBody() {
        return this.body;
    }
    
    @Override
    public void moveBy(final Vector meters) {
        synchronized (this.worldHandler) {
            this.worldHandler.assertNoWorldStep();
            final Vec2 vector = meters.toVec2();
            this.body.setTransform(vector.addLocal(this.body.getPosition()), this.body.getAngle());
            this.body.setAwake(true);
        }
    }
    
    @Override
    public Vector getCenter() {
        if (this.type == BodyType.DYNAMIC || this.type == BodyType.PARTICLE) {
            return Vector.of(this.body.getWorldCenter());
        }
        return Vector.of(this.calculateBodyAABB().getCenter());
    }
    
    @Override
    public boolean contains(final Vector vector) {
        final Vec2 point = vector.toVec2();
        for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
            if (fixture.testPoint(point)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Vector getPosition() {
        return Vector.of(this.body.getPosition());
    }
    
    @Override
    public float getRotation() {
        return (float)Math.toDegrees(this.body.getAngle());
    }
    
    @Override
    public void rotateBy(final float degree) {
        synchronized (this.worldHandler) {
            this.worldHandler.assertNoWorldStep();
            this.body.setTransform(this.body.getPosition(), this.body.getAngle() + (float)Math.toRadians(degree));
        }
    }
    
    @Override
    public void setRotation(final float degree) {
        synchronized (this.worldHandler) {
            this.worldHandler.assertNoWorldStep();
            this.body.setTransform(this.body.getPosition(), (float)Math.toRadians(degree));
        }
    }
    
    @Override
    public void setDensity(final float density) {
        synchronized (this.worldHandler) {
            for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.setDensity(density);
            }
            this.body.resetMassData();
        }
    }
    
    @Override
    public float getDensity() {
        return this.body.m_fixtureList.getDensity();
    }
    
    @Override
    public void setFriction(final float friction) {
        synchronized (this.worldHandler) {
            for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.setFriction(friction);
            }
        }
    }
    
    @Override
    public float getFriction() {
        return this.body.m_fixtureList.getFriction();
    }
    
    @Override
    public void setRestitution(final float elasticity) {
        synchronized (this.worldHandler) {
            for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.setRestitution(elasticity);
            }
        }
    }
    
    @Override
    public float getRestitution() {
        return this.body.m_fixtureList.getRestitution();
    }
    
    @Override
    public float getMass() {
        return this.body.getMass();
    }
    
    @Override
    public void applyForce(final Vector force) {
        synchronized (this.worldHandler) {
            this.body.applyForceToCenter(force.toVec2());
        }
    }
    
    @Override
    public void applyTorque(final float torque) {
        synchronized (this.worldHandler) {
            this.body.applyTorque(torque);
        }
    }
    
    @Override
    public void applyRotationImpulse(final float rotationImpulse) {
        synchronized (this.worldHandler) {
            this.body.applyAngularImpulse(rotationImpulse);
        }
    }
    
    @Override
    public void setType(final BodyType type) {
        synchronized (this.worldHandler) {
            this.worldHandler.assertNoWorldStep();
            if (type == this.type) {
                return;
            }
            this.type = type;
            this.body.setType(type.toBox2D());
            this.body.setActive(true);
            this.body.setAwake(true);
            this.body.setGravityScale(type.getDefaultGravityScale());
            for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                fixture.m_isSensor = type.isSensorType();
                switch (type) {
                    case SENSOR: {
                        fixture.m_filter.categoryBits = 1;
                        fixture.m_filter.maskBits = 65527;
                        break;
                    }
                    case STATIC: {
                        fixture.m_filter.categoryBits = 2;
                        fixture.m_filter.maskBits = 65535;
                        break;
                    }
                    case DYNAMIC:
                    case KINEMATIC: {
                        fixture.m_filter.categoryBits = 4;
                        fixture.m_filter.maskBits = 65527;
                        break;
                    }
                    case PARTICLE: {
                        fixture.m_filter.categoryBits = 8;
                        fixture.m_filter.maskBits = 2;
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unknown body type: " + type);
                    }
                }
            }
        }
    }
    
    @Override
    public BodyType getType() {
        return this.type;
    }
    
    @Override
    public void applyForce(final Vector forceInN, final Vector globalLocation) {
        synchronized (this.worldHandler) {
            this.body.applyForce(forceInN.toVec2(), globalLocation.toVec2());
        }
    }
    
    @Override
    public void applyImpluse(final Vector impluseInNs, final Vector globalLocation) {
        synchronized (this.worldHandler) {
            this.body.applyLinearImpulse(impluseInNs.toVec2(), globalLocation.toVec2(), true);
        }
    }
    
    @Override
    public void resetMovement() {
        synchronized (this.worldHandler) {
            this.body.setLinearVelocity(BodyHandler.NULL_VECTOR);
            this.body.setAngularVelocity(0.0f);
        }
    }
    
    @Override
    public void setVelocity(final Vector metersPerSecond) {
        synchronized (this.worldHandler) {
            this.body.setLinearVelocity(metersPerSecond.toVec2());
        }
    }
    
    @Override
    public Vector getVelocity() {
        return Vector.of(this.body.getLinearVelocity());
    }
    
    @Override
    public void setAngularVelocity(final float rotationsPerSecond) {
        synchronized (this.worldHandler) {
            this.body.setAngularVelocity((float)Math.toRadians(rotationsPerSecond * 360.0f));
        }
    }
    
    @Override
    public float getAngularVelocity() {
        return (float)Math.toDegrees(this.body.getAngularVelocity()) / 360.0f;
    }
    
    @Override
    public void setRotationLocked(final boolean locked) {
        synchronized (this.worldHandler) {
            this.body.setFixedRotation(locked);
        }
    }
    
    @Override
    public boolean isRotationLocked() {
        return this.body.isFixedRotation();
    }
    
    private AABB calculateBodyAABB() {
        final AABB bodyBounds = new AABB();
        bodyBounds.lowerBound.x = Float.MAX_VALUE;
        bodyBounds.lowerBound.y = Float.MAX_VALUE;
        bodyBounds.upperBound.x = -3.4028235E38f;
        bodyBounds.upperBound.y = -3.4028235E38f;
        for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
            bodyBounds.combine(bodyBounds, fixture.getAABB(0));
        }
        return bodyBounds;
    }
    
    @Override
    public boolean isGrounded() {
        if (this.getType() != BodyType.DYNAMIC) {
            throw new RuntimeException("Der Steh-Test ist nur f\u00fcr dynamische Objekte definiert");
        }
        final AABB bodyBounds = this.calculateBodyAABB();
        final AABB testAABB = new AABB();
        final float epsilon = 1.0E-4f;
        testAABB.lowerBound.set(bodyBounds.lowerBound.x, bodyBounds.lowerBound.y);
        testAABB.upperBound.set(bodyBounds.upperBound.x, bodyBounds.lowerBound.y + 1.0E-4f);
        final Fixture[] queryAABB;
        final Fixture[] groundCandidates = queryAABB = this.worldHandler.queryAABB(testAABB);
        for (final Fixture fixture : queryAABB) {
            final Actor corresponding = this.worldHandler.lookupActor(fixture.m_body);
            if (corresponding != null && corresponding.getBodyType() == BodyType.STATIC) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setShapes(final Supplier<List<Shape>> shapes) {
        synchronized (this.worldHandler) {
            final PhysicsData physicsData = this.getPhysicsData();
            for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
                this.body.destroyFixture(fixture);
            }
            final FixtureDef fixtureDef = physicsData.createPlainFixtureDef();
            for (final Shape shape : shapes.get()) {
                fixtureDef.shape = shape;
                this.body.createFixture(fixtureDef);
            }
        }
    }
    
    @Internal
    @Override
    public PhysicsData getPhysicsData() {
        final List<Shape> shapeList = new ArrayList<Shape>();
        for (Fixture fixture = this.body.m_fixtureList; fixture != null; fixture = fixture.m_next) {
            shapeList.add(fixture.m_shape);
        }
        return PhysicsData.fromBody(this.body, () -> shapeList, this.getType());
    }
    
    @Override
    public void applyMountCallbacks(final PhysicsHandler otherHandler) {
    }
    
    @Override
    public WorldHandler getWorldHandler() {
        return this.worldHandler;
    }
    
    static {
        NULL_VECTOR = new Vec2();
    }
}
