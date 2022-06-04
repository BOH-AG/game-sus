// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.common.Settings;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;

public class ContactVelocityConstraint
{
    public VelocityConstraintPoint[] points;
    public final Vec2 normal;
    public final Mat22 normalMass;
    public final Mat22 K;
    public int indexA;
    public int indexB;
    public float invMassA;
    public float invMassB;
    public float invIA;
    public float invIB;
    public float friction;
    public float restitution;
    public float tangentSpeed;
    public int pointCount;
    public int contactIndex;
    
    public ContactVelocityConstraint() {
        this.points = new VelocityConstraintPoint[Settings.maxManifoldPoints];
        this.normal = new Vec2();
        this.normalMass = new Mat22();
        this.K = new Mat22();
        for (int i = 0; i < this.points.length; ++i) {
            this.points[i] = new VelocityConstraintPoint();
        }
    }
    
    public static class VelocityConstraintPoint
    {
        public final Vec2 rA;
        public final Vec2 rB;
        public float normalImpulse;
        public float tangentImpulse;
        public float normalMass;
        public float tangentMass;
        public float velocityBias;
        
        public VelocityConstraintPoint() {
            this.rA = new Vec2();
            this.rB = new Vec2();
        }
    }
}
