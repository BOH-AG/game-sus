// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.common.MathUtils;
import org.jbox2d.pooling.normal.MutableStack;
import org.jbox2d.common.Vec2;

public class VoronoiDiagram
{
    private Generator[] m_generatorBuffer;
    private int m_generatorCount;
    private int m_countX;
    private int m_countY;
    private Generator[] m_diagram;
    private final Vec2 lower;
    private final Vec2 upper;
    private MutableStack<VoronoiDiagramTask> taskPool;
    private final StackQueue<VoronoiDiagramTask> queue;
    
    public VoronoiDiagram(final int generatorCapacity) {
        this.lower = new Vec2();
        this.upper = new Vec2();
        this.taskPool = new MutableStack<VoronoiDiagramTask>(50) {
            @Override
            protected VoronoiDiagramTask newInstance() {
                return new VoronoiDiagramTask();
            }
            
            @Override
            protected VoronoiDiagramTask[] newArray(final int size) {
                return new VoronoiDiagramTask[size];
            }
        };
        this.queue = new StackQueue<VoronoiDiagramTask>();
        this.m_generatorBuffer = new Generator[generatorCapacity];
        for (int i = 0; i < generatorCapacity; ++i) {
            this.m_generatorBuffer[i] = new Generator();
        }
        this.m_generatorCount = 0;
        this.m_countX = 0;
        this.m_countY = 0;
        this.m_diagram = null;
    }
    
    public void getNodes(final VoronoiDiagramCallback callback) {
        for (int y = 0; y < this.m_countY - 1; ++y) {
            for (int x = 0; x < this.m_countX - 1; ++x) {
                final int i = x + y * this.m_countX;
                final Generator a = this.m_diagram[i];
                final Generator b = this.m_diagram[i + 1];
                final Generator c = this.m_diagram[i + this.m_countX];
                final Generator d = this.m_diagram[i + 1 + this.m_countX];
                if (b != c) {
                    if (a != b && a != c) {
                        callback.callback(a.tag, b.tag, c.tag);
                    }
                    if (d != b && d != c) {
                        callback.callback(b.tag, d.tag, c.tag);
                    }
                }
            }
        }
    }
    
    public void addGenerator(final Vec2 center, final int tag) {
        final Generator g = this.m_generatorBuffer[this.m_generatorCount++];
        g.center.x = center.x;
        g.center.y = center.y;
        g.tag = tag;
    }
    
