// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import ea.event.MouseWheelEvent;
import ea.event.MouseButton;
import java.awt.event.KeyEvent;
import ea.actor.Actor;
import ea.internal.physics.WorldHandler;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.RopeJoint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import ea.internal.Bounds;
import java.util.Comparator;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import ea.internal.annotations.Internal;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import ea.event.EventListenerHelper;
import java.util.ArrayList;
import ea.internal.annotations.API;
import java.util.List;
import ea.event.MouseWheelListener;
import ea.event.MouseClickListener;
import ea.event.KeyListener;
import ea.event.EventListeners;
import java.awt.Color;
import ea.event.FrameUpdateListenerContainer;
import ea.event.MouseWheelListenerContainer;
import ea.event.MouseClickListenerContainer;
import ea.event.KeyListenerContainer;

public class Scene implements KeyListenerContainer, MouseClickListenerContainer, MouseWheelListenerContainer, FrameUpdateListenerContainer
{
    public static final Color REVOLUTE_JOINT_COLOR;
    public static final Color ROPE_JOINT_COLOR;
    public static final Color DISTANCE_JOINT_COLOR;
    public static final Color PRISMATIC_JOINT_COLOR;
    private final Camera camera;
    private final EventListeners<KeyListener> keyListeners;
    private final EventListeners<MouseClickListener> mouseClickListeners;
    private final EventListeners<MouseWheelListener> mouseWheelListeners;
    private final EventListeners<FrameUpdateListener> frameUpdateListeners;
    private final List<Layer> layers;
    private final Layer mainLayer;
    private static final int JOINT_CIRCLE_RADIUS = 10;
    private static final int JOINT_RECTANGLE_SIDE = 12;
    
    @API
    public Layer getMainLayer() {
        return this.mainLayer;
    }
    
    public Scene() {
        this.keyListeners = new EventListeners<KeyListener>();
        this.mouseClickListeners = new EventListeners<MouseClickListener>();
        this.mouseWheelListeners = new EventListeners<MouseWheelListener>();
        this.frameUpdateListeners = new EventListeners<FrameUpdateListener>();
        this.layers = new ArrayList<Layer>();
        this.camera = new Camera();
        (this.mainLayer = new Layer()).setLayerPosition(0);
        this.addLayer(this.mainLayer);
        EventListenerHelper.autoRegisterListeners(this);
    }
    
