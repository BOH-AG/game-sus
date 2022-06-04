// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.broadphase;

import org.jbox2d.collision.RayCastInput;
import org.jbox2d.callbacks.TreeRayCastCallback;
import java.util.Arrays;
import org.jbox2d.callbacks.PairCallback;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.callbacks.TreeCallback;

public class DefaultBroadPhaseBuffer implements TreeCallback, BroadPhase
{
    private final BroadPhaseStrategy m_tree;
    private int m_proxyCount;
    private int[] m_moveBuffer;
    private int m_moveCapacity;
    private int m_moveCount;
    private long[] m_pairBuffer;
    private int m_pairCapacity;
    private int m_pairCount;
    private int m_queryProxyId;
    
    public DefaultBroadPhaseBuffer(final BroadPhaseStrategy strategy) {
        this.m_proxyCount = 0;
        this.m_pairCapacity = 16;
        this.m_pairCount = 0;
        this.m_pairBuffer = new long[this.m_pairCapacity];
        for (int i = 0; i < this.m_pairCapacity; ++i) {
            this.m_pairBuffer[i] = 0L;
        }
        this.m_moveCapacity = 16;
        this.m_moveCount = 0;
        this.m_moveBuffer = new int[this.m_moveCapacity];
        this.m_tree = strategy;
        this.m_queryProxyId = -1;
    }
    
    @Override
    public final int createProxy(final AABB aabb, final Object userData) {
        final int proxyId = this.m_tree.createProxy(aabb, userData);
        ++this.m_proxyCount;
        this.bufferMove(proxyId);
        return proxyId;
    }
    
    @Override
    public final void destroyProxy(final int proxyId) {
        this.unbufferMove(proxyId);
        --this.m_proxyCount;
        this.m_tree.destroyProxy(proxyId);
    }
    
    @Override
    public final void moveProxy(final int proxyId, final AABB aabb, final Vec2 displacement) {
        final boolean buffer = this.m_tree.moveProxy(proxyId, aabb, displacement);
        if (buffer) {
            this.bufferMove(proxyId);
        }
    }
    
    @Override
    public void touchProxy(final int proxyId) {
        this.bufferMove(proxyId);
    }
    
    @Override
    public Object getUserData(final int proxyId) {
        return this.m_tree.getUserData(proxyId);
    }
    
    @Override
    public AABB getFatAABB(final int proxyId) {
        return this.m_tree.getFatAABB(proxyId);
    }
    
    @Override
    public boolean testOverlap(final int proxyIdA, final int proxyIdB) {
        final AABB a = this.m_tree.getFatAABB(proxyIdA);
        final AABB b = this.m_tree.getFatAABB(proxyIdB);
        return b.lowerBound.x - a.upperBound.x <= 0.0f && b.lowerBound.y - a.upperBound.y <= 0.0f && a.lowerBound.x - b.upperBound.x <= 0.0f && a.lowerBound.y - b.upperBound.y <= 0.0f;
    }
    
    @Override
    public final int getProxyCount() {
        return this.m_proxyCount;
    }
    
    @Override
    public void drawTree(final DebugDraw argDraw) {
        this.m_tree.drawTree(argDraw);
    }
    
    @Override
    public final void updatePairs(final PairCallback callback) {
        this.m_pairCount = 0;
        for (int i = 0; i < this.m_moveCount; ++i) {
            this.m_queryProxyId = this.m_moveBuffer[i];
            if (this.m_queryProxyId != -1) {
                final AABB fatAABB = this.m_tree.getFatAABB(this.m_queryProxyId);
                this.m_tree.query(this, fatAABB);
            }
        }
        this.m_moveCount = 0;
        Arrays.sort(this.m_pairBuffer, 0, this.m_pairCount);
        int i = 0;
        while (i < this.m_pairCount) {
            final long primaryPair = this.m_pairBuffer[i];
            final Object userDataA = this.m_tree.getUserData((int)(primaryPair >> 32));
            final Object userDataB = this.m_tree.getUserData((int)primaryPair);
            callback.addPair(userDataA, userDataB);
            ++i;
            while (i < this.m_pairCount) {
                final long pair = this.m_pairBuffer[i];
                if (pair != primaryPair) {
                    break;
                }
                ++i;
            }
        }
    }
    
    @Override
    public final void query(final TreeCallback callback, final AABB aabb) {
        this.m_tree.query(callback, aabb);
    }
    
    @Override
    public final void raycast(final TreeRayCastCallback callback, final RayCastInput input) {
        this.m_tree.raycast(callback, input);
    }
    
    @Override
    public final int getTreeHeight() {
        return this.m_tree.getHeight();
    }
    
    @Override
    public int getTreeBalance() {
        return this.m_tree.getMaxBalance();
    }
    
    @Override
    public float getTreeQuality() {
        return this.m_tree.getAreaRatio();
    }
    
    protected final void bufferMove(final int proxyId) {
        if (this.m_moveCount == this.m_moveCapacity) {
            final int[] old = this.m_moveBuffer;
            this.m_moveCapacity *= 2;
            System.arraycopy(old, 0, this.m_moveBuffer = new int[this.m_moveCapacity], 0, old.length);
        }
        this.m_moveBuffer[this.m_moveCount] = proxyId;
        ++this.m_moveCount;
    }
    
    protected final void unbufferMove(final int proxyId) {
        for (int i = 0; i < this.m_moveCount; ++i) {
            if (this.m_moveBuffer[i] == proxyId) {
                this.m_moveBuffer[i] = -1;
            }
        }
    }
    
    @Override
    public final boolean treeCallback(final int proxyId) {
        if (proxyId == this.m_queryProxyId) {
            return true;
        }
        if (this.m_pairCount == this.m_pairCapacity) {
            final long[] oldBuffer = this.m_pairBuffer;
            this.m_pairCapacity *= 2;
            System.arraycopy(oldBuffer, 0, this.m_pairBuffer = new long[this.m_pairCapacity], 0, oldBuffer.length);
            for (int i = oldBuffer.length; i < this.m_pairCapacity; ++i) {
                this.m_pairBuffer[i] = 0L;
            }
        }
        if (proxyId < this.m_queryProxyId) {
            this.m_pairBuffer[this.m_pairCount] = ((long)proxyId << 32 | (long)this.m_queryProxyId);
        }
        else {
            this.m_pairBuffer[this.m_pairCount] = ((long)this.m_queryProxyId << 32 | (long)proxyId);
        }
        ++this.m_pairCount;
        return true;
    }
}
