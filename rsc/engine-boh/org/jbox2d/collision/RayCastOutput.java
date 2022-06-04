// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.Vec2;

public class RayCastOutput
{
    public final Vec2 normal;
    public float fraction;
    
    public RayCastOutput() {
        this.normal = new Vec2();
        this.fraction = 0.0f;
    }
    
    public void set(final RayCastOutput rco) {
        this.normal.set(rco.normal);
        this.fraction = rco.fraction;
    }
}
