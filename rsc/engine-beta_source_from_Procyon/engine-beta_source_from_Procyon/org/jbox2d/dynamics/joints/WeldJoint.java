// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Settings;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Vec3;
import org.jbox2d.common.Vec2;

public class WeldJoint extends Joint
{
    private float m_frequencyHz;
    private float m_dampingRatio;
    private float m_bias;
    private final Vec2 m_localAnchorA;
    private final Vec2 m_localAnchorB;
    private float m_referenceAngle;
    private float m_gamma;
    private final Vec3 m_impulse;
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
    
    protected WeldJoint(final IWorldPool argWorld, final WeldJointDef def) {
        super(argWorld, def);
        this.m_rA = new Vec2();
        this.m_rB = new Vec2();
        this.m_localCenterA = new Vec2();
        this.m_localCenterB = new Vec2();
        this.m_mass = new Mat33();
        this.m_localAnchorA = new Vec2(def.localAnchorA);
        this.m_localAnchorB = new Vec2(def.localAnchorB);
        this.m_referenceAngle = def.referenceAngle;
        this.m_frequencyHz = def.frequencyHz;
        this.m_dampingRatio = def.dampingRatio;
        (this.m_impulse = new Vec3()).setZero();
    }
    
    public float getReferenceAngle() {
        return this.m_referenceAngle;
    }
    
    public Vec2 getLocalAnchorA() {
        return this.m_localAnchorA;
    }
    
    public Vec2 getLocalAnchorB() {
        return this.m_localAnchorB;
    }
    
    public float getFrequency() {
        return this.m_frequencyHz;
    }
    
    public void setFrequency(final float frequencyHz) {
        this.m_frequencyHz = frequencyHz;
    }
    
    public float getDampingRatio() {
        return this.m_dampingRatio;
    }
    
