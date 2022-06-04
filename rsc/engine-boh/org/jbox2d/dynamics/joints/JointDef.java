// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;

public class JointDef
{
    public JointType type;
    public Object userData;
    public Body bodyA;
    public Body bodyB;
    public boolean collideConnected;
    
    public JointDef(final JointType type) {
        this.type = type;
        this.userData = null;
        this.bodyA = null;
        this.bodyB = null;
        this.collideConnected = false;
    }
}
