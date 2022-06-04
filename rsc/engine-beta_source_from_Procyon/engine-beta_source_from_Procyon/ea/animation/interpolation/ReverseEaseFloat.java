// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation.interpolation;

import ea.animation.Interpolator;

public class ReverseEaseFloat implements Interpolator<Float>
{
    private float startAndEnd;
    private float middle;
    
    public ReverseEaseFloat(final float startAndEnd, final float middle) {
        this.startAndEnd = startAndEnd;
        this.middle = middle;
    }
    
    @Override
    public Float interpolate(final float progress) {
        return this.startAndEnd + (float)Math.sin(progress * 3.141592653589793) * (this.middle - this.startAndEnd);
    }
}
