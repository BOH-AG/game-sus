// 
// Decompiled by Procyon v0.5.36
// 

package ea.animation;

import ea.internal.annotations.API;
import ea.animation.interpolation.LinearFloat;
import ea.FrameUpdateListener;
import ea.animation.interpolation.SinusFloat;
import ea.event.FrameUpdateListenerContainer;
import ea.animation.interpolation.CosinusFloat;
import ea.Vector;
import ea.actor.Actor;
import ea.event.AggregateFrameUpdateListener;

public class CircleAnimation extends AggregateFrameUpdateListener
{
    @API
    public CircleAnimation(final Actor actor, final Vector rotationCenter, final float durationInSeconds, final boolean circleClockwise, final boolean rotateActor) {
        final Vector currentActorCenter = actor.getCenter();
        final float radius = new Vector(rotationCenter, currentActorCenter).getLength();
        final Vector rightPoint = rotationCenter.add(new Vector(radius, 0.0f));
        final ValueAnimator<Float> aX = new ValueAnimator<Float>(durationInSeconds, x -> actor.setCenter(x, actor.getCenter().getY()), new CosinusFloat(rightPoint.getX(), radius), AnimationMode.REPEATED, this);
        final ValueAnimator<Float> aY = new ValueAnimator<Float>(durationInSeconds, y -> actor.setCenter(actor.getCenter().getX(), y), new SinusFloat(rotationCenter.getY(), circleClockwise ? (-radius) : radius), AnimationMode.REPEATED, this);
        float angle = rotationCenter.negate().add(rightPoint).getAngle(rotationCenter.negate().add(currentActorCenter));
        if ((circleClockwise && currentActorCenter.getY() > rotationCenter.getY()) || (!circleClockwise && currentActorCenter.getY() < rotationCenter.getY())) {
            angle = 360.0f - angle;
        }
        final float actualProgress = angle / 360.0f;
        aX.setProgress(actualProgress);
        aY.setProgress(actualProgress);
        this.addFrameUpdateListener(aX);
        this.addFrameUpdateListener(aY);
        if (rotateActor) {
            final float rotationAngle = circleClockwise ? angle : (-angle);
            final ValueAnimator<Float> aR = new ValueAnimator<Float>(durationInSeconds, actor::setRotation, new LinearFloat(-rotationAngle, -rotationAngle + 360 * (circleClockwise ? -1 : 1)), AnimationMode.REPEATED, actor);
            aR.setProgress(actualProgress);
            this.addFrameUpdateListener(aR);
        }
    }
}
