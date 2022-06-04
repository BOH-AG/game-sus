// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.MathUtils;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Rot;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Settings;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.pooling.IWorldPool;

public class Collision
{
    public static final int NULL_FEATURE = Integer.MAX_VALUE;
    private final IWorldPool pool;
    private final DistanceInput input;
    private final Distance.SimplexCache cache;
    private final DistanceOutput output;
    private static Vec2 d;
    private final Vec2 temp;
    private final Transform xf;
    private final Vec2 n;
    private final Vec2 v1;
    private final EdgeResults results1;
    private final EdgeResults results2;
    private final ClipVertex[] incidentEdge;
    private final Vec2 localTangent;
    private final Vec2 localNormal;
    private final Vec2 planePoint;
    private final Vec2 tangent;
    private final Vec2 v11;
    private final Vec2 v12;
    private final ClipVertex[] clipPoints1;
    private final ClipVertex[] clipPoints2;
    private final Vec2 Q;
    private final Vec2 e;
    private final ContactID cf;
    private final Vec2 e1;
    private final Vec2 P;
    private final EPCollider collider;
    
    public Collision(final IWorldPool argPool) {
        this.input = new DistanceInput();
        this.cache = new Distance.SimplexCache();
        this.output = new DistanceOutput();
        this.temp = new Vec2();
        this.xf = new Transform();
        this.n = new Vec2();
        this.v1 = new Vec2();
        this.results1 = new EdgeResults();
        this.results2 = new EdgeResults();
        this.incidentEdge = new ClipVertex[2];
        this.localTangent = new Vec2();
        this.localNormal = new Vec2();
        this.planePoint = new Vec2();
        this.tangent = new Vec2();
        this.v11 = new Vec2();
        this.v12 = new Vec2();
        this.clipPoints1 = new ClipVertex[2];
        this.clipPoints2 = new ClipVertex[2];
        this.Q = new Vec2();
        this.e = new Vec2();
        this.cf = new ContactID();
        this.e1 = new Vec2();
        this.P = new Vec2();
        this.collider = new EPCollider();
        this.incidentEdge[0] = new ClipVertex();
        this.incidentEdge[1] = new ClipVertex();
        this.clipPoints1[0] = new ClipVertex();
        this.clipPoints1[1] = new ClipVertex();
        this.clipPoints2[0] = new ClipVertex();
        this.clipPoints2[1] = new ClipVertex();
        this.pool = argPool;
    }
    
    public final boolean testOverlap(final Shape shapeA, final int indexA, final Shape shapeB, final int indexB, final Transform xfA, final Transform xfB) {
        this.input.proxyA.set(shapeA, indexA);
        this.input.proxyB.set(shapeB, indexB);
        this.input.transformA.set(xfA);
        this.input.transformB.set(xfB);
        this.input.useRadii = true;
        this.cache.count = 0;
        this.pool.getDistance().distance(this.output, this.cache, this.input);
        return this.output.distance < 1.1920929E-6f;
    }
    
    public static final void getPointStates(final PointState[] state1, final PointState[] state2, final Manifold manifold1, final Manifold manifold2) {
        for (int i = 0; i < Settings.maxManifoldPoints; ++i) {
            state1[i] = PointState.NULL_STATE;
            state2[i] = PointState.NULL_STATE;
        }
        for (int i = 0; i < manifold1.pointCount; ++i) {
            final ContactID id = manifold1.points[i].id;
            state1[i] = PointState.REMOVE_STATE;
            for (int j = 0; j < manifold2.pointCount; ++j) {
                if (manifold2.points[j].id.isEqual(id)) {
                    state1[i] = PointState.PERSIST_STATE;
                    break;
                }
            }
        }
        for (int i = 0; i < manifold2.pointCount; ++i) {
            final ContactID id = manifold2.points[i].id;
            state2[i] = PointState.ADD_STATE;
            for (int j = 0; j < manifold1.pointCount; ++j) {
                if (manifold1.points[j].id.isEqual(id)) {
                    state2[i] = PointState.PERSIST_STATE;
                    break;
                }
            }
        }
    }
    
    public static final int clipSegmentToLine(final ClipVertex[] vOut, final ClipVertex[] vIn, final Vec2 normal, final float offset, final int vertexIndexA) {
        int numOut = 0;
        final ClipVertex vIn2 = vIn[0];
        final ClipVertex vIn3 = vIn[1];
        final Vec2 vIn0v = vIn2.v;
        final Vec2 vIn1v = vIn3.v;
        final float distance0 = Vec2.dot(normal, vIn0v) - offset;
        final float distance2 = Vec2.dot(normal, vIn1v) - offset;
        if (distance0 <= 0.0f) {
            vOut[numOut++].set(vIn2);
        }
        if (distance2 <= 0.0f) {
            vOut[numOut++].set(vIn3);
        }
        if (distance0 * distance2 < 0.0f) {
            final float interp = distance0 / (distance0 - distance2);
            final ClipVertex vOutNO = vOut[numOut];
            vOutNO.v.x = vIn0v.x + interp * (vIn1v.x - vIn0v.x);
            vOutNO.v.y = vIn0v.y + interp * (vIn1v.y - vIn0v.y);
            vOutNO.id.indexA = (byte)vertexIndexA;
            vOutNO.id.indexB = vIn2.id.indexB;
            vOutNO.id.typeA = (byte)ContactID.Type.VERTEX.ordinal();
            vOutNO.id.typeB = (byte)ContactID.Type.FACE.ordinal();
            ++numOut;
        }
        return numOut;
    }
    
