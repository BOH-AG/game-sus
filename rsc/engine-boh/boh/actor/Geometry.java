// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import ea.FrameUpdateListener;
import ea.event.FrameUpdateListenerContainer;
import ea.animation.Interpolator;
import ea.animation.AnimationMode;
import ea.animation.interpolation.LinearFloat;
import ea.animation.ValueAnimator;
import org.jbox2d.collision.shapes.Shape;
import java.util.function.Supplier;
import java.awt.Color;
import ea.internal.annotations.API;

@API
public abstract class Geometry extends Actor
{
    private Color color;
    
    @API
    public Geometry(final Supplier<Shape> shapeSupplier) {
        super(shapeSupplier);
        this.color = Color.WHITE;
    }
    
    @API
    public void setColor(final Color color) {
        this.color = color;
    }
    
    @API
    public Color getColor() {
        return this.color;
    }
    
    @API
    public ValueAnimator<Float> animateColor(final float duration, final Color color) {
        final Color originalColor = this.getColor();
        final ValueAnimator<Float> animator = new ValueAnimator<Float>(duration, progress -> this.setColor(this.calculateIntermediateColor(originalColor, color, progress)), new LinearFloat(0.0f, 1.0f), AnimationMode.SINGLE, this);
        this.addFrameUpdateListener(animator);
        return animator;
    }
    
    private Color calculateIntermediateColor(final Color original, final Color target, final float progress) {
        final int r = original.getRed() - (int)((original.getRed() - target.getRed()) * progress);
        final int g = original.getGreen() - (int)((original.getGreen() - target.getGreen()) * progress);
        final int b = original.getBlue() - (int)((original.getBlue() - target.getBlue()) * progress);
        return new Color(r, g, b);
    }
}
