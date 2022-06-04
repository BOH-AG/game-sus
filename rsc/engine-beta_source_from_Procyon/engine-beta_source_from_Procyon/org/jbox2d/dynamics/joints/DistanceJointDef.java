// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class DistanceJointDef extends JointDef
{
    public final Vec2 localAnchorA;
    public final Vec2 localAnchorB;
    public float length;
    public float frequencyHz;
    public float dampingRatio;
    
    public DistanceJointDef() {
        super(JointType.DISTANCE);
        this.localAnchorA = new Vec2(0.0f, 0.0f);
        this.localAnchorB = new Vec2(0.0f, 0.0f);
        this.length = 1.0f;
        this.frequencyHz = 0.0f;
        this.dampingRatio = 0.0f;
    }
    
    public void initialize(final Body b1, final Body b2, final Vec2 anchor1, final Vec2 anchor2) {
        this.bodyA = b1;
        this.bodyB = b2;
        this.localAnchorA.set(this.bodyA.getLocalPoint(anchor1));
        this.localAnchorB.set(this.bodyB.getLocalPoint(anchor2));
        final Vec2 d = anchor2.sub(anchor1);
        this.length = d.length();
    }
}
