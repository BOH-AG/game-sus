// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import org.jbox2d.collision.shapes.Shape;
import java.awt.Graphics2D;
import ea.internal.annotations.Internal;
import ea.internal.annotations.API;
import java.util.concurrent.ConcurrentHashMap;
import ea.internal.ShapeBuilder;
import ea.internal.graphics.AnimationFrame;
import java.util.Map;

public class StatefulAnimation<State> extends Actor
{
    private final Map<State, AnimationFrame[]> states;
    private final Map<State, State> stateTransitions;
    private State currentState;
    private AnimationFrame[] currentAnimation;
    private float currentTime;
    private int currentIndex;
    private float width;
    private float height;
    private boolean flipHorizontal;
    private boolean flipVertical;
    private boolean animationPaused;
    
    public StatefulAnimation(final float width, final float height) {
        super(() -> ShapeBuilder.createSimpleRectangularShape(width, height));
        this.states = new ConcurrentHashMap<State, AnimationFrame[]>();
        this.stateTransitions = new ConcurrentHashMap<State, State>();
        this.currentState = null;
        this.currentAnimation = null;
        this.currentTime = 0.0f;
        this.currentIndex = 0;
        this.flipHorizontal = false;
        this.flipVertical = false;
        this.animationPaused = false;
        this.width = width;
        this.height = height;
        this.addFrameUpdateListener(this::internalOnFrameUpdate);
    }
    
    @API
    public float getWidth() {
        return this.width;
    }
    
    @API
    public float getHeight() {
        return this.height;
    }
    
    @API
    public void addState(final State state, final Animation stateAnimation) {
        if (this.states.containsKey(state)) {
            throw new RuntimeException("Zustandsname wird bereits in diesem Objekt genutzt: " + state);
        }
        final AnimationFrame[] frames = stateAnimation.getFrames();
        this.states.put(state, frames);
        this.stateTransitions.put(state, state);
        if (this.currentState == null) {
            this.currentState = state;
            this.currentAnimation = frames;
        }
    }
    
    @API
    public void setState(final State state) {
        if (!this.states.containsKey(state)) {
            throw new RuntimeException("Zustand nicht nicht vorhanden: " + state);
        }
        this.currentIndex = 0;
        this.currentState = state;
        this.currentTime = 0.0f;
        this.currentAnimation = this.states.get(state);
    }
    
    @API
    public void changeState(final State state) {
        if (!state.equals(this.currentState)) {
            this.setState(state);
        }
    }
    
    @API
    public boolean hasState(final State state) {
        return this.states.containsKey(state);
    }
    
    @API
    public State getCurrentState() {
        return this.currentState;
    }
    
    @API
    public void setFlipHorizontal(final boolean flipHorizontal) {
        this.flipHorizontal = flipHorizontal;
    }
    
    @API
    public void setFlipVertical(final boolean flipVertical) {
        this.flipVertical = flipVertical;
    }
    
    @API
    public boolean isFlipHorizontal() {
        return this.flipHorizontal;
    }
    
    @API
    public boolean isFlipVertical() {
        return this.flipVertical;
    }
    
    @API
    public void setAnimationPaused(final boolean animationPaused) {
        this.animationPaused = animationPaused;
    }
    
    @API
    public boolean isAnimationPaused() {
        return this.animationPaused;
    }
    
    @API
    public void setStateTransition(final State stateFrom, final State stateTo) {
        if (!this.states.containsKey(stateFrom)) {
            throw new RuntimeException("Der Von-Zustand ist nicht in dieser Animation eingepflegt: " + stateFrom);
        }
        if (!this.states.containsKey(stateTo)) {
            throw new RuntimeException("Der To-Zustand ist nicht in dieser Animation eingepflegt: " + stateTo);
        }
        this.stateTransitions.remove(stateFrom);
        this.stateTransitions.put(stateFrom, stateTo);
    }
    
    @API
    public void setFrameDuration(final State state, final float frameDuration) {
        if (!this.states.containsKey(state)) {
            throw new RuntimeException("Der Zustand ist nicht bekannt: " + state);
        }
        for (final AnimationFrame frame : this.states.get(state)) {
            frame.setDuration(frameDuration);
        }
    }
    
    @Internal
    private void internalOnFrameUpdate(final float frameDuration) {
        if (this.currentAnimation == null || this.currentAnimation.length == 0 || this.animationPaused) {
            return;
        }
        this.currentTime += frameDuration;
        final AnimationFrame currentFrame = this.currentAnimation[this.currentIndex];
        while (this.currentTime > currentFrame.getDuration()) {
            this.currentTime -= currentFrame.getDuration();
            if (this.currentIndex + 1 == this.currentAnimation.length) {
                this.currentIndex = 0;
                final State nextState = this.stateTransitions.get(this.currentState);
                final AnimationFrame[] nextAnimation = this.states.get(nextState);
                this.currentState = nextState;
                this.currentAnimation = nextAnimation;
            }
            else {
                ++this.currentIndex;
            }
        }
    }
    
    @API
    public void setSize(final float width, final float height) {
        Actor.assertWidthAndHeight(width, height);
        this.width = width;
        this.height = height;
        this.setShape(() -> ShapeBuilder.createSimpleRectangularShape(width, height));
    }
    
    @Internal
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        if (this.currentAnimation == null || this.currentAnimation.length == 0) {
            return;
        }
        this.currentAnimation[this.currentIndex].render(g, this.width * pixelPerMeter, this.height * pixelPerMeter, this.flipHorizontal, this.flipVertical);
    }
}
