// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Mat22;
import org.jbox2d.common.Settings;
import org.jbox2d.common.MathUtils;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Rot;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Vec3;
import org.jbox2d.common.Vec2;

public class PrismaticJoint extends Joint
{
    protected final Vec2 m_localAnchorA;
    protected final Vec2 m_localAnchorB;
    protected final Vec2 m_localXAxisA;
    protected final Vec2 m_localYAxisA;
    protected float m_referenceAngle;
    private final Vec3 m_impulse;
    private float m_motorImpulse;
    private float m_lowerTranslation;
    private float m_upperTranslation;
    private float m_maxMotorForce;
    private float m_motorSpeed;
    private boolean m_enableLimit;
    private boolean m_enableMotor;
    private LimitState m_limitState;
    private int m_indexA;
    private int m_indexB;
    private final Vec2 m_localCenterA;
    private final Vec2 m_localCenterB;
    private float m_invMassA;
    private float m_invMassB;
    private float m_invIA;
    private float m_invIB;
    private final Vec2 m_axis;
    private final Vec2 m_perp;
    private float m_s1;
    private float m_s2;
    private float m_a1;
    private float m_a2;
    private final Mat33 m_K;
    private float m_motorMass;
    
    protected PrismaticJoint(final IWorldPool argWorld, final PrismaticJointDef def) {
        super(argWorld, def);
        this.m_localCenterA = new Vec2();
        this.m_localCenterB = new Vec2();
        this.m_localAnchorA = new Vec2(def.localAnchorA);
        this.m_localAnchorB = new Vec2(def.localAnchorB);
        (this.m_localXAxisA = new Vec2(def.localAxisA)).normalize();
        this.m_localYAxisA = new Vec2();
        Vec2.crossToOutUnsafe(1.0f, this.m_localXAxisA, this.m_localYAxisA);
        this.m_referenceAngle = def.referenceAngle;
        this.m_impulse = new Vec3();
        this.m_motorMass = 0.0f;
        this.m_motorImpulse = 0.0f;
        this.m_lowerTranslation = def.lowerTranslation;
        this.m_upperTranslation = def.upperTranslation;
        this.m_maxMotorForce = def.maxMotorForce;
        this.m_motorSpeed = def.motorSpeed;
        this.m_enableLimit = def.enableLimit;
        this.m_enableMotor = def.enableMotor;
        this.m_limitState = LimitState.INACTIVE;
        this.m_K = new Mat33();
        this.m_axis = new Vec2();
        this.m_perp = new Vec2();
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
        final Vec2 temp = this.pool.popVec2();
        temp.set(this.m_axis).mulLocal(this.m_motorImpulse + this.m_impulse.z);
        argOut.set(this.m_perp).mulLocal(this.m_impulse.x).addLocal(temp).mulLocal(inv_dt);
        this.pool.pushVec2(1);
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return inv_dt * this.m_impulse.y;
    }
    
    public float getJointSpeed() {
        final Body bA = this.m_bodyA;
        final Body bB = this.m_bodyB;
        final Vec2 temp = this.pool.popVec2();
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        final Vec2 p1 = this.pool.popVec2();
        final Vec2 p2 = this.pool.popVec2();
        final Vec2 d = this.pool.popVec2();
        final Vec2 axis = this.pool.popVec2();
        final Vec2 temp2 = this.pool.popVec2();
        final Vec2 temp3 = this.pool.popVec2();
        temp.set(this.m_localAnchorA).subLocal(bA.m_sweep.localCenter);
        Rot.mulToOutUnsafe(bA.m_xf.q, temp, rA);
        temp.set(this.m_localAnchorB).subLocal(bB.m_sweep.localCenter);
        Rot.mulToOutUnsafe(bB.m_xf.q, temp, rB);
        p1.set(bA.m_sweep.c).addLocal(rA);
        p2.set(bB.m_sweep.c).addLocal(rB);
        d.set(p2).subLocal(p1);
        Rot.mulToOutUnsafe(bA.m_xf.q, this.m_localXAxisA, axis);
        final Vec2 vA = bA.m_linearVelocity;
        final Vec2 vB = bB.m_linearVelocity;
        final float wA = bA.m_angularVelocity;
        final float wB = bB.m_angularVelocity;
        Vec2.crossToOutUnsafe(wA, axis, temp);
        Vec2.crossToOutUnsafe(wB, rB, temp2);
        Vec2.crossToOutUnsafe(wA, rA, temp3);
        temp2.addLocal(vB).subLocal(vA).subLocal(temp3);
        final float speed = Vec2.dot(d, temp) + Vec2.dot(axis, temp2);
        this.pool.pushVec2(9);
        return speed;
    }
    
