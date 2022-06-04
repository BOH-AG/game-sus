// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.particle;

import org.jbox2d.common.Settings;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.dynamics.Fixture;
import java.lang.reflect.Array;
import org.jbox2d.callbacks.ParticleRaycastCallback;
import org.jbox2d.callbacks.ParticleDestructionListener;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.TimeStep;
import org.jbox2d.callbacks.QueryCallback;
import java.util.Arrays;
import org.jbox2d.common.MathUtils;
import org.jbox2d.callbacks.ParticleQueryCallback;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.BufferUtils;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.collision.AABB;
import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;

public class ParticleSystem
{
    private static final int k_pairFlags = 8;
    private static final int k_triadFlags = 16;
    private static final int k_noPressureFlags = 64;
    static final int xTruncBits = 12;
    static final int yTruncBits = 12;
    static final int tagBits = 31;
    static final long yOffset = 2048L;
    static final int yShift = 19;
    static final int xShift = 7;
    static final long xScale = 128L;
    static final long xOffset = 262144L;
    static final int xMask = 4095;
    static final int yMask = 4095;
    int m_timestamp;
    int m_allParticleFlags;
    int m_allGroupFlags;
    float m_density;
    float m_inverseDensity;
    float m_gravityScale;
    float m_particleDiameter;
    float m_inverseDiameter;
    float m_squaredDiameter;
    int m_count;
    int m_internalAllocatedCapacity;
    int m_maxCount;
    ParticleBufferInt m_flagsBuffer;
    ParticleBuffer<Vec2> m_positionBuffer;
    ParticleBuffer<Vec2> m_velocityBuffer;
    float[] m_accumulationBuffer;
    Vec2[] m_accumulation2Buffer;
    float[] m_depthBuffer;
    public ParticleBuffer<ParticleColor> m_colorBuffer;
    ParticleGroup[] m_groupBuffer;
    ParticleBuffer<Object> m_userDataBuffer;
    int m_proxyCount;
    int m_proxyCapacity;
    Proxy[] m_proxyBuffer;
    public int m_contactCount;
    int m_contactCapacity;
    public ParticleContact[] m_contactBuffer;
    public int m_bodyContactCount;
    int m_bodyContactCapacity;
    public ParticleBodyContact[] m_bodyContactBuffer;
    int m_pairCount;
    int m_pairCapacity;
    Pair[] m_pairBuffer;
    int m_triadCount;
    int m_triadCapacity;
    Triad[] m_triadBuffer;
    int m_groupCount;
    ParticleGroup m_groupList;
    float m_pressureStrength;
    float m_dampingStrength;
    float m_elasticStrength;
    float m_springStrength;
    float m_viscousStrength;
    float m_surfaceTensionStrengthA;
    float m_surfaceTensionStrengthB;
    float m_powderStrength;
    float m_ejectionStrength;
    float m_colorMixingStrength;
    World m_world;
    private final AABB temp;
    private final DestroyParticlesInShapeCallback dpcallback;
    private final AABB temp2;
    private final Vec2 tempVec;
    private final Transform tempTransform;
    private final Transform tempTransform2;
    private CreateParticleGroupCallback createParticleGroupCallback;
    private final ParticleDef tempParticleDef;
    private final UpdateBodyContactsCallback ubccallback;
    private SolveCollisionCallback sccallback;
    private final Vec2 tempVec2;
    private final Rot tempRot;
    private final Transform tempXf;
    private final Transform tempXf2;
    private final NewIndices newIndices;
    
    static long computeTag(final float x, final float y) {
        return ((long)(y + 2048.0f) << 19) + ((long)(128.0f * x) + 262144L);
    }
    
    static long computeRelativeTag(final long tag, final int x, final int y) {
        return tag + (y << 19) + (x << 7);
    }
    
    static int limitCapacity(final int capacity, final int maxCount) {
        return (maxCount != 0 && capacity > maxCount) ? maxCount : capacity;
    }
    
    public ParticleSystem(final World world) {
        this.temp = new AABB();
        this.dpcallback = new DestroyParticlesInShapeCallback();
        this.temp2 = new AABB();
        this.tempVec = new Vec2();
        this.tempTransform = new Transform();
        this.tempTransform2 = new Transform();
        this.createParticleGroupCallback = new CreateParticleGroupCallback();
        this.tempParticleDef = new ParticleDef();
        this.ubccallback = new UpdateBodyContactsCallback();
        this.sccallback = new SolveCollisionCallback();
        this.tempVec2 = new Vec2();
        this.tempRot = new Rot();
        this.tempXf = new Transform();
        this.tempXf2 = new Transform();
        this.newIndices = new NewIndices();
        this.m_world = world;
        this.m_timestamp = 0;
        this.m_allParticleFlags = 0;
        this.m_allGroupFlags = 0;
        this.m_density = 1.0f;
        this.m_inverseDensity = 1.0f;
        this.m_gravityScale = 1.0f;
        this.m_particleDiameter = 1.0f;
        this.m_inverseDiameter = 1.0f;
        this.m_squaredDiameter = 1.0f;
        this.m_count = 0;
        this.m_internalAllocatedCapacity = 0;
        this.m_maxCount = 0;
        this.m_proxyCount = 0;
        this.m_proxyCapacity = 0;
        this.m_contactCount = 0;
        this.m_contactCapacity = 0;
        this.m_bodyContactCount = 0;
        this.m_bodyContactCapacity = 0;
        this.m_pairCount = 0;
        this.m_pairCapacity = 0;
        this.m_triadCount = 0;
        this.m_triadCapacity = 0;
        this.m_groupCount = 0;
        this.m_pressureStrength = 0.05f;
        this.m_dampingStrength = 1.0f;
        this.m_elasticStrength = 0.25f;
        this.m_springStrength = 0.25f;
        this.m_viscousStrength = 0.25f;
        this.m_surfaceTensionStrengthA = 0.1f;
        this.m_surfaceTensionStrengthB = 0.2f;
        this.m_powderStrength = 0.5f;
        this.m_ejectionStrength = 0.5f;
        this.m_colorMixingStrength = 0.5f;
        this.m_flagsBuffer = new ParticleBufferInt();
        this.m_positionBuffer = new ParticleBuffer<Vec2>(Vec2.class);
        this.m_velocityBuffer = new ParticleBuffer<Vec2>(Vec2.class);
        this.m_colorBuffer = new ParticleBuffer<ParticleColor>(ParticleColor.class);
        this.m_userDataBuffer = new ParticleBuffer<Object>(Object.class);
    }
    
    public int createParticle(final ParticleDef def) {
        if (this.m_count >= this.m_internalAllocatedCapacity) {
            int capacity = (this.m_count != 0) ? (2 * this.m_count) : 256;
            capacity = limitCapacity(capacity, this.m_maxCount);
            capacity = limitCapacity(capacity, this.m_flagsBuffer.userSuppliedCapacity);
            capacity = limitCapacity(capacity, this.m_positionBuffer.userSuppliedCapacity);
            capacity = limitCapacity(capacity, this.m_velocityBuffer.userSuppliedCapacity);
            capacity = limitCapacity(capacity, this.m_colorBuffer.userSuppliedCapacity);
            capacity = limitCapacity(capacity, this.m_userDataBuffer.userSuppliedCapacity);
            if (this.m_internalAllocatedCapacity < capacity) {
                this.m_flagsBuffer.data = reallocateBuffer(this.m_flagsBuffer, this.m_internalAllocatedCapacity, capacity, false);
                this.m_positionBuffer.data = (Vec2[])reallocateBuffer((ParticleBuffer<T>)this.m_positionBuffer, this.m_internalAllocatedCapacity, capacity, false);
                this.m_velocityBuffer.data = (Vec2[])reallocateBuffer((ParticleBuffer<T>)this.m_velocityBuffer, this.m_internalAllocatedCapacity, capacity, false);
                this.m_accumulationBuffer = BufferUtils.reallocateBuffer(this.m_accumulationBuffer, 0, this.m_internalAllocatedCapacity, capacity, false);
                this.m_accumulation2Buffer = BufferUtils.reallocateBuffer(Vec2.class, this.m_accumulation2Buffer, 0, this.m_internalAllocatedCapacity, capacity, true);
                this.m_depthBuffer = BufferUtils.reallocateBuffer(this.m_depthBuffer, 0, this.m_internalAllocatedCapacity, capacity, true);
                this.m_colorBuffer.data = (ParticleColor[])reallocateBuffer((ParticleBuffer<T>)this.m_colorBuffer, this.m_internalAllocatedCapacity, capacity, true);
                this.m_groupBuffer = BufferUtils.reallocateBuffer(ParticleGroup.class, this.m_groupBuffer, 0, this.m_internalAllocatedCapacity, capacity, false);
                this.m_userDataBuffer.data = reallocateBuffer((ParticleBuffer<T>)this.m_userDataBuffer, this.m_internalAllocatedCapacity, capacity, true);
                this.m_internalAllocatedCapacity = capacity;
            }
        }
        if (this.m_count >= this.m_internalAllocatedCapacity) {
            return -1;
        }
        final int index = this.m_count++;
        this.m_flagsBuffer.data[index] = def.flags;
        this.m_positionBuffer.data[index].set(def.position);
        this.m_velocityBuffer.data[index].set(def.velocity);
        this.m_groupBuffer[index] = null;
        if (this.m_depthBuffer != null) {
            this.m_depthBuffer[index] = 0.0f;
        }
        if (this.m_colorBuffer.data != null || def.color != null) {
            this.m_colorBuffer.data = (ParticleColor[])this.requestParticleBuffer((Class<T>)this.m_colorBuffer.dataClass, (T[])this.m_colorBuffer.data);
            this.m_colorBuffer.data[index].set(def.color);
        }
        if (this.m_userDataBuffer.data != null || def.userData != null) {
            (this.m_userDataBuffer.data = this.requestParticleBuffer((Class<T>)this.m_userDataBuffer.dataClass, (T[])this.m_userDataBuffer.data))[index] = def.userData;
        }
        if (this.m_proxyCount >= this.m_proxyCapacity) {
            final int oldCapacity = this.m_proxyCapacity;
            final int newCapacity = (this.m_proxyCount != 0) ? (2 * this.m_proxyCount) : 256;
            this.m_proxyBuffer = BufferUtils.reallocateBuffer(Proxy.class, this.m_proxyBuffer, oldCapacity, newCapacity);
            this.m_proxyCapacity = newCapacity;
        }
        return this.m_proxyBuffer[this.m_proxyCount++].index = index;
    }
    
    public void destroyParticle(final int index, final boolean callDestructionListener) {
        int flags = 2;
        if (callDestructionListener) {
            flags |= 0x200;
        }
        final int[] data = this.m_flagsBuffer.data;
        data[index] |= flags;
    }
    
    public int destroyParticlesInShape(final Shape shape, final Transform xf, final boolean callDestructionListener) {
        this.dpcallback.init(this, shape, xf, callDestructionListener);
        shape.computeAABB(this.temp, xf, 0);
        this.m_world.queryAABB(this.dpcallback, this.temp);
        return this.dpcallback.destroyed;
    }
    
