// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.callbacks;

import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;

public class ContactFilter
{
    public boolean shouldCollide(final Fixture fixtureA, final Fixture fixtureB) {
        final Filter filterA = fixtureA.getFilterData();
        final Filter filterB = fixtureB.getFilterData();
        if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
        }
        final boolean collide = (filterA.maskBits & filterB.categoryBits) != 0x0 && (filterA.categoryBits & filterB.maskBits) != 0x0;
        return collide;
    }
}
