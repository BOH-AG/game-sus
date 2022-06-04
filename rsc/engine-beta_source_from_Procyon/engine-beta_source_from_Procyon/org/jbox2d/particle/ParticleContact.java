// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.common.Vec2;

public class ParticleContact
{
    public int indexA;
    public int indexB;
    public int flags;
    public float weight;
    public final Vec2 normal;
    
    public ParticleContact() {
        this.normal = new Vec2();
    }
}
