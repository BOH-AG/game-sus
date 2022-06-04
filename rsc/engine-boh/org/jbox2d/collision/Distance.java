// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Vec2;

public class Distance
{
    public static final int MAX_ITERS = 20;
    public static int GJK_CALLS;
    public static int GJK_ITERS;
    public static int GJK_MAX_ITERS;
    private Simplex simplex;
    private int[] saveA;
    private int[] saveB;
    private Vec2 closestPoint;
    private Vec2 d;
    private Vec2 temp;
    private Vec2 normal;
    
    public Distance() {
        this.simplex = new Simplex();
        this.saveA = new int[3];
        this.saveB = new int[3];
        this.closestPoint = new Vec2();
        this.d = new Vec2();
        this.temp = new Vec2();
        this.normal = new Vec2();
    }
    
    public final void distance(final DistanceOutput output, final SimplexCache cache, final DistanceInput input) {
        ++Distance.GJK_CALLS;
        final DistanceProxy proxyA = input.proxyA;
        final DistanceProxy proxyB = input.proxyB;
        final Transform transformA = input.transformA;
        final Transform transformB = input.transformB;
        this.simplex.readCache(cache, proxyA, transformA, proxyB, transformB);
        final SimplexVertex[] vertices = this.simplex.vertices;
        int saveCount = 0;
        this.simplex.getClosestPoint(this.closestPoint);
        float distanceSqr2;
        float distanceSqr1 = distanceSqr2 = this.closestPoint.lengthSquared();
        int iter = 0;
        while (iter < 20) {
            saveCount = this.simplex.m_count;
            for (int i = 0; i < saveCount; ++i) {
                this.saveA[i] = vertices[i].indexA;
                this.saveB[i] = vertices[i].indexB;
            }
            switch (this.simplex.m_count) {
                case 1: {
                    break;
                }
                case 2: {
                    this.simplex.solve2();
                    break;
                }
                case 3: {
                    this.simplex.solve3();
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
            if (this.simplex.m_count == 3) {
                break;
            }
            this.simplex.getClosestPoint(this.closestPoint);
            distanceSqr2 = this.closestPoint.lengthSquared();
            if (distanceSqr2 >= distanceSqr1) {}
            distanceSqr1 = distanceSqr2;
            this.simplex.getSearchDirection(this.d);
            if (this.d.lengthSquared() < 1.4210855E-14f) {
                break;
            }
            final SimplexVertex vertex = vertices[this.simplex.m_count];
            Rot.mulTransUnsafe(transformA.q, this.d.negateLocal(), this.temp);
            vertex.indexA = proxyA.getSupport(this.temp);
            Transform.mulToOutUnsafe(transformA, proxyA.getVertex(vertex.indexA), vertex.wA);
            Rot.mulTransUnsafe(transformB.q, this.d.negateLocal(), this.temp);
            vertex.indexB = proxyB.getSupport(this.temp);
            Transform.mulToOutUnsafe(transformB, proxyB.getVertex(vertex.indexB), vertex.wB);
            vertex.w.set(vertex.wB).subLocal(vertex.wA);
            ++iter;
            ++Distance.GJK_ITERS;
            boolean duplicate = false;
            for (int j = 0; j < saveCount; ++j) {
                if (vertex.indexA == this.saveA[j] && vertex.indexB == this.saveB[j]) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                break;
            }
            final Simplex simplex = this.simplex;
            ++simplex.m_count;
        }
        Distance.GJK_MAX_ITERS = MathUtils.max(Distance.GJK_MAX_ITERS, iter);
        this.simplex.getWitnessPoints(output.pointA, output.pointB);
        output.distance = MathUtils.distance(output.pointA, output.pointB);
        output.iterations = iter;
        this.simplex.writeCache(cache);
        if (input.useRadii) {
            final float rA = proxyA.m_radius;
            final float rB = proxyB.m_radius;
            if (output.distance > rA + rB && output.distance > 1.1920929E-7f) {
                output.distance -= rA + rB;
                this.normal.set(output.pointB).subLocal(output.pointA);
                this.normal.normalize();
                this.temp.set(this.normal).mulLocal(rA);
                output.pointA.addLocal(this.temp);
                this.temp.set(this.normal).mulLocal(rB);
                output.pointB.subLocal(this.temp);
            }
            else {
                output.pointA.addLocal(output.pointB).mulLocal(0.5f);
                output.pointB.set(output.pointA);
                output.distance = 0.0f;
            }
        }
    }
    
    static {
        Distance.GJK_CALLS = 0;
        Distance.GJK_ITERS = 0;
        Distance.GJK_MAX_ITERS = 20;
    }
    
    private class SimplexVertex
    {
        public final Vec2 wA;
        public final Vec2 wB;
        public final Vec2 w;
        public float a;
        public int indexA;
        public int indexB;
        
        private SimplexVertex() {
            this.wA = new Vec2();
            this.wB = new Vec2();
            this.w = new Vec2();
        }
        
        public void set(final SimplexVertex sv) {
            this.wA.set(sv.wA);
            this.wB.set(sv.wB);
            this.w.set(sv.w);
            this.a = sv.a;
            this.indexA = sv.indexA;
            this.indexB = sv.indexB;
        }
    }
    
    public static class SimplexCache
    {
        public float metric;
        public int count;
        public final int[] indexA;
        public final int[] indexB;
        
        public SimplexCache() {
            this.indexA = new int[3];
            this.indexB = new int[3];
            this.metric = 0.0f;
            this.count = 0;
            this.indexA[0] = Integer.MAX_VALUE;
            this.indexA[1] = Integer.MAX_VALUE;
            this.indexA[2] = Integer.MAX_VALUE;
            this.indexB[0] = Integer.MAX_VALUE;
            this.indexB[1] = Integer.MAX_VALUE;
            this.indexB[2] = Integer.MAX_VALUE;
        }
        
        public void set(final SimplexCache sc) {
            System.arraycopy(sc.indexA, 0, this.indexA, 0, this.indexA.length);
            System.arraycopy(sc.indexB, 0, this.indexB, 0, this.indexB.length);
            this.metric = sc.metric;
            this.count = sc.count;
        }
    }
    
    private class Simplex
    {
        public final SimplexVertex m_v1;
        public final SimplexVertex m_v2;
        public final SimplexVertex m_v3;
        public final SimplexVertex[] vertices;
        public int m_count;
        private final Vec2 e12;
        private final Vec2 case2;
        private final Vec2 case22;
        private final Vec2 case3;
        private final Vec2 case33;
        private final Vec2 e13;
        private final Vec2 e23;
        private final Vec2 w1;
        private final Vec2 w2;
        private final Vec2 w3;
        
        private Simplex() {
            this.m_v1 = new SimplexVertex();
            this.m_v2 = new SimplexVertex();
            this.m_v3 = new SimplexVertex();
            this.vertices = new SimplexVertex[] { this.m_v1, this.m_v2, this.m_v3 };
            this.e12 = new Vec2();
            this.case2 = new Vec2();
            this.case22 = new Vec2();
            this.case3 = new Vec2();
            this.case33 = new Vec2();
            this.e13 = new Vec2();
            this.e23 = new Vec2();
            this.w1 = new Vec2();
            this.w2 = new Vec2();
            this.w3 = new Vec2();
        }
        
        public void readCache(final SimplexCache cache, final DistanceProxy proxyA, final Transform transformA, final DistanceProxy proxyB, final Transform transformB) {
            assert cache.count <= 3;
            this.m_count = cache.count;
            for (int i = 0; i < this.m_count; ++i) {
                final SimplexVertex v = this.vertices[i];
                v.indexA = cache.indexA[i];
                v.indexB = cache.indexB[i];
                final Vec2 wALocal = proxyA.getVertex(v.indexA);
                final Vec2 wBLocal = proxyB.getVertex(v.indexB);
                Transform.mulToOutUnsafe(transformA, wALocal, v.wA);
                Transform.mulToOutUnsafe(transformB, wBLocal, v.wB);
                v.w.set(v.wB).subLocal(v.wA);
                v.a = 0.0f;
            }
            if (this.m_count > 1) {
                final float metric1 = cache.metric;
                final float metric2 = this.getMetric();
                if (metric2 < 0.5f * metric1 || 2.0f * metric1 < metric2 || metric2 < 1.1920929E-7f) {
                    this.m_count = 0;
                }
            }
            if (this.m_count == 0) {
                final SimplexVertex v2 = this.vertices[0];
                v2.indexA = 0;
                v2.indexB = 0;
                final Vec2 wALocal2 = proxyA.getVertex(0);
                final Vec2 wBLocal2 = proxyB.getVertex(0);
                Transform.mulToOutUnsafe(transformA, wALocal2, v2.wA);
                Transform.mulToOutUnsafe(transformB, wBLocal2, v2.wB);
                v2.w.set(v2.wB).subLocal(v2.wA);
                this.m_count = 1;
            }
        }
        
        public void writeCache(final SimplexCache cache) {
            cache.metric = this.getMetric();
            cache.count = this.m_count;
            for (int i = 0; i < this.m_count; ++i) {
                cache.indexA[i] = this.vertices[i].indexA;
                cache.indexB[i] = this.vertices[i].indexB;
            }
        }
        
        public final void getSearchDirection(final Vec2 out) {
            switch (this.m_count) {
                case 1: {
                    out.set(this.m_v1.w).negateLocal();
                }
                case 2: {
                    this.e12.set(this.m_v2.w).subLocal(this.m_v1.w);
                    out.set(this.m_v1.w).negateLocal();
                    final float sgn = Vec2.cross(this.e12, out);
                    if (sgn > 0.0f) {
                        Vec2.crossToOutUnsafe(1.0f, this.e12, out);
                        return;
                    }
                    Vec2.crossToOutUnsafe(this.e12, 1.0f, out);
                }
                default: {
                    assert false;
                    out.setZero();
                }
            }
        }
        
        public void getClosestPoint(final Vec2 out) {
            switch (this.m_count) {
                case 0: {
                    assert false;
                    out.setZero();
                }
                case 1: {
                    out.set(this.m_v1.w);
                }
                case 2: {
                    this.case22.set(this.m_v2.w).mulLocal(this.m_v2.a);
                    this.case2.set(this.m_v1.w).mulLocal(this.m_v1.a).addLocal(this.case22);
                    out.set(this.case2);
                }
                case 3: {
                    out.setZero();
                }
                default: {
                    assert false;
                    out.setZero();
                }
            }
        }
        
        public void getWitnessPoints(final Vec2 pA, final Vec2 pB) {
            switch (this.m_count) {
                case 0: {
                    assert false;
                    break;
                }
                case 1: {
                    pA.set(this.m_v1.wA);
                    pB.set(this.m_v1.wB);
                    break;
                }
                case 2: {
                    this.case2.set(this.m_v1.wA).mulLocal(this.m_v1.a);
                    pA.set(this.m_v2.wA).mulLocal(this.m_v2.a).addLocal(this.case2);
                    this.case2.set(this.m_v1.wB).mulLocal(this.m_v1.a);
                    pB.set(this.m_v2.wB).mulLocal(this.m_v2.a).addLocal(this.case2);
                    break;
                }
                case 3: {
                    pA.set(this.m_v1.wA).mulLocal(this.m_v1.a);
                    this.case3.set(this.m_v2.wA).mulLocal(this.m_v2.a);
                    this.case33.set(this.m_v3.wA).mulLocal(this.m_v3.a);
                    pA.addLocal(this.case3).addLocal(this.case33);
                    pB.set(pA);
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        
        public float getMetric() {
            switch (this.m_count) {
                case 0: {
                    assert false;
                    return 0.0f;
                }
                case 1: {
                    return 0.0f;
                }
                case 2: {
                    return MathUtils.distance(this.m_v1.w, this.m_v2.w);
                }
                case 3: {
                    this.case3.set(this.m_v2.w).subLocal(this.m_v1.w);
                    this.case33.set(this.m_v3.w).subLocal(this.m_v1.w);
                    return Vec2.cross(this.case3, this.case33);
                }
                default: {
                    assert false;
                    return 0.0f;
                }
            }
        }
        
        public void solve2() {
            final Vec2 w1 = this.m_v1.w;
            final Vec2 w2 = this.m_v2.w;
            this.e12.set(w2).subLocal(w1);
            final float d12_2 = -Vec2.dot(w1, this.e12);
            if (d12_2 <= 0.0f) {
                this.m_v1.a = 1.0f;
                this.m_count = 1;
                return;
            }
            final float d12_3 = Vec2.dot(w2, this.e12);
            if (d12_3 <= 0.0f) {
                this.m_v2.a = 1.0f;
                this.m_count = 1;
                this.m_v1.set(this.m_v2);
                return;
            }
            final float inv_d12 = 1.0f / (d12_3 + d12_2);
            this.m_v1.a = d12_3 * inv_d12;
            this.m_v2.a = d12_2 * inv_d12;
            this.m_count = 2;
        }
        
        public void solve3() {
            this.w1.set(this.m_v1.w);
            this.w2.set(this.m_v2.w);
            this.w3.set(this.m_v3.w);
            this.e12.set(this.w2).subLocal(this.w1);
            final float w1e12 = Vec2.dot(this.w1, this.e12);
            final float d12_1;
            final float w2e12 = d12_1 = Vec2.dot(this.w2, this.e12);
            final float d12_2 = -w1e12;
            this.e13.set(this.w3).subLocal(this.w1);
            final float w1e13 = Vec2.dot(this.w1, this.e13);
            final float d13_1;
            final float w3e13 = d13_1 = Vec2.dot(this.w3, this.e13);
            final float d13_2 = -w1e13;
            this.e23.set(this.w3).subLocal(this.w2);
            final float w2e13 = Vec2.dot(this.w2, this.e23);
            final float d23_1;
            final float w3e14 = d23_1 = Vec2.dot(this.w3, this.e23);
            final float d23_2 = -w2e13;
            final float n123 = Vec2.cross(this.e12, this.e13);
            final float d123_1 = n123 * Vec2.cross(this.w2, this.w3);
            final float d123_2 = n123 * Vec2.cross(this.w3, this.w1);
            final float d123_3 = n123 * Vec2.cross(this.w1, this.w2);
            if (d12_2 <= 0.0f && d13_2 <= 0.0f) {
                this.m_v1.a = 1.0f;
                this.m_count = 1;
                return;
            }
            if (d12_1 > 0.0f && d12_2 > 0.0f && d123_3 <= 0.0f) {
                final float inv_d12 = 1.0f / (d12_1 + d12_2);
                this.m_v1.a = d12_1 * inv_d12;
                this.m_v2.a = d12_2 * inv_d12;
                this.m_count = 2;
                return;
            }
            if (d13_1 > 0.0f && d13_2 > 0.0f && d123_2 <= 0.0f) {
                final float inv_d13 = 1.0f / (d13_1 + d13_2);
                this.m_v1.a = d13_1 * inv_d13;
                this.m_v3.a = d13_2 * inv_d13;
                this.m_count = 2;
                this.m_v2.set(this.m_v3);
                return;
            }
            if (d12_1 <= 0.0f && d23_2 <= 0.0f) {
                this.m_v2.a = 1.0f;
                this.m_count = 1;
                this.m_v1.set(this.m_v2);
                return;
            }
            if (d13_1 <= 0.0f && d23_1 <= 0.0f) {
                this.m_v3.a = 1.0f;
                this.m_count = 1;
                this.m_v1.set(this.m_v3);
                return;
            }
            if (d23_1 > 0.0f && d23_2 > 0.0f && d123_1 <= 0.0f) {
                final float inv_d14 = 1.0f / (d23_1 + d23_2);
                this.m_v2.a = d23_1 * inv_d14;
                this.m_v3.a = d23_2 * inv_d14;
                this.m_count = 2;
                this.m_v1.set(this.m_v3);
                return;
            }
            final float inv_d15 = 1.0f / (d123_1 + d123_2 + d123_3);
            this.m_v1.a = d123_1 * inv_d15;
            this.m_v2.a = d123_2 * inv_d15;
            this.m_v3.a = d123_3 * inv_d15;
            this.m_count = 3;
        }
    }
    
    public static class DistanceProxy
    {
        public final Vec2[] m_vertices;
        public int m_count;
        public float m_radius;
        public final Vec2[] m_buffer;
        
        public DistanceProxy() {
            this.m_vertices = new Vec2[Settings.maxPolygonVertices];
            for (int i = 0; i < this.m_vertices.length; ++i) {
                this.m_vertices[i] = new Vec2();
            }
            this.m_buffer = new Vec2[2];
            this.m_count = 0;
            this.m_radius = 0.0f;
        }
        
        public final void set(final Shape shape, final int index) {
            switch (shape.getType()) {
                case CIRCLE: {
                    final CircleShape circle = (CircleShape)shape;
                    this.m_vertices[0].set(circle.m_p);
                    this.m_count = 1;
                    this.m_radius = circle.m_radius;
                    break;
                }
                case POLYGON: {
                    final PolygonShape poly = (PolygonShape)shape;
                    this.m_count = poly.m_count;
                    this.m_radius = poly.m_radius;
                    for (int i = 0; i < this.m_count; ++i) {
                        this.m_vertices[i].set(poly.m_vertices[i]);
                    }
                    break;
                }
                case CHAIN: {
                    final ChainShape chain = (ChainShape)shape;
                    assert 0 <= index && index < chain.m_count;
                    this.m_buffer[0] = chain.m_vertices[index];
                    if (index + 1 < chain.m_count) {
                        this.m_buffer[1] = chain.m_vertices[index + 1];
                    }
                    else {
                        this.m_buffer[1] = chain.m_vertices[0];
                    }
                    this.m_vertices[0].set(this.m_buffer[0]);
                    this.m_vertices[1].set(this.m_buffer[1]);
                    this.m_count = 2;
                    this.m_radius = chain.m_radius;
                    break;
                }
                case EDGE: {
                    final EdgeShape edge = (EdgeShape)shape;
                    this.m_vertices[0].set(edge.m_vertex1);
                    this.m_vertices[1].set(edge.m_vertex2);
                    this.m_count = 2;
                    this.m_radius = edge.m_radius;
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        
        public final int getSupport(final Vec2 d) {
            int bestIndex = 0;
            float bestValue = Vec2.dot(this.m_vertices[0], d);
            for (int i = 1; i < this.m_count; ++i) {
                final float value = Vec2.dot(this.m_vertices[i], d);
                if (value > bestValue) {
                    bestIndex = i;
                    bestValue = value;
                }
            }
            return bestIndex;
        }
        
        public final Vec2 getSupportVertex(final Vec2 d) {
            int bestIndex = 0;
            float bestValue = Vec2.dot(this.m_vertices[0], d);
            for (int i = 1; i < this.m_count; ++i) {
                final float value = Vec2.dot(this.m_vertices[i], d);
                if (value > bestValue) {
                    bestIndex = i;
                    bestValue = value;
                }
            }
            return this.m_vertices[bestIndex];
        }
        
        public final int getVertexCount() {
            return this.m_count;
        }
        
        public final Vec2 getVertex(final int index) {
            assert 0 <= index && index < this.m_count;
            return this.m_vertices[index];
        }
    }
}
