// 
// Decompiled by Procyon v0.5.36
// 

package ea.collision;

import ea.internal.annotations.API;
import ea.actor.Actor;

public interface CollisionListener<E extends Actor>
{
    @API
    void onCollision(final CollisionEvent<E> p0);
    
    @API
    default void onCollisionEnd(final CollisionEvent<E> collisionEvent) {
    }
}
