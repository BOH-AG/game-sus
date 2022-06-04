// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.shapes;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

public class CircleShape extends Shape
{
    public final Vec2 m_p;
    
    public CircleShape() {
        super(ShapeType.CIRCLE);
        this.m_p = new Vec2();
        this.m_radius = 0.0f;
    }
    
    @Override
    public final Shape clone() {
        final CircleShape shape = new CircleShape();
        shape.m_p.x = this.m_p.x;
        shape.m_p.y = this.m_p.y;
        shape.m_radius = this.m_radius;
        return shape;
    }
    
    @Override
    public final int getChildCount() {
        return 1;
    }
    
    public final int getSupport(final Vec2 d) {
        return 0;
    }
    
    public final Vec2 getSupportVertex(final Vec2 d) {
        return this.m_p;
    }
    
    public final int getVertexCount() {
        return 1;
    }
    
    public final Vec2 getVertex(final int index) {
        assert index == 0;
        return this.m_p;
    }
    
    @Override
    public final boolean testPoint(final Transform transform, final Vec2 p) {
        final Rot q = transform.q;
        final Vec2 tp = transform.p;
        final float centerx = -(q.c * this.m_p.x - q.s * this.m_p.y + tp.x - p.x);
        final float centery = -(q.s * this.m_p.x + q.c * this.m_p.y + tp.y - p.y);
        return centerx * centerx + centery * centery <= this.m_radius * this.m_radius;
    }
    
    @Override
    public float computeDistanceToOut(final Transform xf, final Vec2 p, final int childIndex, final Vec2 normalOut) {
        final Rot xfq = xf.q;
        final float centerx = xfq.c * this.m_p.x - xfq.s * this.m_p.y + xf.p.x;
        final float centery = xfq.s * this.m_p.x + xfq.c * this.m_p.y + xf.p.y;
        final float dx = p.x - centerx;
        final float dy = p.y - centery;
        final float d1 = MathUtils.sqrt(dx * dx + dy * dy);
        normalOut.x = dx * 1.0f / d1;
        normalOut.y = dy * 1.0f / d1;
        return d1 - this.m_radius;
    }
    
    @Override
    public final boolean raycast(final RayCastOutput output, final RayCastInput input, final Transform transform, final int childIndex) {
        final Vec2 inputp1 = input.p1;
        final Vec2 inputp2 = input.p2;
        final Rot tq = transform.q;
        final Vec2 tp = transform.p;
        final float positionx = tq.c * this.m_p.x - tq.s * this.m_p.y + tp.x;
        final float positiony = tq.s * this.m_p.x + tq.c * this.m_p.y + tp.y;
        final float sx = inputp1.x - positionx;
        final float sy = inputp1.y - positiony;
        final float b = sx * sx + sy * sy - this.m_radius * this.m_radius;
        final float rx = inputp2.x - inputp1.x;
        final float ry = inputp2.y - inputp1.y;
        final float c = sx * rx + sy * ry;
        final float rr = rx * rx + ry * ry;
        final float sigma = c * c - rr * b;
        if (sigma < 0.0f || rr < 1.1920929E-7f) {
            return false;
        }
        float a = -(c + MathUtils.sqrt(sigma));
        if (0.0f <= a && a <= input.maxFraction * rr) {
            a /= rr;
            output.fraction = a;
            output.normal.x = rx * a + sx;
            output.normal.y = ry * a + sy;
            output.normal.normalize();
            return true;
        }
        return false;
    }
    
    @Override
    public final void computeAABB(final AABB aabb, final Transform transform, final int childIndex) {
        final Rot tq = transform.q;
        final Vec2 tp = transform.p;
        final float px = tq.c * this.m_p.x - tq.s * this.m_p.y + tp.x;
        final float py = tq.s * this.m_p.x + tq.c * this.m_p.y + tp.y;
        aabb.lowerBound.x = px - this.m_radius;
        aabb.lowerBound.y = py - this.m_radius;
        aabb.upperBound.x = px + this.m_radius;
        aabb.upperBound.y = py + this.m_radius;
    }
    
    @Override
    public final void computeMass(final MassData massData, final float density) {
        massData.mass = density * 3.1415927f * this.m_radius * this.m_radius;
        massData.center.x = this.m_p.x;
        massData.center.y = this.m_p.y;
        massData.I = massData.mass * (0.5f * this.m_radius * this.m_radius + (this.m_p.x * this.m_p.x + this.m_p.y * this.m_p.y));
    }
}
