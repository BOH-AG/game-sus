// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

public class TimeStep
{
    public float dt;
    public float inv_dt;
    public float dtRatio;
    public int velocityIterations;
    public int positionIterations;
    public boolean warmStarting;
}