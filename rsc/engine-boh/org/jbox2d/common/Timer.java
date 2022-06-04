// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.common;

public class Timer
{
    private long resetNanos;
    
    public Timer() {
        this.reset();
    }
    
    public void reset() {
        this.resetNanos = System.nanoTime();
    }
    
    public float getMilliseconds() {
        return (System.nanoTime() - this.resetNanos) / 1000L * 1.0f / 1000.0f;
    }
}
