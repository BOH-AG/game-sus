// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation;

import ea.FrameUpdateListener;
import ea.event.FrameUpdateListenerContainer;
import ea.animation.interpolation.LinearFloat;
import ea.Vector;
import ea.actor.Actor;
import ea.event.AggregateFrameUpdateListener;

public class LineAnimation extends AggregateFrameUpdateListener
{
    public LineAnimation(final Actor actor, final Vector endPoint, final float durationInSeconds, final boolean pingpong) {
        final Vector center = actor.getCenter();
        this.addFrameUpdateListener(new ValueAnimator<Object>(durationInSeconds, x -> actor.setCenter(x, actor.getCenter().getY()), new LinearFloat(center.getX(), endPoint.getX()), pingpong ? AnimationMode.PINGPONG : AnimationMode.SINGLE, this));
        this.addFrameUpdateListener(new ValueAnimator<Object>(durationInSeconds, y -> actor.setCenter(actor.getCenter().getX(), y), new LinearFloat(center.getY(), endPoint.getY()), pingpong ? AnimationMode.PINGPONG : AnimationMode.SINGLE, this));
    }
}
