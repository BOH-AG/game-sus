// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.internal.annotations.API;
import ea.actor.Circle;

@API
public class Kreis extends Geometrie<Circle>
{
    @API
    public Kreis(final double radius) {
        super(new Circle((float)radius * 2.0f));
        this.setzeFarbe("gelb");
    }
}
