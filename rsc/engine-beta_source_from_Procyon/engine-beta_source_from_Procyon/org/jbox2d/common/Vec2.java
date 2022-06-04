// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.io.Serializable;

public class Vec2 implements Serializable
{
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    
    public Vec2() {
        this(0.0f, 0.0f);
    }
    
    public Vec2(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vec2(final Vec2 toCopy) {
        this(toCopy.x, toCopy.y);
    }
    
    public final void setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
    }
    
    public final Vec2 set(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public final Vec2 set(final Vec2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }
    
    public final Vec2 add(final Vec2 v) {
        return new Vec2(this.x + v.x, this.y + v.y);
    }
    
    public final Vec2 sub(final Vec2 v) {
        return new Vec2(this.x - v.x, this.y - v.y);
    }
    
    public final Vec2 mul(final float a) {
        return new Vec2(this.x * a, this.y * a);
    }
    
    public final Vec2 negate() {
        return new Vec2(-this.x, -this.y);
    }
    
    public final Vec2 negateLocal() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }
    
    public final Vec2 addLocal(final Vec2 v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }
    
    public final Vec2 addLocal(final float x, final float y) {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public final Vec2 subLocal(final Vec2 v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }
    
    public final Vec2 mulLocal(final float a) {
        this.x *= a;
        this.y *= a;
        return this;
    }
    
    public final Vec2 skew() {
        return new Vec2(-this.y, this.x);
    }
    
    public final void skew(final Vec2 out) {
        out.x = -this.y;
        out.y = this.x;
    }
    
    public final float length() {
        return MathUtils.sqrt(this.x * this.x + this.y * this.y);
    }
    
    public final float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }
    
    public final float normalize() {
        final float length = this.length();
        if (length < 1.1920929E-7f) {
            return 0.0f;
        }
        final float invLength = 1.0f / length;
        this.x *= invLength;
        this.y *= invLength;
        return length;
    }
    
    public final boolean isValid() {
        return !Float.isNaN(this.x) && !Float.isInfinite(this.x) && !Float.isNaN(this.y) && !Float.isInfinite(this.y);
    }
    
    public final Vec2 abs() {
        return new Vec2(MathUtils.abs(this.x), MathUtils.abs(this.y));
    }
    
    public final void absLocal() {
        this.x = MathUtils.abs(this.x);
        this.y = MathUtils.abs(this.y);
    }
    
    public final Vec2 clone() {
        return new Vec2(this.x, this.y);
    }
    
    @Override
    public final String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
    
    public static final Vec2 abs(final Vec2 a) {
        return new Vec2(MathUtils.abs(a.x), MathUtils.abs(a.y));
    }
    
    public static final void absToOut(final Vec2 a, final Vec2 out) {
        out.x = MathUtils.abs(a.x);
        out.y = MathUtils.abs(a.y);
    }
    
    public static final float dot(final Vec2 a, final Vec2 b) {
        return a.x * b.x + a.y * b.y;
    }
    
    public static final float cross(final Vec2 a, final Vec2 b) {
        return a.x * b.y - a.y * b.x;
    }
    
    public static final Vec2 cross(final Vec2 a, final float s) {
        return new Vec2(s * a.y, -s * a.x);
    }
    
    public static final void crossToOut(final Vec2 a, final float s, final Vec2 out) {
        final float tempy = -s * a.x;
        out.x = s * a.y;
        out.y = tempy;
    }
    
    public static final void crossToOutUnsafe(final Vec2 a, final float s, final Vec2 out) {
        assert out != a;
        out.x = s * a.y;
        out.y = -s * a.x;
    }
    
    public static final Vec2 cross(final float s, final Vec2 a) {
        return new Vec2(-s * a.y, s * a.x);
    }
    
    public static final void crossToOut(final float s, final Vec2 a, final Vec2 out) {
        final float tempY = s * a.x;
        out.x = -s * a.y;
        out.y = tempY;
    }
    
    public static final void crossToOutUnsafe(final float s, final Vec2 a, final Vec2 out) {
        assert out != a;
        out.x = -s * a.y;
        out.y = s * a.x;
    }
    
    public static final void negateToOut(final Vec2 a, final Vec2 out) {
        out.x = -a.x;
        out.y = -a.y;
    }
    
    public static final Vec2 min(final Vec2 a, final Vec2 b) {
        return new Vec2((a.x < b.x) ? a.x : b.x, (a.y < b.y) ? a.y : b.y);
    }
    
    public static final Vec2 max(final Vec2 a, final Vec2 b) {
        return new Vec2((a.x > b.x) ? a.x : b.x, (a.y > b.y) ? a.y : b.y);
    }
    
    public static final void minToOut(final Vec2 a, final Vec2 b, final Vec2 out) {
        out.x = ((a.x < b.x) ? a.x : b.x);
        out.y = ((a.y < b.y) ? a.y : b.y);
    }
    
    public static final void maxToOut(final Vec2 a, final Vec2 b, final Vec2 out) {
        out.x = ((a.x > b.x) ? a.x : b.x);
        out.y = ((a.y > b.y) ? a.y : b.y);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Float.floatToIntBits(this.x);
        result = 31 * result + Float.floatToIntBits(this.y);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Vec2 other = (Vec2)obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(other.x) && Float.floatToIntBits(this.y) == Float.floatToIntBits(other.y);
    }
}
