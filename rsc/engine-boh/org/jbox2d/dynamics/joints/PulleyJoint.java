// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Rot;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.common.Vec2;

public class PulleyJoint extends Joint
{
    public static final float MIN_PULLEY_LENGTH = 2.0f;
    private final Vec2 m_groundAnchorA;
    private final Vec2 m_groundAnchorB;
    private float m_lengthA;
    private float m_lengthB;
    private final Vec2 m_localAnchorA;
    private final Vec2 m_localAnchorB;
    private float m_constant;
    private float m_ratio;
    private float m_impulse;
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_uA;
    private final Vec2 m_uB;
    private final Vec2 m_rA;
    private final Vec2 m_rB;
    private final Vec2 m_localCenterA;
    private final Vec2 m_localCenterB;
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private float m_mass;
    
    protected PulleyJoint(final IWorldPool argWorldPool, final PulleyJointDef def) {
        super(argWorldPool, def);
        this.m_groundAnchorA = new Vec2();
        this.m_groundAnchorB = new Vec2();
        this.m_localAnchorA = new Vec2();
        this.m_localAnchorB = new Vec2();
        this.m_uA = new Vec2();
        this.m_uB = new Vec2();
        this.m_rA = new Vec2();
        this.m_rB = new Vec2();
        this.m_localCenterA = new Vec2();
        this.m_localCenterB = new Vec2();
        this.m_groundAnchorA.set(def.groundAnchorA);
        this.m_groundAnchorB.set(def.groundAnchorB);
        this.m_localAnchorA.set(def.localAnchorA);
        this.m_localAnchorB.set(def.localAnchorB);
        assert def.ratio != 0.0f;
        this.m_ratio = def.ratio;
        this.m_lengthA = def.lengthA;
        this.m_lengthB = def.lengthB;
        this.m_constant = def.lengthA + this.m_ratio * def.lengthB;
        this.m_impulse = 0.0f;
    }
    
    public float getLengthA() {
        return this.m_lengthA;
    }
    
    public float getLengthB() {
        return this.m_lengthB;
    }
    
