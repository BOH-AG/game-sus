// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import ea.internal.PeriodicTask;
import ea.internal.SingleTask;
import ea.FrameUpdateListener;
import ea.internal.annotations.API;

@API
public interface FrameUpdateListenerContainer
{
    EventListeners<FrameUpdateListener> getFrameUpdateListeners();
    
    @API
    default void addFrameUpdateListener(final FrameUpdateListener frameUpdateListener) {
        this.getFrameUpdateListeners().add(frameUpdateListener);
    }
    
    @API
    default void removeFrameUpdateListener(final FrameUpdateListener frameUpdateListener) {
        this.getFrameUpdateListeners().remove(frameUpdateListener);
    }
    
    @API
    default void defer(final Runnable runnable) {
        final FrameUpdateListener frameUpdateListener = new FrameUpdateListener() {
            @Override
            public void onFrameUpdate(final float time) {
                FrameUpdateListenerContainer.this.removeFrameUpdateListener(this);
                runnable.run();
            }
        };
        this.addFrameUpdateListener(frameUpdateListener);
    }
    
    @API
    default FrameUpdateListener delay(final float timeInSeconds, final Runnable runnable) {
        final FrameUpdateListener singleTask = new SingleTask(timeInSeconds, runnable, this);
        this.addFrameUpdateListener(singleTask);
        return singleTask;
    }
    
    @API
    default FrameUpdateListener repeat(final float intervalInSeconds, final Runnable runnable) {
        final FrameUpdateListener periodicTask = new PeriodicTask(intervalInSeconds, runnable);
        this.addFrameUpdateListener(periodicTask);
        return periodicTask;
    }
}
