// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import ea.internal.annotations.API;

public interface MouseWheelListenerContainer
{
    EventListeners<MouseWheelListener> getMouseWheelListeners();
    
    @API
    default void addMouseWheelListener(final MouseWheelListener mouseWheelListener) {
        this.getMouseWheelListeners().add(mouseWheelListener);
    }
    
    @API
    default void removeMouseWheelListener(final MouseWheelListener mouseWheelListener) {
        this.getMouseWheelListeners().remove(mouseWheelListener);
    }
}
