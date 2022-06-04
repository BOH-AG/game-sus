// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class WheelJointDef extends JointDef
{
    public final Vec2 localAnchorA;
    public final Vec2 localAnchorB;
    public final Vec2 localAxisA;
    public boolean enableMotor;
    public float maxMotorTorque;
    public float motorSpeed;
    public float frequencyHz;
    public float dampingRatio;
    
    public WheelJointDef() {
        super(JointType.WHEEL);
        this.localAnchorA = new Vec2();
        this.localAnchorB = new Vec2();
        (this.localAxisA = new Vec2()).set(1.0f, 0.0f);
        this.enableMotor = false;
        this.maxMotorTorque = 0.0f;
        this.motorSpeed = 0.0f;
    }
    
    public void initialize(final Body b1, final Body b2, final Vec2 anchor, final Vec2 axis) {
        this.bodyA = b1;
        this.bodyB = b2;
        b1.getLocalPointToOut(anchor, this.localAnchorA);
        b2.getLocalPointToOut(anchor, this.localAnchorB);
        this.bodyA.getLocalVectorToOut(axis, this.localAxisA);
    }
}
