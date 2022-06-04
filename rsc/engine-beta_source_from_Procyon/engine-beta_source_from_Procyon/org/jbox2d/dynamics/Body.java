// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Sweep;
import org.jbox2d.common.Transform;

public class Body
{
    public static final int e_islandFlag = 1;
    public static final int e_awakeFlag = 2;
    public static final int e_autoSleepFlag = 4;
    public static final int e_bulletFlag = 8;
    public static final int e_fixedRotationFlag = 16;
    public static final int e_activeFlag = 32;
    public static final int e_toiFlag = 64;
    public BodyType m_type;
    public int m_flags;
    public int m_islandIndex;
    public final Transform m_xf;
    public final Transform m_xf0;
    public final Sweep m_sweep;
    public final Vec2 m_linearVelocity;
    public float m_angularVelocity;
    public final Vec2 m_force;
    public float m_torque;
    public World m_world;
    public Body m_prev;
    public Body m_next;
    public Fixture m_fixtureList;
    public int m_fixtureCount;
    public JointEdge m_jointList;
    public ContactEdge m_contactList;
    public float m_mass;
    public float m_invMass;
    public float m_I;
    public float m_invI;
    public float m_linearDamping;
    public float m_angularDamping;
    public float m_gravityScale;
    public float m_sleepTime;
    public Object m_userData;
    private final FixtureDef fixDef;
    private final MassData pmd;
    private final Transform pxf;
    
    public Body(final BodyDef bd, final World world) {
        this.m_xf = new Transform();
        this.m_xf0 = new Transform();
        this.m_sweep = new Sweep();
        this.m_linearVelocity = new Vec2();
        this.m_angularVelocity = 0.0f;
        this.m_force = new Vec2();
        this.m_torque = 0.0f;
        this.fixDef = new FixtureDef();
        this.pmd = new MassData();
        this.pxf = new Transform();
        assert bd.position.isValid();
        assert bd.linearVelocity.isValid();
        assert bd.gravityScale >= 0.0f;
        assert bd.angularDamping >= 0.0f;
        assert bd.linearDamping >= 0.0f;
        this.m_flags = 0;
        if (bd.bullet) {
            this.m_flags |= 0x8;
        }
        if (bd.fixedRotation) {
            this.m_flags |= 0x10;
        }
        if (bd.allowSleep) {
            this.m_flags |= 0x4;
        }
        if (bd.awake) {
            this.m_flags |= 0x2;
        }
        if (bd.active) {
            this.m_flags |= 0x20;
        }
        this.m_world = world;
        this.m_xf.p.set(bd.position);
        this.m_xf.q.set(bd.angle);
        this.m_sweep.localCenter.setZero();
        this.m_sweep.c0.set(this.m_xf.p);
        this.m_sweep.c.set(this.m_xf.p);
        this.m_sweep.a0 = bd.angle;
        this.m_sweep.a = bd.angle;
        this.m_sweep.alpha0 = 0.0f;
        this.m_jointList = null;
        this.m_contactList = null;
        this.m_prev = null;
        this.m_next = null;
        this.m_linearVelocity.set(bd.linearVelocity);
        this.m_angularVelocity = bd.angularVelocity;
        this.m_linearDamping = bd.linearDamping;
        this.m_angularDamping = bd.angularDamping;
        this.m_gravityScale = bd.gravityScale;
        this.m_force.setZero();
        this.m_torque = 0.0f;
        this.m_sleepTime = 0.0f;
        this.m_type = bd.type;
        if (this.m_type == BodyType.DYNAMIC) {
            this.m_mass = 1.0f;
            this.m_invMass = 1.0f;
        }
        else {
            this.m_mass = 0.0f;
            this.m_invMass = 0.0f;
        }
        this.m_I = 0.0f;
        this.m_invI = 0.0f;
        this.m_userData = bd.userData;
        this.m_fixtureList = null;
        this.m_fixtureCount = 0;
    }
    
