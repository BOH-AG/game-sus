// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.io.Serializable;

public class Vec3 implements Serializable
{
    private static final long serialVersionUID = 1L;
    public float x;
    public float y;
    public float z;
    
    public Vec3() {
        final float x = 0.0f;
        this.z = x;
        this.y = x;
        this.x = x;
    }
    
    public Vec3(final float argX, final float argY, final float argZ) {
        this.x = argX;
        this.y = argY;
        this.z = argZ;
    }
    
    public Vec3(final Vec3 copy) {
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
    }
    
    public Vec3 set(final Vec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }
    
    public Vec3 set(final float argX, final float argY, final float argZ) {
        this.x = argX;
        this.y = argY;
        this.z = argZ;
        return this;
    }
    
    public Vec3 addLocal(final Vec3 argVec) {
        this.x += argVec.x;
        this.y += argVec.y;
        this.z += argVec.z;
        return this;
    }
    
    public Vec3 add(final Vec3 argVec) {
        return new Vec3(this.x + argVec.x, this.y + argVec.y, this.z + argVec.z);
    }
    
    public Vec3 subLocal(final Vec3 argVec) {
        this.x -= argVec.x;
        this.y -= argVec.y;
        this.z -= argVec.z;
        return this;
    }
    
    public Vec3 sub(final Vec3 argVec) {
        return new Vec3(this.x - argVec.x, this.y - argVec.y, this.z - argVec.z);
    }
    
    public Vec3 mulLocal(final float argScalar) {
        this.x *= argScalar;
        this.y *= argScalar;
        this.z *= argScalar;
        return this;
    }
    
    public Vec3 mul(final float argScalar) {
        return new Vec3(this.x * argScalar, this.y * argScalar, this.z * argScalar);
    }
    
    public Vec3 negate() {
        return new Vec3(-this.x, -this.y, -this.z);
    }
    
    public Vec3 negateLocal() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }
    
    public void setZero() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }
    
    public Vec3 clone() {
        return new Vec3(this);
    }
    
    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Float.floatToIntBits(this.x);
        result = 31 * result + Float.floatToIntBits(this.y);
        result = 31 * result + Float.floatToIntBits(this.z);
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
        final Vec3 other = (Vec3)obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(other.x) && Float.floatToIntBits(this.y) == Float.floatToIntBits(other.y) && Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
    }
    
    public static final float dot(final Vec3 a, final Vec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
    
    public static final Vec3 cross(final Vec3 a, final Vec3 b) {
        return new Vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }
    
    public static final void crossToOut(final Vec3 a, final Vec3 b, final Vec3 out) {
        final float tempy = a.z * b.x - a.x * b.z;
        final float tempz = a.x * b.y - a.y * b.x;
        out.x = a.y * b.z - a.z * b.y;
        out.y = tempy;
        out.z = tempz;
    }
    
    public static final void crossToOutUnsafe(final Vec3 a, final Vec3 b, final Vec3 out) {
        assert out != b;
        assert out != a;
        out.x = a.y * b.z - a.z * b.y;
        out.y = a.z * b.x - a.x * b.z;
        out.z = a.x * b.y - a.y * b.x;
    }
}
