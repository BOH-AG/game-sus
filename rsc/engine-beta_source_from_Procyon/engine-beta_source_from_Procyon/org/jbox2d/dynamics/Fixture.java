// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.common.Transform;
import org.jbox2d.common.MathUtils;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;

public class Fixture
{
    public float m_density;
    public Fixture m_next;
    public Body m_body;
    public Shape m_shape;
    public float m_friction;
    public float m_restitution;
    public FixtureProxy[] m_proxies;
    public int m_proxyCount;
    public final Filter m_filter;
    public boolean m_isSensor;
    public Object m_userData;
    private final AABB pool1;
    private final AABB pool2;
    private final Vec2 displacement;
    
    public Fixture() {
        this.pool1 = new AABB();
        this.pool2 = new AABB();
        this.displacement = new Vec2();
        this.m_userData = null;
        this.m_body = null;
        this.m_next = null;
        this.m_proxies = null;
        this.m_proxyCount = 0;
        this.m_shape = null;
        this.m_filter = new Filter();
    }
    
    public ShapeType getType() {
        return this.m_shape.getType();
    }
    
    public Shape getShape() {
        return this.m_shape;
    }
    
    public boolean isSensor() {
        return this.m_isSensor;
    }
    
    public void setSensor(final boolean sensor) {
        if (sensor != this.m_isSensor) {
            this.m_body.setAwake(true);
            this.m_isSensor = sensor;
        }
    }
    
    public void setFilterData(final Filter filter) {
        this.m_filter.set(filter);
        this.refilter();
    }
    
    public Filter getFilterData() {
        return this.m_filter;
    }
    
    public void refilter() {
        if (this.m_body == null) {
            return;
        }
        for (ContactEdge edge = this.m_body.getContactList(); edge != null; edge = edge.next) {
            final Contact contact = edge.contact;
            final Fixture fixtureA = contact.getFixtureA();
            final Fixture fixtureB = contact.getFixtureB();
            if (fixtureA == this || fixtureB == this) {
                contact.flagForFiltering();
            }
        }
        final World world = this.m_body.getWorld();
        if (world == null) {
            return;
        }
        final BroadPhase broadPhase = world.m_contactManager.m_broadPhase;
        for (int i = 0; i < this.m_proxyCount; ++i) {
            broadPhase.touchProxy(this.m_proxies[i].proxyId);
        }
    }
    
    public Body getBody() {
        return this.m_body;
    }
    
    public Fixture getNext() {
        return this.m_next;
    }
    
    public void setDensity(final float density) {
        assert density >= 0.0f;
        this.m_density = density;
    }
    
    public float getDensity() {
        return this.m_density;
    }
    
    public Object getUserData() {
        return this.m_userData;
    }
    
    public void setUserData(final Object data) {
        this.m_userData = data;
    }
    
    public boolean testPoint(final Vec2 p) {
        return this.m_shape.testPoint(this.m_body.m_xf, p);
    }
    
    public boolean raycast(final RayCastOutput output, final RayCastInput input, final int childIndex) {
        return this.m_shape.raycast(output, input, this.m_body.m_xf, childIndex);
    }
    
    public void getMassData(final MassData massData) {
        this.m_shape.computeMass(massData, this.m_density);
    }
    
    public float getFriction() {
        return this.m_friction;
    }
    
    public void setFriction(final float friction) {
        this.m_friction = friction;
    }
    
    public float getRestitution() {
        return this.m_restitution;
    }
    
    public void setRestitution(final float restitution) {
        this.m_restitution = restitution;
    }
    
    public AABB getAABB(final int childIndex) {
        assert childIndex >= 0 && childIndex < this.m_proxyCount;
        return this.m_proxies[childIndex].aabb;
    }
    
    public float computeDistance(final Vec2 p, final int childIndex, final Vec2 normalOut) {
        return this.m_shape.computeDistanceToOut(this.m_body.getTransform(), p, childIndex, normalOut);
    }
    
