// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.common.Mat22;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.ManifoldPoint;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.common.MathUtils;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.TimeStep;

public class ContactSolver
{
    public static final boolean DEBUG_SOLVER = false;
    public static final float k_errorTol = 0.001f;
    public static final int INITIAL_NUM_CONSTRAINTS = 256;
    public static final float k_maxConditionNumber = 100.0f;
    public TimeStep m_step;
    public Position[] m_positions;
    public Velocity[] m_velocities;
    public ContactPositionConstraint[] m_positionConstraints;
    public ContactVelocityConstraint[] m_velocityConstraints;
    public Contact[] m_contacts;
    public int m_count;
    private final Transform xfA;
    private final Transform xfB;
    private final WorldManifold worldManifold;
    private final PositionSolverManifold psolver;
    
    public ContactSolver() {
        this.xfA = new Transform();
        this.xfB = new Transform();
        this.worldManifold = new WorldManifold();
        this.psolver = new PositionSolverManifold();
        this.m_positionConstraints = new ContactPositionConstraint[256];
        this.m_velocityConstraints = new ContactVelocityConstraint[256];
        for (int i = 0; i < 256; ++i) {
            this.m_positionConstraints[i] = new ContactPositionConstraint();
            this.m_velocityConstraints[i] = new ContactVelocityConstraint();
        }
    }
    
    public final void init(final ContactSolverDef def) {
        this.m_step = def.step;
        this.m_count = def.count;
        if (this.m_positionConstraints.length < this.m_count) {
            final ContactPositionConstraint[] old = this.m_positionConstraints;
            System.arraycopy(old, 0, this.m_positionConstraints = new ContactPositionConstraint[MathUtils.max(old.length * 2, this.m_count)], 0, old.length);
            for (int i = old.length; i < this.m_positionConstraints.length; ++i) {
                this.m_positionConstraints[i] = new ContactPositionConstraint();
            }
        }
        if (this.m_velocityConstraints.length < this.m_count) {
            final ContactVelocityConstraint[] old2 = this.m_velocityConstraints;
            System.arraycopy(old2, 0, this.m_velocityConstraints = new ContactVelocityConstraint[MathUtils.max(old2.length * 2, this.m_count)], 0, old2.length);
            for (int i = old2.length; i < this.m_velocityConstraints.length; ++i) {
                this.m_velocityConstraints[i] = new ContactVelocityConstraint();
            }
        }
        this.m_positions = def.positions;
        this.m_velocities = def.velocities;
        this.m_contacts = def.contacts;
        for (int j = 0; j < this.m_count; ++j) {
            final Contact contact = this.m_contacts[j];
            final Fixture fixtureA = contact.m_fixtureA;
            final Fixture fixtureB = contact.m_fixtureB;
            final Shape shapeA = fixtureA.getShape();
            final Shape shapeB = fixtureB.getShape();
            final float radiusA = shapeA.m_radius;
            final float radiusB = shapeB.m_radius;
            final Body bodyA = fixtureA.getBody();
            final Body bodyB = fixtureB.getBody();
            final Manifold manifold = contact.getManifold();
            final int pointCount = manifold.pointCount;
            assert pointCount > 0;
            final ContactVelocityConstraint vc = this.m_velocityConstraints[j];
            vc.friction = contact.m_friction;
            vc.restitution = contact.m_restitution;
            vc.tangentSpeed = contact.m_tangentSpeed;
            vc.indexA = bodyA.m_islandIndex;
            vc.indexB = bodyB.m_islandIndex;
            vc.invMassA = bodyA.m_invMass;
            vc.invMassB = bodyB.m_invMass;
            vc.invIA = bodyA.m_invI;
            vc.invIB = bodyB.m_invI;
            vc.contactIndex = j;
            vc.pointCount = pointCount;
            vc.K.setZero();
            vc.normalMass.setZero();
            final ContactPositionConstraint pc = this.m_positionConstraints[j];
            pc.indexA = bodyA.m_islandIndex;
            pc.indexB = bodyB.m_islandIndex;
            pc.invMassA = bodyA.m_invMass;
            pc.invMassB = bodyB.m_invMass;
            pc.localCenterA.set(bodyA.m_sweep.localCenter);
            pc.localCenterB.set(bodyB.m_sweep.localCenter);
            pc.invIA = bodyA.m_invI;
            pc.invIB = bodyB.m_invI;
            pc.localNormal.set(manifold.localNormal);
            pc.localPoint.set(manifold.localPoint);
            pc.pointCount = pointCount;
            pc.radiusA = radiusA;
            pc.radiusB = radiusB;
            pc.type = manifold.type;
            for (int k = 0; k < pointCount; ++k) {
                final ManifoldPoint cp = manifold.points[k];
                final ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[k];
                if (this.m_step.warmStarting) {
                    vcp.normalImpulse = this.m_step.dtRatio * cp.normalImpulse;
                    vcp.tangentImpulse = this.m_step.dtRatio * cp.tangentImpulse;
                }
                else {
                    vcp.normalImpulse = 0.0f;
                    vcp.tangentImpulse = 0.0f;
                }
                vcp.rA.setZero();
                vcp.rB.setZero();
                vcp.normalMass = 0.0f;
                vcp.tangentMass = 0.0f;
                vcp.velocityBias = 0.0f;
                pc.localPoints[k].x = cp.localPoint.x;
                pc.localPoints[k].y = cp.localPoint.y;
            }
        }
    }
    
