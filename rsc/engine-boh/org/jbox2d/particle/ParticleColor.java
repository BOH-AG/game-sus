// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.common.Color3f;

public class ParticleColor
{
    public byte r;
    public byte g;
    public byte b;
    public byte a;
    
    public ParticleColor() {
        this.r = 127;
        this.g = 127;
        this.b = 127;
        this.a = 50;
    }
    
    public ParticleColor(final byte r, final byte g, final byte b, final byte a) {
        this.set(r, g, b, a);
    }
    
    public ParticleColor(final Color3f color) {
        this.set(color);
    }
    
    public void set(final Color3f color) {
        this.r = (byte)(255.0f * color.x);
        this.g = (byte)(255.0f * color.y);
        this.b = (byte)(255.0f * color.z);
        this.a = -1;
    }
    
    public void set(final ParticleColor color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = color.a;
    }
    
    public boolean isZero() {
        return this.r == 0 && this.g == 0 && this.b == 0 && this.a == 0;
    }
    
    public void set(final byte r, final byte g, final byte b, final byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
