// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.dynamics.contacts.ContactVelocityConstraint;
import org.jbox2d.common.Sweep;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.common.Timer;
import org.jbox2d.dynamics.contacts.ContactSolver;
import org.jbox2d.dynamics.contacts.Velocity;
import org.jbox2d.dynamics.contacts.Position;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.callbacks.ContactListener;

public class Island
{
    public ContactListener m_listener;
    public Body[] m_bodies;
    public Contact[] m_contacts;
    public Joint[] m_joints;
    public Position[] m_positions;
    public Velocity[] m_velocities;
    public int m_bodyCount;
    public int m_jointCount;
    public int m_contactCount;
    public int m_bodyCapacity;
    public int m_contactCapacity;
    public int m_jointCapacity;
    private final ContactSolver contactSolver;
    private final Timer timer;
    private final SolverData solverData;
    private final ContactSolver.ContactSolverDef solverDef;
    private final ContactSolver toiContactSolver;
    private final ContactSolver.ContactSolverDef toiSolverDef;
    private final ContactImpulse impulse;
    
    public Island() {
        this.contactSolver = new ContactSolver();
        this.timer = new Timer();
        this.solverData = new SolverData();
        this.solverDef = new ContactSolver.ContactSolverDef();
        this.toiContactSolver = new ContactSolver();
        this.toiSolverDef = new ContactSolver.ContactSolverDef();
        this.impulse = new ContactImpulse();
    }
    
    public void init(final int bodyCapacity, final int contactCapacity, final int jointCapacity, final ContactListener listener) {
        this.m_bodyCapacity = bodyCapacity;
        this.m_contactCapacity = contactCapacity;
        this.m_jointCapacity = jointCapacity;
        this.m_bodyCount = 0;
        this.m_contactCount = 0;
        this.m_jointCount = 0;
        this.m_listener = listener;
        if (this.m_bodies == null || this.m_bodyCapacity > this.m_bodies.length) {
            this.m_bodies = new Body[this.m_bodyCapacity];
        }
        if (this.m_joints == null || this.m_jointCapacity > this.m_joints.length) {
            this.m_joints = new Joint[this.m_jointCapacity];
        }
        if (this.m_contacts == null || this.m_contactCapacity > this.m_contacts.length) {
            this.m_contacts = new Contact[this.m_contactCapacity];
        }
        if (this.m_velocities == null || this.m_bodyCapacity > this.m_velocities.length) {
            final Velocity[] old = (this.m_velocities == null) ? new Velocity[0] : this.m_velocities;
            System.arraycopy(old, 0, this.m_velocities = new Velocity[this.m_bodyCapacity], 0, old.length);
            for (int i = old.length; i < this.m_velocities.length; ++i) {
                this.m_velocities[i] = new Velocity();
            }
        }
        if (this.m_positions == null || this.m_bodyCapacity > this.m_positions.length) {
            final Position[] old2 = (this.m_positions == null) ? new Position[0] : this.m_positions;
            System.arraycopy(old2, 0, this.m_positions = new Position[this.m_bodyCapacity], 0, old2.length);
            for (int i = old2.length; i < this.m_positions.length; ++i) {
                this.m_positions[i] = new Position();
            }
        }
    }
    
    public void clear() {
        this.m_bodyCount = 0;
        this.m_contactCount = 0;
        this.m_jointCount = 0;
    }
    
