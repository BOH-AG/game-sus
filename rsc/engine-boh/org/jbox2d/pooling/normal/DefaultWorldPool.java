// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.pooling.normal;

import org.jbox2d.pooling.IDynamicStack;
import org.jbox2d.dynamics.contacts.ChainAndPolygonContact;
import org.jbox2d.dynamics.contacts.ChainAndCircleContact;
import org.jbox2d.dynamics.contacts.EdgeAndPolygonContact;
import org.jbox2d.dynamics.contacts.EdgeAndCircleContact;
import org.jbox2d.dynamics.contacts.PolygonAndCircleContact;
import org.jbox2d.dynamics.contacts.CircleContact;
import org.jbox2d.dynamics.contacts.PolygonContact;
import org.jbox2d.common.Settings;
import org.jbox2d.collision.Distance;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.collision.Collision;
import org.jbox2d.dynamics.contacts.Contact;
import java.util.HashMap;
import org.jbox2d.common.Rot;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec3;
import org.jbox2d.common.Vec2;
import org.jbox2d.pooling.IWorldPool;

public class DefaultWorldPool implements IWorldPool
{
    private final OrderedStack<Vec2> vecs;
    private final OrderedStack<Vec3> vec3s;
    private final OrderedStack<Mat22> mats;
    private final OrderedStack<Mat33> mat33s;
    private final OrderedStack<AABB> aabbs;
    private final OrderedStack<Rot> rots;
    private final HashMap<Integer, float[]> afloats;
    private final HashMap<Integer, int[]> aints;
    private final HashMap<Integer, Vec2[]> avecs;
    private final IWorldPool world;
    private final MutableStack<Contact> pcstack;
    private final MutableStack<Contact> ccstack;
    private final MutableStack<Contact> cpstack;
    private final MutableStack<Contact> ecstack;
    private final MutableStack<Contact> epstack;
    private final MutableStack<Contact> chcstack;
    private final MutableStack<Contact> chpstack;
    private final Collision collision;
    private final TimeOfImpact toi;
    private final Distance dist;
    
    public DefaultWorldPool(final int argSize, final int argContainerSize) {
        this.afloats = new HashMap<Integer, float[]>();
        this.aints = new HashMap<Integer, int[]>();
        this.avecs = new HashMap<Integer, Vec2[]>();
        this.world = this;
        this.pcstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
            @Override
            protected Contact newInstance() {
                return new PolygonContact(DefaultWorldPool.this.world);
            }
            
            @Override
            protected Contact[] newArray(final int size) {
                return new PolygonContact[size];
            }
        };
        this.ccstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
            @Override
            protected Contact newInstance() {
                return new CircleContact(DefaultWorldPool.this.world);
            }
            
