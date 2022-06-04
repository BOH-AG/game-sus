// 
// Decompiled by Procyon v0.5.36
// 

package org.jbox2d.dynamics;

import org.jbox2d.particle.ParticleBodyContact;
import org.jbox2d.particle.ParticleContact;
import org.jbox2d.particle.ParticleGroup;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.particle.ParticleDef;
import org.jbox2d.particle.ParticleColor;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.dynamics.joints.PulleyJoint;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.callbacks.ParticleRaycastCallback;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.callbacks.ParticleQueryCallback;
import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.ContactFilter;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.pooling.IDynamicStack;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.collision.broadphase.DefaultBroadPhaseBuffer;
import org.jbox2d.collision.broadphase.BroadPhaseStrategy;
import org.jbox2d.collision.broadphase.DynamicTree;
import org.jbox2d.pooling.normal.DefaultWorldPool;
import org.jbox2d.common.Sweep;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.pooling.arrays.Vec2Array;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Timer;
import org.jbox2d.dynamics.contacts.ContactRegister;
import org.jbox2d.particle.ParticleSystem;
import org.jbox2d.pooling.IWorldPool;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.ParticleDestructionListener;
import org.jbox2d.callbacks.DestructionListener;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;

public class World
{
    public static final int WORLD_POOL_SIZE = 100;
    public static final int WORLD_POOL_CONTAINER_SIZE = 10;
    public static final int NEW_FIXTURE = 1;
    public static final int LOCKED = 2;
    public static final int CLEAR_FORCES = 4;
    public int activeContacts;
    public int contactPoolCount;
    protected int m_flags;
    protected ContactManager m_contactManager;
    private Body m_bodyList;
    private Joint m_jointList;
    private int m_bodyCount;
    private int m_jointCount;
    private final Vec2 m_gravity;
    private boolean m_allowSleep;
    private DestructionListener m_destructionListener;
    private ParticleDestructionListener m_particleDestructionListener;
    private DebugDraw m_debugDraw;
    private final IWorldPool pool;
    private float m_inv_dt0;
    private boolean m_warmStarting;
    private boolean m_continuousPhysics;
    private boolean m_subStepping;
    private boolean m_stepComplete;
    private Profile m_profile;
    private ParticleSystem m_particleSystem;
    private ContactRegister[][] contactStacks;
    private final TimeStep step;
    private final Timer stepTimer;
    private final Timer tempTimer;
    private final Color3f color;
    private final Transform xf;
    private final Vec2 cA;
    private final Vec2 cB;
    private final Vec2Array avs;
    private final WorldQueryWrapper wqwrapper;
    private final WorldRayCastWrapper wrcwrapper;
    private final RayCastInput input;
    private final Island island;
    private Body[] stack;
    private final Timer broadphaseTimer;
    private final Island toiIsland;
    private final TimeOfImpact.TOIInput toiInput;
    private final TimeOfImpact.TOIOutput toiOutput;
    private final TimeStep subStep;
    private final Body[] tempBodies;
    private final Sweep backup1;
    private final Sweep backup2;
    private static Integer LIQUID_INT;
    private float liquidLength;
    private float averageLinearVel;
    private final Vec2 liquidOffset;
    private final Vec2 circCenterMoved;
    private final Color3f liquidColor;
    private final Vec2 center;
    private final Vec2 axis;
    private final Vec2 v1;
    private final Vec2 v2;
    private final Vec2Array tlvertices;
    
    public World(final Vec2 gravity) {
        this(gravity, new DefaultWorldPool(100, 10));
    }
    
    public World(final Vec2 gravity, final IWorldPool pool) {
        this(gravity, pool, new DynamicTree());
    }
    
    public World(final Vec2 gravity, final IWorldPool pool, final BroadPhaseStrategy strategy) {
        this(gravity, pool, new DefaultBroadPhaseBuffer(strategy));
    }
    
    public World(final Vec2 gravity, final IWorldPool pool, final BroadPhase broadPhase) {
        this.activeContacts = 0;
        this.contactPoolCount = 0;
        this.m_gravity = new Vec2();
        this.contactStacks = new ContactRegister[ShapeType.values().length][ShapeType.values().length];
        this.step = new TimeStep();
        this.stepTimer = new Timer();
        this.tempTimer = new Timer();
        this.color = new Color3f();
        this.xf = new Transform();
        this.cA = new Vec2();
        this.cB = new Vec2();
        this.avs = new Vec2Array();
        this.wqwrapper = new WorldQueryWrapper();
        this.wrcwrapper = new WorldRayCastWrapper();
        this.input = new RayCastInput();
        this.island = new Island();
        this.stack = new Body[10];
        this.broadphaseTimer = new Timer();
        this.toiIsland = new Island();
        this.toiInput = new TimeOfImpact.TOIInput();
        this.toiOutput = new TimeOfImpact.TOIOutput();
        this.subStep = new TimeStep();
        this.tempBodies = new Body[2];
        this.backup1 = new Sweep();
        this.backup2 = new Sweep();
        this.liquidLength = 0.12f;
        this.averageLinearVel = -1.0f;
        this.liquidOffset = new Vec2();
        this.circCenterMoved = new Vec2();
        this.liquidColor = new Color3f(0.4f, 0.4f, 1.0f);
        this.center = new Vec2();
        this.axis = new Vec2();
        this.v1 = new Vec2();
        this.v2 = new Vec2();
        this.tlvertices = new Vec2Array();
        this.pool = pool;
        this.m_destructionListener = null;
        this.m_debugDraw = null;
        this.m_bodyList = null;
        this.m_jointList = null;
        this.m_bodyCount = 0;
        this.m_jointCount = 0;
        this.m_warmStarting = true;
        this.m_continuousPhysics = true;
        this.m_subStepping = false;
        this.m_stepComplete = true;
        this.m_allowSleep = true;
        this.m_gravity.set(gravity);
        this.m_flags = 4;
        this.m_inv_dt0 = 0.0f;
        this.m_contactManager = new ContactManager(this, broadPhase);
        this.m_profile = new Profile();
        this.m_particleSystem = new ParticleSystem(this);
        this.initializeRegisters();
    }
    
