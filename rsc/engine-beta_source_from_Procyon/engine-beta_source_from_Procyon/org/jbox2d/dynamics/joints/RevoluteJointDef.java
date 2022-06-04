// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class RevoluteJointDef extends JointDef
{
    public Vec2 localAnchorA;
    public Vec2 localAnchorB;
    public float referenceAngle;
    public boolean enableLimit;
    public float lowerAngle;
    public float upperAngle;
    public boolean enableMotor;
    public float motorSpeed;
    public float maxMotorTorque;
    
    public RevoluteJointDef() {
        super(JointType.REVOLUTE);
        this.localAnchorA = new Vec2(0.0f, 0.0f);
        this.localAnchorB = new Vec2(0.0f, 0.0f);
        this.referenceAngle = 0.0f;
        this.lowerAngle = 0.0f;
        this.upperAngle = 0.0f;
        this.maxMotorTorque = 0.0f;
        this.motorSpeed = 0.0f;
        this.enableLimit = false;
        this.enableMotor = false;
    }
    
    public void initialize(final Body b1, final Body b2, final Vec2 anchor) {
        this.bodyA = b1;
        this.bodyB = b2;
        this.bodyA.getLocalPointToOut(anchor, this.localAnchorA);
        this.bodyB.getLocalPointToOut(anchor, this.localAnchorB);
        this.referenceAngle = this.bodyB.getAngle() - this.bodyA.getAngle();
    }
}
