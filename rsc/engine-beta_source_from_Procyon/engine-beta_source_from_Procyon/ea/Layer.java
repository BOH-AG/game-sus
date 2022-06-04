// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import java.util.Iterator;
import java.awt.Graphics2D;
import ea.internal.Bounds;
import org.jbox2d.dynamics.Body;
import ea.internal.physics.PhysicsData;
import ea.internal.physics.NullHandler;
import ea.internal.physics.PhysicsHandler;
import ea.internal.physics.BodyHandler;
import ea.internal.annotations.Internal;
import ea.internal.annotations.API;
import ea.event.EventListenerHelper;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.function.Function;
import ea.event.MouseWheelListener;
import ea.event.MouseClickListener;
import ea.event.KeyListener;
import ea.event.EventListeners;
import ea.internal.physics.WorldHandler;
import java.util.List;
import ea.actor.Actor;
import java.util.Comparator;
import ea.event.FrameUpdateListenerContainer;
import ea.event.MouseWheelListenerContainer;
import ea.event.MouseClickListenerContainer;
import ea.event.KeyListenerContainer;

public class Layer implements KeyListenerContainer, MouseClickListenerContainer, MouseWheelListenerContainer, FrameUpdateListenerContainer
{
    private static final Comparator<? super Actor> ACTOR_COMPARATOR;
    private final List<Actor> actors;
    private float parallaxX;
    private float parallaxY;
    private float parallaxRotation;
    private float parallaxZoom;
    private float timeDistort;
    private int layerPosition;
    private boolean visible;
    private Scene parent;
    private final WorldHandler worldHandler;
    private final EventListeners<KeyListener> keyListeners;
    private final EventListeners<MouseClickListener> mouseClickListeners;
    private final EventListeners<MouseWheelListener> mouseWheelListeners;
    private final EventListeners<FrameUpdateListener> frameUpdateListeners;
    
    private <T> Supplier<T> createParentSupplier(final Function<Scene, T> supplier) {
        final Scene scene;
        return () -> {
            scene = this.getParent();
            if (scene == null) {
                return null;
            }
            else {
                return supplier.apply(scene);
            }
        };
    }
    
    @API
    public Layer() {
        this.parallaxX = 1.0f;
        this.parallaxY = 1.0f;
        this.parallaxRotation = 1.0f;
        this.parallaxZoom = 1.0f;
        this.timeDistort = 1.0f;
        this.layerPosition = -2;
        this.visible = true;
        this.keyListeners = new EventListeners<KeyListener>((Supplier<EventListeners<KeyListener>>)this.createParentSupplier(Scene::getKeyListeners));
        this.mouseClickListeners = new EventListeners<MouseClickListener>((Supplier<EventListeners<MouseClickListener>>)this.createParentSupplier(Scene::getMouseClickListeners));
        this.mouseWheelListeners = new EventListeners<MouseWheelListener>((Supplier<EventListeners<MouseWheelListener>>)this.createParentSupplier(Scene::getMouseWheelListeners));
        this.frameUpdateListeners = new EventListeners<FrameUpdateListener>();
        this.worldHandler = new WorldHandler(this);
        this.actors = new CopyOnWriteArrayList<Actor>();
        EventListenerHelper.autoRegisterListeners(this);
    }
    
    public Scene getParent() {
        return this.parent;
    }
    
    @Internal
    void setParent(final Scene parent) {
        if (parent != null && this.parent != null) {
            throw new IllegalStateException("Das Layer wurde bereits an einer Scene angemeldet.");
        }
        if (parent != null) {
            this.keyListeners.invoke(parent::addKeyListener);
            this.mouseClickListeners.invoke(parent::addMouseClickListener);
            this.mouseWheelListeners.invoke(parent::addMouseWheelListener);
            this.frameUpdateListeners.invoke(parent::addFrameUpdateListener);
        }
        else {
            this.keyListeners.invoke(this.parent::removeKeyListener);
            this.mouseClickListeners.invoke(this.parent::removeMouseClickListener);
            this.mouseWheelListeners.invoke(this.parent::removeMouseWheelListener);
            this.frameUpdateListeners.invoke(this.parent::removeFrameUpdateListener);
        }
        this.parent = parent;
    }
    
    @API
    public void setLayerPosition(final int position) {
        this.layerPosition = position;
        if (this.parent != null) {
            this.parent.sortLayers();
        }
    }
    
    @API
    public int getLayerPosition() {
        return this.layerPosition;
    }
    
    @API
    public void setParallaxPosition(final float parallaxX, final float parallaxY) {
        this.parallaxX = parallaxX;
        this.parallaxY = parallaxY;
    }
    
    @API
    public void setParallaxZoom(final float parallaxZoom) {
        this.parallaxZoom = parallaxZoom;
    }
    
    @API
    public void setParallaxRotation(final float parallaxRotation) {
        this.parallaxRotation = parallaxRotation;
    }
    
    @API
    public void setTimeDistort(final float timeDistort) {
        if (timeDistort < 0.0f) {
            throw new IllegalArgumentException("Zeitverzerrungsfaktor muss gr\u00f6\u00dfer oder gleich 0 sein, war " + timeDistort);
        }
        this.timeDistort = timeDistort;
    }
    
    @API
    public void setGravity(final Vector gravityInNewton) {
        this.worldHandler.getWorld().setGravity(gravityInNewton.toVec2());
    }
    
    @API
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    @API
    public boolean isVisible() {
        return this.visible;
    }
    
