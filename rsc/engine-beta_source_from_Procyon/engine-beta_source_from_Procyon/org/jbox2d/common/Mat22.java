// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.io.Serializable;

public class Mat22 implements Serializable
{
    private static final long serialVersionUID = 2L;
    public final Vec2 ex;
    public final Vec2 ey;
    
    @Override
    public String toString() {
        String s = "";
        s = s + "[" + this.ex.x + "," + this.ey.x + "]\n";
        s = s + "[" + this.ex.y + "," + this.ey.y + "]";
        return s;
    }
    
    public Mat22() {
        this.ex = new Vec2();
        this.ey = new Vec2();
    }
    
    public Mat22(final Vec2 c1, final Vec2 c2) {
        this.ex = c1.clone();
        this.ey = c2.clone();
    }
    
    public Mat22(final float exx, final float col2x, final float exy, final float col2y) {
        this.ex = new Vec2(exx, exy);
        this.ey = new Vec2(col2x, col2y);
    }
    
    public final Mat22 set(final Mat22 m) {
        this.ex.x = m.ex.x;
        this.ex.y = m.ex.y;
        this.ey.x = m.ey.x;
        this.ey.y = m.ey.y;
        return this;
    }
    
    public final Mat22 set(final float exx, final float col2x, final float exy, final float col2y) {
        this.ex.x = exx;
        this.ex.y = exy;
        this.ey.x = col2x;
        this.ey.y = col2y;
        return this;
    }
    
    public final Mat22 clone() {
        return new Mat22(this.ex, this.ey);
    }
    
    public final void set(final float angle) {
        final float c = MathUtils.cos(angle);
        final float s = MathUtils.sin(angle);
        this.ex.x = c;
        this.ey.x = -s;
        this.ex.y = s;
        this.ey.y = c;
    }
    
    public final void setIdentity() {
        this.ex.x = 1.0f;
        this.ey.x = 0.0f;
        this.ex.y = 0.0f;
        this.ey.y = 1.0f;
    }
    
    public final void setZero() {
        this.ex.x = 0.0f;
        this.ey.x = 0.0f;
        this.ex.y = 0.0f;
        this.ey.y = 0.0f;
    }
    
    public final float getAngle() {
        return MathUtils.atan2(this.ex.y, this.ex.x);
    }
    
    public final void set(final Vec2 c1, final Vec2 c2) {
        this.ex.x = c1.x;
        this.ey.x = c2.x;
        this.ex.y = c1.y;
        this.ey.y = c2.y;
    }
    
    public final Mat22 invert() {
        final float a = this.ex.x;
        final float b = this.ey.x;
        final float c = this.ex.y;
        final float d = this.ey.y;
        final Mat22 B = new Mat22();
        float det = a * d - b * c;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        B.ex.x = det * d;
        B.ey.x = -det * b;
        B.ex.y = -det * c;
        B.ey.y = det * a;
        return B;
    }
    
    public final Mat22 invertLocal() {
        final float a = this.ex.x;
        final float b = this.ey.x;
        final float c = this.ex.y;
        final float d = this.ey.y;
        float det = a * d - b * c;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        this.ex.x = det * d;
        this.ey.x = -det * b;
        this.ex.y = -det * c;
        this.ey.y = det * a;
        return this;
    }
    
    public final void invertToOut(final Mat22 out) {
        final float a = this.ex.x;
        final float b = this.ey.x;
        final float c = this.ex.y;
        final float d = this.ey.y;
        float det = a * d - b * c;
        det = 1.0f / det;
        out.ex.x = det * d;
        out.ey.x = -det * b;
        out.ex.y = -det * c;
        out.ey.y = det * a;
    }
    
    public final Mat22 abs() {
        return new Mat22(MathUtils.abs(this.ex.x), MathUtils.abs(this.ey.x), MathUtils.abs(this.ex.y), MathUtils.abs(this.ey.y));
    }
    
    public final void absLocal() {
        this.ex.absLocal();
        this.ey.absLocal();
    }
    
    public static final Mat22 abs(final Mat22 R) {
        return R.abs();
    }
    
