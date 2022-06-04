// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

public class RaycastResult
{
    public float lambda;
    public final Vec2 normal;
    
    public RaycastResult() {
        this.lambda = 0.0f;
        this.normal = new Vec2();
    }
    
    public RaycastResult set(final RaycastResult argOther) {
        this.lambda = argOther.lambda;
        this.normal.set(argOther.normal);
        return this;
    }
}