    public void solve(final Profile profile, final TimeStep step, final Vec2 gravity, final boolean allowSleep) {
        final float h = step.dt;
        for (int i = 0; i < this.m_bodyCount; ++i) {
            final Body b = this.m_bodies[i];
            final Sweep bm_sweep = b.m_sweep;
            final Vec2 c = bm_sweep.c;
            final float a = bm_sweep.a;
            final Vec2 v = b.m_linearVelocity;
            float w = b.m_angularVelocity;
            bm_sweep.c0.set(bm_sweep.c);
            bm_sweep.a0 = bm_sweep.a;
            if (b.m_type == BodyType.DYNAMIC) {
                final Vec2 vec2 = v;
                vec2.x += h * (b.m_gravityScale * gravity.x + b.m_invMass * b.m_force.x);
                final Vec2 vec3 = v;
                vec3.y += h * (b.m_gravityScale * gravity.y + b.m_invMass * b.m_force.y);
                w += h * b.m_invI * b.m_torque;
                final Vec2 vec4 = v;
                vec4.x *= 1.0f / (1.0f + h * b.m_linearDamping);
                final Vec2 vec5 = v;
                vec5.y *= 1.0f / (1.0f + h * b.m_linearDamping);
                w *= 1.0f / (1.0f + h * b.m_angularDamping);
            }
            this.m_positions[i].c.x = c.x;
            this.m_positions[i].c.y = c.y;
            this.m_positions[i].a = a;
            this.m_velocities[i].v.x = v.x;
            this.m_velocities[i].v.y = v.y;
            this.m_velocities[i].w = w;
        }
        this.timer.reset();
        this.solverData.step = step;
        this.solverData.positions = this.m_positions;
        this.solverData.velocities = this.m_velocities;
        this.solverDef.step = step;
        this.solverDef.contacts = this.m_contacts;
        this.solverDef.count = this.m_contactCount;
        this.solverDef.positions = this.m_positions;
        this.solverDef.velocities = this.m_velocities;
        this.contactSolver.init(this.solverDef);
        this.contactSolver.initializeVelocityConstraints();
        if (step.warmStarting) {
            this.contactSolver.warmStart();
        }
        for (int i = 0; i < this.m_jointCount; ++i) {
            this.m_joints[i].initVelocityConstraints(this.solverData);
        }
        profile.solveInit.accum(this.timer.getMilliseconds());
        this.timer.reset();
        for (int i = 0; i < step.velocityIterations; ++i) {
            for (int j = 0; j < this.m_jointCount; ++j) {
                this.m_joints[j].solveVelocityConstraints(this.solverData);
            }
            this.contactSolver.solveVelocityConstraints();
        }
        this.contactSolver.storeImpulses();
        profile.solveVelocity.accum(this.timer.getMilliseconds());
        for (int i = 0; i < this.m_bodyCount; ++i) {
            final Vec2 c2 = this.m_positions[i].c;
            float a2 = this.m_positions[i].a;
            final Vec2 v2 = this.m_velocities[i].v;
            float w2 = this.m_velocities[i].w;
            final float translationx = v2.x * h;
            final float translationy = v2.y * h;
            if (translationx * translationx + translationy * translationy > Settings.maxTranslationSquared) {
                final float ratio = Settings.maxTranslation / MathUtils.sqrt(translationx * translationx + translationy * translationy);
                final Vec2 vec6 = v2;
                vec6.x *= ratio;
                final Vec2 vec7 = v2;
                vec7.y *= ratio;
            }
            final float rotation = h * w2;
            if (rotation * rotation > Settings.maxRotationSquared) {
                final float ratio2 = Settings.maxRotation / MathUtils.abs(rotation);
                w2 *= ratio2;
            }
            final Vec2 vec8 = c2;
            vec8.x += h * v2.x;
            final Vec2 vec9 = c2;
            vec9.y += h * v2.y;
            a2 += h * w2;
            this.m_positions[i].a = a2;
            this.m_velocities[i].w = w2;
        }
        this.timer.reset();
        boolean positionSolved = false;
        for (int k = 0; k < step.positionIterations; ++k) {
            final boolean contactsOkay = this.contactSolver.solvePositionConstraints();
            boolean jointsOkay = true;
            for (int l = 0; l < this.m_jointCount; ++l) {
                final boolean jointOkay = this.m_joints[l].solvePositionConstraints(this.solverData);
                jointsOkay = (jointsOkay && jointOkay);
            }
            if (contactsOkay && jointsOkay) {
                positionSolved = true;
                break;
            }
        }
        for (int k = 0; k < this.m_bodyCount; ++k) {
            final Body body = this.m_bodies[k];
            body.m_sweep.c.x = this.m_positions[k].c.x;
            body.m_sweep.c.y = this.m_positions[k].c.y;
            body.m_sweep.a = this.m_positions[k].a;
            body.m_linearVelocity.x = this.m_velocities[k].v.x;
            body.m_linearVelocity.y = this.m_velocities[k].v.y;
            body.m_angularVelocity = this.m_velocities[k].w;
            body.synchronizeTransform();
        }
        profile.solvePosition.accum(this.timer.getMilliseconds());
        this.report(this.contactSolver.m_velocityConstraints);
        if (allowSleep) {
            float minSleepTime = Float.MAX_VALUE;
            final float linTolSqr = Settings.linearSleepTolerance * Settings.linearSleepTolerance;
            final float angTolSqr = Settings.angularSleepTolerance * Settings.angularSleepTolerance;
            for (int m = 0; m < this.m_bodyCount; ++m) {
                final Body b2 = this.m_bodies[m];
                if (b2.getType() != BodyType.STATIC) {
                    if ((b2.m_flags & 0x4) == 0x0 || b2.m_angularVelocity * b2.m_angularVelocity > angTolSqr || Vec2.dot(b2.m_linearVelocity, b2.m_linearVelocity) > linTolSqr) {
                        b2.m_sleepTime = 0.0f;
                        minSleepTime = 0.0f;
                    }
                    else {
                        final Body body2 = b2;
                        body2.m_sleepTime += h;
                        minSleepTime = MathUtils.min(minSleepTime, b2.m_sleepTime);
                    }
                }
            }
            if (minSleepTime >= Settings.timeToSleep && positionSolved) {
                for (int m = 0; m < this.m_bodyCount; ++m) {
                    final Body b2 = this.m_bodies[m];
                    b2.setAwake(false);
                }
            }
        }
    }
    
