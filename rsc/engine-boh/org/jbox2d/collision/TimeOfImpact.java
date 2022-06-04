// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.common.Sweep;
import org.jbox2d.common.Transform;

public class TimeOfImpact
{
    public static final int MAX_ITERATIONS = 20;
    public static final int MAX_ROOT_ITERATIONS = 50;
    public static int toiCalls;
    public static int toiIters;
    public static int toiMaxIters;
    public static int toiRootIters;
    public static int toiMaxRootIters;
    private final Distance.SimplexCache cache;
    private final DistanceInput distanceInput;
    private final Transform xfA;
    private final Transform xfB;
    private final DistanceOutput distanceOutput;
    private final SeparationFunction fcn;
    private final int[] indexes;
    private final Sweep sweepA;
    private final Sweep sweepB;
    private final IWorldPool pool;
    
    public TimeOfImpact(final IWorldPool argPool) {
        this.cache = new Distance.SimplexCache();
        this.distanceInput = new DistanceInput();
        this.xfA = new Transform();
        this.xfB = new Transform();
        this.distanceOutput = new DistanceOutput();
        this.fcn = new SeparationFunction();
        this.indexes = new int[2];
        this.sweepA = new Sweep();
        this.sweepB = new Sweep();
        this.pool = argPool;
    }
    
    public final void timeOfImpact(final TOIOutput output, final TOIInput input) {
        ++TimeOfImpact.toiCalls;
        output.state = TOIOutputState.UNKNOWN;
        output.t = input.tMax;
        final Distance.DistanceProxy proxyA = input.proxyA;
        final Distance.DistanceProxy proxyB = input.proxyB;
        this.sweepA.set(input.sweepA);
        this.sweepB.set(input.sweepB);
        this.sweepA.normalize();
        this.sweepB.normalize();
        final float tMax = input.tMax;
        final float totalRadius = proxyA.m_radius + proxyB.m_radius;
        final float target = MathUtils.max(Settings.linearSlop, totalRadius - 3.0f * Settings.linearSlop);
        final float tolerance = 0.25f * Settings.linearSlop;
        assert target > tolerance;
        float t1 = 0.0f;
        int iter = 0;
        this.cache.count = 0;
        this.distanceInput.proxyA = input.proxyA;
        this.distanceInput.proxyB = input.proxyB;
        this.distanceInput.useRadii = false;
        while (true) {
            this.sweepA.getTransform(this.xfA, t1);
            this.sweepB.getTransform(this.xfB, t1);
            this.distanceInput.transformA = this.xfA;
            this.distanceInput.transformB = this.xfB;
            this.pool.getDistance().distance(this.distanceOutput, this.cache, this.distanceInput);
            if (this.distanceOutput.distance <= 0.0f) {
                output.state = TOIOutputState.OVERLAPPED;
                output.t = 0.0f;
                break;
            }
            if (this.distanceOutput.distance < target + tolerance) {
                output.state = TOIOutputState.TOUCHING;
                output.t = t1;
                break;
            }
            this.fcn.initialize(this.cache, proxyA, this.sweepA, proxyB, this.sweepB, t1);
            boolean done = false;
            float t2 = tMax;
            int pushBackIter = 0;
            int rootIterCount;
            do {
                float s2 = this.fcn.findMinSeparation(this.indexes, t2);
                if (s2 > target + tolerance) {
                    output.state = TOIOutputState.SEPARATED;
                    output.t = tMax;
                    done = true;
                    break;
                }
                if (s2 > target - tolerance) {
                    t1 = t2;
                    break;
                }
                float s3 = this.fcn.evaluate(this.indexes[0], this.indexes[1], t1);
                if (s3 < target - tolerance) {
                    output.state = TOIOutputState.FAILED;
                    output.t = t1;
                    done = true;
                    break;
                }
                if (s3 <= target + tolerance) {
                    output.state = TOIOutputState.TOUCHING;
                    output.t = t1;
                    done = true;
                    break;
                }
                rootIterCount = 0;
                float a1 = t1;
                float a2 = t2;
                do {
                    float t3;
                    if ((rootIterCount & 0x1) == 0x1) {
                        t3 = a1 + (target - s3) * (a2 - a1) / (s2 - s3);
                    }
                    else {
                        t3 = 0.5f * (a1 + a2);
                    }
                    ++rootIterCount;
                    ++TimeOfImpact.toiRootIters;
                    final float s4 = this.fcn.evaluate(this.indexes[0], this.indexes[1], t3);
                    if (MathUtils.abs(s4 - target) < tolerance) {
                        t2 = t3;
                        break;
                    }
                    if (s4 > target) {
                        a1 = t3;
                        s3 = s4;
                    }
                    else {
                        a2 = t3;
                        s2 = s4;
                    }
                } while (rootIterCount != 50);
                TimeOfImpact.toiMaxRootIters = MathUtils.max(TimeOfImpact.toiMaxRootIters, rootIterCount);
                if (++pushBackIter == Settings.maxPolygonVertices) {
                    break;
                }
            } while (rootIterCount != 50);
            ++iter;
            ++TimeOfImpact.toiIters;
            if (done) {
                break;
            }
            if (iter == 20) {
                output.state = TOIOutputState.FAILED;
                output.t = t1;
                break;
            }
        }
        TimeOfImpact.toiMaxIters = MathUtils.max(TimeOfImpact.toiMaxIters, iter);
    }
    
    static {
        TimeOfImpact.toiCalls = 0;
        TimeOfImpact.toiIters = 0;
        TimeOfImpact.toiMaxIters = 0;
        TimeOfImpact.toiRootIters = 0;
        TimeOfImpact.toiMaxRootIters = 0;
    }
    
    public static class TOIInput
    {
        public final Distance.DistanceProxy proxyA;
        public final Distance.DistanceProxy proxyB;
        public final Sweep sweepA;
        public final Sweep sweepB;
        public float tMax;
        
        public TOIInput() {
            this.proxyA = new Distance.DistanceProxy();
            this.proxyB = new Distance.DistanceProxy();
            this.sweepA = new Sweep();
            this.sweepB = new Sweep();
        }
    }
    
    public enum TOIOutputState
    {
        UNKNOWN, 
        FAILED, 
        OVERLAPPED, 
        TOUCHING, 
        SEPARATED;
    }
    
    public static class TOIOutput
    {
        public TOIOutputState state;
        public float t;
    }
}
