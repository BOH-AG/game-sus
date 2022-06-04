// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu.event;

import ea.edu.Spiel;
import ea.internal.annotations.API;

@API
public interface Ticker
{
    @API
    void tick();
    
    @API
    default void starteTickerNeu(final double intervalInS) {
        Spiel.getActiveScene().addEduTicker((float)intervalInS, this);
    }
    
    @API
    default void stoppeTicker() {
        Spiel.getActiveScene().removeEduTicker(this);
    }
}
