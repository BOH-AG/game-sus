// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.shapes;

import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Rot;
import org.jbox2d.common.MathUtils;
import org.jbox2d.pooling.arrays.IntArray;
import org.jbox2d.pooling.arrays.Vec2Array;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

public class PolygonShape extends Shape
{
    private static final boolean m_debug = false;
    public final Vec2 m_centroid;
    public final Vec2[] m_vertices;
    public final Vec2[] m_normals;
    public int m_count;
    private final Vec2 pool1;
    private final Vec2 pool2;
    private final Vec2 pool3;
    private final Vec2 pool4;
    private Transform poolt1;
    
    public PolygonShape() {
        super(ShapeType.POLYGON);
        this.m_centroid = new Vec2();
        this.pool1 = new Vec2();
        this.pool2 = new Vec2();
        this.pool3 = new Vec2();
        this.pool4 = new Vec2();
        this.poolt1 = new Transform();
        this.m_count = 0;
        this.m_vertices = new Vec2[Settings.maxPolygonVertices];
        for (int i = 0; i < this.m_vertices.length; ++i) {
            this.m_vertices[i] = new Vec2();
        }
        this.m_normals = new Vec2[Settings.maxPolygonVertices];
        for (int i = 0; i < this.m_normals.length; ++i) {
            this.m_normals[i] = new Vec2();
        }
        this.setRadius(Settings.polygonRadius);
        this.m_centroid.setZero();
    }
    
    @Override
    public final Shape clone() {
        final PolygonShape shape = new PolygonShape();
        shape.m_centroid.set(this.m_centroid);
        for (int i = 0; i < shape.m_normals.length; ++i) {
            shape.m_normals[i].set(this.m_normals[i]);
            shape.m_vertices[i].set(this.m_vertices[i]);
        }
        shape.setRadius(this.getRadius());
        shape.m_count = this.m_count;
        return shape;
    }
    
    public final void set(final Vec2[] vertices, final int count) {
        this.set(vertices, count, null, null);
    }
    
    public final void set(final Vec2[] verts, final int num, final Vec2Array vecPool, final IntArray intPool) {
        assert 3 <= num && num <= Settings.maxPolygonVertices;
        if (num < 3) {
            this.setAsBox(1.0f, 1.0f);
            return;
        }
        int n = MathUtils.min(num, Settings.maxPolygonVertices);
        final Vec2[] ps = (vecPool != null) ? vecPool.get(Settings.maxPolygonVertices) : new Vec2[Settings.maxPolygonVertices];
        int tempCount = 0;
        for (final Vec2 v : verts) {
            boolean unique = true;
            for (int j = 0; j < tempCount; ++j) {
                if (MathUtils.distanceSquared(v, ps[j]) < 0.5f * Settings.linearSlop) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                ps[tempCount++] = v;
            }
        }
        n = tempCount;
        if (n >= 3) {
            int i2 = 0;
            float x0 = ps[0].x;
            for (int k = 1; k < n; ++k) {
                final float x2 = ps[k].x;
                if (x2 > x0 || (x2 == x0 && ps[k].y < ps[i2].y)) {
                    i2 = k;
                    x0 = x2;
                }
            }
            final int[] hull = (intPool != null) ? intPool.get(Settings.maxPolygonVertices) : new int[Settings.maxPolygonVertices];
            int m = 0;
            int ih = i2;
            int ie;
            do {
                hull[m] = ih;
                ie = 0;
                for (int l = 1; l < n; ++l) {
                    if (ie == ih) {
                        ie = l;
                    }
                    else {
                        final Vec2 r = this.pool1.set(ps[ie]).subLocal(ps[hull[m]]);
                        final Vec2 v2 = this.pool2.set(ps[l]).subLocal(ps[hull[m]]);
                        final float c = Vec2.cross(r, v2);
                        if (c < 0.0f) {
                            ie = l;
                        }
                        if (c == 0.0f && v2.lengthSquared() > r.lengthSquared()) {
                            ie = l;
                        }
                    }
                }
                ++m;
            } while ((ih = ie) != i2);
            this.m_count = m;
            for (int i3 = 0; i3 < this.m_count; ++i3) {
                if (this.m_vertices[i3] == null) {
                    this.m_vertices[i3] = new Vec2();
                }
                this.m_vertices[i3].set(ps[hull[i3]]);
            }
            final Vec2 edge = this.pool1;
            for (int i4 = 0; i4 < this.m_count; ++i4) {
                final int i5 = i4;
                final int i6 = (i4 + 1 < this.m_count) ? (i4 + 1) : 0;
                edge.set(this.m_vertices[i6]).subLocal(this.m_vertices[i5]);
                assert edge.lengthSquared() > 1.4210855E-14f;
                Vec2.crossToOutUnsafe(edge, 1.0f, this.m_normals[i4]);
                this.m_normals[i4].normalize();
            }
            this.computeCentroidToOut(this.m_vertices, this.m_count, this.m_centroid);
            return;
        }
        assert false;
        this.setAsBox(1.0f, 1.0f);
    }
    