    @Internal
    public final void step(final float deltaSeconds, final Function<Runnable, Future> invoker) throws InterruptedException {
        synchronized (this.layers) {
            final Collection<Future> layerFutures = new ArrayList<Future>(this.layers.size());
            for (final Layer layer : this.layers) {
                final Future future = invoker.apply(() -> layer.step(deltaSeconds));
                layerFutures.add(future);
            }
            for (final Future layerFuture : layerFutures) {
                try {
                    layerFuture.get();
                }
                catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    @Internal
    public final void render(final Graphics2D g, final int width, final int height) {
        final AffineTransform base = g.getTransform();
        synchronized (this.layers) {
            for (final Layer layer : this.layers) {
                layer.render(g, this.camera, width, height);
                g.setTransform(base);
            }
        }
        if (Game.isDebug()) {
            this.renderJoints(g);
        }
    }
    
    @Internal
    final void sortLayers() {
        this.layers.sort(Comparator.comparingInt(Layer::getLayerPosition));
    }
    
    @API
    public final void addLayer(final Layer layer) {
        synchronized (this.layers) {
            layer.setParent(this);
            this.layers.add(layer);
            this.sortLayers();
        }
    }
    
    @API
    public final void removeLayer(final Layer layer) {
        synchronized (this.layers) {
            this.layers.remove(layer);
            layer.setParent(null);
        }
    }
    
    @API
    public Bounds getVisibleArea(final Vector gameSizeInPixels) {
        return this.mainLayer.getVisibleArea(gameSizeInPixels);
    }
    
    @API
    public final Camera getCamera() {
        return this.camera;
    }
    
    @Internal
    private void renderJoints(final Graphics2D g) {
        for (final Layer layer : this.layers) {
            for (Joint j = layer.getWorldHandler().getWorld().getJointList(); j != null; j = j.getNext()) {
                renderJoint(j, g, layer);
            }
        }
    }
    
    @Internal
    private static void renderJoint(final Joint j, final Graphics2D g, final Layer layer) {
        final Vec2 anchorA = new Vec2();
        final Vec2 anchorB = new Vec2();
        j.getAnchorA(anchorA);
        j.getAnchorB(anchorB);
        final Vector aInPx = layer.translateWorldPointToFramePxCoordinates(Vector.of(anchorA));
        final Vector bInPx = layer.translateWorldPointToFramePxCoordinates(Vector.of(anchorB));
        if (j instanceof RevoluteJoint) {
            g.setColor(Scene.REVOLUTE_JOINT_COLOR);
            g.drawOval((int)aInPx.getX() - 5, (int)aInPx.getY() - 5, 10, 10);
        }
        else if (j instanceof RopeJoint) {
            renderJointRectangle(g, Scene.ROPE_JOINT_COLOR, aInPx, bInPx, layer.calculatePixelPerMeter());
        }
        else if (j instanceof DistanceJoint) {
            renderJointRectangle(g, Scene.DISTANCE_JOINT_COLOR, aInPx, bInPx, layer.calculatePixelPerMeter());
        }
        else if (j instanceof PrismaticJoint) {
            renderJointRectangle(g, Scene.PRISMATIC_JOINT_COLOR, aInPx, bInPx, layer.calculatePixelPerMeter());
        }
    }
    
    @Internal
    private static void renderJointRectangle(final Graphics2D g, final Color color, final Vector a, final Vector b, final float pixelPerMeter) {
        g.setColor(color);
        g.drawRect((int)a.getX() - 5, (int)a.getY() - 5, 12, 12);
        g.drawRect((int)b.getX() - 5, (int)b.getY() - 5, 12, 12);
        g.drawLine((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
        final Vector middle = a.add(b).divide(2.0f);
        g.drawString("" + a.fromThisTo(b).divide(pixelPerMeter).getLength(), middle.getX(), middle.getY());
    }
    
    @Internal
    public final WorldHandler getWorldHandler() {
        return this.mainLayer.getWorldHandler();
    }
    
    @API
    public void setGravity(final Vector gravityInNewton) {
        this.mainLayer.setGravity(gravityInNewton);
    }
    
    @API
    public void setPhysicsPaused(final boolean worldPaused) {
        this.mainLayer.getWorldHandler().setWorldPaused(worldPaused);
    }
    
    @API
    public boolean isPhysicsPaused() {
        return this.mainLayer.getWorldHandler().isWorldPaused();
    }
    
    @API
    public final void add(final Actor... actors) {
        for (final Actor actor : actors) {
            this.mainLayer.add(actor);
        }
    }
    
    @API
    public final void remove(final Actor... actors) {
        for (final Actor actor : actors) {
            this.mainLayer.remove(actor);
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
    public final void invokeFrameUpdateListeners(final float deltaSeconds) {
        this.frameUpdateListeners.invoke(frameUpdateListener -> frameUpdateListener.onFrameUpdate(deltaSeconds));
        synchronized (this.layers) {
            for (final Layer layer : this.layers) {
                layer.invokeFrameUpdateListeners(deltaSeconds);
            }
        }
    }
    
    @Internal
    final void invokeKeyDownListeners(final KeyEvent e) {
        this.keyListeners.invoke(keyListener -> keyListener.onKeyDown(e));
    }
    
    @Internal
    final void invokeKeyUpListeners(final KeyEvent e) {
        this.keyListeners.invoke(keyListener -> keyListener.onKeyUp(e));
    }
    
    @Internal
    final void invokeMouseDownListeners(final Vector position, final MouseButton button) {
        this.mouseClickListeners.invoke(mouseClickListener -> mouseClickListener.onMouseDown(position, button));
    }
    
    @Internal
    final void invokeMouseUpListeners(final Vector position, final MouseButton button) {
        this.mouseClickListeners.invoke(mouseClickListener -> mouseClickListener.onMouseUp(position, button));
    }
    
    @Internal
    final void invokeMouseWheelMoveListeners(final MouseWheelEvent mouseWheelEvent) {
        this.mouseWheelListeners.invoke(mouseWheelListener -> mouseWheelListener.onMouseWheelMove(mouseWheelEvent));
    }
    
    @API
    public final Vector getMousePosition() {
        return Game.convertMousePosition(this, Game.getMousePositionInFrame());
    }
    
    static {
        REVOLUTE_JOINT_COLOR = Color.BLUE;
        ROPE_JOINT_COLOR = Color.CYAN;
        DISTANCE_JOINT_COLOR = Color.ORANGE;
        PRISMATIC_JOINT_COLOR = Color.GREEN;
    }
}
