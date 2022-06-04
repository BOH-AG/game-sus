// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import org.jbox2d.collision.shapes.Shape;
import ea.internal.annotations.Internal;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import ea.internal.ShapeBuilder;
import ea.Vector;
import ea.internal.annotations.API;

@API
public class Polygon extends Geometry
{
    private float[] px;
    private float[] py;
    private int[] scaledPx;
    private int[] scaledPy;
    private float scaleX;
    private float scaleY;
    
    @API
    public Polygon(final Vector... points) {
        super(() -> ShapeBuilder.createPolygonShape(points));
        this.resetPoints(points);
    }
    
    @API
    public void resetPoints(final Vector... points) {
        if (points.length < 3) {
            throw new RuntimeException("Der Streckenzug muss mindestens aus 3 Punkten bestehen, um ein g\u00fcltiges Polygon zu beschreiben.");
        }
        this.px = new float[points.length];
        this.py = new float[points.length];
        this.scaledPx = new int[points.length];
        this.scaledPy = new int[points.length];
        for (int i = 0; i < points.length; ++i) {
            this.px[i] = points[i].getX();
            this.py[i] = points[i].getY();
        }
        this.setShape(() -> ShapeBuilder.createPolygonShape(points));
    }
    
    @Internal
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        for (int i = 0; i < this.scaledPx.length; ++i) {
            this.scaledPx[i] = (int)(this.px[i] * pixelPerMeter);
            this.scaledPy[i] = (int)(this.py[i] * pixelPerMeter);
        }
        final AffineTransform at = g.getTransform();
        g.scale(1.0, -1.0);
        g.setColor(this.getColor());
        g.fillPolygon(this.scaledPx, this.scaledPy, this.scaledPx.length);
        g.setTransform(at);
    }
}
