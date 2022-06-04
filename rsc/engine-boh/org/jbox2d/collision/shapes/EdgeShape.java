// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.shapes;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Rot;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;

public class EdgeShape extends Shape
{
    public final Vec2 m_vertex1;
    public final Vec2 m_vertex2;
    public final Vec2 m_vertex0;
    public final Vec2 m_vertex3;
    public boolean m_hasVertex0;
    public boolean m_hasVertex3;
    private final Vec2 normal;
    
    public EdgeShape() {
        super(ShapeType.EDGE);
        this.m_vertex1 = new Vec2();
        this.m_vertex2 = new Vec2();
        this.m_vertex0 = new Vec2();
        this.m_vertex3 = new Vec2();
        this.m_hasVertex0 = false;
        this.m_hasVertex3 = false;
        this.normal = new Vec2();
        this.m_radius = Settings.polygonRadius;
    }
    
    @Override
    public int getChildCount() {
        return 1;
    }
    
    public void set(final Vec2 v1, final Vec2 v2) {
        this.m_vertex1.set(v1);
        this.m_vertex2.set(v2);
        final boolean b = false;
        this.m_hasVertex3 = b;
        this.m_hasVertex0 = b;
    }
    
    @Override
    public boolean testPoint(final Transform xf, final Vec2 p) {
        return false;
    }
    
    @Override
    public float computeDistanceToOut(final Transform xf, final Vec2 p, final int childIndex, final Vec2 normalOut) {
        final float xfqc = xf.q.c;
        final float xfqs = xf.q.s;
        final float xfpx = xf.p.x;
        final float xfpy = xf.p.y;
        final float v1x = xfqc * this.m_vertex1.x - xfqs * this.m_vertex1.y + xfpx;
        final float v1y = xfqs * this.m_vertex1.x + xfqc * this.m_vertex1.y + xfpy;
        final float v2x = xfqc * this.m_vertex2.x - xfqs * this.m_vertex2.y + xfpx;
        final float v2y = xfqs * this.m_vertex2.x + xfqc * this.m_vertex2.y + xfpy;
        float dx = p.x - v1x;
        float dy = p.y - v1y;
        final float sx = v2x - v1x;
        final float sy = v2y - v1y;
        final float ds = dx * sx + dy * sy;
        if (ds > 0.0f) {
            final float s2 = sx * sx + sy * sy;
            if (ds > s2) {
                dx = p.x - v2x;
                dy = p.y - v2y;
            }
            else {
                dx -= ds / s2 * sx;
                dy -= ds / s2 * sy;
            }
        }
        final float d1 = MathUtils.sqrt(dx * dx + dy * dy);
        if (d1 > 0.0f) {
            normalOut.x = 1.0f / d1 * dx;
            normalOut.y = 1.0f / d1 * dy;
        }
        else {
            normalOut.x = 0.0f;
            normalOut.y = 0.0f;
        }
        return d1;
    }
    
    @Override
    public boolean raycast(final RayCastOutput output, final RayCastInput input, final Transform xf, final int childIndex) {
        final Vec2 v1 = this.m_vertex1;
        final Vec2 v2 = this.m_vertex2;
        final Rot xfq = xf.q;
        final Vec2 xfp = xf.p;
        float tempx = input.p1.x - xfp.x;
        float tempy = input.p1.y - xfp.y;
        final float p1x = xfq.c * tempx + xfq.s * tempy;
        final float p1y = -xfq.s * tempx + xfq.c * tempy;
        tempx = input.p2.x - xfp.x;
        tempy = input.p2.y - xfp.y;
        final float p2x = xfq.c * tempx + xfq.s * tempy;
        final float p2y = -xfq.s * tempx + xfq.c * tempy;
        final float dx = p2x - p1x;
        final float dy = p2y - p1y;
        this.normal.x = v2.y - v1.y;
        this.normal.y = v1.x - v2.x;
        this.normal.normalize();
        final float normalx = this.normal.x;
        final float normaly = this.normal.y;
        tempx = v1.x - p1x;
        tempy = v1.y - p1y;
        final float numerator = normalx * tempx + normaly * tempy;
        final float denominator = normalx * dx + normaly * dy;
        if (denominator == 0.0f) {
            return false;
        }
        final float t = numerator / denominator;
        if (t < 0.0f || 1.0f < t) {
            return false;
        }
        final float qx = p1x + t * dx;
        final float qy = p1y + t * dy;
        final float rx = v2.x - v1.x;
        final float ry = v2.y - v1.y;
        final float rr = rx * rx + ry * ry;
        if (rr == 0.0f) {
            return false;
        }
        tempx = qx - v1.x;
        tempy = qy - v1.y;
        final float s = (tempx * rx + tempy * ry) / rr;
        if (s < 0.0f || 1.0f < s) {
            return false;
        }
        output.fraction = t;
        if (numerator > 0.0f) {
            output.normal.x = -xfq.c * this.normal.x + xfq.s * this.normal.y;
            output.normal.y = -xfq.s * this.normal.x - xfq.c * this.normal.y;
        }
        else {
            output.normal.x = xfq.c * this.normal.x - xfq.s * this.normal.y;
            output.normal.y = xfq.s * this.normal.x + xfq.c * this.normal.y;
        }
        return true;
    }
    
    @Override
    public void computeAABB(final AABB aabb, final Transform xf, final int childIndex) {
        final Vec2 lowerBound = aabb.lowerBound;
        final Vec2 upperBound = aabb.upperBound;
        final Rot xfq = xf.q;
        final float v1x = xfq.c * this.m_vertex1.x - xfq.s * this.m_vertex1.y + xf.p.x;
        final float v1y = xfq.s * this.m_vertex1.x + xfq.c * this.m_vertex1.y + xf.p.y;
        final float v2x = xfq.c * this.m_vertex2.x - xfq.s * this.m_vertex2.y + xf.p.x;
        final float v2y = xfq.s * this.m_vertex2.x + xfq.c * this.m_vertex2.y + xf.p.y;
        lowerBound.x = ((v1x < v2x) ? v1x : v2x);
        lowerBound.y = ((v1y < v2y) ? v1y : v2y);
        upperBound.x = ((v1x > v2x) ? v1x : v2x);
        upperBound.y = ((v1y > v2y) ? v1y : v2y);
        final Vec2 vec2 = lowerBound;
        vec2.x -= this.m_radius;
        final Vec2 vec3 = lowerBound;
        vec3.y -= this.m_radius;
        final Vec2 vec4 = upperBound;
        vec4.x += this.m_radius;
        final Vec2 vec5 = upperBound;
        vec5.y += this.m_radius;
    }
    
    @Override
    public void computeMass(final MassData massData, final float density) {
        massData.mass = 0.0f;
        massData.center.set(this.m_vertex1).addLocal(this.m_vertex2).mulLocal(0.5f);
        massData.I = 0.0f;
    }
    
    @Override
    public Shape clone() {
        final EdgeShape edge = new EdgeShape();
        edge.m_radius = this.m_radius;
        edge.m_hasVertex0 = this.m_hasVertex0;
        edge.m_hasVertex3 = this.m_hasVertex3;
        edge.m_vertex0.set(this.m_vertex0);
        edge.m_vertex1.set(this.m_vertex1);
        edge.m_vertex2.set(this.m_vertex2);
        edge.m_vertex3.set(this.m_vertex3);
        return edge;
    }
}