    @API
    public void add(final Actor... actors) {
        final int length;
        int i = 0;
        Actor actor;
        PhysicsHandler oldHandler;
        PhysicsHandler newHandler;
        this.defer(() -> {
            length = actors.length;
            while (i < length) {
                actor = actors[i];
                if (actor.isMounted()) {
                    if (actor.getLayer() != this) {
                        throw new IllegalArgumentException("Ein Actor kann nur an einem Layer gleichzeitig angemeldet sein");
                    }
                    else {
                        return;
                    }
                }
                else {
                    oldHandler = actor.getPhysicsHandler();
                    newHandler = new BodyHandler(actor, oldHandler.getPhysicsData(), this.worldHandler);
                    actor.setPhysicsHandler(newHandler);
                    oldHandler.applyMountCallbacks(newHandler);
                    this.actors.add(actor);
                    ++i;
                }
            }
            this.actors.sort(Layer.ACTOR_COMPARATOR);
        });
    }
    
    @API
    public final void remove(final Actor... actors) {
        int length;
        int i = 0;
        Actor actor;
        PhysicsData physicsData;
        PhysicsHandler physicsHandler;
        Body body;
        this.defer(() -> {
            for (length = actors.length; i < length; ++i) {
                actor = actors[i];
                this.actors.remove(actor);
                physicsData = actor.getPhysicsHandler().getPhysicsData();
                physicsHandler = actor.getPhysicsHandler();
                if (physicsHandler.getWorldHandler() != null) {
                    body = physicsHandler.getBody();
                    this.worldHandler.removeAllInternalReferences(body);
                    this.worldHandler.getWorld().destroyBody(body);
                    actor.setPhysicsHandler(new NullHandler(physicsData));
                }
            }
        });
    }
    
    @Internal
    public Vector translateWorldPointToFramePxCoordinates(final Vector worldPoint) {
        final float pixelPerMeter = this.calculatePixelPerMeter();
        final Vector frameSize = Game.getFrameSizeInPixels();
        final Vector cameraPositionInPx = new Vector(frameSize.getX() / 2.0f, frameSize.getY() / 2.0f);
        final Vector fromCamToPointInWorld = this.parent.getCamera().getPosition().multiplyX(this.parallaxX).multiplyY(this.parallaxY).fromThisTo(worldPoint);
        return cameraPositionInPx.add(fromCamToPointInWorld.multiplyY(-1.0f).multiply(pixelPerMeter * this.parallaxZoom));
    }
    
    @API
    public Bounds getVisibleArea(final Vector gameSizeInPixels) {
        final Vector center = this.parent.getCamera().getPosition();
        final float pixelPerMeter = this.calculatePixelPerMeter();
        return new Bounds(0.0f, 0.0f, gameSizeInPixels.getX() / pixelPerMeter, gameSizeInPixels.getY() / pixelPerMeter).withCenterPoint(center);
    }
    
    @API
    public void setVisibleWidth(final float width, final Vector gameSizeInPixels) {
        final float desiredPixelPerMeter = gameSizeInPixels.getX() / width;
        final float desiredZoom = 1.0f + (desiredPixelPerMeter - 1.0f) / this.parallaxZoom;
        this.parent.getCamera().setZoom(desiredZoom);
    }
    
    @API
    public void setVisibleHeight(final float height, final Vector gameSizeInPixels) {
        final float desiredPixelPerMeter = gameSizeInPixels.getY() / height;
        final float desiredZoom = 1.0f + (desiredPixelPerMeter - 1.0f) / this.parallaxZoom;
        this.parent.getCamera().setZoom(desiredZoom);
    }
    
    @API
    public float calculatePixelPerMeter() {
        return 1.0f + (this.parent.getCamera().getZoom() - 1.0f) * this.parallaxZoom;
    }
    
    @Internal
    public void render(final Graphics2D g, final Camera camera, final int width, final int height) {
        if (!this.visible) {
            return;
        }
        final Vector position = camera.getPosition();
        final float rotation = -camera.getRotation();
        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);
        final float pixelPerMeter = this.calculatePixelPerMeter();
        g.rotate(rotation * this.parallaxRotation, 0.0, 0.0);
        g.translate(-position.getX() * this.parallaxX * pixelPerMeter, position.getY() * this.parallaxY * pixelPerMeter);
        final int size = Math.max(width, height);
        boolean needsSort = false;
        int previousPosition = Integer.MIN_VALUE;
        for (final Actor actor : this.actors) {
            actor.renderBasic(g, new Bounds(position.getX() - size, position.getY() - size, (float)(size * 2), (float)(size * 2)), pixelPerMeter);
            if (!needsSort) {
                final int actorPosition = actor.getLayerPosition();
                if (actorPosition < previousPosition) {
                    needsSort = true;
                }
                previousPosition = actorPosition;
            }
        }
        if (needsSort) {
            this.actors.sort(Layer.ACTOR_COMPARATOR);
        }
    }
    
    @Internal
    public WorldHandler getWorldHandler() {
        return this.worldHandler;
    }
    
    @Internal
    public void step(final float deltaSeconds) {
        synchronized (this.worldHandler) {
            this.worldHandler.step(deltaSeconds * this.timeDistort);
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
    
    @Internal
    void invokeFrameUpdateListeners(final float deltaSeconds) {
        final float scaledSeconds = deltaSeconds * this.timeDistort;
        this.frameUpdateListeners.invoke(frameUpdateListener -> frameUpdateListener.onFrameUpdate(scaledSeconds));
    }
    
    static {
        ACTOR_COMPARATOR = Comparator.comparingInt(Actor::getLayerPosition);
    }
}
