// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.graphics;

import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import java.awt.Graphics;
import ea.internal.DebugInfo;
import ea.Vector;
import ea.Camera;
import java.awt.Font;
import ea.internal.annotations.Internal;
import java.awt.geom.AffineTransform;
import ea.Scene;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Canvas;

public final class RenderPanel extends Canvas
{
    private static final Color COLOR_FPS_BACKGROUND;
    private static final Color COLOR_FPS_BORDER;
    private static final Color COLOR_BODY_COUNT_BORDER;
    private static final Color COLOR_BODY_COUNT_BACKGROUND;
    private static final int DEBUG_INFO_HEIGHT = 20;
    private static final int DEBUG_INFO_LEFT = 10;
    private static final int DEBUG_INFO_TEXT_OFFSET = 16;
    private static final Color DEBUG_GRID_COLOR;
    public static final int GRID_SIZE_IN_PIXELS = 150;
    public static final int GRID_SIZE_METER_LIMIT = 100000;
    public static final int DEBUG_TEXT_SIZE = 12;
    
    public RenderPanel(final int width, final int height) {
        this.setSize(width, height);
        this.setPreferredSize(this.getSize());
    }
    
    public final void allocateBuffers() {
        this.createBufferStrategy(2);
    }
    
    @Internal
    public void render(final Graphics2D g, final Scene scene) {
        g.setColor(Color.black);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        final AffineTransform transform = g.getTransform();
        scene.render(g, this.getWidth(), this.getHeight());
        g.setTransform(transform);
    }
    
    @Internal
    public void renderGrid(final Graphics2D g, final Scene scene) {
        final AffineTransform pre = g.getTransform();
        final Camera camera = scene.getCamera();
        final Vector position = camera.getPosition();
        final float rotation = -camera.getRotation();
        final int width = this.getWidth();
        final int height = this.getHeight();
        g.setClip(0, 0, width, height);
        g.translate(width / 2, height / 2);
        final float pixelPerMeter = camera.getZoom();
        g.rotate(rotation, 0.0, 0.0);
        g.translate(-position.getX() * pixelPerMeter, position.getY() * pixelPerMeter);
        final int gridSizeInMeters = Math.round(150.0f / pixelPerMeter);
        final float gridSizeInPixels = gridSizeInMeters * pixelPerMeter;
        final float gridSizeFactor = gridSizeInPixels / gridSizeInMeters;
        if (gridSizeInMeters > 0 && gridSizeInMeters < 100000) {
            final int windowSizeInPixels = (int)Math.ceil(Math.max(width, height));
            int startX = (int)(position.getX() - windowSizeInPixels / 2 / pixelPerMeter);
            int startY = (int)(-1.0f * position.getY() - windowSizeInPixels / 2 / pixelPerMeter);
            startX -= startX % gridSizeInMeters + gridSizeInMeters;
            startY -= startY % gridSizeInMeters + gridSizeInMeters;
            startX -= gridSizeInMeters;
            final int stopX = (int)(startX + windowSizeInPixels / pixelPerMeter + gridSizeInMeters * 2);
            final int stopY = (int)(startY + windowSizeInPixels / pixelPerMeter + gridSizeInMeters * 2);
            g.setFont(new Font("Monospaced", 0, 12));
            g.setColor(RenderPanel.DEBUG_GRID_COLOR);
            for (int x = startX; x < stopX; x += gridSizeInMeters) {
                g.fillRect((int)(x * gridSizeFactor) - 1, (int)((startY - 1) * gridSizeFactor), 2, (int)(windowSizeInPixels + 2.0f * gridSizeInPixels));
            }
            for (int y = startY; y < stopY; y += gridSizeInMeters) {
                g.fillRect((int)((startX - 1) * gridSizeFactor), (int)(y * gridSizeFactor - 1.0f), (int)(windowSizeInPixels + 2.0f * gridSizeInPixels), 2);
            }
            for (int x = startX; x < stopX; x += gridSizeInMeters) {
                for (int y2 = startY; y2 < stopY; y2 += gridSizeInMeters) {
                    g.drawString(x + " / " + -y2, x * gridSizeFactor + 5.0f, y2 * gridSizeFactor - 5.0f);
                }
            }
        }
        g.setTransform(pre);
    }
    
    @Internal
    public void renderInfo(final Graphics2D g, final DebugInfo debugInfo) {
        final float frameDuration = debugInfo.getFrameDuration();
        final int bodyCount = debugInfo.getBodyCount();
        final Font displayFont = new Font("Monospaced", 0, 12);
        final FontMetrics fm = g.getFontMetrics(displayFont);
        int y = 10;
        final String fpsMessage = "FPS: " + ((frameDuration == 0.0f) ? "\u221e" : Integer.valueOf(Math.round(1.0f / frameDuration)));
        Rectangle2D bounds = fm.getStringBounds(fpsMessage, g);
        g.setColor(RenderPanel.COLOR_FPS_BORDER);
        g.fillRect(10, y, (int)bounds.getWidth() + 20, (int)bounds.getHeight() + 16);
        g.setColor(RenderPanel.COLOR_FPS_BACKGROUND);
        g.drawRect(10, y, (int)bounds.getWidth() + 20 - 1, (int)bounds.getHeight() + 16 - 1);
        g.setColor(Color.WHITE);
        g.setFont(displayFont);
        g.drawString(fpsMessage, 20, y + 8 + fm.getHeight() - fm.getDescent());
        y += fm.getHeight() + 20;
        final String bodyMessage = "Bodies: " + bodyCount;
        bounds = fm.getStringBounds(bodyMessage, g);
        g.setColor(RenderPanel.COLOR_BODY_COUNT_BORDER);
        g.fillRect(10, y, (int)bounds.getWidth() + 20, (int)bounds.getHeight() + 16);
        g.setColor(RenderPanel.COLOR_BODY_COUNT_BACKGROUND);
        g.drawRect(10, y, (int)bounds.getWidth() + 20 - 1, (int)bounds.getHeight() + 16 - 1);
        g.setColor(Color.WHITE);
        g.setFont(displayFont);
        g.drawString(bodyMessage, 20, y + 8 + fm.getHeight() - fm.getDescent());
    }
    
    static {
        COLOR_FPS_BACKGROUND = new Color(255, 255, 255, 50);
        COLOR_FPS_BORDER = new Color(0, 106, 214);
        COLOR_BODY_COUNT_BORDER = new Color(0, 214, 84);
        COLOR_BODY_COUNT_BACKGROUND = new Color(255, 255, 255, 50);
        DEBUG_GRID_COLOR = new Color(255, 255, 255, 100);
    }
}
