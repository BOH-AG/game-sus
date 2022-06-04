// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.collision.AABB;

public class FixtureProxy
{
    final AABB aabb;
    Fixture fixture;
    int childIndex;
    int proxyId;
    
    public FixtureProxy() {
        this.aabb = new AABB();
    }
}
