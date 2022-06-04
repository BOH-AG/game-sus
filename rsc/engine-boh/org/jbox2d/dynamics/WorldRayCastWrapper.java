// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.collision.RayCastInput;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.callbacks.TreeRayCastCallback;

class WorldRayCastWrapper implements TreeRayCastCallback
{
    private final RayCastOutput output;
    private final Vec2 temp;
    private final Vec2 point;
    BroadPhase broadPhase;
    RayCastCallback callback;
    
    WorldRayCastWrapper() {
        this.output = new RayCastOutput();
        this.temp = new Vec2();
        this.point = new Vec2();
    }
    
    @Override
    public float raycastCallback(final RayCastInput input, final int nodeId) {
        final Object userData = this.broadPhase.getUserData(nodeId);
        final FixtureProxy proxy = (FixtureProxy)userData;
        final Fixture fixture = proxy.fixture;
        final int index = proxy.childIndex;
        final boolean hit = fixture.raycast(this.output, input, index);
        if (hit) {
            final float fraction = this.output.fraction;
            this.temp.set(input.p2).mulLocal(fraction);
            this.point.set(input.p1).mulLocal(1.0f - fraction).addLocal(this.temp);
            return this.callback.reportFixture(fixture, this.point, this.output.normal, fraction);
        }
        return input.maxFraction;
    }
}
