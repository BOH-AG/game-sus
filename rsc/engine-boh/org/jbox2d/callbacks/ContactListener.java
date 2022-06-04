// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.callbacks;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public interface ContactListener
{
    void beginContact(final Contact p0);
    
    void endContact(final Contact p0);
    
    void preSolve(final Contact p0, final Manifold p1);
    
    void postSolve(final Contact p0, final ContactImpulse p1);
}
