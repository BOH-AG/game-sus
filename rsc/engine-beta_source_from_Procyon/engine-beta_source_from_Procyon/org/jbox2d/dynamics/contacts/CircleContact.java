// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

public class CircleContact extends Contact
{
    public CircleContact(final IWorldPool argPool) {
        super(argPool);
    }
    
    public void init(final Fixture fixtureA, final Fixture fixtureB) {
        super.init(fixtureA, 0, fixtureB, 0);
        assert this.m_fixtureA.getType() == ShapeType.CIRCLE;
        assert this.m_fixtureB.getType() == ShapeType.CIRCLE;
    }
    
    @Override
    public void evaluate(final Manifold manifold, final Transform xfA, final Transform xfB) {
        this.pool.getCollision().collideCircles(manifold, (CircleShape)this.m_fixtureA.getShape(), xfA, (CircleShape)this.m_fixtureB.getShape(), xfB);
    }
}
