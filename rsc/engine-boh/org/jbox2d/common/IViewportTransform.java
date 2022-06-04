// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

public interface IViewportTransform
{
    boolean isYFlip();
    
    void setYFlip(final boolean p0);
    
    Vec2 getExtents();
    
    void setExtents(final Vec2 p0);
    
    void setExtents(final float p0, final float p1);
    
    Vec2 getCenter();
    
    void setCenter(final Vec2 p0);
    
    void setCenter(final float p0, final float p1);
    
    void setCamera(final float p0, final float p1, final float p2);
    
    void getWorldVectorToScreen(final Vec2 p0, final Vec2 p1);
    
    void getScreenVectorToWorld(final Vec2 p0, final Vec2 p1);
    
    Mat22 getMat22Representation();
    
    void getWorldToScreen(final Vec2 p0, final Vec2 p1);
    
    void getScreenToWorld(final Vec2 p0, final Vec2 p1);
    
    void mulByTransform(final Mat22 p0);
}
