// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

import java.io.Serializable;

public class Transform implements Serializable
{
    private static final long serialVersionUID = 1L;
    public final Vec2 p;
    public final Rot q;
    private static Vec2 pool;
    
    public Transform() {
        this.p = new Vec2();
        this.q = new Rot();
    }
    
    public Transform(final Transform xf) {
        this.p = xf.p.clone();
        this.q = xf.q.clone();
    }
    
    public Transform(final Vec2 _position, final Rot _R) {
        this.p = _position.clone();
        this.q = _R.clone();
    }
    
    public final Transform set(final Transform xf) {
        this.p.set(xf.p);
        this.q.set(xf.q);
        return this;
    }
    
    public final void set(final Vec2 p, final float angle) {
        this.p.set(p);
        this.q.set(angle);
    }
    
    public final void setIdentity() {
        this.p.setZero();
        this.q.setIdentity();
    }
    
    public static final Vec2 mul(final Transform T, final Vec2 v) {
        return new Vec2(T.q.c * v.x - T.q.s * v.y + T.p.x, T.q.s * v.x + T.q.c * v.y + T.p.y);
    }
    
    public static final void mulToOut(final Transform T, final Vec2 v, final Vec2 out) {
        final float tempy = T.q.s * v.x + T.q.c * v.y + T.p.y;
        out.x = T.q.c * v.x - T.q.s * v.y + T.p.x;
        out.y = tempy;
    }
    
    public static final void mulToOutUnsafe(final Transform T, final Vec2 v, final Vec2 out) {
        assert v != out;
        out.x = T.q.c * v.x - T.q.s * v.y + T.p.x;
        out.y = T.q.s * v.x + T.q.c * v.y + T.p.y;
    }
    
    public static final Vec2 mulTrans(final Transform T, final Vec2 v) {
        final float px = v.x - T.p.x;
        final float py = v.y - T.p.y;
        return new Vec2(T.q.c * px + T.q.s * py, -T.q.s * px + T.q.c * py);
    }
    
    public static final void mulTransToOut(final Transform T, final Vec2 v, final Vec2 out) {
        final float px = v.x - T.p.x;
        final float py = v.y - T.p.y;
        final float tempy = -T.q.s * px + T.q.c * py;
        out.x = T.q.c * px + T.q.s * py;
        out.y = tempy;
    }
    
    public static final void mulTransToOutUnsafe(final Transform T, final Vec2 v, final Vec2 out) {
        assert v != out;
        final float px = v.x - T.p.x;
        final float py = v.y - T.p.y;
        out.x = T.q.c * px + T.q.s * py;
        out.y = -T.q.s * px + T.q.c * py;
    }
    
    public static final Transform mul(final Transform A, final Transform B) {
        final Transform C = new Transform();
        Rot.mulUnsafe(A.q, B.q, C.q);
        Rot.mulToOutUnsafe(A.q, B.p, C.p);
        C.p.addLocal(A.p);
        return C;
    }
    
    public static final void mulToOut(final Transform A, final Transform B, final Transform out) {
        assert out != A;
        Rot.mul(A.q, B.q, out.q);
        Rot.mulToOut(A.q, B.p, out.p);
        out.p.addLocal(A.p);
    }
    
    public static final void mulToOutUnsafe(final Transform A, final Transform B, final Transform out) {
        assert out != B;
        assert out != A;
        Rot.mulUnsafe(A.q, B.q, out.q);
        Rot.mulToOutUnsafe(A.q, B.p, out.p);
        out.p.addLocal(A.p);
    }
    
    public static final Transform mulTrans(final Transform A, final Transform B) {
        final Transform C = new Transform();
        Rot.mulTransUnsafe(A.q, B.q, C.q);
        Transform.pool.set(B.p).subLocal(A.p);
        Rot.mulTransUnsafe(A.q, Transform.pool, C.p);
        return C;
    }
    
    public static final void mulTransToOut(final Transform A, final Transform B, final Transform out) {
        assert out != A;
        Rot.mulTrans(A.q, B.q, out.q);
        Transform.pool.set(B.p).subLocal(A.p);
        Rot.mulTrans(A.q, Transform.pool, out.p);
    }
    
    public static final void mulTransToOutUnsafe(final Transform A, final Transform B, final Transform out) {
        assert out != A;
        assert out != B;
        Rot.mulTransUnsafe(A.q, B.q, out.q);
        Transform.pool.set(B.p).subLocal(A.p);
        Rot.mulTransUnsafe(A.q, Transform.pool, out.p);
    }
    
    @Override
    public final String toString() {
        String s = "XForm:\n";
        s = s + "Position: " + this.p + "\n";
        s = s + "R: \n" + this.q + "\n";
        return s;
    }
    
    static {
        Transform.pool = new Vec2();
    }
}
