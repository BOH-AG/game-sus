// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation.interpolation;

import ea.internal.annotations.Internal;
import ea.internal.annotations.API;
import ea.animation.Interpolator;

public class EaseInOutFloat implements Interpolator<Float>
{
    private final float start;
    private final float end;
    
    @API
    public EaseInOutFloat(final float start, final float end) {
        this.start = start;
        this.end = end;
    }
    
    @Internal
    @Override
    public Float interpolate(final float progress) {
        return (float)((Math.sin(progress * 3.141592653589793 - 1.5707963267948966) + 1.0) / 2.0) * (this.end - this.start) + this.start;
    }
}
