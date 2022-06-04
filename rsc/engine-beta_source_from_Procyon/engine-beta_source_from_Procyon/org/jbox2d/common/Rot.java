// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.io.Serializable;

public class Rot implements Serializable
{
    private static final long serialVersionUID = 1L;
    public float s;
    public float c;
    
    public Rot() {
        this.setIdentity();
    }
    
    public Rot(final float angle) {
        this.set(angle);
    }
    
    public float getSin() {
        return this.s;
    }
    
    @Override
    public String toString() {
        return "Rot(s:" + this.s + ", c:" + this.c + ")";
    }
    
    public float getCos() {
        return this.c;
    }
    
    public Rot set(final float angle) {
        this.s = MathUtils.sin(angle);
        this.c = MathUtils.cos(angle);
        return this;
    }
    
    public Rot set(final Rot other) {
        this.s = other.s;
        this.c = other.c;
        return this;
    }
    
    public Rot setIdentity() {
        this.s = 0.0f;
        this.c = 1.0f;
        return this;
    }
    
    public float getAngle() {
        return MathUtils.atan2(this.s, this.c);
    }
    
    public void getXAxis(final Vec2 xAxis) {
        xAxis.set(this.c, this.s);
    }
    
    public void getYAxis(final Vec2 yAxis) {
        yAxis.set(-this.s, this.c);
    }
    
    public Rot clone() {
        final Rot copy = new Rot();
        copy.s = this.s;
        copy.c = this.c;
        return copy;
    }
    
    public static final void mul(final Rot q, final Rot r, final Rot out) {
        final float tempc = q.c * r.c - q.s * r.s;
        out.s = q.s * r.c + q.c * r.s;
        out.c = tempc;
    }
    
    public static final void mulUnsafe(final Rot q, final Rot r, final Rot out) {
        assert r != out;
        assert q != out;
        out.s = q.s * r.c + q.c * r.s;
        out.c = q.c * r.c - q.s * r.s;
    }
    
    public static final void mulTrans(final Rot q, final Rot r, final Rot out) {
        final float tempc = q.c * r.c + q.s * r.s;
        out.s = q.c * r.s - q.s * r.c;
        out.c = tempc;
    }
    
    public static final void mulTransUnsafe(final Rot q, final Rot r, final Rot out) {
        out.s = q.c * r.s - q.s * r.c;
        out.c = q.c * r.c + q.s * r.s;
    }
    
    public static final void mulToOut(final Rot q, final Vec2 v, final Vec2 out) {
        final float tempy = q.s * v.x + q.c * v.y;
        out.x = q.c * v.x - q.s * v.y;
        out.y = tempy;
    }
    
    public static final void mulToOutUnsafe(final Rot q, final Vec2 v, final Vec2 out) {
        out.x = q.c * v.x - q.s * v.y;
        out.y = q.s * v.x + q.c * v.y;
    }
    
    public static final void mulTrans(final Rot q, final Vec2 v, final Vec2 out) {
        final float tempy = -q.s * v.x + q.c * v.y;
        out.x = q.c * v.x + q.s * v.y;
        out.y = tempy;
    }
    
    public static final void mulTransUnsafe(final Rot q, final Vec2 v, final Vec2 out) {
        out.x = q.c * v.x + q.s * v.y;
        out.y = -q.s * v.x + q.c * v.y;
    }
}
