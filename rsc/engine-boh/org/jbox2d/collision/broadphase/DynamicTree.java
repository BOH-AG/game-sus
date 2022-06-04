// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.broadphase;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.MathUtils;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Color3f;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;

public class DynamicTree implements BroadPhaseStrategy
{
    public static final int MAX_STACK_SIZE = 64;
    public static final int NULL_NODE = -1;
    private DynamicTreeNode m_root;
    private DynamicTreeNode[] m_nodes;
    private int m_nodeCount;
    private int m_nodeCapacity;
    private int m_freeList;
    private final Vec2[] drawVecs;
    private DynamicTreeNode[] nodeStack;
    private int nodeStackIndex;
    private final Vec2 r;
    private final AABB aabb;
    private final RayCastInput subInput;
    private final AABB combinedAABB;
    private final Color3f color;
    private final Vec2 textVec;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public DynamicTree() {
        this.drawVecs = new Vec2[4];
        this.nodeStack = new DynamicTreeNode[20];
        this.nodeStackIndex = 0;
        this.r = new Vec2();
        this.aabb = new AABB();
        this.subInput = new RayCastInput();
        this.combinedAABB = new AABB();
        this.color = new Color3f();
        this.textVec = new Vec2();
        this.m_root = null;
        this.m_nodeCount = 0;
        this.m_nodeCapacity = 16;
        this.m_nodes = new DynamicTreeNode[16];
        for (int i = this.m_nodeCapacity - 1; i >= 0; --i) {
            this.m_nodes[i] = new DynamicTreeNode(i);
            this.m_nodes[i].parent = ((i == this.m_nodeCapacity - 1) ? null : this.m_nodes[i + 1]);
            this.m_nodes[i].height = -1;
        }
        this.m_freeList = 0;
        for (int i = 0; i < this.drawVecs.length; ++i) {
            this.drawVecs[i] = new Vec2();
        }
    }
    
    @Override
    public final int createProxy(final AABB aabb, final Object userData) {
        assert aabb.isValid();
        final DynamicTreeNode node = this.allocateNode();
        final int proxyId = node.id;
        final AABB nodeAABB = node.aabb;
        nodeAABB.lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
        nodeAABB.lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
        nodeAABB.upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
        nodeAABB.upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
        node.userData = userData;
        this.insertLeaf(proxyId);
        return proxyId;
    }
    
    @Override
    public final void destroyProxy(final int proxyId) {
        assert 0 <= proxyId && proxyId < this.m_nodeCapacity;
        final DynamicTreeNode node = this.m_nodes[proxyId];
        assert node.child1 == null;
        this.removeLeaf(node);
        this.freeNode(node);
    }
    
    @Override
    public final boolean moveProxy(final int proxyId, final AABB aabb, final Vec2 displacement) {
        assert aabb.isValid();
        assert 0 <= proxyId && proxyId < this.m_nodeCapacity;
        final DynamicTreeNode node = this.m_nodes[proxyId];
        assert node.child1 == null;
        final AABB nodeAABB = node.aabb;
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
        assert 0 <= proxyId && proxyId < this.m_nodeCapacity;
        return this.m_nodes[proxyId].userData;
    }
    
    @Override
    public final AABB getFatAABB(final int proxyId) {
        assert 0 <= proxyId && proxyId < this.m_nodeCapacity;
        return this.m_nodes[proxyId].aabb;
    }
    