    public final void collideCircles(final Manifold manifold, final CircleShape circle1, final Transform xfA, final CircleShape circle2, final Transform xfB) {
        manifold.pointCount = 0;
        final Vec2 circle1p = circle1.m_p;
        final Vec2 circle2p = circle2.m_p;
        final float pAx = xfA.q.c * circle1p.x - xfA.q.s * circle1p.y + xfA.p.x;
        final float pAy = xfA.q.s * circle1p.x + xfA.q.c * circle1p.y + xfA.p.y;
        final float pBx = xfB.q.c * circle2p.x - xfB.q.s * circle2p.y + xfB.p.x;
        final float pBy = xfB.q.s * circle2p.x + xfB.q.c * circle2p.y + xfB.p.y;
        final float dx = pBx - pAx;
        final float dy = pBy - pAy;
        final float distSqr = dx * dx + dy * dy;
        final float radius = circle1.m_radius + circle2.m_radius;
        if (distSqr > radius * radius) {
            return;
        }
        manifold.type = Manifold.ManifoldType.CIRCLES;
        manifold.localPoint.set(circle1p);
        manifold.localNormal.setZero();
        manifold.pointCount = 1;
        manifold.points[0].localPoint.set(circle2p);
        manifold.points[0].id.zero();
    }
    
    public final void collidePolygonAndCircle(final Manifold manifold, final PolygonShape polygon, final Transform xfA, final CircleShape circle, final Transform xfB) {
        manifold.pointCount = 0;
        final Vec2 circlep = circle.m_p;
        final Rot xfBq = xfB.q;
        final Rot xfAq = xfA.q;
        final float cx = xfBq.c * circlep.x - xfBq.s * circlep.y + xfB.p.x;
        final float cy = xfBq.s * circlep.x + xfBq.c * circlep.y + xfB.p.y;
        final float px = cx - xfA.p.x;
        final float py = cy - xfA.p.y;
        final float cLocalx = xfAq.c * px + xfAq.s * py;
        final float cLocaly = -xfAq.s * px + xfAq.c * py;
        int normalIndex = 0;
        float separation = -3.4028235E38f;
        final float radius = polygon.m_radius + circle.m_radius;
        final int vertexCount = polygon.m_count;
        final Vec2[] vertices = polygon.m_vertices;
        final Vec2[] normals = polygon.m_normals;
        for (int i = 0; i < vertexCount; ++i) {
            final Vec2 vertex = vertices[i];
            final float tempx = cLocalx - vertex.x;
            final float tempy = cLocaly - vertex.y;
            final float s = normals[i].x * tempx + normals[i].y * tempy;
            if (s > radius) {
                return;
            }
            if (s > separation) {
                separation = s;
                normalIndex = i;
            }
        }
        final int vertIndex1 = normalIndex;
        final int vertIndex2 = (vertIndex1 + 1 < vertexCount) ? (vertIndex1 + 1) : 0;
        final Vec2 v1 = vertices[vertIndex1];
        final Vec2 v2 = vertices[vertIndex2];
        if (separation < 1.1920929E-7f) {
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            final Vec2 normal = normals[normalIndex];
            manifold.localNormal.x = normal.x;
            manifold.localNormal.y = normal.y;
            manifold.localPoint.x = (v1.x + v2.x) * 0.5f;
            manifold.localPoint.y = (v1.y + v2.y) * 0.5f;
            final ManifoldPoint mpoint = manifold.points[0];
            mpoint.localPoint.x = circlep.x;
            mpoint.localPoint.y = circlep.y;
            mpoint.id.zero();
            return;
        }
        final float tempX = cLocalx - v1.x;
        final float tempY = cLocaly - v1.y;
        final float temp2X = v2.x - v1.x;
        final float temp2Y = v2.y - v1.y;
        final float u1 = tempX * temp2X + tempY * temp2Y;
        final float temp3X = cLocalx - v2.x;
        final float temp3Y = cLocaly - v2.y;
        final float temp4X = v1.x - v2.x;
        final float temp4Y = v1.y - v2.y;
        final float u2 = temp3X * temp4X + temp3Y * temp4Y;
        if (u1 <= 0.0f) {
            final float dx = cLocalx - v1.x;
            final float dy = cLocaly - v1.y;
            if (dx * dx + dy * dy > radius * radius) {
                return;
            }
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.x = cLocalx - v1.x;
            manifold.localNormal.y = cLocaly - v1.y;
            manifold.localNormal.normalize();
            manifold.localPoint.set(v1);
            manifold.points[0].localPoint.set(circlep);
            manifold.points[0].id.zero();
        }
        else if (u2 <= 0.0f) {
            final float dx = cLocalx - v2.x;
            final float dy = cLocaly - v2.y;
            if (dx * dx + dy * dy > radius * radius) {
                return;
            }
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.x = cLocalx - v2.x;
            manifold.localNormal.y = cLocaly - v2.y;
            manifold.localNormal.normalize();
            manifold.localPoint.set(v2);
            manifold.points[0].localPoint.set(circlep);
            manifold.points[0].id.zero();
        }
        else {
            final float fcx = (v1.x + v2.x) * 0.5f;
            final float fcy = (v1.y + v2.y) * 0.5f;
            final float tx = cLocalx - fcx;
            final float ty = cLocaly - fcy;
            final Vec2 normal2 = normals[vertIndex1];
            separation = tx * normal2.x + ty * normal2.y;
            if (separation > radius) {
                return;
            }
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.set(normals[vertIndex1]);
            manifold.localPoint.x = fcx;
            manifold.localPoint.y = fcy;
            manifold.points[0].localPoint.set(circlep);
            manifold.points[0].id.zero();
        }
    }
    
