// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.callbacks;

import org.jbox2d.common.Settings;

public class ContactImpulse
{
    public float[] normalImpulses;
    public float[] tangentImpulses;
    public int count;
    
    public ContactImpulse() {
        this.normalImpulses = new float[Settings.maxManifoldPoints];
        this.tangentImpulses = new float[Settings.maxManifoldPoints];
    }
}
