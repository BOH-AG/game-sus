// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.physics;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.jbox2d.dynamics.joints.Joint;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.collision.AABB;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import ea.collision.CollisionEvent;
import java.util.Iterator;
import ea.internal.util.Logger;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.BodyDef;
import ea.internal.annotations.Internal;
import org.jbox2d.common.Vec2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import ea.actor.Actor;
import ea.collision.CollisionListener;
import java.util.List;
import org.jbox2d.dynamics.Body;
import java.util.Map;
import org.jbox2d.dynamics.World;
import ea.Layer;
import org.jbox2d.callbacks.ContactListener;

public class WorldHandler implements ContactListener
{
    public static final int CATEGORY_PASSIVE = 1;
    public static final int CATEGORY_STATIC = 2;
    public static final int CATEGORY_DYNAMIC_OR_KINEMATIC = 4;
    public static final int CATEGORY_PARTICLE = 8;
    public static final float STEP_TIME = 0.008f;
    private final Layer layer;
    private boolean worldPaused;
    private final World world;
    private final Map<Body, List<Checkup>> specificCollisionListeners;
    private final Map<Body, List<CollisionListener<Actor>>> generalCollisonListeners;
    private final Map<Body, Actor> worldMap;
    private final Collection<FixturePair> contactsToIgnore;
    private float simulationAccumulator;
    
    @Internal
    public WorldHandler(final Layer layer) {
        this.worldPaused = false;
        this.specificCollisionListeners = new ConcurrentHashMap<Body, List<Checkup>>();
        this.generalCollisonListeners = new HashMap<Body, List<CollisionListener<Actor>>>();
        this.worldMap = new HashMap<Body, Actor>();
        this.contactsToIgnore = new ArrayList<FixturePair>();
        this.simulationAccumulator = 0.0f;
        this.layer = layer;
        (this.world = new World(new Vec2())).setContactListener(this);
    }
    
    @Internal
    public World getWorld() {
        return this.world;
    }
    
    public void setWorldPaused(final boolean worldPaused) {
        this.worldPaused = worldPaused;
    }
    
    public boolean isWorldPaused() {
        return this.worldPaused;
    }
    
    @Internal
    public void assertNoWorldStep() {
        if (this.getWorld().isLocked()) {
            throw new RuntimeException("Die Operation kann nicht w\u00e4hrend des World-Step ausgef\u00fchrt werden. Ggf. mit Game.afterWorldStep wrappen.");
        }
    }
    
    public void step(final float deltaSeconds) {
        if (this.worldPaused) {
            return;
        }
        synchronized (this) {
            synchronized (this.world) {
                this.simulationAccumulator += deltaSeconds;
                while (this.simulationAccumulator >= 0.008f) {
                    this.simulationAccumulator -= 0.008f;
                    this.world.step(0.008f, 6, 3);
                }
            }
        }
    }
    
    public Body createBody(final BodyDef bd, final Actor actor) {
        final Body body;
        synchronized (this.world) {
            body = this.world.createBody(bd);
            this.worldMap.put(body, actor);
        }
        return body;
    }
    
    @Internal
    public Actor lookupActor(final Body body) {
        final Actor result = this.worldMap.get(body);
        if (result == null) {
            throw new RuntimeException("No actor found for given body");
        }
        return result;
    }
    
    @Internal
    public void removeAllInternalReferences(final Body body) {
        this.specificCollisionListeners.remove(body);
        this.generalCollisonListeners.remove(body);
        this.worldMap.remove(body);
    }
    
    @Internal
    public void addContactToBlacklist(final Contact contact) {
        this.contactsToIgnore.add(new FixturePair(contact.m_fixtureA, contact.m_fixtureB));
    }
    
    @Override
    public void beginContact(final Contact contact) {
        this.processContact(contact, true);
    }
    
    @Override
    public void endContact(final Contact contact) {
        this.processContact(contact, false);
    }
    