    public static void absToOut(final Mat22 R, final Mat22 out) {
        out.ex.x = MathUtils.abs(R.ex.x);
        out.ex.y = MathUtils.abs(R.ex.y);
        out.ey.x = MathUtils.abs(R.ey.x);
        out.ey.y = MathUtils.abs(R.ey.y);
    }
    
    public final Vec2 mul(final Vec2 v) {
        return new Vec2(this.ex.x * v.x + this.ey.x * v.y, this.ex.y * v.x + this.ey.y * v.y);
    }
    
    public final void mulToOut(final Vec2 v, final Vec2 out) {
        final float tempy = this.ex.y * v.x + this.ey.y * v.y;
        out.x = this.ex.x * v.x + this.ey.x * v.y;
        out.y = tempy;
    }
    
    public final void mulToOutUnsafe(final Vec2 v, final Vec2 out) {
        assert v != out;
        out.x = this.ex.x * v.x + this.ey.x * v.y;
        out.y = this.ex.y * v.x + this.ey.y * v.y;
    }
    
    public final Mat22 mul(final Mat22 R) {
        final Mat22 C = new Mat22();
        C.ex.x = this.ex.x * R.ex.x + this.ey.x * R.ex.y;
        C.ex.y = this.ex.y * R.ex.x + this.ey.y * R.ex.y;
        C.ey.x = this.ex.x * R.ey.x + this.ey.x * R.ey.y;
        C.ey.y = this.ex.y * R.ey.x + this.ey.y * R.ey.y;
        return C;
    }
    
    public final Mat22 mulLocal(final Mat22 R) {
        this.mulToOut(R, this);
        return this;
    }
    
    public final void mulToOut(final Mat22 R, final Mat22 out) {
        final float tempy1 = this.ex.y * R.ex.x + this.ey.y * R.ex.y;
        final float tempx1 = this.ex.x * R.ex.x + this.ey.x * R.ex.y;
        out.ex.x = tempx1;
        out.ex.y = tempy1;
        final float tempy2 = this.ex.y * R.ey.x + this.ey.y * R.ey.y;
        final float tempx2 = this.ex.x * R.ey.x + this.ey.x * R.ey.y;
        out.ey.x = tempx2;
        out.ey.y = tempy2;
    }
    
    public final void mulToOutUnsafe(final Mat22 R, final Mat22 out) {
        assert out != R;
        assert out != this;
        out.ex.x = this.ex.x * R.ex.x + this.ey.x * R.ex.y;
        out.ex.y = this.ex.y * R.ex.x + this.ey.y * R.ex.y;
        out.ey.x = this.ex.x * R.ey.x + this.ey.x * R.ey.y;
        out.ey.y = this.ex.y * R.ey.x + this.ey.y * R.ey.y;
    }
    
    public final Mat22 mulTrans(final Mat22 B) {
        final Mat22 C = new Mat22();
        C.ex.x = Vec2.dot(this.ex, B.ex);
        C.ex.y = Vec2.dot(this.ey, B.ex);
        C.ey.x = Vec2.dot(this.ex, B.ey);
        C.ey.y = Vec2.dot(this.ey, B.ey);
        return C;
    }
    
    public final Mat22 mulTransLocal(final Mat22 B) {
        this.mulTransToOut(B, this);
        return this;
    }
    
    public final void mulTransToOut(final Mat22 B, final Mat22 out) {
        final float x1 = this.ex.x * B.ex.x + this.ex.y * B.ex.y;
        final float y1 = this.ey.x * B.ex.x + this.ey.y * B.ex.y;
        final float x2 = this.ex.x * B.ey.x + this.ex.y * B.ey.y;
        final float y2 = this.ey.x * B.ey.x + this.ey.y * B.ey.y;
        out.ex.x = x1;
        out.ey.x = x2;
        out.ex.y = y1;
        out.ey.y = y2;
    }
    
    public final void mulTransToOutUnsafe(final Mat22 B, final Mat22 out) {
        assert B != out;
        assert this != out;
        out.ex.x = this.ex.x * B.ex.x + this.ex.y * B.ex.y;
        out.ey.x = this.ex.x * B.ey.x + this.ex.y * B.ey.y;
        out.ex.y = this.ey.x * B.ex.x + this.ey.y * B.ex.y;
        out.ey.y = this.ey.x * B.ey.x + this.ey.y * B.ey.y;
    }
    
