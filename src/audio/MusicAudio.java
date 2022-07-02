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
    AudioMatrix am;

    public MusicAudio(String track, boolean looped) {
        am = new AudioMatrix();
        try {
            if (looped) createLoopedMusic(track);
            else createMusic(track);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createMusic(String track) throws Exception {
        fis = new FileInputStream(am.get(track, true));
        audio = new AdvancedPlayer(fis);
        curThread = new Thread(() -> {
            try {audio.play();} catch (Exception e) {e.printStackTrace();}
        });
        curThread.start();
    }

    private void createLoopedMusic(String track) throws Exception {
        fis = new FileInputStream(am.get(track, true));
        audio = new AdvancedPlayer(fis);
        curThread = new Thread(() -> {
            try {audio.play();} catch (Exception e) {e.printStackTrace();}
        });
        curThread.start();
        audio.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent evt) {
                super.playbackFinished(evt);
                curThread = null;
                try {createLoopedMusic(track);} catch (Exception e) {e.printStackTrace();}
            }
        });
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
