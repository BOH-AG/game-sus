// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu.internal;

import ea.event.MouseWheelEvent;
import ea.internal.PeriodicTask;
import java.awt.event.KeyEvent;
import ea.event.EventListeners;
import ea.event.MouseButton;
import ea.actor.Actor;
import ea.Vector;
import ea.Game;
import java.util.HashMap;
import ea.Layer;
import ea.event.MouseWheelListener;
import ea.edu.event.MausRadReagierbar;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.event.MouseClickListener;
import ea.edu.event.MausKlickReagierbar;
import ea.event.KeyListener;
import ea.edu.event.TastenReagierbar;
import ea.FrameUpdateListener;
import ea.edu.event.Ticker;
import java.util.Map;
import ea.internal.annotations.Internal;
import ea.Scene;

@Internal
public class EduScene extends Scene
{
    private static final String MAINLAYER_NAME = "Hauptebene";
    private static final float EXPLORE_BASE_MOVE_PER_SEC = 100.0f;
    private static final float DEFAULT_GRAVITY = -9.81f;
    private static final float EXPLORE_ZOOM_FACTOR = 0.3f;
    private final Map<Ticker, FrameUpdateListener> sceneTickers;
    private final Map<TastenReagierbar, KeyListener> sceneKeyListeners;
    private final Map<MausKlickReagierbar, MouseClickListener> sceneMouseClickListeners;
    private final Map<BildAktualisierungReagierbar, FrameUpdateListener> sceneFrameUpdateListeners;
    private final Map<MausRadReagierbar, MouseWheelListener> sceneMouseWheelListeners;
    private boolean exploreMode;
    private String name;
    private final Map<String, Layer> layers;
    private Layer activeLayer;
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public EduScene() {
        this.sceneTickers = new HashMap<Ticker, FrameUpdateListener>();
        this.sceneKeyListeners = new HashMap<TastenReagierbar, KeyListener>();
        this.sceneMouseClickListeners = new HashMap<MausKlickReagierbar, MouseClickListener>();
        this.sceneFrameUpdateListeners = new HashMap<BildAktualisierungReagierbar, FrameUpdateListener>();
        this.sceneMouseWheelListeners = new HashMap<MausRadReagierbar, MouseWheelListener>();
        this.exploreMode = false;
        this.name = null;
        this.layers = new HashMap<String, Layer>();
        this.activeLayer = this.getMainLayer();
        this.layers.put("Hauptebene", this.getMainLayer());
        float dX;
        float dY;
        this.activeLayer.addFrameUpdateListener(deltaSeconds -> {
            if (!this.exploreMode) {
                return;
            }
            else {
                dX = 0.0f;
                dY = 0.0f;
                if (Game.isKeyPressed(37)) {
                    dX -= 100.0f / this.getCamera().getZoom();
                }
                else if (Game.isKeyPressed(39)) {
                    dX += 100.0f / this.getCamera().getZoom();
                }
                if (Game.isKeyPressed(38)) {
                    dY += 100.0f / this.getCamera().getZoom();
                }
                else if (Game.isKeyPressed(40)) {
                    dY -= 100.0f / this.getCamera().getZoom();
                }
                this.getCamera().moveBy(new Vector(dX, dY).multiply(deltaSeconds));
                return;
            }
        });
        float rotation;
        float factor;
        float zoom;
        this.activeLayer.addMouseWheelListener(event -> {
            if (!this.exploreMode) {
                return;
            }
            else {
                rotation = -event.getPreciseWheelRotation();
                factor = ((rotation > 0.0f) ? (1.0f + 0.3f * rotation) : (1.0f / (1.0f - 0.3f * rotation)));
                zoom = this.getCamera().getZoom() * factor;
                if (zoom <= 0.0f) {
                    return;
                }
                else {
                    this.getCamera().setZoom(zoom);
                    return;
                }
            }
        });
        this.setGravity(new Vector(0.0f, -9.81f));
    }
    
    @Internal
    public void setExploreMode(final boolean aktiv) {
        this.exploreMode = aktiv;
    }
    
    @Internal
    public String[] getLayerNames() {
        return this.layers.keySet().toArray(new String[0]);
    }
    
    @Internal
    public void addLayer(final String layerName, final int layerPosition) {
        this.assertLayerMapDoesNotContain(layerName);
        final Layer layer = new Layer();
        layer.setLayerPosition(layerPosition);
        this.addLayer(layer);
        this.layers.put(layerName, layer);
    }
    
    @Internal
    public void setLayerParallax(final String layerName, final float x, final float y, final float zoom) {
        this.assertLayerMapContains(layerName);
        final Layer layer = this.layers.get(layerName);
        layer.setParallaxPosition(x, y);
        layer.setParallaxZoom(zoom);
    }
    
    @Internal
    public void setLayerTimeDistort(final String layerName, final float tpx) {
        this.assertLayerMapContains(layerName);
        final Layer layer = this.layers.get(layerName);
        layer.setTimeDistort(tpx);
    }
    
