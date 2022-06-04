// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import ea.internal.annotations.API;
import ea.internal.io.FontLoader;
import ea.internal.annotations.Internal;
import ea.Vector;
import ea.internal.ShapeBuilder;
import ea.internal.util.FontMetrics;
import org.jbox2d.collision.shapes.Shape;
import java.awt.Font;

public class Text extends Geometry
{
    private static final int SIZE = 1000;
    private float height;
    private int fontStyle;
    private String content;
    private Font font;
    private transient int cachedDescent;
    private transient float cachedScaleFactor;
    
    @Internal
    private static Shape createShape(final String content, final float height, final Font font) {
        final Vector sizeInPixels = FontMetrics.getSize(content, font);
        return ShapeBuilder.createSimpleRectangularShape(sizeInPixels.getX() * height / sizeInPixels.getY(), height);
    }
    
    @API
    public Text(final String content, final float height, final String fontName, final int style) {
        super(() -> createShape((content == null) ? "" : content, height, FontLoader.loadByName(fontName).deriveFont(style, 1000.0f)));
        this.content = ((content == null) ? "" : content);
        this.height = height;
        this.setStyle(style);
        this.setFont(fontName);
    }
    
    @API
    public Text(final String content, final float height, final String fontName) {
        this(content, height, fontName, 0);
    }
    
    @API
    public Text(final String content, final float height) {
        this(content, height, "SansSerif", 0);
    }
    
    @API
    public void setFont(final String fontName) {
        this.setFont(FontLoader.loadByName(fontName));
    }
    
    @API
    public void setFont(final Font font) {
        this.font = font.deriveFont(this.fontStyle, 1000.0f);
        this.update();
    }
    
    @API
    public Font getFont() {
        return this.font;
    }
    
    @API
    public void setContent(final String content) {
        String normalizedContent = content;
        if (normalizedContent == null) {
            normalizedContent = "";
        }
        if (!this.content.equals(normalizedContent)) {
            this.content = normalizedContent;
            this.update();
        }
    }
    
    @API
    public String getContent() {
        return this.content;
    }
    
    @API
    public void setStyle(final int style) {
        if (style >= 0 && style <= 3 && style != this.fontStyle) {
            this.fontStyle = style;
            this.font = this.font.deriveFont(style, 1000.0f);
            this.update();
        }
    }
    
    @API
    public int getStyle() {
        return this.fontStyle;
    }
    
    @API
    public void setHeight(final float height) {
        if (this.height != height) {
            this.height = height;
            this.update();
        }
    }
    
    @API
    public float getHeight() {
        return this.height;
    }
    
    @API
    public float getWidth() {
        final Vector sizeInPixels = FontMetrics.getSize(this.content, this.font);
        return sizeInPixels.getX() * this.height / sizeInPixels.getY();
    }
    
    @API
    public void setWidth(final float width) {
        final Vector sizeInPixels = FontMetrics.getSize(this.content, this.font);
        this.setHeight(width / sizeInPixels.getX() * sizeInPixels.getY());
    }
    
    @Internal
    private void update() {
        final Vector size = FontMetrics.getSize(this.content, this.font);
        this.cachedScaleFactor = this.height / size.getY();
        this.cachedDescent = FontMetrics.getDescent(this.font);
        this.setShape(() -> createShape(this.content, this.height, this.font));
    }
    
    @Internal
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        final AffineTransform pre = g.getTransform();
        final Font preFont = g.getFont();
        g.setColor(this.getColor());
        g.scale(this.cachedScaleFactor * pixelPerMeter, this.cachedScaleFactor * pixelPerMeter);
        g.setFont(this.font);
        g.drawString(this.content, 0, -this.cachedDescent);
        g.setFont(preFont);
        g.setTransform(pre);
    }
}
