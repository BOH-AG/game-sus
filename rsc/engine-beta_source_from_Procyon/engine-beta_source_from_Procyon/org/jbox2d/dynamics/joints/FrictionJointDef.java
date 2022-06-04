// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class FrictionJointDef extends JointDef
{
    public final Vec2 localAnchorA;
    public final Vec2 localAnchorB;
    public float maxForce;
    public float maxTorque;
    
    public FrictionJointDef() {
        super(JointType.FRICTION);
        this.localAnchorA = new Vec2();
        this.localAnchorB = new Vec2();
        this.maxForce = 0.0f;
        this.maxTorque = 0.0f;
    }
    
    public void initialize(final Body bA, final Body bB, final Vec2 anchor) {
        this.bodyA = bA;
        this.bodyB = bB;
        bA.getLocalPointToOut(anchor, this.localAnchorA);
        bB.getLocalPointToOut(anchor, this.localAnchorB);
    }
}
