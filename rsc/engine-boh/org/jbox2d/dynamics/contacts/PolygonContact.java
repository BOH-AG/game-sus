// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

public class PolygonContact extends Contact
{
    public PolygonContact(final IWorldPool argPool) {
        super(argPool);
    }
    
    public void init(final Fixture fixtureA, final Fixture fixtureB) {
        super.init(fixtureA, 0, fixtureB, 0);
        assert this.m_fixtureA.getType() == ShapeType.POLYGON;
        assert this.m_fixtureB.getType() == ShapeType.POLYGON;
    }
    
    @Override
    public void evaluate(final Manifold manifold, final Transform xfA, final Transform xfB) {
        this.pool.getCollision().collidePolygons(manifold, (PolygonShape)this.m_fixtureA.getShape(), xfA, (PolygonShape)this.m_fixtureB.getShape(), xfB);
    }
}
