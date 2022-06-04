// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling;

import org.jbox2d.collision.Distance;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.collision.Collision;
import org.jbox2d.common.Rot;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec3;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;

public interface IWorldPool
{
    IDynamicStack<Contact> getPolyContactStack();
    
    IDynamicStack<Contact> getCircleContactStack();
    
    IDynamicStack<Contact> getPolyCircleContactStack();
    
    IDynamicStack<Contact> getEdgeCircleContactStack();
    
    IDynamicStack<Contact> getEdgePolyContactStack();
    
    IDynamicStack<Contact> getChainCircleContactStack();
    
    IDynamicStack<Contact> getChainPolyContactStack();
    
    Vec2 popVec2();
    
    Vec2[] popVec2(final int p0);
    
    void pushVec2(final int p0);
    
    Vec3 popVec3();
    
    Vec3[] popVec3(final int p0);
    
    void pushVec3(final int p0);
    
    Mat22 popMat22();
    
    Mat22[] popMat22(final int p0);
    
    void pushMat22(final int p0);
    
    Mat33 popMat33();
    
    void pushMat33(final int p0);
    
    AABB popAABB();
    
    AABB[] popAABB(final int p0);
    
    void pushAABB(final int p0);
    
    Rot popRot();
    
    void pushRot(final int p0);
    
    Collision getCollision();
    
    TimeOfImpact getTimeOfImpact();
    
    Distance getDistance();
    
    float[] getFloatArray(final int p0);
    
    int[] getIntArray(final int p0);
    
    Vec2[] getVec2Array(final int p0);
}
