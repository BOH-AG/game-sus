// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import ea.internal.annotations.Internal;
import ea.internal.annotations.API;

@API
public enum BodyType
{
    STATIC(1.0f), 
    DYNAMIC(1.0f), 
    KINEMATIC(1.0f), 
    SENSOR(0.0f), 
    PARTICLE(0.0f);
    
    private float defaultGravityScale;
    
    private BodyType(final float defaultGravityScale) {
        this.defaultGravityScale = defaultGravityScale;
    }
    
    @Internal
    public org.jbox2d.dynamics.BodyType toBox2D() {
        switch (this) {
            case STATIC: {
                return org.jbox2d.dynamics.BodyType.STATIC;
            }
            case DYNAMIC:
            case SENSOR:
            case PARTICLE: {
                return org.jbox2d.dynamics.BodyType.DYNAMIC;
            }
            case KINEMATIC: {
                return org.jbox2d.dynamics.BodyType.KINEMATIC;
            }
            default: {
                throw new RuntimeException("Unhandled body type: " + this);
            }
        }
    }
    
    public float getDefaultGravityScale() {
        return this.defaultGravityScale;
    }
    
    public boolean isSensorType() {
        return this == BodyType.SENSOR;
    }
}
