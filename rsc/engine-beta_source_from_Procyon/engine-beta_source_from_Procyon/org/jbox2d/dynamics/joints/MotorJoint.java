// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;

public class MotorJoint extends Joint
{
    private final Vec2 m_linearOffset;
    private float m_angularOffset;
    private final Vec2 m_linearImpulse;
    private float m_angularImpulse;
    private float m_maxForce;
    private float m_maxTorque;
    private float m_correctionFactor;
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_rA;
    private final Vec2 m_rB;
    private final Vec2 m_localCenterA;
    private final Vec2 m_localCenterB;
    private final Vec2 m_linearError;
    private float m_angularError;
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private final Mat22 m_linearMass;
    private float m_angularMass;
    
    public MotorJoint(final IWorldPool pool, final MotorJointDef def) {
        super(pool, def);
        this.m_linearOffset = new Vec2();
        this.m_linearImpulse = new Vec2();
        this.m_rA = new Vec2();
        this.m_rB = new Vec2();
        this.m_localCenterA = new Vec2();
        this.m_localCenterB = new Vec2();
        this.m_linearError = new Vec2();
        this.m_linearMass = new Mat22();
        this.m_linearOffset.set(def.linearOffset);
        this.m_angularOffset = def.angularOffset;
        this.m_angularImpulse = 0.0f;
        this.m_maxForce = def.maxForce;
        this.m_maxTorque = def.maxTorque;
        this.m_correctionFactor = def.correctionFactor;
    }
    
    @Override
    public void getAnchorA(final Vec2 out) {
        out.set(this.m_bodyA.getPosition());
    }
    
    @Override
    public void getAnchorB(final Vec2 out) {
        out.set(this.m_bodyB.getPosition());
    }
    
    @Override
    public void getReactionForce(final float inv_dt, final Vec2 out) {
        out.set(this.m_linearImpulse).mulLocal(inv_dt);
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return this.m_angularImpulse * inv_dt;
    }
    
    public float getCorrectionFactor() {
        return this.m_correctionFactor;
    }
    
    public void setCorrectionFactor(final float correctionFactor) {
        this.m_correctionFactor = correctionFactor;
    }
    
    public void setLinearOffset(final Vec2 linearOffset) {
        if (linearOffset.x != this.m_linearOffset.x || linearOffset.y != this.m_linearOffset.y) {
            this.m_bodyA.setAwake(true);
            this.m_bodyB.setAwake(true);
            this.m_linearOffset.set(linearOffset);
        }
    }
    
    public void getLinearOffset(final Vec2 out) {
        out.set(this.m_linearOffset);
    }
    
    public Vec2 getLinearOffset() {
        return this.m_linearOffset;
    }
    
    public void setAngularOffset(final float angularOffset) {
        if (angularOffset != this.m_angularOffset) {
            this.m_bodyA.setAwake(true);
            this.m_bodyB.setAwake(true);
            this.m_angularOffset = angularOffset;
        }
    }
    
    public float getAngularOffset() {
        return this.m_angularOffset;
    }
    
    public void setMaxForce(final float force) {
        assert force >= 0.0f;
        this.m_maxForce = force;
    }
    
    public float getMaxForce() {
        return this.m_maxForce;
    }
    
    public void setMaxTorque(final float torque) {
        assert torque >= 0.0f;
        this.m_maxTorque = torque;
    }
    