    public void setAllowSleep(final boolean flag) {
        if (flag == this.m_allowSleep) {
            return;
        }
        if (!(this.m_allowSleep = flag)) {
            for (Body b = this.m_bodyList; b != null; b = b.m_next) {
                b.setAwake(true);
            }
        }
    }
    
    public void setSubStepping(final boolean subStepping) {
        this.m_subStepping = subStepping;
    }
    
    public boolean isSubStepping() {
        return this.m_subStepping;
    }
    
    public boolean isAllowSleep() {
        return this.m_allowSleep;
    }
    
    private void addType(final IDynamicStack<Contact> creator, final ShapeType type1, final ShapeType type2) {
        final ContactRegister register = new ContactRegister();
        register.creator = creator;
        register.primary = true;
        this.contactStacks[type1.ordinal()][type2.ordinal()] = register;
        if (type1 != type2) {
            final ContactRegister register2 = new ContactRegister();
            register2.creator = creator;
            register2.primary = false;
            this.contactStacks[type2.ordinal()][type1.ordinal()] = register2;
        }
    }
    
    private void initializeRegisters() {
        this.addType(this.pool.getCircleContactStack(), ShapeType.CIRCLE, ShapeType.CIRCLE);
        this.addType(this.pool.getPolyCircleContactStack(), ShapeType.POLYGON, ShapeType.CIRCLE);
        this.addType(this.pool.getPolyContactStack(), ShapeType.POLYGON, ShapeType.POLYGON);
        this.addType(this.pool.getEdgeCircleContactStack(), ShapeType.EDGE, ShapeType.CIRCLE);
        this.addType(this.pool.getEdgePolyContactStack(), ShapeType.EDGE, ShapeType.POLYGON);
        this.addType(this.pool.getChainCircleContactStack(), ShapeType.CHAIN, ShapeType.CIRCLE);
        this.addType(this.pool.getChainPolyContactStack(), ShapeType.CHAIN, ShapeType.POLYGON);
    }
    
    public DestructionListener getDestructionListener() {
        return this.m_destructionListener;
    }
    
    public ParticleDestructionListener getParticleDestructionListener() {
        return this.m_particleDestructionListener;
    }
    
    public void setParticleDestructionListener(final ParticleDestructionListener listener) {
        this.m_particleDestructionListener = listener;
    }
    
    public Contact popContact(final Fixture fixtureA, final int indexA, final Fixture fixtureB, final int indexB) {
        final ShapeType type1 = fixtureA.getType();
        final ShapeType type2 = fixtureB.getType();
        final ContactRegister reg = this.contactStacks[type1.ordinal()][type2.ordinal()];
        if (reg == null) {
            return null;
        }
        if (reg.primary) {
            final Contact c = reg.creator.pop();
            c.init(fixtureA, indexA, fixtureB, indexB);
            return c;
        }
        final Contact c = reg.creator.pop();
        c.init(fixtureB, indexB, fixtureA, indexA);
        return c;
    }
    