    public final Fixture createFixture(final FixtureDef def) {
        assert !this.m_world.isLocked();
        if (this.m_world.isLocked()) {
            return null;
        }
        final Fixture fixture = new Fixture();
        fixture.create(this, def);
        if ((this.m_flags & 0x20) == 0x20) {
            final BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
            fixture.createProxies(broadPhase, this.m_xf);
        }
        fixture.m_next = this.m_fixtureList;
        this.m_fixtureList = fixture;
        ++this.m_fixtureCount;
        fixture.m_body = this;
        if (fixture.m_density > 0.0f) {
            this.resetMassData();
        }
        final World world = this.m_world;
        world.m_flags |= 0x1;
        return fixture;
    }
    
    public final Fixture createFixture(final Shape shape, final float density) {
        this.fixDef.shape = shape;
        this.fixDef.density = density;
        return this.createFixture(this.fixDef);
    }
    
    public final void destroyFixture(Fixture fixture) {
        assert !this.m_world.isLocked();
        if (this.m_world.isLocked()) {
            return;
        }
        assert fixture.m_body == this;
        assert this.m_fixtureCount > 0;
        Fixture node = this.m_fixtureList;
        Fixture last = null;
        boolean found = false;
        while (node != null) {
            if (node == fixture) {
                node = fixture.m_next;
                found = true;
                break;
            }
            last = node;
            node = node.m_next;
        }
        assert found;
        if (last == null) {
            this.m_fixtureList = fixture.m_next;
        }
        else {
            last.m_next = fixture.m_next;
        }
        ContactEdge edge = this.m_contactList;
        while (edge != null) {
            final Contact c = edge.contact;
            edge = edge.next;
            final Fixture fixtureA = c.getFixtureA();
            final Fixture fixtureB = c.getFixtureB();
            if (fixture == fixtureA || fixture == fixtureB) {
                this.m_world.m_contactManager.destroy(c);
            }
        }
        if ((this.m_flags & 0x20) == 0x20) {
            final BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
            fixture.destroyProxies(broadPhase);
        }
        fixture.destroy();
        fixture.m_body = null;
        fixture.m_next = null;
        fixture = null;
        --this.m_fixtureCount;
        this.resetMassData();
    }
    