    public void warmStart() {
        for (int i = 0; i < this.m_count; ++i) {
            final ContactVelocityConstraint vc = this.m_velocityConstraints[i];
            final int indexA = vc.indexA;
            final int indexB = vc.indexB;
            final float mA = vc.invMassA;
            final float iA = vc.invIA;
            final float mB = vc.invMassB;
            final float iB = vc.invIB;
            final int pointCount = vc.pointCount;
            final Vec2 vA = this.m_velocities[indexA].v;
            float wA = this.m_velocities[indexA].w;
            final Vec2 vB = this.m_velocities[indexB].v;
            float wB = this.m_velocities[indexB].w;
            final Vec2 normal = vc.normal;
            final float tangentx = 1.0f * normal.y;
            final float tangenty = -1.0f * normal.x;
            for (int j = 0; j < pointCount; ++j) {
                final ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[j];
                final float Px = tangentx * vcp.tangentImpulse + normal.x * vcp.normalImpulse;
                final float Py = tangenty * vcp.tangentImpulse + normal.y * vcp.normalImpulse;
                wA -= iA * (vcp.rA.x * Py - vcp.rA.y * Px);
                final Vec2 vec2 = vA;
                vec2.x -= Px * mA;
                final Vec2 vec3 = vA;
                vec3.y -= Py * mA;
                wB += iB * (vcp.rB.x * Py - vcp.rB.y * Px);
                final Vec2 vec4 = vB;
                vec4.x += Px * mB;
                final Vec2 vec5 = vB;
                vec5.y += Py * mB;
            }
            this.m_velocities[indexA].w = wA;
            this.m_velocities[indexB].w = wB;
        }
    }
    
