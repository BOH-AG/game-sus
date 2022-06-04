// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import org.jbox2d.collision.shapes.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.Graphics2D;
import ea.internal.annotations.Internal;
import java.awt.Dimension;
import ea.internal.annotations.API;
import ea.internal.io.ImageLoader;
import ea.internal.ShapeBuilder;
import java.awt.image.BufferedImage;

public class Image extends Actor
{
    private final BufferedImage image;
    private float width;
    private float height;
    
    @API
    public Image(final String filepath, final float width, final float height) {
        super(() -> ShapeBuilder.createSimpleRectangularShape(width, height));
        this.assertViableSizes(width, height);
        this.image = ImageLoader.load(filepath);
        this.width = width;
        this.height = height;
    }
    
    @API
    public Image(final String filepath, final float pixelPerMeter) {
        super(() -> ShapeBuilder.createSimpleRectangularShape(ImageLoader.load(filepath).getWidth() / pixelPerMeter, ImageLoader.load(filepath).getHeight() / pixelPerMeter));
        this.assertViablePPM(pixelPerMeter);
        this.image = ImageLoader.load(filepath);
        this.width = this.image.getWidth() / pixelPerMeter;
        this.height = this.image.getHeight() / pixelPerMeter;
    }
    
    @Internal
    public Dimension getImageSizeInPx() {
        return new Dimension(this.image.getWidth(), this.image.getHeight());
    }
    
    @API
    public BufferedImage getImage() {
        return this.image;
    }
    
    public void resetImageSize(final float width, final float height) {
        this.assertViableSizes(width, height);
        this.width = width;
        this.height = height;
        this.setShape(() -> ShapeBuilder.createSimpleRectangularShape(width, height));
    }
    
    public void resetPixelPerMeter(final float pixelPerMeter) {
        this.assertViablePPM(pixelPerMeter);
        this.resetImageSize(this.image.getWidth() / pixelPerMeter, this.image.getHeight() / pixelPerMeter);
    }
    
    private void assertViableSizes(final float width, final float height) {
        if (width <= 0.0f || height <= 0.0f) {
            throw new IllegalArgumentException("Bildh\u00f6he und Breite m\u00fcssen gr\u00f6\u00dfer als 0 sein.");
        }
    }
    
    private void assertViablePPM(final float pixelPerMeter) {
        if (pixelPerMeter <= 0.0f) {
            throw new IllegalArgumentException("Die Umrechnungszahl f\u00fcr Pixel pro Meter darf nicht negativ sein. War " + pixelPerMeter);
        }
    }
    
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        final AffineTransform pre = g.getTransform();
        g.scale(this.width * pixelPerMeter / this.image.getWidth(), this.height * pixelPerMeter / this.image.getHeight());
        g.drawImage(this.image, 0, -this.image.getHeight(), null);
        g.setTransform(pre);
    }
}
