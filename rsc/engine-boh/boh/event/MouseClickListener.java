// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import ea.internal.annotations.API;
import ea.Vector;

public interface MouseClickListener
{
    @API
    void onMouseDown(final Vector p0, final MouseButton p1);
    
    @API
    default void onMouseUp(final Vector position, final MouseButton button) {
    }
}
