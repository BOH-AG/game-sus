// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class WeldJointDef extends JointDef
{
    public final Vec2 localAnchorA;
    public final Vec2 localAnchorB;
    public float referenceAngle;
    public float frequencyHz;
    public float dampingRatio;
    
    public WeldJointDef() {
        super(JointType.WELD);
        this.localAnchorA = new Vec2();
        this.localAnchorB = new Vec2();
        this.referenceAngle = 0.0f;
    }
    
    public void initialize(final Body bA, final Body bB, final Vec2 anchor) {
        this.bodyA = bA;
        this.bodyB = bB;
        this.bodyA.getLocalPointToOut(anchor, this.localAnchorA);
        this.bodyB.getLocalPointToOut(anchor, this.localAnchorB);
        this.referenceAngle = this.bodyB.getAngle() - this.bodyA.getAngle();
    }
}
