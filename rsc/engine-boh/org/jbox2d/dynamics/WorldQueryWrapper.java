// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.callbacks.TreeCallback;

class WorldQueryWrapper implements TreeCallback
{
    BroadPhase broadPhase;
    QueryCallback callback;
    
    @Override
    public boolean treeCallback(final int nodeId) {
        final FixtureProxy proxy = (FixtureProxy)this.broadPhase.getUserData(nodeId);
        return this.callback.reportFixture(proxy.fixture);
    }
}
