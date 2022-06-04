// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;

public class Jacobian
{
    public final Vec2 linearA;
    public float angularA;
    public float angularB;
    
    public Jacobian() {
        this.linearA = new Vec2();
    }
}
