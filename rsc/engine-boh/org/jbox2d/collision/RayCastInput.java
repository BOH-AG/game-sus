// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.Vec2;

public class RayCastInput
{
    public final Vec2 p1;
    public final Vec2 p2;
    public float maxFraction;
    
    public RayCastInput() {
        this.p1 = new Vec2();
        this.p2 = new Vec2();
        this.maxFraction = 0.0f;
    }
    
    public void set(final RayCastInput rci) {
        this.p1.set(rci.p1);
        this.p2.set(rci.p2);
        this.maxFraction = rci.maxFraction;
    }
}