    public void destroyParticlesInGroup(final ParticleGroup group, final boolean callDestructionListener) {
        for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            this.destroyParticle(i, callDestructionListener);
        }
    }
    
    public ParticleGroup createParticleGroup(final ParticleGroupDef groupDef) {
        final float stride = this.getParticleStride();
        final Transform identity = this.tempTransform;
        identity.setIdentity();
        final Transform transform = this.tempTransform2;
        transform.setIdentity();
        final int firstIndex = this.m_count;
        if (groupDef.shape != null) {
            final ParticleDef particleDef = this.tempParticleDef;
            particleDef.flags = groupDef.flags;
            particleDef.color = groupDef.color;
            particleDef.userData = groupDef.userData;
            final Shape shape = groupDef.shape;
            transform.set(groupDef.position, groupDef.angle);
            final AABB aabb = this.temp;
            for (int childCount = shape.getChildCount(), childIndex = 0; childIndex < childCount; ++childIndex) {
                if (childIndex == 0) {
                    shape.computeAABB(aabb, identity, childIndex);
                }
                else {
                    final AABB childAABB = this.temp2;
                    shape.computeAABB(childAABB, identity, childIndex);
                    aabb.combine(childAABB);
                }
            }
            final float upperBoundY = aabb.upperBound.y;
            final float upperBoundX = aabb.upperBound.x;
            for (float y = MathUtils.floor(aabb.lowerBound.y / stride) * stride; y < upperBoundY; y += stride) {
                for (float x = MathUtils.floor(aabb.lowerBound.x / stride) * stride; x < upperBoundX; x += stride) {
                    final Vec2 p = this.tempVec;
                    p.x = x;
                    p.y = y;
                    if (shape.testPoint(identity, p)) {
                        Transform.mulToOut(transform, p, p);
                        particleDef.position.x = p.x;
                        particleDef.position.y = p.y;
                        p.subLocal(groupDef.position);
                        Vec2.crossToOutUnsafe(groupDef.angularVelocity, p, particleDef.velocity);
                        particleDef.velocity.addLocal(groupDef.linearVelocity);
                        this.createParticle(particleDef);
                    }
                }
            }
        }
        final int lastIndex = this.m_count;
        final ParticleGroup group = new ParticleGroup();
        group.m_system = this;
        group.m_firstIndex = firstIndex;
        group.m_lastIndex = lastIndex;
        group.m_groupFlags = groupDef.groupFlags;
        group.m_strength = groupDef.strength;
        group.m_userData = groupDef.userData;
        group.m_transform.set(transform);
        group.m_destroyAutomatically = groupDef.destroyAutomatically;
        group.m_prev = null;
        group.m_next = this.m_groupList;
        if (this.m_groupList != null) {
            this.m_groupList.m_prev = group;
        }
        this.m_groupList = group;
        ++this.m_groupCount;
        for (int i = firstIndex; i < lastIndex; ++i) {
            this.m_groupBuffer[i] = group;
        }
        this.updateContacts(true);
        if ((groupDef.flags & 0x8) != 0x0) {
            for (int k = 0; k < this.m_contactCount; ++k) {
                final ParticleContact contact = this.m_contactBuffer[k];
                int a = contact.indexA;
                int b = contact.indexB;
                if (a > b) {
                    final int temp = a;
                    a = b;
                    b = temp;
                }
                if (firstIndex <= a && b < lastIndex) {
                    if (this.m_pairCount >= this.m_pairCapacity) {
                        final int oldCapacity = this.m_pairCapacity;
                        final int newCapacity = (this.m_pairCount != 0) ? (2 * this.m_pairCount) : 256;
                        this.m_pairBuffer = BufferUtils.reallocateBuffer(Pair.class, this.m_pairBuffer, oldCapacity, newCapacity);
                        this.m_pairCapacity = newCapacity;
                    }
                    final Pair pair = this.m_pairBuffer[this.m_pairCount];
                    pair.indexA = a;
                    pair.indexB = b;
                    pair.flags = contact.flags;
                    pair.strength = groupDef.strength;
                    pair.distance = MathUtils.distance(this.m_positionBuffer.data[a], this.m_positionBuffer.data[b]);
                    ++this.m_pairCount;
                }
            }
        }
        if ((groupDef.flags & 0x10) != 0x0) {
            final VoronoiDiagram diagram = new VoronoiDiagram(lastIndex - firstIndex);
            for (int j = firstIndex; j < lastIndex; ++j) {
                diagram.addGenerator(this.m_positionBuffer.data[j], j);
            }
            diagram.generate(stride / 2.0f);
            this.createParticleGroupCallback.system = this;
            this.createParticleGroupCallback.def = groupDef;
            this.createParticleGroupCallback.firstIndex = firstIndex;
            diagram.getNodes(this.createParticleGroupCallback);
        }
        if ((groupDef.groupFlags & 0x1) != 0x0) {
            this.computeDepthForGroup(group);
        }
        return group;
    }
    
    public void joinParticleGroups(final ParticleGroup groupA, final ParticleGroup groupB) {
        assert groupA != groupB;
        this.RotateBuffer(groupB.m_firstIndex, groupB.m_lastIndex, this.m_count);
        assert groupB.m_lastIndex == this.m_count;
        this.RotateBuffer(groupA.m_firstIndex, groupA.m_lastIndex, groupB.m_firstIndex);
        assert groupA.m_lastIndex == groupB.m_firstIndex;
        int particleFlags = 0;
        for (int i = groupA.m_firstIndex; i < groupB.m_lastIndex; ++i) {
            particleFlags |= this.m_flagsBuffer.data[i];
        }
        this.updateContacts(true);
        if ((particleFlags & 0x8) != 0x0) {
            for (int k = 0; k < this.m_contactCount; ++k) {
                final ParticleContact contact = this.m_contactBuffer[k];
                int a = contact.indexA;
                int b = contact.indexB;
                if (a > b) {
                    final int temp = a;
                    a = b;
                    b = temp;
                }
                if (groupA.m_firstIndex <= a && a < groupA.m_lastIndex && groupB.m_firstIndex <= b && b < groupB.m_lastIndex) {
                    if (this.m_pairCount >= this.m_pairCapacity) {
                        final int oldCapacity = this.m_pairCapacity;
                        final int newCapacity = (this.m_pairCount != 0) ? (2 * this.m_pairCount) : 256;
                        this.m_pairBuffer = BufferUtils.reallocateBuffer(Pair.class, this.m_pairBuffer, oldCapacity, newCapacity);
                        this.m_pairCapacity = newCapacity;
                    }
                    final Pair pair = this.m_pairBuffer[this.m_pairCount];
                    pair.indexA = a;
                    pair.indexB = b;
                    pair.flags = contact.flags;
                    pair.strength = MathUtils.min(groupA.m_strength, groupB.m_strength);
                    pair.distance = MathUtils.distance(this.m_positionBuffer.data[a], this.m_positionBuffer.data[b]);
                    ++this.m_pairCount;
                }
            }
        }
        if ((particleFlags & 0x10) != 0x0) {
            final VoronoiDiagram diagram = new VoronoiDiagram(groupB.m_lastIndex - groupA.m_firstIndex);
            for (int j = groupA.m_firstIndex; j < groupB.m_lastIndex; ++j) {
                if ((this.m_flagsBuffer.data[j] & 0x2) == 0x0) {
                    diagram.addGenerator(this.m_positionBuffer.data[j], j);
                }
            }
            diagram.generate(this.getParticleStride() / 2.0f);
            final JoinParticleGroupsCallback callback = new JoinParticleGroupsCallback();
            callback.system = this;
            callback.groupA = groupA;
            callback.groupB = groupB;
            diagram.getNodes(callback);
        }
        for (int i = groupB.m_firstIndex; i < groupB.m_lastIndex; ++i) {
            this.m_groupBuffer[i] = groupA;
        }
        final int groupFlags = groupA.m_groupFlags | groupB.m_groupFlags;
        groupA.m_groupFlags = groupFlags;
        groupA.m_lastIndex = groupB.m_lastIndex;
        groupB.m_firstIndex = groupB.m_lastIndex;
        this.destroyParticleGroup(groupB);
        if ((groupFlags & 0x1) != 0x0) {
            this.computeDepthForGroup(groupA);
        }
    }
    
    void destroyParticleGroup(final ParticleGroup group) {
        assert this.m_groupCount > 0;
        assert group != null;
        if (this.m_world.getParticleDestructionListener() != null) {
            this.m_world.getParticleDestructionListener().sayGoodbye(group);
        }
        for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            this.m_groupBuffer[i] = null;
        }
        if (group.m_prev != null) {
            group.m_prev.m_next = group.m_next;
        }
        if (group.m_next != null) {
            group.m_next.m_prev = group.m_prev;
        }
        if (group == this.m_groupList) {
            this.m_groupList = group.m_next;
        }
        --this.m_groupCount;
    }
    
    public void computeDepthForGroup(final ParticleGroup group) {
        for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            this.m_accumulationBuffer[i] = 0.0f;
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact = this.m_contactBuffer[k];
            final int a = contact.indexA;
            final int b = contact.indexB;
            if (a >= group.m_firstIndex && a < group.m_lastIndex && b >= group.m_firstIndex && b < group.m_lastIndex) {
                final float w = contact.weight;
                final float[] accumulationBuffer = this.m_accumulationBuffer;
                final int n = a;
                accumulationBuffer[n] += w;
                final float[] accumulationBuffer2 = this.m_accumulationBuffer;
                final int n2 = b;
                accumulationBuffer2[n2] += w;
            }
        }
        this.m_depthBuffer = this.requestParticleBuffer(this.m_depthBuffer);
        for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
            final float w2 = this.m_accumulationBuffer[i];
            this.m_depthBuffer[i] = ((w2 < 0.8f) ? 0.0f : Float.MAX_VALUE);
        }
        for (int interationCount = group.getParticleCount(), t = 0; t < interationCount; ++t) {
            boolean updated = false;
            for (int j = 0; j < this.m_contactCount; ++j) {
                final ParticleContact contact2 = this.m_contactBuffer[j];
                final int a2 = contact2.indexA;
                final int b2 = contact2.indexB;
                if (a2 >= group.m_firstIndex && a2 < group.m_lastIndex && b2 >= group.m_firstIndex && b2 < group.m_lastIndex) {
                    final float r = 1.0f - contact2.weight;
                    final float ap0 = this.m_depthBuffer[a2];
                    final float bp0 = this.m_depthBuffer[b2];
                    final float ap2 = bp0 + r;
                    final float bp2 = ap0 + r;
                    if (ap0 > ap2) {
                        this.m_depthBuffer[a2] = ap2;
                        updated = true;
                    }
                    if (bp0 > bp2) {
                        this.m_depthBuffer[b2] = bp2;
                        updated = true;
                    }
                }
            }
            if (!updated) {
                break;
            }
        }
        for (int l = group.m_firstIndex; l < group.m_lastIndex; ++l) {
            final float p = this.m_depthBuffer[l];
            if (p < Float.MAX_VALUE) {
                final float[] depthBuffer = this.m_depthBuffer;
                final int n3 = l;
                depthBuffer[n3] *= this.m_particleDiameter;
            }
            else {
                this.m_depthBuffer[l] = 0.0f;
            }
        }
    }
    
    public void addContact(final int a, final int b) {
        assert a != b;
        final Vec2 pa = this.m_positionBuffer.data[a];
        final Vec2 pb = this.m_positionBuffer.data[b];
        final float dx = pb.x - pa.x;
        final float dy = pb.y - pa.y;
        final float d2 = dx * dx + dy * dy;
        if (d2 < this.m_squaredDiameter) {
            if (this.m_contactCount >= this.m_contactCapacity) {
                final int oldCapacity = this.m_contactCapacity;
                final int newCapacity = (this.m_contactCount != 0) ? (2 * this.m_contactCount) : 256;
                this.m_contactBuffer = BufferUtils.reallocateBuffer(ParticleContact.class, this.m_contactBuffer, oldCapacity, newCapacity);
                this.m_contactCapacity = newCapacity;
            }
            final float invD = (d2 != 0.0f) ? MathUtils.sqrt(1.0f / d2) : Float.MAX_VALUE;
            final ParticleContact contact = this.m_contactBuffer[this.m_contactCount];
            contact.indexA = a;
            contact.indexB = b;
            contact.flags = (this.m_flagsBuffer.data[a] | this.m_flagsBuffer.data[b]);
            contact.weight = 1.0f - d2 * invD * this.m_inverseDiameter;
            contact.normal.x = invD * dx;
            contact.normal.y = invD * dy;
            ++this.m_contactCount;
        }
    }
    
    public void updateContacts(final boolean exceptZombie) {
        for (int p = 0; p < this.m_proxyCount; ++p) {
            final Proxy proxy = this.m_proxyBuffer[p];
            final int i = proxy.index;
            final Vec2 pos = this.m_positionBuffer.data[i];
            proxy.tag = computeTag(this.m_inverseDiameter * pos.x, this.m_inverseDiameter * pos.y);
        }
        Arrays.sort(this.m_proxyBuffer, 0, this.m_proxyCount);
        this.m_contactCount = 0;
        int c_index = 0;
        for (int j = 0; j < this.m_proxyCount; ++j) {
            final Proxy a = this.m_proxyBuffer[j];
            final long rightTag = computeRelativeTag(a.tag, 1, 0);
            for (int k = j + 1; k < this.m_proxyCount; ++k) {
                final Proxy b = this.m_proxyBuffer[k];
                if (rightTag < b.tag) {
                    break;
                }
                this.addContact(a.index, b.index);
            }
            final long bottomLeftTag = computeRelativeTag(a.tag, -1, 1);
            while (c_index < this.m_proxyCount) {
                final Proxy c = this.m_proxyBuffer[c_index];
                if (bottomLeftTag <= c.tag) {
                    break;
                }
                ++c_index;
            }
            final long bottomRightTag = computeRelativeTag(a.tag, 1, 1);
            for (int b_index = c_index; b_index < this.m_proxyCount; ++b_index) {
                final Proxy b2 = this.m_proxyBuffer[b_index];
                if (bottomRightTag < b2.tag) {
                    break;
                }
                this.addContact(a.index, b2.index);
            }
        }
        if (exceptZombie) {
            int l = this.m_contactCount;
            for (int i = 0; i < l; ++i) {
                if ((this.m_contactBuffer[i].flags & 0x2) != 0x0) {
                    --l;
                    final ParticleContact temp = this.m_contactBuffer[l];
                    this.m_contactBuffer[l] = this.m_contactBuffer[i];
                    this.m_contactBuffer[i] = temp;
                    --i;
                }
            }
            this.m_contactCount = l;
        }
    }
    
    public void updateBodyContacts() {
        final AABB aabb = this.temp;
        aabb.lowerBound.x = Float.MAX_VALUE;
        aabb.lowerBound.y = Float.MAX_VALUE;
        aabb.upperBound.x = -3.4028235E38f;
        aabb.upperBound.y = -3.4028235E38f;
        for (int i = 0; i < this.m_count; ++i) {
            final Vec2 p = this.m_positionBuffer.data[i];
            Vec2.minToOut(aabb.lowerBound, p, aabb.lowerBound);
            Vec2.maxToOut(aabb.upperBound, p, aabb.upperBound);
        }
        final Vec2 lowerBound = aabb.lowerBound;
        lowerBound.x -= this.m_particleDiameter;
        final Vec2 lowerBound2 = aabb.lowerBound;
        lowerBound2.y -= this.m_particleDiameter;
        final Vec2 upperBound = aabb.upperBound;
        upperBound.x += this.m_particleDiameter;
        final Vec2 upperBound2 = aabb.upperBound;
        upperBound2.y += this.m_particleDiameter;
        this.m_bodyContactCount = 0;
        this.ubccallback.system = this;
        this.m_world.queryAABB(this.ubccallback, aabb);
    }
    
    public void solveCollision(final TimeStep step) {
        final AABB aabb = this.temp;
        final Vec2 lowerBound = aabb.lowerBound;
        final Vec2 upperBound = aabb.upperBound;
        lowerBound.x = Float.MAX_VALUE;
        lowerBound.y = Float.MAX_VALUE;
        upperBound.x = -3.4028235E38f;
        upperBound.y = -3.4028235E38f;
        for (int i = 0; i < this.m_count; ++i) {
            final Vec2 v = this.m_velocityBuffer.data[i];
            final Vec2 p1 = this.m_positionBuffer.data[i];
            final float p1x = p1.x;
            final float p1y = p1.y;
            final float p2x = p1x + step.dt * v.x;
            final float p2y = p1y + step.dt * v.y;
            final float bx = (p1x < p2x) ? p1x : p2x;
            final float by = (p1y < p2y) ? p1y : p2y;
            lowerBound.x = ((lowerBound.x < bx) ? lowerBound.x : bx);
            lowerBound.y = ((lowerBound.y < by) ? lowerBound.y : by);
            final float b1x = (p1x > p2x) ? p1x : p2x;
            final float b1y = (p1y > p2y) ? p1y : p2y;
            upperBound.x = ((upperBound.x > b1x) ? upperBound.x : b1x);
            upperBound.y = ((upperBound.y > b1y) ? upperBound.y : b1y);
        }
        this.sccallback.step = step;
        this.sccallback.system = this;
        this.m_world.queryAABB(this.sccallback, aabb);
    }
    
    public void solve(final TimeStep step) {
        ++this.m_timestamp;
        if (this.m_count == 0) {
            return;
        }
        this.m_allParticleFlags = 0;
        for (int i = 0; i < this.m_count; ++i) {
            this.m_allParticleFlags |= this.m_flagsBuffer.data[i];
        }
        if ((this.m_allParticleFlags & 0x2) != 0x0) {
            this.solveZombie();
        }
        if (this.m_count == 0) {
            return;
        }
        this.m_allGroupFlags = 0;
        for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            this.m_allGroupFlags |= group.m_groupFlags;
        }
        final float gravityx = step.dt * this.m_gravityScale * this.m_world.getGravity().x;
        final float gravityy = step.dt * this.m_gravityScale * this.m_world.getGravity().y;
        final float criticalVelocytySquared = this.getCriticalVelocitySquared(step);
        for (int j = 0; j < this.m_count; ++j) {
            final Vec2 vec2;
            final Vec2 v = vec2 = this.m_velocityBuffer.data[j];
            vec2.x += gravityx;
            final Vec2 vec3 = v;
            vec3.y += gravityy;
            final float v2 = v.x * v.x + v.y * v.y;
            if (v2 > criticalVelocytySquared) {
                final float a = (v2 == 0.0f) ? Float.MAX_VALUE : MathUtils.sqrt(criticalVelocytySquared / v2);
                final Vec2 vec4 = v;
                vec4.x *= a;
                final Vec2 vec5 = v;
                vec5.y *= a;
            }
        }
        this.solveCollision(step);
        if ((this.m_allGroupFlags & 0x2) != 0x0) {
            this.solveRigid(step);
        }
        if ((this.m_allParticleFlags & 0x4) != 0x0) {
            this.solveWall(step);
        }
        for (int j = 0; j < this.m_count; ++j) {
            final Vec2 pos = this.m_positionBuffer.data[j];
            final Vec2 vel = this.m_velocityBuffer.data[j];
            final Vec2 vec6 = pos;
            vec6.x += step.dt * vel.x;
            final Vec2 vec7 = pos;
            vec7.y += step.dt * vel.y;
        }
        this.updateBodyContacts();
        this.updateContacts(false);
        if ((this.m_allParticleFlags & 0x20) != 0x0) {
            this.solveViscous(step);
        }
        if ((this.m_allParticleFlags & 0x40) != 0x0) {
            this.solvePowder(step);
        }
        if ((this.m_allParticleFlags & 0x80) != 0x0) {
            this.solveTensile(step);
        }
        if ((this.m_allParticleFlags & 0x10) != 0x0) {
            this.solveElastic(step);
        }
        if ((this.m_allParticleFlags & 0x8) != 0x0) {
            this.solveSpring(step);
        }
        if ((this.m_allGroupFlags & 0x1) != 0x0) {
            this.solveSolid(step);
        }
        if ((this.m_allParticleFlags & 0x100) != 0x0) {
            this.solveColorMixing(step);
        }
        this.solvePressure(step);
        this.solveDamping(step);
    }
    
    void solvePressure(final TimeStep step) {
        for (int i = 0; i < this.m_count; ++i) {
            this.m_accumulationBuffer[i] = 0.0f;
        }
        for (int k = 0; k < this.m_bodyContactCount; ++k) {
            final ParticleBodyContact contact = this.m_bodyContactBuffer[k];
            final int a = contact.index;
            final float w = contact.weight;
            final float[] accumulationBuffer = this.m_accumulationBuffer;
            final int n3 = a;
            accumulationBuffer[n3] += w;
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact2 = this.m_contactBuffer[k];
            final int a = contact2.indexA;
            final int b = contact2.indexB;
            final float w2 = contact2.weight;
            final float[] accumulationBuffer2 = this.m_accumulationBuffer;
            final int n4 = a;
            accumulationBuffer2[n4] += w2;
            final float[] accumulationBuffer3 = this.m_accumulationBuffer;
            final int n5 = b;
            accumulationBuffer3[n5] += w2;
        }
        if ((this.m_allParticleFlags & 0x40) != 0x0) {
            for (int i = 0; i < this.m_count; ++i) {
                if ((this.m_flagsBuffer.data[i] & 0x40) != 0x0) {
                    this.m_accumulationBuffer[i] = 0.0f;
                }
            }
        }
        final float pressurePerWeight = this.m_pressureStrength * this.getCriticalPressure(step);
        for (int j = 0; j < this.m_count; ++j) {
            final float w3 = this.m_accumulationBuffer[j];
            final float h = pressurePerWeight * MathUtils.max(0.0f, MathUtils.min(w3, 5.0f) - 1.0f);
            this.m_accumulationBuffer[j] = h;
        }
        final float velocityPerPressure = step.dt / (this.m_density * this.m_particleDiameter);
        for (int l = 0; l < this.m_bodyContactCount; ++l) {
            final ParticleBodyContact contact3 = this.m_bodyContactBuffer[l];
            final int a2 = contact3.index;
            final Body b2 = contact3.body;
            final float w4 = contact3.weight;
            final float m = contact3.mass;
            final Vec2 n = contact3.normal;
            final Vec2 p = this.m_positionBuffer.data[a2];
            final float h2 = this.m_accumulationBuffer[a2] + pressurePerWeight * w4;
            final Vec2 f = this.tempVec;
            final float coef = velocityPerPressure * w4 * m * h2;
            f.x = coef * n.x;
            f.y = coef * n.y;
            final Vec2 velData = this.m_velocityBuffer.data[a2];
            final float particleInvMass = this.getParticleInvMass();
            final Vec2 vec2 = velData;
            vec2.x -= particleInvMass * f.x;
            final Vec2 vec3 = velData;
            vec3.y -= particleInvMass * f.y;
            b2.applyLinearImpulse(f, p, true);
        }
        for (int l = 0; l < this.m_contactCount; ++l) {
            final ParticleContact contact4 = this.m_contactBuffer[l];
            final int a2 = contact4.indexA;
            final int b3 = contact4.indexB;
            final float w4 = contact4.weight;
            final Vec2 n2 = contact4.normal;
            final float h3 = this.m_accumulationBuffer[a2] + this.m_accumulationBuffer[b3];
            final float fx = velocityPerPressure * w4 * h3 * n2.x;
            final float fy = velocityPerPressure * w4 * h3 * n2.y;
            final Vec2 velDataA = this.m_velocityBuffer.data[a2];
            final Vec2 velDataB = this.m_velocityBuffer.data[b3];
            final Vec2 vec4 = velDataA;
            vec4.x -= fx;
            final Vec2 vec5 = velDataA;
            vec5.y -= fy;
            final Vec2 vec6 = velDataB;
            vec6.x += fx;
            final Vec2 vec7 = velDataB;
            vec7.y += fy;
        }
    }
    
    void solveDamping(final TimeStep step) {
        final float damping = this.m_dampingStrength;
        for (int k = 0; k < this.m_bodyContactCount; ++k) {
            final ParticleBodyContact contact = this.m_bodyContactBuffer[k];
            final int a = contact.index;
            final Body b = contact.body;
            final float w = contact.weight;
            final float m = contact.mass;
            final Vec2 n = contact.normal;
            final Vec2 p = this.m_positionBuffer.data[a];
            final float tempX = p.x - b.m_sweep.c.x;
            final float tempY = p.y - b.m_sweep.c.y;
            final Vec2 velA = this.m_velocityBuffer.data[a];
            final float vx = -b.m_angularVelocity * tempY + b.m_linearVelocity.x - velA.x;
            final float vy = b.m_angularVelocity * tempX + b.m_linearVelocity.y - velA.y;
            final float vn = vx * n.x + vy * n.y;
            if (vn < 0.0f) {
                final Vec2 f = this.tempVec;
                f.x = damping * w * m * vn * n.x;
                f.y = damping * w * m * vn * n.y;
                final float invMass = this.getParticleInvMass();
                final Vec2 vec2 = velA;
                vec2.x += invMass * f.x;
                final Vec2 vec3 = velA;
                vec3.y += invMass * f.y;
                f.x = -f.x;
                f.y = -f.y;
                b.applyLinearImpulse(f, p, true);
            }
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact2 = this.m_contactBuffer[k];
            final int a = contact2.indexA;
            final int b2 = contact2.indexB;
            final float w = contact2.weight;
            final Vec2 n2 = contact2.normal;
            final Vec2 velA2 = this.m_velocityBuffer.data[a];
            final Vec2 velB = this.m_velocityBuffer.data[b2];
            final float vx2 = velB.x - velA2.x;
            final float vy2 = velB.y - velA2.y;
            final float vn2 = vx2 * n2.x + vy2 * n2.y;
            if (vn2 < 0.0f) {
                final float fx = damping * w * vn2 * n2.x;
                final float fy = damping * w * vn2 * n2.y;
                final Vec2 vec4 = velA2;
                vec4.x += fx;
                final Vec2 vec5 = velA2;
                vec5.y += fy;
                final Vec2 vec6 = velB;
                vec6.x -= fx;
                final Vec2 vec7 = velB;
                vec7.y -= fy;
            }
        }
    }
    
    public void solveWall(final TimeStep step) {
        for (int i = 0; i < this.m_count; ++i) {
            if ((this.m_flagsBuffer.data[i] & 0x4) != 0x0) {
                final Vec2 r = this.m_velocityBuffer.data[i];
                r.x = 0.0f;
                r.y = 0.0f;
            }
        }
    }
    
    void solveRigid(final TimeStep step) {
        for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            if ((group.m_groupFlags & 0x2) != 0x0) {
                group.updateStatistics();
                final Vec2 temp = this.tempVec;
                final Vec2 cross = this.tempVec2;
                final Rot rotation = this.tempRot;
                rotation.set(step.dt * group.m_angularVelocity);
                Rot.mulToOutUnsafe(rotation, group.m_center, cross);
                temp.set(group.m_linearVelocity).mulLocal(step.dt).addLocal(group.m_center).subLocal(cross);
                this.tempXf.p.set(temp);
                this.tempXf.q.set(rotation);
                Transform.mulToOut(this.tempXf, group.m_transform, group.m_transform);
                final Transform velocityTransform = this.tempXf2;
                velocityTransform.p.x = step.inv_dt * this.tempXf.p.x;
                velocityTransform.p.y = step.inv_dt * this.tempXf.p.y;
                velocityTransform.q.s = step.inv_dt * this.tempXf.q.s;
                velocityTransform.q.c = step.inv_dt * (this.tempXf.q.c - 1.0f);
                for (int i = group.m_firstIndex; i < group.m_lastIndex; ++i) {
                    Transform.mulToOutUnsafe(velocityTransform, this.m_positionBuffer.data[i], this.m_velocityBuffer.data[i]);
                }
            }
        }
    }
    
    void solveElastic(final TimeStep step) {
        final float elasticStrength = step.inv_dt * this.m_elasticStrength;
        for (int k = 0; k < this.m_triadCount; ++k) {
            final Triad triad = this.m_triadBuffer[k];
            if ((triad.flags & 0x10) != 0x0) {
                final int a = triad.indexA;
                final int b = triad.indexB;
                final int c = triad.indexC;
                final Vec2 oa = triad.pa;
                final Vec2 ob = triad.pb;
                final Vec2 oc = triad.pc;
                final Vec2 pa = this.m_positionBuffer.data[a];
                final Vec2 pb = this.m_positionBuffer.data[b];
                final Vec2 pc = this.m_positionBuffer.data[c];
                final float px = 0.33333334f * (pa.x + pb.x + pc.x);
                final float py = 0.33333334f * (pa.y + pb.y + pc.y);
                float rs = Vec2.cross(oa, pa) + Vec2.cross(ob, pb) + Vec2.cross(oc, pc);
                float rc = Vec2.dot(oa, pa) + Vec2.dot(ob, pb) + Vec2.dot(oc, pc);
                final float r2 = rs * rs + rc * rc;
                final float invR = (r2 == 0.0f) ? Float.MAX_VALUE : MathUtils.sqrt(1.0f / r2);
                rs *= invR;
                rc *= invR;
                final float strength = elasticStrength * triad.strength;
                final float roax = rc * oa.x - rs * oa.y;
                final float roay = rs * oa.x + rc * oa.y;
                final float robx = rc * ob.x - rs * ob.y;
                final float roby = rs * ob.x + rc * ob.y;
                final float rocx = rc * oc.x - rs * oc.y;
                final float rocy = rs * oc.x + rc * oc.y;
                final Vec2 va = this.m_velocityBuffer.data[a];
                final Vec2 vb = this.m_velocityBuffer.data[b];
                final Vec2 vc = this.m_velocityBuffer.data[c];
                final Vec2 vec2 = va;
                vec2.x += strength * (roax - (pa.x - px));
                final Vec2 vec3 = va;
                vec3.y += strength * (roay - (pa.y - py));
                final Vec2 vec4 = vb;
                vec4.x += strength * (robx - (pb.x - px));
                final Vec2 vec5 = vb;
                vec5.y += strength * (roby - (pb.y - py));
                final Vec2 vec6 = vc;
                vec6.x += strength * (rocx - (pc.x - px));
                final Vec2 vec7 = vc;
                vec7.y += strength * (rocy - (pc.y - py));
            }
        }
    }
    
    void solveSpring(final TimeStep step) {
        final float springStrength = step.inv_dt * this.m_springStrength;
        for (int k = 0; k < this.m_pairCount; ++k) {
            final Pair pair = this.m_pairBuffer[k];
            if ((pair.flags & 0x8) != 0x0) {
                final int a = pair.indexA;
                final int b = pair.indexB;
                final Vec2 pa = this.m_positionBuffer.data[a];
                final Vec2 pb = this.m_positionBuffer.data[b];
                final float dx = pb.x - pa.x;
                final float dy = pb.y - pa.y;
                final float r0 = pair.distance;
                float r2 = MathUtils.sqrt(dx * dx + dy * dy);
                if (r2 == 0.0f) {
                    r2 = Float.MAX_VALUE;
                }
                final float strength = springStrength * pair.strength;
                final float fx = strength * (r0 - r2) / r2 * dx;
                final float fy = strength * (r0 - r2) / r2 * dy;
                final Vec2 va = this.m_velocityBuffer.data[a];
                final Vec2 vb = this.m_velocityBuffer.data[b];
                final Vec2 vec2 = va;
                vec2.x -= fx;
                final Vec2 vec3 = va;
                vec3.y -= fy;
                final Vec2 vec4 = vb;
                vec4.x += fx;
                final Vec2 vec5 = vb;
                vec5.y += fy;
            }
        }
    }
    
    void solveTensile(final TimeStep step) {
        this.m_accumulation2Buffer = this.requestParticleBuffer(Vec2.class, this.m_accumulation2Buffer);
        for (int i = 0; i < this.m_count; ++i) {
            this.m_accumulationBuffer[i] = 0.0f;
            this.m_accumulation2Buffer[i].setZero();
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact = this.m_contactBuffer[k];
            if ((contact.flags & 0x80) != 0x0) {
                final int a = contact.indexA;
                final int b = contact.indexB;
                final float w = contact.weight;
                final Vec2 n = contact.normal;
                final float[] accumulationBuffer = this.m_accumulationBuffer;
                final int n3 = a;
                accumulationBuffer[n3] += w;
                final float[] accumulationBuffer2 = this.m_accumulationBuffer;
                final int n4 = b;
                accumulationBuffer2[n4] += w;
                final Vec2 a2A = this.m_accumulation2Buffer[a];
                final Vec2 a2B = this.m_accumulation2Buffer[b];
                final float inter = (1.0f - w) * w;
                final Vec2 vec2 = a2A;
                vec2.x -= inter * n.x;
                final Vec2 vec3 = a2A;
                vec3.y -= inter * n.y;
                final Vec2 vec4 = a2B;
                vec4.x += inter * n.x;
                final Vec2 vec5 = a2B;
                vec5.y += inter * n.y;
            }
        }
        final float strengthA = this.m_surfaceTensionStrengthA * this.getCriticalVelocity(step);
        final float strengthB = this.m_surfaceTensionStrengthB * this.getCriticalVelocity(step);
        for (int j = 0; j < this.m_contactCount; ++j) {
            final ParticleContact contact2 = this.m_contactBuffer[j];
            if ((contact2.flags & 0x80) != 0x0) {
                final int a2 = contact2.indexA;
                final int b2 = contact2.indexB;
                final float w2 = contact2.weight;
                final Vec2 n2 = contact2.normal;
                final Vec2 a2A2 = this.m_accumulation2Buffer[a2];
                final Vec2 a2B2 = this.m_accumulation2Buffer[b2];
                final float h = this.m_accumulationBuffer[a2] + this.m_accumulationBuffer[b2];
                final float sx = a2B2.x - a2A2.x;
                final float sy = a2B2.y - a2A2.y;
                final float fn = (strengthA * (h - 2.0f) + strengthB * (sx * n2.x + sy * n2.y)) * w2;
                final float fx = fn * n2.x;
                final float fy = fn * n2.y;
                final Vec2 va = this.m_velocityBuffer.data[a2];
                final Vec2 vb = this.m_velocityBuffer.data[b2];
                final Vec2 vec6 = va;
                vec6.x -= fx;
                final Vec2 vec7 = va;
                vec7.y -= fy;
                final Vec2 vec8 = vb;
                vec8.x += fx;
                final Vec2 vec9 = vb;
                vec9.y += fy;
            }
        }
    }
    
    void solveViscous(final TimeStep step) {
        final float viscousStrength = this.m_viscousStrength;
        for (int k = 0; k < this.m_bodyContactCount; ++k) {
            final ParticleBodyContact contact = this.m_bodyContactBuffer[k];
            final int a = contact.index;
            if ((this.m_flagsBuffer.data[a] & 0x20) != 0x0) {
                final Body b = contact.body;
                final float w = contact.weight;
                final float m = contact.mass;
                final Vec2 p = this.m_positionBuffer.data[a];
                final Vec2 va = this.m_velocityBuffer.data[a];
                final float tempX = p.x - b.m_sweep.c.x;
                final float tempY = p.y - b.m_sweep.c.y;
                final float vx = -b.m_angularVelocity * tempY + b.m_linearVelocity.x - va.x;
                final float vy = b.m_angularVelocity * tempX + b.m_linearVelocity.y - va.y;
                final Vec2 f = this.tempVec;
                final float pInvMass = this.getParticleInvMass();
                f.x = viscousStrength * m * w * vx;
                f.y = viscousStrength * m * w * vy;
                final Vec2 vec2 = va;
                vec2.x += pInvMass * f.x;
                final Vec2 vec3 = va;
                vec3.y += pInvMass * f.y;
                f.x = -f.x;
                f.y = -f.y;
                b.applyLinearImpulse(f, p, true);
            }
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact2 = this.m_contactBuffer[k];
            if ((contact2.flags & 0x20) != 0x0) {
                final int a = contact2.indexA;
                final int b2 = contact2.indexB;
                final float w = contact2.weight;
                final Vec2 va2 = this.m_velocityBuffer.data[a];
                final Vec2 vb = this.m_velocityBuffer.data[b2];
                final float vx2 = vb.x - va2.x;
                final float vy2 = vb.y - va2.y;
                final float fx = viscousStrength * w * vx2;
                final float fy = viscousStrength * w * vy2;
                final Vec2 vec4 = va2;
                vec4.x += fx;
                final Vec2 vec5 = va2;
                vec5.y += fy;
                final Vec2 vec6 = vb;
                vec6.x -= fx;
                final Vec2 vec7 = vb;
                vec7.y -= fy;
            }
        }
    }
    
    void solvePowder(final TimeStep step) {
        final float powderStrength = this.m_powderStrength * this.getCriticalVelocity(step);
        final float minWeight = 0.25f;
        for (int k = 0; k < this.m_bodyContactCount; ++k) {
            final ParticleBodyContact contact = this.m_bodyContactBuffer[k];
            final int a = contact.index;
            if ((this.m_flagsBuffer.data[a] & 0x40) != 0x0) {
                final float w = contact.weight;
                if (w > minWeight) {
                    final Body b = contact.body;
                    final float m = contact.mass;
                    final Vec2 p = this.m_positionBuffer.data[a];
                    final Vec2 n = contact.normal;
                    final Vec2 f = this.tempVec;
                    final Vec2 va = this.m_velocityBuffer.data[a];
                    final float inter = powderStrength * m * (w - minWeight);
                    final float pInvMass = this.getParticleInvMass();
                    f.x = inter * n.x;
                    f.y = inter * n.y;
                    final Vec2 vec2 = va;
                    vec2.x -= pInvMass * f.x;
                    final Vec2 vec3 = va;
                    vec3.y -= pInvMass * f.y;
                    b.applyLinearImpulse(f, p, true);
                }
            }
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact2 = this.m_contactBuffer[k];
            if ((contact2.flags & 0x40) != 0x0) {
                final float w2 = contact2.weight;
                if (w2 > minWeight) {
                    final int a2 = contact2.indexA;
                    final int b2 = contact2.indexB;
                    final Vec2 n2 = contact2.normal;
                    final Vec2 va2 = this.m_velocityBuffer.data[a2];
                    final Vec2 vb = this.m_velocityBuffer.data[b2];
                    final float inter2 = powderStrength * (w2 - minWeight);
                    final float fx = inter2 * n2.x;
                    final float fy = inter2 * n2.y;
                    final Vec2 vec4 = va2;
                    vec4.x -= fx;
                    final Vec2 vec5 = va2;
                    vec5.y -= fy;
                    final Vec2 vec6 = vb;
                    vec6.x += fx;
                    final Vec2 vec7 = vb;
                    vec7.y += fy;
                }
            }
        }
    }
    
    void solveSolid(final TimeStep step) {
        this.m_depthBuffer = this.requestParticleBuffer(this.m_depthBuffer);
        final float ejectionStrength = step.inv_dt * this.m_ejectionStrength;
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact = this.m_contactBuffer[k];
            final int a = contact.indexA;
            final int b = contact.indexB;
            if (this.m_groupBuffer[a] != this.m_groupBuffer[b]) {
                final float w = contact.weight;
                final Vec2 n = contact.normal;
                final float h = this.m_depthBuffer[a] + this.m_depthBuffer[b];
                final Vec2 va = this.m_velocityBuffer.data[a];
                final Vec2 vb = this.m_velocityBuffer.data[b];
                final float inter = ejectionStrength * h * w;
                final float fx = inter * n.x;
                final float fy = inter * n.y;
                final Vec2 vec2 = va;
                vec2.x -= fx;
                final Vec2 vec3 = va;
                vec3.y -= fy;
                final Vec2 vec4 = vb;
                vec4.x += fx;
                final Vec2 vec5 = vb;
                vec5.y += fy;
            }
        }
    }
    
    void solveColorMixing(final TimeStep step) {
        this.m_colorBuffer.data = (ParticleColor[])this.requestParticleBuffer((Class<T>)ParticleColor.class, (T[])this.m_colorBuffer.data);
        final int colorMixing256 = (int)(256.0f * this.m_colorMixingStrength);
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact = this.m_contactBuffer[k];
            final int a = contact.indexA;
            final int b = contact.indexB;
            if ((this.m_flagsBuffer.data[a] & this.m_flagsBuffer.data[b] & 0x100) != 0x0) {
                final ParticleColor colorA = this.m_colorBuffer.data[a];
                final ParticleColor colorB = this.m_colorBuffer.data[b];
                final int dr = colorMixing256 * (colorB.r - colorA.r) >> 8;
                final int dg = colorMixing256 * (colorB.g - colorA.g) >> 8;
                final int db = colorMixing256 * (colorB.b - colorA.b) >> 8;
                final int da = colorMixing256 * (colorB.a - colorA.a) >> 8;
                final ParticleColor particleColor = colorA;
                particleColor.r += (byte)dr;
                final ParticleColor particleColor2 = colorA;
                particleColor2.g += (byte)dg;
                final ParticleColor particleColor3 = colorA;
                particleColor3.b += (byte)db;
                final ParticleColor particleColor4 = colorA;
                particleColor4.a += (byte)da;
                final ParticleColor particleColor5 = colorB;
                particleColor5.r -= (byte)dr;
                final ParticleColor particleColor6 = colorB;
                particleColor6.g -= (byte)dg;
                final ParticleColor particleColor7 = colorB;
                particleColor7.b -= (byte)db;
                final ParticleColor particleColor8 = colorB;
                particleColor8.a -= (byte)da;
            }
        }
    }
    
    void solveZombie() {
        int newCount = 0;
        final int[] newIndices = new int[this.m_count];
        for (int i = 0; i < this.m_count; ++i) {
            final int flags = this.m_flagsBuffer.data[i];
            if ((flags & 0x2) != 0x0) {
                final ParticleDestructionListener destructionListener = this.m_world.getParticleDestructionListener();
                if ((flags & 0x200) != 0x0 && destructionListener != null) {
                    destructionListener.sayGoodbye(i);
                }
                newIndices[i] = -1;
            }
            else {
                if (i != (newIndices[i] = newCount)) {
                    this.m_flagsBuffer.data[newCount] = this.m_flagsBuffer.data[i];
                    this.m_positionBuffer.data[newCount].set(this.m_positionBuffer.data[i]);
                    this.m_velocityBuffer.data[newCount].set(this.m_velocityBuffer.data[i]);
                    this.m_groupBuffer[newCount] = this.m_groupBuffer[i];
                    if (this.m_depthBuffer != null) {
                        this.m_depthBuffer[newCount] = this.m_depthBuffer[i];
                    }
                    if (this.m_colorBuffer.data != null) {
                        this.m_colorBuffer.data[newCount].set(this.m_colorBuffer.data[i]);
                    }
                    if (this.m_userDataBuffer.data != null) {
                        this.m_userDataBuffer.data[newCount] = this.m_userDataBuffer.data[i];
                    }
                }
                ++newCount;
            }
        }
        for (int k = 0; k < this.m_proxyCount; ++k) {
            final Proxy proxy = this.m_proxyBuffer[k];
            proxy.index = newIndices[proxy.index];
        }
        int j = this.m_proxyCount;
        for (int l = 0; l < j; ++l) {
            if (Test.IsProxyInvalid(this.m_proxyBuffer[l])) {
                --j;
                final Proxy temp = this.m_proxyBuffer[j];
                this.m_proxyBuffer[j] = this.m_proxyBuffer[l];
                this.m_proxyBuffer[l] = temp;
                --l;
            }
        }
        this.m_proxyCount = j;
        for (int m = 0; m < this.m_contactCount; ++m) {
            final ParticleContact contact = this.m_contactBuffer[m];
            contact.indexA = newIndices[contact.indexA];
            contact.indexB = newIndices[contact.indexB];
        }
        j = this.m_contactCount;
        for (int l = 0; l < j; ++l) {
            if (Test.IsContactInvalid(this.m_contactBuffer[l])) {
                --j;
                final ParticleContact temp2 = this.m_contactBuffer[j];
                this.m_contactBuffer[j] = this.m_contactBuffer[l];
                this.m_contactBuffer[l] = temp2;
                --l;
            }
        }
        this.m_contactCount = j;
        for (int m = 0; m < this.m_bodyContactCount; ++m) {
            final ParticleBodyContact contact2 = this.m_bodyContactBuffer[m];
            contact2.index = newIndices[contact2.index];
        }
        j = this.m_bodyContactCount;
        for (int l = 0; l < j; ++l) {
            if (Test.IsBodyContactInvalid(this.m_bodyContactBuffer[l])) {
                --j;
                final ParticleBodyContact temp3 = this.m_bodyContactBuffer[j];
                this.m_bodyContactBuffer[j] = this.m_bodyContactBuffer[l];
                this.m_bodyContactBuffer[l] = temp3;
                --l;
            }
        }
        this.m_bodyContactCount = j;
        for (int m = 0; m < this.m_pairCount; ++m) {
            final Pair pair = this.m_pairBuffer[m];
            pair.indexA = newIndices[pair.indexA];
            pair.indexB = newIndices[pair.indexB];
        }
        j = this.m_pairCount;
        for (int l = 0; l < j; ++l) {
            if (Test.IsPairInvalid(this.m_pairBuffer[l])) {
                --j;
                final Pair temp4 = this.m_pairBuffer[j];
                this.m_pairBuffer[j] = this.m_pairBuffer[l];
                this.m_pairBuffer[l] = temp4;
                --l;
            }
        }
        this.m_pairCount = j;
        for (int m = 0; m < this.m_triadCount; ++m) {
            final Triad triad = this.m_triadBuffer[m];
            triad.indexA = newIndices[triad.indexA];
            triad.indexB = newIndices[triad.indexB];
            triad.indexC = newIndices[triad.indexC];
        }
        j = this.m_triadCount;
        for (int l = 0; l < j; ++l) {
            if (Test.IsTriadInvalid(this.m_triadBuffer[l])) {
                --j;
                final Triad temp5 = this.m_triadBuffer[j];
                this.m_triadBuffer[j] = this.m_triadBuffer[l];
                this.m_triadBuffer[l] = temp5;
                --l;
            }
        }
        this.m_triadCount = j;
        for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            int firstIndex = newCount;
            int lastIndex = 0;
            boolean modified = false;
            for (int i2 = group.m_firstIndex; i2 < group.m_lastIndex; ++i2) {
                j = newIndices[i2];
                if (j >= 0) {
                    firstIndex = MathUtils.min(firstIndex, j);
                    lastIndex = MathUtils.max(lastIndex, j + 1);
                }
                else {
                    modified = true;
                }
            }
            if (firstIndex < lastIndex) {
                group.m_firstIndex = firstIndex;
                group.m_lastIndex = lastIndex;
                if (modified && (group.m_groupFlags & 0x2) != 0x0) {
                    group.m_toBeSplit = true;
                }
            }
            else {
                group.m_firstIndex = 0;
                group.m_lastIndex = 0;
                if (group.m_destroyAutomatically) {
                    group.m_toBeDestroyed = true;
                }
            }
        }
        this.m_count = newCount;
        ParticleGroup next;
        for (ParticleGroup group = this.m_groupList; group != null; group = next) {
            next = group.getNext();
            if (group.m_toBeDestroyed) {
                this.destroyParticleGroup(group);
            }
            else if (group.m_toBeSplit) {}
        }
    }
    
    void RotateBuffer(final int start, final int mid, final int end) {
        if (start == mid || mid == end) {
            return;
        }
        this.newIndices.start = start;
        this.newIndices.mid = mid;
        this.newIndices.end = end;
        BufferUtils.rotate(this.m_flagsBuffer.data, start, mid, end);
        BufferUtils.rotate(this.m_positionBuffer.data, start, mid, end);
        BufferUtils.rotate(this.m_velocityBuffer.data, start, mid, end);
        BufferUtils.rotate(this.m_groupBuffer, start, mid, end);
        if (this.m_depthBuffer != null) {
            BufferUtils.rotate(this.m_depthBuffer, start, mid, end);
        }
        if (this.m_colorBuffer.data != null) {
            BufferUtils.rotate(this.m_colorBuffer.data, start, mid, end);
        }
        if (this.m_userDataBuffer.data != null) {
            BufferUtils.rotate(this.m_userDataBuffer.data, start, mid, end);
        }
        for (int k = 0; k < this.m_proxyCount; ++k) {
            final Proxy proxy = this.m_proxyBuffer[k];
            proxy.index = this.newIndices.getIndex(proxy.index);
        }
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact = this.m_contactBuffer[k];
            contact.indexA = this.newIndices.getIndex(contact.indexA);
            contact.indexB = this.newIndices.getIndex(contact.indexB);
        }
        for (int k = 0; k < this.m_bodyContactCount; ++k) {
            final ParticleBodyContact contact2 = this.m_bodyContactBuffer[k];
            contact2.index = this.newIndices.getIndex(contact2.index);
        }
        for (int k = 0; k < this.m_pairCount; ++k) {
            final Pair pair = this.m_pairBuffer[k];
            pair.indexA = this.newIndices.getIndex(pair.indexA);
            pair.indexB = this.newIndices.getIndex(pair.indexB);
        }
        for (int k = 0; k < this.m_triadCount; ++k) {
            final Triad triad = this.m_triadBuffer[k];
            triad.indexA = this.newIndices.getIndex(triad.indexA);
            triad.indexB = this.newIndices.getIndex(triad.indexB);
            triad.indexC = this.newIndices.getIndex(triad.indexC);
        }
        for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            group.m_firstIndex = this.newIndices.getIndex(group.m_firstIndex);
            group.m_lastIndex = this.newIndices.getIndex(group.m_lastIndex - 1) + 1;
        }
    }
    
    public void setParticleRadius(final float radius) {
        this.m_particleDiameter = 2.0f * radius;
        this.m_squaredDiameter = this.m_particleDiameter * this.m_particleDiameter;
        this.m_inverseDiameter = 1.0f / this.m_particleDiameter;
    }
    
    public void setParticleDensity(final float density) {
        this.m_density = density;
        this.m_inverseDensity = 1.0f / this.m_density;
    }
    
    public float getParticleDensity() {
        return this.m_density;
    }
    
    public void setParticleGravityScale(final float gravityScale) {
        this.m_gravityScale = gravityScale;
    }
    
    public float getParticleGravityScale() {
        return this.m_gravityScale;
    }
    
    public void setParticleDamping(final float damping) {
        this.m_dampingStrength = damping;
    }
    
    public float getParticleDamping() {
        return this.m_dampingStrength;
    }
    
    public float getParticleRadius() {
        return this.m_particleDiameter / 2.0f;
    }
    
    float getCriticalVelocity(final TimeStep step) {
        return this.m_particleDiameter * step.inv_dt;
    }
    
    float getCriticalVelocitySquared(final TimeStep step) {
        final float velocity = this.getCriticalVelocity(step);
        return velocity * velocity;
    }
    
    float getCriticalPressure(final TimeStep step) {
        return this.m_density * this.getCriticalVelocitySquared(step);
    }
    
    float getParticleStride() {
        return 0.75f * this.m_particleDiameter;
    }
    
    float getParticleMass() {
        final float stride = this.getParticleStride();
        return this.m_density * stride * stride;
    }
    
    float getParticleInvMass() {
        return 1.777777f * this.m_inverseDensity * this.m_inverseDiameter * this.m_inverseDiameter;
    }
    
    public int[] getParticleFlagsBuffer() {
        return this.m_flagsBuffer.data;
    }
    
    public Vec2[] getParticlePositionBuffer() {
        return this.m_positionBuffer.data;
    }
    
    public Vec2[] getParticleVelocityBuffer() {
        return this.m_velocityBuffer.data;
    }
    
    public ParticleColor[] getParticleColorBuffer() {
        this.m_colorBuffer.data = (ParticleColor[])this.requestParticleBuffer((Class<T>)ParticleColor.class, (T[])this.m_colorBuffer.data);
        return this.m_colorBuffer.data;
    }
    
    public Object[] getParticleUserDataBuffer() {
        return this.m_userDataBuffer.data = this.requestParticleBuffer((Class<T>)Object.class, (T[])this.m_userDataBuffer.data);
    }
    
    public int getParticleMaxCount() {
        return this.m_maxCount;
    }
    
    public void setParticleMaxCount(final int count) {
        assert this.m_count <= count;
        this.m_maxCount = count;
    }
    
    void setParticleBuffer(final ParticleBufferInt buffer, final int[] newData, final int newCapacity) {
        assert newData == null && newCapacity == 0;
        if (buffer.userSuppliedCapacity != 0) {}
        buffer.data = newData;
        buffer.userSuppliedCapacity = newCapacity;
    }
    
     <T> void setParticleBuffer(final ParticleBuffer<T> buffer, final T[] newData, final int newCapacity) {
        assert newData == null && newCapacity == 0;
        if (buffer.userSuppliedCapacity != 0) {}
        buffer.data = newData;
        buffer.userSuppliedCapacity = newCapacity;
    }
    
    public void setParticleFlagsBuffer(final int[] buffer, final int capacity) {
        this.setParticleBuffer(this.m_flagsBuffer, buffer, capacity);
    }
    
    public void setParticlePositionBuffer(final Vec2[] buffer, final int capacity) {
        this.setParticleBuffer(this.m_positionBuffer, buffer, capacity);
    }
    
    public void setParticleVelocityBuffer(final Vec2[] buffer, final int capacity) {
        this.setParticleBuffer(this.m_velocityBuffer, buffer, capacity);
    }
    
    public void setParticleColorBuffer(final ParticleColor[] buffer, final int capacity) {
        this.setParticleBuffer(this.m_colorBuffer, buffer, capacity);
    }
    
    public ParticleGroup[] getParticleGroupBuffer() {
        return this.m_groupBuffer;
    }
    
    public int getParticleGroupCount() {
        return this.m_groupCount;
    }
    
    public ParticleGroup[] getParticleGroupList() {
        return this.m_groupBuffer;
    }
    
    public int getParticleCount() {
        return this.m_count;
    }
    
    public void setParticleUserDataBuffer(final Object[] buffer, final int capacity) {
        this.setParticleBuffer(this.m_userDataBuffer, buffer, capacity);
    }
    
    private static final int lowerBound(final Proxy[] ray, int length, final long tag) {
        int left = 0;
        while (length > 0) {
            final int step = length / 2;
            final int curr = left + step;
            if (ray[curr].tag < tag) {
                left = curr + 1;
                length -= step + 1;
            }
            else {
                length = step;
            }
        }
        return left;
    }
    
    private static final int upperBound(final Proxy[] ray, int length, final long tag) {
        int left = 0;
        while (length > 0) {
            final int step = length / 2;
            final int curr = left + step;
            if (ray[curr].tag <= tag) {
                left = curr + 1;
                length -= step + 1;
            }
            else {
                length = step;
            }
        }
        return left;
    }
    
    public void queryAABB(final ParticleQueryCallback callback, final AABB aabb) {
        if (this.m_proxyCount == 0) {
            return;
        }
        final float lowerBoundX = aabb.lowerBound.x;
        final float lowerBoundY = aabb.lowerBound.y;
        final float upperBoundX = aabb.upperBound.x;
        final float upperBoundY = aabb.upperBound.y;
        final int firstProxy = lowerBound(this.m_proxyBuffer, this.m_proxyCount, computeTag(this.m_inverseDiameter * lowerBoundX, this.m_inverseDiameter * lowerBoundY));
        for (int lastProxy = upperBound(this.m_proxyBuffer, this.m_proxyCount, computeTag(this.m_inverseDiameter * upperBoundX, this.m_inverseDiameter * upperBoundY)), proxy = firstProxy; proxy < lastProxy; ++proxy) {
            final int i = this.m_proxyBuffer[proxy].index;
            final Vec2 p = this.m_positionBuffer.data[i];
            if (lowerBoundX < p.x && p.x < upperBoundX && lowerBoundY < p.y && p.y < upperBoundY && !callback.reportParticle(i)) {
                break;
            }
        }
    }
    
    public void raycast(final ParticleRaycastCallback callback, final Vec2 point1, final Vec2 point2) {
        if (this.m_proxyCount == 0) {
            return;
        }
        final int firstProxy = lowerBound(this.m_proxyBuffer, this.m_proxyCount, computeTag(this.m_inverseDiameter * MathUtils.min(point1.x, point2.x) - 1.0f, this.m_inverseDiameter * MathUtils.min(point1.y, point2.y) - 1.0f));
        final int lastProxy = upperBound(this.m_proxyBuffer, this.m_proxyCount, computeTag(this.m_inverseDiameter * MathUtils.max(point1.x, point2.x) + 1.0f, this.m_inverseDiameter * MathUtils.max(point1.y, point2.y) + 1.0f));
        float fraction = 1.0f;
        final float vx = point2.x - point1.x;
        final float vy = point2.y - point1.y;
        float v2 = vx * vx + vy * vy;
        if (v2 == 0.0f) {
            v2 = Float.MAX_VALUE;
        }
        for (int proxy = firstProxy; proxy < lastProxy; ++proxy) {
            final int i = this.m_proxyBuffer[proxy].index;
            final Vec2 posI = this.m_positionBuffer.data[i];
            final float px = point1.x - posI.x;
            final float py = point1.y - posI.y;
            final float pv = px * vx + py * vy;
            final float p2 = px * px + py * py;
            final float determinant = pv * pv - v2 * (p2 - this.m_squaredDiameter);
            if (determinant >= 0.0f) {
                final float sqrtDeterminant = MathUtils.sqrt(determinant);
                float t = (-pv - sqrtDeterminant) / v2;
                if (t <= fraction) {
                    if (t < 0.0f) {
                        t = (-pv + sqrtDeterminant) / v2;
                        if (t < 0.0f) {
                            continue;
                        }
                        if (t > fraction) {
                            continue;
                        }
                    }
                    final Vec2 n = this.tempVec;
                    this.tempVec.x = px + t * vx;
                    this.tempVec.y = py + t * vy;
                    n.normalize();
                    final Vec2 point3 = this.tempVec2;
                    point3.x = point1.x + t * vx;
                    point3.y = point1.y + t * vy;
                    final float f = callback.reportParticle(i, point3, n, t);
                    fraction = MathUtils.min(fraction, f);
                    if (fraction <= 0.0f) {
                        break;
                    }
                }
            }
        }
    }
    
    public float computeParticleCollisionEnergy() {
        float sum_v2 = 0.0f;
        for (int k = 0; k < this.m_contactCount; ++k) {
            final ParticleContact contact = this.m_contactBuffer[k];
            final int a = contact.indexA;
            final int b = contact.indexB;
            final Vec2 n = contact.normal;
            final Vec2 va = this.m_velocityBuffer.data[a];
            final Vec2 vb = this.m_velocityBuffer.data[b];
            final float vx = vb.x - va.x;
            final float vy = vb.y - va.y;
            final float vn = vx * n.x + vy * n.y;
            if (vn < 0.0f) {
                sum_v2 += vn * vn;
            }
        }
        return 0.5f * this.getParticleMass() * sum_v2;
    }
    
    static <T> T[] reallocateBuffer(final ParticleBuffer<T> buffer, final int oldCapacity, final int newCapacity, final boolean deferred) {
        assert newCapacity > oldCapacity;
        return BufferUtils.reallocateBuffer(buffer.dataClass, buffer.data, buffer.userSuppliedCapacity, oldCapacity, newCapacity, deferred);
    }
    
    static int[] reallocateBuffer(final ParticleBufferInt buffer, final int oldCapacity, final int newCapacity, final boolean deferred) {
        assert newCapacity > oldCapacity;
        return BufferUtils.reallocateBuffer(buffer.data, buffer.userSuppliedCapacity, oldCapacity, newCapacity, deferred);
    }
    
     <T> T[] requestParticleBuffer(final Class<T> klass, T[] buffer) {
        if (buffer == null) {
            buffer = (T[])Array.newInstance(klass, this.m_internalAllocatedCapacity);
            for (int i = 0; i < this.m_internalAllocatedCapacity; ++i) {
                try {
                    buffer[i] = klass.newInstance();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return buffer;
    }
    
    float[] requestParticleBuffer(float[] buffer) {
        if (buffer == null) {
            buffer = new float[this.m_internalAllocatedCapacity];
        }
        return buffer;
    }
    
    private static class NewIndices
    {
        int start;
        int mid;
        int end;
        
        final int getIndex(final int i) {
            if (i < this.start) {
                return i;
            }
            if (i < this.mid) {
                return i + this.end - this.mid;
            }
            if (i < this.end) {
                return i + this.start - this.mid;
            }
            return i;
        }
    }
    
    public static class ParticleBuffer<T>
    {
        public T[] data;
        final Class<T> dataClass;
        int userSuppliedCapacity;
        
        public ParticleBuffer(final Class<T> dataClass) {
            this.dataClass = dataClass;
        }
    }
    
    static class ParticleBufferInt
    {
        int[] data;
        int userSuppliedCapacity;
    }
    
    public static class Proxy implements Comparable<Proxy>
    {
        int index;
        long tag;
        
        @Override
        public int compareTo(final Proxy o) {
            return (this.tag - o.tag < 0L) ? -1 : ((o.tag == this.tag) ? 0 : 1);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final Proxy other = (Proxy)obj;
            return this.tag == other.tag;
        }
    }
    
    public static class Pair
    {
        int indexA;
        int indexB;
        int flags;
        float strength;
        float distance;
    }
    
    public static class Triad
    {
        int indexA;
        int indexB;
        int indexC;
        int flags;
        float strength;
        final Vec2 pa;
        final Vec2 pb;
        final Vec2 pc;
        float ka;
        float kb;
        float kc;
        float s;
        
        public Triad() {
            this.pa = new Vec2();
            this.pb = new Vec2();
            this.pc = new Vec2();
        }
    }
    
    static class CreateParticleGroupCallback implements VoronoiDiagram.VoronoiDiagramCallback
    {
        ParticleSystem system;
        ParticleGroupDef def;
        int firstIndex;
        
        @Override
        public void callback(final int a, final int b, final int c) {
            final Vec2 pa = this.system.m_positionBuffer.data[a];
            final Vec2 pb = this.system.m_positionBuffer.data[b];
            final Vec2 pc = this.system.m_positionBuffer.data[c];
            final float dabx = pa.x - pb.x;
            final float daby = pa.y - pb.y;
            final float dbcx = pb.x - pc.x;
            final float dbcy = pb.y - pc.y;
            final float dcax = pc.x - pa.x;
            final float dcay = pc.y - pa.y;
            final float maxDistanceSquared = 4.0f * this.system.m_squaredDiameter;
            if (dabx * dabx + daby * daby < maxDistanceSquared && dbcx * dbcx + dbcy * dbcy < maxDistanceSquared && dcax * dcax + dcay * dcay < maxDistanceSquared) {
                if (this.system.m_triadCount >= this.system.m_triadCapacity) {
                    final int oldCapacity = this.system.m_triadCapacity;
                    final int newCapacity = (this.system.m_triadCount != 0) ? (2 * this.system.m_triadCount) : 256;
                    this.system.m_triadBuffer = BufferUtils.reallocateBuffer(Triad.class, this.system.m_triadBuffer, oldCapacity, newCapacity);
                    this.system.m_triadCapacity = newCapacity;
                }
                final Triad triad = this.system.m_triadBuffer[this.system.m_triadCount];
                triad.indexA = a;
                triad.indexB = b;
                triad.indexC = c;
                triad.flags = (this.system.m_flagsBuffer.data[a] | this.system.m_flagsBuffer.data[b] | this.system.m_flagsBuffer.data[c]);
                triad.strength = this.def.strength;
                final float midPointx = 0.33333334f * (pa.x + pb.x + pc.x);
                final float midPointy = 0.33333334f * (pa.y + pb.y + pc.y);
                triad.pa.x = pa.x - midPointx;
                triad.pa.y = pa.y - midPointy;
                triad.pb.x = pb.x - midPointx;
                triad.pb.y = pb.y - midPointy;
                triad.pc.x = pc.x - midPointx;
                triad.pc.y = pc.y - midPointy;
                triad.ka = -(dcax * dabx + dcay * daby);
                triad.kb = -(dabx * dbcx + daby * dbcy);
                triad.kc = -(dbcx * dcax + dbcy * dcay);
                triad.s = Vec2.cross(pa, pb) + Vec2.cross(pb, pc) + Vec2.cross(pc, pa);
                final ParticleSystem system = this.system;
                ++system.m_triadCount;
            }
        }
    }
    
    static class JoinParticleGroupsCallback implements VoronoiDiagram.VoronoiDiagramCallback
    {
        ParticleSystem system;
        ParticleGroup groupA;
        ParticleGroup groupB;
        
        @Override
        public void callback(final int a, final int b, final int c) {
            final int countA = ((a < this.groupB.m_firstIndex) + (b < this.groupB.m_firstIndex) + (c < this.groupB.m_firstIndex)) ? 1 : 0;
            if (countA > 0 && countA < 3) {
                final int af = this.system.m_flagsBuffer.data[a];
                final int bf = this.system.m_flagsBuffer.data[b];
                final int cf = this.system.m_flagsBuffer.data[c];
                if ((af & bf & cf & 0x10) != 0x0) {
                    final Vec2 pa = this.system.m_positionBuffer.data[a];
                    final Vec2 pb = this.system.m_positionBuffer.data[b];
                    final Vec2 pc = this.system.m_positionBuffer.data[c];
                    final float dabx = pa.x - pb.x;
                    final float daby = pa.y - pb.y;
                    final float dbcx = pb.x - pc.x;
                    final float dbcy = pb.y - pc.y;
                    final float dcax = pc.x - pa.x;
                    final float dcay = pc.y - pa.y;
                    final float maxDistanceSquared = 4.0f * this.system.m_squaredDiameter;
                    if (dabx * dabx + daby * daby < maxDistanceSquared && dbcx * dbcx + dbcy * dbcy < maxDistanceSquared && dcax * dcax + dcay * dcay < maxDistanceSquared) {
                        if (this.system.m_triadCount >= this.system.m_triadCapacity) {
                            final int oldCapacity = this.system.m_triadCapacity;
                            final int newCapacity = (this.system.m_triadCount != 0) ? (2 * this.system.m_triadCount) : 256;
                            this.system.m_triadBuffer = BufferUtils.reallocateBuffer(Triad.class, this.system.m_triadBuffer, oldCapacity, newCapacity);
                            this.system.m_triadCapacity = newCapacity;
                        }
                        final Triad triad = this.system.m_triadBuffer[this.system.m_triadCount];
                        triad.indexA = a;
                        triad.indexB = b;
                        triad.indexC = c;
                        triad.flags = (af | bf | cf);
                        triad.strength = MathUtils.min(this.groupA.m_strength, this.groupB.m_strength);
                        final float midPointx = 0.33333334f * (pa.x + pb.x + pc.x);
                        final float midPointy = 0.33333334f * (pa.y + pb.y + pc.y);
                        triad.pa.x = pa.x - midPointx;
                        triad.pa.y = pa.y - midPointy;
                        triad.pb.x = pb.x - midPointx;
                        triad.pb.y = pb.y - midPointy;
                        triad.pc.x = pc.x - midPointx;
                        triad.pc.y = pc.y - midPointy;
                        triad.ka = -(dcax * dabx + dcay * daby);
                        triad.kb = -(dabx * dbcx + daby * dbcy);
                        triad.kc = -(dbcx * dcax + dbcy * dcay);
                        triad.s = Vec2.cross(pa, pb) + Vec2.cross(pb, pc) + Vec2.cross(pc, pa);
                        final ParticleSystem system = this.system;
                        ++system.m_triadCount;
                    }
                }
            }
        }
    }
    
    static class DestroyParticlesInShapeCallback implements ParticleQueryCallback
    {
        ParticleSystem system;
        Shape shape;
        Transform xf;
        boolean callDestructionListener;
        int destroyed;
        
        public DestroyParticlesInShapeCallback() {
        }
        
        public void init(final ParticleSystem system, final Shape shape, final Transform xf, final boolean callDestructionListener) {
            this.system = system;
            this.shape = shape;
            this.xf = xf;
            this.destroyed = 0;
            this.callDestructionListener = callDestructionListener;
        }
        
        @Override
        public boolean reportParticle(final int index) {
            assert index >= 0 && index < this.system.m_count;
            if (this.shape.testPoint(this.xf, this.system.m_positionBuffer.data[index])) {
                this.system.destroyParticle(index, this.callDestructionListener);
                ++this.destroyed;
            }
            return true;
        }
    }
    
    static class UpdateBodyContactsCallback implements QueryCallback
    {
        ParticleSystem system;
        private final Vec2 tempVec;
        
        UpdateBodyContactsCallback() {
            this.tempVec = new Vec2();
        }
        
        @Override
        public boolean reportFixture(final Fixture fixture) {
            if (fixture.isSensor()) {
                return true;
            }
            final Shape shape = fixture.getShape();
            final Body b = fixture.getBody();
            final Vec2 bp = b.getWorldCenter();
            final float bm = b.getMass();
            final float bI = b.getInertia() - bm * b.getLocalCenter().lengthSquared();
            final float invBm = (bm > 0.0f) ? (1.0f / bm) : 0.0f;
            final float invBI = (bI > 0.0f) ? (1.0f / bI) : 0.0f;
            for (int childCount = shape.getChildCount(), childIndex = 0; childIndex < childCount; ++childIndex) {
                final AABB aabb = fixture.getAABB(childIndex);
                final float aabblowerBoundx = aabb.lowerBound.x - this.system.m_particleDiameter;
                final float aabblowerBoundy = aabb.lowerBound.y - this.system.m_particleDiameter;
                final float aabbupperBoundx = aabb.upperBound.x + this.system.m_particleDiameter;
                final float aabbupperBoundy = aabb.upperBound.y + this.system.m_particleDiameter;
                final int firstProxy = lowerBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabblowerBoundx, this.system.m_inverseDiameter * aabblowerBoundy));
                for (int lastProxy = upperBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabbupperBoundx, this.system.m_inverseDiameter * aabbupperBoundy)), proxy = firstProxy; proxy != lastProxy; ++proxy) {
                    final int a = this.system.m_proxyBuffer[proxy].index;
                    final Vec2 ap = this.system.m_positionBuffer.data[a];
                    if (aabblowerBoundx <= ap.x && ap.x <= aabbupperBoundx && aabblowerBoundy <= ap.y && ap.y <= aabbupperBoundy) {
                        final Vec2 n = this.tempVec;
                        final float d = fixture.computeDistance(ap, childIndex, n);
                        if (d < this.system.m_particleDiameter) {
                            final float invAm = ((this.system.m_flagsBuffer.data[a] & 0x4) != 0x0) ? 0.0f : this.system.getParticleInvMass();
                            final float rpx = ap.x - bp.x;
                            final float rpy = ap.y - bp.y;
                            final float rpn = rpx * n.y - rpy * n.x;
                            if (this.system.m_bodyContactCount >= this.system.m_bodyContactCapacity) {
                                final int oldCapacity = this.system.m_bodyContactCapacity;
                                final int newCapacity = (this.system.m_bodyContactCount != 0) ? (2 * this.system.m_bodyContactCount) : 256;
                                this.system.m_bodyContactBuffer = BufferUtils.reallocateBuffer(ParticleBodyContact.class, this.system.m_bodyContactBuffer, oldCapacity, newCapacity);
                                this.system.m_bodyContactCapacity = newCapacity;
                            }
                            final ParticleBodyContact contact = this.system.m_bodyContactBuffer[this.system.m_bodyContactCount];
                            contact.index = a;
                            contact.body = b;
                            contact.weight = 1.0f - d * this.system.m_inverseDiameter;
                            contact.normal.x = -n.x;
                            contact.normal.y = -n.y;
                            contact.mass = 1.0f / (invAm + invBm + invBI * rpn * rpn);
                            final ParticleSystem system = this.system;
                            ++system.m_bodyContactCount;
                        }
                    }
                }
            }
            return true;
        }
    }
    
    static class SolveCollisionCallback implements QueryCallback
    {
        ParticleSystem system;
        TimeStep step;
        private final RayCastInput input;
        private final RayCastOutput output;
        private final Vec2 tempVec;
        private final Vec2 tempVec2;
        
        SolveCollisionCallback() {
            this.input = new RayCastInput();
            this.output = new RayCastOutput();
            this.tempVec = new Vec2();
            this.tempVec2 = new Vec2();
        }
        
        @Override
        public boolean reportFixture(final Fixture fixture) {
            if (fixture.isSensor()) {
                return true;
            }
            final Shape shape = fixture.getShape();
            final Body body = fixture.getBody();
            for (int childCount = shape.getChildCount(), childIndex = 0; childIndex < childCount; ++childIndex) {
                final AABB aabb = fixture.getAABB(childIndex);
                final float aabblowerBoundx = aabb.lowerBound.x - this.system.m_particleDiameter;
                final float aabblowerBoundy = aabb.lowerBound.y - this.system.m_particleDiameter;
                final float aabbupperBoundx = aabb.upperBound.x + this.system.m_particleDiameter;
                final float aabbupperBoundy = aabb.upperBound.y + this.system.m_particleDiameter;
                final int firstProxy = lowerBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabblowerBoundx, this.system.m_inverseDiameter * aabblowerBoundy));
                for (int lastProxy = upperBound(this.system.m_proxyBuffer, this.system.m_proxyCount, ParticleSystem.computeTag(this.system.m_inverseDiameter * aabbupperBoundx, this.system.m_inverseDiameter * aabbupperBoundy)), proxy = firstProxy; proxy != lastProxy; ++proxy) {
                    final int a = this.system.m_proxyBuffer[proxy].index;
                    final Vec2 ap = this.system.m_positionBuffer.data[a];
                    if (aabblowerBoundx <= ap.x && ap.x <= aabbupperBoundx && aabblowerBoundy <= ap.y && ap.y <= aabbupperBoundy) {
                        final Vec2 av = this.system.m_velocityBuffer.data[a];
                        final Vec2 temp = this.tempVec;
                        Transform.mulTransToOutUnsafe(body.m_xf0, ap, temp);
                        Transform.mulToOutUnsafe(body.m_xf, temp, this.input.p1);
                        this.input.p2.x = ap.x + this.step.dt * av.x;
                        this.input.p2.y = ap.y + this.step.dt * av.y;
                        this.input.maxFraction = 1.0f;
                        if (fixture.raycast(this.output, this.input, childIndex)) {
                            final Vec2 p = this.tempVec;
                            p.x = (1.0f - this.output.fraction) * this.input.p1.x + this.output.fraction * this.input.p2.x + Settings.linearSlop * this.output.normal.x;
                            p.y = (1.0f - this.output.fraction) * this.input.p1.y + this.output.fraction * this.input.p2.y + Settings.linearSlop * this.output.normal.y;
                            final float vx = this.step.inv_dt * (p.x - ap.x);
                            final float vy = this.step.inv_dt * (p.y - ap.y);
                            av.x = vx;
                            av.y = vy;
                            final float particleMass = this.system.getParticleMass();
                            final float ax = particleMass * (av.x - vx);
                            final float ay = particleMass * (av.y - vy);
                            final Vec2 b = this.output.normal;
                            final float fdn = ax * b.x + ay * b.y;
                            final Vec2 f = this.tempVec2;
                            f.x = fdn * b.x;
                            f.y = fdn * b.y;
                            body.applyLinearImpulse(f, p, true);
                        }
                    }
                }
            }
            return true;
        }
    }
    
    static class Test
    {
        static boolean IsProxyInvalid(final Proxy proxy) {
            return proxy.index < 0;
        }
        
        static boolean IsContactInvalid(final ParticleContact contact) {
            return contact.indexA < 0 || contact.indexB < 0;
        }
        
        static boolean IsBodyContactInvalid(final ParticleBodyContact contact) {
            return contact.index < 0;
        }
        
        static boolean IsPairInvalid(final Pair pair) {
            return pair.indexA < 0 || pair.indexB < 0;
        }
        
        static boolean IsTriadInvalid(final Triad triad) {
            return triad.indexA < 0 || triad.indexB < 0 || triad.indexC < 0;
        }
    }
}
