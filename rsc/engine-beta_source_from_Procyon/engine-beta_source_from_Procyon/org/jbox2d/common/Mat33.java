// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.io.Serializable;

public class Mat33 implements Serializable
{
    private static final long serialVersionUID = 2L;
    public static final Mat33 IDENTITY;
    public final Vec3 ex;
    public final Vec3 ey;
    public final Vec3 ez;
    
    public Mat33() {
        this.ex = new Vec3();
        this.ey = new Vec3();
        this.ez = new Vec3();
    }
    
    public Mat33(final float exx, final float exy, final float exz, final float eyx, final float eyy, final float eyz, final float ezx, final float ezy, final float ezz) {
        this.ex = new Vec3(exx, exy, exz);
        this.ey = new Vec3(eyx, eyy, eyz);
        this.ez = new Vec3(ezx, ezy, ezz);
    }
    
    public Mat33(final Vec3 argCol1, final Vec3 argCol2, final Vec3 argCol3) {
        this.ex = argCol1.clone();
        this.ey = argCol2.clone();
        this.ez = argCol3.clone();
    }
    
    public void setZero() {
        this.ex.setZero();
        this.ey.setZero();
        this.ez.setZero();
    }
    
    public void set(final float exx, final float exy, final float exz, final float eyx, final float eyy, final float eyz, final float ezx, final float ezy, final float ezz) {
        this.ex.x = exx;
        this.ex.y = exy;
        this.ex.z = exz;
        this.ey.x = eyx;
        this.ey.y = eyy;
        this.ey.z = eyz;
        this.ez.x = eyx;
        this.ez.y = eyy;
        this.ez.z = eyz;
    }
    
    public void set(final Mat33 mat) {
        final Vec3 vec = mat.ex;
        this.ex.x = vec.x;
        this.ex.y = vec.y;
        this.ex.z = vec.z;
        final Vec3 vec2 = mat.ey;
        this.ey.x = vec2.x;
        this.ey.y = vec2.y;
        this.ey.z = vec2.z;
        final Vec3 vec3 = mat.ez;
        this.ez.x = vec3.x;
        this.ez.y = vec3.y;
        this.ez.z = vec3.z;
    }
    
    public void setIdentity() {
        this.ex.x = 1.0f;
        this.ex.y = 0.0f;
        this.ex.z = 0.0f;
        this.ey.x = 0.0f;
        this.ey.y = 1.0f;
        this.ey.z = 0.0f;
        this.ez.x = 0.0f;
        this.ez.y = 0.0f;
        this.ez.z = 1.0f;
    }
    
    public static final Vec3 mul(final Mat33 A, final Vec3 v) {
        return new Vec3(v.x * A.ex.x + v.y * A.ey.x + v.z + A.ez.x, v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y, v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z);
    }
    
    public static final Vec2 mul22(final Mat33 A, final Vec2 v) {
        return new Vec2(A.ex.x * v.x + A.ey.x * v.y, A.ex.y * v.x + A.ey.y * v.y);
    }
    
    public static final void mul22ToOut(final Mat33 A, final Vec2 v, final Vec2 out) {
        final float tempx = A.ex.x * v.x + A.ey.x * v.y;
        out.y = A.ex.y * v.x + A.ey.y * v.y;
        out.x = tempx;
    }
    
    public static final void mul22ToOutUnsafe(final Mat33 A, final Vec2 v, final Vec2 out) {
        assert v != out;
        out.y = A.ex.y * v.x + A.ey.y * v.y;
        out.x = A.ex.x * v.x + A.ey.x * v.y;
    }
    
    public static final void mulToOut(final Mat33 A, final Vec3 v, final Vec3 out) {
        final float tempy = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
        final float tempz = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
        out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
        out.y = tempy;
        out.z = tempz;
    }
    
    public static final void mulToOutUnsafe(final Mat33 A, final Vec3 v, final Vec3 out) {
        assert out != v;
        out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
        out.y = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
        out.z = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
    }
    
    public final Vec2 solve22(final Vec2 b) {
        final Vec2 x = new Vec2();
        this.solve22ToOut(b, x);
        return x;
    }
    
