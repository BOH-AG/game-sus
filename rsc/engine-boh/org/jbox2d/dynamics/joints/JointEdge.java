// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.dynamics.Body;

public class JointEdge
{
    public Body other;
    public Joint joint;
    public JointEdge prev;
    public JointEdge next;
    
    public JointEdge() {
        this.other = null;
        this.joint = null;
        this.prev = null;
        this.next = null;
    }
}