    public final void initializeVelocityConstraints() {
        for (int i = 0; i < this.m_count; ++i) {
            final ContactVelocityConstraint vc = this.m_velocityConstraints[i];
            final ContactPositionConstraint pc = this.m_positionConstraints[i];
            final float radiusA = pc.radiusA;
            final float radiusB = pc.radiusB;
            final Manifold manifold = this.m_contacts[vc.contactIndex].getManifold();
            final int indexA = vc.indexA;
            final int indexB = vc.indexB;
            final float mA = vc.invMassA;
            final float mB = vc.invMassB;
            final float iA = vc.invIA;
            final float iB = vc.invIB;
            final Vec2 localCenterA = pc.localCenterA;
            final Vec2 localCenterB = pc.localCenterB;
            final Vec2 cA = this.m_positions[indexA].c;
            final float aA = this.m_positions[indexA].a;
            final Vec2 vA = this.m_velocities[indexA].v;
            final float wA = this.m_velocities[indexA].w;
            final Vec2 cB = this.m_positions[indexB].c;
            final float aB = this.m_positions[indexB].a;
            final Vec2 vB = this.m_velocities[indexB].v;
            final float wB = this.m_velocities[indexB].w;
            assert manifold.pointCount > 0;
            final Rot xfAq = this.xfA.q;
            final Rot xfBq = this.xfB.q;
            xfAq.set(aA);
            xfBq.set(aB);
            this.xfA.p.x = cA.x - (xfAq.c * localCenterA.x - xfAq.s * localCenterA.y);
            this.xfA.p.y = cA.y - (xfAq.s * localCenterA.x + xfAq.c * localCenterA.y);
            this.xfB.p.x = cB.x - (xfBq.c * localCenterB.x - xfBq.s * localCenterB.y);
            this.xfB.p.y = cB.y - (xfBq.s * localCenterB.x + xfBq.c * localCenterB.y);
            this.worldManifold.initialize(manifold, this.xfA, radiusA, this.xfB, radiusB);
            final Vec2 vcnormal = vc.normal;
            vcnormal.x = this.worldManifold.normal.x;
            vcnormal.y = this.worldManifold.normal.y;
            for (int pointCount = vc.pointCount, j = 0; j < pointCount; ++j) {
                final ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[j];
                final Vec2 wmPj = this.worldManifold.points[j];
                final Vec2 vcprA = vcp.rA;
                final Vec2 vcprB = vcp.rB;
                vcprA.x = wmPj.x - cA.x;
                vcprA.y = wmPj.y - cA.y;
                vcprB.x = wmPj.x - cB.x;
                vcprB.y = wmPj.y - cB.y;
                final float rnA = vcprA.x * vcnormal.y - vcprA.y * vcnormal.x;
                final float rnB = vcprB.x * vcnormal.y - vcprB.y * vcnormal.x;
                final float kNormal = mA + mB + iA * rnA * rnA + iB * rnB * rnB;
                vcp.normalMass = ((kNormal > 0.0f) ? (1.0f / kNormal) : 0.0f);
                final float tangentx = 1.0f * vcnormal.y;
                final float tangenty = -1.0f * vcnormal.x;
                final float rtA = vcprA.x * tangenty - vcprA.y * tangentx;
                final float rtB = vcprB.x * tangenty - vcprB.y * tangentx;
                final float kTangent = mA + mB + iA * rtA * rtA + iB * rtB * rtB;
                vcp.tangentMass = ((kTangent > 0.0f) ? (1.0f / kTangent) : 0.0f);
                vcp.velocityBias = 0.0f;
                final float tempx = vB.x + -wB * vcprB.y - vA.x - -wA * vcprA.y;
                final float tempy = vB.y + wB * vcprB.x - vA.y - wA * vcprA.x;
                final float vRel = vcnormal.x * tempx + vcnormal.y * tempy;
                if (vRel < -Settings.velocityThreshold) {
                    vcp.velocityBias = -vc.restitution * vRel;
                }
            }
            if (vc.pointCount == 2) {
                final ContactVelocityConstraint.VelocityConstraintPoint vcp2 = vc.points[0];
                final ContactVelocityConstraint.VelocityConstraintPoint vcp3 = vc.points[1];
                final float rn1A = vcp2.rA.x * vcnormal.y - vcp2.rA.y * vcnormal.x;
                final float rn1B = vcp2.rB.x * vcnormal.y - vcp2.rB.y * vcnormal.x;
                final float rn2A = vcp3.rA.x * vcnormal.y - vcp3.rA.y * vcnormal.x;
                final float rn2B = vcp3.rB.x * vcnormal.y - vcp3.rB.y * vcnormal.x;
                final float k11 = mA + mB + iA * rn1A * rn1A + iB * rn1B * rn1B;
                final float k12 = mA + mB + iA * rn2A * rn2A + iB * rn2B * rn2B;
                final float k13 = mA + mB + iA * rn1A * rn2A + iB * rn1B * rn2B;
                if (k11 * k11 < 100.0f * (k11 * k12 - k13 * k13)) {
                    vc.K.ex.x = k11;
                    vc.K.ex.y = k13;
                    vc.K.ey.x = k13;
                    vc.K.ey.y = k12;
                    vc.K.invertToOut(vc.normalMass);
                }
                else {
                    vc.pointCount = 1;
                }
            }
        }
    }
    
