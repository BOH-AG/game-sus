// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics.contacts;

import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

class PositionSolverManifold
{
    public final Vec2 normal;
    public final Vec2 point;
    public float separation;
    
    PositionSolverManifold() {
        this.normal = new Vec2();
        this.point = new Vec2();
    }
    
    public void initialize(final ContactPositionConstraint pc, final Transform xfA, final Transform xfB, final int index) {
        assert pc.pointCount > 0;
        final Rot xfAq = xfA.q;
        final Rot xfBq = xfB.q;
        final Vec2 pcLocalPointsI = pc.localPoints[index];
        switch (pc.type) {
            case CIRCLES: {
                final Vec2 plocalPoint = pc.localPoint;
                final Vec2 pLocalPoints0 = pc.localPoints[0];
                final float pointAx = xfAq.c * plocalPoint.x - xfAq.s * plocalPoint.y + xfA.p.x;
                final float pointAy = xfAq.s * plocalPoint.x + xfAq.c * plocalPoint.y + xfA.p.y;
                final float pointBx = xfBq.c * pLocalPoints0.x - xfBq.s * pLocalPoints0.y + xfB.p.x;
                final float pointBy = xfBq.s * pLocalPoints0.x + xfBq.c * pLocalPoints0.y + xfB.p.y;
                this.normal.x = pointBx - pointAx;
                this.normal.y = pointBy - pointAy;
                this.normal.normalize();
                this.point.x = (pointAx + pointBx) * 0.5f;
                this.point.y = (pointAy + pointBy) * 0.5f;
                final float tempx = pointBx - pointAx;
                final float tempy = pointBy - pointAy;
                this.separation = tempx * this.normal.x + tempy * this.normal.y - pc.radiusA - pc.radiusB;
                break;
            }
            case FACE_A: {
                final Vec2 pcLocalNormal = pc.localNormal;
                final Vec2 pcLocalPoint = pc.localPoint;
                this.normal.x = xfAq.c * pcLocalNormal.x - xfAq.s * pcLocalNormal.y;
                this.normal.y = xfAq.s * pcLocalNormal.x + xfAq.c * pcLocalNormal.y;
                final float planePointx = xfAq.c * pcLocalPoint.x - xfAq.s * pcLocalPoint.y + xfA.p.x;
                final float planePointy = xfAq.s * pcLocalPoint.x + xfAq.c * pcLocalPoint.y + xfA.p.y;
                final float clipPointx = xfBq.c * pcLocalPointsI.x - xfBq.s * pcLocalPointsI.y + xfB.p.x;
                final float clipPointy = xfBq.s * pcLocalPointsI.x + xfBq.c * pcLocalPointsI.y + xfB.p.y;
                final float tempx = clipPointx - planePointx;
                final float tempy = clipPointy - planePointy;
                this.separation = tempx * this.normal.x + tempy * this.normal.y - pc.radiusA - pc.radiusB;
                this.point.x = clipPointx;
                this.point.y = clipPointy;
                break;
            }
            case FACE_B: {
                final Vec2 pcLocalNormal = pc.localNormal;
                final Vec2 pcLocalPoint = pc.localPoint;
                this.normal.x = xfBq.c * pcLocalNormal.x - xfBq.s * pcLocalNormal.y;
                this.normal.y = xfBq.s * pcLocalNormal.x + xfBq.c * pcLocalNormal.y;
                final float planePointx = xfBq.c * pcLocalPoint.x - xfBq.s * pcLocalPoint.y + xfB.p.x;
                final float planePointy = xfBq.s * pcLocalPoint.x + xfBq.c * pcLocalPoint.y + xfB.p.y;
                final float clipPointx = xfAq.c * pcLocalPointsI.x - xfAq.s * pcLocalPointsI.y + xfA.p.x;
                final float clipPointy = xfAq.s * pcLocalPointsI.x + xfAq.c * pcLocalPointsI.y + xfA.p.y;
                final float tempx = clipPointx - planePointx;
                final float tempy = clipPointy - planePointy;
                this.separation = tempx * this.normal.x + tempy * this.normal.y - pc.radiusA - pc.radiusB;
                this.point.x = clipPointx;
                this.point.y = clipPointy;
                final Vec2 normal = this.normal;
                normal.x *= -1.0f;
                final Vec2 normal2 = this.normal;
                normal2.y *= -1.0f;
                break;
            }
        }
    }
}
