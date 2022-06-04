// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import ea.internal.annotations.API;

public interface KeyListenerContainer
{
    EventListeners<KeyListener> getKeyListeners();
    
    @API
    default void addKeyListener(final KeyListener keyListener) {
        this.getKeyListeners().add(keyListener);
    }
    
    @API
    default void removeKeyListener(final KeyListener keyListener) {
        this.getKeyListeners().remove(keyListener);
    }
}
