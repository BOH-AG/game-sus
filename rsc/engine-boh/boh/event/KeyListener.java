// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import java.awt.event.KeyEvent;
import ea.internal.annotations.API;

@API
public interface KeyListener
{
    @API
    void onKeyDown(final KeyEvent p0);
    
    @API
    default void onKeyUp(final KeyEvent e) {
    }
}
