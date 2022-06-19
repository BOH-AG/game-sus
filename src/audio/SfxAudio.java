package audio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

public class SfxAudio {
    Thread curThread;
    FileInputStream fis;
    BufferedInputStream bis;
    Player audio;

    public SfxAudio(String track) {
        AudioMatrix am = new AudioMatrix();
        try {
            fis = new FileInputStream(am.get(track, false));
            bis = new BufferedInputStream(fis);
            audio = new Player(bis);
            curThread = new Thread(
                    () -> {try {audio.play();} catch (Exception ignored) {}}
            );
            curThread.setDaemon(true);
            curThread.start();
        }
        catch (Exception ignored) {}
    }
}
