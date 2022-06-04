// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.collision.broadphase;

import org.jbox2d.collision.AABB;

public class DynamicTreeNode
{
    public final AABB aabb;
    public Object userData;
    protected DynamicTreeNode parent;
    protected DynamicTreeNode child1;
    protected DynamicTreeNode child2;
    protected final int id;
    protected int height;
    
    public Object getUserData() {
        return this.userData;
    }
    
    public void setUserData(final Object argData) {
        this.userData = argData;
    }
    
    protected DynamicTreeNode(final int id) {
        this.aabb = new AABB();
        this.id = id;
    }
}
