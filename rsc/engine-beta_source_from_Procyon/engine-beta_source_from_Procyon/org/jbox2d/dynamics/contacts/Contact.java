// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.common.MathUtils;
import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.ManifoldPoint;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;

public abstract class Contact
{
    public static final int ISLAND_FLAG = 1;
    public static final int TOUCHING_FLAG = 2;
    public static final int ENABLED_FLAG = 4;
    public static final int FILTER_FLAG = 8;
    public static final int BULLET_HIT_FLAG = 16;
    public static final int TOI_FLAG = 32;
    public int m_flags;
    public Contact m_prev;
    public Contact m_next;
    public ContactEdge m_nodeA;
    public ContactEdge m_nodeB;
    public Fixture m_fixtureA;
    public Fixture m_fixtureB;
    public int m_indexA;
    public int m_indexB;
    public final Manifold m_manifold;
    public float m_toiCount;
    public float m_toi;
    public float m_friction;
    public float m_restitution;
    public float m_tangentSpeed;
    protected final IWorldPool pool;
    private final Manifold oldManifold;
    
    protected Contact(final IWorldPool argPool) {
        this.m_nodeA = null;
        this.m_nodeB = null;
        this.oldManifold = new Manifold();
        this.m_fixtureA = null;
        this.m_fixtureB = null;
        this.m_nodeA = new ContactEdge();
        this.m_nodeB = new ContactEdge();
        this.m_manifold = new Manifold();
        this.pool = argPool;
    }
    
    public void init(final Fixture fA, final int indexA, final Fixture fB, final int indexB) {
        this.m_flags = 4;
        this.m_fixtureA = fA;
        this.m_fixtureB = fB;
        this.m_indexA = indexA;
        this.m_indexB = indexB;
        this.m_manifold.pointCount = 0;
        this.m_prev = null;
        this.m_next = null;
        this.m_nodeA.contact = null;
        this.m_nodeA.prev = null;
        this.m_nodeA.next = null;
        this.m_nodeA.other = null;
        this.m_nodeB.contact = null;
        this.m_nodeB.prev = null;
        this.m_nodeB.next = null;
        this.m_nodeB.other = null;
        this.m_toiCount = 0.0f;
        this.m_friction = mixFriction(fA.m_friction, fB.m_friction);
        this.m_restitution = mixRestitution(fA.m_restitution, fB.m_restitution);
        this.m_tangentSpeed = 0.0f;
    }
    
    public Manifold getManifold() {
        return this.m_manifold;
    }
    
    public void getWorldManifold(final WorldManifold worldManifold) {
        final Body bodyA = this.m_fixtureA.getBody();
        final Body bodyB = this.m_fixtureB.getBody();
        final Shape shapeA = this.m_fixtureA.getShape();
        final Shape shapeB = this.m_fixtureB.getShape();
        worldManifold.initialize(this.m_manifold, bodyA.getTransform(), shapeA.m_radius, bodyB.getTransform(), shapeB.m_radius);
    }
    
    public boolean isTouching() {
        return (this.m_flags & 0x2) == 0x2;
    }
    
    public void setEnabled(final boolean flag) {
        if (flag) {
            this.m_flags |= 0x4;
        }
        else {
            this.m_flags &= 0xFFFFFFFB;
        }
    }
    
    public boolean isEnabled() {
        return (this.m_flags & 0x4) == 0x4;
    }
    
    public Contact getNext() {
        return this.m_next;
    }
    
    public Fixture getFixtureA() {
        return this.m_fixtureA;
    }
    
    public int getChildIndexA() {
        return this.m_indexA;
    }
    
    public Fixture getFixtureB() {
        return this.m_fixtureB;
    }
    
    public int getChildIndexB() {
        return this.m_indexB;
    }
    
    public void setFriction(final float friction) {
        this.m_friction = friction;
    }
    
    public float getFriction() {
        return this.m_friction;
    }
    
    public void resetFriction() {
        this.m_friction = mixFriction(this.m_fixtureA.m_friction, this.m_fixtureB.m_friction);
    }
    
    public void setRestitution(final float restitution) {
        this.m_restitution = restitution;
    }
    
    public float getRestitution() {
        return this.m_restitution;
    }
    
    public void resetRestitution() {
        this.m_restitution = mixRestitution(this.m_fixtureA.m_restitution, this.m_fixtureB.m_restitution);
    }
    
    public void setTangentSpeed(final float speed) {
        this.m_tangentSpeed = speed;
    }
    
    public float getTangentSpeed() {
        return this.m_tangentSpeed;
    }
    
    public abstract void evaluate(final Manifold p0, final Transform p1, final Transform p2);
    
    public void flagForFiltering() {
        this.m_flags |= 0x8;
    }
    
    public void update(final ContactListener listener) {
        this.oldManifold.set(this.m_manifold);
        this.m_flags |= 0x4;
        boolean touching = false;
        final boolean wasTouching = (this.m_flags & 0x2) == 0x2;
        final boolean sensorA = this.m_fixtureA.isSensor();
        final boolean sensorB = this.m_fixtureB.isSensor();
        final boolean sensor = sensorA || sensorB;
        final Body bodyA = this.m_fixtureA.getBody();
        final Body bodyB = this.m_fixtureB.getBody();
        final Transform xfA = bodyA.getTransform();
        final Transform xfB = bodyB.getTransform();
        if (sensor) {
            final Shape shapeA = this.m_fixtureA.getShape();
            final Shape shapeB = this.m_fixtureB.getShape();
            touching = this.pool.getCollision().testOverlap(shapeA, this.m_indexA, shapeB, this.m_indexB, xfA, xfB);
            this.m_manifold.pointCount = 0;
        }
        else {
            this.evaluate(this.m_manifold, xfA, xfB);
            touching = (this.m_manifold.pointCount > 0);
            for (int i = 0; i < this.m_manifold.pointCount; ++i) {
                final ManifoldPoint mp2 = this.m_manifold.points[i];
                mp2.normalImpulse = 0.0f;
                mp2.tangentImpulse = 0.0f;
                final ContactID id2 = mp2.id;
                for (int j = 0; j < this.oldManifold.pointCount; ++j) {
                    final ManifoldPoint mp3 = this.oldManifold.points[j];
                    if (mp3.id.isEqual(id2)) {
                        mp2.normalImpulse = mp3.normalImpulse;
                        mp2.tangentImpulse = mp3.tangentImpulse;
                        break;
                    }
                }
            }
            if (touching != wasTouching) {
                bodyA.setAwake(true);
                bodyB.setAwake(true);
            }
        }
        if (touching) {
            this.m_flags |= 0x2;
        }
        else {
            this.m_flags &= 0xFFFFFFFD;
        }
        if (listener == null) {
            return;
        }
        if (!wasTouching && touching) {
            listener.beginContact(this);
        }
        if (wasTouching && !touching) {
            listener.endContact(this);
        }
        if (!sensor && touching) {
            listener.preSolve(this, this.oldManifold);
        }
    }
    
    public static final float mixFriction(final float friction1, final float friction2) {
        return MathUtils.sqrt(friction1 * friction2);
    }
    
    public static final float mixRestitution(final float restitution1, final float restitution2) {
        return (restitution1 > restitution2) ? restitution1 : restitution2;
    }
}