    public void generate(final float radius) {
        assert this.m_diagram == null;
        final float inverseRadius = 1.0f / radius;
        this.lower.x = Float.MAX_VALUE;
        this.lower.y = Float.MAX_VALUE;
        this.upper.x = -3.4028235E38f;
        this.upper.y = -3.4028235E38f;
        for (int k = 0; k < this.m_generatorCount; ++k) {
            final Generator g = this.m_generatorBuffer[k];
            Vec2.minToOut(this.lower, g.center, this.lower);
            Vec2.maxToOut(this.upper, g.center, this.upper);
        }
        this.m_countX = 1 + (int)(inverseRadius * (this.upper.x - this.lower.x));
        this.m_countY = 1 + (int)(inverseRadius * (this.upper.y - this.lower.y));
        this.m_diagram = new Generator[this.m_countX * this.m_countY];
        this.queue.reset(new VoronoiDiagramTask[4 * this.m_countX * this.m_countX]);
        for (int k = 0; k < this.m_generatorCount; ++k) {
            final Generator g = this.m_generatorBuffer[k];
            g.center.x = inverseRadius * (g.center.x - this.lower.x);
            g.center.y = inverseRadius * (g.center.y - this.lower.y);
            final int x = MathUtils.max(0, MathUtils.min((int)g.center.x, this.m_countX - 1));
            final int y = MathUtils.max(0, MathUtils.min((int)g.center.y, this.m_countY - 1));
            this.queue.push(this.taskPool.pop().set(x, y, x + y * this.m_countX, g));
        }
        while (!this.queue.empty()) {
            final VoronoiDiagramTask front = this.queue.pop();
            final int x2 = front.m_x;
            final int y2 = front.m_y;
            final int i = front.m_i;
            final Generator g2 = front.m_generator;
            if (this.m_diagram[i] == null) {
                this.m_diagram[i] = g2;
                if (x2 > 0) {
                    this.queue.push(this.taskPool.pop().set(x2 - 1, y2, i - 1, g2));
                }
                if (y2 > 0) {
                    this.queue.push(this.taskPool.pop().set(x2, y2 - 1, i - this.m_countX, g2));
                }
                if (x2 < this.m_countX - 1) {
                    this.queue.push(this.taskPool.pop().set(x2 + 1, y2, i + 1, g2));
                }
                if (y2 < this.m_countY - 1) {
                    this.queue.push(this.taskPool.pop().set(x2, y2 + 1, i + this.m_countX, g2));
                }
            }
            this.taskPool.push(front);
        }
        for (int maxIteration = this.m_countX + this.m_countY, iteration = 0; iteration < maxIteration; ++iteration) {
            for (int y2 = 0; y2 < this.m_countY; ++y2) {
                for (int x3 = 0; x3 < this.m_countX - 1; ++x3) {
                    final int j = x3 + y2 * this.m_countX;
                    final Generator a = this.m_diagram[j];
                    final Generator b = this.m_diagram[j + 1];
                    if (a != b) {
                        this.queue.push(this.taskPool.pop().set(x3, y2, j, b));
                        this.queue.push(this.taskPool.pop().set(x3 + 1, y2, j + 1, a));
                    }
                }
            }
            for (int y2 = 0; y2 < this.m_countY - 1; ++y2) {
                for (int x3 = 0; x3 < this.m_countX; ++x3) {
                    final int j = x3 + y2 * this.m_countX;
                    final Generator a = this.m_diagram[j];
                    final Generator b = this.m_diagram[j + this.m_countX];
                    if (a != b) {
                        this.queue.push(this.taskPool.pop().set(x3, y2, j, b));
                        this.queue.push(this.taskPool.pop().set(x3, y2 + 1, j + this.m_countX, a));
                    }
                }
            }
            boolean updated = false;
            while (!this.queue.empty()) {
                final VoronoiDiagramTask front2 = this.queue.pop();
                final int x4 = front2.m_x;
                final int y3 = front2.m_y;
                final int l = front2.m_i;
                final Generator m = front2.m_generator;
                final Generator a2 = this.m_diagram[l];
                final Generator b2 = m;
                if (a2 != b2) {
                    final float ax = a2.center.x - x4;
                    final float ay = a2.center.y - y3;
                    final float bx = b2.center.x - x4;
                    final float by = b2.center.y - y3;
                    final float a3 = ax * ax + ay * ay;
                    final float b3 = bx * bx + by * by;
                    if (a3 > b3) {
                        this.m_diagram[l] = b2;
                        if (x4 > 0) {
                            this.queue.push(this.taskPool.pop().set(x4 - 1, y3, l - 1, b2));
                        }
                        if (y3 > 0) {
                            this.queue.push(this.taskPool.pop().set(x4, y3 - 1, l - this.m_countX, b2));
                        }
                        if (x4 < this.m_countX - 1) {
                            this.queue.push(this.taskPool.pop().set(x4 + 1, y3, l + 1, b2));
                        }
                        if (y3 < this.m_countY - 1) {
                            this.queue.push(this.taskPool.pop().set(x4, y3 + 1, l + this.m_countX, b2));
                        }
                        updated = true;
                    }
                }
                this.taskPool.push(front2);
            }
            if (!updated) {
                break;
            }
        }
    }
    
    public static class Generator
    {
        final Vec2 center;
        int tag;
        
        public Generator() {
            this.center = new Vec2();
        }
    }
    
    public static class VoronoiDiagramTask
    {
        int m_x;
        int m_y;
        int m_i;
        Generator m_generator;
        
        public VoronoiDiagramTask() {
        }
        
        public VoronoiDiagramTask(final int x, final int y, final int i, final Generator g) {
            this.m_x = x;
            this.m_y = y;
            this.m_i = i;
            this.m_generator = g;
        }
        
        public VoronoiDiagramTask set(final int x, final int y, final int i, final Generator g) {
            this.m_x = x;
            this.m_y = y;
            this.m_i = i;
            this.m_generator = g;
            return this;
        }
    }
    
    public interface VoronoiDiagramCallback
    {
        void callback(final int p0, final int p1, final int p2);
    }
}