    public void setDampingRatio(final float dampingRatio) {
        this.m_dampingRatio = dampingRatio;
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
        argOut.set(this.m_impulse.x, this.m_impulse.y);
        argOut.mulLocal(inv_dt);
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return inv_dt * this.m_impulse.z;
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
        final Mat33 K = this.pool.popMat33();
        K.ex.x = mA + mB + this.m_rA.y * this.m_rA.y * iA + this.m_rB.y * this.m_rB.y * iB;
        K.ey.x = -this.m_rA.y * this.m_rA.x * iA - this.m_rB.y * this.m_rB.x * iB;
        K.ez.x = -this.m_rA.y * iA - this.m_rB.y * iB;
        K.ex.y = K.ey.x;
        K.ey.y = mA + mB + this.m_rA.x * this.m_rA.x * iA + this.m_rB.x * this.m_rB.x * iB;
        K.ez.y = this.m_rA.x * iA + this.m_rB.x * iB;
        K.ex.z = K.ez.x;
        K.ey.z = K.ez.y;
        K.ez.z = iA + iB;
        if (this.m_frequencyHz > 0.0f) {
            K.getInverse22(this.m_mass);
            float invM = iA + iB;
            final float m = (invM > 0.0f) ? (1.0f / invM) : 0.0f;
            final float C = aB - aA - this.m_referenceAngle;
            final float omega = 6.2831855f * this.m_frequencyHz;
            final float d = 2.0f * m * this.m_dampingRatio * omega;
            final float k = m * omega * omega;
            final float h = data.step.dt;
            this.m_gamma = h * (d + h * k);
            this.m_gamma = ((this.m_gamma != 0.0f) ? (1.0f / this.m_gamma) : 0.0f);
            this.m_bias = C * h * k * this.m_gamma;
            invM += this.m_gamma;
            this.m_mass.ez.z = ((invM != 0.0f) ? (1.0f / invM) : 0.0f);
        }
        else {
            K.getSymInverse33(this.m_mass);
            this.m_gamma = 0.0f;
            this.m_bias = 0.0f;
        }
        if (data.step.warmStarting) {
            final Vec2 P = this.pool.popVec2();
            this.m_impulse.mulLocal(data.step.dtRatio);
            P.set(this.m_impulse.x, this.m_impulse.y);
            final Vec2 vec2 = vA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= mA * P.y;
            wA -= iA * (Vec2.cross(this.m_rA, P) + this.m_impulse.z);
            final Vec2 vec4 = vB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += mB * P.y;
            wB += iB * (Vec2.cross(this.m_rB, P) + this.m_impulse.z);
            this.pool.pushVec2(1);
        }
        else {
            this.m_impulse.setZero();
        }
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushVec2(1);
        this.pool.pushRot(2);
        this.pool.pushMat33(1);
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
        final Vec2 Cdot1 = this.pool.popVec2();
        final Vec2 P = this.pool.popVec2();
        final Vec2 temp = this.pool.popVec2();
        if (this.m_frequencyHz > 0.0f) {
            final float Cdot2 = wB - wA;
            final float impulse2 = -this.m_mass.ez.z * (Cdot2 + this.m_bias + this.m_gamma * this.m_impulse.z);
            final Vec3 impulse5 = this.m_impulse;
            impulse5.z += impulse2;
            wA -= iA * impulse2;
            wB += iB * impulse2;
            Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot1);
            Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
            Cdot1.addLocal(vB).subLocal(vA).subLocal(temp);
            final Vec2 impulse3 = P;
            Mat33.mul22ToOutUnsafe(this.m_mass, Cdot1, impulse3);
            impulse3.negateLocal();
            final Vec3 impulse6 = this.m_impulse;
            impulse6.x += impulse3.x;
            final Vec3 impulse7 = this.m_impulse;
            impulse7.y += impulse3.y;
            final Vec2 vec2 = vA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = vA;
            vec3.y -= mA * P.y;
            wA -= iA * Vec2.cross(this.m_rA, P);
            final Vec2 vec4 = vB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = vB;
            vec5.y += mB * P.y;
            wB += iB * Vec2.cross(this.m_rB, P);
        }
        else {
            Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
            Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot1);
            Cdot1.addLocal(vB).subLocal(vA).subLocal(temp);
            final float Cdot2 = wB - wA;
            final Vec3 Cdot3 = this.pool.popVec3();
            Cdot3.set(Cdot1.x, Cdot1.y, Cdot2);
            final Vec3 impulse4 = this.pool.popVec3();
            Mat33.mulToOutUnsafe(this.m_mass, Cdot3, impulse4);
            impulse4.negateLocal();
            this.m_impulse.addLocal(impulse4);
            P.set(impulse4.x, impulse4.y);
            final Vec2 vec6 = vA;
            vec6.x -= mA * P.x;
            final Vec2 vec7 = vA;
            vec7.y -= mA * P.y;
            wA -= iA * (Vec2.cross(this.m_rA, P) + impulse4.z);
            final Vec2 vec8 = vB;
            vec8.x += mB * P.x;
            final Vec2 vec9 = vB;
            vec9.y += mB * P.y;
            wB += iB * (Vec2.cross(this.m_rB, P) + impulse4.z);
            this.pool.pushVec3(2);
        }
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        this.pool.pushVec2(3);
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        final Vec2 cA = data.positions[this.m_indexA].c;
        float aA = data.positions[this.m_indexA].a;
        final Vec2 cB = data.positions[this.m_indexB].c;
        float aB = data.positions[this.m_indexB].a;
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Vec2 temp = this.pool.popVec2();
        final Vec2 rA = this.pool.popVec2();
        final Vec2 rB = this.pool.popVec2();
        qA.set(aA);
        qB.set(aB);
        final float mA = this.m_invMassA;
        final float mB = this.m_invMassB;
        final float iA = this.m_invIA;
        final float iB = this.m_invIB;
        Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
        Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
        final Mat33 K = this.pool.popMat33();
        final Vec2 C1 = this.pool.popVec2();
        final Vec2 P = this.pool.popVec2();
        K.ex.x = mA + mB + rA.y * rA.y * iA + rB.y * rB.y * iB;
        K.ey.x = -rA.y * rA.x * iA - rB.y * rB.x * iB;
        K.ez.x = -rA.y * iA - rB.y * iB;
        K.ex.y = K.ey.x;
        K.ey.y = mA + mB + rA.x * rA.x * iA + rB.x * rB.x * iB;
        K.ez.y = rA.x * iA + rB.x * iB;
        K.ex.z = K.ez.x;
        K.ey.z = K.ez.y;
        K.ez.z = iA + iB;
        float positionError;
        float angularError;
        if (this.m_frequencyHz > 0.0f) {
            C1.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
            positionError = C1.length();
            angularError = 0.0f;
            K.solve22ToOut(C1, P);
            P.negateLocal();
            final Vec2 vec2 = cA;
            vec2.x -= mA * P.x;
            final Vec2 vec3 = cA;
            vec3.y -= mA * P.y;
            aA -= iA * Vec2.cross(rA, P);
            final Vec2 vec4 = cB;
            vec4.x += mB * P.x;
            final Vec2 vec5 = cB;
            vec5.y += mB * P.y;
            aB += iB * Vec2.cross(rB, P);
        }
        else {
            C1.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
            final float C2 = aB - aA - this.m_referenceAngle;
            positionError = C1.length();
            angularError = MathUtils.abs(C2);
            final Vec3 C3 = this.pool.popVec3();
            final Vec3 impulse = this.pool.popVec3();
            C3.set(C1.x, C1.y, C2);
            K.solve33ToOut(C3, impulse);
            impulse.negateLocal();
            P.set(impulse.x, impulse.y);
            final Vec2 vec6 = cA;
            vec6.x -= mA * P.x;
            final Vec2 vec7 = cA;
            vec7.y -= mA * P.y;
            aA -= iA * (Vec2.cross(rA, P) + impulse.z);
            final Vec2 vec8 = cB;
            vec8.x += mB * P.x;
            final Vec2 vec9 = cB;
            vec9.y += mB * P.y;
            aB += iB * (Vec2.cross(rB, P) + impulse.z);
            this.pool.pushVec3(2);
        }
        data.positions[this.m_indexA].a = aA;
        data.positions[this.m_indexB].a = aB;
        this.pool.pushVec2(5);
        this.pool.pushRot(2);
        this.pool.pushMat33(1);
        return positionError <= Settings.linearSlop && angularError <= Settings.angularSlop;
    }
}
