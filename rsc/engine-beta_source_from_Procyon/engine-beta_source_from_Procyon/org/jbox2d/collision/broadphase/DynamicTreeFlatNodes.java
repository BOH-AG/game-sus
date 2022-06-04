// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.broadphase;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.MathUtils;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.common.Settings;
import org.jbox2d.common.BufferUtils;
import org.jbox2d.common.Color3f;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;

public class DynamicTreeFlatNodes implements BroadPhaseStrategy
{
    public static final int MAX_STACK_SIZE = 64;
    public static final int NULL_NODE = -1;
    public static final int INITIAL_BUFFER_LENGTH = 16;
    public int m_root;
    public AABB[] m_aabb;
    public Object[] m_userData;
    protected int[] m_parent;
    protected int[] m_child1;
    protected int[] m_child2;
    protected int[] m_height;
    private int m_nodeCount;
    private int m_nodeCapacity;
    private int m_freeList;
    private final Vec2[] drawVecs;
    private int[] nodeStack;
    private int nodeStackIndex;
    private final Vec2 r;
    private final AABB aabb;
    private final RayCastInput subInput;
    private final AABB combinedAABB;
    private final Color3f color;
    private final Vec2 textVec;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public DynamicTreeFlatNodes() {
        this.drawVecs = new Vec2[4];
        this.nodeStack = new int[20];
        this.r = new Vec2();
        this.aabb = new AABB();
        this.subInput = new RayCastInput();
        this.combinedAABB = new AABB();
        this.color = new Color3f();
        this.textVec = new Vec2();
        this.m_root = -1;
        this.expandBuffers(this.m_nodeCount = 0, this.m_nodeCapacity = 16);
        for (int i = 0; i < this.drawVecs.length; ++i) {
            this.drawVecs[i] = new Vec2();
        }
    }
    
    private void expandBuffers(final int oldSize, final int newSize) {
        this.m_aabb = BufferUtils.reallocateBuffer(AABB.class, this.m_aabb, oldSize, newSize);
        this.m_userData = BufferUtils.reallocateBuffer(Object.class, this.m_userData, oldSize, newSize);
        this.m_parent = BufferUtils.reallocateBuffer(this.m_parent, oldSize, newSize);
        this.m_child1 = BufferUtils.reallocateBuffer(this.m_child1, oldSize, newSize);
        this.m_child2 = BufferUtils.reallocateBuffer(this.m_child2, oldSize, newSize);
        this.m_height = BufferUtils.reallocateBuffer(this.m_height, oldSize, newSize);
        for (int i = oldSize; i < newSize; ++i) {
            this.m_aabb[i] = new AABB();
            this.m_parent[i] = ((i == newSize - 1) ? -1 : (i + 1));
            this.m_height[i] = -1;
            this.m_child1[i] = -1;
            this.m_child2[i] = -1;
        }
        this.m_freeList = oldSize;
    }
    
    @Override
    public final int createProxy(final AABB aabb, final Object userData) {
        final int node = this.allocateNode();
        final AABB nodeAABB = this.m_aabb[node];
        nodeAABB.lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
        nodeAABB.lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
        nodeAABB.upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
        nodeAABB.upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
        this.m_userData[node] = userData;
        this.insertLeaf(node);
        return node;
    }
    
    @Override
    public final void destroyProxy(final int proxyId) {
        assert 0 <= proxyId && proxyId < this.m_nodeCapacity;
        assert this.m_child1[proxyId] == -1;
        this.removeLeaf(proxyId);
        this.freeNode(proxyId);
    }
    
