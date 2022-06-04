// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal;

import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import ea.Scene;
import java.util.function.Supplier;
import ea.internal.graphics.RenderPanel;
import java.util.concurrent.Phaser;
import ea.internal.annotations.Internal;

@Internal
public final class RenderThread extends Thread
{
    private final Phaser frameStartBarrier;
    private final Phaser frameEndBarrier;
    private final RenderPanel renderPanel;
    private final Supplier<Scene> currentScene;
    private final Supplier<DebugInfo> debugInfo;
    
    @Internal
    public RenderThread(final Phaser frameStartBarrier, final Phaser frameEndBarrier, final RenderPanel renderPanel, final Supplier<Scene> currentScene, final Supplier<DebugInfo> debugInfo) {
        super("ea.rendering");
        this.frameStartBarrier = frameStartBarrier;
        this.frameEndBarrier = frameEndBarrier;
        this.renderPanel = renderPanel;
        this.currentScene = currentScene;
        this.debugInfo = debugInfo;
    }
    
    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                this.frameStartBarrier.awaitAdvanceInterruptibly(this.frameStartBarrier.arrive());
                try {
                    do {
                        final BufferStrategy bufferStrategy = this.renderPanel.getBufferStrategy();
                        do {
                            final Graphics2D g = (Graphics2D)bufferStrategy.getDrawGraphics();
                            this.renderFrame(g);
                        } while (bufferStrategy.contentsRestored() && !this.isInterrupted());
                        if (!bufferStrategy.contentsLost()) {
                            bufferStrategy.show();
                            Toolkit.getDefaultToolkit().sync();
                        }
                    } while (this.renderPanel.getBufferStrategy().contentsLost() && !this.isInterrupted());
                }
                catch (IllegalStateException e) {
                    throw new RuntimeException(e);
                }
                this.frameEndBarrier.awaitAdvanceInterruptibly(this.frameEndBarrier.arrive());
                continue;
            }
            catch (InterruptedException e2) {
                return;
            }
            break;
        }
    }
    
    @Internal
    public void renderFrame(final Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        final Scene scene = this.currentScene.get();
        final DebugInfo debugInfo = this.debugInfo.get();
        this.renderPanel.render(g, scene);
        if (debugInfo != null) {
            this.renderPanel.renderGrid(g, scene);
            this.renderPanel.renderInfo(g, debugInfo);
        }
        g.dispose();
    }
}
