// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

public class ParticleGroupDef
{
    public int flags;
    public int groupFlags;
    public final Vec2 position;
    public float angle;
    public final Vec2 linearVelocity;
    public float angularVelocity;
    public ParticleColor color;
    public float strength;
    public Shape shape;
    public boolean destroyAutomatically;
    public Object userData;
    
    public ParticleGroupDef() {
        this.position = new Vec2();
        this.linearVelocity = new Vec2();
        this.flags = 0;
        this.groupFlags = 0;
        this.angle = 0.0f;
        this.angularVelocity = 0.0f;
        this.strength = 1.0f;
        this.destroyAutomatically = true;
    }
}