    @Override
    public final boolean moveProxy(final int proxyId, final AABB aabb, final Vec2 displacement) {
        assert 0 <= proxyId && proxyId < this.m_nodeCapacity;
        final int node = proxyId;
        assert this.m_child1[node] == -1;
        final AABB nodeAABB = this.m_aabb[node];
        if (nodeAABB.lowerBound.x <= aabb.lowerBound.x && nodeAABB.lowerBound.y <= aabb.lowerBound.y && aabb.upperBound.x <= nodeAABB.upperBound.x && aabb.upperBound.y <= nodeAABB.upperBound.y) {
            return false;
        }
        this.removeLeaf(node);
        final Vec2 lowerBound = nodeAABB.lowerBound;
        final Vec2 upperBound = nodeAABB.upperBound;
        lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
        lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
        upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
        upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
        final float dx = displacement.x * Settings.aabbMultiplier;
        final float dy = displacement.y * Settings.aabbMultiplier;
        if (dx < 0.0f) {
            final Vec2 vec2 = lowerBound;
            vec2.x += dx;
        }
        else {
            final Vec2 vec3 = upperBound;
            vec3.x += dx;
        }
        if (dy < 0.0f) {
            final Vec2 vec4 = lowerBound;
            vec4.y += dy;
        }
        else {
            final Vec2 vec5 = upperBound;
            vec5.y += dy;
        }
        this.insertLeaf(proxyId);
        return true;
    }
    
    @Override
    public final Object getUserData(final int proxyId) {
        assert 0 <= proxyId && proxyId < this.m_nodeCount;
        return this.m_userData[proxyId];
    }
    
    @Override
    public final AABB getFatAABB(final int proxyId) {
        assert 0 <= proxyId && proxyId < this.m_nodeCount;
        return this.m_aabb[proxyId];
    }
    
    @Override
    public final void query(final TreeCallback callback, final AABB aabb) {
        this.nodeStackIndex = 0;
        this.nodeStack[this.nodeStackIndex++] = this.m_root;
        while (this.nodeStackIndex > 0) {
            final int[] nodeStack = this.nodeStack;
            final int nodeStackIndex = this.nodeStackIndex - 1;
            this.nodeStackIndex = nodeStackIndex;
            final int node = nodeStack[nodeStackIndex];
            if (node == -1) {
                continue;
            }
            if (!AABB.testOverlap(this.m_aabb[node], aabb)) {
                continue;
            }
            final int child1 = this.m_child1[node];
            if (child1 == -1) {
                final boolean proceed = callback.treeCallback(node);
                if (!proceed) {
                    return;
                }
                continue;
            }
            else {
                if (this.nodeStack.length - this.nodeStackIndex - 2 <= 0) {
                    this.nodeStack = BufferUtils.reallocateBuffer(this.nodeStack, this.nodeStack.length, this.nodeStack.length * 2);
                }
                this.nodeStack[this.nodeStackIndex++] = child1;
                this.nodeStack[this.nodeStackIndex++] = this.m_child2[node];
            }
        }
    }
    
