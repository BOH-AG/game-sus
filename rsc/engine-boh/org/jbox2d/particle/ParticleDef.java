// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.common.Vec2;

public class ParticleDef
{
    int flags;
    public final Vec2 position;
    public final Vec2 velocity;
    public ParticleColor color;
    public Object userData;
    
    public ParticleDef() {
        this.position = new Vec2();
        this.velocity = new Vec2();
    }
}