    public float getJointTranslation() {
        final Vec2 pA = this.pool.popVec2();
        final Vec2 pB = this.pool.popVec2();
        final Vec2 axis = this.pool.popVec2();
        this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, pA);
        this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, pB);
        this.m_bodyA.getWorldVectorToOutUnsafe(this.m_localXAxisA, axis);
        pB.subLocal(pA);
        final float translation = Vec2.dot(pB, axis);
        this.pool.pushVec2(3);
        return translation;
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
        return this.m_lowerTranslation;
    }
    
    public float getUpperLimit() {
        return this.m_upperTranslation;
    }
    
    public void setLimits(final float lower, final float upper) {
        assert lower <= upper;
        if (lower != this.m_lowerTranslation || upper != this.m_upperTranslation) {
            this.m_bodyA.setAwake(true);
            this.m_bodyB.setAwake(true);
            this.m_lowerTranslation = lower;
            this.m_upperTranslation = upper;
            this.m_impulse.z = 0.0f;
        }
    }
    
    public boolean isMotorEnabled() {
        return this.m_enableMotor;
    }
    
    public void enableMotor(final boolean flag) {
        this.m_bodyA.setAwake(true);
        this.m_bodyB.setAwake(true);
        this.m_enableMotor = flag;
    }
    
    public void setMotorSpeed(final float speed) {
        this.m_bodyA.setAwake(true);
        this.m_bodyB.setAwake(true);
        this.m_motorSpeed = speed;
    }
    
    public float getMotorSpeed() {
        return this.m_motorSpeed;
    }
    
    public void setMaxMotorForce(final float force) {
        this.m_bodyA.setAwake(true);
        this.m_bodyB.setAwake(true);
        this.m_maxMotorForce = force;
    }
    
    public float getMotorForce(final float inv_dt) {
        return this.m_motorImpulse * inv_dt;
    }
    
    public float getMaxMotorForce() {
        return this.m_maxMotorForce;
    }
    
    public float getReferenceAngle() {
        return this.m_referenceAngle;
    }
    
    public Vec2 getLocalAxisA() {
        return this.m_localXAxisA;
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
        final Vec2 d = this.pool.popVec2();
        final Vec2 temp = this.pool.popVec2();
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        qA.set(aA);
        qB.set(aB);
        Rot.mulToOutUnsafe(qA, d.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
        Rot.mulToOutUnsafe(qB, d.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
        d.set(cB).subLocal(cA).addLocal(rB).subLocal(rA);
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        Rot.mulToOutUnsafe(qA, this.m_localXAxisA, this.m_axis);
        temp.set(d).addLocal(rA);
        this.m_a1 = Vec2.cross(temp, this.m_axis);
        this.m_a2 = Vec2.cross(rB, this.m_axis);
        this.m_motorMass = mA + mB + iA * this.m_a1 * this.m_a1 + iB * this.m_a2 * this.m_a2;
        if (this.m_motorMass > 0.0f) {
            this.m_motorMass = 1.0f / this.m_motorMass;
        }
        Rot.mulToOutUnsafe(qA, this.m_localYAxisA, this.m_perp);
        temp.set(d).addLocal(rA);
        this.m_s1 = Vec2.cross(temp, this.m_perp);
        this.m_s2 = Vec2.cross(rB, this.m_perp);
        final float k11 = mA + mB + iA * this.m_s1 * this.m_s1 + iB * this.m_s2 * this.m_s2;
        final float k12 = iA * this.m_s1 + iB * this.m_s2;
        final float k13 = iA * this.m_s1 * this.m_a1 + iB * this.m_s2 * this.m_a2;
        float k14 = iA + iB;
        if (k14 == 0.0f) {
            k14 = 1.0f;
        }
        final float k15 = iA * this.m_a1 + iB * this.m_a2;
        final float k16 = mA + mB + iA * this.m_a1 * this.m_a1 + iB * this.m_a2 * this.m_a2;
        this.m_K.ex.set(k11, k12, k13);
        this.m_K.ey.set(k12, k14, k15);
        this.m_K.ez.set(k13, k15, k16);
        if (this.m_enableLimit) {
            final float jointTranslation = Vec2.dot(this.m_axis, d);
            if (MathUtils.abs(this.m_upperTranslation - this.m_lowerTranslation) < 2.0f * Settings.linearSlop) {
                this.m_limitState = LimitState.EQUAL;
            }
            else if (jointTranslation <= this.m_lowerTranslation) {
                if (this.m_limitState != LimitState.AT_LOWER) {
                    this.m_limitState = LimitState.AT_LOWER;
                    this.m_impulse.z = 0.0f;
                }
            }
            else if (jointTranslation >= this.m_upperTranslation) {
                if (this.m_limitState != LimitState.AT_UPPER) {
                    this.m_limitState = LimitState.AT_UPPER;
                    this.m_impulse.z = 0.0f;
                }
            }
            else {
                this.m_limitState = LimitState.INACTIVE;
                this.m_impulse.z = 0.0f;
            }
        }
        else {
            this.m_limitState = LimitState.INACTIVE;
            this.m_impulse.z = 0.0f;
        }
        if (!this.m_enableMotor) {
            this.m_motorImpulse = 0.0f;
        }
        if (data.step.warmStarting) {
            this.m_impulse.mulLocal(data.step.dtRatio);
            this.m_motorImpulse *= data.step.dtRatio;
            final Vec2 P = this.pool.popVec2();
            temp.set(this.m_axis).mulLocal(this.m_motorImpulse + this.m_impulse.z);
            P.set(this.m_perp).mulLocal(this.m_impulse.x).addLocal(temp);
            final float LA = this.m_impulse.x * this.m_s1 + this.m_impulse.y + (this.m_motorImpulse + this.m_impulse.z) * this.m_a1;
            final float LB = this.m_impulse.x * this.m_s2 + this.m_impulse.y + (this.m_motorImpulse + this.m_impulse.z) * this.m_a2;
            final Vec2 vec2 = vA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= mA * P.y;
            wA -= iA * LA;
            final Vec2 vec4 = vB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += mB * P.y;
            wB += iB * LB;
            this.pool.pushVec2(1);
        }
        else {
            this.m_impulse.setZero();
            this.m_motorImpulse = 0.0f;
        }
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushRot(2);
        this.pool.pushVec2(4);
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
        final Vec2 temp = this.pool.popVec2();
        if (this.m_enableMotor && this.m_limitState != LimitState.EQUAL) {
            temp.set(vB).subLocal(vA);
            final float Cdot = Vec2.dot(this.m_axis, temp) + this.m_a2 * wB - this.m_a1 * wA;
            float impulse = this.m_motorMass * (this.m_motorSpeed - Cdot);
            final float oldImpulse = this.m_motorImpulse;
            final float maxImpulse = data.step.dt * this.m_maxMotorForce;
            this.m_motorImpulse = MathUtils.clamp(this.m_motorImpulse + impulse, -maxImpulse, maxImpulse);
            impulse = this.m_motorImpulse - oldImpulse;
            final Vec2 P = this.pool.popVec2();
            P.set(this.m_axis).mulLocal(impulse);
            final float LA = impulse * this.m_a1;
            final float LB = impulse * this.m_a2;
            final Vec2 vec2 = vA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= mA * P.y;
            wA -= iA * LA;
            final Vec2 vec4 = vB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += mB * P.y;
            wB += iB * LB;
            this.pool.pushVec2(1);
        }
        final Vec2 Cdot2 = this.pool.popVec2();
        temp.set(vB).subLocal(vA);
        Cdot2.x = Vec2.dot(this.m_perp, temp) + this.m_s2 * wB - this.m_s1 * wA;
        Cdot2.y = wB - wA;
        if (this.m_enableLimit && this.m_limitState != LimitState.INACTIVE) {
            temp.set(vB).subLocal(vA);
            final float Cdot3 = Vec2.dot(this.m_axis, temp) + this.m_a2 * wB - this.m_a1 * wA;
            final Vec3 Cdot4 = this.pool.popVec3();
            Cdot4.set(Cdot2.x, Cdot2.y, Cdot3);
            final Vec3 f1 = this.pool.popVec3();
            final Vec3 df = this.pool.popVec3();
            f1.set(this.m_impulse);
            this.m_K.solve33ToOut(Cdot4.negateLocal(), df);
            this.m_impulse.addLocal(df);
            if (this.m_limitState == LimitState.AT_LOWER) {
                this.m_impulse.z = MathUtils.max(this.m_impulse.z, 0.0f);
            }
            else if (this.m_limitState == LimitState.AT_UPPER) {
                this.m_impulse.z = MathUtils.min(this.m_impulse.z, 0.0f);
            }
            final Vec2 b = this.pool.popVec2();
            final Vec2 f2r = this.pool.popVec2();
            temp.set(this.m_K.ez.x, this.m_K.ez.y).mulLocal(this.m_impulse.z - f1.z);
            b.set(Cdot2).negateLocal().subLocal(temp);
            this.m_K.solve22ToOut(b, f2r);
            f2r.addLocal(f1.x, f1.y);
            this.m_impulse.x = f2r.x;
            this.m_impulse.y = f2r.y;
            df.set(this.m_impulse).subLocal(f1);
            final Vec2 P2 = this.pool.popVec2();
            temp.set(this.m_axis).mulLocal(df.z);
            P2.set(this.m_perp).mulLocal(df.x).addLocal(temp);
            final float LA2 = df.x * this.m_s1 + df.y + df.z * this.m_a1;
            final float LB2 = df.x * this.m_s2 + df.y + df.z * this.m_a2;
            final Vec2 vec6 = vA;
            vec6.x -= mA * P2.x;
            final Vec2 vec7 = vA;
            vec7.y -= mA * P2.y;
            wA -= iA * LA2;
            final Vec2 vec8 = vB;
            vec8.x += mB * P2.x;
            final Vec2 vec9 = vB;
            vec9.y += mB * P2.y;
            wB += iB * LB2;
            this.pool.pushVec2(3);
            this.pool.pushVec3(3);
        }
        else {
            final Vec2 df2 = this.pool.popVec2();
            this.m_K.solve22ToOut(Cdot2.negateLocal(), df2);
            Cdot2.negateLocal();
            final Vec3 impulse2 = this.m_impulse;
            impulse2.x += df2.x;
            final Vec3 impulse3 = this.m_impulse;
            impulse3.y += df2.y;
            final Vec2 P3 = this.pool.popVec2();
            P3.set(this.m_perp).mulLocal(df2.x);
            final float LA3 = df2.x * this.m_s1 + df2.y;
            final float LB3 = df2.x * this.m_s2 + df2.y;
            final Vec2 vec10 = vA;
            vec10.x -= mA * P3.x;
            final Vec2 vec11 = vA;
            vec11.y -= mA * P3.y;
            wA -= iA * LA3;
            final Vec2 vec12 = vB;
            vec12.x += mB * P3.x;
            final Vec2 vec13 = vB;
            vec13.y += mB * P3.y;
            wB += iB * LB3;
            this.pool.pushVec2(2);
        }
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushVec2(2);
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        final Vec2 d = this.pool.popVec2();
        final Vec2 axis = this.pool.popVec2();
        final Vec2 perp = this.pool.popVec2();
        final Vec2 temp = this.pool.popVec2();
        final Vec2 C1 = this.pool.popVec2();
        final Vec3 impulse = this.pool.popVec3();
        final Vec2 cA = data.positions[this.m_indexA].c;
        float aA = data.positions[this.m_indexA].a;
        final Vec2 cB = data.positions[this.m_indexB].c;
        float aB = data.positions[this.m_indexB].a;
        qA.set(aA);
        qB.set(aB);
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
        Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
        d.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
        Rot.mulToOutUnsafe(qA, this.m_localXAxisA, axis);
        final float a1 = Vec2.cross(temp.set(d).addLocal(rA), axis);
        final float a2 = Vec2.cross(rB, axis);
        Rot.mulToOutUnsafe(qA, this.m_localYAxisA, perp);
        final float s1 = Vec2.cross(temp.set(d).addLocal(rA), perp);
        final float s2 = Vec2.cross(rB, perp);
        C1.x = Vec2.dot(perp, d);
        C1.y = aB - aA - this.m_referenceAngle;
        float linearError = MathUtils.abs(C1.x);
        final float angularError = MathUtils.abs(C1.y);
        boolean active = false;
        float C2 = 0.0f;
        if (this.m_enableLimit) {
            final float translation = Vec2.dot(axis, d);
            if (MathUtils.abs(this.m_upperTranslation - this.m_lowerTranslation) < 2.0f * Settings.linearSlop) {
                C2 = MathUtils.clamp(translation, -Settings.maxLinearCorrection, Settings.maxLinearCorrection);
                linearError = MathUtils.max(linearError, MathUtils.abs(translation));
                active = true;
            }
            else if (translation <= this.m_lowerTranslation) {
                C2 = MathUtils.clamp(translation - this.m_lowerTranslation + Settings.linearSlop, -Settings.maxLinearCorrection, 0.0f);
                linearError = MathUtils.max(linearError, this.m_lowerTranslation - translation);
                active = true;
            }
            else if (translation >= this.m_upperTranslation) {
                C2 = MathUtils.clamp(translation - this.m_upperTranslation - Settings.linearSlop, 0.0f, Settings.maxLinearCorrection);
                linearError = MathUtils.max(linearError, translation - this.m_upperTranslation);
                active = true;
            }
        }
        if (active) {
            final float k11 = mA + mB + iA * s1 * s1 + iB * s2 * s2;
            final float k12 = iA * s1 + iB * s2;
            final float k13 = iA * s1 * a1 + iB * s2 * a2;
            float k14 = iA + iB;
            if (k14 == 0.0f) {
                k14 = 1.0f;
            }
            final float k15 = iA * a1 + iB * a2;
            final float k16 = mA + mB + iA * a1 * a1 + iB * a2 * a2;
            final Mat33 K = this.pool.popMat33();
            K.ex.set(k11, k12, k13);
            K.ey.set(k12, k14, k15);
            K.ez.set(k13, k15, k16);
            final Vec3 C3 = this.pool.popVec3();
            C3.x = C1.x;
            C3.y = C1.y;
            C3.z = C2;
            K.solve33ToOut(C3.negateLocal(), impulse);
            this.pool.pushVec3(1);
            this.pool.pushMat33(1);
        }
        else {
            final float k11 = mA + mB + iA * s1 * s1 + iB * s2 * s2;
            final float k12 = iA * s1 + iB * s2;
            float k17 = iA + iB;
            if (k17 == 0.0f) {
                k17 = 1.0f;
            }
            final Mat22 K2 = this.pool.popMat22();
            K2.ex.set(k11, k12);
            K2.ey.set(k12, k17);
            K2.solveToOut(C1.negateLocal(), temp);
            C1.negateLocal();
            impulse.x = temp.x;
            impulse.y = temp.y;
            impulse.z = 0.0f;
            this.pool.pushMat22(1);
        }
        final float Px = impulse.x * perp.x + impulse.z * axis.x;
        final float Py = impulse.x * perp.y + impulse.z * axis.y;
        final float LA = impulse.x * s1 + impulse.y + impulse.z * a1;
        final float LB = impulse.x * s2 + impulse.y + impulse.z * a2;
        final Vec2 vec2 = cA;
        vec2.x -= mA * Px;
        final Vec2 vec3 = cA;
        vec3.y -= mA * Py;
        aA -= iA * LA;
        final Vec2 vec4 = cB;
        vec4.x += mB * Px;
        final Vec2 vec5 = cB;
        vec5.y += mB * Py;
        aB += iB * LB;
        data.positions[this.m_indexA].a = aA;
        data.positions[this.m_indexB].a = aB;
        this.pool.pushVec2(7);
        this.pool.pushVec3(1);
        this.pool.pushRot(2);
        return linearError <= Settings.linearSlop && angularError <= Settings.angularSlop;
    }
}
