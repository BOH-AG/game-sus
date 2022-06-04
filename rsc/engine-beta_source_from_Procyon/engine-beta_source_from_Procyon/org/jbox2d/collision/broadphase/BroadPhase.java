// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.broadphase;

import org.jbox2d.collision.RayCastInput;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.callbacks.PairCallback;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;

public interface BroadPhase
{
    public static final int NULL_PROXY = -1;
    
    int createProxy(final AABB p0, final Object p1);
    
    void destroyProxy(final int p0);
    
    void moveProxy(final int p0, final AABB p1, final Vec2 p2);
    
    void touchProxy(final int p0);
    
    Object getUserData(final int p0);
    
    AABB getFatAABB(final int p0);
    
    boolean testOverlap(final int p0, final int p1);
    
    int getProxyCount();
    
    void drawTree(final DebugDraw p0);
    
    void updatePairs(final PairCallback p0);
    
    void query(final TreeCallback p0, final AABB p1);
    
    void raycast(final TreeRayCastCallback p0, final RayCastInput p1);
    
    int getTreeHeight();
    
    int getTreeBalance();
    
    float getTreeQuality();
}
