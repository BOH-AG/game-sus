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

public class DistanceJoint extends Joint
{
    private float m_frequencyHz;
    private float m_dampingRatio;
    private float m_bias;
    private final Vec2 m_localAnchorA;
    private final Vec2 m_localAnchorB;
    private float m_gamma;
    private float m_impulse;
    private float m_length;
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_u;
    private final Vec2 m_rA;
    private final Vec2 m_rB;
    private final Vec2 m_localCenterA;
    private final Vec2 m_localCenterB;
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private float m_mass;
    
    protected DistanceJoint(final IWorldPool argWorld, final DistanceJointDef def) {
        super(argWorld, def);
        this.m_u = new Vec2();
        this.m_rA = new Vec2();
        this.m_rB = new Vec2();
        this.m_localCenterA = new Vec2();
        this.m_localCenterB = new Vec2();
        this.m_localAnchorA = def.localAnchorA.clone();
        this.m_localAnchorB = def.localAnchorB.clone();
        this.m_length = def.length;
        this.m_impulse = 0.0f;
        this.m_frequencyHz = def.frequencyHz;
        this.m_dampingRatio = def.dampingRatio;
        this.m_gamma = 0.0f;
        this.m_bias = 0.0f;
    }
    
    public void setFrequency(final float hz) {
        this.m_frequencyHz = hz;
    }
    
    public float getFrequency() {
        return this.m_frequencyHz;
    }
    
    public float getLength() {
        return this.m_length;
    }
    
    public void setLength(final float argLength) {
        this.m_length = argLength;
    }
    
    public void setDampingRatio(final float damp) {
        this.m_dampingRatio = damp;
    }
    
    public float getDampingRatio() {
        return this.m_dampingRatio;
    }
    
