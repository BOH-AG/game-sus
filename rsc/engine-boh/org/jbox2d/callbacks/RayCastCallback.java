// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.callbacks;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

public interface RayCastCallback
{
    float reportFixture(final Fixture p0, final Vec2 p1, final Vec2 p2, final float p3);
}