    @Override
    public void raycast(final TreeRayCastCallback callback, final RayCastInput input) {
        final Vec2 p1 = input.p1;
        final Vec2 p2 = input.p2;
        final float p1x = p1.x;
        final float p2x = p2.x;
        final float p1y = p1.y;
        final float p2y = p2.y;
        this.r.x = p2x - p1x;
        this.r.y = p2y - p1y;
        assert this.r.x * this.r.x + this.r.y * this.r.y > 0.0f;
        this.r.normalize();
        final float rx = this.r.x;
        final float ry = this.r.y;
        final float vx = -1.0f * ry;
        final float vy = 1.0f * rx;
        final float absVx = MathUtils.abs(vx);
        final float absVy = MathUtils.abs(vy);
        float maxFraction = input.maxFraction;
        final AABB segAABB = this.aabb;
        float tempx = (p2x - p1x) * maxFraction + p1x;
        float tempy = (p2y - p1y) * maxFraction + p1y;
        segAABB.lowerBound.x = ((p1x < tempx) ? p1x : tempx);
        segAABB.lowerBound.y = ((p1y < tempy) ? p1y : tempy);
        segAABB.upperBound.x = ((p1x > tempx) ? p1x : tempx);
        segAABB.upperBound.y = ((p1y > tempy) ? p1y : tempy);
        this.nodeStackIndex = 0;
        this.nodeStack[this.nodeStackIndex++] = this.m_root;
        while (this.nodeStackIndex > 0) {
            final int[] nodeStack = this.nodeStack;
            final int nodeStackIndex = this.nodeStackIndex - 1;
            this.nodeStackIndex = nodeStackIndex;
            final int root = this.m_root;
            nodeStack[nodeStackIndex] = root;
            final int node = root;
            if (node == -1) {
                continue;
            }
            final AABB nodeAABB = this.m_aabb[node];
            if (!AABB.testOverlap(nodeAABB, segAABB)) {
                continue;
            }
            final float cx = (nodeAABB.lowerBound.x + nodeAABB.upperBound.x) * 0.5f;
            final float cy = (nodeAABB.lowerBound.y + nodeAABB.upperBound.y) * 0.5f;
            final float hx = (nodeAABB.upperBound.x - nodeAABB.lowerBound.x) * 0.5f;
            final float hy = (nodeAABB.upperBound.y - nodeAABB.lowerBound.y) * 0.5f;
            tempx = p1x - cx;
            tempy = p1y - cy;
            final float separation = MathUtils.abs(vx * tempx + vy * tempy) - (absVx * hx + absVy * hy);
            if (separation > 0.0f) {
                continue;
            }
            final int child1 = this.m_child1[node];
            if (child1 == -1) {
                this.subInput.p1.x = p1x;
                this.subInput.p1.y = p1y;
                this.subInput.p2.x = p2x;
                this.subInput.p2.y = p2y;
                this.subInput.maxFraction = maxFraction;
                final float value = callback.raycastCallback(this.subInput, node);
                if (value == 0.0f) {
                    return;
                }
                if (value <= 0.0f) {
                    continue;
                }
                maxFraction = value;
                tempx = (p2x - p1x) * maxFraction + p1x;
                tempy = (p2y - p1y) * maxFraction + p1y;
                segAABB.lowerBound.x = ((p1x < tempx) ? p1x : tempx);
                segAABB.lowerBound.y = ((p1y < tempy) ? p1y : tempy);
                segAABB.upperBound.x = ((p1x > tempx) ? p1x : tempx);
                segAABB.upperBound.y = ((p1y > tempy) ? p1y : tempy);
            }
            else {
                this.nodeStack[this.nodeStackIndex++] = child1;
                this.nodeStack[this.nodeStackIndex++] = this.m_child2[node];
            }
        }
    }
    
    @Override
    public final int computeHeight() {
        return this.computeHeight(this.m_root);
    }
    
    private final int computeHeight(final int node) {
        assert 0 <= node && node < this.m_nodeCapacity;
        if (this.m_child1[node] == -1) {
            return 0;
        }
        final int height1 = this.computeHeight(this.m_child1[node]);
        final int height2 = this.computeHeight(this.m_child2[node]);
        return 1 + MathUtils.max(height1, height2);
    }
    
    public void validate() {
        this.validateStructure(this.m_root);
        this.validateMetrics(this.m_root);
        int freeCount = 0;
        for (int freeNode = this.m_freeList; freeNode != -1; freeNode = this.m_parent[freeNode], ++freeCount) {
            assert 0 <= freeNode && freeNode < this.m_nodeCapacity;
        }
        assert this.getHeight() == this.computeHeight();
        assert this.m_nodeCount + freeCount == this.m_nodeCapacity;
    }
    
    @Override
    public int getHeight() {
        if (this.m_root == -1) {
            return 0;
        }
        return this.m_height[this.m_root];
    }
    
    @Override
    public int getMaxBalance() {
        int maxBalance = 0;
        for (int i = 0; i < this.m_nodeCapacity; ++i) {
            if (this.m_height[i] > 1) {
                assert this.m_child1[i] != -1;
                final int child1 = this.m_child1[i];
                final int child2 = this.m_child2[i];
                final int balance = MathUtils.abs(this.m_height[child2] - this.m_height[child1]);
                maxBalance = MathUtils.max(maxBalance, balance);
            }
        }
        return maxBalance;
    }
    
    @Override
    public float getAreaRatio() {
        if (this.m_root == -1) {
            return 0.0f;
        }
        final int root = this.m_root;
        final float rootArea = this.m_aabb[root].getPerimeter();
        float totalArea = 0.0f;
        for (int i = 0; i < this.m_nodeCapacity; ++i) {
            if (this.m_height[i] >= 0) {
                totalArea += this.m_aabb[i].getPerimeter();
            }
        }
        return totalArea / rootArea;
    }
    
