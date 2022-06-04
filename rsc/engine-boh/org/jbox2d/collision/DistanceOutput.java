// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.Vec2;

public class DistanceOutput
{
    public final Vec2 pointA;
    public final Vec2 pointB;
    public float distance;
    public int iterations;
    
    public DistanceOutput() {
        this.pointA = new Vec2();
        this.pointB = new Vec2();
    }
}
