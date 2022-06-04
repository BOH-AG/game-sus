// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

public interface ContactCreator
{
    Contact contactCreateFcn(final IWorldPool p0, final Fixture p1, final Fixture p2);
    
    void contactDestroyFcn(final IWorldPool p0, final Contact p1);
}
