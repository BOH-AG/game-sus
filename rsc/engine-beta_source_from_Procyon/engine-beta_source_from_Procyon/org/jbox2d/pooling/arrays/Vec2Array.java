// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.arrays;

import org.jbox2d.common.Vec2;
import java.util.HashMap;

public class Vec2Array
{
    private final HashMap<Integer, Vec2[]> map;
    
    public Vec2Array() {
        this.map = new HashMap<Integer, Vec2[]>();
    }
    
    public Vec2[] get(final int argLength) {
        assert argLength > 0;
        if (!this.map.containsKey(argLength)) {
            this.map.put(argLength, this.getInitializedArray(argLength));
        }
        assert this.map.get(argLength).length == argLength : "Array not built of correct length";
        return this.map.get(argLength);
    }
    
    protected Vec2[] getInitializedArray(final int argLength) {
        final Vec2[] ray = new Vec2[argLength];
        for (int i = 0; i < ray.length; ++i) {
            ray[i] = new Vec2();
        }
        return ray;
    }
}
