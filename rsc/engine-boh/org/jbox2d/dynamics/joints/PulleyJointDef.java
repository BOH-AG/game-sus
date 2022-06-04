// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;
import org.jbox2d.common.Vec2;

public class PulleyJointDef extends JointDef
{
    public Vec2 groundAnchorA;
    public Vec2 groundAnchorB;
    public Vec2 localAnchorA;
    public Vec2 localAnchorB;
    public float lengthA;
    public float lengthB;
    public float ratio;
    
    public PulleyJointDef() {
        super(JointType.PULLEY);
        this.groundAnchorA = new Vec2(-1.0f, 1.0f);
        this.groundAnchorB = new Vec2(1.0f, 1.0f);
        this.localAnchorA = new Vec2(-1.0f, 0.0f);
        this.localAnchorB = new Vec2(1.0f, 0.0f);
        this.lengthA = 0.0f;
        this.lengthB = 0.0f;
        this.ratio = 1.0f;
        this.collideConnected = true;
    }
    
    public void initialize(final Body b1, final Body b2, final Vec2 ga1, final Vec2 ga2, final Vec2 anchor1, final Vec2 anchor2, final float r) {
        this.bodyA = b1;
        this.bodyB = b2;
        this.groundAnchorA = ga1;
        this.groundAnchorB = ga2;
        this.localAnchorA = this.bodyA.getLocalPoint(anchor1);
        this.localAnchorB = this.bodyB.getLocalPoint(anchor2);
        final Vec2 d1 = anchor1.sub(ga1);
        this.lengthA = d1.length();
        final Vec2 d2 = anchor2.sub(ga2);
        this.lengthB = d2.length();
        this.ratio = r;
        assert this.ratio > 1.1920929E-7f;
    }
}
