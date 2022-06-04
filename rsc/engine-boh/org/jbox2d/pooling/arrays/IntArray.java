// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.arrays;

import java.util.HashMap;

public class IntArray
{
    private final HashMap<Integer, int[]> map;
    
    public IntArray() {
        this.map = new HashMap<Integer, int[]>();
    }
    
    public int[] get(final int argLength) {
        assert argLength > 0;
        if (!this.map.containsKey(argLength)) {
            this.map.put(argLength, this.getInitializedArray(argLength));
        }
        assert this.map.get(argLength).length == argLength : "Array not built of correct length";
        return this.map.get(argLength);
    }
    
    protected int[] getInitializedArray(final int argLength) {
        return new int[argLength];
    }
}
