// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.MathUtils;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.pooling.normal.DefaultWorldPool;
import org.jbox2d.common.Vec2;

public class AABB
{
    public final Vec2 lowerBound;
    public final Vec2 upperBound;
    
    public AABB() {
        this.lowerBound = new Vec2();
        this.upperBound = new Vec2();
    }
    
    public AABB(final AABB copy) {
        this(copy.lowerBound, copy.upperBound);
    }
    
    public AABB(final Vec2 lowerVertex, final Vec2 upperVertex) {
        this.lowerBound = lowerVertex.clone();
        this.upperBound = upperVertex.clone();
    }
    
    public final void set(final AABB aabb) {
        final Vec2 v = aabb.lowerBound;
        this.lowerBound.x = v.x;
        this.lowerBound.y = v.y;
        final Vec2 v2 = aabb.upperBound;
        this.upperBound.x = v2.x;
        this.upperBound.y = v2.y;
    }
    
    public final boolean isValid() {
        final float dx = this.upperBound.x - this.lowerBound.x;
        if (dx < 0.0f) {
            return false;
        }
        final float dy = this.upperBound.y - this.lowerBound.y;
        return dy >= 0.0f && this.lowerBound.isValid() && this.upperBound.isValid();
    }
    
    public final Vec2 getCenter() {
        final Vec2 center = new Vec2(this.lowerBound);
        center.addLocal(this.upperBound);
        center.mulLocal(0.5f);
        return center;
    }
    
    public final void getCenterToOut(final Vec2 out) {
        out.x = (this.lowerBound.x + this.upperBound.x) * 0.5f;
        out.y = (this.lowerBound.y + this.upperBound.y) * 0.5f;
    }
    
    public final Vec2 getExtents() {
        final Vec2 center = new Vec2(this.upperBound);
        center.subLocal(this.lowerBound);
        center.mulLocal(0.5f);
        return center;
    }
    
    public final void getExtentsToOut(final Vec2 out) {
        out.x = (this.upperBound.x - this.lowerBound.x) * 0.5f;
        out.y = (this.upperBound.y - this.lowerBound.y) * 0.5f;
    }
    
    public final void getVertices(final Vec2[] argRay) {
        argRay[0].set(this.lowerBound);
        argRay[1].set(this.lowerBound);
        final Vec2 vec2 = argRay[1];
        vec2.x += this.upperBound.x - this.lowerBound.x;
        argRay[2].set(this.upperBound);
        argRay[3].set(this.upperBound);
        final Vec2 vec3 = argRay[3];
        vec3.x -= this.upperBound.x - this.lowerBound.x;
    }
    
    public final void combine(final AABB aabb1, final AABB aab) {
        this.lowerBound.x = ((aabb1.lowerBound.x < aab.lowerBound.x) ? aabb1.lowerBound.x : aab.lowerBound.x);
        this.lowerBound.y = ((aabb1.lowerBound.y < aab.lowerBound.y) ? aabb1.lowerBound.y : aab.lowerBound.y);
        this.upperBound.x = ((aabb1.upperBound.x > aab.upperBound.x) ? aabb1.upperBound.x : aab.upperBound.x);
        this.upperBound.y = ((aabb1.upperBound.y > aab.upperBound.y) ? aabb1.upperBound.y : aab.upperBound.y);
    }
    
    public final float getPerimeter() {
        return 2.0f * (this.upperBound.x - this.lowerBound.x + this.upperBound.y - this.lowerBound.y);
    }
    
    public final void combine(final AABB aabb) {
        this.lowerBound.x = ((this.lowerBound.x < aabb.lowerBound.x) ? this.lowerBound.x : aabb.lowerBound.x);
        this.lowerBound.y = ((this.lowerBound.y < aabb.lowerBound.y) ? this.lowerBound.y : aabb.lowerBound.y);
        this.upperBound.x = ((this.upperBound.x > aabb.upperBound.x) ? this.upperBound.x : aabb.upperBound.x);
        this.upperBound.y = ((this.upperBound.y > aabb.upperBound.y) ? this.upperBound.y : aabb.upperBound.y);
    }
    
    public final boolean contains(final AABB aabb) {
        return this.lowerBound.x <= aabb.lowerBound.x && this.lowerBound.y <= aabb.lowerBound.y && aabb.upperBound.x <= this.upperBound.x && aabb.upperBound.y <= this.upperBound.y;
    }
    
    @Deprecated
    public final boolean raycast(final RayCastOutput output, final RayCastInput input) {
        return this.raycast(output, input, new DefaultWorldPool(4, 4));
    }
    
    public final boolean raycast(final RayCastOutput output, final RayCastInput input, final IWorldPool argPool) {
        float tmin = -3.4028235E38f;
        float tmax = Float.MAX_VALUE;
        final Vec2 p = argPool.popVec2();
        final Vec2 d = argPool.popVec2();
        final Vec2 absD = argPool.popVec2();
        final Vec2 normal = argPool.popVec2();
        p.set(input.p1);
        d.set(input.p2).subLocal(input.p1);
        Vec2.absToOut(d, absD);
        if (absD.x < 1.1920929E-7f) {
            if (p.x < this.lowerBound.x || this.upperBound.x < p.x) {
                argPool.pushVec2(4);
                return false;
            }
        }
        else {
            final float inv_d = 1.0f / d.x;
            float t1 = (this.lowerBound.x - p.x) * inv_d;
            float t2 = (this.upperBound.x - p.x) * inv_d;
            float s = -1.0f;
            if (t1 > t2) {
                final float temp = t1;
                t1 = t2;
                t2 = temp;
                s = 1.0f;
            }
            if (t1 > tmin) {
                normal.setZero();
                normal.x = s;
                tmin = t1;
            }
            tmax = MathUtils.min(tmax, t2);
            if (tmin > tmax) {
                argPool.pushVec2(4);
                return false;
            }
        }
        if (absD.y < 1.1920929E-7f) {
            if (p.y < this.lowerBound.y || this.upperBound.y < p.y) {
                argPool.pushVec2(4);
                return false;
            }
        }
        else {
            final float inv_d = 1.0f / d.y;
            float t1 = (this.lowerBound.y - p.y) * inv_d;
            float t2 = (this.upperBound.y - p.y) * inv_d;
            float s = -1.0f;
            if (t1 > t2) {
                final float temp = t1;
                t1 = t2;
                t2 = temp;
                s = 1.0f;
            }
            if (t1 > tmin) {
                normal.setZero();
                normal.y = s;
                tmin = t1;
            }
            tmax = MathUtils.min(tmax, t2);
            if (tmin > tmax) {
                argPool.pushVec2(4);
                return false;
            }
        }
        if (tmin < 0.0f || input.maxFraction < tmin) {
            argPool.pushVec2(4);
            return false;
        }
        output.fraction = tmin;
        output.normal.x = normal.x;
        output.normal.y = normal.y;
        argPool.pushVec2(4);
        return true;
    }
    
    public static final boolean testOverlap(final AABB a, final AABB b) {
        return b.lowerBound.x - a.upperBound.x <= 0.0f && b.lowerBound.y - a.upperBound.y <= 0.0f && a.lowerBound.x - b.upperBound.x <= 0.0f && a.lowerBound.y - b.upperBound.y <= 0.0f;
    }
    
    @Override
    public final String toString() {
        final String s = "AABB[" + this.lowerBound + " . " + this.upperBound + "]";
        return s;
    }
}
