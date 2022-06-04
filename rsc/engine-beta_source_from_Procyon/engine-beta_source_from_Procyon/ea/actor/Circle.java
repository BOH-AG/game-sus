// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import ea.internal.annotations.Internal;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import java.util.function.Supplier;
import java.awt.Graphics2D;
import ea.internal.annotations.API;
import java.awt.Color;

public class Circle extends Geometry
{
    private float diameter;
    
    public Circle(final float diameter) {
        super(createCircleSupplier(diameter));
        this.diameter = diameter;
        this.setColor(Color.WHITE);
    }
    
    @API
    public float getDiameter() {
        return this.diameter;
    }
    
    @API
    public float getRadius() {
        return this.diameter / 2.0f;
    }
    
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        g.setColor(this.getColor());
        g.fillOval(0, -(int)(this.diameter * pixelPerMeter), (int)(this.diameter * pixelPerMeter), (int)(this.diameter * pixelPerMeter));
    }
    
    @API
    public void resetRadius(final float radius) {
        this.diameter = 2.0f * radius;
        this.setShape(createCircleSupplier(this.diameter));
    }
    
    @Internal
    private static Supplier<Shape> createCircleSupplier(final float diameter) {
        final CircleShape shape;
        return (Supplier<Shape>)(() -> {
            shape = new CircleShape();
            shape.m_radius = diameter / 2.0f;
            shape.m_p.set(shape.m_radius, shape.m_radius);
            return shape;
        });
    }
}