    public float getCurrentLengthA() {
        final Vec2 p = this.pool.popVec2();
        this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, p);
        p.subLocal(this.m_groundAnchorA);
        final float length = p.length();
        this.pool.pushVec2(1);
        return length;
    }
    
    public float getCurrentLengthB() {
        final Vec2 p = this.pool.popVec2();
        this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, p);
        p.subLocal(this.m_groundAnchorB);
        final float length = p.length();
        this.pool.pushVec2(1);
        return length;
    }
    
    public Vec2 getLocalAnchorA() {
        return this.m_localAnchorA;
    }
    
    public Vec2 getLocalAnchorB() {
        return this.m_localAnchorB;
    }
    
    @Override
    public void getAnchorA(final Vec2 argOut) {
        this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, argOut);
    }
    
    @Override
    public void getAnchorB(final Vec2 argOut) {
        this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, argOut);
    }
    
    @Override
    public void getReactionForce(final float inv_dt, final Vec2 argOut) {
        argOut.set(this.m_uB).mulLocal(this.m_impulse).mulLocal(inv_dt);
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return 0.0f;
    }
    
    public Vec2 getGroundAnchorA() {
        return this.m_groundAnchorA;
    }
    
    public Vec2 getGroundAnchorB() {
        return this.m_groundAnchorB;
    }
    
    public float getLength1() {
        final Vec2 p = this.pool.popVec2();
        this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, p);
        p.subLocal(this.m_groundAnchorA);
        final float len = p.length();
        this.pool.pushVec2(1);
        return len;
    }
    
    public float getLength2() {
        final Vec2 p = this.pool.popVec2();
        this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, p);
        p.subLocal(this.m_groundAnchorB);
        final float len = p.length();
        this.pool.pushVec2(1);
        return len;
    }
    
    public float getRatio() {
        return this.m_ratio;
    }
    
    @Override
    public void initVelocityConstraints(final SolverData data) {
        this.m_indexA = this.m_bodyA.m_islandIndex;
        this.m_indexB = this.m_bodyB.m_islandIndex;
        this.m_localCenterA.set(this.m_bodyA.m_sweep.localCenter);
        this.m_localCenterB.set(this.m_bodyB.m_sweep.localCenter);
        this.m_invMassA = this.m_bodyA.m_invMass;
        this.m_invMassB = this.m_bodyB.m_invMass;
        this.m_invIA = this.m_bodyA.m_invI;
        this.m_invIB = this.m_bodyB.m_invI;
        final Vec2 cA = data.positions[this.m_indexA].c;
        final float aA = data.positions[this.m_indexA].a;
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
        final Vec2 cB = data.positions[this.m_indexB].c;
        final float aB = data.positions[this.m_indexB].a;
        final Vec2 vB = data.velocities[this.m_indexB].v;
        float wB = data.velocities[this.m_indexB].w;
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Vec2 temp = this.pool.popVec2();
        qA.set(aA);
        qB.set(aB);
        Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), this.m_rA);
        Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.m_rB);
        this.m_uA.set(cA).addLocal(this.m_rA).subLocal(this.m_groundAnchorA);
        this.m_uB.set(cB).addLocal(this.m_rB).subLocal(this.m_groundAnchorB);
        final float lengthA = this.m_uA.length();
        final float lengthB = this.m_uB.length();
        if (lengthA > 10.0f * Settings.linearSlop) {
            this.m_uA.mulLocal(1.0f / lengthA);
        }
        else {
            this.m_uA.setZero();
        }
        if (lengthB > 10.0f * Settings.linearSlop) {
            this.m_uB.mulLocal(1.0f / lengthB);
        }
        else {
            this.m_uB.setZero();
        }
        final float ruA = Vec2.cross(this.m_rA, this.m_uA);
        final float ruB = Vec2.cross(this.m_rB, this.m_uB);
        final float mA = this.m_invMassA + this.m_invIA * ruA * ruA;
        final float mB = this.m_invMassB + this.m_invIB * ruB * ruB;
        this.m_mass = mA + this.m_ratio * this.m_ratio * mB;
        if (this.m_mass > 0.0f) {
            this.m_mass = 1.0f / this.m_mass;
        }
        if (data.step.warmStarting) {
            this.m_impulse *= data.step.dtRatio;
            final Vec2 PA = this.pool.popVec2();
            final Vec2 PB = this.pool.popVec2();
            PA.set(this.m_uA).mulLocal(-this.m_impulse);
            PB.set(this.m_uB).mulLocal(-this.m_ratio * this.m_impulse);
            final Vec2 vec2 = vA;
            vec2.x += this.m_invMassA * PA.x;
            final Vec2 vec3 = vA;
            vec3.y += this.m_invMassA * PA.y;
            wA += this.m_invIA * Vec2.cross(this.m_rA, PA);
            final Vec2 vec4 = vB;
            vec4.x += this.m_invMassB * PB.x;
            final Vec2 vec5 = vB;
            vec5.y += this.m_invMassB * PB.y;
            wB += this.m_invIB * Vec2.cross(this.m_rB, PB);
            this.pool.pushVec2(2);
        }
        else {
            this.m_impulse = 0.0f;
        }
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushVec2(1);
        this.pool.pushRot(2);
    }
    
    @Override
    public void solveVelocityConstraints(final SolverData data) {
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
        final Vec2 vB = data.velocities[this.m_indexB].v;
        float wB = data.velocities[this.m_indexB].w;
        final Vec2 vpA = this.pool.popVec2();
        final Vec2 vpB = this.pool.popVec2();
        final Vec2 PA = this.pool.popVec2();
        final Vec2 PB = this.pool.popVec2();
        Vec2.crossToOutUnsafe(wA, this.m_rA, vpA);
        vpA.addLocal(vA);
        Vec2.crossToOutUnsafe(wB, this.m_rB, vpB);
        vpB.addLocal(vB);
        final float Cdot = -Vec2.dot(this.m_uA, vpA) - this.m_ratio * Vec2.dot(this.m_uB, vpB);
        final float impulse = -this.m_mass * Cdot;
        this.m_impulse += impulse;
        PA.set(this.m_uA).mulLocal(-impulse);
        PB.set(this.m_uB).mulLocal(-this.m_ratio * impulse);
        final Vec2 vec2 = vA;
        vec2.x += this.m_invMassA * PA.x;
        final Vec2 vec3 = vA;
        vec3.y += this.m_invMassA * PA.y;
        wA += this.m_invIA * Vec2.cross(this.m_rA, PA);
        final Vec2 vec4 = vB;
        vec4.x += this.m_invMassB * PB.x;
        final Vec2 vec5 = vB;
        vec5.y += this.m_invMassB * PB.y;
        wB += this.m_invIB * Vec2.cross(this.m_rB, PB);
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushVec2(4);
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        final Vec2 uA = this.pool.popVec2();
        final Vec2 uB = this.pool.popVec2();
        final Vec2 temp = this.pool.popVec2();
        final Vec2 PA = this.pool.popVec2();
        final Vec2 PB = this.pool.popVec2();
        final Vec2 cA = data.positions[this.m_indexA].c;
        float aA = data.positions[this.m_indexA].a;
        final Vec2 cB = data.positions[this.m_indexB].c;
        float aB = data.positions[this.m_indexB].a;
        qA.set(aA);
        qB.set(aB);
        Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
        Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
        uA.set(cA).addLocal(rA).subLocal(this.m_groundAnchorA);
        uB.set(cB).addLocal(rB).subLocal(this.m_groundAnchorB);
        final float lengthA = uA.length();
        final float lengthB = uB.length();
        if (lengthA > 10.0f * Settings.linearSlop) {
            uA.mulLocal(1.0f / lengthA);
        }
        else {
            uA.setZero();
        }
        if (lengthB > 10.0f * Settings.linearSlop) {
            uB.mulLocal(1.0f / lengthB);
        }
        else {
            uB.setZero();
        }
        final float ruA = Vec2.cross(rA, uA);
        final float ruB = Vec2.cross(rB, uB);
        final float mA = this.m_invMassA + this.m_invIA * ruA * ruA;
        final float mB = this.m_invMassB + this.m_invIB * ruB * ruB;
        float mass = mA + this.m_ratio * this.m_ratio * mB;
        if (mass > 0.0f) {
            mass = 1.0f / mass;
        }
        final float C = this.m_constant - lengthA - this.m_ratio * lengthB;
        final float linearError = MathUtils.abs(C);
        final float impulse = -mass * C;
        PA.set(uA).mulLocal(-impulse);
        PB.set(uB).mulLocal(-this.m_ratio * impulse);
        final Vec2 vec2 = cA;
        vec2.x += this.m_invMassA * PA.x;
        final Vec2 vec3 = cA;
        vec3.y += this.m_invMassA * PA.y;
        aA += this.m_invIA * Vec2.cross(rA, PA);
        final Vec2 vec4 = cB;
        vec4.x += this.m_invMassB * PB.x;
        final Vec2 vec5 = cB;
        vec5.y += this.m_invMassB * PB.y;
        aB += this.m_invIB * Vec2.cross(rB, PB);
        data.positions[this.m_indexA].a = aA;
        data.positions[this.m_indexB].a = aB;
        this.pool.pushRot(2);
        this.pool.pushVec2(7);
        return linearError < Settings.linearSlop;
    }
}