    public final void setAsBox(final float hx, final float hy) {
        this.m_count = 4;
        this.m_vertices[0].set(-hx, -hy);
        this.m_vertices[1].set(hx, -hy);
        this.m_vertices[2].set(hx, hy);
        this.m_vertices[3].set(-hx, hy);
        this.m_normals[0].set(0.0f, -1.0f);
        this.m_normals[1].set(1.0f, 0.0f);
        this.m_normals[2].set(0.0f, 1.0f);
        this.m_normals[3].set(-1.0f, 0.0f);
        this.m_centroid.setZero();
    }
    
    public final void setAsBox(final float hx, final float hy, final Vec2 center, final float angle) {
        this.m_count = 4;
        this.m_vertices[0].set(-hx, -hy);
        this.m_vertices[1].set(hx, -hy);
        this.m_vertices[2].set(hx, hy);
        this.m_vertices[3].set(-hx, hy);
        this.m_normals[0].set(0.0f, -1.0f);
        this.m_normals[1].set(1.0f, 0.0f);
        this.m_normals[2].set(0.0f, 1.0f);
        this.m_normals[3].set(-1.0f, 0.0f);
        this.m_centroid.set(center);
        final Transform xf = this.poolt1;
        xf.p.set(center);
        xf.q.set(angle);
        for (int i = 0; i < this.m_count; ++i) {
            Transform.mulToOut(xf, this.m_vertices[i], this.m_vertices[i]);
            Rot.mulToOut(xf.q, this.m_normals[i], this.m_normals[i]);
        }
    }
    
    @Override
    public int getChildCount() {
        return 1;
    }
    
