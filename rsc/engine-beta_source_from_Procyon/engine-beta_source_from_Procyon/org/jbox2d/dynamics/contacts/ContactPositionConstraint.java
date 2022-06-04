// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.common.Settings;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;

public class ContactPositionConstraint
{
    Vec2[] localPoints;
    final Vec2 localNormal;
    final Vec2 localPoint;
    int indexA;
    int indexB;
    float invMassA;
    float invMassB;
    final Vec2 localCenterA;
    final Vec2 localCenterB;
    float invIA;
    float invIB;
    Manifold.ManifoldType type;
    float radiusA;
    float radiusB;
    int pointCount;
    
    public ContactPositionConstraint() {
        this.localPoints = new Vec2[Settings.maxManifoldPoints];
        this.localNormal = new Vec2();
        this.localPoint = new Vec2();
        this.localCenterA = new Vec2();
        this.localCenterB = new Vec2();
        for (int i = 0; i < this.localPoints.length; ++i) {
            this.localPoints[i] = new Vec2();
        }
    }
}
