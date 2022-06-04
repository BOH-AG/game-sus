// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu.event;

import ea.internal.annotations.API;

@API
public interface MausKlickReagierbar
{
    @API
    void klickReagieren(final double p0, final double p1);
    
    @API
    default void klickLosgelassenReagieren(final double mx, final double my) {
    }
}
