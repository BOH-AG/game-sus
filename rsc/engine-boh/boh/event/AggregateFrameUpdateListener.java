// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import ea.internal.annotations.API;
import ea.FrameUpdateListener;

public abstract class AggregateFrameUpdateListener implements FrameUpdateListener, FrameUpdateListenerContainer
{
    private final EventListeners<FrameUpdateListener> listeners;
    private boolean paused;
    
    public AggregateFrameUpdateListener() {
        this.listeners = new EventListeners<FrameUpdateListener>();
        this.paused = false;
    }
    
    @API
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
    
    @API
    public boolean isPaused() {
        return this.paused;
    }
    
    @Override
    public void onFrameUpdate(final float deltaSeconds) {
        if (!this.paused) {
            this.listeners.invoke(listener -> listener.onFrameUpdate(deltaSeconds));
        }
    }
    
    @Override
    public EventListeners<FrameUpdateListener> getFrameUpdateListeners() {
        return this.listeners;
    }
}
