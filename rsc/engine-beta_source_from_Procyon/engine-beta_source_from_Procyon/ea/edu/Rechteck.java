// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.internal.annotations.API;
import ea.actor.Rectangle;

@API
public class Rechteck extends Geometrie<Rectangle>
{
    @API
    public Rechteck(final double breite, final double hoehe) {
        super(new Rectangle((float)breite, (float)hoehe));
        this.setzeFarbe("blau");
    }
}
