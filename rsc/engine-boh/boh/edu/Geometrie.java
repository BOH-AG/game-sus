// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.internal.annotations.API;
import ea.actor.Geometry;

@API
public abstract class Geometrie<Actor extends Geometry> extends EduActor<Actor>
{
    @API
    public Geometrie(final Actor actor) {
        super(actor);
    }
    
    @API
    public void setzeFarbe(final String farbe) {
        this.getActor().setColor(Spiel.konvertiereVonFarbname(farbe));
    }
    
    @API
    public String nenneFarbe() {
        return Spiel.konvertiereZuFarbname(this.getActor().getColor());
    }
    
    @API
    public void animiereFarbe(final double dauerInSekunden, final String farbe) {
        this.getActor().animateColor((float)dauerInSekunden, Spiel.konvertiereVonFarbname(farbe));
    }
}
