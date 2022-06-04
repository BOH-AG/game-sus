// 
// Decompiled by Procyon v0.5.36
// 

package ea.event;

import ea.internal.annotations.Internal;
import ea.internal.annotations.API;

@API
public class MouseWheelEvent
{
    private final float wheelRotation;
    
    @Internal
    public MouseWheelEvent(final float wheelRotation) {
        this.wheelRotation = wheelRotation;
    }
    
    @API
    public int getWheelRotation() {
        return (int)this.wheelRotation;
    }
    
    @API
    public float getPreciseWheelRotation() {
        return this.wheelRotation;
    }
}
