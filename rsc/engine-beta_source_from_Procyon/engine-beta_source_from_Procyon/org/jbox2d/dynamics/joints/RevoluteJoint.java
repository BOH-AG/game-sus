// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Settings;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Vec3;
import org.jbox2d.common.Vec2;

public class RevoluteJoint extends Joint
{
    protected final Vec2 m_localAnchorA;
    protected final Vec2 m_localAnchorB;
    private final Vec3 m_impulse;
    private float m_motorImpulse;
    private boolean m_enableMotor;
    private float m_maxMotorTorque;
    private float m_motorSpeed;
    private boolean m_enableLimit;
    protected float m_referenceAngle;
    private float m_lowerAngle;
    private float m_upperAngle;
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_rA;
    private final Vec2 m_rB;
    private final Vec2 m_localCenterA;
    private final Vec2 m_localCenterB;
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private final Mat33 m_mass;
    private float m_motorMass;
    private LimitState m_limitState;
    
    protected RevoluteJoint(final IWorldPool argWorld, final RevoluteJointDef def) {
        super(argWorld, def);
        this.m_localAnchorA = new Vec2();
        this.m_localAnchorB = new Vec2();
        this.m_impulse = new Vec3();
        this.m_rA = new Vec2();
        this.m_rB = new Vec2();
        this.m_localCenterA = new Vec2();
        this.m_localCenterB = new Vec2();
        this.m_mass = new Mat33();
        this.m_localAnchorA.set(def.localAnchorA);
        this.m_localAnchorB.set(def.localAnchorB);
        this.m_referenceAngle = def.referenceAngle;
        this.m_motorImpulse = 0.0f;
        this.m_lowerAngle = def.lowerAngle;
        this.m_upperAngle = def.upperAngle;
        this.m_maxMotorTorque = def.maxMotorTorque;
        this.m_motorSpeed = def.motorSpeed;
        this.m_enableLimit = def.enableLimit;
        this.m_enableMotor = def.enableMotor;
        this.m_limitState = LimitState.INACTIVE;
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
        final float aA = data.positions[this.m_indexA].a;
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
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
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        final boolean fixedRotation = iA + iB == 0.0f;
        this.m_mass.ex.x = mA + mB + this.m_rA.y * this.m_rA.y * iA + this.m_rB.y * this.m_rB.y * iB;
        this.m_mass.ey.x = -this.m_rA.y * this.m_rA.x * iA - this.m_rB.y * this.m_rB.x * iB;
        this.m_mass.ez.x = -this.m_rA.y * iA - this.m_rB.y * iB;
        this.m_mass.ex.y = this.m_mass.ey.x;
        this.m_mass.ey.y = mA + mB + this.m_rA.x * this.m_rA.x * iA + this.m_rB.x * this.m_rB.x * iB;
        this.m_mass.ez.y = this.m_rA.x * iA + this.m_rB.x * iB;
        this.m_mass.ex.z = this.m_mass.ez.x;
        this.m_mass.ey.z = this.m_mass.ez.y;
        this.m_mass.ez.z = iA + iB;
        this.m_motorMass = iA + iB;
        if (this.m_motorMass > 0.0f) {
            this.m_motorMass = 1.0f / this.m_motorMass;
        }
        if (!this.m_enableMotor || fixedRotation) {
            this.m_motorImpulse = 0.0f;
        }
        if (this.m_enableLimit && !fixedRotation) {
            final float jointAngle = aB - aA - this.m_referenceAngle;
            if (MathUtils.abs(this.m_upperAngle - this.m_lowerAngle) < 2.0f * Settings.angularSlop) {
                this.m_limitState = LimitState.EQUAL;
            }
            else if (jointAngle <= this.m_lowerAngle) {
                if (this.m_limitState != LimitState.AT_LOWER) {
                    this.m_impulse.z = 0.0f;
                }
                this.m_limitState = LimitState.AT_LOWER;
            }
            else if (jointAngle >= this.m_upperAngle) {
                if (this.m_limitState != LimitState.AT_UPPER) {
                    this.m_impulse.z = 0.0f;
                }
                this.m_limitState = LimitState.AT_UPPER;
            }
            else {
                this.m_limitState = LimitState.INACTIVE;
                this.m_impulse.z = 0.0f;
            }
        }
        else {
            this.m_limitState = LimitState.INACTIVE;
        }
        if (data.step.warmStarting) {
            final Vec2 P = this.pool.popVec2();
            final Vec3 impulse = this.m_impulse;
            impulse.x *= data.step.dtRatio;
            final Vec3 impulse2 = this.m_impulse;
            impulse2.y *= data.step.dtRatio;
            this.m_motorImpulse *= data.step.dtRatio;
            P.x = this.m_impulse.x;
            P.y = this.m_impulse.y;
            final Vec2 vec2 = vA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= mA * P.y;
            wA -= iA * (Vec2.cross(this.m_rA, P) + this.m_motorImpulse + this.m_impulse.z);
            final Vec2 vec4 = vB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += mB * P.y;
            wB += iB * (Vec2.cross(this.m_rB, P) + this.m_motorImpulse + this.m_impulse.z);
            this.pool.pushVec2(1);
        }
        else {
            this.m_impulse.setZero();
            this.m_motorImpulse = 0.0f;
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
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        final boolean fixedRotation = iA + iB == 0.0f;
        if (this.m_enableMotor && this.m_limitState != LimitState.EQUAL && !fixedRotation) {
            final float Cdot = wB - wA - this.m_motorSpeed;
            float impulse = -this.m_motorMass * Cdot;
            final float oldImpulse = this.m_motorImpulse;
            final float maxImpulse = data.step.dt * this.m_maxMotorTorque;
            this.m_motorImpulse = MathUtils.clamp(this.m_motorImpulse + impulse, -maxImpulse, maxImpulse);
            impulse = this.m_motorImpulse - oldImpulse;
            wA -= iA * impulse;
            wB += iB * impulse;
        }
        final Vec2 temp = this.pool.popVec2();
        if (this.m_enableLimit && this.m_limitState != LimitState.INACTIVE && !fixedRotation) {
            final Vec2 Cdot2 = this.pool.popVec2();
            final Vec3 Cdot3 = this.pool.popVec3();
            Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
            Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot2);
            Cdot2.addLocal(vB).subLocal(vA).subLocal(temp);
            final float Cdot4 = wB - wA;
            Cdot3.set(Cdot2.x, Cdot2.y, Cdot4);
            final Vec3 impulse2 = this.pool.popVec3();
            this.m_mass.solve33ToOut(Cdot3, impulse2);
            impulse2.negateLocal();
            if (this.m_limitState == LimitState.EQUAL) {
                this.m_impulse.addLocal(impulse2);
            }
            else if (this.m_limitState == LimitState.AT_LOWER) {
                final float newImpulse = this.m_impulse.z + impulse2.z;
                if (newImpulse < 0.0f) {
                    final Vec2 rhs = this.pool.popVec2();
                    rhs.set(this.m_mass.ez.x, this.m_mass.ez.y).mulLocal(this.m_impulse.z).subLocal(Cdot2);
                    this.m_mass.solve22ToOut(rhs, temp);
                    impulse2.x = temp.x;
                    impulse2.y = temp.y;
                    impulse2.z = -this.m_impulse.z;
                    final Vec3 impulse4 = this.m_impulse;
                    impulse4.x += temp.x;
                    final Vec3 impulse5 = this.m_impulse;
                    impulse5.y += temp.y;
                    this.m_impulse.z = 0.0f;
                    this.pool.pushVec2(1);
                }
                else {
                    this.m_impulse.addLocal(impulse2);
                }
            }
            else if (this.m_limitState == LimitState.AT_UPPER) {
                final float newImpulse = this.m_impulse.z + impulse2.z;
                if (newImpulse > 0.0f) {
                    final Vec2 rhs = this.pool.popVec2();
                    rhs.set(this.m_mass.ez.x, this.m_mass.ez.y).mulLocal(this.m_impulse.z).subLocal(Cdot2);
                    this.m_mass.solve22ToOut(rhs, temp);
                    impulse2.x = temp.x;
                    impulse2.y = temp.y;
                    impulse2.z = -this.m_impulse.z;
                    final Vec3 impulse6 = this.m_impulse;
                    impulse6.x += temp.x;
                    final Vec3 impulse7 = this.m_impulse;
                    impulse7.y += temp.y;
                    this.m_impulse.z = 0.0f;
                    this.pool.pushVec2(1);
                }
                else {
                    this.m_impulse.addLocal(impulse2);
                }
            }
            final Vec2 P = this.pool.popVec2();
            P.set(impulse2.x, impulse2.y);
            final Vec2 vec2 = vA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= mA * P.y;
            wA -= iA * (Vec2.cross(this.m_rA, P) + impulse2.z);
            final Vec2 vec4 = vB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += mB * P.y;
            wB += iB * (Vec2.cross(this.m_rB, P) + impulse2.z);
            this.pool.pushVec2(2);
            this.pool.pushVec3(2);
        }
        else {
            final Vec2 Cdot5 = this.pool.popVec2();
            final Vec2 impulse3 = this.pool.popVec2();
            Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
            Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot5);
            Cdot5.addLocal(vB).subLocal(vA).subLocal(temp);
            this.m_mass.solve22ToOut(Cdot5.negateLocal(), impulse3);
            final Vec3 impulse8 = this.m_impulse;
            impulse8.x += impulse3.x;
            final Vec3 impulse9 = this.m_impulse;
            impulse9.y += impulse3.y;
            final Vec2 vec6 = vA;
            vec6.x -= mA * impulse3.x;
            final Vec2 vec7 = vA;
            vec7.y -= mA * impulse3.y;
            wA -= iA * Vec2.cross(this.m_rA, impulse3);
            final Vec2 vec8 = vB;
            vec8.x += mB * impulse3.x;
            final Vec2 vec9 = vB;
            vec9.y += mB * impulse3.y;
            wB += iB * Vec2.cross(this.m_rB, impulse3);
            this.pool.pushVec2(2);
        }
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushVec2(1);
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Vec2 cA = data.positions[this.m_indexA].c;
        float aA = data.positions[this.m_indexA].a;
        final Vec2 cB = data.positions[this.m_indexB].c;
        float aB = data.positions[this.m_indexB].a;
        qA.set(aA);
        qB.set(aB);
        float angularError = 0.0f;
        float positionError = 0.0f;
        final boolean fixedRotation = this.m_invIA + this.m_invIB == 0.0f;
        if (this.m_enableLimit && this.m_limitState != LimitState.INACTIVE && !fixedRotation) {
            final float angle = aB - aA - this.m_referenceAngle;
            float limitImpulse = 0.0f;
            if (this.m_limitState == LimitState.EQUAL) {
                final float C = MathUtils.clamp(angle - this.m_lowerAngle, -Settings.maxAngularCorrection, Settings.maxAngularCorrection);
                limitImpulse = -this.m_motorMass * C;
                angularError = MathUtils.abs(C);
            }
            else if (this.m_limitState == LimitState.AT_LOWER) {
                float C = angle - this.m_lowerAngle;
                angularError = -C;
                C = MathUtils.clamp(C + Settings.angularSlop, -Settings.maxAngularCorrection, 0.0f);
                limitImpulse = -this.m_motorMass * C;
            }
            else if (this.m_limitState == LimitState.AT_UPPER) {
                float C = angularError = angle - this.m_upperAngle;
                C = MathUtils.clamp(C - Settings.angularSlop, 0.0f, Settings.maxAngularCorrection);
                limitImpulse = -this.m_motorMass * C;
            }
            aA -= this.m_invIA * limitImpulse;
            aB += this.m_invIB * limitImpulse;
        }
        qA.set(aA);
        qB.set(aB);
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        final Vec2 C2 = this.pool.popVec2();
        final Vec2 impulse = this.pool.popVec2();
        Rot.mulToOutUnsafe(qA, C2.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
        Rot.mulToOutUnsafe(qB, C2.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
        C2.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
        positionError = C2.length();
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        final Mat22 K = this.pool.popMat22();
        K.ex.x = mA + mB + iA * rA.y * rA.y + iB * rB.y * rB.y;
        K.ex.y = -iA * rA.x * rA.y - iB * rB.x * rB.y;
        K.ey.x = K.ex.y;
        K.ey.y = mA + mB + iA * rA.x * rA.x + iB * rB.x * rB.x;
        K.solveToOut(C2, impulse);
        impulse.negateLocal();
        final Vec2 vec2 = cA;
        vec2.x -= mA * impulse.x;
        final Vec2 vec3 = cA;
        vec3.y -= mA * impulse.y;
        aA -= iA * Vec2.cross(rA, impulse);
        final Vec2 vec4 = cB;
        vec4.x += mB * impulse.x;
        final Vec2 vec5 = cB;
        vec5.y += mB * impulse.y;
        aB += iB * Vec2.cross(rB, impulse);
        this.pool.pushVec2(4);
        this.pool.pushMat22(1);
        data.positions[this.m_indexA].a = aA;
        data.positions[this.m_indexB].a = aB;
        this.pool.pushRot(2);
        return positionError <= Settings.linearSlop && angularError <= Settings.angularSlop;
    }
    
    public Vec2 getLocalAnchorA() {
        return this.m_localAnchorA;
    }
    
    public Vec2 getLocalAnchorB() {
        return this.m_localAnchorB;
    }
    
    public float getReferenceAngle() {
        return this.m_referenceAngle;
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
        argOut.set(this.m_impulse.x, this.m_impulse.y).mulLocal(inv_dt);
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return inv_dt * this.m_impulse.z;
    }
    
    public float getJointAngle() {
        final Body b1 = this.m_bodyA;
        final Body b2 = this.m_bodyB;
        return b2.m_sweep.a - b1.m_sweep.a - this.m_referenceAngle;
    }
    
    public float getJointSpeed() {
        final Body b1 = this.m_bodyA;
        final Body b2 = this.m_bodyB;
        return b2.m_angularVelocity - b1.m_angularVelocity;
    }
    
    public boolean isMotorEnabled() {
        return this.m_enableMotor;
    }
    
    public void enableMotor(final boolean flag) {
        this.m_bodyA.setAwake(true);
        this.m_bodyB.setAwake(true);
        this.m_enableMotor = flag;
    }
    
    public float getMotorTorque(final float inv_dt) {
        return this.m_motorImpulse * inv_dt;
    }
    
    public void setMotorSpeed(final float speed) {
        this.m_bodyA.setAwake(true);
        this.m_bodyB.setAwake(true);
        this.m_motorSpeed = speed;
    }
    
    public void setMaxMotorTorque(final float torque) {
        this.m_bodyA.setAwake(true);
        this.m_bodyB.setAwake(true);
        this.m_maxMotorTorque = torque;
    }
    
    public float getMotorSpeed() {
        return this.m_motorSpeed;
    }
    
    public float getMaxMotorTorque() {
        return this.m_maxMotorTorque;
    }
    
    public boolean isLimitEnabled() {
        return this.m_enableLimit;
    }
    
    public void enableLimit(final boolean flag) {
        if (flag != this.m_enableLimit) {
            this.m_bodyA.setAwake(true);
            this.m_bodyB.setAwake(true);
            this.m_enableLimit = flag;
            this.m_impulse.z = 0.0f;
        }
    }
    
    public float getLowerLimit() {
        return this.m_lowerAngle;
    }
    
    public float getUpperLimit() {
        return this.m_upperAngle;
    }
    
    public void setLimits(final float lower, final float upper) {
        assert lower <= upper;
        if (lower != this.m_lowerAngle || upper != this.m_upperAngle) {
            this.m_bodyA.setAwake(true);
            this.m_bodyB.setAwake(true);
            this.m_impulse.z = 0.0f;
            this.m_lowerAngle = lower;
            this.m_upperAngle = upper;
        }
    }
}
