// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.Transform;

public class DistanceInput
{
    public Distance.DistanceProxy proxyA;
    public Distance.DistanceProxy proxyB;
    public Transform transformA;
    public Transform transformB;
    public boolean useRadii;
    
    public DistanceInput() {
        this.proxyA = new Distance.DistanceProxy();
        this.proxyB = new Distance.DistanceProxy();
        this.transformA = new Transform();
        this.transformB = new Transform();
    }
}
