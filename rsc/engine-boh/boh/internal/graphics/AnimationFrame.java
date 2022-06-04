// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.graphics;

import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import ea.internal.annotations.Internal;

@Internal
public final class AnimationFrame
{
    private final BufferedImage image;
    private float duration;
    
    @Internal
    public AnimationFrame(final BufferedImage image, final float duration) {
        this.image = image;
        this.duration = duration;
    }
    
    @Internal
    public void setDuration(final float duration) {
        this.duration = duration;
    }
    
    @Internal
    public BufferedImage getImage() {
        return this.image;
    }
    
    @Internal
    public float getDuration() {
        return this.duration;
    }
    
    @Internal
    public void render(final Graphics2D g, final float width, final float height, final boolean flipHorizontal, final boolean flipVertical) {
        final AffineTransform pre = g.getTransform();
        g.scale(width / this.image.getWidth(), height / this.image.getHeight());
        g.drawImage(this.image, flipHorizontal ? this.image.getWidth() : 0, -this.image.getHeight() + (flipVertical ? this.image.getHeight() : 0), (flipHorizontal ? -1 : 1) * this.image.getWidth(), (flipVertical ? -1 : 1) * this.image.getHeight(), null);
        g.setTransform(pre);
    }
}
