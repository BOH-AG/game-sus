// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import org.jbox2d.dynamics.World;
import ea.animation.Interpolator;
import ea.animation.interpolation.EaseInOutFloat;
import ea.animation.ValueAnimator;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RopeJointDef;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import ea.collision.CollisionListener;
import org.jbox2d.common.Vec2;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import java.awt.Color;
import ea.internal.annotations.Internal;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.Body;
import java.awt.geom.AffineTransform;
import java.awt.RenderingHints;
import ea.Game;
import java.awt.Composite;
import java.awt.AlphaComposite;
import ea.internal.Bounds;
import java.awt.Graphics2D;
import java.util.List;
import ea.internal.ShapeBuilder;
import ea.internal.physics.WorldHandler;
import ea.Vector;
import ea.internal.annotations.API;
import ea.event.EventListenerHelper;
import ea.internal.physics.NullHandler;
import ea.internal.physics.PhysicsData;
import java.util.Collections;
import org.jbox2d.collision.shapes.Shape;
import java.util.function.Supplier;
import ea.Layer;
import java.util.function.Function;
import ea.FrameUpdateListener;
import ea.event.MouseWheelListener;
import ea.event.MouseClickListener;
import ea.event.KeyListener;
import ea.event.EventListeners;
import ea.internal.physics.PhysicsHandler;
import ea.event.FrameUpdateListenerContainer;
import ea.event.MouseWheelListenerContainer;
import ea.event.MouseClickListenerContainer;
import ea.event.KeyListenerContainer;

public abstract class Actor implements KeyListenerContainer, MouseClickListenerContainer, MouseWheelListenerContainer, FrameUpdateListenerContainer
{
    private boolean visible;
    private int layerPosition;
    private float opacity;
    private PhysicsHandler physicsHandler;
    private final Object physicsHandlerLock;
    private final EventListeners<Runnable> mountListeners;
    private final EventListeners<Runnable> unmountListeners;
    private final EventListeners<KeyListener> keyListeners;
    private final EventListeners<MouseClickListener> mouseClickListeners;
    private final EventListeners<MouseWheelListener> mouseWheelListeners;
    private final EventListeners<FrameUpdateListener> frameUpdateListeners;
    
    private <T> Supplier<T> createParentSupplier(final Function<Layer, T> supplier) {
        final Layer layer;
        return () -> {
            layer = this.getLayer();
            if (layer == null) {
                return null;
            }
            else {
                return supplier.apply(layer);
            }
        };
    }
    
    public Actor(final Supplier<Shape> shapeSupplier) {
        this.visible = true;
        this.layerPosition = 1;
        this.opacity = 1.0f;
        this.physicsHandlerLock = new Object();
        this.mountListeners = new EventListeners<Runnable>();
        this.unmountListeners = new EventListeners<Runnable>();
        this.keyListeners = new EventListeners<KeyListener>((Supplier<EventListeners<KeyListener>>)this.createParentSupplier(Layer::getKeyListeners));
        this.mouseClickListeners = new EventListeners<MouseClickListener>((Supplier<EventListeners<MouseClickListener>>)this.createParentSupplier(Layer::getMouseClickListeners));
        this.mouseWheelListeners = new EventListeners<MouseWheelListener>((Supplier<EventListeners<MouseWheelListener>>)this.createParentSupplier(Layer::getMouseWheelListeners));
        this.frameUpdateListeners = new EventListeners<FrameUpdateListener>((Supplier<EventListeners<FrameUpdateListener>>)this.createParentSupplier(Layer::getFrameUpdateListeners));
        this.physicsHandler = new NullHandler(new PhysicsData(() -> Collections.singletonList(shapeSupplier.get())));
        EventListenerHelper.autoRegisterListeners(this);
    }
    
    @API
    public final void addMountListener(final Runnable listener) {
        synchronized (this.physicsHandlerLock) {
            this.mountListeners.add(listener);
            if (this.isMounted()) {
                listener.run();
            }
        }
    }
    