    public final void findMaxSeparation(final EdgeResults results, final PolygonShape poly1, final Transform xf1, final PolygonShape poly2, final Transform xf2) {
        final int count1 = poly1.m_count;
        final int count2 = poly2.m_count;
        final Vec2[] n1s = poly1.m_normals;
        final Vec2[] v1s = poly1.m_vertices;
        final Vec2[] v2s = poly2.m_vertices;
        Transform.mulTransToOutUnsafe(xf2, xf1, this.xf);
        final Rot xfq = this.xf.q;
        int bestIndex = 0;
        float maxSeparation = -3.4028235E38f;
        for (int i = 0; i < count1; ++i) {
            Rot.mulToOutUnsafe(xfq, n1s[i], this.n);
            Transform.mulToOutUnsafe(this.xf, v1s[i], this.v1);
            float si = Float.MAX_VALUE;
            for (final Vec2 v2sj : v2s) {
                final float sij = this.n.x * (v2sj.x - this.v1.x) + this.n.y * (v2sj.y - this.v1.y);
                if (sij < si) {
                    si = sij;
                }
            }
            if (si > maxSeparation) {
                maxSeparation = si;
                bestIndex = i;
            }
        }
        results.edgeIndex = bestIndex;
        results.separation = maxSeparation;
    }
    
    public final void findIncidentEdge(final ClipVertex[] c, final PolygonShape poly1, final Transform xf1, final int edge1, final PolygonShape poly2, final Transform xf2) {
        final int count1 = poly1.m_count;
        final Vec2[] normals1 = poly1.m_normals;
        final int count2 = poly2.m_count;
        final Vec2[] vertices2 = poly2.m_vertices;
        final Vec2[] normals2 = poly2.m_normals;
        assert 0 <= edge1 && edge1 < count1;
        final ClipVertex c2 = c[0];
        final ClipVertex c3 = c[1];
        final Rot xf1q = xf1.q;
        final Rot xf2q = xf2.q;
        final Vec2 v = normals1[edge1];
        final float tempx = xf1q.c * v.x - xf1q.s * v.y;
        final float tempy = xf1q.s * v.x + xf1q.c * v.y;
        final float normal1x = xf2q.c * tempx + xf2q.s * tempy;
        final float normal1y = -xf2q.s * tempx + xf2q.c * tempy;
        int index = 0;
        float minDot = Float.MAX_VALUE;
        for (int i = 0; i < count2; ++i) {
            final Vec2 b = normals2[i];
            final float dot = normal1x * b.x + normal1y * b.y;
            if (dot < minDot) {
                minDot = dot;
                index = i;
            }
        }
        final int i2 = index;
        final int i3 = (i2 + 1 < count2) ? (i2 + 1) : 0;
        final Vec2 v2 = vertices2[i2];
        final Vec2 out = c2.v;
        out.x = xf2q.c * v2.x - xf2q.s * v2.y + xf2.p.x;
        out.y = xf2q.s * v2.x + xf2q.c * v2.y + xf2.p.y;
        c2.id.indexA = (byte)edge1;
        c2.id.indexB = (byte)i2;
        c2.id.typeA = (byte)ContactID.Type.FACE.ordinal();
        c2.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
        final Vec2 v3 = vertices2[i3];
        final Vec2 out2 = c3.v;
        out2.x = xf2q.c * v3.x - xf2q.s * v3.y + xf2.p.x;
        out2.y = xf2q.s * v3.x + xf2q.c * v3.y + xf2.p.y;
        c3.id.indexA = (byte)edge1;
        c3.id.indexB = (byte)i3;
        c3.id.typeA = (byte)ContactID.Type.FACE.ordinal();
        c3.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
    }
    