    @Internal
    private void processContact(final Contact contact, final boolean isBegin) {
        final Body b1 = contact.getFixtureA().getBody();
        final Body b2 = contact.getFixtureB().getBody();
        if (b1 == b2) {
            Logger.error("Collision", "Inter-Body Collision!");
            return;
        }
        if (b1.hashCode() == b2.hashCode()) {
            final List<Checkup> result1 = this.specificCollisionListeners.get(b1);
            if (result1 != null) {
                for (final Checkup c : result1) {
                    c.checkCollision(b2, contact, isBegin);
                }
            }
            final List<Checkup> result2 = this.specificCollisionListeners.get(b2);
            if (result2 != null) {
                for (final Checkup c2 : result2) {
                    c2.checkCollision(b1, contact, isBegin);
                }
            }
        }
        else {
            Body lower;
            Body higher;
            if (b1.hashCode() < b2.hashCode()) {
                lower = b1;
                higher = b2;
            }
            else {
                lower = b2;
                higher = b1;
            }
            final List<Checkup> result3 = this.specificCollisionListeners.get(lower);
            if (result3 != null) {
                for (final Checkup c : result3) {
                    c.checkCollision(higher, contact, isBegin);
                }
            }
        }
        this.generalCheckup(b1, b2, contact, isBegin);
        this.generalCheckup(b2, b1, contact, isBegin);
        if (!isBegin) {
            contact.setEnabled(true);
            this.removeFromBlacklist(contact);
        }
    }
    
    private void removeFromBlacklist(final Contact contact) {
        FixturePair fixturePair = null;
        for (final FixturePair ignoredPair : this.contactsToIgnore) {
            if (ignoredPair.matches(contact.m_fixtureA, contact.m_fixtureB)) {
                fixturePair = ignoredPair;
                break;
            }
        }
        if (fixturePair != null) {
            this.contactsToIgnore.remove(fixturePair);
        }
    }
    
    @Internal
    private void generalCheckup(final Body act, final Body col, final Contact contact, final boolean isBegin) {
        final List<CollisionListener<Actor>> list = this.generalCollisonListeners.get(act);
        if (list != null) {
            final Actor other = this.worldMap.get(col);
            if (other == null) {
                return;
            }
            final CollisionEvent<Actor> collisionEvent = new CollisionEvent<Actor>(contact, other);
            for (final CollisionListener<Actor> listener : list) {
                if (isBegin) {
                    listener.onCollision(collisionEvent);
                }
                else {
                    listener.onCollisionEnd(collisionEvent);
                }
            }
        }
    }
    
    @Override
    public void preSolve(final Contact contact, final Manifold manifold) {
        for (final FixturePair ignoredPair : this.contactsToIgnore) {
            if (ignoredPair.matches(contact.m_fixtureA, contact.m_fixtureB)) {
                contact.setEnabled(false);
            }
        }
    }
    
    @Override
    public void postSolve(final Contact contact, final ContactImpulse contactImpulse) {
    }
    
    public Layer getLayer() {
        return this.layer;
    }
    
    @Internal
    public Fixture[] queryAABB(final AABB aabb) {
        final ArrayList<Fixture> fixtures = new ArrayList<Fixture>();
        this.world.queryAABB(fixtures::add, aabb);
        return fixtures.toArray(new Fixture[0]);
    }
    
    @Internal
    public static boolean isBodyCollision(final Body a, final Body b) {
        if (a == null || b == null) {
            return false;
        }
        for (ContactEdge contact = a.getContactList(); contact != null; contact = contact.next) {
            if (contact.other == b && contact.contact.isTouching()) {
                return true;
            }
        }
        return false;
    }
    
    @Internal
    public static void addGenericCollisionListener(final CollisionListener<Actor> listener, final Actor actor) {
        final Body body;
        actor.addMountListener(() -> {
            body = actor.getPhysicsHandler().getBody();
            if (body == null) {
                throw new IllegalStateException("Body is missing on an Actor with an existing WorldHandler");
            }
            else {
                actor.getPhysicsHandler().getWorldHandler().generalCollisonListeners.computeIfAbsent(body, key -> new CopyOnWriteArrayList()).add(listener);
            }
        });
    }
    
