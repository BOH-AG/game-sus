// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.dynamics.Body;

public class ContactEdge
{
    public Body other;
    public Contact contact;
    public ContactEdge prev;
    public ContactEdge next;
    
    public ContactEdge() {
        this.other = null;
        this.contact = null;
        this.prev = null;
        this.next = null;
    }
}
