package audio;

import java.io.FileInputStream;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

@SuppressWarnings("ALL")
public class MusicAudio {

    Thread curThread;
    FileInputStream fis;
    AdvancedPlayer audio;

    public MusicAudio(String track, boolean looped) {
        AudioMatrix am = new AudioMatrix();
        try {
            fis = new FileInputStream(am.get(track, true));
            audio = new AdvancedPlayer(fis);
            curThread = new Thread(
                    () -> {try {
                        audio.play();
                        if (looped) {
                            audio.setPlayBackListener(
                                    new PlaybackListener() {
                                        @Override
                                        public void playbackFinished(PlaybackEvent playbackEvent) {
                                            super.playbackFinished(playbackEvent);
                                            try {audio.play();} catch (Exception e) {e.printStackTrace();}
                                        }
                                    }
                            );
                        }
                    } catch (Exception ignored) {}}
            );
            curThread.start();
        }
        catch (Exception ignored) {}
    }

    public void pause() {
        curThread.suspend();
    }

    public void resume() {
        curThread.resume();
    }

    public void stop() {
        curThread.stop();
    }

}