    private final int allocateNode() {
        if (this.m_freeList == -1) {
            assert this.m_nodeCount == this.m_nodeCapacity;
            this.m_nodeCapacity *= 2;
            this.expandBuffers(this.m_nodeCount, this.m_nodeCapacity);
        }
        assert this.m_freeList != -1;
        final int node = this.m_freeList;
        this.m_freeList = this.m_parent[node];
        this.m_parent[node] = -1;
        this.m_child1[node] = -1;
        this.m_height[node] = 0;
        ++this.m_nodeCount;
        return node;
    }
    
    private final void freeNode(final int node) {
        assert node != -1;
        assert 0 < this.m_nodeCount;
        this.m_parent[node] = ((this.m_freeList != -1) ? this.m_freeList : -1);
        this.m_height[node] = -1;
        this.m_freeList = node;
        --this.m_nodeCount;
    }
    
    private final void insertLeaf(final int leaf) {
        if (this.m_root == -1) {
            this.m_root = leaf;
            this.m_parent[this.m_root] = -1;
            return;
        }
        final AABB leafAABB = this.m_aabb[leaf];
        int index = this.m_root;
        while (this.m_child1[index] != -1) {
            final int node = index;
            final int child1 = this.m_child1[node];
            final int child2 = this.m_child2[node];
            final AABB nodeAABB = this.m_aabb[node];
            final float area = nodeAABB.getPerimeter();
            this.combinedAABB.combine(nodeAABB, leafAABB);
            final float combinedArea = this.combinedAABB.getPerimeter();
            final float cost = 2.0f * combinedArea;
            final float inheritanceCost = 2.0f * (combinedArea - area);
            final AABB child1AABB = this.m_aabb[child1];
            float cost2;
            if (this.m_child1[child1] == -1) {
                this.combinedAABB.combine(leafAABB, child1AABB);
                cost2 = this.combinedAABB.getPerimeter() + inheritanceCost;
            }
            else {
                this.combinedAABB.combine(leafAABB, child1AABB);
                final float oldArea = child1AABB.getPerimeter();
                final float newArea = this.combinedAABB.getPerimeter();
                cost2 = newArea - oldArea + inheritanceCost;
            }
            final AABB child2AABB = this.m_aabb[child2];
            float cost3;
            if (this.m_child1[child2] == -1) {
                this.combinedAABB.combine(leafAABB, child2AABB);
                cost3 = this.combinedAABB.getPerimeter() + inheritanceCost;
            }
            else {
                this.combinedAABB.combine(leafAABB, child2AABB);
                final float oldArea2 = child2AABB.getPerimeter();
                final float newArea2 = this.combinedAABB.getPerimeter();
                cost3 = newArea2 - oldArea2 + inheritanceCost;
            }
            if (cost < cost2 && cost < cost3) {
                break;
            }
            if (cost2 < cost3) {
                index = child1;
            }
            else {
                index = child2;
            }
        }
        final int sibling = index;
        final int oldParent = this.m_parent[sibling];
        final int newParent = this.allocateNode();
        this.m_parent[newParent] = oldParent;
        this.m_userData[newParent] = null;
        this.m_aabb[newParent].combine(leafAABB, this.m_aabb[sibling]);
        this.m_height[newParent] = this.m_height[sibling] + 1;
        if (oldParent != -1) {
            if (this.m_child1[oldParent] == sibling) {
                this.m_child1[oldParent] = newParent;
            }
            else {
                this.m_child2[oldParent] = newParent;
            }
            this.m_child1[newParent] = sibling;
            this.m_child2[newParent] = leaf;
            this.m_parent[sibling] = newParent;
            this.m_parent[leaf] = newParent;
        }
        else {
            this.m_child1[newParent] = sibling;
            this.m_child2[newParent] = leaf;
            this.m_parent[sibling] = newParent;
            this.m_parent[leaf] = newParent;
            this.m_root = newParent;
        }
        for (index = this.m_parent[leaf]; index != -1; index = this.m_parent[index]) {
            index = this.balance(index);
            final int child3 = this.m_child1[index];
            final int child4 = this.m_child2[index];
            assert child3 != -1;
            assert child4 != -1;
            this.m_height[index] = 1 + MathUtils.max(this.m_height[child3], this.m_height[child4]);
            this.m_aabb[index].combine(this.m_aabb[child3], this.m_aabb[child4]);
        }
    }
    
