// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.io.Serializable;

public class Sweep implements Serializable
{
    private static final long serialVersionUID = 1L;
    public final Vec2 localCenter;
    public final Vec2 c0;
    public final Vec2 c;
    public float a0;
    public float a;
    public float alpha0;
    
    @Override
    public String toString() {
        String s = "Sweep:\nlocalCenter: " + this.localCenter + "\n";
        s = s + "c0: " + this.c0 + ", c: " + this.c + "\n";
        s = s + "a0: " + this.a0 + ", a: " + this.a + "\n";
        s = s + "alpha0: " + this.alpha0;
        return s;
    }
    
    public Sweep() {
        this.localCenter = new Vec2();
        this.c0 = new Vec2();
        this.c = new Vec2();
    }
    
    public final void normalize() {
        final float d = 6.2831855f * MathUtils.floor(this.a0 / 6.2831855f);
        this.a0 -= d;
        this.a -= d;
    }
    
    public final Sweep set(final Sweep other) {
        this.localCenter.set(other.localCenter);
        this.c0.set(other.c0);
        this.c.set(other.c);
        this.a0 = other.a0;
        this.a = other.a;
        this.alpha0 = other.alpha0;
        return this;
    }
    
    public final void getTransform(final Transform xf, final float beta) {
        assert xf != null;
        xf.p.x = (1.0f - beta) * this.c0.x + beta * this.c.x;
        xf.p.y = (1.0f - beta) * this.c0.y + beta * this.c.y;
        final float angle = (1.0f - beta) * this.a0 + beta * this.a;
        xf.q.set(angle);
        final Rot q = xf.q;
        final Vec2 p = xf.p;
        p.x -= q.c * this.localCenter.x - q.s * this.localCenter.y;
        final Vec2 p2 = xf.p;
        p2.y -= q.s * this.localCenter.x + q.c * this.localCenter.y;
    }
    
    public final void advance(final float alpha) {
        assert this.alpha0 < 1.0f;
        final float beta = (alpha - this.alpha0) / (1.0f - this.alpha0);
        final Vec2 c0 = this.c0;
        c0.x += beta * (this.c.x - this.c0.x);
        final Vec2 c2 = this.c0;
        c2.y += beta * (this.c.y - this.c0.y);
        this.a0 += beta * (this.a - this.a0);
        this.alpha0 = alpha;
    }
}