    public float getMaxTorque() {
        return this.m_maxTorque;
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
        final Mat22 K = this.pool.popMat22();
        qA.set(aA);
        qB.set(aB);
        this.m_rA.x = qA.c * -this.m_localCenterA.x - qA.s * -this.m_localCenterA.y;
        this.m_rA.y = qA.s * -this.m_localCenterA.x + qA.c * -this.m_localCenterA.y;
        this.m_rB.x = qB.c * -this.m_localCenterB.x - qB.s * -this.m_localCenterB.y;
        this.m_rB.y = qB.s * -this.m_localCenterB.x + qB.c * -this.m_localCenterB.y;
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        K.ex.x = mA + mB + iA * this.m_rA.y * this.m_rA.y + iB * this.m_rB.y * this.m_rB.y;
        K.ex.y = -iA * this.m_rA.x * this.m_rA.y - iB * this.m_rB.x * this.m_rB.y;
        K.ey.x = K.ex.y;
        K.ey.y = mA + mB + iA * this.m_rA.x * this.m_rA.x + iB * this.m_rB.x * this.m_rB.x;
        K.invertToOut(this.m_linearMass);
        this.m_angularMass = iA + iB;
        if (this.m_angularMass > 0.0f) {
            this.m_angularMass = 1.0f / this.m_angularMass;
        }
        Rot.mulToOutUnsafe(qA, this.m_linearOffset, temp);
        this.m_linearError.x = cB.x + this.m_rB.x - cA.x - this.m_rA.x - temp.x;
        this.m_linearError.y = cB.y + this.m_rB.y - cA.y - this.m_rA.y - temp.y;
        this.m_angularError = aB - aA - this.m_angularOffset;
        if (data.step.warmStarting) {
            final Vec2 linearImpulse = this.m_linearImpulse;
            linearImpulse.x *= data.step.dtRatio;
            final Vec2 linearImpulse2 = this.m_linearImpulse;
            linearImpulse2.y *= data.step.dtRatio;
            this.m_angularImpulse *= data.step.dtRatio;
            final Vec2 P = this.m_linearImpulse;
            final Vec2 vec2 = vA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= mA * P.y;
            wA -= iA * (this.m_rA.x * P.y - this.m_rA.y * P.x + this.m_angularImpulse);
            final Vec2 vec4 = vB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += mB * P.y;
            wB += iB * (this.m_rB.x * P.y - this.m_rB.y * P.x + this.m_angularImpulse);
        }
        else {
            this.m_linearImpulse.setZero();
            this.m_angularImpulse = 0.0f;
        }
        this.pool.pushVec2(1);
        this.pool.pushMat22(1);
        this.pool.pushRot(2);
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
    }
    
    @Override
    public void solveVelocityConstraints(final SolverData data) {
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
        final Vec2 vB = data.velocities[this.m_indexB].v;
        float wB = data.velocities[this.m_indexB].w;
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        final float h = data.step.dt;
        final float inv_h = data.step.inv_dt;
        final Vec2 temp = this.pool.popVec2();
        final float Cdot = wB - wA + inv_h * this.m_correctionFactor * this.m_angularError;
        float impulse = -this.m_angularMass * Cdot;
        final float oldImpulse = this.m_angularImpulse;
        float maxImpulse = h * this.m_maxTorque;
        this.m_angularImpulse = MathUtils.clamp(this.m_angularImpulse + impulse, -maxImpulse, maxImpulse);
        impulse = this.m_angularImpulse - oldImpulse;
        wA -= iA * impulse;
        wB += iB * impulse;
        final Vec2 Cdot2 = this.pool.popVec2();
        Cdot2.x = vB.x + -wB * this.m_rB.y - vA.x - -wA * this.m_rA.y + inv_h * this.m_correctionFactor * this.m_linearError.x;
        Cdot2.y = vB.y + wB * this.m_rB.x - vA.y - wA * this.m_rA.x + inv_h * this.m_correctionFactor * this.m_linearError.y;
        final Vec2 impulse2 = temp;
        Mat22.mulToOutUnsafe(this.m_linearMass, Cdot2, impulse2);
        impulse2.negateLocal();
        final Vec2 oldImpulse2 = this.pool.popVec2();
        oldImpulse2.set(this.m_linearImpulse);
        this.m_linearImpulse.addLocal(impulse2);
        maxImpulse = h * this.m_maxForce;
        if (this.m_linearImpulse.lengthSquared() > maxImpulse * maxImpulse) {
            this.m_linearImpulse.normalize();
            this.m_linearImpulse.mulLocal(maxImpulse);
        }
        impulse2.x = this.m_linearImpulse.x - oldImpulse2.x;
        impulse2.y = this.m_linearImpulse.y - oldImpulse2.y;
        final Vec2 vec2 = vA;
        vec2.x -= mA * impulse2.x;
        final Vec2 vec3 = vA;
        vec3.y -= mA * impulse2.y;
        wA -= iA * (this.m_rA.x * impulse2.y - this.m_rA.y * impulse2.x);
        final Vec2 vec4 = vB;
        vec4.x += mB * impulse2.x;
        final Vec2 vec5 = vB;
        vec5.y += mB * impulse2.y;
        wB += iB * (this.m_rB.x * impulse2.y - this.m_rB.y * impulse2.x);
        this.pool.pushVec2(3);
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        return true;
    }
}