    @API
    public final void removeMountListener(final Runnable listener) {
        synchronized (this.physicsHandlerLock) {
            this.mountListeners.remove(listener);
        }
    }
    
    @API
    public final void addUnmountListener(final Runnable listener) {
        synchronized (this.physicsHandlerLock) {
            this.unmountListeners.add(listener);
        }
    }
    
    @API
    public final void removeUnmountListener(final Runnable listener) {
        synchronized (this.physicsHandlerLock) {
            this.unmountListeners.remove(listener);
        }
    }
    
    @API
    public void setLayerPosition(final int position) {
        this.layerPosition = position;
    }
    
    @API
    public int getLayerPosition() {
        return this.layerPosition;
    }
    
    @API
    public final void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    @API
    public final boolean isVisible() {
        return this.visible;
    }
    
    @API
    public float getOpacity() {
        return this.opacity;
    }
    
    @API
    public void setOpacity(final float opacity) {
        this.opacity = opacity;
    }
    
    @API
    public final boolean contains(final Vector p) {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.contains(p);
        }
    }
    
    @API
    public final boolean overlaps(final Actor other) {
        synchronized (this.physicsHandlerLock) {
            return WorldHandler.isBodyCollision(this.physicsHandler.getBody(), other.getPhysicsHandler().getBody());
        }
    }
    
    @API
    public void setBodyType(final BodyType type) {
        if (type == null) {
            throw new IllegalArgumentException("BodyType darf nicht null sein");
        }
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setType(type);
        }
    }
    
    @API
    public BodyType getBodyType() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getType();
        }
    }
    
    @API
    public final void setShapes(final String shapeCode) {
        this.setShapes(ShapeBuilder.fromString(shapeCode));
    }
    
    @API
    public final void setShape(final Supplier<Shape> shapeSupplier) {
        this.setShapes(() -> Collections.singletonList(shapeSupplier.get()));
    }
    
    @API
    public final void setShapes(final Supplier<List<Shape>> shapesSupplier) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setShapes(shapesSupplier);
        }
    }
    
    @Internal
    public void renderBasic(final Graphics2D g, final Bounds r, final float pixelPerMeter) {
        if (this.visible && this.isWithinBounds(r)) {
            synchronized (this.physicsHandlerLock) {
                final float rotation = this.physicsHandler.getRotation();
                final Vector position = this.physicsHandler.getPosition();
                final AffineTransform transform = g.getTransform();
                g.rotate(-Math.toRadians(rotation), position.getX() * pixelPerMeter, -position.getY() * pixelPerMeter);
                g.translate(position.getX() * pixelPerMeter, -position.getY() * pixelPerMeter);
                Composite composite;
                if (this.opacity != 1.0f) {
                    composite = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(3, this.opacity));
                }
                else {
                    composite = null;
                }
                this.render(g, pixelPerMeter);
                if (Game.isDebug()) {
                    synchronized (this) {
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                        final Body body = this.physicsHandler.getBody();
                        if (body != null) {
                            for (Fixture fixture = body.m_fixtureList; fixture != null && fixture.m_shape != null; fixture = fixture.m_next) {
                                renderShape(fixture.m_shape, g, pixelPerMeter);
                            }
                        }
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    }
                }
                if (composite != null) {
                    g.setComposite(composite);
                }
                g.setTransform(transform);
            }
        }
    }
    
    @Internal
    public static void renderShape(final Shape shape, final Graphics2D g, final float pixelPerMeter) {
        if (shape == null) {
            return;
        }
        final AffineTransform pre = g.getTransform();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(Color.YELLOW);
        g.drawLine(0, 0, 0, 0);
        g.setColor(Color.RED);
        if (shape instanceof PolygonShape) {
            final PolygonShape polygonShape = (PolygonShape)shape;
            final Vec2[] vec2s = polygonShape.getVertices();
            final int[] xs = new int[polygonShape.getVertexCount()];
            final int[] ys = new int[polygonShape.getVertexCount()];
            for (int i = 0; i < xs.length; ++i) {
                xs[i] = (int)(vec2s[i].x * pixelPerMeter);
                ys[i] = -1 * (int)(vec2s[i].y * pixelPerMeter);
            }
            g.drawPolygon(xs, ys, xs.length);
        }
        else if (shape instanceof CircleShape) {
            final CircleShape circleShape = (CircleShape)shape;
            final float diameter = circleShape.m_radius * 2.0f;
            g.drawOval((int)((circleShape.m_p.x - circleShape.m_radius) * pixelPerMeter), (int)((-circleShape.m_p.y - circleShape.m_radius) * pixelPerMeter), (int)(diameter * (double)pixelPerMeter), (int)(diameter * (double)pixelPerMeter));
        }
        else {
            Logger.error("Debug/Render", "Konnte die Shape (" + shape + ") nicht rendern. Unerwartete Shape.");
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setTransform(pre);
    }
    
    @Internal
    private boolean isWithinBounds(final Bounds r) {
        return true;
    }
    
    @Internal
    public PhysicsHandler getPhysicsHandler() {
        return this.physicsHandler;
    }
    
    @API
    public <E extends Actor> void addCollisionListener(final E collider, final CollisionListener<E> listener) {
        WorldHandler.addSpecificCollisionListener(this, collider, listener);
    }
    
    @API
    public void addCollisionListener(final CollisionListener<Actor> listener) {
        WorldHandler.addGenericCollisionListener(listener, this);
    }
    
    @Internal
    public abstract void render(final Graphics2D p0, final float p1);
    
    @Internal
    public void setPhysicsHandler(final PhysicsHandler handler) {
        synchronized (this.physicsHandlerLock) {
            final WorldHandler worldHandler = handler.getWorldHandler();
            final WorldHandler previousWorldHandler = this.physicsHandler.getWorldHandler();
            if (worldHandler == null) {
                if (previousWorldHandler == null) {
                    return;
                }
                final Layer layer = previousWorldHandler.getLayer();
                this.keyListeners.invoke(layer::removeKeyListener);
                this.mouseClickListeners.invoke(layer::removeMouseClickListener);
                this.mouseWheelListeners.invoke(layer::removeMouseWheelListener);
                this.frameUpdateListeners.invoke(layer::removeFrameUpdateListener);
                this.unmountListeners.invoke(Runnable::run);
                this.physicsHandler = handler;
            }
            else {
                if (previousWorldHandler != null) {
                    return;
                }
                this.physicsHandler = handler;
                final Layer layer = worldHandler.getLayer();
                this.mountListeners.invoke(Runnable::run);
                this.keyListeners.invoke(layer::addKeyListener);
                this.mouseClickListeners.invoke(layer::addMouseClickListener);
                this.mouseWheelListeners.invoke(layer::addMouseWheelListener);
                this.frameUpdateListeners.invoke(layer::addFrameUpdateListener);
            }
        }
    }
    
    public Layer getLayer() {
        synchronized (this.physicsHandlerLock) {
            final WorldHandler worldHandler = this.physicsHandler.getWorldHandler();
            if (worldHandler == null) {
                return null;
            }
            return worldHandler.getLayer();
        }
    }
    
    public void remove() {
        final Layer layer = this.getLayer();
        if (layer != null) {
            layer.remove(this);
        }
    }
    
    @API
    @Override
    public EventListeners<KeyListener> getKeyListeners() {
        return this.keyListeners;
    }
    
    @API
    @Override
    public EventListeners<MouseClickListener> getMouseClickListeners() {
        return this.mouseClickListeners;
    }
    
    @API
    @Override
    public EventListeners<MouseWheelListener> getMouseWheelListeners() {
        return this.mouseWheelListeners;
    }
    
    @API
    @Override
    public EventListeners<FrameUpdateListener> getFrameUpdateListeners() {
        return this.frameUpdateListeners;
    }
    
    @API
    public void setRotationLocked(final boolean rotationLocked) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setRotationLocked(rotationLocked);
        }
    }
    
    @API
    public boolean isRotationLocked() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.isRotationLocked();
        }
    }
    
    @API
    public float getMass() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getMass();
        }
    }
    
    @API
    public void setDensity(final float densityInKgProQM) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setDensity(densityInKgProQM);
        }
    }
    
    @API
    public float getDensity() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getDensity();
        }
    }
    
    @API
    public void setFriction(final float friction) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setFriction(friction);
        }
    }
    
    @API
    public float getFriction() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getFriction();
        }
    }
    
    @API
    public void setVelocity(final Vector velocityInMPerS) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setVelocity(velocityInMPerS);
        }
    }
    
    @API
    public Vector getVelocity() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getVelocity();
        }
    }
    
    @API
    public float getAngularVelocity() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getAngularVelocity();
        }
    }
    
    @API
    public void setAngularVelocity(final float rotationsPerSecond) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setAngularVelocity(rotationsPerSecond);
        }
    }
    
    @API
    public void setRestitution(final float restitution) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setRestitution(restitution);
        }
    }
    
    @API
    public float getRestitution() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getRestitution();
        }
    }
    
    @API
    public void applyForce(final Vector force) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.applyForce(force);
        }
    }
    
    @API
    public void applyTorque(final float torque) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.applyTorque(torque);
        }
    }
    
    @API
    public void applyForce(final Vector kraftInN, final Vector globalPoint) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.applyForce(kraftInN, globalPoint);
        }
    }
    
    @API
    public void applyImpulse(final Vector impulseInNS) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.applyImpluse(impulseInNS, this.physicsHandler.getCenter());
        }
    }
    
    @API
    public void applyImpulse(final Vector impulseInNS, final Vector globalPoint) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.applyImpluse(impulseInNS, globalPoint);
        }
    }
    
    @API
    public void resetMovement() {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.resetMovement();
        }
    }
    
    @API
    public boolean isGrounded() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.isGrounded();
        }
    }
    
    @API
    public RevoluteJoint createRevoluteJoint(final Actor other, final Vector relativeAnchor) {
        final RevoluteJointDef revoluteJointDef;
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.initialize(a, b, this.getPosition().add(relativeAnchor).toVec2());
            revoluteJointDef.collideConnected = false;
            return (org.jbox2d.dynamics.joints.RevoluteJoint)world.createJoint(revoluteJointDef);
        }, new RevoluteJoint());
    }
    
    @API
    public RopeJoint createRopeJoint(final Actor other, final Vector relativeAnchor, final Vector relativeAnchorOther, final float ropeLength) {
        final RopeJointDef ropeJointDef;
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            ropeJointDef = new RopeJointDef();
            ropeJointDef.bodyA = a;
            ropeJointDef.bodyB = b;
            ropeJointDef.localAnchorA.set(relativeAnchor.toVec2());
            ropeJointDef.localAnchorB.set(relativeAnchorOther.toVec2());
            ropeJointDef.collideConnected = true;
            ropeJointDef.maxLength = ropeLength;
            return (org.jbox2d.dynamics.joints.RopeJoint)world.createJoint(ropeJointDef);
        }, new RopeJoint());
    }
    
    @API
    public PrismaticJoint createPrismaticJoint(final Actor other, final Vector anchor, final float axisAngle) {
        final PrismaticJointDef prismaticJointDef;
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            prismaticJointDef = new PrismaticJointDef();
            prismaticJointDef.initialize(a, b, this.getPosition().add(anchor).toVec2(), new Vec2((float)Math.cos(Math.toRadians(axisAngle)), (float)Math.sin(Math.toRadians(axisAngle))));
            prismaticJointDef.collideConnected = false;
            return (org.jbox2d.dynamics.joints.PrismaticJoint)world.createJoint(prismaticJointDef);
        }, new PrismaticJoint());
    }
    
    @API
    public DistanceJoint createDistanceJoint(final Actor other, final Vector anchorRelativeToThis, final Vector anchorRelativeToOther) {
        final DistanceJointDef distanceJointDef;
        final Vector distanceBetweenBothActors;
        return WorldHandler.createJoint(this, other, (world, a, b) -> {
            distanceJointDef = new DistanceJointDef();
            distanceJointDef.bodyA = a;
            distanceJointDef.bodyB = b;
            distanceJointDef.localAnchorA.set(anchorRelativeToThis.toVec2());
            distanceJointDef.localAnchorB.set(anchorRelativeToOther.toVec2());
            distanceBetweenBothActors = this.getPosition().add(anchorRelativeToThis).fromThisTo(other.getPosition().add(anchorRelativeToOther));
            distanceJointDef.length = distanceBetweenBothActors.getLength();
            return (org.jbox2d.dynamics.joints.DistanceJoint)world.createJoint(distanceJointDef);
        }, new DistanceJoint());
    }
    
    @API
    public void setPosition(final float x, final float y) {
        this.setPosition(new Vector(x, y));
    }
    
    @API
    public void setPosition(final Vector p) {
        this.moveBy(new Vector(p.getX() - this.getX(), p.getY() - this.getY()));
    }
    
    @API
    public void moveBy(final Vector v) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.moveBy(v);
        }
    }
    
    @API
    public void setCenter(final float x, final float y) {
        this.setCenter(new Vector(x, y));
    }
    
    @API
    public void setCenter(final Vector p) {
        this.moveBy(this.getCenter().negate().add(p));
    }
    
    @API
    public float getX() {
        return this.getPosition().getX();
    }
    
    @API
    public void setX(final float x) {
        this.moveBy(x - this.getX(), 0.0f);
    }
    
    @API
    public float getY() {
        return this.getPosition().getY();
    }
    
    @API
    public void setY(final float y) {
        this.moveBy(0.0f, y - this.getY());
    }
    
    @API
    public Vector getCenter() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getCenter();
        }
    }
    
    @API
    public Vector getCenterRelative() {
        return this.getCenter().subtract(this.getPosition());
    }
    
    @API
    public void moveBy(final float dX, final float dY) {
        this.moveBy(new Vector(dX, dY));
    }
    
    @API
    public Vector getPosition() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getPosition();
        }
    }
    
    @API
    public void rotateBy(final float degree) {
        this.physicsHandler.rotateBy(degree);
    }
    
    @API
    public float getRotation() {
        synchronized (this.physicsHandlerLock) {
            return this.physicsHandler.getRotation();
        }
    }
    
    @API
    public void setRotation(final float degree) {
        synchronized (this.physicsHandlerLock) {
            this.physicsHandler.setRotation(degree);
        }
    }
    
    @API
    public boolean isMounted() {
        return this.getLayer() != null;
    }
    
    @API
    public ValueAnimator<Float> animateParticle(final float lifetime) {
        this.setBodyType(BodyType.PARTICLE);
        this.setOpacity(1.0f);
        final ValueAnimator<Float> animator = this.animateOpacity(lifetime, 0.0f);
        animator.addCompletionListener(value -> this.remove());
        return animator;
    }
    
    @API
    public ValueAnimator<Float> animateOpacity(final float time, final float toOpacityValue) {
        final ValueAnimator<Float> animator = new ValueAnimator<Float>(time, this::setOpacity, new EaseInOutFloat(this.getOpacity(), toOpacityValue), this);
        this.addFrameUpdateListener(animator);
        return animator;
    }
    
    @Internal
    static void assertWidthAndHeight(final float width, final float height) {
        if (width <= 0.0f || height <= 0.0f) {
            throw new IllegalArgumentException("H\u00f6he und Breite d\u00fcrfen nicht negativ sein! Waren: " + width + " und " + height);
        }
    }
}
