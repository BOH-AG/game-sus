// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import ea.internal.annotations.Internal;
import java.util.concurrent.TimeUnit;
import ea.internal.DebugInfo;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import ea.internal.graphics.RenderPanel;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.concurrent.Phaser;
import ea.internal.RenderThread;
import java.util.concurrent.ExecutorService;

public final class GameLogic
{
    private static final float DESIRED_FRAME_DURATION = 0.016f;
    private static final int NANOSECONDS_PER_SECOND = 1000000000;
    private final ExecutorService threadPoolExecutor;
    private final RenderThread renderThread;
    private final Phaser frameStartBarrier;
    private final Phaser frameEndBarrier;
    private final Supplier<Scene> currentScene;
    private Queue<Runnable> dispatchableQueue;
    private float frameDuration;
    
    public GameLogic(final RenderPanel renderPanel, final Supplier<Scene> currentScene, final Supplier<Boolean> isDebug) {
        this.threadPoolExecutor = Executors.newCachedThreadPool();
        this.frameStartBarrier = new Phaser(2);
        this.frameEndBarrier = new Phaser(2);
        this.dispatchableQueue = new ConcurrentLinkedQueue<Runnable>();
        this.renderThread = new RenderThread(this.frameStartBarrier, this.frameEndBarrier, renderPanel, currentScene, () -> {
            if (isDebug.get()) {
                return new DebugInfo(this.frameDuration, currentScene.get().getWorldHandler().getWorld().getBodyCount());
            }
            else {
                return null;
            }
        });
        this.currentScene = currentScene;
    }
    
    public void enqueue(final Runnable runnable) {
        this.dispatchableQueue.add(runnable);
    }
    
    public void run() {
        this.renderThread.start();
        this.frameDuration = 0.016f;
        long frameStart = System.nanoTime();
        while (!Thread.interrupted()) {
            final Scene scene = this.currentScene.get();
            try {
                final float deltaSeconds = Math.min(0.032f, this.frameDuration);
                scene.step(deltaSeconds, this.threadPoolExecutor::submit);
                scene.getCamera().onFrameUpdate();
                this.frameStartBarrier.arriveAndAwaitAdvance();
                scene.invokeFrameUpdateListeners(deltaSeconds);
                for (Runnable runnable = this.dispatchableQueue.poll(); runnable != null; runnable = this.dispatchableQueue.poll()) {
                    runnable.run();
                }
                this.frameEndBarrier.arriveAndAwaitAdvance();
                long frameEnd = System.nanoTime();
                final float duration = (frameEnd - frameStart) / 1.0E9f;
                if (duration < 0.016f) {
                    try {
                        Thread.sleep((int)(1000.0f * (0.016f - duration)));
                    }
                    catch (InterruptedException e2) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                frameEnd = System.nanoTime();
                this.frameDuration = (frameEnd - frameStart) / 1.0E9f;
                frameStart = frameEnd;
            }
            catch (InterruptedException e3) {
                break;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        while (this.renderThread.isAlive()) {
            this.renderThread.interrupt();
            try {
                this.renderThread.join();
            }
            catch (InterruptedException ex) {}
        }
        this.threadPoolExecutor.shutdown();
        try {
            this.threadPoolExecutor.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e4) {}
    }
    
    @Internal
    RenderThread getRenderThread() {
        return this.renderThread;
    }
}
