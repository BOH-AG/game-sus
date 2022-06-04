// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class ParticleBodyContact
{
    public int index;
    public Body body;
    float weight;
    public final Vec2 normal;
    float mass;
    
    public ParticleBodyContact() {
        this.normal = new Vec2();
    }
}