    public final void solve22ToOut(final Vec2 b, final Vec2 out) {
        final float a11 = this.ex.x;
        final float a12 = this.ey.x;
        final float a13 = this.ex.y;
        final float a14 = this.ey.y;
        float det = a11 * a14 - a12 * a13;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        out.x = det * (a14 * b.x - a12 * b.y);
        out.y = det * (a11 * b.y - a13 * b.x);
    }
    
    public final Vec3 solve33(final Vec3 b) {
        final Vec3 x = new Vec3();
        this.solve33ToOut(b, x);
        return x;
    }
    
    public final void solve33ToOut(final Vec3 b, final Vec3 out) {
        assert b != out;
        Vec3.crossToOutUnsafe(this.ey, this.ez, out);
        float det = Vec3.dot(this.ex, out);
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        Vec3.crossToOutUnsafe(this.ey, this.ez, out);
        final float x = det * Vec3.dot(b, out);
        Vec3.crossToOutUnsafe(b, this.ez, out);
        final float y = det * Vec3.dot(this.ex, out);
        Vec3.crossToOutUnsafe(this.ey, b, out);
        final float z = det * Vec3.dot(this.ex, out);
        out.x = x;
        out.y = y;
        out.z = z;
    }
    
    public void getInverse22(final Mat33 M) {
        final float a = this.ex.x;
        final float b = this.ey.x;
        final float c = this.ex.y;
        final float d = this.ey.y;
        float det = a * d - b * c;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        M.ex.x = det * d;
        M.ey.x = -det * b;
        M.ex.z = 0.0f;
        M.ex.y = -det * c;
        M.ey.y = det * a;
        M.ey.z = 0.0f;
        M.ez.x = 0.0f;
        M.ez.y = 0.0f;
        M.ez.z = 0.0f;
    }
    
    public void getSymInverse33(final Mat33 M) {
        final float bx = this.ey.y * this.ez.z - this.ey.z * this.ez.y;
        final float by = this.ey.z * this.ez.x - this.ey.x * this.ez.z;
        final float bz = this.ey.x * this.ez.y - this.ey.y * this.ez.x;
        float det = this.ex.x * bx + this.ex.y * by + this.ex.z * bz;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        final float a11 = this.ex.x;
        final float a12 = this.ey.x;
        final float a13 = this.ez.x;
        final float a14 = this.ey.y;
        final float a15 = this.ez.y;
        final float a16 = this.ez.z;
        M.ex.x = det * (a14 * a16 - a15 * a15);
        M.ex.y = det * (a13 * a15 - a12 * a16);
        M.ex.z = det * (a12 * a15 - a13 * a14);
        M.ey.x = M.ex.y;
        M.ey.y = det * (a11 * a16 - a13 * a13);
        M.ey.z = det * (a13 * a12 - a11 * a15);
        M.ez.x = M.ex.z;
        M.ez.y = M.ey.z;
        M.ez.z = det * (a11 * a14 - a12 * a12);
    }
    
    public static final void setScaleTransform(final float scale, final Mat33 out) {
        out.ex.x = scale;
        out.ey.y = scale;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.ex == null) ? 0 : this.ex.hashCode());
        result = 31 * result + ((this.ey == null) ? 0 : this.ey.hashCode());
        result = 31 * result + ((this.ez == null) ? 0 : this.ez.hashCode());
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
        final Mat33 other = (Mat33)obj;
        if (this.ex == null) {
            if (other.ex != null) {
                return false;
            }
        }
        else if (!this.ex.equals(other.ex)) {
            return false;
        }
        if (this.ey == null) {
            if (other.ey != null) {
                return false;
            }
        }
        else if (!this.ey.equals(other.ey)) {
            return false;
        }
        if (this.ez == null) {
            if (other.ez != null) {
                return false;
            }
        }
        else if (!this.ez.equals(other.ez)) {
            return false;
        }
        return true;
    }
    
    static {
        IDENTITY = new Mat33(new Vec3(1.0f, 0.0f, 0.0f), new Vec3(0.0f, 1.0f, 0.0f), new Vec3(0.0f, 0.0f, 1.0f));
    }
}