    public void create(final Body body, final FixtureDef def) {
        this.m_userData = def.userData;
        this.m_friction = def.friction;
        this.m_restitution = def.restitution;
        this.m_body = body;
        this.m_next = null;
        this.m_filter.set(def.filter);
        this.m_isSensor = def.isSensor;
        this.m_shape = def.shape.clone();
        final int childCount = this.m_shape.getChildCount();
        if (this.m_proxies == null) {
            this.m_proxies = new FixtureProxy[childCount];
            for (int i = 0; i < childCount; ++i) {
                this.m_proxies[i] = new FixtureProxy();
                this.m_proxies[i].fixture = null;
                this.m_proxies[i].proxyId = -1;
            }
        }
        if (this.m_proxies.length < childCount) {
            final FixtureProxy[] old = this.m_proxies;
            final int newLen = MathUtils.max(old.length * 2, childCount);
            System.arraycopy(old, 0, this.m_proxies = new FixtureProxy[newLen], 0, old.length);
            for (int j = 0; j < newLen; ++j) {
                if (j >= old.length) {
                    this.m_proxies[j] = new FixtureProxy();
                }
                this.m_proxies[j].fixture = null;
                this.m_proxies[j].proxyId = -1;
            }
        }
        this.m_proxyCount = 0;
        this.m_density = def.density;
    }
    
    public void destroy() {
        assert this.m_proxyCount == 0;
        this.m_shape = null;
        this.m_proxies = null;
        this.m_next = null;
    }
    
    public void createProxies(final BroadPhase broadPhase, final Transform xf) {
        assert this.m_proxyCount == 0;
        this.m_proxyCount = this.m_shape.getChildCount();
        for (int i = 0; i < this.m_proxyCount; ++i) {
            final FixtureProxy proxy = this.m_proxies[i];
            this.m_shape.computeAABB(proxy.aabb, xf, i);
            proxy.proxyId = broadPhase.createProxy(proxy.aabb, proxy);
            proxy.fixture = this;
            proxy.childIndex = i;
        }
    }
    
    public void destroyProxies(final BroadPhase broadPhase) {
        for (int i = 0; i < this.m_proxyCount; ++i) {
            final FixtureProxy proxy = this.m_proxies[i];
            broadPhase.destroyProxy(proxy.proxyId);
            proxy.proxyId = -1;
        }
        this.m_proxyCount = 0;
    }
    
    protected void synchronize(final BroadPhase broadPhase, final Transform transform1, final Transform transform2) {
        if (this.m_proxyCount == 0) {
            return;
        }
        for (int i = 0; i < this.m_proxyCount; ++i) {
            final FixtureProxy proxy = this.m_proxies[i];
            final AABB aabb1 = this.pool1;
            final AABB aab = this.pool2;
            this.m_shape.computeAABB(aabb1, transform1, proxy.childIndex);
            this.m_shape.computeAABB(aab, transform2, proxy.childIndex);
            proxy.aabb.lowerBound.x = ((aabb1.lowerBound.x < aab.lowerBound.x) ? aabb1.lowerBound.x : aab.lowerBound.x);
            proxy.aabb.lowerBound.y = ((aabb1.lowerBound.y < aab.lowerBound.y) ? aabb1.lowerBound.y : aab.lowerBound.y);
            proxy.aabb.upperBound.x = ((aabb1.upperBound.x > aab.upperBound.x) ? aabb1.upperBound.x : aab.upperBound.x);
            proxy.aabb.upperBound.y = ((aabb1.upperBound.y > aab.upperBound.y) ? aabb1.upperBound.y : aab.upperBound.y);
            this.displacement.x = transform2.p.x - transform1.p.x;
            this.displacement.y = transform2.p.y - transform1.p.y;
            broadPhase.moveProxy(proxy.proxyId, proxy.aabb, this.displacement);
        }
    }
}
