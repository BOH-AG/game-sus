package audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import ea.FrameUpdateListener;
import ea.event.EventListeners;
import ea.event.FrameUpdateListenerContainer;
import javazoom.jl.player.Player;

public class SfxAudio implements FrameUpdateListenerContainer {
    Thread curThread;
    FileInputStream fis;
    BufferedInputStream bis;
    Player audio;
    int a;

    public SfxAudio(String track) {
        AudioMatrix am = new AudioMatrix();
        a = 0;
        try {
            fis = new FileInputStream(am.get(track, false));
            bis = new BufferedInputStream(fis);
            audio = new Player(bis);
            curThread = new Thread(
                    () -> {try {audio.play();} catch (Exception ignored) {}}
            );
            curThread.setDaemon(true);
            curThread.start();
            delay(
                    0.1f,
                    () -> {curThread = null;}
            );

        }
        catch (Exception ignored) {}
    }


    @Override
    public EventListeners<FrameUpdateListener> getFrameUpdateListeners() {
        return null;
    }

    @Override
    public FrameUpdateListener delay(float timeInSeconds, Runnable runnable) {
        return FrameUpdateListenerContainer.super.delay(timeInSeconds, runnable);
    }
}
