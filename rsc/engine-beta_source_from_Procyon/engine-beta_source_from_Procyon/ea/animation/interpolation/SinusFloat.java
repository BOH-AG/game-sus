// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation.interpolation;

import ea.animation.Interpolator;

public class SinusFloat implements Interpolator<Float>
{
    private final float start;
    private final float amplitude;
    
    public SinusFloat(final float start, final float amplitude) {
        this.start = start;
        this.amplitude = amplitude;
    }
    
    @Override
    public Float interpolate(final float progress) {
        return (float)Math.sin(3.141592653589793 * progress * 2.0) * this.amplitude + this.start;
    }
}
