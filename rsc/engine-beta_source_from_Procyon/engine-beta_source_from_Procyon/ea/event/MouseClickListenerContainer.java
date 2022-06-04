// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

public interface MouseClickListenerContainer
{
    EventListeners<MouseClickListener> getMouseClickListeners();
    
    default void addMouseClickListener(final MouseClickListener mouseClickListener) {
        this.getMouseClickListeners().add(mouseClickListener);
    }
    
    default void removeMouseClickListener(final MouseClickListener mouseClickListener) {
        this.getMouseClickListeners().remove(mouseClickListener);
    }
}