    public final Vec2 mulTrans(final Vec2 v) {
        return new Vec2(v.x * this.ex.x + v.y * this.ex.y, v.x * this.ey.x + v.y * this.ey.y);
    }
    
    public final void mulTransToOut(final Vec2 v, final Vec2 out) {
        final float tempx = v.x * this.ex.x + v.y * this.ex.y;
        out.y = v.x * this.ey.x + v.y * this.ey.y;
        out.x = tempx;
    }
    
    public final Mat22 add(final Mat22 B) {
        final Mat22 m = new Mat22();
        m.ex.x = this.ex.x + B.ex.x;
        m.ex.y = this.ex.y + B.ex.y;
        m.ey.x = this.ey.x + B.ey.x;
        m.ey.y = this.ey.y + B.ey.y;
        return m;
    }
    
    public final Mat22 addLocal(final Mat22 B) {
        final Vec2 ex = this.ex;
        ex.x += B.ex.x;
        final Vec2 ex2 = this.ex;
        ex2.y += B.ex.y;
        final Vec2 ey = this.ey;
        ey.x += B.ey.x;
        final Vec2 ey2 = this.ey;
        ey2.y += B.ey.y;
        return this;
    }
    
    public final Vec2 solve(final Vec2 b) {
        final float a11 = this.ex.x;
        final float a12 = this.ey.x;
        final float a13 = this.ex.y;
        final float a14 = this.ey.y;
        float det = a11 * a14 - a12 * a13;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        final Vec2 x = new Vec2(det * (a14 * b.x - a12 * b.y), det * (a11 * b.y - a13 * b.x));
        return x;
    }
    
    public final void solveToOut(final Vec2 b, final Vec2 out) {
        final float a11 = this.ex.x;
        final float a12 = this.ey.x;
        final float a13 = this.ex.y;
        final float a14 = this.ey.y;
        float det = a11 * a14 - a12 * a13;
        if (det != 0.0f) {
            det = 1.0f / det;
        }
        final float tempy = det * (a11 * b.y - a13 * b.x);
        out.x = det * (a14 * b.x - a12 * b.y);
        out.y = tempy;
    }
    
    public static final Vec2 mul(final Mat22 R, final Vec2 v) {
        return new Vec2(R.ex.x * v.x + R.ey.x * v.y, R.ex.y * v.x + R.ey.y * v.y);
    }
    
    public static final void mulToOut(final Mat22 R, final Vec2 v, final Vec2 out) {
        final float tempy = R.ex.y * v.x + R.ey.y * v.y;
        out.x = R.ex.x * v.x + R.ey.x * v.y;
        out.y = tempy;
    }
    
    public static final void mulToOutUnsafe(final Mat22 R, final Vec2 v, final Vec2 out) {
        assert v != out;
        out.x = R.ex.x * v.x + R.ey.x * v.y;
        out.y = R.ex.y * v.x + R.ey.y * v.y;
    }
    
    public static final Mat22 mul(final Mat22 A, final Mat22 B) {
        final Mat22 C = new Mat22();
        C.ex.x = A.ex.x * B.ex.x + A.ey.x * B.ex.y;
        C.ex.y = A.ex.y * B.ex.x + A.ey.y * B.ex.y;
        C.ey.x = A.ex.x * B.ey.x + A.ey.x * B.ey.y;
        C.ey.y = A.ex.y * B.ey.x + A.ey.y * B.ey.y;
        return C;
    }
    
    public static final void mulToOut(final Mat22 A, final Mat22 B, final Mat22 out) {
        final float tempy1 = A.ex.y * B.ex.x + A.ey.y * B.ex.y;
        final float tempx1 = A.ex.x * B.ex.x + A.ey.x * B.ex.y;
        final float tempy2 = A.ex.y * B.ey.x + A.ey.y * B.ey.y;
        final float tempx2 = A.ex.x * B.ey.x + A.ey.x * B.ey.y;
        out.ex.x = tempx1;
        out.ex.y = tempy1;
        out.ey.x = tempx2;
        out.ey.y = tempy2;
    }
    