    public void pushContact(final Contact contact) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();
        if (contact.m_manifold.pointCount > 0 && !fixtureA.isSensor() && !fixtureB.isSensor()) {
            fixtureA.getBody().setAwake(true);
            fixtureB.getBody().setAwake(true);
        }
        final ShapeType type1 = fixtureA.getType();
        final ShapeType type2 = fixtureB.getType();
        final IDynamicStack<Contact> creator = this.contactStacks[type1.ordinal()][type2.ordinal()].creator;
        creator.push(contact);
    }
    
    public IWorldPool getPool() {
        return this.pool;
    }
    
    public void setDestructionListener(final DestructionListener listener) {
        this.m_destructionListener = listener;
    }
    
    public void setContactFilter(final ContactFilter filter) {
        this.m_contactManager.m_contactFilter = filter;
    }
    
    public void setContactListener(final ContactListener listener) {
        this.m_contactManager.m_contactListener = listener;
    }
    
    public void setDebugDraw(final DebugDraw debugDraw) {
        this.m_debugDraw = debugDraw;
    }
    
    public Body createBody(final BodyDef def) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return null;
        }
        final Body b = new Body(def, this);
        b.m_prev = null;
        b.m_next = this.m_bodyList;
        if (this.m_bodyList != null) {
            this.m_bodyList.m_prev = b;
        }
        this.m_bodyList = b;
        ++this.m_bodyCount;
        return b;
    }
    
    public void destroyBody(final Body body) {
        assert this.m_bodyCount > 0;
        assert !this.isLocked();
        if (this.isLocked()) {
            return;
        }
        JointEdge je = body.m_jointList;
        while (je != null) {
            final JointEdge je2 = je;
            je = je.next;
            if (this.m_destructionListener != null) {
                this.m_destructionListener.sayGoodbye(je2.joint);
            }
            this.destroyJoint(je2.joint);
            body.m_jointList = je;
        }
        body.m_jointList = null;
        ContactEdge ce = body.m_contactList;
        while (ce != null) {
            final ContactEdge ce2 = ce;
            ce = ce.next;
            this.m_contactManager.destroy(ce2.contact);
        }
        body.m_contactList = null;
        Fixture f = body.m_fixtureList;
        while (f != null) {
            final Fixture f2 = f;
            f = f.m_next;
            if (this.m_destructionListener != null) {
                this.m_destructionListener.sayGoodbye(f2);
            }
            f2.destroyProxies(this.m_contactManager.m_broadPhase);
            f2.destroy();
            body.m_fixtureList = f;
            --body.m_fixtureCount;
        }
        body.m_fixtureList = null;
        body.m_fixtureCount = 0;
        if (body.m_prev != null) {
            body.m_prev.m_next = body.m_next;
        }
        if (body.m_next != null) {
            body.m_next.m_prev = body.m_prev;
        }
        if (body == this.m_bodyList) {
            this.m_bodyList = body.m_next;
        }
        --this.m_bodyCount;
    }
    
    public Joint createJoint(final JointDef def) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return null;
        }
        final Joint j = Joint.create(this, def);
        j.m_prev = null;
        j.m_next = this.m_jointList;
        if (this.m_jointList != null) {
            this.m_jointList.m_prev = j;
        }
        this.m_jointList = j;
        ++this.m_jointCount;
        j.m_edgeA.joint = j;
        j.m_edgeA.other = j.getBodyB();
        j.m_edgeA.prev = null;
        j.m_edgeA.next = j.getBodyA().m_jointList;
        if (j.getBodyA().m_jointList != null) {
            j.getBodyA().m_jointList.prev = j.m_edgeA;
        }
        j.getBodyA().m_jointList = j.m_edgeA;
        j.m_edgeB.joint = j;
        j.m_edgeB.other = j.getBodyA();
        j.m_edgeB.prev = null;
        j.m_edgeB.next = j.getBodyB().m_jointList;
        if (j.getBodyB().m_jointList != null) {
            j.getBodyB().m_jointList.prev = j.m_edgeB;
        }
        j.getBodyB().m_jointList = j.m_edgeB;
        final Body bodyA = def.bodyA;
        final Body bodyB = def.bodyB;
        if (!def.collideConnected) {
            for (ContactEdge edge = bodyB.getContactList(); edge != null; edge = edge.next) {
                if (edge.other == bodyA) {
                    edge.contact.flagForFiltering();
                }
            }
        }
        return j;
    }
    
    public void destroyJoint(final Joint j) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return;
        }
        final boolean collideConnected = j.getCollideConnected();
        if (j.m_prev != null) {
            j.m_prev.m_next = j.m_next;
        }
        if (j.m_next != null) {
            j.m_next.m_prev = j.m_prev;
        }
        if (j == this.m_jointList) {
            this.m_jointList = j.m_next;
        }
        final Body bodyA = j.getBodyA();
        final Body bodyB = j.getBodyB();
        bodyA.setAwake(true);
        bodyB.setAwake(true);
        if (j.m_edgeA.prev != null) {
            j.m_edgeA.prev.next = j.m_edgeA.next;
        }
        if (j.m_edgeA.next != null) {
            j.m_edgeA.next.prev = j.m_edgeA.prev;
        }
        if (j.m_edgeA == bodyA.m_jointList) {
            bodyA.m_jointList = j.m_edgeA.next;
        }
        j.m_edgeA.prev = null;
        j.m_edgeA.next = null;
        if (j.m_edgeB.prev != null) {
            j.m_edgeB.prev.next = j.m_edgeB.next;
        }
        if (j.m_edgeB.next != null) {
            j.m_edgeB.next.prev = j.m_edgeB.prev;
        }
        if (j.m_edgeB == bodyB.m_jointList) {
            bodyB.m_jointList = j.m_edgeB.next;
        }
        j.m_edgeB.prev = null;
        j.m_edgeB.next = null;
        Joint.destroy(j);
        assert this.m_jointCount > 0;
        --this.m_jointCount;
        if (!collideConnected) {
            for (ContactEdge edge = bodyB.getContactList(); edge != null; edge = edge.next) {
                if (edge.other == bodyA) {
                    edge.contact.flagForFiltering();
                }
            }
        }
    }
    
    public void step(final float dt, final int velocityIterations, final int positionIterations) {
        this.stepTimer.reset();
        this.tempTimer.reset();
        if ((this.m_flags & 0x1) == 0x1) {
            this.m_contactManager.findNewContacts();
            this.m_flags &= 0xFFFFFFFE;
        }
        this.m_flags |= 0x2;
        this.step.dt = dt;
        this.step.velocityIterations = velocityIterations;
        this.step.positionIterations = positionIterations;
        if (dt > 0.0f) {
            this.step.inv_dt = 1.0f / dt;
        }
        else {
            this.step.inv_dt = 0.0f;
        }
        this.step.dtRatio = this.m_inv_dt0 * dt;
        this.step.warmStarting = this.m_warmStarting;
        this.m_profile.stepInit.record(this.tempTimer.getMilliseconds());
        this.tempTimer.reset();
        this.m_contactManager.collide();
        this.m_profile.collide.record(this.tempTimer.getMilliseconds());
        if (this.m_stepComplete && this.step.dt > 0.0f) {
            this.tempTimer.reset();
            this.m_particleSystem.solve(this.step);
            this.m_profile.solveParticleSystem.record(this.tempTimer.getMilliseconds());
            this.tempTimer.reset();
            this.solve(this.step);
            this.m_profile.solve.record(this.tempTimer.getMilliseconds());
        }
        if (this.m_continuousPhysics && this.step.dt > 0.0f) {
            this.tempTimer.reset();
            this.solveTOI(this.step);
            this.m_profile.solveTOI.record(this.tempTimer.getMilliseconds());
        }
        if (this.step.dt > 0.0f) {
            this.m_inv_dt0 = this.step.inv_dt;
        }
        if ((this.m_flags & 0x4) == 0x4) {
            this.clearForces();
        }
        this.m_flags &= 0xFFFFFFFD;
        this.m_profile.step.record(this.stepTimer.getMilliseconds());
    }
    
    public void clearForces() {
        for (Body body = this.m_bodyList; body != null; body = body.getNext()) {
            body.m_force.setZero();
            body.m_torque = 0.0f;
        }
    }
    
    public void drawDebugData() {
        if (this.m_debugDraw == null) {
            return;
        }
        final int flags = this.m_debugDraw.getFlags();
        final boolean wireframe = (flags & 0x80) != 0x0;
        if ((flags & 0x2) != 0x0) {
            for (Body b = this.m_bodyList; b != null; b = b.getNext()) {
                this.xf.set(b.getTransform());
                for (Fixture f = b.getFixtureList(); f != null; f = f.getNext()) {
                    if (!b.isActive()) {
                        this.color.set(0.5f, 0.5f, 0.3f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                    }
                    else if (b.getType() == BodyType.STATIC) {
                        this.color.set(0.5f, 0.9f, 0.3f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                    }
                    else if (b.getType() == BodyType.KINEMATIC) {
                        this.color.set(0.5f, 0.5f, 0.9f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                    }
                    else if (!b.isAwake()) {
                        this.color.set(0.5f, 0.5f, 0.5f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                    }
                    else {
                        this.color.set(0.9f, 0.7f, 0.7f);
                        this.drawShape(f, this.xf, this.color, wireframe);
                    }
                }
            }
            this.drawParticleSystem(this.m_particleSystem);
        }
        if ((flags & 0x4) != 0x0) {
            for (Joint j = this.m_jointList; j != null; j = j.getNext()) {
                this.drawJoint(j);
            }
        }
        if ((flags & 0x10) != 0x0) {
            this.color.set(0.3f, 0.9f, 0.9f);
            for (Contact c = this.m_contactManager.m_contactList; c != null; c = c.getNext()) {
                final Fixture fixtureA = c.getFixtureA();
                final Fixture fixtureB = c.getFixtureB();
                fixtureA.getAABB(c.getChildIndexA()).getCenterToOut(this.cA);
                fixtureB.getAABB(c.getChildIndexB()).getCenterToOut(this.cB);
                this.m_debugDraw.drawSegment(this.cA, this.cB, this.color);
            }
        }
        if ((flags & 0x8) != 0x0) {
            this.color.set(0.9f, 0.3f, 0.9f);
            for (Body b = this.m_bodyList; b != null; b = b.getNext()) {
                if (b.isActive()) {
                    for (Fixture f = b.getFixtureList(); f != null; f = f.getNext()) {
                        for (int i = 0; i < f.m_proxyCount; ++i) {
                            final FixtureProxy proxy = f.m_proxies[i];
                            final AABB aabb = this.m_contactManager.m_broadPhase.getFatAABB(proxy.proxyId);
                            if (aabb != null) {
                                final Vec2[] vs = this.avs.get(4);
                                vs[0].set(aabb.lowerBound.x, aabb.lowerBound.y);
                                vs[1].set(aabb.upperBound.x, aabb.lowerBound.y);
                                vs[2].set(aabb.upperBound.x, aabb.upperBound.y);
                                vs[3].set(aabb.lowerBound.x, aabb.upperBound.y);
                                this.m_debugDraw.drawPolygon(vs, 4, this.color);
                            }
                        }
                    }
                }
            }
        }
        if ((flags & 0x20) != 0x0) {
            for (Body b = this.m_bodyList; b != null; b = b.getNext()) {
                this.xf.set(b.getTransform());
                this.xf.p.set(b.getWorldCenter());
                this.m_debugDraw.drawTransform(this.xf);
            }
        }
        if ((flags & 0x40) != 0x0) {
            this.m_contactManager.m_broadPhase.drawTree(this.m_debugDraw);
        }
        this.m_debugDraw.flush();
    }
    
    public void queryAABB(final QueryCallback callback, final AABB aabb) {
        this.wqwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wqwrapper.callback = callback;
        this.m_contactManager.m_broadPhase.query(this.wqwrapper, aabb);
    }
    
    public void queryAABB(final QueryCallback callback, final ParticleQueryCallback particleCallback, final AABB aabb) {
        this.wqwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wqwrapper.callback = callback;
        this.m_contactManager.m_broadPhase.query(this.wqwrapper, aabb);
        this.m_particleSystem.queryAABB(particleCallback, aabb);
    }
    
    public void queryAABB(final ParticleQueryCallback particleCallback, final AABB aabb) {
        this.m_particleSystem.queryAABB(particleCallback, aabb);
    }
    
    public void raycast(final RayCastCallback callback, final Vec2 point1, final Vec2 point2) {
        this.wrcwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wrcwrapper.callback = callback;
        this.input.maxFraction = 1.0f;
        this.input.p1.set(point1);
        this.input.p2.set(point2);
        this.m_contactManager.m_broadPhase.raycast(this.wrcwrapper, this.input);
    }
    
    public void raycast(final RayCastCallback callback, final ParticleRaycastCallback particleCallback, final Vec2 point1, final Vec2 point2) {
        this.wrcwrapper.broadPhase = this.m_contactManager.m_broadPhase;
        this.wrcwrapper.callback = callback;
        this.input.maxFraction = 1.0f;
        this.input.p1.set(point1);
        this.input.p2.set(point2);
        this.m_contactManager.m_broadPhase.raycast(this.wrcwrapper, this.input);
        this.m_particleSystem.raycast(particleCallback, point1, point2);
    }
    
    public void raycast(final ParticleRaycastCallback particleCallback, final Vec2 point1, final Vec2 point2) {
        this.m_particleSystem.raycast(particleCallback, point1, point2);
    }
    
    public Body getBodyList() {
        return this.m_bodyList;
    }
    
    public Joint getJointList() {
        return this.m_jointList;
    }
    
    public Contact getContactList() {
        return this.m_contactManager.m_contactList;
    }
    
    public boolean isSleepingAllowed() {
        return this.m_allowSleep;
    }
    
    public void setSleepingAllowed(final boolean sleepingAllowed) {
        this.m_allowSleep = sleepingAllowed;
    }
    
    public void setWarmStarting(final boolean flag) {
        this.m_warmStarting = flag;
    }
    
    public boolean isWarmStarting() {
        return this.m_warmStarting;
    }
    
    public void setContinuousPhysics(final boolean flag) {
        this.m_continuousPhysics = flag;
    }
    
    public boolean isContinuousPhysics() {
        return this.m_continuousPhysics;
    }
    
    public int getProxyCount() {
        return this.m_contactManager.m_broadPhase.getProxyCount();
    }
    
    public int getBodyCount() {
        return this.m_bodyCount;
    }
    
    public int getJointCount() {
        return this.m_jointCount;
    }
    
    public int getContactCount() {
        return this.m_contactManager.m_contactCount;
    }
    
    public int getTreeHeight() {
        return this.m_contactManager.m_broadPhase.getTreeHeight();
    }
    
    public int getTreeBalance() {
        return this.m_contactManager.m_broadPhase.getTreeBalance();
    }
    
    public float getTreeQuality() {
        return this.m_contactManager.m_broadPhase.getTreeQuality();
    }
    
    public void setGravity(final Vec2 gravity) {
        this.m_gravity.set(gravity);
    }
    
    public Vec2 getGravity() {
        return this.m_gravity;
    }
    
    public boolean isLocked() {
        return (this.m_flags & 0x2) == 0x2;
    }
    
    public void setAutoClearForces(final boolean flag) {
        if (flag) {
            this.m_flags |= 0x4;
        }
        else {
            this.m_flags &= 0xFFFFFFFB;
        }
    }
    
    public boolean getAutoClearForces() {
        return (this.m_flags & 0x4) == 0x4;
    }
    
    public ContactManager getContactManager() {
        return this.m_contactManager;
    }
    
    public Profile getProfile() {
        return this.m_profile;
    }
    
    private void solve(final TimeStep step) {
        this.m_profile.solveInit.startAccum();
        this.m_profile.solveVelocity.startAccum();
        this.m_profile.solvePosition.startAccum();
        for (Body b = this.m_bodyList; b != null; b = b.m_next) {
            b.m_xf0.set(b.m_xf);
        }
        this.island.init(this.m_bodyCount, this.m_contactManager.m_contactCount, this.m_jointCount, this.m_contactManager.m_contactListener);
        for (Body b = this.m_bodyList; b != null; b = b.m_next) {
            final Body body = b;
            body.m_flags &= 0xFFFFFFFE;
        }
        for (Contact c = this.m_contactManager.m_contactList; c != null; c = c.m_next) {
            final Contact contact2 = c;
            contact2.m_flags &= 0xFFFFFFFE;
        }
        for (Joint j = this.m_jointList; j != null; j = j.m_next) {
            j.m_islandFlag = false;
        }
        final int stackSize = this.m_bodyCount;
        if (this.stack.length < stackSize) {
            this.stack = new Body[stackSize];
        }
        for (Body seed = this.m_bodyList; seed != null; seed = seed.m_next) {
            if ((seed.m_flags & 0x1) != 0x1) {
                if (seed.isAwake()) {
                    if (seed.isActive()) {
                        if (seed.getType() != BodyType.STATIC) {
                            this.island.clear();
                            int stackCount = 0;
                            this.stack[stackCount++] = seed;
                            final Body body2 = seed;
                            body2.m_flags |= 0x1;
                            while (stackCount > 0) {
                                final Body b2 = this.stack[--stackCount];
                                assert b2.isActive();
                                this.island.add(b2);
                                b2.setAwake(true);
                                if (b2.getType() == BodyType.STATIC) {
                                    continue;
                                }
                                for (ContactEdge ce = b2.m_contactList; ce != null; ce = ce.next) {
                                    final Contact contact = ce.contact;
                                    if ((contact.m_flags & 0x1) != 0x1) {
                                        if (contact.isEnabled()) {
                                            if (contact.isTouching()) {
                                                final boolean sensorA = contact.m_fixtureA.m_isSensor;
                                                final boolean sensorB = contact.m_fixtureB.m_isSensor;
                                                if (!sensorA) {
                                                    if (!sensorB) {
                                                        this.island.add(contact);
                                                        final Contact contact3 = contact;
                                                        contact3.m_flags |= 0x1;
                                                        final Body other = ce.other;
                                                        if ((other.m_flags & 0x1) != 0x1) {
                                                            assert stackCount < stackSize;
                                                            this.stack[stackCount++] = other;
                                                            final Body body3 = other;
                                                            body3.m_flags |= 0x1;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                for (JointEdge je = b2.m_jointList; je != null; je = je.next) {
                                    if (!je.joint.m_islandFlag) {
                                        final Body other2 = je.other;
                                        if (other2.isActive()) {
                                            this.island.add(je.joint);
                                            je.joint.m_islandFlag = true;
                                            if ((other2.m_flags & 0x1) != 0x1) {
                                                assert stackCount < stackSize;
                                                this.stack[stackCount++] = other2;
                                                final Body body4 = other2;
                                                body4.m_flags |= 0x1;
                                            }
                                        }
                                    }
                                }
                            }
                            this.island.solve(this.m_profile, step, this.m_gravity, this.m_allowSleep);
                            for (int i = 0; i < this.island.m_bodyCount; ++i) {
                                final Body b3 = this.island.m_bodies[i];
                                if (b3.getType() == BodyType.STATIC) {
                                    final Body body5 = b3;
                                    body5.m_flags &= 0xFFFFFFFE;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.m_profile.solveInit.endAccum();
        this.m_profile.solveVelocity.endAccum();
        this.m_profile.solvePosition.endAccum();
        this.broadphaseTimer.reset();
        for (Body b4 = this.m_bodyList; b4 != null; b4 = b4.getNext()) {
            if ((b4.m_flags & 0x1) != 0x0) {
                if (b4.getType() != BodyType.STATIC) {
                    b4.synchronizeFixtures();
                }
            }
        }
        this.m_contactManager.findNewContacts();
        this.m_profile.broadphase.record(this.broadphaseTimer.getMilliseconds());
    }
    
    private void solveTOI(final TimeStep step) {
        final Island island = this.toiIsland;
        island.init(2 * Settings.maxTOIContacts, Settings.maxTOIContacts, 0, this.m_contactManager.m_contactListener);
        if (this.m_stepComplete) {
            for (Body b = this.m_bodyList; b != null; b = b.m_next) {
                final Body body2 = b;
                body2.m_flags &= 0xFFFFFFFE;
                b.m_sweep.alpha0 = 0.0f;
            }
            for (Contact c = this.m_contactManager.m_contactList; c != null; c = c.m_next) {
                final Contact contact2 = c;
                contact2.m_flags &= 0xFFFFFFDE;
                c.m_toiCount = 0.0f;
                c.m_toi = 1.0f;
            }
        }
        while (true) {
            Contact minContact = null;
            float minAlpha = 1.0f;
            for (Contact c2 = this.m_contactManager.m_contactList; c2 != null; c2 = c2.m_next) {
                if (c2.isEnabled()) {
                    if (c2.m_toiCount <= Settings.maxSubSteps) {
                        float alpha = 1.0f;
                        if ((c2.m_flags & 0x20) != 0x0) {
                            alpha = c2.m_toi;
                        }
                        else {
                            final Fixture fA = c2.getFixtureA();
                            final Fixture fB = c2.getFixtureB();
                            if (fA.isSensor()) {
                                continue;
                            }
                            if (fB.isSensor()) {
                                continue;
                            }
                            final Body bA = fA.getBody();
                            final Body bB = fB.getBody();
                            final BodyType typeA = bA.m_type;
                            final BodyType typeB = bB.m_type;
                            assert typeB == BodyType.DYNAMIC;
                            final boolean activeA = bA.isAwake() && typeA != BodyType.STATIC;
                            final boolean activeB = bB.isAwake() && typeB != BodyType.STATIC;
                            if (!activeA && !activeB) {
                                continue;
                            }
                            final boolean collideA = bA.isBullet() || typeA != BodyType.DYNAMIC;
                            final boolean collideB = bB.isBullet() || typeB != BodyType.DYNAMIC;
                            if (!collideA && !collideB) {
                                continue;
                            }
                            float alpha2 = bA.m_sweep.alpha0;
                            if (bA.m_sweep.alpha0 < bB.m_sweep.alpha0) {
                                alpha2 = bB.m_sweep.alpha0;
                                bA.m_sweep.advance(alpha2);
                            }
                            else if (bB.m_sweep.alpha0 < bA.m_sweep.alpha0) {
                                alpha2 = bA.m_sweep.alpha0;
                                bB.m_sweep.advance(alpha2);
                            }
                            assert alpha2 < 1.0f;
                            final int indexA = c2.getChildIndexA();
                            final int indexB = c2.getChildIndexB();
                            final TimeOfImpact.TOIInput input = this.toiInput;
                            input.proxyA.set(fA.getShape(), indexA);
                            input.proxyB.set(fB.getShape(), indexB);
                            input.sweepA.set(bA.m_sweep);
                            input.sweepB.set(bB.m_sweep);
                            input.tMax = 1.0f;
                            this.pool.getTimeOfImpact().timeOfImpact(this.toiOutput, input);
                            final float beta = this.toiOutput.t;
                            if (this.toiOutput.state == TimeOfImpact.TOIOutputState.TOUCHING) {
                                alpha = MathUtils.min(alpha2 + (1.0f - alpha2) * beta, 1.0f);
                            }
                            else {
                                alpha = 1.0f;
                            }
                            c2.m_toi = alpha;
                            final Contact contact3 = c2;
                            contact3.m_flags |= 0x20;
                        }
                        if (alpha < minAlpha) {
                            minContact = c2;
                            minAlpha = alpha;
                        }
                    }
                }
            }
            if (minContact == null || 0.9999988f < minAlpha) {
                this.m_stepComplete = true;
                break;
            }
            final Fixture fA2 = minContact.getFixtureA();
            final Fixture fB2 = minContact.getFixtureB();
            final Body bA2 = fA2.getBody();
            final Body bB2 = fB2.getBody();
            this.backup1.set(bA2.m_sweep);
            this.backup2.set(bB2.m_sweep);
            bA2.advance(minAlpha);
            bB2.advance(minAlpha);
            minContact.update(this.m_contactManager.m_contactListener);
            final Contact contact4 = minContact;
            contact4.m_flags &= 0xFFFFFFDF;
            final Contact contact5 = minContact;
            ++contact5.m_toiCount;
            if (!minContact.isEnabled() || !minContact.isTouching()) {
                minContact.setEnabled(false);
                bA2.m_sweep.set(this.backup1);
                bB2.m_sweep.set(this.backup2);
                bA2.synchronizeTransform();
                bB2.synchronizeTransform();
            }
            else {
                bA2.setAwake(true);
                bB2.setAwake(true);
                island.clear();
                island.add(bA2);
                island.add(bB2);
                island.add(minContact);
                final Body body3 = bA2;
                body3.m_flags |= 0x1;
                final Body body4 = bB2;
                body4.m_flags |= 0x1;
                final Contact contact6 = minContact;
                contact6.m_flags |= 0x1;
                this.tempBodies[0] = bA2;
                this.tempBodies[1] = bB2;
                for (int i = 0; i < 2; ++i) {
                    final Body body = this.tempBodies[i];
                    if (body.m_type == BodyType.DYNAMIC) {
                        for (ContactEdge ce = body.m_contactList; ce != null; ce = ce.next) {
                            if (island.m_bodyCount == island.m_bodyCapacity) {
                                break;
                            }
                            if (island.m_contactCount == island.m_contactCapacity) {
                                break;
                            }
                            final Contact contact = ce.contact;
                            if ((contact.m_flags & 0x1) == 0x0) {
                                final Body other = ce.other;
                                if (other.m_type != BodyType.DYNAMIC || body.isBullet() || other.isBullet()) {
                                    final boolean sensorA = contact.m_fixtureA.m_isSensor;
                                    final boolean sensorB = contact.m_fixtureB.m_isSensor;
                                    if (!sensorA) {
                                        if (!sensorB) {
                                            this.backup1.set(other.m_sweep);
                                            if ((other.m_flags & 0x1) == 0x0) {
                                                other.advance(minAlpha);
                                            }
                                            contact.update(this.m_contactManager.m_contactListener);
                                            if (!contact.isEnabled()) {
                                                other.m_sweep.set(this.backup1);
                                                other.synchronizeTransform();
                                            }
                                            else if (!contact.isTouching()) {
                                                other.m_sweep.set(this.backup1);
                                                other.synchronizeTransform();
                                            }
                                            else {
                                                final Contact contact7 = contact;
                                                contact7.m_flags |= 0x1;
                                                island.add(contact);
                                                if ((other.m_flags & 0x1) == 0x0) {
                                                    final Body body5 = other;
                                                    body5.m_flags |= 0x1;
                                                    if (other.m_type != BodyType.STATIC) {
                                                        other.setAwake(true);
                                                    }
                                                    island.add(other);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                this.subStep.dt = (1.0f - minAlpha) * step.dt;
                this.subStep.inv_dt = 1.0f / this.subStep.dt;
                this.subStep.dtRatio = 1.0f;
                this.subStep.positionIterations = 20;
                this.subStep.velocityIterations = step.velocityIterations;
                this.subStep.warmStarting = false;
                island.solveTOI(this.subStep, bA2.m_islandIndex, bB2.m_islandIndex);
                for (int i = 0; i < island.m_bodyCount; ++i) {
                    final Body body6;
                    final Body body = body6 = island.m_bodies[i];
                    body6.m_flags &= 0xFFFFFFFE;
                    if (body.m_type == BodyType.DYNAMIC) {
                        body.synchronizeFixtures();
                        for (ContactEdge ce = body.m_contactList; ce != null; ce = ce.next) {
                            final Contact contact8 = ce.contact;
                            contact8.m_flags &= 0xFFFFFFDE;
                        }
                    }
                }
                this.m_contactManager.findNewContacts();
                if (this.m_subStepping) {
                    this.m_stepComplete = false;
                    break;
                }
                continue;
            }
        }
    }
    
    private void drawJoint(final Joint joint) {
        final Body bodyA = joint.getBodyA();
        final Body bodyB = joint.getBodyB();
        final Transform xf1 = bodyA.getTransform();
        final Transform xf2 = bodyB.getTransform();
        final Vec2 x1 = xf1.p;
        final Vec2 x2 = xf2.p;
        final Vec2 p1 = this.pool.popVec2();
        final Vec2 p2 = this.pool.popVec2();
        joint.getAnchorA(p1);
        joint.getAnchorB(p2);
        this.color.set(0.5f, 0.8f, 0.8f);
        switch (joint.getType()) {
            case DISTANCE: {
                this.m_debugDraw.drawSegment(p1, p2, this.color);
                break;
            }
            case PULLEY: {
                final PulleyJoint pulley = (PulleyJoint)joint;
                final Vec2 s1 = pulley.getGroundAnchorA();
                final Vec2 s2 = pulley.getGroundAnchorB();
                this.m_debugDraw.drawSegment(s1, p1, this.color);
                this.m_debugDraw.drawSegment(s2, p2, this.color);
                this.m_debugDraw.drawSegment(s1, s2, this.color);
                break;
            }
            case CONSTANT_VOLUME:
            case MOUSE: {
                break;
            }
            default: {
                this.m_debugDraw.drawSegment(x1, p1, this.color);
                this.m_debugDraw.drawSegment(p1, p2, this.color);
                this.m_debugDraw.drawSegment(x2, p2, this.color);
                break;
            }
        }
        this.pool.pushVec2(2);
    }
    
    private void drawShape(final Fixture fixture, final Transform xf, final Color3f color, final boolean wireframe) {
        switch (fixture.getType()) {
            case CIRCLE: {
                final CircleShape circle = (CircleShape)fixture.getShape();
                Transform.mulToOutUnsafe(xf, circle.m_p, this.center);
                final float radius = circle.m_radius;
                xf.q.getXAxis(this.axis);
                if (fixture.getUserData() != null && fixture.getUserData().equals(World.LIQUID_INT)) {
                    final Body b = fixture.getBody();
                    this.liquidOffset.set(b.m_linearVelocity);
                    final float linVelLength = b.m_linearVelocity.length();
                    if (this.averageLinearVel == -1.0f) {
                        this.averageLinearVel = linVelLength;
                    }
                    else {
                        this.averageLinearVel = 0.98f * this.averageLinearVel + 0.02f * linVelLength;
                    }
                    this.liquidOffset.mulLocal(this.liquidLength / this.averageLinearVel / 2.0f);
                    this.circCenterMoved.set(this.center).addLocal(this.liquidOffset);
                    this.center.subLocal(this.liquidOffset);
                    this.m_debugDraw.drawSegment(this.center, this.circCenterMoved, this.liquidColor);
                    return;
                }
                if (wireframe) {
                    this.m_debugDraw.drawCircle(this.center, radius, this.axis, color);
                }
                else {
                    this.m_debugDraw.drawSolidCircle(this.center, radius, this.axis, color);
                }
                break;
            }
            case POLYGON: {
                final PolygonShape poly = (PolygonShape)fixture.getShape();
                final int vertexCount = poly.m_count;
                assert vertexCount <= Settings.maxPolygonVertices;
                final Vec2[] vertices = this.tlvertices.get(Settings.maxPolygonVertices);
                for (int i = 0; i < vertexCount; ++i) {
                    Transform.mulToOutUnsafe(xf, poly.m_vertices[i], vertices[i]);
                }
                if (wireframe) {
                    this.m_debugDraw.drawPolygon(vertices, vertexCount, color);
                }
                else {
                    this.m_debugDraw.drawSolidPolygon(vertices, vertexCount, color);
                }
                break;
            }
            case EDGE: {
                final EdgeShape edge = (EdgeShape)fixture.getShape();
                Transform.mulToOutUnsafe(xf, edge.m_vertex1, this.v1);
                Transform.mulToOutUnsafe(xf, edge.m_vertex2, this.v2);
                this.m_debugDraw.drawSegment(this.v1, this.v2, color);
                break;
            }
            case CHAIN: {
                final ChainShape chain = (ChainShape)fixture.getShape();
                final int count = chain.m_count;
                final Vec2[] vertices = chain.m_vertices;
                Transform.mulToOutUnsafe(xf, vertices[0], this.v1);
                for (int i = 1; i < count; ++i) {
                    Transform.mulToOutUnsafe(xf, vertices[i], this.v2);
                    this.m_debugDraw.drawSegment(this.v1, this.v2, color);
                    this.m_debugDraw.drawCircle(this.v1, 0.05f, color);
                    this.v1.set(this.v2);
                }
                break;
            }
        }
    }
    
    private void drawParticleSystem(final ParticleSystem system) {
        final boolean wireframe = (this.m_debugDraw.getFlags() & 0x80) != 0x0;
        final int particleCount = system.getParticleCount();
        if (particleCount != 0) {
            final float particleRadius = system.getParticleRadius();
            final Vec2[] positionBuffer = system.getParticlePositionBuffer();
            ParticleColor[] colorBuffer = null;
            if (system.m_colorBuffer.data != null) {
                colorBuffer = system.getParticleColorBuffer();
            }
            if (wireframe) {
                this.m_debugDraw.drawParticlesWireframe(positionBuffer, particleRadius, colorBuffer, particleCount);
            }
            else {
                this.m_debugDraw.drawParticles(positionBuffer, particleRadius, colorBuffer, particleCount);
            }
        }
    }
    
    public int createParticle(final ParticleDef def) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return 0;
        }
        final int p = this.m_particleSystem.createParticle(def);
        return p;
    }
    
    public void destroyParticle(final int index) {
        this.destroyParticle(index, false);
    }
    
    public void destroyParticle(final int index, final boolean callDestructionListener) {
        this.m_particleSystem.destroyParticle(index, callDestructionListener);
    }
    
    public int destroyParticlesInShape(final Shape shape, final Transform xf) {
        return this.destroyParticlesInShape(shape, xf, false);
    }
    
    public int destroyParticlesInShape(final Shape shape, final Transform xf, final boolean callDestructionListener) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return 0;
        }
        return this.m_particleSystem.destroyParticlesInShape(shape, xf, callDestructionListener);
    }
    
    public ParticleGroup createParticleGroup(final ParticleGroupDef def) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return null;
        }
        final ParticleGroup g = this.m_particleSystem.createParticleGroup(def);
        return g;
    }
    
    public void joinParticleGroups(final ParticleGroup groupA, final ParticleGroup groupB) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return;
        }
        this.m_particleSystem.joinParticleGroups(groupA, groupB);
    }
    
    public void destroyParticlesInGroup(final ParticleGroup group, final boolean callDestructionListener) {
        assert !this.isLocked();
        if (this.isLocked()) {
            return;
        }
        this.m_particleSystem.destroyParticlesInGroup(group, callDestructionListener);
    }
    
    public void destroyParticlesInGroup(final ParticleGroup group) {
        this.destroyParticlesInGroup(group, false);
    }
    
    public ParticleGroup[] getParticleGroupList() {
        return this.m_particleSystem.getParticleGroupList();
    }
    
    public int getParticleGroupCount() {
        return this.m_particleSystem.getParticleGroupCount();
    }
    
    public int getParticleCount() {
        return this.m_particleSystem.getParticleCount();
    }
    
    public int getParticleMaxCount() {
        return this.m_particleSystem.getParticleMaxCount();
    }
    
    public void setParticleMaxCount(final int count) {
        this.m_particleSystem.setParticleMaxCount(count);
    }
    
    public void setParticleDensity(final float density) {
        this.m_particleSystem.setParticleDensity(density);
    }
    
    public float getParticleDensity() {
        return this.m_particleSystem.getParticleDensity();
    }
    
    public void setParticleGravityScale(final float gravityScale) {
        this.m_particleSystem.setParticleGravityScale(gravityScale);
    }
    
    public float getParticleGravityScale() {
        return this.m_particleSystem.getParticleGravityScale();
    }
    
    public void setParticleDamping(final float damping) {
        this.m_particleSystem.setParticleDamping(damping);
    }
    
    public float getParticleDamping() {
        return this.m_particleSystem.getParticleDamping();
    }
    
    public void setParticleRadius(final float radius) {
        this.m_particleSystem.setParticleRadius(radius);
    }
    
    public float getParticleRadius() {
        return this.m_particleSystem.getParticleRadius();
    }
    
    public int[] getParticleFlagsBuffer() {
        return this.m_particleSystem.getParticleFlagsBuffer();
    }
    
    public Vec2[] getParticlePositionBuffer() {
        return this.m_particleSystem.getParticlePositionBuffer();
    }
    
    public Vec2[] getParticleVelocityBuffer() {
        return this.m_particleSystem.getParticleVelocityBuffer();
    }
    
    public ParticleColor[] getParticleColorBuffer() {
        return this.m_particleSystem.getParticleColorBuffer();
    }
    
    public ParticleGroup[] getParticleGroupBuffer() {
        return this.m_particleSystem.getParticleGroupBuffer();
    }
    
    public Object[] getParticleUserDataBuffer() {
        return this.m_particleSystem.getParticleUserDataBuffer();
    }
    
    public void setParticleFlagsBuffer(final int[] buffer, final int capacity) {
        this.m_particleSystem.setParticleFlagsBuffer(buffer, capacity);
    }
    
    public void setParticlePositionBuffer(final Vec2[] buffer, final int capacity) {
        this.m_particleSystem.setParticlePositionBuffer(buffer, capacity);
    }
    
    public void setParticleVelocityBuffer(final Vec2[] buffer, final int capacity) {
        this.m_particleSystem.setParticleVelocityBuffer(buffer, capacity);
    }
    
    public void setParticleColorBuffer(final ParticleColor[] buffer, final int capacity) {
        this.m_particleSystem.setParticleColorBuffer(buffer, capacity);
    }
    
    public void setParticleUserDataBuffer(final Object[] buffer, final int capacity) {
        this.m_particleSystem.setParticleUserDataBuffer(buffer, capacity);
    }
    
    public ParticleContact[] getParticleContacts() {
        return this.m_particleSystem.m_contactBuffer;
    }
    
    public int getParticleContactCount() {
        return this.m_particleSystem.m_contactCount;
    }
    
    public ParticleBodyContact[] getParticleBodyContacts() {
        return this.m_particleSystem.m_bodyContactBuffer;
    }
    
    public int getParticleBodyContactCount() {
        return this.m_particleSystem.m_bodyContactCount;
    }
    
    public float computeParticleCollisionEnergy() {
        return this.m_particleSystem.computeParticleCollisionEnergy();
    }
    
    static {
        World.LIQUID_INT = new Integer(1234598372);
    }
}
