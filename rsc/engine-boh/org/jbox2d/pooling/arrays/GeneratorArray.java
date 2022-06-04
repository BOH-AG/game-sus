// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.arrays;

import org.jbox2d.particle.VoronoiDiagram;
import java.util.HashMap;

public class GeneratorArray
{
    private final HashMap<Integer, VoronoiDiagram.Generator[]> map;
    
    public GeneratorArray() {
        this.map = new HashMap<Integer, VoronoiDiagram.Generator[]>();
    }
    
    public VoronoiDiagram.Generator[] get(final int length) {
        assert length > 0;
        if (!this.map.containsKey(length)) {
            this.map.put(length, this.getInitializedArray(length));
        }
        assert this.map.get(length).length == length : "Array not built of correct length";
        return this.map.get(length);
    }
    
    protected VoronoiDiagram.Generator[] getInitializedArray(final int length) {
        final VoronoiDiagram.Generator[] ray = new VoronoiDiagram.Generator[length];
        for (int i = 0; i < ray.length; ++i) {
            ray[i] = new VoronoiDiagram.Generator();
        }
        return ray;
    }
}