    private final void removeLeaf(final int leaf) {
        if (leaf == this.m_root) {
            this.m_root = -1;
            return;
        }
        final int parent = this.m_parent[leaf];
        final int grandParent = this.m_parent[parent];
        final int parentChild1 = this.m_child1[parent];
        final int parentChild2 = this.m_child2[parent];
        int sibling;
        if (parentChild1 == leaf) {
            sibling = parentChild2;
        }
        else {
            sibling = parentChild1;
        }
        if (grandParent != -1) {
            if (this.m_child1[grandParent] == parent) {
                this.m_child1[grandParent] = sibling;
            }
            else {
                this.m_child2[grandParent] = sibling;
            }
            this.m_parent[sibling] = grandParent;
            this.freeNode(parent);
            for (int index = grandParent; index != -1; index = this.m_parent[index]) {
                index = this.balance(index);
                final int child1 = this.m_child1[index];
                final int child2 = this.m_child2[index];
                this.m_aabb[index].combine(this.m_aabb[child1], this.m_aabb[child2]);
                this.m_height[index] = 1 + MathUtils.max(this.m_height[child1], this.m_height[child2]);
            }
        }
        else {
            this.m_root = sibling;
            this.m_parent[sibling] = -1;
            this.freeNode(parent);
        }
    }
    
    private int balance(final int iA) {
        assert iA != -1;
        final int A = iA;
        if (this.m_child1[A] == -1 || this.m_height[A] < 2) {
            return iA;
        }
        final int iB = this.m_child1[A];
        final int iC = this.m_child2[A];
        assert 0 <= iB && iB < this.m_nodeCapacity;
        assert 0 <= iC && iC < this.m_nodeCapacity;
        final int B = iB;
        final int C = iC;
        final int balance = this.m_height[C] - this.m_height[B];
        if (balance > 1) {
            final int iF = this.m_child1[C];
            final int iG = this.m_child2[C];
            final int F = iF;
            final int G = iG;
            assert 0 <= iF && iF < this.m_nodeCapacity;
            assert 0 <= iG && iG < this.m_nodeCapacity;
            this.m_child1[C] = iA;
            final int[] parent = this.m_parent;
            final int n = C;
            final int n2 = this.m_parent[A];
            parent[n] = n2;
            final int cParent = n2;
            this.m_parent[A] = iC;
            if (cParent != -1) {
                if (this.m_child1[cParent] == iA) {
                    this.m_child1[cParent] = iC;
                }
                else {
                    assert this.m_child2[cParent] == iA;
                    this.m_child2[cParent] = iC;
                }
            }
            else {
                this.m_root = iC;
            }
            if (this.m_height[F] > this.m_height[G]) {
                this.m_child2[C] = iF;
                this.m_child2[A] = iG;
                this.m_parent[G] = iA;
                this.m_aabb[A].combine(this.m_aabb[B], this.m_aabb[G]);
                this.m_aabb[C].combine(this.m_aabb[A], this.m_aabb[F]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[B], this.m_height[G]);
                this.m_height[C] = 1 + MathUtils.max(this.m_height[A], this.m_height[F]);
            }
            else {
                this.m_child2[C] = iG;
                this.m_child2[A] = iF;
                this.m_parent[F] = iA;
                this.m_aabb[A].combine(this.m_aabb[B], this.m_aabb[F]);
                this.m_aabb[C].combine(this.m_aabb[A], this.m_aabb[G]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[B], this.m_height[F]);
                this.m_height[C] = 1 + MathUtils.max(this.m_height[A], this.m_height[G]);
            }
            return iC;
        }
        else {
            if (balance >= -1) {
                return iA;
            }
            final int iD = this.m_child1[B];
            final int iE = this.m_child2[B];
            final int D = iD;
            final int E = iE;
            assert 0 <= iD && iD < this.m_nodeCapacity;
            assert 0 <= iE && iE < this.m_nodeCapacity;
            this.m_child1[B] = iA;
            final int[] parent2 = this.m_parent;
            final int n3 = B;
            final int n4 = this.m_parent[A];
            parent2[n3] = n4;
            final int Bparent = n4;
            this.m_parent[A] = iB;
            if (Bparent != -1) {
                if (this.m_child1[Bparent] == iA) {
                    this.m_child1[Bparent] = iB;
                }
                else {
                    assert this.m_child2[Bparent] == iA;
                    this.m_child2[Bparent] = iB;
                }
            }
            else {
                this.m_root = iB;
            }
            if (this.m_height[D] > this.m_height[E]) {
                this.m_child2[B] = iD;
                this.m_child1[A] = iE;
                this.m_parent[E] = iA;
                this.m_aabb[A].combine(this.m_aabb[C], this.m_aabb[E]);
                this.m_aabb[B].combine(this.m_aabb[A], this.m_aabb[D]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[C], this.m_height[E]);
                this.m_height[B] = 1 + MathUtils.max(this.m_height[A], this.m_height[D]);
            }
            else {
                this.m_child2[B] = iE;
                this.m_child1[A] = iD;
                this.m_parent[D] = iA;
                this.m_aabb[A].combine(this.m_aabb[C], this.m_aabb[D]);
                this.m_aabb[B].combine(this.m_aabb[A], this.m_aabb[E]);
                this.m_height[A] = 1 + MathUtils.max(this.m_height[C], this.m_height[D]);
                this.m_height[B] = 1 + MathUtils.max(this.m_height[A], this.m_height[E]);
            }
            return iB;
        }
    }
    