    public final void collidePolygons(final Manifold manifold, final PolygonShape polyA, final Transform xfA, final PolygonShape polyB, final Transform xfB) {
        manifold.pointCount = 0;
        final float totalRadius = polyA.m_radius + polyB.m_radius;
        this.findMaxSeparation(this.results1, polyA, xfA, polyB, xfB);
        if (this.results1.separation > totalRadius) {
            return;
        }
        this.findMaxSeparation(this.results2, polyB, xfB, polyA, xfA);
        if (this.results2.separation > totalRadius) {
            return;
        }
        final float k_tol = 0.1f * Settings.linearSlop;
        PolygonShape poly1;
        PolygonShape poly2;
        Transform xf1;
        Transform xf2;
        int edge1;
        boolean flip;
        if (this.results2.separation > this.results1.separation + k_tol) {
            poly1 = polyB;
            poly2 = polyA;
            xf1 = xfB;
            xf2 = xfA;
            edge1 = this.results2.edgeIndex;
            manifold.type = Manifold.ManifoldType.FACE_B;
            flip = true;
        }
        else {
            poly1 = polyA;
            poly2 = polyB;
            xf1 = xfA;
            xf2 = xfB;
            edge1 = this.results1.edgeIndex;
            manifold.type = Manifold.ManifoldType.FACE_A;
            flip = false;
        }
        final Rot xf1q = xf1.q;
        this.findIncidentEdge(this.incidentEdge, poly1, xf1, edge1, poly2, xf2);
        final int count1 = poly1.m_count;
        final Vec2[] vertices1 = poly1.m_vertices;
        final int iv1 = edge1;
        final int iv2 = (edge1 + 1 < count1) ? (edge1 + 1) : 0;
        this.v11.set(vertices1[iv1]);
        this.v12.set(vertices1[iv2]);
        this.localTangent.x = this.v12.x - this.v11.x;
        this.localTangent.y = this.v12.y - this.v11.y;
        this.localTangent.normalize();
        this.localNormal.x = 1.0f * this.localTangent.y;
        this.localNormal.y = -1.0f * this.localTangent.x;
        this.planePoint.x = (this.v11.x + this.v12.x) * 0.5f;
        this.planePoint.y = (this.v11.y + this.v12.y) * 0.5f;
        this.tangent.x = xf1q.c * this.localTangent.x - xf1q.s * this.localTangent.y;
        this.tangent.y = xf1q.s * this.localTangent.x + xf1q.c * this.localTangent.y;
        final float normalx = 1.0f * this.tangent.y;
        final float normaly = -1.0f * this.tangent.x;
        Transform.mulToOut(xf1, this.v11, this.v11);
        Transform.mulToOut(xf1, this.v12, this.v12);
        final float frontOffset = normalx * this.v11.x + normaly * this.v11.y;
        final float sideOffset1 = -(this.tangent.x * this.v11.x + this.tangent.y * this.v11.y) + totalRadius;
        final float sideOffset2 = this.tangent.x * this.v12.x + this.tangent.y * this.v12.y + totalRadius;
        this.tangent.negateLocal();
        int np = clipSegmentToLine(this.clipPoints1, this.incidentEdge, this.tangent, sideOffset1, iv1);
        this.tangent.negateLocal();
        if (np < 2) {
            return;
        }
        np = clipSegmentToLine(this.clipPoints2, this.clipPoints1, this.tangent, sideOffset2, iv2);
        if (np < 2) {
            return;
        }
        manifold.localNormal.set(this.localNormal);
        manifold.localPoint.set(this.planePoint);
        int pointCount = 0;
        for (int i = 0; i < Settings.maxManifoldPoints; ++i) {
            final float separation = normalx * this.clipPoints2[i].v.x + normaly * this.clipPoints2[i].v.y - frontOffset;
            if (separation <= totalRadius) {
                final ManifoldPoint cp = manifold.points[pointCount];
                final Vec2 out = cp.localPoint;
                final float px = this.clipPoints2[i].v.x - xf2.p.x;
                final float py = this.clipPoints2[i].v.y - xf2.p.y;
                out.x = xf2.q.c * px + xf2.q.s * py;
                out.y = -xf2.q.s * px + xf2.q.c * py;
                cp.id.set(this.clipPoints2[i].id);
                if (flip) {
                    cp.id.flip();
                }
                ++pointCount;
            }
        }
        manifold.pointCount = pointCount;
    }
    
    public void collideEdgeAndCircle(final Manifold manifold, final EdgeShape edgeA, final Transform xfA, final CircleShape circleB, final Transform xfB) {
        manifold.pointCount = 0;
        Transform.mulToOutUnsafe(xfB, circleB.m_p, this.temp);
        Transform.mulTransToOutUnsafe(xfA, this.temp, this.Q);
        final Vec2 A = edgeA.m_vertex1;
        final Vec2 B = edgeA.m_vertex2;
        this.e.set(B).subLocal(A);
        final float u = Vec2.dot(this.e, this.temp.set(B).subLocal(this.Q));
        final float v = Vec2.dot(this.e, this.temp.set(this.Q).subLocal(A));
        final float radius = edgeA.m_radius + circleB.m_radius;
        this.cf.indexB = 0;
        this.cf.typeB = (byte)ContactID.Type.VERTEX.ordinal();
        if (v <= 0.0f) {
            final Vec2 P = A;
            Collision.d.set(this.Q).subLocal(P);
            final float dd = Vec2.dot(Collision.d, Collision.d);
            if (dd > radius * radius) {
                return;
            }
            if (edgeA.m_hasVertex0) {
                final Vec2 A2 = edgeA.m_vertex0;
                final Vec2 B2 = A;
                this.e1.set(B2).subLocal(A2);
                final float u2 = Vec2.dot(this.e1, this.temp.set(B2).subLocal(this.Q));
                if (u2 > 0.0f) {
                    return;
                }
            }
            this.cf.indexA = 0;
            this.cf.typeA = (byte)ContactID.Type.VERTEX.ordinal();
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.CIRCLES;
            manifold.localNormal.setZero();
            manifold.localPoint.set(P);
            manifold.points[0].id.set(this.cf);
            manifold.points[0].localPoint.set(circleB.m_p);
        }
        else if (u <= 0.0f) {
            final Vec2 P = B;
            Collision.d.set(this.Q).subLocal(P);
            final float dd = Vec2.dot(Collision.d, Collision.d);
            if (dd > radius * radius) {
                return;
            }
            if (edgeA.m_hasVertex3) {
                final Vec2 B3 = edgeA.m_vertex3;
                final Vec2 A3 = B;
                final Vec2 e2 = this.e1;
                e2.set(B3).subLocal(A3);
                final float v2 = Vec2.dot(e2, this.temp.set(this.Q).subLocal(A3));
                if (v2 > 0.0f) {
                    return;
                }
            }
            this.cf.indexA = 1;
            this.cf.typeA = (byte)ContactID.Type.VERTEX.ordinal();
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.CIRCLES;
            manifold.localNormal.setZero();
            manifold.localPoint.set(P);
            manifold.points[0].id.set(this.cf);
            manifold.points[0].localPoint.set(circleB.m_p);
        }
        else {
            final float den = Vec2.dot(this.e, this.e);
            assert den > 0.0f;
            this.P.set(A).mulLocal(u).addLocal(this.temp.set(B).mulLocal(v));
            this.P.mulLocal(1.0f / den);
            Collision.d.set(this.Q).subLocal(this.P);
            final float dd = Vec2.dot(Collision.d, Collision.d);
            if (dd > radius * radius) {
                return;
            }
            this.n.x = -this.e.y;
            this.n.y = this.e.x;
            if (Vec2.dot(this.n, this.temp.set(this.Q).subLocal(A)) < 0.0f) {
                this.n.set(-this.n.x, -this.n.y);
            }
            this.n.normalize();
            this.cf.indexA = 0;
            this.cf.typeA = (byte)ContactID.Type.FACE.ordinal();
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.set(this.n);
            manifold.localPoint.set(A);
            manifold.points[0].id.set(this.cf);
            manifold.points[0].localPoint.set(circleB.m_p);
        }
    }
    
