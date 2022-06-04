// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.shapes;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Transform;

public abstract class Shape
{
    public final ShapeType m_type;
    public float m_radius;
    
    public Shape(final ShapeType type) {
        this.m_type = type;
    }
    
    public ShapeType getType() {
        return this.m_type;
    }
    
    public float getRadius() {
        return this.m_radius;
    }
    
    public void setRadius(final float radius) {
        this.m_radius = radius;
    }
    
    public abstract int getChildCount();
    
    public abstract boolean testPoint(final Transform p0, final Vec2 p1);
    
    public abstract boolean raycast(final RayCastOutput p0, final RayCastInput p1, final Transform p2, final int p3);
    
    public abstract void computeAABB(final AABB p0, final Transform p1, final int p2);
    
    public abstract void computeMass(final MassData p0, final float p1);
    
    public abstract float computeDistanceToOut(final Transform p0, final Vec2 p1, final int p2, final Vec2 p3);
    
    public abstract Shape clone();
}