    @Override
    public final void query(final TreeCallback callback, final AABB aabb) {
        assert aabb.isValid();
        this.nodeStackIndex = 0;
        this.nodeStack[this.nodeStackIndex++] = this.m_root;
        while (this.nodeStackIndex > 0) {
            final DynamicTreeNode[] nodeStack = this.nodeStack;
            final int nodeStackIndex = this.nodeStackIndex - 1;
            this.nodeStackIndex = nodeStackIndex;
            final DynamicTreeNode node = nodeStack[nodeStackIndex];
            if (node == null) {
                continue;
            }
            if (!AABB.testOverlap(node.aabb, aabb)) {
                continue;
            }
            if (node.child1 == null) {
                final boolean proceed = callback.treeCallback(node.id);
                if (!proceed) {
                    return;
                }
                continue;
            }
            else {
                if (this.nodeStack.length - this.nodeStackIndex - 2 <= 0) {
                    final DynamicTreeNode[] newBuffer = new DynamicTreeNode[this.nodeStack.length * 2];
                    System.arraycopy(this.nodeStack, 0, newBuffer, 0, this.nodeStack.length);
                    this.nodeStack = newBuffer;
                }
                this.nodeStack[this.nodeStackIndex++] = node.child1;
                this.nodeStack[this.nodeStackIndex++] = node.child2;
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
            final DynamicTreeNode[] nodeStack = this.nodeStack;
            final int nodeStackIndex = this.nodeStackIndex - 1;
            this.nodeStackIndex = nodeStackIndex;
            final DynamicTreeNode node = nodeStack[nodeStackIndex];
            if (node == null) {
                continue;
            }
            final AABB nodeAABB = node.aabb;
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
            if (node.child1 == null) {
                this.subInput.p1.x = p1x;
                this.subInput.p1.y = p1y;
                this.subInput.p2.x = p2x;
                this.subInput.p2.y = p2y;
                this.subInput.maxFraction = maxFraction;
                final float value = callback.raycastCallback(this.subInput, node.id);
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
                if (this.nodeStack.length - this.nodeStackIndex - 2 <= 0) {
                    final DynamicTreeNode[] newBuffer = new DynamicTreeNode[this.nodeStack.length * 2];
                    System.arraycopy(this.nodeStack, 0, newBuffer, 0, this.nodeStack.length);
                    this.nodeStack = newBuffer;
                }
                this.nodeStack[this.nodeStackIndex++] = node.child1;
                this.nodeStack[this.nodeStackIndex++] = node.child2;
            }
        }
    }
    
    @Override
    public final int computeHeight() {
        return this.computeHeight(this.m_root);
    }
    
    private final int computeHeight(final DynamicTreeNode node) {
        assert 0 <= node.id && node.id < this.m_nodeCapacity;
        if (node.child1 == null) {
            return 0;
        }
        final int height1 = this.computeHeight(node.child1);
        final int height2 = this.computeHeight(node.child2);
        return 1 + MathUtils.max(height1, height2);
    }
    
    public void validate() {
        this.validateStructure(this.m_root);
        this.validateMetrics(this.m_root);
        int freeCount = 0;
        for (DynamicTreeNode freeNode = (this.m_freeList != -1) ? this.m_nodes[this.m_freeList] : null; freeNode != null; freeNode = freeNode.parent, ++freeCount) {
            assert 0 <= freeNode.id && freeNode.id < this.m_nodeCapacity;
            assert freeNode == this.m_nodes[freeNode.id];
        }
        assert this.getHeight() == this.computeHeight();
        assert this.m_nodeCount + freeCount == this.m_nodeCapacity;
    }
    
    @Override
    public int getHeight() {
        if (this.m_root == null) {
            return 0;
        }
        return this.m_root.height;
    }
    
    @Override
    public int getMaxBalance() {
        int maxBalance = 0;
        for (int i = 0; i < this.m_nodeCapacity; ++i) {
            final DynamicTreeNode node = this.m_nodes[i];
            if (node.height > 1) {
                assert node.child1 != null;
                final DynamicTreeNode child1 = node.child1;
                final DynamicTreeNode child2 = node.child2;
                final int balance = MathUtils.abs(child2.height - child1.height);
                maxBalance = MathUtils.max(maxBalance, balance);
            }
        }
        return maxBalance;
    }
    
    @Override
    public float getAreaRatio() {
        if (this.m_root == null) {
            return 0.0f;
        }
        final DynamicTreeNode root = this.m_root;
        final float rootArea = root.aabb.getPerimeter();
        float totalArea = 0.0f;
        for (int i = 0; i < this.m_nodeCapacity; ++i) {
            final DynamicTreeNode node = this.m_nodes[i];
            if (node.height >= 0) {
                totalArea += node.aabb.getPerimeter();
            }
        }
        return totalArea / rootArea;
    }
    
    public void rebuildBottomUp() {
        final int[] nodes = new int[this.m_nodeCount];
        int count = 0;
        for (int i = 0; i < this.m_nodeCapacity; ++i) {
            if (this.m_nodes[i].height >= 0) {
                final DynamicTreeNode node = this.m_nodes[i];
                if (node.child1 == null) {
                    node.parent = null;
                    nodes[count] = i;
                    ++count;
                }
                else {
                    this.freeNode(node);
                }
            }
        }
        final AABB b = new AABB();
        while (count > 1) {
            float minCost = Float.MAX_VALUE;
            int iMin = -1;
            int jMin = -1;
            for (int j = 0; j < count; ++j) {
                final AABB aabbi = this.m_nodes[nodes[j]].aabb;
                for (int k = j + 1; k < count; ++k) {
                    final AABB aabbj = this.m_nodes[nodes[k]].aabb;
                    b.combine(aabbi, aabbj);
                    final float cost = b.getPerimeter();
                    if (cost < minCost) {
                        iMin = j;
                        jMin = k;
                        minCost = cost;
                    }
                }
            }
            final int index1 = nodes[iMin];
            final int index2 = nodes[jMin];
            final DynamicTreeNode child1 = this.m_nodes[index1];
            final DynamicTreeNode child2 = this.m_nodes[index2];
            final DynamicTreeNode parent = this.allocateNode();
            parent.child1 = child1;
            parent.child2 = child2;
            parent.height = 1 + MathUtils.max(child1.height, child2.height);
            parent.aabb.combine(child1.aabb, child2.aabb);
            parent.parent = null;
            child1.parent = parent;
            child2.parent = parent;
            nodes[jMin] = nodes[count - 1];
            nodes[iMin] = parent.id;
            --count;
        }
        this.m_root = this.m_nodes[nodes[0]];
        this.validate();
    }
    
    private final DynamicTreeNode allocateNode() {
        if (this.m_freeList == -1) {
            assert this.m_nodeCount == this.m_nodeCapacity;
            final DynamicTreeNode[] old = this.m_nodes;
            this.m_nodeCapacity *= 2;
            System.arraycopy(old, 0, this.m_nodes = new DynamicTreeNode[this.m_nodeCapacity], 0, old.length);
            for (int i = this.m_nodeCapacity - 1; i >= this.m_nodeCount; --i) {
                this.m_nodes[i] = new DynamicTreeNode(i);
                this.m_nodes[i].parent = ((i == this.m_nodeCapacity - 1) ? null : this.m_nodes[i + 1]);
                this.m_nodes[i].height = -1;
            }
            this.m_freeList = this.m_nodeCount;
        }
        final int nodeId = this.m_freeList;
        final DynamicTreeNode treeNode = this.m_nodes[nodeId];
        this.m_freeList = ((treeNode.parent != null) ? treeNode.parent.id : -1);
        treeNode.parent = null;
        treeNode.child1 = null;
        treeNode.child2 = null;
        treeNode.height = 0;
        treeNode.userData = null;
        ++this.m_nodeCount;
        return treeNode;
    }
    
    private final void freeNode(final DynamicTreeNode node) {
        assert node != null;
        assert 0 < this.m_nodeCount;
        node.parent = ((this.m_freeList != -1) ? this.m_nodes[this.m_freeList] : null);
        node.height = -1;
        this.m_freeList = node.id;
        --this.m_nodeCount;
    }
    
    private final void insertLeaf(final int leaf_index) {
        final DynamicTreeNode leaf = this.m_nodes[leaf_index];
        if (this.m_root == null) {
            this.m_root = leaf;
            this.m_root.parent = null;
            return;
        }
        final AABB leafAABB = leaf.aabb;
        DynamicTreeNode index = this.m_root;
        while (index.child1 != null) {
            final DynamicTreeNode node = index;
            final DynamicTreeNode child1 = node.child1;
            final DynamicTreeNode child2 = node.child2;
            final float area = node.aabb.getPerimeter();
            this.combinedAABB.combine(node.aabb, leafAABB);
            final float combinedArea = this.combinedAABB.getPerimeter();
            final float cost = 2.0f * combinedArea;
            final float inheritanceCost = 2.0f * (combinedArea - area);
            float cost2;
            if (child1.child1 == null) {
                this.combinedAABB.combine(leafAABB, child1.aabb);
                cost2 = this.combinedAABB.getPerimeter() + inheritanceCost;
            }
            else {
                this.combinedAABB.combine(leafAABB, child1.aabb);
                final float oldArea = child1.aabb.getPerimeter();
                final float newArea = this.combinedAABB.getPerimeter();
                cost2 = newArea - oldArea + inheritanceCost;
            }
            float cost3;
            if (child2.child1 == null) {
                this.combinedAABB.combine(leafAABB, child2.aabb);
                cost3 = this.combinedAABB.getPerimeter() + inheritanceCost;
            }
            else {
                this.combinedAABB.combine(leafAABB, child2.aabb);
                final float oldArea2 = child2.aabb.getPerimeter();
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
        final DynamicTreeNode sibling = index;
        final DynamicTreeNode oldParent = this.m_nodes[sibling.id].parent;
        final DynamicTreeNode newParent = this.allocateNode();
        newParent.parent = oldParent;
        newParent.userData = null;
        newParent.aabb.combine(leafAABB, sibling.aabb);
        newParent.height = sibling.height + 1;
        if (oldParent != null) {
            if (oldParent.child1 == sibling) {
                oldParent.child1 = newParent;
            }
            else {
                oldParent.child2 = newParent;
            }
            newParent.child1 = sibling;
            newParent.child2 = leaf;
            sibling.parent = newParent;
            leaf.parent = newParent;
        }
        else {
            newParent.child1 = sibling;
            newParent.child2 = leaf;
            sibling.parent = newParent;
            leaf.parent = newParent;
            this.m_root = newParent;
        }
        for (index = leaf.parent; index != null; index = index.parent) {
            index = this.balance(index);
            final DynamicTreeNode child3 = index.child1;
            final DynamicTreeNode child4 = index.child2;
            assert child3 != null;
            assert child4 != null;
            index.height = 1 + MathUtils.max(child3.height, child4.height);
            index.aabb.combine(child3.aabb, child4.aabb);
        }
    }
    
    private final void removeLeaf(final DynamicTreeNode leaf) {
        if (leaf == this.m_root) {
            this.m_root = null;
            return;
        }
        final DynamicTreeNode parent = leaf.parent;
        final DynamicTreeNode grandParent = parent.parent;
        DynamicTreeNode sibling;
        if (parent.child1 == leaf) {
            sibling = parent.child2;
        }
        else {
            sibling = parent.child1;
        }
        if (grandParent != null) {
            if (grandParent.child1 == parent) {
                grandParent.child1 = sibling;
            }
            else {
                grandParent.child2 = sibling;
            }
            sibling.parent = grandParent;
            this.freeNode(parent);
            for (DynamicTreeNode index = grandParent; index != null; index = index.parent) {
                index = this.balance(index);
                final DynamicTreeNode child1 = index.child1;
                final DynamicTreeNode child2 = index.child2;
                index.aabb.combine(child1.aabb, child2.aabb);
                index.height = 1 + MathUtils.max(child1.height, child2.height);
            }
        }
        else {
            this.m_root = sibling;
            sibling.parent = null;
            this.freeNode(parent);
        }
    }
    
    private DynamicTreeNode balance(final DynamicTreeNode iA) {
        assert iA != null;
        final DynamicTreeNode A = iA;
        if (A.child1 == null || A.height < 2) {
            return iA;
        }
        final DynamicTreeNode iB = A.child1;
        final DynamicTreeNode iC = A.child2;
        assert 0 <= iB.id && iB.id < this.m_nodeCapacity;
        assert 0 <= iC.id && iC.id < this.m_nodeCapacity;
        final DynamicTreeNode B = iB;
        final DynamicTreeNode C = iC;
        final int balance = C.height - B.height;
        if (balance > 1) {
            final DynamicTreeNode iF = C.child1;
            final DynamicTreeNode iG = C.child2;
            final DynamicTreeNode F = iF;
            final DynamicTreeNode G = iG;
            assert F != null;
            assert G != null;
            assert 0 <= iF.id && iF.id < this.m_nodeCapacity;
            assert 0 <= iG.id && iG.id < this.m_nodeCapacity;
            C.child1 = iA;
            C.parent = A.parent;
            A.parent = iC;
            if (C.parent != null) {
                if (C.parent.child1 == iA) {
                    C.parent.child1 = iC;
                }
                else {
                    assert C.parent.child2 == iA;
                    C.parent.child2 = iC;
                }
            }
            else {
                this.m_root = iC;
            }
            if (F.height > G.height) {
                C.child2 = iF;
                A.child2 = iG;
                G.parent = iA;
                A.aabb.combine(B.aabb, G.aabb);
                C.aabb.combine(A.aabb, F.aabb);
                A.height = 1 + MathUtils.max(B.height, G.height);
                C.height = 1 + MathUtils.max(A.height, F.height);
            }
            else {
                C.child2 = iG;
                A.child2 = iF;
                F.parent = iA;
                A.aabb.combine(B.aabb, F.aabb);
                C.aabb.combine(A.aabb, G.aabb);
                A.height = 1 + MathUtils.max(B.height, F.height);
                C.height = 1 + MathUtils.max(A.height, G.height);
            }
            return iC;
        }
        else {
            if (balance >= -1) {
                return iA;
            }
            final DynamicTreeNode iD = B.child1;
            final DynamicTreeNode iE = B.child2;
            final DynamicTreeNode D = iD;
            final DynamicTreeNode E = iE;
            assert 0 <= iD.id && iD.id < this.m_nodeCapacity;
            assert 0 <= iE.id && iE.id < this.m_nodeCapacity;
            B.child1 = iA;
            B.parent = A.parent;
            A.parent = iB;
            if (B.parent != null) {
                if (B.parent.child1 == iA) {
                    B.parent.child1 = iB;
                }
                else {
                    assert B.parent.child2 == iA;
                    B.parent.child2 = iB;
                }
            }
            else {
                this.m_root = iB;
            }
            if (D.height > E.height) {
                B.child2 = iD;
                A.child1 = iE;
                E.parent = iA;
                A.aabb.combine(C.aabb, E.aabb);
                B.aabb.combine(A.aabb, D.aabb);
                A.height = 1 + MathUtils.max(C.height, E.height);
                B.height = 1 + MathUtils.max(A.height, D.height);
            }
            else {
                B.child2 = iE;
                A.child1 = iD;
                D.parent = iA;
                A.aabb.combine(C.aabb, D.aabb);
                B.aabb.combine(A.aabb, E.aabb);
                A.height = 1 + MathUtils.max(C.height, D.height);
                B.height = 1 + MathUtils.max(A.height, E.height);
            }
            return iB;
        }
    }
    
    private void validateStructure(final DynamicTreeNode node) {
        if (node == null) {
            return;
        }
        assert node == this.m_nodes[node.id];
        if (node == this.m_root && !DynamicTree.$assertionsDisabled && node.parent != null) {
            throw new AssertionError();
        }
        final DynamicTreeNode child1 = node.child1;
        final DynamicTreeNode child2 = node.child2;
        if (node.child1 == null) {
            assert child1 == null;
            assert child2 == null;
            assert node.height == 0;
        }
        else {
            assert child1 != null && 0 <= child1.id && child1.id < this.m_nodeCapacity;
            assert child2 != null && 0 <= child2.id && child2.id < this.m_nodeCapacity;
            assert child1.parent == node;
            assert child2.parent == node;
            this.validateStructure(child1);
            this.validateStructure(child2);
        }
    }
    
    private void validateMetrics(final DynamicTreeNode node) {
        if (node == null) {
            return;
        }
        final DynamicTreeNode child1 = node.child1;
        final DynamicTreeNode child2 = node.child2;
        if (node.child1 == null) {
            assert child1 == null;
            assert child2 == null;
            assert node.height == 0;
        }
        else {
            assert child1 != null && 0 <= child1.id && child1.id < this.m_nodeCapacity;
            assert child2 != null && 0 <= child2.id && child2.id < this.m_nodeCapacity;
            final int height1 = child1.height;
            final int height2 = child2.height;
            final int height3 = 1 + MathUtils.max(height1, height2);
            assert node.height == height3;
            final AABB aabb = new AABB();
            aabb.combine(child1.aabb, child2.aabb);
            assert aabb.lowerBound.equals(node.aabb.lowerBound);
            assert aabb.upperBound.equals(node.aabb.upperBound);
            this.validateMetrics(child1);
            this.validateMetrics(child2);
        }
    }
    
    @Override
    public void drawTree(final DebugDraw argDraw) {
        if (this.m_root == null) {
            return;
        }
        final int height = this.computeHeight();
        this.drawTree(argDraw, this.m_root, 0, height);
    }
    
    public void drawTree(final DebugDraw argDraw, final DynamicTreeNode node, final int spot, final int height) {
        node.aabb.getVertices(this.drawVecs);
        this.color.set(1.0f, (height - spot) * 1.0f / height, (height - spot) * 1.0f / height);
        argDraw.drawPolygon(this.drawVecs, 4, this.color);
        argDraw.getViewportTranform().getWorldToScreen(node.aabb.upperBound, this.textVec);
        argDraw.drawString(this.textVec.x, this.textVec.y, node.id + "-" + (spot + 1) + "/" + height, this.color);
        if (node.child1 != null) {
            this.drawTree(argDraw, node.child1, spot + 1, height);
        }
        if (node.child2 != null) {
            this.drawTree(argDraw, node.child2, spot + 1, height);
        }
    }
}