    public void solveTOI(final TimeStep subStep, final int toiIndexA, final int toiIndexB) {
        assert toiIndexA < this.m_bodyCount;
        assert toiIndexB < this.m_bodyCount;
        for (int i = 0; i < this.m_bodyCount; ++i) {
            this.m_positions[i].c.x = this.m_bodies[i].m_sweep.c.x;
            this.m_positions[i].c.y = this.m_bodies[i].m_sweep.c.y;
            this.m_positions[i].a = this.m_bodies[i].m_sweep.a;
            this.m_velocities[i].v.x = this.m_bodies[i].m_linearVelocity.x;
            this.m_velocities[i].v.y = this.m_bodies[i].m_linearVelocity.y;
            this.m_velocities[i].w = this.m_bodies[i].m_angularVelocity;
        }
        this.toiSolverDef.contacts = this.m_contacts;
        this.toiSolverDef.count = this.m_contactCount;
        this.toiSolverDef.step = subStep;
        this.toiSolverDef.positions = this.m_positions;
        this.toiSolverDef.velocities = this.m_velocities;
        this.toiContactSolver.init(this.toiSolverDef);
        for (int i = 0; i < subStep.positionIterations; ++i) {
            final boolean contactsOkay = this.toiContactSolver.solveTOIPositionConstraints(toiIndexA, toiIndexB);
            if (contactsOkay) {
                break;
            }
        }
        this.m_bodies[toiIndexA].m_sweep.c0.x = this.m_positions[toiIndexA].c.x;
        this.m_bodies[toiIndexA].m_sweep.c0.y = this.m_positions[toiIndexA].c.y;
        this.m_bodies[toiIndexA].m_sweep.a0 = this.m_positions[toiIndexA].a;
        this.m_bodies[toiIndexB].m_sweep.c0.set(this.m_positions[toiIndexB].c);
        this.m_bodies[toiIndexB].m_sweep.a0 = this.m_positions[toiIndexB].a;
        this.toiContactSolver.initializeVelocityConstraints();
        for (int i = 0; i < subStep.velocityIterations; ++i) {
            this.toiContactSolver.solveVelocityConstraints();
        }
        final float h = subStep.dt;
        for (int j = 0; j < this.m_bodyCount; ++j) {
            final Vec2 c = this.m_positions[j].c;
            float a = this.m_positions[j].a;
            final Vec2 v = this.m_velocities[j].v;
            float w = this.m_velocities[j].w;
            final float translationx = v.x * h;
            final float translationy = v.y * h;
            if (translationx * translationx + translationy * translationy > Settings.maxTranslationSquared) {
                final float ratio = Settings.maxTranslation / MathUtils.sqrt(translationx * translationx + translationy * translationy);
                v.mulLocal(ratio);
            }
            final float rotation = h * w;
            if (rotation * rotation > Settings.maxRotationSquared) {
                final float ratio2 = Settings.maxRotation / MathUtils.abs(rotation);
                w *= ratio2;
            }
            final Vec2 vec2 = c;
            vec2.x += v.x * h;
            final Vec2 vec3 = c;
            vec3.y += v.y * h;
            a += h * w;
            this.m_positions[j].c.x = c.x;
            this.m_positions[j].c.y = c.y;
            this.m_positions[j].a = a;
            this.m_velocities[j].v.x = v.x;
            this.m_velocities[j].v.y = v.y;
            this.m_velocities[j].w = w;
            final Body body = this.m_bodies[j];
            body.m_sweep.c.x = c.x;
            body.m_sweep.c.y = c.y;
            body.m_sweep.a = a;
            body.m_linearVelocity.x = v.x;
            body.m_linearVelocity.y = v.y;
            body.m_angularVelocity = w;
            body.synchronizeTransform();
        }
        this.report(this.toiContactSolver.m_velocityConstraints);
    }
    
    public void add(final Body body) {
        assert this.m_bodyCount < this.m_bodyCapacity;
        body.m_islandIndex = this.m_bodyCount;
        this.m_bodies[this.m_bodyCount] = body;
        ++this.m_bodyCount;
    }
    
    public void add(final Contact contact) {
        assert this.m_contactCount < this.m_contactCapacity;
        this.m_contacts[this.m_contactCount++] = contact;
    }
    
    public void add(final Joint joint) {
        assert this.m_jointCount < this.m_jointCapacity;
        this.m_joints[this.m_jointCount++] = joint;
    }
    
    public void report(final ContactVelocityConstraint[] constraints) {
        if (this.m_listener == null) {
            return;
        }
        for (int i = 0; i < this.m_contactCount; ++i) {
            final Contact c = this.m_contacts[i];
            final ContactVelocityConstraint vc = constraints[i];
            this.impulse.count = vc.pointCount;
            for (int j = 0; j < vc.pointCount; ++j) {
                this.impulse.normalImpulses[j] = vc.points[j].normalImpulse;
                this.impulse.tangentImpulses[j] = vc.points[j].tangentImpulse;
            }
            this.m_listener.postSolve(c, this.impulse);
        }
    }
}
