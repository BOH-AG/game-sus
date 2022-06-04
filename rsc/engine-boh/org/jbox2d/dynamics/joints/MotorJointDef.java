// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class MotorJointDef extends JointDef
{
    public final Vec2 linearOffset;
    public float angularOffset;
    public float maxForce;
    public float maxTorque;
    public float correctionFactor;
    
    public MotorJointDef() {
        super(JointType.MOTOR);
        this.linearOffset = new Vec2();
        this.angularOffset = 0.0f;
        this.maxForce = 1.0f;
        this.maxTorque = 1.0f;
        this.correctionFactor = 0.3f;
    }
    
    public void initialize(final Body bA, final Body bB) {
        this.bodyA = bA;
        this.bodyB = bB;
        final Vec2 xB = this.bodyB.getPosition();
        this.bodyA.getLocalPointToOut(xB, this.linearOffset);
        final float angleA = this.bodyA.getAngle();
        final float angleB = this.bodyB.getAngle();
        this.angularOffset = angleB - angleA;
    }
}
