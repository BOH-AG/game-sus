// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.collision.shapes.EdgeShape;

public class ChainAndPolygonContact extends Contact
{
    private final EdgeShape edge;
    
    public ChainAndPolygonContact(final IWorldPool argPool) {
        super(argPool);
        this.edge = new EdgeShape();
    }
    
    @Override
    public void init(final Fixture fA, final int indexA, final Fixture fB, final int indexB) {
        super.init(fA, indexA, fB, indexB);
        assert this.m_fixtureA.getType() == ShapeType.CHAIN;
        assert this.m_fixtureB.getType() == ShapeType.POLYGON;
    }
    
    @Override
    public void evaluate(final Manifold manifold, final Transform xfA, final Transform xfB) {
        final ChainShape chain = (ChainShape)this.m_fixtureA.getShape();
        chain.getChildEdge(this.edge, this.m_indexA);
        this.pool.getCollision().collideEdgeAndPolygon(manifold, this.edge, xfA, (PolygonShape)this.m_fixtureB.getShape(), xfB);
    }
}