    public final void solveVelocityConstraints() {
        for (int i = 0; i < this.m_count; ++i) {
            final ContactVelocityConstraint vc = this.m_velocityConstraints[i];
            final int indexA = vc.indexA;
            final int indexB = vc.indexB;
            final float mA = vc.invMassA;
            final float mB = vc.invMassB;
            final float iA = vc.invIA;
            final float iB = vc.invIB;
            final int pointCount = vc.pointCount;
            final Vec2 vA = this.m_velocities[indexA].v;
            float wA = this.m_velocities[indexA].w;
            final Vec2 vB = this.m_velocities[indexB].v;
            float wB = this.m_velocities[indexB].w;
            final Vec2 normal = vc.normal;
            final float normalx = normal.x;
            final float normaly = normal.y;
            final float tangentx = 1.0f * vc.normal.y;
            final float tangenty = -1.0f * vc.normal.x;
            final float friction = vc.friction;
            assert pointCount == 2;
            for (int j = 0; j < pointCount; ++j) {
                final ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[j];
                final Vec2 a = vcp.rA;
                final float dvx = -wB * vcp.rB.y + vB.x - vA.x + wA * a.y;
                final float dvy = wB * vcp.rB.x + vB.y - vA.y - wA * a.x;
                final float vt = dvx * tangentx + dvy * tangenty - vc.tangentSpeed;
                float lambda = vcp.tangentMass * -vt;
                final float maxFriction = friction * vcp.normalImpulse;
                final float newImpulse = MathUtils.clamp(vcp.tangentImpulse + lambda, -maxFriction, maxFriction);
                lambda = newImpulse - vcp.tangentImpulse;
                vcp.tangentImpulse = newImpulse;
                final float Px = tangentx * lambda;
                final float Py = tangenty * lambda;
                final Vec2 vec2 = vA;
                vec2.x -= Px * mA;
                final Vec2 vec3 = vA;
                vec3.y -= Py * mA;
                wA -= iA * (vcp.rA.x * Py - vcp.rA.y * Px);
                final Vec2 vec4 = vB;
                vec4.x += Px * mB;
                final Vec2 vec5 = vB;
                vec5.y += Py * mB;
                wB += iB * (vcp.rB.x * Py - vcp.rB.y * Px);
            }
            if (vc.pointCount == 1) {
                final ContactVelocityConstraint.VelocityConstraintPoint vcp2 = vc.points[0];
                final float dvx2 = -wB * vcp2.rB.y + vB.x - vA.x + wA * vcp2.rA.y;
                final float dvy2 = wB * vcp2.rB.x + vB.y - vA.y - wA * vcp2.rA.x;
                final float vn = dvx2 * normalx + dvy2 * normaly;
                float lambda2 = -vcp2.normalMass * (vn - vcp2.velocityBias);
                final float a2 = vcp2.normalImpulse + lambda2;
                final float newImpulse2 = (a2 > 0.0f) ? a2 : 0.0f;
                lambda2 = newImpulse2 - vcp2.normalImpulse;
                vcp2.normalImpulse = newImpulse2;
                final float Px2 = normalx * lambda2;
                final float Py2 = normaly * lambda2;
                final Vec2 vec6 = vA;
                vec6.x -= Px2 * mA;
                final Vec2 vec7 = vA;
                vec7.y -= Py2 * mA;
                wA -= iA * (vcp2.rA.x * Py2 - vcp2.rA.y * Px2);
                final Vec2 vec8 = vB;
                vec8.x += Px2 * mB;
                final Vec2 vec9 = vB;
                vec9.y += Py2 * mB;
                wB += iB * (vcp2.rB.x * Py2 - vcp2.rB.y * Px2);
            }
            else {
                final ContactVelocityConstraint.VelocityConstraintPoint cp1 = vc.points[0];
                final ContactVelocityConstraint.VelocityConstraintPoint cp2 = vc.points[1];
                final Vec2 cp1rA = cp1.rA;
                final Vec2 cp1rB = cp1.rB;
                final Vec2 cp2rA = cp2.rA;
                final Vec2 cp2rB = cp2.rB;
                final float ax = cp1.normalImpulse;
                final float ay = cp2.normalImpulse;
                assert ax >= 0.0f && ay >= 0.0f;
                final float dv1x = -wB * cp1rB.y + vB.x - vA.x + wA * cp1rA.y;
                final float dv1y = wB * cp1rB.x + vB.y - vA.y - wA * cp1rA.x;
                final float dv2x = -wB * cp2rB.y + vB.x - vA.x + wA * cp2rA.y;
                final float dv2y = wB * cp2rB.x + vB.y - vA.y - wA * cp2rA.x;
                float vn2 = dv1x * normalx + dv1y * normaly;
                float vn3 = dv2x * normalx + dv2y * normaly;
                float bx = vn2 - cp1.velocityBias;
                float by = vn3 - cp2.velocityBias;
                final Mat22 R = vc.K;
                bx -= R.ex.x * ax + R.ey.x * ay;
                by -= R.ex.y * ax + R.ey.y * ay;
                final Mat22 R2 = vc.normalMass;
                float xx = R2.ex.x * bx + R2.ey.x * by;
                float xy = R2.ex.y * bx + R2.ey.y * by;
                xx *= -1.0f;
                xy *= -1.0f;
                if (xx >= 0.0f && xy >= 0.0f) {
                    final float dx = xx - ax;
                    final float dy = xy - ay;
                    final float P1x = dx * normalx;
                    final float P1y = dx * normaly;
                    final float P2x = dy * normalx;
                    final float P2y = dy * normaly;
                    final Vec2 vec10 = vA;
                    vec10.x -= mA * (P1x + P2x);
                    final Vec2 vec11 = vA;
                    vec11.y -= mA * (P1y + P2y);
                    final Vec2 vec12 = vB;
                    vec12.x += mB * (P1x + P2x);
                    final Vec2 vec13 = vB;
                    vec13.y += mB * (P1y + P2y);
                    wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
                    wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
                    cp1.normalImpulse = xx;
                    cp2.normalImpulse = xy;
                }
                else {
                    xx = -cp1.normalMass * bx;
                    xy = 0.0f;
                    vn2 = 0.0f;
                    vn3 = vc.K.ex.y * xx + by;
                    if (xx >= 0.0f && vn3 >= 0.0f) {
                        final float dx = xx - ax;
                        final float dy = xy - ay;
                        final float P1x = normalx * dx;
                        final float P1y = normaly * dx;
                        final float P2x = normalx * dy;
                        final float P2y = normaly * dy;
                        final Vec2 vec14 = vA;
                        vec14.x -= mA * (P1x + P2x);
                        final Vec2 vec15 = vA;
                        vec15.y -= mA * (P1y + P2y);
                        final Vec2 vec16 = vB;
                        vec16.x += mB * (P1x + P2x);
                        final Vec2 vec17 = vB;
                        vec17.y += mB * (P1y + P2y);
                        wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
                        wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
                        cp1.normalImpulse = xx;
                        cp2.normalImpulse = xy;
                    }
                    else {
                        xx = 0.0f;
                        xy = -cp2.normalMass * by;
                        vn2 = vc.K.ey.x * xy + bx;
                        vn3 = 0.0f;
                        if (xy >= 0.0f && vn2 >= 0.0f) {
                            final float dx = xx - ax;
                            final float dy = xy - ay;
                            final float P1x = normalx * dx;
                            final float P1y = normaly * dx;
                            final float P2x = normalx * dy;
                            final float P2y = normaly * dy;
                            final Vec2 vec18 = vA;
                            vec18.x -= mA * (P1x + P2x);
                            final Vec2 vec19 = vA;
                            vec19.y -= mA * (P1y + P2y);
                            final Vec2 vec20 = vB;
                            vec20.x += mB * (P1x + P2x);
                            final Vec2 vec21 = vB;
                            vec21.y += mB * (P1y + P2y);
                            wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
                            wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
                            cp1.normalImpulse = xx;
                            cp2.normalImpulse = xy;
                        }
                        else {
                            xx = 0.0f;
                            xy = 0.0f;
                            vn2 = bx;
                            vn3 = by;
                            if (vn2 >= 0.0f && vn3 >= 0.0f) {
                                final float dx = xx - ax;
                                final float dy = xy - ay;
                                final float P1x = normalx * dx;
                                final float P1y = normaly * dx;
                                final float P2x = normalx * dy;
                                final float P2y = normaly * dy;
                                final Vec2 vec22 = vA;
                                vec22.x -= mA * (P1x + P2x);
                                final Vec2 vec23 = vA;
                                vec23.y -= mA * (P1y + P2y);
                                final Vec2 vec24 = vB;
                                vec24.x += mB * (P1x + P2x);
                                final Vec2 vec25 = vB;
                                vec25.y += mB * (P1y + P2y);
                                wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
                                wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
                                cp1.normalImpulse = xx;
                                cp2.normalImpulse = xy;
                            }
                        }
                    }
                }
            }
            this.m_velocities[indexA].w = wA;
            this.m_velocities[indexB].w = wB;
        }
    }
    