            @Override
            protected Contact[] newArray(final int size) {
                return new CircleContact[size];
            }
        };
        this.cpstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
            @Override
            protected Contact newInstance() {
                return new PolygonAndCircleContact(DefaultWorldPool.this.world);
            }
            
            @Override
            protected Contact[] newArray(final int size) {
                return new PolygonAndCircleContact[size];
            }
        };
        this.ecstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
            @Override
            protected Contact newInstance() {
                return new EdgeAndCircleContact(DefaultWorldPool.this.world);
            }
            
            @Override
            protected Contact[] newArray(final int size) {
                return new EdgeAndCircleContact[size];
            }
        };
        this.epstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
            @Override
            protected Contact newInstance() {
                return new EdgeAndPolygonContact(DefaultWorldPool.this.world);
            }
            
            @Override
            protected Contact[] newArray(final int size) {
                return new EdgeAndPolygonContact[size];
            }
        };
        this.chcstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
            @Override
            protected Contact newInstance() {
                return new ChainAndCircleContact(DefaultWorldPool.this.world);
            }
            
            @Override
            protected Contact[] newArray(final int size) {
                return new ChainAndCircleContact[size];
            }
        };
        this.chpstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
            @Override
            protected Contact newInstance() {
                return new ChainAndPolygonContact(DefaultWorldPool.this.world);
            }
            
            @Override
            protected Contact[] newArray(final int size) {
                return new ChainAndPolygonContact[size];
            }
        };
        this.vecs = new OrderedStack<Vec2>(argSize, argContainerSize) {
            @Override
            protected Vec2 newInstance() {
                return new Vec2();
            }
        };
        this.vec3s = new OrderedStack<Vec3>(argSize, argContainerSize) {
            @Override
            protected Vec3 newInstance() {
                return new Vec3();
            }
        };
        this.mats = new OrderedStack<Mat22>(argSize, argContainerSize) {
            @Override
            protected Mat22 newInstance() {
                return new Mat22();
            }
        };
        this.aabbs = new OrderedStack<AABB>(argSize, argContainerSize) {
            @Override
            protected AABB newInstance() {
                return new AABB();
            }
        };
        this.rots = new OrderedStack<Rot>(argSize, argContainerSize) {
            @Override
            protected Rot newInstance() {
                return new Rot();
            }
        };
        this.mat33s = new OrderedStack<Mat33>(argSize, argContainerSize) {
            @Override
            protected Mat33 newInstance() {
                return new Mat33();
            }
        };
        this.dist = new Distance();
        this.collision = new Collision(this);
        this.toi = new TimeOfImpact(this);
    }
    
    @Override
    public final IDynamicStack<Contact> getPolyContactStack() {
        return this.pcstack;
    }
    
    @Override
    public final IDynamicStack<Contact> getCircleContactStack() {
        return this.ccstack;
    }
    
    @Override
    public final IDynamicStack<Contact> getPolyCircleContactStack() {
        return this.cpstack;
    }
    
    @Override
    public IDynamicStack<Contact> getEdgeCircleContactStack() {
        return this.ecstack;
    }
    
    @Override
    public IDynamicStack<Contact> getEdgePolyContactStack() {
        return this.epstack;
    }
    
    @Override
    public IDynamicStack<Contact> getChainCircleContactStack() {
        return this.chcstack;
    }
    
    @Override
    public IDynamicStack<Contact> getChainPolyContactStack() {
        return this.chpstack;
    }
    
    @Override
    public final Vec2 popVec2() {
        return this.vecs.pop();
    }
    
    @Override
    public final Vec2[] popVec2(final int argNum) {
        return this.vecs.pop(argNum);
    }
    
    @Override
    public final void pushVec2(final int argNum) {
        this.vecs.push(argNum);
    }
    
    @Override
    public final Vec3 popVec3() {
        return this.vec3s.pop();
    }
    
    @Override
    public final Vec3[] popVec3(final int argNum) {
        return this.vec3s.pop(argNum);
    }
    
    @Override
    public final void pushVec3(final int argNum) {
        this.vec3s.push(argNum);
    }
    
    @Override
    public final Mat22 popMat22() {
        return this.mats.pop();
    }
    
    @Override
    public final Mat22[] popMat22(final int argNum) {
        return this.mats.pop(argNum);
    }
    
    @Override
    public final void pushMat22(final int argNum) {
        this.mats.push(argNum);
    }
    
    @Override
    public final Mat33 popMat33() {
        return this.mat33s.pop();
    }
    
    @Override
    public final void pushMat33(final int argNum) {
        this.mat33s.push(argNum);
    }
    
    @Override
    public final AABB popAABB() {
        return this.aabbs.pop();
    }
    
    @Override
    public final AABB[] popAABB(final int argNum) {
        return this.aabbs.pop(argNum);
    }
    
    @Override
    public final void pushAABB(final int argNum) {
        this.aabbs.push(argNum);
    }
    
    @Override
    public final Rot popRot() {
        return this.rots.pop();
    }
    
    @Override
    public final void pushRot(final int num) {
        this.rots.push(num);
    }
    
    @Override
    public final Collision getCollision() {
        return this.collision;
    }
    
    @Override
    public final TimeOfImpact getTimeOfImpact() {
        return this.toi;
    }
    
    @Override
    public final Distance getDistance() {
        return this.dist;
    }
    
    @Override
    public final float[] getFloatArray(final int argLength) {
        if (!this.afloats.containsKey(argLength)) {
            this.afloats.put(argLength, new float[argLength]);
        }
        assert this.afloats.get(argLength).length == argLength : "Array not built with correct length";
        return this.afloats.get(argLength);
    }
    
    @Override
    public final int[] getIntArray(final int argLength) {
        if (!this.aints.containsKey(argLength)) {
            this.aints.put(argLength, new int[argLength]);
        }
        assert this.aints.get(argLength).length == argLength : "Array not built with correct length";
        return this.aints.get(argLength);
    }
    
    @Override
    public final Vec2[] getVec2Array(final int argLength) {
        if (!this.avecs.containsKey(argLength)) {
            final Vec2[] ray = new Vec2[argLength];
            for (int i = 0; i < argLength; ++i) {
                ray[i] = new Vec2();
            }
            this.avecs.put(argLength, ray);
        }
        assert this.avecs.get(argLength).length == argLength : "Array not built with correct length";
        return this.avecs.get(argLength);
    }
}
