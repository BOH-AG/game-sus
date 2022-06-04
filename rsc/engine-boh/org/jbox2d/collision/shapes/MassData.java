// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.shapes;

import org.jbox2d.common.Vec2;

public class MassData
{
    public float mass;
    public final Vec2 center;
    public float I;
    
    public MassData() {
        final float n = 0.0f;
        this.I = n;
        this.mass = n;
        this.center = new Vec2();
    }
    
    public MassData(final MassData md) {
        this.mass = md.mass;
        this.I = md.I;
        this.center = md.center.clone();
    }
    
    public void set(final MassData md) {
        this.mass = md.mass;
        this.I = md.I;
        this.center.set(md.center);
    }
    
    public MassData clone() {
        return new MassData(this);
    }
}