    public final void setTransform(final Vec2 position, final float angle) {
        assert !this.m_world.isLocked();
        if (this.m_world.isLocked()) {
            return;
        }
        this.m_xf.q.set(angle);
        this.m_xf.p.set(position);
        Transform.mulToOutUnsafe(this.m_xf, this.m_sweep.localCenter, this.m_sweep.c);
        this.m_sweep.a = angle;
        this.m_sweep.c0.set(this.m_sweep.c);
        this.m_sweep.a0 = this.m_sweep.a;
        final BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
        for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
            f.synchronize(broadPhase, this.m_xf, this.m_xf);
        }
    }
    
    public final Transform getTransform() {
        return this.m_xf;
    }
    
    public final Vec2 getPosition() {
        return this.m_xf.p;
    }
    
    public final float getAngle() {
        return this.m_sweep.a;
    }
    
    public final Vec2 getWorldCenter() {
        return this.m_sweep.c;
    }
    
    public final Vec2 getLocalCenter() {
        return this.m_sweep.localCenter;
    }
    
    public final void setLinearVelocity(final Vec2 v) {
        if (this.m_type == BodyType.STATIC) {
            return;
        }
        if (Vec2.dot(v, v) > 0.0f) {
            this.setAwake(true);
        }
        this.m_linearVelocity.set(v);
    }
    
    public final Vec2 getLinearVelocity() {
        return this.m_linearVelocity;
    }
    
    public final void setAngularVelocity(final float w) {
        if (this.m_type == BodyType.STATIC) {
            return;
        }
        if (w * w > 0.0f) {
            this.setAwake(true);
        }
        this.m_angularVelocity = w;
    }
    
    public final float getAngularVelocity() {
        return this.m_angularVelocity;
    }
    
    public float getGravityScale() {
        return this.m_gravityScale;
    }
    
    public void setGravityScale(final float gravityScale) {
        this.m_gravityScale = gravityScale;
    }
    
    public final void applyForce(final Vec2 force, final Vec2 point) {
        if (this.m_type != BodyType.DYNAMIC) {
            return;
        }
        if (!this.isAwake()) {
            this.setAwake(true);
        }
        final Vec2 force2 = this.m_force;
        force2.x += force.x;
        final Vec2 force3 = this.m_force;
        force3.y += force.y;
        this.m_torque += (point.x - this.m_sweep.c.x) * force.y - (point.y - this.m_sweep.c.y) * force.x;
    }
    
    public final void applyForceToCenter(final Vec2 force) {
        if (this.m_type != BodyType.DYNAMIC) {
            return;
        }
        if (!this.isAwake()) {
            this.setAwake(true);
        }
        final Vec2 force2 = this.m_force;
        force2.x += force.x;
        final Vec2 force3 = this.m_force;
        force3.y += force.y;
    }
    
    public final void applyTorque(final float torque) {
        if (this.m_type != BodyType.DYNAMIC) {
            return;
        }
        if (!this.isAwake()) {
            this.setAwake(true);
        }
        this.m_torque += torque;
    }
    
    public final void applyLinearImpulse(final Vec2 impulse, final Vec2 point, final boolean wake) {
        if (this.m_type != BodyType.DYNAMIC) {
            return;
        }
        if (!this.isAwake()) {
            if (!wake) {
                return;
            }
            this.setAwake(true);
        }
        final Vec2 linearVelocity = this.m_linearVelocity;
        linearVelocity.x += impulse.x * this.m_invMass;
        final Vec2 linearVelocity2 = this.m_linearVelocity;
        linearVelocity2.y += impulse.y * this.m_invMass;
        this.m_angularVelocity += this.m_invI * ((point.x - this.m_sweep.c.x) * impulse.y - (point.y - this.m_sweep.c.y) * impulse.x);
    }
    
    public void applyAngularImpulse(final float impulse) {
        if (this.m_type != BodyType.DYNAMIC) {
            return;
        }
        if (!this.isAwake()) {
            this.setAwake(true);
        }
        this.m_angularVelocity += this.m_invI * impulse;
    }
    
    public final float getMass() {
        return this.m_mass;
    }
    
    public final float getInertia() {
        return this.m_I + this.m_mass * (this.m_sweep.localCenter.x * this.m_sweep.localCenter.x + this.m_sweep.localCenter.y * this.m_sweep.localCenter.y);
    }
    
    public final void getMassData(final MassData data) {
        data.mass = this.m_mass;
        data.I = this.m_I + this.m_mass * (this.m_sweep.localCenter.x * this.m_sweep.localCenter.x + this.m_sweep.localCenter.y * this.m_sweep.localCenter.y);
        data.center.x = this.m_sweep.localCenter.x;
        data.center.y = this.m_sweep.localCenter.y;
    }
    
    public final void setMassData(final MassData massData) {
        assert !this.m_world.isLocked();
        if (this.m_world.isLocked()) {
            return;
        }
        if (this.m_type != BodyType.DYNAMIC) {
            return;
        }
        this.m_invMass = 0.0f;
        this.m_I = 0.0f;
        this.m_invI = 0.0f;
        this.m_mass = massData.mass;
        if (this.m_mass <= 0.0f) {
            this.m_mass = 1.0f;
        }
        this.m_invMass = 1.0f / this.m_mass;
        if (massData.I > 0.0f && (this.m_flags & 0x10) == 0x0) {
            this.m_I = massData.I - this.m_mass * Vec2.dot(massData.center, massData.center);
            assert this.m_I > 0.0f;
            this.m_invI = 1.0f / this.m_I;
        }
        final Vec2 oldCenter = this.m_world.getPool().popVec2();
        oldCenter.set(this.m_sweep.c);
        this.m_sweep.localCenter.set(massData.center);
        Transform.mulToOutUnsafe(this.m_xf, this.m_sweep.localCenter, this.m_sweep.c0);
        this.m_sweep.c.set(this.m_sweep.c0);
        final Vec2 temp = this.m_world.getPool().popVec2();
        temp.set(this.m_sweep.c).subLocal(oldCenter);
        Vec2.crossToOut(this.m_angularVelocity, temp, temp);
        this.m_linearVelocity.addLocal(temp);
        this.m_world.getPool().pushVec2(2);
    }
    
    public final void resetMassData() {
        this.m_mass = 0.0f;
        this.m_invMass = 0.0f;
        this.m_I = 0.0f;
        this.m_invI = 0.0f;
        this.m_sweep.localCenter.setZero();
        if (this.m_type == BodyType.STATIC || this.m_type == BodyType.KINEMATIC) {
            this.m_sweep.c0.set(this.m_xf.p);
            this.m_sweep.c.set(this.m_xf.p);
            this.m_sweep.a0 = this.m_sweep.a;
            return;
        }
        assert this.m_type == BodyType.DYNAMIC;
        final Vec2 localCenter = this.m_world.getPool().popVec2();
        localCenter.setZero();
        final Vec2 temp = this.m_world.getPool().popVec2();
        final MassData massData = this.pmd;
        for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
            if (f.m_density != 0.0f) {
                f.getMassData(massData);
                this.m_mass += massData.mass;
                temp.set(massData.center).mulLocal(massData.mass);
                localCenter.addLocal(temp);
                this.m_I += massData.I;
            }
        }
        if (this.m_mass > 0.0f) {
            localCenter.mulLocal(this.m_invMass = 1.0f / this.m_mass);
        }
        else {
            this.m_mass = 1.0f;
            this.m_invMass = 1.0f;
        }
        if (this.m_I > 0.0f && (this.m_flags & 0x10) == 0x0) {
            this.m_I -= this.m_mass * Vec2.dot(localCenter, localCenter);
            assert this.m_I > 0.0f;
            this.m_invI = 1.0f / this.m_I;
        }
        else {
            this.m_I = 0.0f;
            this.m_invI = 0.0f;
        }
        final Vec2 oldCenter = this.m_world.getPool().popVec2();
        oldCenter.set(this.m_sweep.c);
        this.m_sweep.localCenter.set(localCenter);
        Transform.mulToOutUnsafe(this.m_xf, this.m_sweep.localCenter, this.m_sweep.c0);
        this.m_sweep.c.set(this.m_sweep.c0);
        temp.set(this.m_sweep.c).subLocal(oldCenter);
        final Vec2 temp2 = oldCenter;
        Vec2.crossToOutUnsafe(this.m_angularVelocity, temp, temp2);
        this.m_linearVelocity.addLocal(temp2);
        this.m_world.getPool().pushVec2(3);
    }
    
    public final Vec2 getWorldPoint(final Vec2 localPoint) {
        final Vec2 v = new Vec2();
        this.getWorldPointToOut(localPoint, v);
        return v;
    }
    
    public final void getWorldPointToOut(final Vec2 localPoint, final Vec2 out) {
        Transform.mulToOut(this.m_xf, localPoint, out);
    }
    
    public final Vec2 getWorldVector(final Vec2 localVector) {
        final Vec2 out = new Vec2();
        this.getWorldVectorToOut(localVector, out);
        return out;
    }
    
    public final void getWorldVectorToOut(final Vec2 localVector, final Vec2 out) {
        Rot.mulToOut(this.m_xf.q, localVector, out);
    }
    
    public final void getWorldVectorToOutUnsafe(final Vec2 localVector, final Vec2 out) {
        Rot.mulToOutUnsafe(this.m_xf.q, localVector, out);
    }
    
    public final Vec2 getLocalPoint(final Vec2 worldPoint) {
        final Vec2 out = new Vec2();
        this.getLocalPointToOut(worldPoint, out);
        return out;
    }
    
    public final void getLocalPointToOut(final Vec2 worldPoint, final Vec2 out) {
        Transform.mulTransToOut(this.m_xf, worldPoint, out);
    }
    
    public final Vec2 getLocalVector(final Vec2 worldVector) {
        final Vec2 out = new Vec2();
        this.getLocalVectorToOut(worldVector, out);
        return out;
    }
    
    public final void getLocalVectorToOut(final Vec2 worldVector, final Vec2 out) {
        Rot.mulTrans(this.m_xf.q, worldVector, out);
    }
    
    public final void getLocalVectorToOutUnsafe(final Vec2 worldVector, final Vec2 out) {
        Rot.mulTransUnsafe(this.m_xf.q, worldVector, out);
    }
    
    public final Vec2 getLinearVelocityFromWorldPoint(final Vec2 worldPoint) {
        final Vec2 out = new Vec2();
        this.getLinearVelocityFromWorldPointToOut(worldPoint, out);
        return out;
    }
    
    public final void getLinearVelocityFromWorldPointToOut(final Vec2 worldPoint, final Vec2 out) {
        final float tempX = worldPoint.x - this.m_sweep.c.x;
        final float tempY = worldPoint.y - this.m_sweep.c.y;
        out.x = -this.m_angularVelocity * tempY + this.m_linearVelocity.x;
        out.y = this.m_angularVelocity * tempX + this.m_linearVelocity.y;
    }
    
    public final Vec2 getLinearVelocityFromLocalPoint(final Vec2 localPoint) {
        final Vec2 out = new Vec2();
        this.getLinearVelocityFromLocalPointToOut(localPoint, out);
        return out;
    }
    
    public final void getLinearVelocityFromLocalPointToOut(final Vec2 localPoint, final Vec2 out) {
        this.getWorldPointToOut(localPoint, out);
        this.getLinearVelocityFromWorldPointToOut(out, out);
    }
    
    public final float getLinearDamping() {
        return this.m_linearDamping;
    }
    
    public final void setLinearDamping(final float linearDamping) {
        this.m_linearDamping = linearDamping;
    }
    
    public final float getAngularDamping() {
        return this.m_angularDamping;
    }
    
    public final void setAngularDamping(final float angularDamping) {
        this.m_angularDamping = angularDamping;
    }
    
    public BodyType getType() {
        return this.m_type;
    }
    
    public void setType(final BodyType type) {
        assert !this.m_world.isLocked();
        if (this.m_world.isLocked()) {
            return;
        }
        if (this.m_type == type) {
            return;
        }
        this.m_type = type;
        this.resetMassData();
        if (this.m_type == BodyType.STATIC) {
            this.m_linearVelocity.setZero();
            this.m_angularVelocity = 0.0f;
            this.m_sweep.a0 = this.m_sweep.a;
            this.m_sweep.c0.set(this.m_sweep.c);
            this.synchronizeFixtures();
        }
        this.setAwake(true);
        this.m_force.setZero();
        this.m_torque = 0.0f;
        ContactEdge ce = this.m_contactList;
        while (ce != null) {
            final ContactEdge ce2 = ce;
            ce = ce.next;
            this.m_world.m_contactManager.destroy(ce2.contact);
        }
        this.m_contactList = null;
        final BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
        for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
            for (int proxyCount = f.m_proxyCount, i = 0; i < proxyCount; ++i) {
                broadPhase.touchProxy(f.m_proxies[i].proxyId);
            }
        }
    }
    
    public final boolean isBullet() {
        return (this.m_flags & 0x8) == 0x8;
    }
    
    public final void setBullet(final boolean flag) {
        if (flag) {
            this.m_flags |= 0x8;
        }
        else {
            this.m_flags &= 0xFFFFFFF7;
        }
    }
    
    public void setSleepingAllowed(final boolean flag) {
        if (flag) {
            this.m_flags |= 0x4;
        }
        else {
            this.m_flags &= 0xFFFFFFFB;
            this.setAwake(true);
        }
    }
    
    public boolean isSleepingAllowed() {
        return (this.m_flags & 0x4) == 0x4;
    }
    
    public void setAwake(final boolean flag) {
        if (flag) {
            if ((this.m_flags & 0x2) == 0x0) {
                this.m_flags |= 0x2;
                this.m_sleepTime = 0.0f;
            }
        }
        else {
            this.m_flags &= 0xFFFFFFFD;
            this.m_sleepTime = 0.0f;
            this.m_linearVelocity.setZero();
            this.m_angularVelocity = 0.0f;
            this.m_force.setZero();
            this.m_torque = 0.0f;
        }
    }
    
    public boolean isAwake() {
        return (this.m_flags & 0x2) == 0x2;
    }
    
    public void setActive(final boolean flag) {
        assert !this.m_world.isLocked();
        if (flag == this.isActive()) {
            return;
        }
        if (flag) {
            this.m_flags |= 0x20;
            final BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
            for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
                f.createProxies(broadPhase, this.m_xf);
            }
        }
        else {
            this.m_flags &= 0xFFFFFFDF;
            final BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
            for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
                f.destroyProxies(broadPhase);
            }
            ContactEdge ce = this.m_contactList;
            while (ce != null) {
                final ContactEdge ce2 = ce;
                ce = ce.next;
                this.m_world.m_contactManager.destroy(ce2.contact);
            }
            this.m_contactList = null;
        }
    }
    
    public boolean isActive() {
        return (this.m_flags & 0x20) == 0x20;
    }
    
    public void setFixedRotation(final boolean flag) {
        if (flag) {
            this.m_flags |= 0x10;
        }
        else {
            this.m_flags &= 0xFFFFFFEF;
        }
        this.resetMassData();
    }
    
    public boolean isFixedRotation() {
        return (this.m_flags & 0x10) == 0x10;
    }
    
    public final Fixture getFixtureList() {
        return this.m_fixtureList;
    }
    
    public final JointEdge getJointList() {
        return this.m_jointList;
    }
    
    public final ContactEdge getContactList() {
        return this.m_contactList;
    }
    
    public final Body getNext() {
        return this.m_next;
    }
    
    public final Object getUserData() {
        return this.m_userData;
    }
    
    public final void setUserData(final Object data) {
        this.m_userData = data;
    }
    
    public final World getWorld() {
        return this.m_world;
    }
    
    protected final void synchronizeFixtures() {
        final Transform xf1 = this.pxf;
        xf1.q.s = MathUtils.sin(this.m_sweep.a0);
        xf1.q.c = MathUtils.cos(this.m_sweep.a0);
        xf1.p.x = this.m_sweep.c0.x - xf1.q.c * this.m_sweep.localCenter.x + xf1.q.s * this.m_sweep.localCenter.y;
        xf1.p.y = this.m_sweep.c0.y - xf1.q.s * this.m_sweep.localCenter.x - xf1.q.c * this.m_sweep.localCenter.y;
        for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
            f.synchronize(this.m_world.m_contactManager.m_broadPhase, xf1, this.m_xf);
        }
    }
    
    public final void synchronizeTransform() {
        this.m_xf.q.s = MathUtils.sin(this.m_sweep.a);
        this.m_xf.q.c = MathUtils.cos(this.m_sweep.a);
        final Rot q = this.m_xf.q;
        final Vec2 v = this.m_sweep.localCenter;
        this.m_xf.p.x = this.m_sweep.c.x - q.c * v.x + q.s * v.y;
        this.m_xf.p.y = this.m_sweep.c.y - q.s * v.x - q.c * v.y;
    }
    
    public boolean shouldCollide(final Body other) {
        if (this.m_type != BodyType.DYNAMIC && other.m_type != BodyType.DYNAMIC) {
            return false;
        }
        for (JointEdge jn = this.m_jointList; jn != null; jn = jn.next) {
            if (jn.other == other && !jn.joint.getCollideConnected()) {
                return false;
            }
        }
        return true;
    }
    
    protected final void advance(final float t) {
        this.m_sweep.advance(t);
        this.m_sweep.c.set(this.m_sweep.c0);
        this.m_sweep.a = this.m_sweep.a0;
        this.m_xf.q.set(this.m_sweep.a);
        Rot.mulToOutUnsafe(this.m_xf.q, this.m_sweep.localCenter, this.m_xf.p);
        this.m_xf.p.mulLocal(-1.0f).addLocal(this.m_sweep.c);
    }
}
