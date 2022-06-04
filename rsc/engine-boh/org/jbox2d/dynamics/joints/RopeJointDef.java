// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;

public class RopeJointDef extends JointDef
{
    public final Vec2 localAnchorA;
    public final Vec2 localAnchorB;
    public float maxLength;
    
    public RopeJointDef() {
        super(JointType.ROPE);
        this.localAnchorA = new Vec2();
        this.localAnchorB = new Vec2();
        this.localAnchorA.set(-1.0f, 0.0f);
        this.localAnchorB.set(1.0f, 0.0f);
    }
}
