// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.callbacks;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.Joint;

public interface DestructionListener
{
    void sayGoodbye(final Joint p0);
    
    void sayGoodbye(final Fixture p0);
}
