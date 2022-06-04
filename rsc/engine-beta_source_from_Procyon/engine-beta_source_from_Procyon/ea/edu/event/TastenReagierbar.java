// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu.event;

import ea.internal.annotations.API;

@API
public interface TastenReagierbar
{
    @API
    void tasteReagieren(final int p0);
    
    @API
    default void tasteLosgelassenReagieren(final int tastenCode) {
    }
}
