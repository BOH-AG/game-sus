// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.internal.annotations.API;

@API
public class Text extends Geometrie<ea.actor.Text>
{
    @API
    public Text(final String inhalt, final double hoehe) {
        super(new ea.actor.Text(inhalt, (float)hoehe));
        this.setzeFarbe("weiss");
    }
    
    @API
    public void setzeInhalt(final String inhalt) {
        this.getActor().setContent(inhalt);
    }
    
    @API
    public void setzeHoehe(final double hoehe) {
        this.getActor().setHeight((float)hoehe);
    }
}
