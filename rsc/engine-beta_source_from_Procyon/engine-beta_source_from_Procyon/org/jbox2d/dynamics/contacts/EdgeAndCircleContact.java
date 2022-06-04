// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

public class EdgeAndCircleContact extends Contact
{
    public EdgeAndCircleContact(final IWorldPool argPool) {
        super(argPool);
    }
    
    @Override
    public void init(final Fixture fA, final int indexA, final Fixture fB, final int indexB) {
        super.init(fA, indexA, fB, indexB);
        assert this.m_fixtureA.getType() == ShapeType.EDGE;
        assert this.m_fixtureB.getType() == ShapeType.CIRCLE;
    }
    
    @Override
    public void evaluate(final Manifold manifold, final Transform xfA, final Transform xfB) {
        this.pool.getCollision().collideEdgeAndCircle(manifold, (EdgeShape)this.m_fixtureA.getShape(), xfA, (CircleShape)this.m_fixtureB.getShape(), xfB);
    }
}