    @Override
    public final boolean testPoint(final Transform xf, final Vec2 p) {
        final Rot xfq = xf.q;
        float tempx = p.x - xf.p.x;
        float tempy = p.y - xf.p.y;
        final float pLocalx = xfq.c * tempx + xfq.s * tempy;
        final float pLocaly = -xfq.s * tempx + xfq.c * tempy;
        for (int i = 0; i < this.m_count; ++i) {
            final Vec2 vertex = this.m_vertices[i];
            final Vec2 normal = this.m_normals[i];
            tempx = pLocalx - vertex.x;
            tempy = pLocaly - vertex.y;
            final float dot = normal.x * tempx + normal.y * tempy;
            if (dot > 0.0f) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public final void computeAABB(final AABB aabb, final Transform xf, final int childIndex) {
        final Vec2 lower = aabb.lowerBound;
        final Vec2 upper = aabb.upperBound;
        final Vec2 v1 = this.m_vertices[0];
        final float xfqc = xf.q.c;
        final float xfqs = xf.q.s;
        final float xfpx = xf.p.x;
        final float xfpy = xf.p.y;
        lower.x = xfqc * v1.x - xfqs * v1.y + xfpx;
        lower.y = xfqs * v1.x + xfqc * v1.y + xfpy;
        upper.x = lower.x;
        upper.y = lower.y;
        for (int i = 1; i < this.m_count; ++i) {
            final Vec2 v2 = this.m_vertices[i];
            final float vx = xfqc * v2.x - xfqs * v2.y + xfpx;
            final float vy = xfqs * v2.x + xfqc * v2.y + xfpy;
            lower.x = ((lower.x < vx) ? lower.x : vx);
            lower.y = ((lower.y < vy) ? lower.y : vy);
            upper.x = ((upper.x > vx) ? upper.x : vx);
            upper.y = ((upper.y > vy) ? upper.y : vy);
        }
        final Vec2 vec2 = lower;
        vec2.x -= this.m_radius;
        final Vec2 vec3 = lower;
        vec3.y -= this.m_radius;
        final Vec2 vec4 = upper;
        vec4.x += this.m_radius;
        final Vec2 vec5 = upper;
        vec5.y += this.m_radius;
    }
    
    public final int getVertexCount() {
        return this.m_count;
    }
    
    public final Vec2 getVertex(final int index) {
        assert 0 <= index && index < this.m_count;
        return this.m_vertices[index];
    }
    
    @Override
    public float computeDistanceToOut(final Transform xf, final Vec2 p, final int childIndex, final Vec2 normalOut) {
        final float xfqc = xf.q.c;
        final float xfqs = xf.q.s;
        float tx = p.x - xf.p.x;
        float ty = p.y - xf.p.y;
        final float pLocalx = xfqc * tx + xfqs * ty;
        final float pLocaly = -xfqs * tx + xfqc * ty;
        float maxDistance = -3.4028235E38f;
        float normalForMaxDistanceX = pLocalx;
        float normalForMaxDistanceY = pLocaly;
        for (int i = 0; i < this.m_count; ++i) {
            final Vec2 vertex = this.m_vertices[i];
            final Vec2 normal = this.m_normals[i];
            tx = pLocalx - vertex.x;
            ty = pLocaly - vertex.y;
            final float dot = normal.x * tx + normal.y * ty;
            if (dot > maxDistance) {
                maxDistance = dot;
                normalForMaxDistanceX = normal.x;
                normalForMaxDistanceY = normal.y;
            }
        }
        float distance3;
        if (maxDistance > 0.0f) {
            float minDistanceX = normalForMaxDistanceX;
            float minDistanceY = normalForMaxDistanceY;
            float minDistance2 = maxDistance * maxDistance;
            for (int j = 0; j < this.m_count; ++j) {
                final Vec2 vertex2 = this.m_vertices[j];
                final float distanceVecX = pLocalx - vertex2.x;
                final float distanceVecY = pLocaly - vertex2.y;
                final float distance2 = distanceVecX * distanceVecX + distanceVecY * distanceVecY;
                if (minDistance2 > distance2) {
                    minDistanceX = distanceVecX;
                    minDistanceY = distanceVecY;
                    minDistance2 = distance2;
                }
            }
            distance3 = MathUtils.sqrt(minDistance2);
            normalOut.x = xfqc * minDistanceX - xfqs * minDistanceY;
            normalOut.y = xfqs * minDistanceX + xfqc * minDistanceY;
            normalOut.normalize();
        }
        else {
            distance3 = maxDistance;
            normalOut.x = xfqc * normalForMaxDistanceX - xfqs * normalForMaxDistanceY;
            normalOut.y = xfqs * normalForMaxDistanceX + xfqc * normalForMaxDistanceY;
        }
        return distance3;
    }
    
    @Override
    public final boolean raycast(final RayCastOutput output, final RayCastInput input, final Transform xf, final int childIndex) {
        final float xfqc = xf.q.c;
        final float xfqs = xf.q.s;
        final Vec2 xfp = xf.p;
        float tempx = input.p1.x - xfp.x;
        float tempy = input.p1.y - xfp.y;
        final float p1x = xfqc * tempx + xfqs * tempy;
        final float p1y = -xfqs * tempx + xfqc * tempy;
        tempx = input.p2.x - xfp.x;
        tempy = input.p2.y - xfp.y;
        final float p2x = xfqc * tempx + xfqs * tempy;
        final float p2y = -xfqs * tempx + xfqc * tempy;
        final float dx = p2x - p1x;
        final float dy = p2y - p1y;
        float lower = 0.0f;
        float upper = input.maxFraction;
        int index = -1;
        for (int i = 0; i < this.m_count; ++i) {
            final Vec2 normal = this.m_normals[i];
            final Vec2 vertex = this.m_vertices[i];
            final float tempxn = vertex.x - p1x;
            final float tempyn = vertex.y - p1y;
            final float numerator = normal.x * tempxn + normal.y * tempyn;
            final float denominator = normal.x * dx + normal.y * dy;
            if (denominator == 0.0f) {
                if (numerator < 0.0f) {
                    return false;
                }
            }
            else if (denominator < 0.0f && numerator < lower * denominator) {
                lower = numerator / denominator;
                index = i;
            }
            else if (denominator > 0.0f && numerator < upper * denominator) {
                upper = numerator / denominator;
            }
            if (upper < lower) {
                return false;
            }
        }
        assert 0.0f <= lower && lower <= input.maxFraction;
        if (index >= 0) {
            output.fraction = lower;
            final Vec2 normal2 = this.m_normals[index];
            final Vec2 out = output.normal;
            out.x = xfqc * normal2.x - xfqs * normal2.y;
            out.y = xfqs * normal2.x + xfqc * normal2.y;
            return true;
        }
        return false;
    }
    
    public final void computeCentroidToOut(final Vec2[] vs, final int count, final Vec2 out) {
        assert count >= 3;
        out.set(0.0f, 0.0f);
        float area = 0.0f;
        final Vec2 pRef = this.pool1;
        pRef.setZero();
        final Vec2 e1 = this.pool2;
        final Vec2 e2 = this.pool3;
        final float inv3 = 0.33333334f;
        for (int i = 0; i < count; ++i) {
            final Vec2 p1 = pRef;
            final Vec2 p2 = vs[i];
            final Vec2 p3 = (i + 1 < count) ? vs[i + 1] : vs[0];
            e1.set(p2).subLocal(p1);
            e2.set(p3).subLocal(p1);
            final float D = Vec2.cross(e1, e2);
            final float triangleArea = 0.5f * D;
            area += triangleArea;
            e1.set(p1).addLocal(p2).addLocal(p3).mulLocal(triangleArea * 0.33333334f);
            out.addLocal(e1);
        }
        assert area > 1.1920929E-7f;
        out.mulLocal(1.0f / area);
    }
    
    @Override
    public void computeMass(final MassData massData, final float density) {
        assert this.m_count >= 3;
        final Vec2 center = this.pool1;
        center.setZero();
        float area = 0.0f;
        float I = 0.0f;
        final Vec2 s = this.pool2;
        s.setZero();
        for (int i = 0; i < this.m_count; ++i) {
            s.addLocal(this.m_vertices[i]);
        }
        s.mulLocal(1.0f / this.m_count);
        final float k_inv3 = 0.33333334f;
        final Vec2 e1 = this.pool3;
        final Vec2 e2 = this.pool4;
        for (int j = 0; j < this.m_count; ++j) {
            e1.set(this.m_vertices[j]).subLocal(s);
            e2.set(s).negateLocal().addLocal((j + 1 < this.m_count) ? this.m_vertices[j + 1] : this.m_vertices[0]);
            final float D = Vec2.cross(e1, e2);
            final float triangleArea = 0.5f * D;
            area += triangleArea;
            final Vec2 vec2 = center;
            vec2.x += triangleArea * 0.33333334f * (e1.x + e2.x);
            final Vec2 vec3 = center;
            vec3.y += triangleArea * 0.33333334f * (e1.y + e2.y);
            final float ex1 = e1.x;
            final float ey1 = e1.y;
            final float ex2 = e2.x;
            final float ey2 = e2.y;
            final float intx2 = ex1 * ex1 + ex2 * ex1 + ex2 * ex2;
            final float inty2 = ey1 * ey1 + ey2 * ey1 + ey2 * ey2;
            I += 0.083333336f * D * (intx2 + inty2);
        }
        massData.mass = density * area;
        assert area > 1.1920929E-7f;
        center.mulLocal(1.0f / area);
        massData.center.set(center).addLocal(s);
        massData.I = I * density;
        massData.I += massData.mass * Vec2.dot(massData.center, massData.center);
    }
    
    public boolean validate() {
        for (int i = 0; i < this.m_count; ++i) {
            final int i2 = i;
            final int i3 = (i < this.m_count - 1) ? (i2 + 1) : 0;
            final Vec2 p = this.m_vertices[i2];
            final Vec2 e = this.pool1.set(this.m_vertices[i3]).subLocal(p);
            for (int j = 0; j < this.m_count; ++j) {
                if (j != i2) {
                    if (j != i3) {
                        final Vec2 v = this.pool2.set(this.m_vertices[j]).subLocal(p);
                        final float c = Vec2.cross(e, v);
                        if (c < 0.0f) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    public Vec2[] getVertices() {
        return this.m_vertices;
    }
    
    public Vec2[] getNormals() {
        return this.m_normals;
    }
    
    public Vec2 centroid(final Transform xf) {
        return Transform.mul(xf, this.m_centroid);
    }
    
    public Vec2 centroidToOut(final Transform xf, final Vec2 out) {
        Transform.mulToOutUnsafe(xf, this.m_centroid, out);
        return out;
    }
}
