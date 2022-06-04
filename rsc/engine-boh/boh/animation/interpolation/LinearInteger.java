// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation.interpolation;

import ea.animation.Interpolator;

public class LinearInteger implements Interpolator<Integer>
{
    private int start;
    private int end;
    
    public LinearInteger(final int start, final int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public Integer interpolate(final float progress) {
        return this.start + (int)((this.end - this.start) * progress);
    }
}
