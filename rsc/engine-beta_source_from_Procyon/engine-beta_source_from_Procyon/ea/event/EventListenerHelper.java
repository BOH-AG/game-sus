// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import ea.FrameUpdateListener;

public class EventListenerHelper
{
    public static void autoRegisterListeners(final Object target) {
        if (target instanceof KeyListenerContainer && target instanceof KeyListener) {
            ((KeyListenerContainer)target).addKeyListener((KeyListener)target);
        }
        if (target instanceof MouseClickListenerContainer && target instanceof MouseClickListener) {
            ((MouseClickListenerContainer)target).addMouseClickListener((MouseClickListener)target);
        }
        if (target instanceof MouseWheelListenerContainer && target instanceof MouseWheelListener) {
            ((MouseWheelListenerContainer)target).addMouseWheelListener((MouseWheelListener)target);
        }
        if (target instanceof FrameUpdateListenerContainer && target instanceof FrameUpdateListener) {
            ((FrameUpdateListenerContainer)target).addFrameUpdateListener((FrameUpdateListener)target);
        }
    }
}
