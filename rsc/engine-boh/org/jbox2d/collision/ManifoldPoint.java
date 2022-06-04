// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.Vec2;

public class ManifoldPoint
{
    public final Vec2 localPoint;
    public float normalImpulse;
    public float tangentImpulse;
    public final ContactID id;
    
    public ManifoldPoint() {
        this.localPoint = new Vec2();
        final float n = 0.0f;
        this.tangentImpulse = n;
        this.normalImpulse = n;
        this.id = new ContactID();
    }
    
    public ManifoldPoint(final ManifoldPoint cp) {
        this.localPoint = cp.localPoint.clone();
        this.normalImpulse = cp.normalImpulse;
        this.tangentImpulse = cp.tangentImpulse;
        this.id = new ContactID(cp.id);
    }
    
    public void set(final ManifoldPoint cp) {
        this.localPoint.set(cp.localPoint);
        this.normalImpulse = cp.normalImpulse;
        this.tangentImpulse = cp.tangentImpulse;
        this.id.set(cp.id);
    }
}
