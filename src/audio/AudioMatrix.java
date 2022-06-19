package audio;

import java.util.HashMap;
import java.util.Map;

final class AudioMatrix {

    private Map<String, String> map1;
    private Map<String, String> map2;

    AudioMatrix() {
        map1 = new HashMap<>();

        map1.put("radiation storm"       , "rsc/music/WBA-Radiation Storm-cut.mp3");
        map1.put("license to kill"       , "rsc/music/WBA-License to Kill.mp3");
        map1.put("eternal night"         , "rsc/music/WBA-Eternal Night.mp3");
        map1.put("deadly force"          , "rsc/music/WBA-Deadly Force.mp3");
        map1.put("be quick or be dead"   , "rsc/music/WBA-Be Quick or Be Dead.mp3");
        map1.put("dream"                 , "rsc/music/dream.mp3");
        map1.put("ghostbusters"          , "rsc/music/14 - Ghostbusters (_Ghostbusters_) - Parker, Ray Jr._Ray Parker Jr. - Ghostbusters (Original Motion Picture Soundtrack).mp3");

        map2 = new HashMap<>();

        map2.put("rifle"                 , "rsc/sfx/rifleshot.mp3");
    }

    public String get(String track, boolean music) {
        if (music) return map1.get(track);
        else return map2.get(track);
    }

}
