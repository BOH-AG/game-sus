// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.Rot;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;

public class WorldManifold
{
    public final Vec2 normal;
    public final Vec2[] points;
    public final float[] separations;
    private final Vec2 pool3;
    private final Vec2 pool4;
    
    public WorldManifold() {
        this.pool3 = new Vec2();
        this.pool4 = new Vec2();
        this.normal = new Vec2();
        this.points = new Vec2[Settings.maxManifoldPoints];
        this.separations = new float[Settings.maxManifoldPoints];
        for (int i = 0; i < Settings.maxManifoldPoints; ++i) {
            this.points[i] = new Vec2();
        }
    }
    
    public final void initialize(final Manifold manifold, final Transform xfA, final float radiusA, final Transform xfB, final float radiusB) {
        if (manifold.pointCount == 0) {
            return;
        }
        switch (manifold.type) {
            case CIRCLES: {
                final Vec2 pointA = this.pool3;
                final Vec2 pointB = this.pool4;
                this.normal.x = 1.0f;
                this.normal.y = 0.0f;
                final Vec2 v = manifold.localPoint;
                pointA.x = xfA.q.c * v.x - xfA.q.s * v.y + xfA.p.x;
                pointA.y = xfA.q.s * v.x + xfA.q.c * v.y + xfA.p.y;
                final Vec2 mp0p = manifold.points[0].localPoint;
                pointB.x = xfB.q.c * mp0p.x - xfB.q.s * mp0p.y + xfB.p.x;
                pointB.y = xfB.q.s * mp0p.x + xfB.q.c * mp0p.y + xfB.p.y;
                if (MathUtils.distanceSquared(pointA, pointB) > 1.4210855E-14f) {
                    this.normal.x = pointB.x - pointA.x;
                    this.normal.y = pointB.y - pointA.y;
                    this.normal.normalize();
                }
                final float cAx = this.normal.x * radiusA + pointA.x;
                final float cAy = this.normal.y * radiusA + pointA.y;
                final float cBx = -this.normal.x * radiusB + pointB.x;
                final float cBy = -this.normal.y * radiusB + pointB.y;
                this.points[0].x = (cAx + cBx) * 0.5f;
                this.points[0].y = (cAy + cBy) * 0.5f;
                this.separations[0] = (cBx - cAx) * this.normal.x + (cBy - cAy) * this.normal.y;
                break;
            }
            case FACE_A: {
                final Vec2 planePoint = this.pool3;
                Rot.mulToOutUnsafe(xfA.q, manifold.localNormal, this.normal);
                Transform.mulToOut(xfA, manifold.localPoint, planePoint);
                final Vec2 clipPoint = this.pool4;
                for (int i = 0; i < manifold.pointCount; ++i) {
                    Transform.mulToOut(xfB, manifold.points[i].localPoint, clipPoint);
                    final float scalar = radiusA - ((clipPoint.x - planePoint.x) * this.normal.x + (clipPoint.y - planePoint.y) * this.normal.y);
                    final float cAx = this.normal.x * scalar + clipPoint.x;
                    final float cAy = this.normal.y * scalar + clipPoint.y;
                    final float cBx = -this.normal.x * radiusB + clipPoint.x;
                    final float cBy = -this.normal.y * radiusB + clipPoint.y;
                    this.points[i].x = (cAx + cBx) * 0.5f;
                    this.points[i].y = (cAy + cBy) * 0.5f;
                    this.separations[i] = (cBx - cAx) * this.normal.x + (cBy - cAy) * this.normal.y;
                }
                break;
            }
            case FACE_B: {
                final Vec2 planePoint = this.pool3;
                Rot.mulToOutUnsafe(xfB.q, manifold.localNormal, this.normal);
                Transform.mulToOut(xfB, manifold.localPoint, planePoint);
                final Vec2 clipPoint = this.pool4;
                for (int i = 0; i < manifold.pointCount; ++i) {
                    Transform.mulToOut(xfA, manifold.points[i].localPoint, clipPoint);
                    final float scalar = radiusB - ((clipPoint.x - planePoint.x) * this.normal.x + (clipPoint.y - planePoint.y) * this.normal.y);
                    final float cBx2 = this.normal.x * scalar + clipPoint.x;
                    final float cBy2 = this.normal.y * scalar + clipPoint.y;
                    final float cAx2 = -this.normal.x * radiusA + clipPoint.x;
                    final float cAy2 = -this.normal.y * radiusA + clipPoint.y;
                    this.points[i].x = (cAx2 + cBx2) * 0.5f;
                    this.points[i].y = (cAy2 + cBy2) * 0.5f;
                    this.separations[i] = (cAx2 - cBx2) * this.normal.x + (cAy2 - cBy2) * this.normal.y;
                }
                this.normal.x = -this.normal.x;
                this.normal.y = -this.normal.y;
                break;
            }
        }
    }
}