    public void collideEdgeAndPolygon(final Manifold manifold, final EdgeShape edgeA, final Transform xfA, final PolygonShape polygonB, final Transform xfB) {
        this.collider.collide(manifold, edgeA, xfA, polygonB, xfB);
    }
    
    static {
        Collision.d = new Vec2();
    }
    
    private static class EdgeResults
    {
        public float separation;
        public int edgeIndex;
    }
    
    public static class ClipVertex
    {
        public final Vec2 v;
        public final ContactID id;
        
        public ClipVertex() {
            this.v = new Vec2();
            this.id = new ContactID();
        }
        
        public void set(final ClipVertex cv) {
            final Vec2 v1 = cv.v;
            this.v.x = v1.x;
            this.v.y = v1.y;
            final ContactID c = cv.id;
            this.id.indexA = c.indexA;
            this.id.indexB = c.indexB;
            this.id.typeA = c.typeA;
            this.id.typeB = c.typeB;
        }
    }
    
    public enum PointState
    {
        NULL_STATE, 
        ADD_STATE, 
        PERSIST_STATE, 
        REMOVE_STATE;
    }
    
    static class EPAxis
    {
        Type type;
        int index;
        float separation;
        
        enum Type
        {
            UNKNOWN, 
            EDGE_A, 
            EDGE_B;
        }
    }
    
    static class TempPolygon
    {
        final Vec2[] vertices;
        final Vec2[] normals;
        int count;
        
        public TempPolygon() {
            this.vertices = new Vec2[Settings.maxPolygonVertices];
            this.normals = new Vec2[Settings.maxPolygonVertices];
            for (int i = 0; i < this.vertices.length; ++i) {
                this.vertices[i] = new Vec2();
                this.normals[i] = new Vec2();
            }
        }
    }
    
    static class ReferenceFace
    {
        int i1;
        int i2;
        final Vec2 v1;
        final Vec2 v2;
        final Vec2 normal;
        final Vec2 sideNormal1;
        float sideOffset1;
        final Vec2 sideNormal2;
        float sideOffset2;
        
        ReferenceFace() {
            this.v1 = new Vec2();
            this.v2 = new Vec2();
            this.normal = new Vec2();
            this.sideNormal1 = new Vec2();
            this.sideNormal2 = new Vec2();
        }
    }
    
    static class EPCollider
    {
        final TempPolygon m_polygonB;
        final Transform m_xf;
        final Vec2 m_centroidB;
        Vec2 m_v0;
        Vec2 m_v1;
        Vec2 m_v2;
        Vec2 m_v3;
        final Vec2 m_normal0;
        final Vec2 m_normal1;
        final Vec2 m_normal2;
        final Vec2 m_normal;
        VertexType m_type1;
        VertexType m_type2;
        final Vec2 m_lowerLimit;
        final Vec2 m_upperLimit;
        float m_radius;
        boolean m_front;
        private final Vec2 edge1;
        private final Vec2 temp;
        private final Vec2 edge0;
        private final Vec2 edge2;
        private final ClipVertex[] ie;
        private final ClipVertex[] clipPoints1;
        private final ClipVertex[] clipPoints2;
        private final ReferenceFace rf;
        private final EPAxis edgeAxis;
        private final EPAxis polygonAxis;
        private final Vec2 perp;
        private final Vec2 n;
        
        public EPCollider() {
            this.m_polygonB = new TempPolygon();
            this.m_xf = new Transform();
            this.m_centroidB = new Vec2();
            this.m_v0 = new Vec2();
            this.m_v1 = new Vec2();
            this.m_v2 = new Vec2();
            this.m_v3 = new Vec2();
            this.m_normal0 = new Vec2();
            this.m_normal1 = new Vec2();
            this.m_normal2 = new Vec2();
            this.m_normal = new Vec2();
            this.m_lowerLimit = new Vec2();
            this.m_upperLimit = new Vec2();
            this.edge1 = new Vec2();
            this.temp = new Vec2();
            this.edge0 = new Vec2();
            this.edge2 = new Vec2();
            this.ie = new ClipVertex[2];
            this.clipPoints1 = new ClipVertex[2];
            this.clipPoints2 = new ClipVertex[2];
            this.rf = new ReferenceFace();
            this.edgeAxis = new EPAxis();
            this.polygonAxis = new EPAxis();
            this.perp = new Vec2();
            this.n = new Vec2();
            for (int i = 0; i < 2; ++i) {
                this.ie[i] = new ClipVertex();
                this.clipPoints1[i] = new ClipVertex();
                this.clipPoints2[i] = new ClipVertex();
            }
        }
        
