// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import ea.internal.annotations.API;

@API
public enum Direction
{
    UP, 
    UP_RIGHT, 
    RIGHT, 
    DOWN_RIGHT, 
    DOWN, 
    DOWN_LEFT, 
    LEFT, 
    UP_LEFT, 
    NONE;
    
    @API
    public Vector toVector() {
        switch (this) {
            case UP: {
                return Vector.UP;
            }
            case UP_RIGHT: {
                return Vector.UP.add(Vector.RIGHT);
            }
            case RIGHT: {
                return Vector.RIGHT;
            }
            case DOWN_RIGHT: {
                return Vector.DOWN.add(Vector.RIGHT);
            }
            case DOWN: {
                return Vector.DOWN;
            }
            case DOWN_LEFT: {
                return Vector.DOWN.add(Vector.LEFT);
            }
            case LEFT: {
                return Vector.LEFT;
            }
            case UP_LEFT: {
                return Vector.UP.add(Vector.LEFT);
            }
            case NONE: {
                return Vector.NULL;
            }
            default: {
                throw new IllegalStateException("Invalid enum value");
            }
        }
    }
}
