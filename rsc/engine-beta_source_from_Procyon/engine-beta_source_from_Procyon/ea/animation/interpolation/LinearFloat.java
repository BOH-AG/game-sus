// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation.interpolation;

import ea.animation.Interpolator;

public class LinearFloat implements Interpolator<Float>
{
    private float start;
    private float end;
    
    public LinearFloat(final float start, final float end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public Float interpolate(final float progress) {
        return this.start + (this.end - this.start) * progress;
    }
}