        public void collide(final Manifold manifold, final EdgeShape edgeA, final Transform xfA, final PolygonShape polygonB, final Transform xfB) {
            Transform.mulTransToOutUnsafe(xfA, xfB, this.m_xf);
            Transform.mulToOutUnsafe(this.m_xf, polygonB.m_centroid, this.m_centroidB);
            this.m_v0 = edgeA.m_vertex0;
            this.m_v1 = edgeA.m_vertex1;
            this.m_v2 = edgeA.m_vertex2;
            this.m_v3 = edgeA.m_vertex3;
            final boolean hasVertex0 = edgeA.m_hasVertex0;
            final boolean hasVertex2 = edgeA.m_hasVertex3;
            this.edge1.set(this.m_v2).subLocal(this.m_v1);
            this.edge1.normalize();
            this.m_normal1.set(this.edge1.y, -this.edge1.x);
            final float offset1 = Vec2.dot(this.m_normal1, this.temp.set(this.m_centroidB).subLocal(this.m_v1));
            float offset2 = 0.0f;
            float offset3 = 0.0f;
            boolean convex1 = false;
            boolean convex2 = false;
            if (hasVertex0) {
                this.edge0.set(this.m_v1).subLocal(this.m_v0);
                this.edge0.normalize();
                this.m_normal0.set(this.edge0.y, -this.edge0.x);
                convex1 = (Vec2.cross(this.edge0, this.edge1) >= 0.0f);
                offset2 = Vec2.dot(this.m_normal0, this.temp.set(this.m_centroidB).subLocal(this.m_v0));
            }
            if (hasVertex2) {
                this.edge2.set(this.m_v3).subLocal(this.m_v2);
                this.edge2.normalize();
                this.m_normal2.set(this.edge2.y, -this.edge2.x);
                convex2 = (Vec2.cross(this.edge1, this.edge2) > 0.0f);
                offset3 = Vec2.dot(this.m_normal2, this.temp.set(this.m_centroidB).subLocal(this.m_v2));
            }
            if (hasVertex0 && hasVertex2) {
                if (convex1 && convex2) {
                    this.m_front = (offset2 >= 0.0f || offset1 >= 0.0f || offset3 >= 0.0f);
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal0.x;
                        this.m_lowerLimit.y = this.m_normal0.y;
                        this.m_upperLimit.x = this.m_normal2.x;
                        this.m_upperLimit.y = this.m_normal2.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal1.x;
                        this.m_lowerLimit.y = -this.m_normal1.y;
                        this.m_upperLimit.x = -this.m_normal1.x;
                        this.m_upperLimit.y = -this.m_normal1.y;
                    }
                }
                else if (convex1) {
                    this.m_front = (offset2 >= 0.0f || (offset1 >= 0.0f && offset3 >= 0.0f));
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal0.x;
                        this.m_lowerLimit.y = this.m_normal0.y;
                        this.m_upperLimit.x = this.m_normal1.x;
                        this.m_upperLimit.y = this.m_normal1.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal2.x;
                        this.m_lowerLimit.y = -this.m_normal2.y;
                        this.m_upperLimit.x = -this.m_normal1.x;
                        this.m_upperLimit.y = -this.m_normal1.y;
                    }
                }
                else if (convex2) {
                    this.m_front = (offset3 >= 0.0f || (offset2 >= 0.0f && offset1 >= 0.0f));
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal1.x;
                        this.m_lowerLimit.y = this.m_normal1.y;
                        this.m_upperLimit.x = this.m_normal2.x;
                        this.m_upperLimit.y = this.m_normal2.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal1.x;
                        this.m_lowerLimit.y = -this.m_normal1.y;
                        this.m_upperLimit.x = -this.m_normal0.x;
                        this.m_upperLimit.y = -this.m_normal0.y;
                    }
                }
                else {
                    this.m_front = (offset2 >= 0.0f && offset1 >= 0.0f && offset3 >= 0.0f);
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal1.x;
                        this.m_lowerLimit.y = this.m_normal1.y;
                        this.m_upperLimit.x = this.m_normal1.x;
                        this.m_upperLimit.y = this.m_normal1.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal2.x;
                        this.m_lowerLimit.y = -this.m_normal2.y;
                        this.m_upperLimit.x = -this.m_normal0.x;
                        this.m_upperLimit.y = -this.m_normal0.y;
                    }
                }
            }
            else if (hasVertex0) {
                if (convex1) {
                    this.m_front = (offset2 >= 0.0f || offset1 >= 0.0f);
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal0.x;
                        this.m_lowerLimit.y = this.m_normal0.y;
                        this.m_upperLimit.x = -this.m_normal1.x;
                        this.m_upperLimit.y = -this.m_normal1.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal1.x;
                        this.m_lowerLimit.y = this.m_normal1.y;
                        this.m_upperLimit.x = -this.m_normal1.x;
                        this.m_upperLimit.y = -this.m_normal1.y;
                    }
                }
                else {
                    this.m_front = (offset2 >= 0.0f && offset1 >= 0.0f);
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal1.x;
                        this.m_lowerLimit.y = this.m_normal1.y;
                        this.m_upperLimit.x = -this.m_normal1.x;
                        this.m_upperLimit.y = -this.m_normal1.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = this.m_normal1.x;
                        this.m_lowerLimit.y = this.m_normal1.y;
                        this.m_upperLimit.x = -this.m_normal0.x;
                        this.m_upperLimit.y = -this.m_normal0.y;
                    }
                }
            }
            else if (hasVertex2) {
                if (convex2) {
                    this.m_front = (offset1 >= 0.0f || offset3 >= 0.0f);
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal1.x;
                        this.m_lowerLimit.y = -this.m_normal1.y;
                        this.m_upperLimit.x = this.m_normal2.x;
                        this.m_upperLimit.y = this.m_normal2.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal1.x;
                        this.m_lowerLimit.y = -this.m_normal1.y;
                        this.m_upperLimit.x = this.m_normal1.x;
                        this.m_upperLimit.y = this.m_normal1.y;
                    }
                }
                else {
                    this.m_front = (offset1 >= 0.0f && offset3 >= 0.0f);
                    if (this.m_front) {
                        this.m_normal.x = this.m_normal1.x;
                        this.m_normal.y = this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal1.x;
                        this.m_lowerLimit.y = -this.m_normal1.y;
                        this.m_upperLimit.x = this.m_normal1.x;
                        this.m_upperLimit.y = this.m_normal1.y;
                    }
                    else {
                        this.m_normal.x = -this.m_normal1.x;
                        this.m_normal.y = -this.m_normal1.y;
                        this.m_lowerLimit.x = -this.m_normal2.x;
                        this.m_lowerLimit.y = -this.m_normal2.y;
                        this.m_upperLimit.x = this.m_normal1.x;
                        this.m_upperLimit.y = this.m_normal1.y;
                    }
                }
            }
            else {
                this.m_front = (offset1 >= 0.0f);
                if (this.m_front) {
                    this.m_normal.x = this.m_normal1.x;
                    this.m_normal.y = this.m_normal1.y;
                    this.m_lowerLimit.x = -this.m_normal1.x;
                    this.m_lowerLimit.y = -this.m_normal1.y;
                    this.m_upperLimit.x = -this.m_normal1.x;
                    this.m_upperLimit.y = -this.m_normal1.y;
                }
                else {
                    this.m_normal.x = -this.m_normal1.x;
                    this.m_normal.y = -this.m_normal1.y;
                    this.m_lowerLimit.x = this.m_normal1.x;
                    this.m_lowerLimit.y = this.m_normal1.y;
                    this.m_upperLimit.x = this.m_normal1.x;
                    this.m_upperLimit.y = this.m_normal1.y;
                }
            }
            this.m_polygonB.count = polygonB.m_count;
            for (int i = 0; i < polygonB.m_count; ++i) {
                Transform.mulToOutUnsafe(this.m_xf, polygonB.m_vertices[i], this.m_polygonB.vertices[i]);
                Rot.mulToOutUnsafe(this.m_xf.q, polygonB.m_normals[i], this.m_polygonB.normals[i]);
            }
            this.m_radius = 2.0f * Settings.polygonRadius;
            manifold.pointCount = 0;
            this.computeEdgeSeparation(this.edgeAxis);
            if (this.edgeAxis.type == EPAxis.Type.UNKNOWN) {
                return;
            }
            if (this.edgeAxis.separation > this.m_radius) {
                return;
            }
            this.computePolygonSeparation(this.polygonAxis);
            if (this.polygonAxis.type != EPAxis.Type.UNKNOWN && this.polygonAxis.separation > this.m_radius) {
                return;
            }
            final float k_relativeTol = 0.98f;
            final float k_absoluteTol = 0.001f;
            EPAxis primaryAxis;
            if (this.polygonAxis.type == EPAxis.Type.UNKNOWN) {
                primaryAxis = this.edgeAxis;
            }
            else if (this.polygonAxis.separation > 0.98f * this.edgeAxis.separation + 0.001f) {
                primaryAxis = this.polygonAxis;
            }
            else {
                primaryAxis = this.edgeAxis;
            }
            final ClipVertex ie0 = this.ie[0];
            final ClipVertex ie2 = this.ie[1];
            if (primaryAxis.type == EPAxis.Type.EDGE_A) {
                manifold.type = Manifold.ManifoldType.FACE_A;
                int bestIndex = 0;
                float bestValue = Vec2.dot(this.m_normal, this.m_polygonB.normals[0]);
                for (int j = 1; j < this.m_polygonB.count; ++j) {
                    final float value = Vec2.dot(this.m_normal, this.m_polygonB.normals[j]);
                    if (value < bestValue) {
                        bestValue = value;
                        bestIndex = j;
                    }
                }
                final int i2 = bestIndex;
                final int i3 = (i2 + 1 < this.m_polygonB.count) ? (i2 + 1) : 0;
                ie0.v.set(this.m_polygonB.vertices[i2]);
                ie0.id.indexA = 0;
                ie0.id.indexB = (byte)i2;
                ie0.id.typeA = (byte)ContactID.Type.FACE.ordinal();
                ie0.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
                ie2.v.set(this.m_polygonB.vertices[i3]);
                ie2.id.indexA = 0;
                ie2.id.indexB = (byte)i3;
                ie2.id.typeA = (byte)ContactID.Type.FACE.ordinal();
                ie2.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
                if (this.m_front) {
                    this.rf.i1 = 0;
                    this.rf.i2 = 1;
                    this.rf.v1.set(this.m_v1);
                    this.rf.v2.set(this.m_v2);
                    this.rf.normal.set(this.m_normal1);
                }
                else {
                    this.rf.i1 = 1;
                    this.rf.i2 = 0;
                    this.rf.v1.set(this.m_v2);
                    this.rf.v2.set(this.m_v1);
                    this.rf.normal.set(this.m_normal1).negateLocal();
                }
            }
            else {
                manifold.type = Manifold.ManifoldType.FACE_B;
                ie0.v.set(this.m_v1);
                ie0.id.indexA = 0;
                ie0.id.indexB = (byte)primaryAxis.index;
                ie0.id.typeA = (byte)ContactID.Type.VERTEX.ordinal();
                ie0.id.typeB = (byte)ContactID.Type.FACE.ordinal();
                ie2.v.set(this.m_v2);
                ie2.id.indexA = 0;
                ie2.id.indexB = (byte)primaryAxis.index;
                ie2.id.typeA = (byte)ContactID.Type.VERTEX.ordinal();
                ie2.id.typeB = (byte)ContactID.Type.FACE.ordinal();
                this.rf.i1 = primaryAxis.index;
                this.rf.i2 = ((this.rf.i1 + 1 < this.m_polygonB.count) ? (this.rf.i1 + 1) : 0);
                this.rf.v1.set(this.m_polygonB.vertices[this.rf.i1]);
                this.rf.v2.set(this.m_polygonB.vertices[this.rf.i2]);
                this.rf.normal.set(this.m_polygonB.normals[this.rf.i1]);
            }
            this.rf.sideNormal1.set(this.rf.normal.y, -this.rf.normal.x);
            this.rf.sideNormal2.set(this.rf.sideNormal1).negateLocal();
            this.rf.sideOffset1 = Vec2.dot(this.rf.sideNormal1, this.rf.v1);
            this.rf.sideOffset2 = Vec2.dot(this.rf.sideNormal2, this.rf.v2);
            int np = Collision.clipSegmentToLine(this.clipPoints1, this.ie, this.rf.sideNormal1, this.rf.sideOffset1, this.rf.i1);
            if (np < Settings.maxManifoldPoints) {
                return;
            }
            np = Collision.clipSegmentToLine(this.clipPoints2, this.clipPoints1, this.rf.sideNormal2, this.rf.sideOffset2, this.rf.i2);
            if (np < Settings.maxManifoldPoints) {
                return;
            }
            if (primaryAxis.type == EPAxis.Type.EDGE_A) {
                manifold.localNormal.set(this.rf.normal);
                manifold.localPoint.set(this.rf.v1);
            }
            else {
                manifold.localNormal.set(polygonB.m_normals[this.rf.i1]);
                manifold.localPoint.set(polygonB.m_vertices[this.rf.i1]);
            }
            int pointCount = 0;
            for (int j = 0; j < Settings.maxManifoldPoints; ++j) {
                final float separation = Vec2.dot(this.rf.normal, this.temp.set(this.clipPoints2[j].v).subLocal(this.rf.v1));
                if (separation <= this.m_radius) {
                    final ManifoldPoint cp = manifold.points[pointCount];
                    if (primaryAxis.type == EPAxis.Type.EDGE_A) {
                        Transform.mulTransToOutUnsafe(this.m_xf, this.clipPoints2[j].v, cp.localPoint);
                        cp.id.set(this.clipPoints2[j].id);
                    }
                    else {
                        cp.localPoint.set(this.clipPoints2[j].v);
                        cp.id.typeA = this.clipPoints2[j].id.typeB;
                        cp.id.typeB = this.clipPoints2[j].id.typeA;
                        cp.id.indexA = this.clipPoints2[j].id.indexB;
                        cp.id.indexB = this.clipPoints2[j].id.indexA;
                    }
                    ++pointCount;
                }
            }
            manifold.pointCount = pointCount;
        }
        
        public void computeEdgeSeparation(final EPAxis axis) {
            axis.type = EPAxis.Type.EDGE_A;
            axis.index = (this.m_front ? 0 : 1);
            axis.separation = Float.MAX_VALUE;
            final float nx = this.m_normal.x;
            final float ny = this.m_normal.y;
            for (int i = 0; i < this.m_polygonB.count; ++i) {
                final Vec2 v = this.m_polygonB.vertices[i];
                final float tempx = v.x - this.m_v1.x;
                final float tempy = v.y - this.m_v1.y;
                final float s = nx * tempx + ny * tempy;
                if (s < axis.separation) {
                    axis.separation = s;
                }
            }
        }
        
        public void computePolygonSeparation(final EPAxis axis) {
            axis.type = EPAxis.Type.UNKNOWN;
            axis.index = -1;
            axis.separation = -3.4028235E38f;
            this.perp.x = -this.m_normal.y;
            this.perp.y = this.m_normal.x;
            for (int i = 0; i < this.m_polygonB.count; ++i) {
                final Vec2 normalB = this.m_polygonB.normals[i];
                final Vec2 vB = this.m_polygonB.vertices[i];
                this.n.x = -normalB.x;
                this.n.y = -normalB.y;
                float tempx = vB.x - this.m_v1.x;
                float tempy = vB.y - this.m_v1.y;
                final float s1 = this.n.x * tempx + this.n.y * tempy;
                tempx = vB.x - this.m_v2.x;
                tempy = vB.y - this.m_v2.y;
                final float s2 = this.n.x * tempx + this.n.y * tempy;
                final float s3 = MathUtils.min(s1, s2);
                if (s3 > this.m_radius) {
                    axis.type = EPAxis.Type.EDGE_B;
                    axis.index = i;
                    axis.separation = s3;
                    return;
                }
                if (this.n.x * this.perp.x + this.n.y * this.perp.y >= 0.0f) {
                    if (Vec2.dot(this.temp.set(this.n).subLocal(this.m_upperLimit), this.m_normal) < -Settings.angularSlop) {
                        continue;
                    }
                }
                else if (Vec2.dot(this.temp.set(this.n).subLocal(this.m_lowerLimit), this.m_normal) < -Settings.angularSlop) {
                    continue;
                }
                if (s3 > axis.separation) {
                    axis.type = EPAxis.Type.EDGE_B;
                    axis.index = i;
                    axis.separation = s3;
                }
            }
        }
        
        enum VertexType
        {
            ISOLATED, 
            CONCAVE, 
            CONVEX;
        }
    }
}