    public void storeImpulses() {
        for (int i = 0; i < this.m_count; ++i) {
            final ContactVelocityConstraint vc = this.m_velocityConstraints[i];
            final Manifold manifold = this.m_contacts[vc.contactIndex].getManifold();
            for (int j = 0; j < vc.pointCount; ++j) {
                manifold.points[j].normalImpulse = vc.points[j].normalImpulse;
                manifold.points[j].tangentImpulse = vc.points[j].tangentImpulse;
            }
        }
    }
    
    public final boolean solvePositionConstraints() {
        float minSeparation = 0.0f;
        for (int i = 0; i < this.m_count; ++i) {
            final ContactPositionConstraint pc = this.m_positionConstraints[i];
            final int indexA = pc.indexA;
            final int indexB = pc.indexB;
            final float mA = pc.invMassA;
            final float iA = pc.invIA;
            final Vec2 localCenterA = pc.localCenterA;
            final float localCenterAx = localCenterA.x;
            final float localCenterAy = localCenterA.y;
            final float mB = pc.invMassB;
            final float iB = pc.invIB;
            final Vec2 localCenterB = pc.localCenterB;
            final float localCenterBx = localCenterB.x;
            final float localCenterBy = localCenterB.y;
            final int pointCount = pc.pointCount;
            final Vec2 cA = this.m_positions[indexA].c;
            float aA = this.m_positions[indexA].a;
            final Vec2 cB = this.m_positions[indexB].c;
            float aB = this.m_positions[indexB].a;
            for (int j = 0; j < pointCount; ++j) {
                final Rot xfAq = this.xfA.q;
                final Rot xfBq = this.xfB.q;
                xfAq.set(aA);
                xfBq.set(aB);
                this.xfA.p.x = cA.x - xfAq.c * localCenterAx + xfAq.s * localCenterAy;
                this.xfA.p.y = cA.y - xfAq.s * localCenterAx - xfAq.c * localCenterAy;
                this.xfB.p.x = cB.x - xfBq.c * localCenterBx + xfBq.s * localCenterBy;
                this.xfB.p.y = cB.y - xfBq.s * localCenterBx - xfBq.c * localCenterBy;
                final PositionSolverManifold psm = this.psolver;
                psm.initialize(pc, this.xfA, this.xfB, j);
                final Vec2 normal = psm.normal;
                final Vec2 point = psm.point;
                final float separation = psm.separation;
                final float rAx = point.x - cA.x;
                final float rAy = point.y - cA.y;
                final float rBx = point.x - cB.x;
                final float rBy = point.y - cB.y;
                minSeparation = MathUtils.min(minSeparation, separation);
                final float C = MathUtils.clamp(Settings.baumgarte * (separation + Settings.linearSlop), -Settings.maxLinearCorrection, 0.0f);
                final float rnA = rAx * normal.y - rAy * normal.x;
                final float rnB = rBx * normal.y - rBy * normal.x;
                final float K = mA + mB + iA * rnA * rnA + iB * rnB * rnB;
                final float impulse = (K > 0.0f) ? (-C / K) : 0.0f;
                final float Px = normal.x * impulse;
                final float Py = normal.y * impulse;
                final Vec2 vec2 = cA;
                vec2.x -= Px * mA;
                final Vec2 vec3 = cA;
                vec3.y -= Py * mA;
                aA -= iA * (rAx * Py - rAy * Px);
                final Vec2 vec4 = cB;
                vec4.x += Px * mB;
                final Vec2 vec5 = cB;
                vec5.y += Py * mB;
                aB += iB * (rBx * Py - rBy * Px);
            }
            this.m_positions[indexA].a = aA;
            this.m_positions[indexB].a = aB;
        }
        return minSeparation >= -3.0f * Settings.linearSlop;
    }
    
