// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.internal.annotations.API;
import java.util.function.Supplier;
import ea.internal.annotations.Internal;
import ea.Layer;
import ea.edu.event.TastenReagierbar;
import ea.edu.event.MausRadReagierbar;
import ea.edu.event.MausKlickReagierbar;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.actor.Actor;
import ea.edu.internal.EduScene;

public final class EduSetup
{
    private static ThreadLocal<EduScene> customSetup;
    
    @Internal
    static <T extends Actor> void setup(final EduActor<T> eduActor, final EduScene eduScene) {
        final Layer activeLayer = eduScene.getActiveLayer();
        activeLayer.defer(() -> activeLayer.add(eduActor.getActor()));
        if (eduActor instanceof BildAktualisierungReagierbar) {
            eduScene.addEduFrameUpdateListener((BildAktualisierungReagierbar)eduActor);
        }
        if (eduActor instanceof MausKlickReagierbar) {
            eduScene.addEduClickListener((MausKlickReagierbar)eduActor);
        }
        if (eduActor instanceof MausRadReagierbar) {
            eduScene.addEduMouseWheelListener((MausRadReagierbar)eduActor);
        }
        if (eduActor instanceof TastenReagierbar) {
            eduScene.addEduKeyListener((TastenReagierbar)eduActor);
        }
    }
    
    public static EduScene getActiveScene() {
        EduScene activeScene = EduSetup.customSetup.get();
        if (activeScene == null) {
            activeScene = Spiel.getActiveScene();
        }
        return activeScene;
    }
    
    @API
    public static <T> T customSetup(final Supplier<T> runnable, final EduScene scene) {
        final EduScene pre = EduSetup.customSetup.get();
        try {
            EduSetup.customSetup.set(scene);
            return runnable.get();
        }
        finally {
            EduSetup.customSetup.set(pre);
        }
    }
    
    private EduSetup() {
    }
    
    static {
        EduSetup.customSetup = ThreadLocal.withInitial(() -> null);
    }
}
