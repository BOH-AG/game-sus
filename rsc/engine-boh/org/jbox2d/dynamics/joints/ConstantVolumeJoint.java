// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.contacts.Velocity;
import org.jbox2d.dynamics.SolverData;
import org.jbox2d.common.Settings;
import org.jbox2d.common.MathUtils;
import org.jbox2d.dynamics.contacts.Position;
import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class ConstantVolumeJoint extends Joint
{
    private final Body[] bodies;
    private float[] targetLengths;
    private float targetVolume;
    private Vec2[] normals;
    private float m_impulse;
    private World world;
    private DistanceJoint[] distanceJoints;
    
    public Body[] getBodies() {
        return this.bodies;
    }
    
    public DistanceJoint[] getJoints() {
        return this.distanceJoints;
    }
    
    public void inflate(final float factor) {
        this.targetVolume *= factor;
    }
    
    public ConstantVolumeJoint(final World argWorld, final ConstantVolumeJointDef def) {
        super(argWorld.getPool(), def);
        this.m_impulse = 0.0f;
        this.world = argWorld;
        if (def.bodies.size() <= 2) {
            throw new IllegalArgumentException("You cannot create a constant volume joint with less than three bodies.");
        }
        this.bodies = def.bodies.toArray(new Body[0]);
        this.targetLengths = new float[this.bodies.length];
        for (int i = 0; i < this.targetLengths.length; ++i) {
            final int next = (i == this.targetLengths.length - 1) ? 0 : (i + 1);
            final float dist = this.bodies[i].getWorldCenter().sub(this.bodies[next].getWorldCenter()).length();
            this.targetLengths[i] = dist;
        }
        this.targetVolume = this.getBodyArea();
        if (def.joints != null && def.joints.size() != def.bodies.size()) {
            throw new IllegalArgumentException("Incorrect joint definition.  Joints have to correspond to the bodies");
        }
        if (def.joints == null) {
            final DistanceJointDef djd = new DistanceJointDef();
            this.distanceJoints = new DistanceJoint[this.bodies.length];
            for (int j = 0; j < this.targetLengths.length; ++j) {
                final int next2 = (j == this.targetLengths.length - 1) ? 0 : (j + 1);
                djd.frequencyHz = def.frequencyHz;
                djd.dampingRatio = def.dampingRatio;
                djd.collideConnected = def.collideConnected;
                djd.initialize(this.bodies[j], this.bodies[next2], this.bodies[j].getWorldCenter(), this.bodies[next2].getWorldCenter());
                this.distanceJoints[j] = (DistanceJoint)this.world.createJoint(djd);
            }
        }
        else {
            this.distanceJoints = def.joints.toArray(new DistanceJoint[0]);
        }
        this.normals = new Vec2[this.bodies.length];
        for (int i = 0; i < this.normals.length; ++i) {
            this.normals[i] = new Vec2();
        }
    }
    
    @Override
    public void destructor() {
        for (int i = 0; i < this.distanceJoints.length; ++i) {
            this.world.destroyJoint(this.distanceJoints[i]);
        }
    }
    
    private float getBodyArea() {
        float area = 0.0f;
        for (int i = 0; i < this.bodies.length; ++i) {
            final int next = (i == this.bodies.length - 1) ? 0 : (i + 1);
            area += this.bodies[i].getWorldCenter().x * this.bodies[next].getWorldCenter().y - this.bodies[next].getWorldCenter().x * this.bodies[i].getWorldCenter().y;
        }
        area *= 0.5f;
        return area;
    }
    
    private float getSolverArea(final Position[] positions) {
        float area = 0.0f;
        for (int i = 0; i < this.bodies.length; ++i) {
            final int next = (i == this.bodies.length - 1) ? 0 : (i + 1);
            area += positions[this.bodies[i].m_islandIndex].c.x * positions[this.bodies[next].m_islandIndex].c.y - positions[this.bodies[next].m_islandIndex].c.x * positions[this.bodies[i].m_islandIndex].c.y;
        }
        area *= 0.5f;
        return area;
    }
    
    private boolean constrainEdges(final Position[] positions) {
        float perimeter = 0.0f;
        for (int i = 0; i < this.bodies.length; ++i) {
            final int next = (i == this.bodies.length - 1) ? 0 : (i + 1);
            final float dx = positions[this.bodies[next].m_islandIndex].c.x - positions[this.bodies[i].m_islandIndex].c.x;
            final float dy = positions[this.bodies[next].m_islandIndex].c.y - positions[this.bodies[i].m_islandIndex].c.y;
            float dist = MathUtils.sqrt(dx * dx + dy * dy);
            if (dist < 1.1920929E-7f) {
                dist = 1.0f;
            }
            this.normals[i].x = dy / dist;
            this.normals[i].y = -dx / dist;
            perimeter += dist;
        }
        final Vec2 delta = this.pool.popVec2();
        final float deltaArea = this.targetVolume - this.getSolverArea(positions);
        final float toExtrude = 0.5f * deltaArea / perimeter;
        boolean done = true;
        for (int j = 0; j < this.bodies.length; ++j) {
            final int next2 = (j == this.bodies.length - 1) ? 0 : (j + 1);
            delta.set(toExtrude * (this.normals[j].x + this.normals[next2].x), toExtrude * (this.normals[j].y + this.normals[next2].y));
            final float normSqrd = delta.lengthSquared();
            if (normSqrd > Settings.maxLinearCorrection * Settings.maxLinearCorrection) {
                delta.mulLocal(Settings.maxLinearCorrection / MathUtils.sqrt(normSqrd));
            }
            if (normSqrd > Settings.linearSlop * Settings.linearSlop) {
                done = false;
            }
            final Vec2 c = positions[this.bodies[next2].m_islandIndex].c;
            c.x += delta.x;
            final Vec2 c2 = positions[this.bodies[next2].m_islandIndex].c;
            c2.y += delta.y;
        }
        this.pool.pushVec2(1);
        return done;
    }
    
    @Override
    public void initVelocityConstraints(final SolverData step) {
        final Velocity[] velocities = step.velocities;
        final Position[] positions = step.positions;
        final Vec2[] d = this.pool.getVec2Array(this.bodies.length);
        for (int i = 0; i < this.bodies.length; ++i) {
            final int prev = (i == 0) ? (this.bodies.length - 1) : (i - 1);
            final int next = (i == this.bodies.length - 1) ? 0 : (i + 1);
            d[i].set(positions[this.bodies[next].m_islandIndex].c);
            d[i].subLocal(positions[this.bodies[prev].m_islandIndex].c);
        }
        if (step.step.warmStarting) {
            this.m_impulse *= step.step.dtRatio;
            for (int i = 0; i < this.bodies.length; ++i) {
                final Vec2 v = velocities[this.bodies[i].m_islandIndex].v;
                v.x += this.bodies[i].m_invMass * d[i].y * 0.5f * this.m_impulse;
                final Vec2 v2 = velocities[this.bodies[i].m_islandIndex].v;
                v2.y += this.bodies[i].m_invMass * -d[i].x * 0.5f * this.m_impulse;
            }
        }
        else {
            this.m_impulse = 0.0f;
        }
    }
    
    @Override
    public boolean solvePositionConstraints(final SolverData step) {
        return this.constrainEdges(step.positions);
    }
    
    @Override
    public void solveVelocityConstraints(final SolverData step) {
        float crossMassSum = 0.0f;
        float dotMassSum = 0.0f;
        final Velocity[] velocities = step.velocities;
        final Position[] positions = step.positions;
        final Vec2[] d = this.pool.getVec2Array(this.bodies.length);
        for (int i = 0; i < this.bodies.length; ++i) {
            final int prev = (i == 0) ? (this.bodies.length - 1) : (i - 1);
            final int next = (i == this.bodies.length - 1) ? 0 : (i + 1);
            d[i].set(positions[this.bodies[next].m_islandIndex].c);
            d[i].subLocal(positions[this.bodies[prev].m_islandIndex].c);
            dotMassSum += d[i].lengthSquared() / this.bodies[i].getMass();
            crossMassSum += Vec2.cross(velocities[this.bodies[i].m_islandIndex].v, d[i]);
        }
        final float lambda = -2.0f * crossMassSum / dotMassSum;
        this.m_impulse += lambda;
        for (int j = 0; j < this.bodies.length; ++j) {
            final Vec2 v = velocities[this.bodies[j].m_islandIndex].v;
            v.x += this.bodies[j].m_invMass * d[j].y * 0.5f * lambda;
            final Vec2 v2 = velocities[this.bodies[j].m_islandIndex].v;
            v2.y += this.bodies[j].m_invMass * -d[j].x * 0.5f * lambda;
        }
    }
    
    @Override
    public void getAnchorA(final Vec2 argOut) {
    }
    
    @Override
    public void getAnchorB(final Vec2 argOut) {
    }
    
    @Override
    public void getReactionForce(final float inv_dt, final Vec2 argOut) {
    }
    
    @Override
    public float getReactionTorque(final float inv_dt) {
        return 0.0f;
    }
}