    @Override
    public void getAnchorA(final Vec2 argOut) {
        this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, argOut);
    }
    
    @Override
    public void getAnchorB(final Vec2 argOut) {
        this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, argOut);
    }
    
    public Vec2 getLocalAnchorA() {
        return this.m_localAnchorA;
    }
    
    public Vec2 getLocalAnchorB() {
        return this.m_localAnchorB;
    }
    
    @Override
    public void getReactionForce(final float inv_dt, final Vec2 argOut) {
        argOut.x = this.m_impulse * this.m_u.x * inv_dt;
        argOut.y = this.m_impulse * this.m_u.y * inv_dt;
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return 0.0f;
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
        qA.set(aA);
        qB.set(aB);
        Rot.mulToOutUnsafe(qA, this.m_u.set(this.m_localAnchorA).subLocal(this.m_localCenterA), this.m_rA);
        Rot.mulToOutUnsafe(qB, this.m_u.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.m_rB);
        this.m_u.set(cB).addLocal(this.m_rB).subLocal(cA).subLocal(this.m_rA);
        this.pool.pushRot(2);
        final float length = this.m_u.length();
        if (length > Settings.linearSlop) {
            final Vec2 u = this.m_u;
            u.x *= 1.0f / length;
            final Vec2 u2 = this.m_u;
            u2.y *= 1.0f / length;
        }
        else {
            this.m_u.set(0.0f, 0.0f);
        }
        final float crAu = Vec2.cross(this.m_rA, this.m_u);
        final float crBu = Vec2.cross(this.m_rB, this.m_u);
        float invMass = this.m_invMassA + this.m_invIA * crAu * crAu + this.m_invMassB + this.m_invIB * crBu * crBu;
        this.m_mass = ((invMass != 0.0f) ? (1.0f / invMass) : 0.0f);
        if (this.m_frequencyHz > 0.0f) {
            final float C = length - this.m_length;
            final float omega = 6.2831855f * this.m_frequencyHz;
            final float d = 2.0f * this.m_mass * this.m_dampingRatio * omega;
            final float k = this.m_mass * omega * omega;
            final float h = data.step.dt;
            this.m_gamma = h * (d + h * k);
            this.m_gamma = ((this.m_gamma != 0.0f) ? (1.0f / this.m_gamma) : 0.0f);
            this.m_bias = C * h * k * this.m_gamma;
            invMass += this.m_gamma;
            this.m_mass = ((invMass != 0.0f) ? (1.0f / invMass) : 0.0f);
        }
        else {
            this.m_gamma = 0.0f;
            this.m_bias = 0.0f;
        }
        if (data.step.warmStarting) {
            this.m_impulse *= data.step.dtRatio;
            final Vec2 P = this.pool.popVec2();
            P.set(this.m_u).mulLocal(this.m_impulse);
            final Vec2 vec2 = vA;
            vec2.x -= this.m_invMassA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= this.m_invMassA * P.y;
            wA -= this.m_invIA * Vec2.cross(this.m_rA, P);
            final Vec2 vec4 = vB;
            vec4.x += this.m_invMassB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += this.m_invMassB * P.y;
            wB += this.m_invIB * Vec2.cross(this.m_rB, P);
            this.pool.pushVec2(1);
        }
        else {
            this.m_impulse = 0.0f;
        }
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
    }
    
    @Override
    public void solveVelocityConstraints(final SolverData data) {
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
        final Vec2 vB = data.velocities[this.m_indexB].v;
        float wB = data.velocities[this.m_indexB].w;
        final Vec2 vpA = this.pool.popVec2();
        final Vec2 vpB = this.pool.popVec2();
        Vec2.crossToOutUnsafe(wA, this.m_rA, vpA);
        vpA.addLocal(vA);
        Vec2.crossToOutUnsafe(wB, this.m_rB, vpB);
        vpB.addLocal(vB);
        final float Cdot = Vec2.dot(this.m_u, vpB.subLocal(vpA));
        final float impulse = -this.m_mass * (Cdot + this.m_bias + this.m_gamma * this.m_impulse);
        this.m_impulse += impulse;
        final float Px = impulse * this.m_u.x;
        final float Py = impulse * this.m_u.y;
        final Vec2 vec2 = vA;
        vec2.x -= this.m_invMassA * Px;
        final Vec2 vec3 = vA;
        vec3.y -= this.m_invMassA * Py;
        wA -= this.m_invIA * (this.m_rA.x * Py - this.m_rA.y * Px);
        final Vec2 vec4 = vB;
        vec4.x += this.m_invMassB * Px;
        final Vec2 vec5 = vB;
        vec5.y += this.m_invMassB * Py;
        wB += this.m_invIB * (this.m_rB.x * Py - this.m_rB.y * Px);
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushVec2(2);
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        if (this.m_frequencyHz > 0.0f) {
            return true;
        }
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        final Vec2 u = this.pool.popVec2();
        final Vec2 cA = data.positions[this.m_indexA].c;
        float aA = data.positions[this.m_indexA].a;
        final Vec2 cB = data.positions[this.m_indexB].c;
        float aB = data.positions[this.m_indexB].a;
        qA.set(aA);
        qB.set(aB);
        Rot.mulToOutUnsafe(qA, u.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
        Rot.mulToOutUnsafe(qB, u.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
        u.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
        final float length = u.normalize();
        float C = length - this.m_length;
        C = MathUtils.clamp(C, -Settings.maxLinearCorrection, Settings.maxLinearCorrection);
        final float impulse = -this.m_mass * C;
        final float Px = impulse * u.x;
        final float Py = impulse * u.y;
        final Vec2 vec2 = cA;
        vec2.x -= this.m_invMassA * Px;
        final Vec2 vec3 = cA;
        vec3.y -= this.m_invMassA * Py;
        aA -= this.m_invIA * (rA.x * Py - rA.y * Px);
        final Vec2 vec4 = cB;
        vec4.x += this.m_invMassB * Px;
        final Vec2 vec5 = cB;
        vec5.y += this.m_invMassB * Py;
        aB += this.m_invIB * (rB.x * Py - rB.y * Px);
        data.positions[this.m_indexA].a = aA;
        data.positions[this.m_indexB].a = aB;
        this.pool.pushVec2(3);
        this.pool.pushRot(2);
        return MathUtils.abs(C) < Settings.linearSlop;
    }
}
