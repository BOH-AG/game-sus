// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import ea.event.MouseButton;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentHashMap;
import ea.internal.io.ImageWriter;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import ea.internal.annotations.Internal;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyListener;
import java.awt.Image;
import ea.internal.io.ImageLoader;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Component;
import ea.internal.annotations.API;
import java.awt.Point;
import java.util.Collection;
import ea.internal.graphics.RenderPanel;
import java.awt.Frame;

public final class Game
{
    private static boolean debug;
    private static boolean verbose;
    private static int width;
    private static int height;
    private static final Frame frame;
    private static RenderPanel renderPanel;
    private static boolean exitOnEsc;
    private static Scene scene;
    private static GameLogic gameLogic;
    private static Thread mainThread;
    private static Collection<Integer> pressedKeys;
    private static Point mousePosition;
    
    @API
    public static void setTitle(final String title) {
        Game.frame.setTitle(title);
    }
    
    @API
    public static void setExitOnEsc(final boolean value) {
        Game.exitOnEsc = value;
    }
    
    @API
    public static Vector getFrameSizeInPixels() {
        return new Vector((float)Game.width, (float)Game.height);
    }
    
    @API
    public static void start(final int width, final int height, final Scene scene) {
        if (Game.renderPanel != null) {
            throw new RuntimeException("Game.start wurde bereits ausgef\u00fchrt.");
        }
        Game.width = width;
        Game.height = height;
        Game.scene = scene;
        Game.renderPanel = new RenderPanel(width, height);
        Game.frame.setResizable(false);
        Game.frame.add(Game.renderPanel);
        Game.frame.pack();
        Game.frame.setLocationRelativeTo(null);
        Game.renderPanel.allocateBuffers();
        Game.frame.setVisible(true);
        Game.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                Game.exit();
            }
        });
        final java.awt.event.KeyListener keyListener = new KeyListener();
        Game.frame.addKeyListener(keyListener);
        Game.renderPanel.addKeyListener(keyListener);
        Game.renderPanel.setFocusable(true);
        final MouseAdapter mouseListener = new MouseListener();
        Game.renderPanel.addMouseMotionListener(mouseListener);
        Game.renderPanel.addMouseListener(mouseListener);
        Game.renderPanel.addMouseWheelListener(Game::enqueueMouseWheelEvent);
        try {
            Game.frame.setIconImage(ImageLoader.load("assets/favicon.png"));
        }
        catch (Exception ex) {}
        Game.mousePosition = new Point(width / 2, height / 2);
        (Game.mainThread = new Thread(Game::run, "ea.main")).start();
        Game.mainThread.setPriority(10);
    }
    
    private static void run() {
        (Game.gameLogic = new GameLogic(Game.renderPanel, Game::getActiveScene, Game::isDebug)).run();
        Game.frame.setVisible(false);
        Game.frame.dispose();
        System.exit(0);
    }
    
    @Internal
    public static Vector convertMousePosition(final Scene scene, final Point mousePosition) {
        final float zoom = scene.getCamera().getZoom();
        final float rotation = scene.getCamera().getRotation();
        final Vector position = scene.getCamera().getPosition();
        return new Vector(position.getX() + ((float)Math.cos(Math.toRadians(rotation)) * (mousePosition.x - Game.width / 2.0f) + (float)Math.sin(Math.toRadians(rotation)) * (mousePosition.y - Game.height / 2.0f)) / zoom, position.getY() + ((float)Math.sin(rotation) * (mousePosition.x - Game.width / 2.0f) - (float)Math.cos(rotation) * (mousePosition.y - Game.height / 2.0f)) / zoom);
    }
    
    private static void enqueueMouseWheelEvent(final MouseWheelEvent mouseWheelEvent) {
        final ea.event.MouseWheelEvent mouseWheelAction = new ea.event.MouseWheelEvent((float)mouseWheelEvent.getPreciseWheelRotation());
        Game.gameLogic.enqueue(() -> Game.scene.invokeMouseWheelMoveListeners(mouseWheelAction));
    }
    
    @API
    public static void transitionToScene(final Scene scene) {
        Game.gameLogic.enqueue(() -> Game.scene = scene);
    }
    
    @API
    public static Scene getActiveScene() {
        return Game.scene;
    }
    
    @API
    public static boolean isKeyPressed(final int keyCode) {
        return Game.pressedKeys.contains(keyCode);
    }
    
    @API
    public static boolean isRunning() {
        return Game.frame.isVisible();
    }
    
    @API
    public static void setFrameSize(final int width, final int height) {
        if (width <= 0 || height <= 0) {
            throw new RuntimeException("Die Fenstergr\u00f6\u00dfe kann nicht kleiner/gleich 0 sein. Eingabe war: " + width + " - " + height + ".");
        }
        if (Game.renderPanel == null) {
            throw new RuntimeException("Fenster-Resizing ist erst m\u00f6glich, nachdem Game.start ausgef\u00fchrt wurde.");
        }
        Game.width = width;
        Game.height = height;
        Game.renderPanel.setSize(width, height);
        Game.renderPanel.setPreferredSize(new Dimension(width, height));
        Game.frame.pack();
    }
    
    @API
    public static void setFramePosition(final int x, final int y) {
        Game.frame.setLocation(x, y);
    }
    
    @API
    public static void exit() {
        if (Game.mainThread == null) {
            System.exit(0);
            return;
        }
        Game.mainThread.interrupt();
    }
    
    @API
    public static void showMessage(final String message, final String title) {
        JOptionPane.showMessageDialog(Game.frame, message, title, 1);
    }
    
    @API
    public static String requestStringInput(final String message, final String title) {
        return JOptionPane.showInputDialog(Game.frame, message, title, -1);
    }
    
    @API
    public static boolean requestYesNo(final String message, final String title) {
        return JOptionPane.showConfirmDialog(Game.frame, message, title, 0, -1) == 0;
    }
    
    @API
    public static boolean requestOkCancel(final String message, final String title) {
        return JOptionPane.showConfirmDialog(Game.frame, message, title, 2, -1) == 0;
    }
    
    @Internal
    public static Point getMousePositionInFrame() {
        return Game.mousePosition;
    }
    
    @API
    public static boolean isDebug() {
        return Game.debug;
    }
    
    @API
    public static boolean isVerbose() {
        return Game.verbose;
    }
    
    @API
    public static void setVerbose(final boolean value) {
        Game.verbose = value;
    }
    
    @API
    public static void setDebug(final boolean value) {
        Game.debug = value;
    }
    
    @API
    public static void writeScreenshot(final String filename) {
        final BufferedImage screenshot = new BufferedImage(Game.width, Game.height, 1);
        final Graphics2D g2d = (Graphics2D)screenshot.getGraphics();
        Game.gameLogic.getRenderThread().renderFrame(g2d);
        ImageWriter.writeImage(screenshot, filename);
    }
    
    static {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        System.setProperty("sun.java2d.noddraw", "false");
        System.setProperty("sun.java2d.pmoffscreen", "true");
        System.setProperty("sun.java2d.ddoffscreen", "true");
        System.setProperty("sun.java2d.ddscale", "true");
        frame = new Frame("Engine Alpha");
        Game.exitOnEsc = true;
        Game.pressedKeys = (Collection<Integer>)ConcurrentHashMap.newKeySet();
    }
    
    private static class MouseListener extends MouseAdapter
    {
        @Override
        public void mousePressed(final MouseEvent e) {
            this.enqueueMouseEvent(e, true);
        }
        
        @Override
        public void mouseReleased(final MouseEvent e) {
            this.enqueueMouseEvent(e, false);
        }
        
        @Override
        public void mouseEntered(final MouseEvent e) {
            Game.mousePosition = e.getPoint();
        }
        
        @Override
        public void mouseMoved(final MouseEvent e) {
            Game.mousePosition = e.getPoint();
        }
        
        @Override
        public void mouseDragged(final MouseEvent e) {
            Game.mousePosition = e.getPoint();
        }
        
        private void enqueueMouseEvent(final MouseEvent e, final boolean down) {
            final Vector sourcePosition = Game.convertMousePosition(Game.scene, e.getPoint());
            switch (e.getButton()) {
                case 1: {
                    final MouseButton button = MouseButton.LEFT;
                    break;
                }
                case 3: {
                    final MouseButton button = MouseButton.RIGHT;
                    break;
                }
                default: {
                    return;
                }
            }
            final Vector vector;
            final MouseButton mouseButton;
            Game.gameLogic.enqueue(() -> {
                if (down) {
                    Game.scene.invokeMouseDownListeners(vector, mouseButton);
                }
                else {
                    Game.scene.invokeMouseUpListeners(vector, mouseButton);
                }
            });
        }
    }
    
    private static class KeyListener extends KeyAdapter
    {
        @Override
        public void keyPressed(final KeyEvent e) {
            this.enqueueKeyEvent(e, true);
        }
        
        @Override
        public void keyReleased(final KeyEvent e) {
            this.enqueueKeyEvent(e, false);
        }
        
        private void enqueueKeyEvent(final KeyEvent e, final boolean down) {
            if (e.getKeyCode() == 27 && Game.exitOnEsc) {
                Game.exit();
            }
            final boolean pressed = Game.pressedKeys.contains(e.getKeyCode());
            if (down) {
                if (pressed) {
                    return;
                }
                Game.pressedKeys.add(e.getKeyCode());
            }
            else {
                Game.pressedKeys.remove(e.getKeyCode());
            }
            Game.gameLogic.enqueue(() -> {
                if (down) {
                    Game.scene.invokeKeyDownListeners(e);
                }
                else {
                    Game.scene.invokeKeyUpListeners(e);
                }
            });
        }
    }
}