    public boolean solveTOIPositionConstraints(final int toiIndexA, final int toiIndexB) {
        float minSeparation = 0.0f;
        for (int i = 0; i < this.m_count; ++i) {
            final ContactPositionConstraint pc = this.m_positionConstraints[i];
            final int indexA = pc.indexA;
            final int indexB = pc.indexB;
            final Vec2 localCenterA = pc.localCenterA;
            final Vec2 localCenterB = pc.localCenterB;
            final float localCenterAx = localCenterA.x;
            final float localCenterAy = localCenterA.y;
            final float localCenterBx = localCenterB.x;
            final float localCenterBy = localCenterB.y;
            final int pointCount = pc.pointCount;
            float mA = 0.0f;
            float iA = 0.0f;
            if (indexA == toiIndexA || indexA == toiIndexB) {
                mA = pc.invMassA;
                iA = pc.invIA;
            }
            float mB = 0.0f;
            float iB = 0.0f;
            if (indexB == toiIndexA || indexB == toiIndexB) {
                mB = pc.invMassB;
                iB = pc.invIB;
            }
            final Vec2 cA = this.m_positions[indexA].c;
            float aA = this.m_positions[indexA].a;
            final Vec2 cB = this.m_positions[indexB].c;
            float aB = this.m_positions[indexB].a;
            for (int j = 0; j < pointCount; ++j) {
                final Rot xfAq = this.xfA.q;
                final Rot xfBq = this.xfB.q;
                xfAq.set(aA);
                xfBq.set(aB);
                this.xfA.p.x = cA.x - xfAq.c * localCenterAx + xfAq.s * localCenterAy;
                this.xfA.p.y = cA.y - xfAq.s * localCenterAx - xfAq.c * localCenterAy;
                this.xfB.p.x = cB.x - xfBq.c * localCenterBx + xfBq.s * localCenterBy;
                this.xfB.p.y = cB.y - xfBq.s * localCenterBx - xfBq.c * localCenterBy;
                final PositionSolverManifold psm = this.psolver;
                psm.initialize(pc, this.xfA, this.xfB, j);
                final Vec2 normal = psm.normal;
                final Vec2 point = psm.point;
                final float separation = psm.separation;
                final float rAx = point.x - cA.x;
                final float rAy = point.y - cA.y;
                final float rBx = point.x - cB.x;
                final float rBy = point.y - cB.y;
                minSeparation = MathUtils.min(minSeparation, separation);
                final float C = MathUtils.clamp(Settings.toiBaugarte * (separation + Settings.linearSlop), -Settings.maxLinearCorrection, 0.0f);
                final float rnA = rAx * normal.y - rAy * normal.x;
                final float rnB = rBx * normal.y - rBy * normal.x;
                final float K = mA + mB + iA * rnA * rnA + iB * rnB * rnB;
                final float impulse = (K > 0.0f) ? (-C / K) : 0.0f;
                final float Px = normal.x * impulse;
                final float Py = normal.y * impulse;
                final Vec2 vec2 = cA;
                vec2.x -= Px * mA;
                final Vec2 vec3 = cA;
                vec3.y -= Py * mA;
                aA -= iA * (rAx * Py - rAy * Px);
                final Vec2 vec4 = cB;
                vec4.x += Px * mB;
                final Vec2 vec5 = cB;
                vec5.y += Py * mB;
                aB += iB * (rBx * Py - rBy * Px);
            }
            this.m_positions[indexA].a = aA;
            this.m_positions[indexB].a = aB;
        }
        return minSeparation >= -1.5f * Settings.linearSlop;
    }
    
    public static class ContactSolverDef
    {
        public TimeStep step;
        public Contact[] contacts;
        public int count;
        public Position[] positions;
        public Velocity[] velocities;
    }
}