    @Internal
    public static <E extends Actor> void addSpecificCollisionListener(final Actor actor, final E collider, final CollisionListener<E> listener) {
        final Body b1;
        final Body b2;
        Body lower;
        Body higher;
        Checkup<Object> checkup;
        addMountListener(actor, collider, worldHandler -> {
            b1 = actor.getPhysicsHandler().getBody();
            b2 = collider.getPhysicsHandler().getBody();
            if (b1 == null || b2 == null) {
                Logger.error("Kollision", "Ein Actor-Objekt ohne physikalischen Body wurde zur Kollisions\u00fcberwachung angemeldet.");
            }
            else {
                if (b1.hashCode() < b2.hashCode()) {
                    lower = b1;
                    higher = b2;
                }
                else {
                    lower = b2;
                    higher = b1;
                }
                checkup = new Checkup<Object>((CollisionListener)listener, higher, (Actor)collider);
                worldHandler.specificCollisionListeners.computeIfAbsent(lower, key -> new CopyOnWriteArrayList()).add(checkup);
            }
        });
    }
    
    @Internal
    public static <JointType extends Joint, Wrapper extends ea.actor.Joint<JointType>> Wrapper createJoint(final Actor a, final Actor b, final JointBuilder<JointType> jointBuilder, final Wrapper wrapper) {
        final List<Runnable> releaseCallbacks = addMountListener(a, b, worldHandler -> wrapper.setJoint(jointBuilder.createJoint(worldHandler.getWorld(), a.getPhysicsHandler().getBody(), b.getPhysicsHandler().getBody()), worldHandler));
        releaseCallbacks.forEach(wrapper::addReleaseListener);
        return wrapper;
    }
    
    @Internal
    public static List<Runnable> addMountListener(final Actor a, final Actor b, final Consumer<WorldHandler> runnable) {
        final List<Runnable> releases = new ArrayList<Runnable>();
        final AtomicBoolean skipListener = new AtomicBoolean(true);
        final WorldHandler worldHandler;
        final AtomicBoolean atomicBoolean;
        final Runnable listenerA = () -> {
            worldHandler = a.getPhysicsHandler().getWorldHandler();
            if (!atomicBoolean.get() && b.isMounted() && b.getPhysicsHandler().getWorldHandler() == worldHandler) {
                runnable.accept(worldHandler);
            }
            return;
        };
        final WorldHandler worldHandler2;
        final AtomicBoolean atomicBoolean2;
        final Runnable listenerB = () -> {
            worldHandler2 = b.getPhysicsHandler().getWorldHandler();
            if (!atomicBoolean2.get() && a.isMounted() && a.getPhysicsHandler().getWorldHandler() == worldHandler2) {
                runnable.accept(worldHandler2);
            }
            return;
        };
        a.addMountListener(listenerA);
        b.addMountListener(listenerB);
        skipListener.set(false);
        releases.add(() -> a.removeMountListener(listenerA));
        releases.add(() -> b.removeMountListener(listenerB));
        if (a.isMounted() && b.isMounted()) {
            runnable.accept(a.getPhysicsHandler().getWorldHandler());
        }
        return releases;
    }
    
    private static class Checkup<E extends Actor>
    {
        private final CollisionListener<E> listener;
        private final Body body;
        private final E collidingActor;
        
        private Checkup(final CollisionListener<E> listener, final Body body, final E collidingActor) {
            this.listener = listener;
            this.body = body;
            this.collidingActor = collidingActor;
        }
        
        public void checkCollision(final Body body, final Contact contact, final boolean isBegin) {
            if (this.body == body) {
                final CollisionEvent<E> collisionEvent = new CollisionEvent<E>(contact, this.collidingActor);
                if (isBegin) {
                    this.listener.onCollision(collisionEvent);
                }
                else {
                    this.listener.onCollisionEnd(collisionEvent);
                }
            }
        }
    }
    
    private static class FixturePair
    {
        private final Fixture f1;
        private final Fixture f2;
        
        public FixturePair(final Fixture b1, final Fixture b2) {
            this.f1 = b1;
            this.f2 = b2;
        }
        
        public boolean matches(final Fixture a, final Fixture b) {
            return (this.f1 == a && this.f2 == b) || (this.f1 == b && this.f2 == a);
        }
    }
}
