// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.callbacks.PairCallback;

public class ContactManager implements PairCallback
{
    public BroadPhase m_broadPhase;
    public Contact m_contactList;
    public int m_contactCount;
    public ContactFilter m_contactFilter;
    public ContactListener m_contactListener;
    private final World pool;
    
    public ContactManager(final World argPool, final BroadPhase broadPhase) {
        this.m_contactList = null;
        this.m_contactCount = 0;
        this.m_contactFilter = new ContactFilter();
        this.m_contactListener = null;
        this.m_broadPhase = broadPhase;
        this.pool = argPool;
    }
    
    @Override
    public void addPair(final Object proxyUserDataA, final Object proxyUserDataB) {
        final FixtureProxy proxyA = (FixtureProxy)proxyUserDataA;
        final FixtureProxy proxyB = (FixtureProxy)proxyUserDataB;
        Fixture fixtureA = proxyA.fixture;
        Fixture fixtureB = proxyB.fixture;
        int indexA = proxyA.childIndex;
        int indexB = proxyB.childIndex;
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();
        if (bodyA == bodyB) {
            return;
        }
        for (ContactEdge edge = bodyB.getContactList(); edge != null; edge = edge.next) {
            if (edge.other == bodyA) {
                final Fixture fA = edge.contact.getFixtureA();
                final Fixture fB = edge.contact.getFixtureB();
                final int iA = edge.contact.getChildIndexA();
                final int iB = edge.contact.getChildIndexB();
                if (fA == fixtureA && iA == indexA && fB == fixtureB && iB == indexB) {
                    return;
                }
                if (fA == fixtureB && iA == indexB && fB == fixtureA && iB == indexA) {
                    return;
                }
            }
        }
        if (!bodyB.shouldCollide(bodyA)) {
            return;
        }
        if (this.m_contactFilter != null && !this.m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
            return;
        }
        final Contact c = this.pool.popContact(fixtureA, indexA, fixtureB, indexB);
        if (c == null) {
            return;
        }
        fixtureA = c.getFixtureA();
        fixtureB = c.getFixtureB();
        indexA = c.getChildIndexA();
        indexB = c.getChildIndexB();
        bodyA = fixtureA.getBody();
        bodyB = fixtureB.getBody();
        c.m_prev = null;
        c.m_next = this.m_contactList;
        if (this.m_contactList != null) {
            this.m_contactList.m_prev = c;
        }
        this.m_contactList = c;
        c.m_nodeA.contact = c;
        c.m_nodeA.other = bodyB;
        c.m_nodeA.prev = null;
        c.m_nodeA.next = bodyA.m_contactList;
        if (bodyA.m_contactList != null) {
            bodyA.m_contactList.prev = c.m_nodeA;
        }
        bodyA.m_contactList = c.m_nodeA;
        c.m_nodeB.contact = c;
        c.m_nodeB.other = bodyA;
        c.m_nodeB.prev = null;
        c.m_nodeB.next = bodyB.m_contactList;
        if (bodyB.m_contactList != null) {
            bodyB.m_contactList.prev = c.m_nodeB;
        }
        bodyB.m_contactList = c.m_nodeB;
        if (!fixtureA.isSensor() && !fixtureB.isSensor()) {
            bodyA.setAwake(true);
            bodyB.setAwake(true);
        }
        ++this.m_contactCount;
    }
    
    public void findNewContacts() {
        this.m_broadPhase.updatePairs(this);
    }
    
    public void destroy(final Contact c) {
        final Fixture fixtureA = c.getFixtureA();
        final Fixture fixtureB = c.getFixtureB();
        final Body bodyA = fixtureA.getBody();
        final Body bodyB = fixtureB.getBody();
        if (this.m_contactListener != null && c.isTouching()) {
            this.m_contactListener.endContact(c);
        }
        if (c.m_prev != null) {
            c.m_prev.m_next = c.m_next;
        }
        if (c.m_next != null) {
            c.m_next.m_prev = c.m_prev;
        }
        if (c == this.m_contactList) {
            this.m_contactList = c.m_next;
        }
        if (c.m_nodeA.prev != null) {
            c.m_nodeA.prev.next = c.m_nodeA.next;
        }
        if (c.m_nodeA.next != null) {
            c.m_nodeA.next.prev = c.m_nodeA.prev;
        }
        if (c.m_nodeA == bodyA.m_contactList) {
            bodyA.m_contactList = c.m_nodeA.next;
        }
        if (c.m_nodeB.prev != null) {
            c.m_nodeB.prev.next = c.m_nodeB.next;
        }
        if (c.m_nodeB.next != null) {
            c.m_nodeB.next.prev = c.m_nodeB.prev;
        }
        if (c.m_nodeB == bodyB.m_contactList) {
            bodyB.m_contactList = c.m_nodeB.next;
        }
        this.pool.pushContact(c);
        --this.m_contactCount;
    }
    
    public void collide() {
        Contact c = this.m_contactList;
        while (c != null) {
            final Fixture fixtureA = c.getFixtureA();
            final Fixture fixtureB = c.getFixtureB();
            final int indexA = c.getChildIndexA();
            final int indexB = c.getChildIndexB();
            final Body bodyA = fixtureA.getBody();
            final Body bodyB = fixtureB.getBody();
            if ((c.m_flags & 0x8) == 0x8) {
                if (!bodyB.shouldCollide(bodyA)) {
                    final Contact cNuke = c;
                    c = cNuke.getNext();
                    this.destroy(cNuke);
                    continue;
                }
                if (this.m_contactFilter != null && !this.m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
                    final Contact cNuke = c;
                    c = cNuke.getNext();
                    this.destroy(cNuke);
                    continue;
                }
                final Contact contact = c;
                contact.m_flags &= 0xFFFFFFF7;
            }
            final boolean activeA = bodyA.isAwake() && bodyA.m_type != BodyType.STATIC;
            final boolean activeB = bodyB.isAwake() && bodyB.m_type != BodyType.STATIC;
            if (!activeA && !activeB) {
                c = c.getNext();
            }
            else {
                final int proxyIdA = fixtureA.m_proxies[indexA].proxyId;
                final int proxyIdB = fixtureB.m_proxies[indexB].proxyId;
                final boolean overlap = this.m_broadPhase.testOverlap(proxyIdA, proxyIdB);
                if (!overlap) {
                    final Contact cNuke2 = c;
                    c = cNuke2.getNext();
                    this.destroy(cNuke2);
                }
                else {
                    c.update(this.m_contactListener);
                    c = c.getNext();
                }
            }
        }
    }
}
