// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import java.awt.Graphics2D;
import ea.internal.annotations.API;
import org.jbox2d.collision.shapes.Shape;
import java.util.function.Supplier;
import ea.internal.ShapeBuilder;

public class Rectangle extends Geometry
{
    private float width;
    private float height;
    private float borderRadius;
    
    public Rectangle(final float width, final float height) {
        this(width, height, () -> ShapeBuilder.createSimpleRectangularShape(width, height));
    }
    
    public Rectangle(final float width, final float height, final Supplier<Shape> shapeSupplier) {
        super(shapeSupplier);
        Actor.assertWidthAndHeight(width, height);
        this.width = width;
        this.height = height;
    }
    
    @API
    public float getWidth() {
        return this.width;
    }
    
    @API
    public float getHeight() {
        return this.height;
    }
    
    @API
    public void setSize(final float width, final float height) {
        Actor.assertWidthAndHeight(width, height);
        this.width = width;
        this.height = height;
        this.setShape(() -> ShapeBuilder.createSimpleRectangularShape(width, height));
    }
    
    @API
    public float getBorderRadius() {
        return this.borderRadius;
    }
    
    @API
    public void setBorderRadius(final float percent) {
        if (percent < 0.0f || percent > 1.0f) {
            throw new IllegalArgumentException("Borderradius kann nur zwischen 0 und 1 sein. War " + percent);
        }
        this.borderRadius = percent;
    }
    
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        g.setColor(this.getColor());
        if (this.borderRadius == 0.0f) {
            g.fillRect(0, (int)(-this.height * pixelPerMeter), (int)(this.width * pixelPerMeter), (int)(this.height * pixelPerMeter));
        }
        else {
            final int borderRadius = (int)(Math.min(this.width, this.height) * pixelPerMeter * this.borderRadius);
            g.fillRoundRect(0, (int)(-this.height * pixelPerMeter), (int)(this.width * pixelPerMeter), (int)(this.height * pixelPerMeter), borderRadius, borderRadius);
        }
    }
}
