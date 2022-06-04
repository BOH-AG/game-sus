// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal;

public final class DebugInfo
{
    private final float frameDuration;
    private final int bodyCount;
    
    public DebugInfo(final float frameDuration, final int bodyCount) {
        this.frameDuration = frameDuration;
        this.bodyCount = bodyCount;
    }
    
    public float getFrameDuration() {
        return this.frameDuration;
    }
    
    public int getBodyCount() {
        return this.bodyCount;
    }
}
