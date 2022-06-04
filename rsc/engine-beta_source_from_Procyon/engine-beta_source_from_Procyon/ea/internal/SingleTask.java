// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal;

import ea.internal.annotations.API;
import ea.event.FrameUpdateListenerContainer;
import ea.internal.annotations.Internal;
import ea.FrameUpdateListener;

@Internal
public final class SingleTask implements FrameUpdateListener
{
    private float delay;
    private float countdown;
    private Runnable runnable;
    private boolean done;
    private FrameUpdateListenerContainer parent;
    
    public SingleTask(final float delayInSeconds, final Runnable runnable, final FrameUpdateListenerContainer parent) {
        this.delay = delayInSeconds;
        this.countdown = delayInSeconds;
        this.runnable = runnable;
        this.parent = parent;
    }
    
    @API
    public float getDelay() {
        return this.delay;
    }
    
    @API
    public boolean isDone() {
        return this.done;
    }
    
    @Override
    public void onFrameUpdate(final float deltaSeconds) {
        this.countdown -= deltaSeconds;
        if (!this.done && this.countdown < 0.0f) {
            this.runnable.run();
            this.parent.removeFrameUpdateListener(this);
            this.done = true;
        }
    }
}
