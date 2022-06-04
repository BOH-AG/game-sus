// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

public class Color3f
{
    public static final Color3f WHITE;
    public static final Color3f BLACK;
    public static final Color3f BLUE;
    public static final Color3f GREEN;
    public static final Color3f RED;
    public float x;
    public float y;
    public float z;
    
    public Color3f() {
        final float x = 0.0f;
        this.z = x;
        this.y = x;
        this.x = x;
    }
    
    public Color3f(final float r, final float g, final float b) {
        this.x = r;
        this.y = g;
        this.z = b;
    }
    
    public void set(final float r, final float g, final float b) {
        this.x = r;
        this.y = g;
        this.z = b;
    }
    
    public void set(final Color3f argColor) {
        this.x = argColor.x;
        this.y = argColor.y;
        this.z = argColor.z;
    }
    
    static {
        WHITE = new Color3f(1.0f, 1.0f, 1.0f);
        BLACK = new Color3f(0.0f, 0.0f, 0.0f);
        BLUE = new Color3f(0.0f, 0.0f, 1.0f);
        GREEN = new Color3f(0.0f, 1.0f, 0.0f);
        RED = new Color3f(1.0f, 0.0f, 0.0f);
    }
}
