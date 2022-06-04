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

public class RopeJoint extends Joint
{
    private final Vec2 m_localAnchorA;
    private final Vec2 m_localAnchorB;
    private float m_maxLength;
    private float m_length;
    private float m_impulse;
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
    private LimitState m_state;
    
    protected RopeJoint(final IWorldPool worldPool, final RopeJointDef def) {
        super(worldPool, def);
        this.m_localAnchorA = new Vec2();
        this.m_localAnchorB = new Vec2();
        this.m_u = new Vec2();
        this.m_rA = new Vec2();
        this.m_rB = new Vec2();
        this.m_localCenterA = new Vec2();
        this.m_localCenterB = new Vec2();
        this.m_localAnchorA.set(def.localAnchorA);
        this.m_localAnchorB.set(def.localAnchorB);
        this.m_maxLength = def.maxLength;
        this.m_mass = 0.0f;
        this.m_impulse = 0.0f;
        this.m_state = LimitState.INACTIVE;
        this.m_length = 0.0f;
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
        this.m_u.set(cB).addLocal(this.m_rB).subLocal(cA).subLocal(this.m_rA);
        this.m_length = this.m_u.length();
        final float C = this.m_length - this.m_maxLength;
        if (C > 0.0f) {
            this.m_state = LimitState.AT_UPPER;
        }
        else {
            this.m_state = LimitState.INACTIVE;
        }
        if (this.m_length > Settings.linearSlop) {
            this.m_u.mulLocal(1.0f / this.m_length);
            final float crA = Vec2.cross(this.m_rA, this.m_u);
            final float crB = Vec2.cross(this.m_rB, this.m_u);
            final float invMass = this.m_invMassA + this.m_invIA * crA * crA + this.m_invMassB + this.m_invIB * crB * crB;
            this.m_mass = ((invMass != 0.0f) ? (1.0f / invMass) : 0.0f);
            if (data.step.warmStarting) {
                this.m_impulse *= data.step.dtRatio;
                final float Px = this.m_impulse * this.m_u.x;
                final float Py = this.m_impulse * this.m_u.y;
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
            }
            else {
                this.m_impulse = 0.0f;
            }
            this.pool.pushRot(2);
            this.pool.pushVec2(1);
            data.velocities[this.m_indexA].w = wA;
            data.velocities[this.m_indexB].w = wB;
            return;
        }
        this.m_u.setZero();
        this.m_mass = 0.0f;
        this.m_impulse = 0.0f;
        this.pool.pushRot(2);
        this.pool.pushVec2(1);
    }
    
    @Override
    public void solveVelocityConstraints(final SolverData data) {
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
        final Vec2 vB = data.velocities[this.m_indexB].v;
        float wB = data.velocities[this.m_indexB].w;
        final Vec2 vpA = this.pool.popVec2();
        final Vec2 vpB = this.pool.popVec2();
        final Vec2 temp = this.pool.popVec2();
        Vec2.crossToOutUnsafe(wA, this.m_rA, vpA);
        vpA.addLocal(vA);
        Vec2.crossToOutUnsafe(wB, this.m_rB, vpB);
        vpB.addLocal(vB);
        final float C = this.m_length - this.m_maxLength;
        float Cdot = Vec2.dot(this.m_u, temp.set(vpB).subLocal(vpA));
        if (C < 0.0f) {
            Cdot += data.step.inv_dt * C;
        }
        float impulse = -this.m_mass * Cdot;
        final float oldImpulse = this.m_impulse;
        this.m_impulse = MathUtils.min(0.0f, this.m_impulse + impulse);
        impulse = this.m_impulse - oldImpulse;
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
        this.pool.pushVec2(3);
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        final Vec2 cA = data.positions[this.m_indexA].c;
        float aA = data.positions[this.m_indexA].a;
        final Vec2 cB = data.positions[this.m_indexB].c;
        float aB = data.positions[this.m_indexB].a;
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Vec2 u = this.pool.popVec2();
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        final Vec2 temp = this.pool.popVec2();
        qA.set(aA);
        qB.set(aB);
        Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
        Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
        u.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
        final float length = u.normalize();
        float C = length - this.m_maxLength;
        C = MathUtils.clamp(C, 0.0f, Settings.maxLinearCorrection);
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
        this.pool.pushRot(2);
        this.pool.pushVec2(4);
        data.positions[this.m_indexA].a = aA;
        data.positions[this.m_indexB].a = aB;
        return length - this.m_maxLength < Settings.linearSlop;
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
        argOut.set(this.m_u).mulLocal(inv_dt).mulLocal(this.m_impulse);
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return 0.0f;
    }
    
    public Vec2 getLocalAnchorA() {
        return this.m_localAnchorA;
    }
    
    public Vec2 getLocalAnchorB() {
        return this.m_localAnchorB;
    }
    
    public float getMaxLength() {
        return this.m_maxLength;
    }
    
    public void setMaxLength(final float maxLength) {
        this.m_maxLength = maxLength;
    }
    
    public LimitState getLimitState() {
        return this.m_state;
    }
}
