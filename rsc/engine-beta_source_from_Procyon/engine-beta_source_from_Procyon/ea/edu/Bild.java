// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.internal.annotations.API;
import ea.actor.Image;

@API
public class Bild extends EduActor<Image>
{
    @API
    public Bild(final double breite, final double hoehe, final String filepath) {
        super(new Image(filepath, (float)breite, (float)hoehe));
    }
}
