// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.Vector;
import ea.internal.annotations.API;
import ea.actor.Polygon;

@API
public class Dreieck extends Geometrie<Polygon>
{
    @API
    public Dreieck(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3) {
        super(new Polygon(new Vector[] { new Vector(x1, y1), new Vector(x2, y2), new Vector(x3, y3) }));
        this.setzeFarbe("rot");
    }
}
