// 
// Decompiled by Procyon v0.5.36
// 

package ea.collision;

import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import org.jbox2d.dynamics.contacts.Contact;
import ea.actor.Actor;

public class CollisionEvent<E extends Actor>
{
    private final Contact contact;
    private final E colliding;
    
    @Internal
    public CollisionEvent(final Contact contact, final E colliding) {
        this.contact = contact;
        this.colliding = colliding;
    }
    
    @API
    public E getColliding() {
        return this.colliding;
    }
    
    @API
    public void ignoreCollision() {
        this.contact.setEnabled(false);
        this.colliding.getPhysicsHandler().getWorldHandler().addContactToBlacklist(this.contact);
    }
}
