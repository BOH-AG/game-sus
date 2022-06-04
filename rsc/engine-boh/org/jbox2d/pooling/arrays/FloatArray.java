// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.arrays;

import java.util.HashMap;

public class FloatArray
{
    private final HashMap<Integer, float[]> map;
    
    public FloatArray() {
        this.map = new HashMap<Integer, float[]>();
    }
    
    public float[] get(final int argLength) {
        assert argLength > 0;
        if (!this.map.containsKey(argLength)) {
            this.map.put(argLength, this.getInitializedArray(argLength));
        }
        assert this.map.get(argLength).length == argLength : "Array not built of correct length";
        return this.map.get(argLength);
    }
    
    protected float[] getInitializedArray(final int argLength) {
        return new float[argLength];
    }
}
