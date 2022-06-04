// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Settings;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Rot;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class GearJoint extends Joint
{
    private final Joint m_joint1;
    private final Joint m_joint2;
    private final JointType m_typeA;
    private final JointType m_typeB;
    private final Body m_bodyC;
    private final Body m_bodyD;
    private final Vec2 m_localAnchorA;
    private final Vec2 m_localAnchorB;
    private final Vec2 m_localAnchorC;
    private final Vec2 m_localAnchorD;
    private final Vec2 m_localAxisC;
    private final Vec2 m_localAxisD;
    private float m_referenceAngleA;
    private float m_referenceAngleB;
    private float m_constant;
    private float m_ratio;
    private float m_impulse;
    private int m_indexA;
    private int m_indexB;
    private int m_indexC;
    private int m_indexD;
    private final Vec2 m_lcA;
    private final Vec2 m_lcB;
    private final Vec2 m_lcC;
    private final Vec2 m_lcD;
    private float m_mA;
    private float m_mB;
    private float m_mC;
    private float m_mD;
    private float m_iA;
    private float m_iB;
    private float m_iC;
    private float m_iD;
    private final Vec2 m_JvAC;
    private final Vec2 m_JvBD;
    private float m_JwA;
    private float m_JwB;
    private float m_JwC;
    private float m_JwD;
    private float m_mass;
    
    protected GearJoint(final IWorldPool argWorldPool, final GearJointDef def) {
        super(argWorldPool, def);
        this.m_localAnchorA = new Vec2();
        this.m_localAnchorB = new Vec2();
        this.m_localAnchorC = new Vec2();
        this.m_localAnchorD = new Vec2();
        this.m_localAxisC = new Vec2();
        this.m_localAxisD = new Vec2();
        this.m_lcA = new Vec2();
        this.m_lcB = new Vec2();
        this.m_lcC = new Vec2();
        this.m_lcD = new Vec2();
        this.m_JvAC = new Vec2();
        this.m_JvBD = new Vec2();
        this.m_joint1 = def.joint1;
        this.m_joint2 = def.joint2;
        this.m_typeA = this.m_joint1.getType();
        this.m_typeB = this.m_joint2.getType();
        assert this.m_typeA == JointType.PRISMATIC;
        assert this.m_typeB == JointType.PRISMATIC;
        this.m_bodyC = this.m_joint1.getBodyA();
        this.m_bodyA = this.m_joint1.getBodyB();
        final Transform xfA = this.m_bodyA.m_xf;
        final float aA = this.m_bodyA.m_sweep.a;
        final Transform xfC = this.m_bodyC.m_xf;
        final float aC = this.m_bodyC.m_sweep.a;
        float coordinateA;
        if (this.m_typeA == JointType.REVOLUTE) {
            final RevoluteJoint revolute = (RevoluteJoint)def.joint1;
            this.m_localAnchorC.set(revolute.m_localAnchorA);
            this.m_localAnchorA.set(revolute.m_localAnchorB);
            this.m_referenceAngleA = revolute.m_referenceAngle;
            this.m_localAxisC.setZero();
            coordinateA = aA - aC - this.m_referenceAngleA;
        }
        else {
            final Vec2 pA = this.pool.popVec2();
            final Vec2 temp = this.pool.popVec2();
            final PrismaticJoint prismatic = (PrismaticJoint)def.joint1;
            this.m_localAnchorC.set(prismatic.m_localAnchorA);
            this.m_localAnchorA.set(prismatic.m_localAnchorB);
            this.m_referenceAngleA = prismatic.m_referenceAngle;
            this.m_localAxisC.set(prismatic.m_localXAxisA);
            final Vec2 pC = this.m_localAnchorC;
            Rot.mulToOutUnsafe(xfA.q, this.m_localAnchorA, temp);
            temp.addLocal(xfA.p).subLocal(xfC.p);
            Rot.mulTransUnsafe(xfC.q, temp, pA);
            coordinateA = Vec2.dot(pA.subLocal(pC), this.m_localAxisC);
            this.pool.pushVec2(2);
        }
        this.m_bodyD = this.m_joint2.getBodyA();
        this.m_bodyB = this.m_joint2.getBodyB();
        final Transform xfB = this.m_bodyB.m_xf;
        final float aB = this.m_bodyB.m_sweep.a;
        final Transform xfD = this.m_bodyD.m_xf;
        final float aD = this.m_bodyD.m_sweep.a;
        float coordinateB;
        if (this.m_typeB == JointType.REVOLUTE) {
            final RevoluteJoint revolute2 = (RevoluteJoint)def.joint2;
            this.m_localAnchorD.set(revolute2.m_localAnchorA);
            this.m_localAnchorB.set(revolute2.m_localAnchorB);
            this.m_referenceAngleB = revolute2.m_referenceAngle;
            this.m_localAxisD.setZero();
            coordinateB = aB - aD - this.m_referenceAngleB;
        }
        else {
            final Vec2 pB = this.pool.popVec2();
            final Vec2 temp2 = this.pool.popVec2();
            final PrismaticJoint prismatic2 = (PrismaticJoint)def.joint2;
            this.m_localAnchorD.set(prismatic2.m_localAnchorA);
            this.m_localAnchorB.set(prismatic2.m_localAnchorB);
            this.m_referenceAngleB = prismatic2.m_referenceAngle;
            this.m_localAxisD.set(prismatic2.m_localXAxisA);
            final Vec2 pD = this.m_localAnchorD;
            Rot.mulToOutUnsafe(xfB.q, this.m_localAnchorB, temp2);
            temp2.addLocal(xfB.p).subLocal(xfD.p);
            Rot.mulTransUnsafe(xfD.q, temp2, pB);
            coordinateB = Vec2.dot(pB.subLocal(pD), this.m_localAxisD);
            this.pool.pushVec2(2);
        }
        this.m_ratio = def.ratio;
        this.m_constant = coordinateA + this.m_ratio * coordinateB;
        this.m_impulse = 0.0f;
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
        argOut.set(this.m_JvAC).mulLocal(this.m_impulse);
        argOut.mulLocal(inv_dt);
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        final float L = this.m_impulse * this.m_JwA;
        return inv_dt * L;
    }
    
    public void setRatio(final float argRatio) {
        this.m_ratio = argRatio;
    }
    
    public float getRatio() {
        return this.m_ratio;
    }
    
    @Override
    public void initVelocityConstraints(final SolverData data) {
        this.m_indexA = this.m_bodyA.m_islandIndex;
        this.m_indexB = this.m_bodyB.m_islandIndex;
        this.m_indexC = this.m_bodyC.m_islandIndex;
        this.m_indexD = this.m_bodyD.m_islandIndex;
        this.m_lcA.set(this.m_bodyA.m_sweep.localCenter);
        this.m_lcB.set(this.m_bodyB.m_sweep.localCenter);
        this.m_lcC.set(this.m_bodyC.m_sweep.localCenter);
        this.m_lcD.set(this.m_bodyD.m_sweep.localCenter);
        this.m_mA = this.m_bodyA.m_invMass;
        this.m_mB = this.m_bodyB.m_invMass;
        this.m_mC = this.m_bodyC.m_invMass;
        this.m_mD = this.m_bodyD.m_invMass;
        this.m_iA = this.m_bodyA.m_invI;
        this.m_iB = this.m_bodyB.m_invI;
        this.m_iC = this.m_bodyC.m_invI;
        this.m_iD = this.m_bodyD.m_invI;
        final float aA = data.positions[this.m_indexA].a;
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
        final float aB = data.positions[this.m_indexB].a;
        final Vec2 vB = data.velocities[this.m_indexB].v;
        float wB = data.velocities[this.m_indexB].w;
        final float aC = data.positions[this.m_indexC].a;
        final Vec2 vC = data.velocities[this.m_indexC].v;
        float wC = data.velocities[this.m_indexC].w;
        final float aD = data.positions[this.m_indexD].a;
        final Vec2 vD = data.velocities[this.m_indexD].v;
        float wD = data.velocities[this.m_indexD].w;
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Rot qC = this.pool.popRot();
        final Rot qD = this.pool.popRot();
        qA.set(aA);
        qB.set(aB);
        qC.set(aC);
        qD.set(aD);
        this.m_mass = 0.0f;
        final Vec2 temp = this.pool.popVec2();
        if (this.m_typeA == JointType.REVOLUTE) {
            this.m_JvAC.setZero();
            this.m_JwA = 1.0f;
            this.m_JwC = 1.0f;
            this.m_mass += this.m_iA + this.m_iC;
        }
        else {
            final Vec2 rC = this.pool.popVec2();
            final Vec2 rA = this.pool.popVec2();
            Rot.mulToOutUnsafe(qC, this.m_localAxisC, this.m_JvAC);
            Rot.mulToOutUnsafe(qC, temp.set(this.m_localAnchorC).subLocal(this.m_lcC), rC);
            Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_lcA), rA);
            this.m_JwC = Vec2.cross(rC, this.m_JvAC);
            this.m_JwA = Vec2.cross(rA, this.m_JvAC);
            this.m_mass += this.m_mC + this.m_mA + this.m_iC * this.m_JwC * this.m_JwC + this.m_iA * this.m_JwA * this.m_JwA;
            this.pool.pushVec2(2);
        }
        if (this.m_typeB == JointType.REVOLUTE) {
            this.m_JvBD.setZero();
            this.m_JwB = this.m_ratio;
            this.m_JwD = this.m_ratio;
            this.m_mass += this.m_ratio * this.m_ratio * (this.m_iB + this.m_iD);
        }
        else {
            final Vec2 u = this.pool.popVec2();
            final Vec2 rD = this.pool.popVec2();
            final Vec2 rB = this.pool.popVec2();
            Rot.mulToOutUnsafe(qD, this.m_localAxisD, u);
            Rot.mulToOutUnsafe(qD, temp.set(this.m_localAnchorD).subLocal(this.m_lcD), rD);
            Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_lcB), rB);
            this.m_JvBD.set(u).mulLocal(this.m_ratio);
            this.m_JwD = this.m_ratio * Vec2.cross(rD, u);
            this.m_JwB = this.m_ratio * Vec2.cross(rB, u);
            this.m_mass += this.m_ratio * this.m_ratio * (this.m_mD + this.m_mB) + this.m_iD * this.m_JwD * this.m_JwD + this.m_iB * this.m_JwB * this.m_JwB;
            this.pool.pushVec2(3);
        }
        this.m_mass = ((this.m_mass > 0.0f) ? (1.0f / this.m_mass) : 0.0f);
        if (data.step.warmStarting) {
            final Vec2 vec2 = vA;
            vec2.x += this.m_mA * this.m_impulse * this.m_JvAC.x;
            final Vec2 vec3 = vA;
            vec3.y += this.m_mA * this.m_impulse * this.m_JvAC.y;
            wA += this.m_iA * this.m_impulse * this.m_JwA;
            final Vec2 vec4 = vB;
            vec4.x += this.m_mB * this.m_impulse * this.m_JvBD.x;
            final Vec2 vec5 = vB;
            vec5.y += this.m_mB * this.m_impulse * this.m_JvBD.y;
            wB += this.m_iB * this.m_impulse * this.m_JwB;
            final Vec2 vec6 = vC;
            vec6.x -= this.m_mC * this.m_impulse * this.m_JvAC.x;
            final Vec2 vec7 = vC;
            vec7.y -= this.m_mC * this.m_impulse * this.m_JvAC.y;
            wC -= this.m_iC * this.m_impulse * this.m_JwC;
            final Vec2 vec8 = vD;
            vec8.x -= this.m_mD * this.m_impulse * this.m_JvBD.x;
            final Vec2 vec9 = vD;
            vec9.y -= this.m_mD * this.m_impulse * this.m_JvBD.y;
            wD -= this.m_iD * this.m_impulse * this.m_JwD;
        }
        else {
            this.m_impulse = 0.0f;
        }
        this.pool.pushVec2(1);
        this.pool.pushRot(4);
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        data.velocities[this.m_indexC].w = wC;
        data.velocities[this.m_indexD].w = wD;
    }
    
    @Override
    public void solveVelocityConstraints(final SolverData data) {
        final Vec2 vA = data.velocities[this.m_indexA].v;
        float wA = data.velocities[this.m_indexA].w;
        final Vec2 vB = data.velocities[this.m_indexB].v;
        float wB = data.velocities[this.m_indexB].w;
        final Vec2 vC = data.velocities[this.m_indexC].v;
        float wC = data.velocities[this.m_indexC].w;
        final Vec2 vD = data.velocities[this.m_indexD].v;
        float wD = data.velocities[this.m_indexD].w;
        final Vec2 temp1 = this.pool.popVec2();
        final Vec2 temp2 = this.pool.popVec2();
        float Cdot = Vec2.dot(this.m_JvAC, temp1.set(vA).subLocal(vC)) + Vec2.dot(this.m_JvBD, temp2.set(vB).subLocal(vD));
        Cdot += this.m_JwA * wA - this.m_JwC * wC + (this.m_JwB * wB - this.m_JwD * wD);
        this.pool.pushVec2(2);
        final float impulse = -this.m_mass * Cdot;
        this.m_impulse += impulse;
        final Vec2 vec2 = vA;
        vec2.x += this.m_mA * impulse * this.m_JvAC.x;
        final Vec2 vec3 = vA;
        vec3.y += this.m_mA * impulse * this.m_JvAC.y;
        wA += this.m_iA * impulse * this.m_JwA;
        final Vec2 vec4 = vB;
        vec4.x += this.m_mB * impulse * this.m_JvBD.x;
        final Vec2 vec5 = vB;
        vec5.y += this.m_mB * impulse * this.m_JvBD.y;
        wB += this.m_iB * impulse * this.m_JwB;
        final Vec2 vec6 = vC;
        vec6.x -= this.m_mC * impulse * this.m_JvAC.x;
        final Vec2 vec7 = vC;
        vec7.y -= this.m_mC * impulse * this.m_JvAC.y;
        wC -= this.m_iC * impulse * this.m_JwC;
        final Vec2 vec8 = vD;
        vec8.x -= this.m_mD * impulse * this.m_JvBD.x;
        final Vec2 vec9 = vD;
        vec9.y -= this.m_mD * impulse * this.m_JvBD.y;
        wD -= this.m_iD * impulse * this.m_JwD;
        data.velocities[this.m_indexA].w = wA;
        data.velocities[this.m_indexB].w = wB;
        data.velocities[this.m_indexC].w = wC;
        data.velocities[this.m_indexD].w = wD;
    }
    
    public Joint getJoint1() {
        return this.m_joint1;
    }
    
    public Joint getJoint2() {
        return this.m_joint2;
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData data) {
        final Vec2 cA = data.positions[this.m_indexA].c;
        float aA = data.positions[this.m_indexA].a;
        final Vec2 cB = data.positions[this.m_indexB].c;
        float aB = data.positions[this.m_indexB].a;
        final Vec2 cC = data.positions[this.m_indexC].c;
        float aC = data.positions[this.m_indexC].a;
        final Vec2 cD = data.positions[this.m_indexD].c;
        float aD = data.positions[this.m_indexD].a;
        final Rot qA = this.pool.popRot();
        final Rot qB = this.pool.popRot();
        final Rot qC = this.pool.popRot();
        final Rot qD = this.pool.popRot();
        qA.set(aA);
        qB.set(aB);
        qC.set(aC);
        qD.set(aD);
        final float linearError = 0.0f;
        final Vec2 temp = this.pool.popVec2();
        final Vec2 JvAC = this.pool.popVec2();
        final Vec2 JvBD = this.pool.popVec2();
        float mass = 0.0f;
        float JwA;
        float JwC;
        float coordinateA;
        if (this.m_typeA == JointType.REVOLUTE) {
            JvAC.setZero();
            JwA = 1.0f;
            JwC = 1.0f;
            mass += this.m_iA + this.m_iC;
            coordinateA = aA - aC - this.m_referenceAngleA;
        }
        else {
            final Vec2 rC = this.pool.popVec2();
            final Vec2 rA = this.pool.popVec2();
            final Vec2 pC = this.pool.popVec2();
            final Vec2 pA = this.pool.popVec2();
            Rot.mulToOutUnsafe(qC, this.m_localAxisC, JvAC);
            Rot.mulToOutUnsafe(qC, temp.set(this.m_localAnchorC).subLocal(this.m_lcC), rC);
            Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_lcA), rA);
            JwC = Vec2.cross(rC, JvAC);
            JwA = Vec2.cross(rA, JvAC);
            mass += this.m_mC + this.m_mA + this.m_iC * JwC * JwC + this.m_iA * JwA * JwA;
            pC.set(this.m_localAnchorC).subLocal(this.m_lcC);
            Rot.mulTransUnsafe(qC, temp.set(rA).addLocal(cA).subLocal(cC), pA);
            coordinateA = Vec2.dot(pA.subLocal(pC), this.m_localAxisC);
            this.pool.pushVec2(4);
        }
        float JwB;
        float JwD;
        float coordinateB;
        if (this.m_typeB == JointType.REVOLUTE) {
            JvBD.setZero();
            JwB = this.m_ratio;
            JwD = this.m_ratio;
            mass += this.m_ratio * this.m_ratio * (this.m_iB + this.m_iD);
            coordinateB = aB - aD - this.m_referenceAngleB;
        }
        else {
            final Vec2 u = this.pool.popVec2();
            final Vec2 rD = this.pool.popVec2();
            final Vec2 rB = this.pool.popVec2();
            final Vec2 pD = this.pool.popVec2();
            final Vec2 pB = this.pool.popVec2();
            Rot.mulToOutUnsafe(qD, this.m_localAxisD, u);
            Rot.mulToOutUnsafe(qD, temp.set(this.m_localAnchorD).subLocal(this.m_lcD), rD);
            Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_lcB), rB);
            JvBD.set(u).mulLocal(this.m_ratio);
            JwD = Vec2.cross(rD, u);
            JwB = Vec2.cross(rB, u);
            mass += this.m_ratio * this.m_ratio * (this.m_mD + this.m_mB) + this.m_iD * JwD * JwD + this.m_iB * JwB * JwB;
            pD.set(this.m_localAnchorD).subLocal(this.m_lcD);
            Rot.mulTransUnsafe(qD, temp.set(rB).addLocal(cB).subLocal(cD), pB);
            coordinateB = Vec2.dot(pB.subLocal(pD), this.m_localAxisD);
            this.pool.pushVec2(5);
        }
        final float C = coordinateA + this.m_ratio * coordinateB - this.m_constant;
        float impulse = 0.0f;
        if (mass > 0.0f) {
            impulse = -C / mass;
        }
        this.pool.pushVec2(3);
        this.pool.pushRot(4);
        final Vec2 vec2 = cA;
        vec2.x += this.m_mA * impulse * JvAC.x;
        final Vec2 vec3 = cA;
        vec3.y += this.m_mA * impulse * JvAC.y;
        aA += this.m_iA * impulse * JwA;
        final Vec2 vec4 = cB;
        vec4.x += this.m_mB * impulse * JvBD.x;
        final Vec2 vec5 = cB;
        vec5.y += this.m_mB * impulse * JvBD.y;
        aB += this.m_iB * impulse * JwB;
        final Vec2 vec6 = cC;
        vec6.x -= this.m_mC * impulse * JvAC.x;
        final Vec2 vec7 = cC;
        vec7.y -= this.m_mC * impulse * JvAC.y;
        aC -= this.m_iC * impulse * JwC;
        final Vec2 vec8 = cD;
        vec8.x -= this.m_mD * impulse * JvBD.x;
        final Vec2 vec9 = cD;
        vec9.y -= this.m_mD * impulse * JvBD.y;
        aD -= this.m_iD * impulse * JwD;
        data.positions[this.m_indexA].a = aA;
        data.positions[this.m_indexB].a = aB;
        data.positions[this.m_indexC].a = aC;
        data.positions[this.m_indexD].a = aD;
        return linearError < Settings.linearSlop;
    }
}