    public static final void mulToOutUnsafe(final Mat22 A, final Mat22 B, final Mat22 out) {
        assert out != A;
        assert out != B;
        out.ex.x = A.ex.x * B.ex.x + A.ey.x * B.ex.y;
        out.ex.y = A.ex.y * B.ex.x + A.ey.y * B.ex.y;
        out.ey.x = A.ex.x * B.ey.x + A.ey.x * B.ey.y;
        out.ey.y = A.ex.y * B.ey.x + A.ey.y * B.ey.y;
    }
    
    public static final Vec2 mulTrans(final Mat22 R, final Vec2 v) {
        return new Vec2(v.x * R.ex.x + v.y * R.ex.y, v.x * R.ey.x + v.y * R.ey.y);
    }
    
    public static final void mulTransToOut(final Mat22 R, final Vec2 v, final Vec2 out) {
        final float outx = v.x * R.ex.x + v.y * R.ex.y;
        out.y = v.x * R.ey.x + v.y * R.ey.y;
        out.x = outx;
    }
    
    public static final void mulTransToOutUnsafe(final Mat22 R, final Vec2 v, final Vec2 out) {
        assert out != v;
        out.y = v.x * R.ey.x + v.y * R.ey.y;
        out.x = v.x * R.ex.x + v.y * R.ex.y;
    }
    
    public static final Mat22 mulTrans(final Mat22 A, final Mat22 B) {
        final Mat22 C = new Mat22();
        C.ex.x = A.ex.x * B.ex.x + A.ex.y * B.ex.y;
        C.ex.y = A.ey.x * B.ex.x + A.ey.y * B.ex.y;
        C.ey.x = A.ex.x * B.ey.x + A.ex.y * B.ey.y;
        C.ey.y = A.ey.x * B.ey.x + A.ey.y * B.ey.y;
        return C;
    }
    
    public static final void mulTransToOut(final Mat22 A, final Mat22 B, final Mat22 out) {
        final float x1 = A.ex.x * B.ex.x + A.ex.y * B.ex.y;
        final float y1 = A.ey.x * B.ex.x + A.ey.y * B.ex.y;
        final float x2 = A.ex.x * B.ey.x + A.ex.y * B.ey.y;
        final float y2 = A.ey.x * B.ey.x + A.ey.y * B.ey.y;
        out.ex.x = x1;
        out.ex.y = y1;
        out.ey.x = x2;
        out.ey.y = y2;
    }
    
    public static final void mulTransToOutUnsafe(final Mat22 A, final Mat22 B, final Mat22 out) {
        assert A != out;
        assert B != out;
        out.ex.x = A.ex.x * B.ex.x + A.ex.y * B.ex.y;
        out.ex.y = A.ey.x * B.ex.x + A.ey.y * B.ex.y;
        out.ey.x = A.ex.x * B.ey.x + A.ex.y * B.ey.y;
        out.ey.y = A.ey.x * B.ey.x + A.ey.y * B.ey.y;
    }
    
    public static final Mat22 createRotationalTransform(final float angle) {
        final Mat22 mat = new Mat22();
        final float c = MathUtils.cos(angle);
        final float s = MathUtils.sin(angle);
        mat.ex.x = c;
        mat.ey.x = -s;
        mat.ex.y = s;
        mat.ey.y = c;
        return mat;
    }
    
    public static final void createRotationalTransform(final float angle, final Mat22 out) {
        final float c = MathUtils.cos(angle);
        final float s = MathUtils.sin(angle);
        out.ex.x = c;
        out.ey.x = -s;
        out.ex.y = s;
        out.ey.y = c;
    }
    
    public static final Mat22 createScaleTransform(final float scale) {
        final Mat22 mat = new Mat22();
        mat.ex.x = scale;
        mat.ey.y = scale;
        return mat;
    }
    
    public static final void createScaleTransform(final float scale, final Mat22 out) {
        out.ex.x = scale;
        out.ey.y = scale;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.ex == null) ? 0 : this.ex.hashCode());
        result = 31 * result + ((this.ey == null) ? 0 : this.ey.hashCode());
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
        final Mat22 other = (Mat22)obj;
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
        return true;
    }
}
