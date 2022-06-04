// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation;

import ea.internal.annotations.API;
import ea.event.FrameUpdateListenerContainer;
import ea.event.EventListeners;
import java.util.function.Consumer;
import ea.FrameUpdateListener;

public class ValueAnimator<Value> implements FrameUpdateListener
{
    private Consumer<Value> consumer;
    private Interpolator<Value> interpolator;
    private AnimationMode mode;
    private float currentTime;
    private float duration;
    private boolean complete;
    private boolean paused;
    private boolean goingBackwards;
    private EventListeners<Consumer<Value>> completionListeners;
    
    public ValueAnimator(final float duration, final Consumer<Value> consumer, final Interpolator<Value> interpolator, final AnimationMode mode, final FrameUpdateListenerContainer parent) {
        this.currentTime = 0.0f;
        this.complete = false;
        this.paused = false;
        this.goingBackwards = false;
        this.completionListeners = new EventListeners<Consumer<Value>>();
        this.duration = duration;
        this.consumer = consumer;
        this.interpolator = interpolator;
        this.mode = mode;
        if (mode == AnimationMode.SINGLE) {
            this.addCompletionListener(v -> parent.removeFrameUpdateListener(this));
        }
    }
    
    @API
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
    
    @API
    public boolean isPaused() {
        return this.paused;
    }
    
    public ValueAnimator(final float duration, final Consumer<Value> consumer, final Interpolator<Value> interpolator, final FrameUpdateListenerContainer parent) {
        this(duration, consumer, interpolator, AnimationMode.SINGLE, parent);
    }
    
    @API
    public void setProgress(final float progress) {
        if (progress < 0.0f || progress > 1.0f) {
            throw new IllegalArgumentException("Der eingegebene Progess muss zwischen 0 und 1 liegen. War " + progress);
        }
        this.goingBackwards = false;
        this.currentTime = this.duration * progress;
        this.interpolator.interpolate(progress);
    }
    
    @Override
    public void onFrameUpdate(final float deltaSeconds) {
        if (this.paused) {
            return;
        }
        float progress = 0.0f;
        if (!this.goingBackwards) {
            this.currentTime += deltaSeconds;
            if (this.currentTime > this.duration) {
                switch (this.mode) {
                    case REPEATED: {
                        this.currentTime %= this.duration;
                        progress = this.currentTime / this.duration;
                        break;
                    }
                    case SINGLE: {
                        this.currentTime = this.duration;
                        progress = 1.0f;
                        this.complete = true;
                        final Value finalValue = this.interpolator.interpolate(1.0f);
                        this.completionListeners.invoke(listener -> listener.accept(finalValue));
                        break;
                    }
                    case PINGPONG: {
                        this.goingBackwards = true;
                        progress = 1.0f;
                        break;
                    }
                    default: {
                        progress = -1.0f;
                        break;
                    }
                }
            }
            else {
                progress = this.currentTime / this.duration;
            }
        }
        else {
            this.currentTime -= deltaSeconds;
            if (this.currentTime < 0.0f) {
                this.goingBackwards = false;
                progress = 0.0f;
            }
            else {
                progress = this.currentTime / this.duration;
            }
        }
        this.consumer.accept(this.interpolator.interpolate(progress));
    }
    
    public ValueAnimator<Value> addCompletionListener(final Consumer<Value> listener) {
        if (this.complete) {
            listener.accept(this.interpolator.interpolate(1.0f));
        }
        else {
            this.completionListeners.add(listener);
        }
        return this;
    }
}