    private void validateStructure(final int node) {
        if (node == -1) {
            return;
        }
        if (node == this.m_root && !DynamicTreeFlatNodes.$assertionsDisabled && this.m_parent[node] != -1) {
            throw new AssertionError();
        }
        final int child1 = this.m_child1[node];
        final int child2 = this.m_child2[node];
        if (child1 == -1) {
            assert child1 == -1;
            assert child2 == -1;
            assert this.m_height[node] == 0;
        }
        else {
            assert child1 != -1 && 0 <= child1 && child1 < this.m_nodeCapacity;
            assert child2 != -1 && 0 <= child2 && child2 < this.m_nodeCapacity;
            assert this.m_parent[child1] == node;
            assert this.m_parent[child2] == node;
            this.validateStructure(child1);
            this.validateStructure(child2);
        }
    }
    
    private void validateMetrics(final int node) {
        if (node == -1) {
            return;
        }
        final int child1 = this.m_child1[node];
        final int child2 = this.m_child2[node];
        if (child1 == -1) {
            assert child1 == -1;
            assert child2 == -1;
            assert this.m_height[node] == 0;
        }
        else {
            assert child1 != -1 && 0 <= child1 && child1 < this.m_nodeCapacity;
            assert child2 != child1 && 0 <= child2 && child2 < this.m_nodeCapacity;
            final int height1 = this.m_height[child1];
            final int height2 = this.m_height[child2];
            final int height3 = 1 + MathUtils.max(height1, height2);
            assert this.m_height[node] == height3;
            final AABB aabb = new AABB();
            aabb.combine(this.m_aabb[child1], this.m_aabb[child2]);
            assert aabb.lowerBound.equals(this.m_aabb[node].lowerBound);
            assert aabb.upperBound.equals(this.m_aabb[node].upperBound);
            this.validateMetrics(child1);
            this.validateMetrics(child2);
        }
    }
    
    @Override
    public void drawTree(final DebugDraw argDraw) {
        if (this.m_root == -1) {
            return;
        }
        final int height = this.computeHeight();
        this.drawTree(argDraw, this.m_root, 0, height);
    }
    
    public void drawTree(final DebugDraw argDraw, final int node, final int spot, final int height) {
        final AABB a = this.m_aabb[node];
        a.getVertices(this.drawVecs);
        this.color.set(1.0f, (height - spot) * 1.0f / height, (height - spot) * 1.0f / height);
        argDraw.drawPolygon(this.drawVecs, 4, this.color);
        argDraw.getViewportTranform().getWorldToScreen(a.upperBound, this.textVec);
        argDraw.drawString(this.textVec.x, this.textVec.y, node + "-" + (spot + 1) + "/" + height, this.color);
        final int c1 = this.m_child1[node];
        final int c2 = this.m_child2[node];
        if (c1 != -1) {
            this.drawTree(argDraw, c1, spot + 1, height);
        }
        if (c2 != -1) {
            this.drawTree(argDraw, c2, spot + 1, height);
        }
    }
}
