// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal;

import ea.internal.annotations.Internal;
import ea.internal.annotations.API;
import ea.FrameUpdateListener;

public final class PeriodicTask implements FrameUpdateListener
{
    private float interval;
    private float countdown;
    private Runnable runnable;
    
    public PeriodicTask(final float intervalInSeconds, final Runnable runnable) {
        this.setInterval(intervalInSeconds);
        this.countdown = intervalInSeconds;
        this.runnable = runnable;
    }
    
    @API
    public void setInterval(final float interval) {
        if (interval <= 0.0f) {
            throw new RuntimeException("Das Interval eines periodischen Tasks muss gr\u00f6\u00dfer als 0 sein, war " + interval);
        }
        this.interval = interval;
    }
    
    @API
    public float getInterval() {
        return this.interval;
    }
    
    @Internal
    @Override
    public void onFrameUpdate(final float deltaSeconds) {
        this.countdown -= deltaSeconds;
        while (this.countdown < 0.0f) {
            this.countdown += this.interval;
            this.runnable.run();
        }
    }
}