    @Internal
    public void setActiveLayer(final String layerName) {
        this.assertLayerMapContains(layerName);
        this.activeLayer = this.layers.get(layerName);
    }
    
    @Internal
    public Layer getActiveLayer() {
        return this.activeLayer;
    }
    
    @Internal
    public void resetToMainLayer() {
        this.setActiveLayer("Hauptebene");
    }
    
    @Internal
    public void addEduActor(final Actor actor) {
        this.activeLayer.add(actor);
    }
    
    @Internal
    public void addEduClickListener(final MausKlickReagierbar client) {
        addListener(client, (Map<MausKlickReagierbar, EduScene$1>)this.sceneMouseClickListeners, (EventListeners<EduScene$1>)this.activeLayer.getMouseClickListeners(), new MouseClickListener() {
            @Override
            public void onMouseDown(final Vector e, final MouseButton b) {
                client.klickReagieren(e.getX(), e.getY());
            }
            
            @Override
            public void onMouseUp(final Vector e, final MouseButton b) {
                client.klickLosgelassenReagieren(e.getX(), e.getY());
            }
        });
    }
    
    @Internal
    public void removeEduClickListener(final MausKlickReagierbar object) {
        removeListener(object, this.sceneMouseClickListeners, this.activeLayer.getMouseClickListeners());
    }
    
    @Internal
    public void addEduKeyListener(final TastenReagierbar o) {
        addListener(o, (Map<TastenReagierbar, EduScene$2>)this.sceneKeyListeners, (EventListeners<EduScene$2>)this.activeLayer.getKeyListeners(), new KeyListener() {
            @Override
            public void onKeyDown(final KeyEvent e) {
                o.tasteReagieren(e.getKeyCode());
            }
            
            @Override
            public void onKeyUp(final KeyEvent e) {
                o.tasteLosgelassenReagieren(e.getKeyCode());
            }
        });
    }
    
    @Internal
    public void removeEduKeyListener(final TastenReagierbar o) {
        removeListener(o, this.sceneKeyListeners, this.activeLayer.getKeyListeners());
    }
    
    @Internal
    public void addEduTicker(final float interval, final Ticker ticker) {
        final FrameUpdateListener periodicTask = new PeriodicTask(interval, ticker::tick);
        addListener(ticker, this.sceneTickers, this.activeLayer.getFrameUpdateListeners(), periodicTask);
    }
    
    @Internal
    public void removeEduTicker(final Ticker ticker) {
        removeListener(ticker, this.sceneTickers, this.activeLayer.getFrameUpdateListeners());
    }
    
    @Internal
    public void addEduFrameUpdateListener(final BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        addListener(bildAktualisierungReagierbar, this.sceneFrameUpdateListeners, this.activeLayer.getFrameUpdateListeners(), (FrameUpdateListener)bildAktualisierungReagierbar::bildAktualisierungReagieren);
    }
    
    @Internal
    public void removeEduFrameUpdateListener(final BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        removeListener(bildAktualisierungReagierbar, this.sceneFrameUpdateListeners, this.activeLayer.getFrameUpdateListeners());
    }
    
    @Internal
    public void addEduMouseWheelListener(final MausRadReagierbar mausRadReagierbar) {
        addListener(mausRadReagierbar, this.sceneMouseWheelListeners, this.activeLayer.getMouseWheelListeners(), mwe -> mausRadReagierbar.mausRadReagieren(mwe.getPreciseWheelRotation()));
    }
    
    @Internal
    public void removeEduMouseWheelListener(final MausRadReagierbar mausRadReagierbar) {
        removeListener(mausRadReagierbar, this.sceneMouseWheelListeners, this.activeLayer.getMouseWheelListeners());
    }
    
    @Internal
    private static <K, V> void removeListener(final K eduListener, final Map<K, V> transitionMap, final EventListeners<V> engineListeners) {
        final V fromHashMap = transitionMap.get(eduListener);
        if (fromHashMap == null) {
            throw new IllegalArgumentException("Ein Reagierbar-Objekt sollte entfernt werden, war aber nicht an diesem Layer in dieser Szene angemeldet.");
        }
        engineListeners.remove(fromHashMap);
        transitionMap.remove(eduListener);
    }
    
    @Internal
    private static <K, V> void addListener(final K eduListener, final Map<K, V> transitionHashMap, final EventListeners<V> engineListeners, final V engineListener) {
        transitionHashMap.put(eduListener, engineListener);
        engineListeners.add(engineListener);
    }
    
    @Internal
    private void assertLayerMapContains(final String key) {
        if (!this.layers.containsKey(key)) {
            throw new IllegalArgumentException("Diese Edu-Scene enth\u00e4lt keine Ebene mit dem Namen " + key);
        }
    }
    
    @Internal
    private void assertLayerMapDoesNotContain(final String key) {
        if (this.layers.containsKey(key)) {
            throw new IllegalArgumentException("Diese Edu-Scene enth\u00e4lt bereits eine Ebene mit dem Namen " + key);
        }
    }
}
