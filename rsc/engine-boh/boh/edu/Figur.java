// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.internal.annotations.Internal;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import ea.internal.io.ResourceLoader;
import ea.Vector;
import ea.actor.Animation;
import ea.internal.io.ImageLoader;
import ea.internal.annotations.API;
import ea.actor.StatefulAnimation;

@API
public class Figur extends EduActor<StatefulAnimation<String>>
{
    private static final float DEFAULT_FRAME_DURATION = 0.25f;
    
    @API
    public Figur(final String zustandsname, final String gifBildPfad) {
        super(new StatefulAnimation(ImageLoader.load(gifBildPfad).getWidth() / 30.0f, ImageLoader.load(gifBildPfad).getHeight() / 30.0f));
        this.fuegeZustandVonGifHinzu(zustandsname, gifBildPfad);
    }
    
    @API
    public Figur(final String zustandsname, final String spriteSheetPfad, final int anzahlX, final int anzahlY) {
        super(new StatefulAnimation(ImageLoader.load(spriteSheetPfad).getWidth() / 30.0f / anzahlX, ImageLoader.load(spriteSheetPfad).getHeight() / 30.0f / anzahlY));
        this.fuegeZustandVonSpritesheetHinzu(zustandsname, spriteSheetPfad, anzahlX, anzahlY);
    }
    
    @API
    public Figur(final String zustandname, final String verzeichnisPfad, final String praefix) {
        super(new StatefulAnimation(getWidthHeightFromPrefixed(verzeichnisPfad, praefix).getX() / 30.0f, getWidthHeightFromPrefixed(verzeichnisPfad, praefix).getY() / 30.0f));
        this.fuegeZustandVonPraefixHinzu(zustandname, verzeichnisPfad, praefix);
    }
    
    @API
    public void skaliere(final double faktor) {
        ((EduActor<StatefulAnimation>)this).getActor().setSize(((EduActor<StatefulAnimation>)this).getActor().getWidth() * (float)faktor, ((EduActor<StatefulAnimation>)this).getActor().getHeight() * (float)faktor);
    }
    
    @API
    public void fuegeZustandVonGifHinzu(final String zustandsname, final String bildpfad) {
        if (!bildpfad.toLowerCase().endsWith(".gif")) {
            throw new RuntimeException("Der agegebene Bildpfad muss eine GIF-Datei sein und auf \".gif\" enden. Der angegebene Bildpfad war: " + bildpfad);
        }
        this.getActor().addState(zustandsname, Animation.createFromAnimatedGif(bildpfad, ((EduActor<StatefulAnimation>)this).getActor().getWidth(), ((EduActor<StatefulAnimation>)this).getActor().getHeight()));
    }
    
    @API
    public void fuegeZustandVonSpritesheetHinzu(final String zustandsname, final String bildpfad, final int anzahlX, final int anzahlY) {
        this.getActor().addState(zustandsname, Animation.createFromSpritesheet(0.25f, bildpfad, anzahlX, anzahlY, ((EduActor<StatefulAnimation>)this).getActor().getWidth(), ((EduActor<StatefulAnimation>)this).getActor().getHeight()));
    }
    
    @API
    public void fuegeZustandVonEinzelbildernHinzu(final String zustandsname, final String... bildpfade) {
        this.getActor().addState(zustandsname, Animation.createFromImages(0.25f, ((EduActor<StatefulAnimation>)this).getActor().getWidth(), ((EduActor<StatefulAnimation>)this).getActor().getHeight(), bildpfade));
    }
    
    @API
    public void fuegeZustandVonPraefixHinzu(final String zustandsname, final String verzeichnis, final String praefix) {
        this.getActor().addState(zustandsname, Animation.createFromImagesPrefix(0.25f, ((EduActor<StatefulAnimation>)this).getActor().getWidth(), ((EduActor<StatefulAnimation>)this).getActor().getHeight(), verzeichnis, praefix));
    }
    
    @API
    public void setzeZustand(final String zustandsname) {
        this.getActor().setState(zustandsname);
    }
    
    @API
    public void setzeAutomatischenUebergang(final String zustandVon, final String zustandNach) {
        this.getActor().setStateTransition(zustandVon, zustandNach);
    }
    
    @API
    public String nenneAktivenZustand() {
        return this.getActor().getCurrentState();
    }
    
    @API
    public void setzeAnimationsgeschwindigkeit(final String zustandsname, final double dauerInSekunden) {
        this.getActor().setFrameDuration(zustandsname, (float)dauerInSekunden);
    }
    
    @API
    public void setzeAnimationPausiert(final boolean animationPausiert) {
        ((EduActor<StatefulAnimation>)this).getActor().setAnimationPaused(animationPausiert);
    }
    
    @API
    public boolean nenneAnimationPausiert() {
        return ((EduActor<StatefulAnimation>)this).getActor().isAnimationPaused();
    }
    
    @Internal
    private static Vector getWidthHeightFromPrefixed(final String directoryPath, final String prefix) {
        try {
            final File directory = ResourceLoader.loadAsFile(directoryPath);
            if (!directory.isDirectory()) {
                throw new RuntimeException("Der angegebene Pfad war kein Verzeichnis: " + directoryPath);
            }
            final File[] children = directory.listFiles();
            if (children != null) {
                for (final File file : children) {
                    if (!file.isDirectory() && file.getName().startsWith(prefix)) {
                        final BufferedImage image = ImageLoader.load(file.getAbsolutePath());
                        return new Vector((float)image.getWidth(), (float)image.getHeight());
                    }
                }
            }
            throw new RuntimeException("Es gab kein Bild im Verzeichnis " + directoryPath + " mit Pr\u00e4fix " + prefix);
        }
        catch (IOException e) {
            throw new RuntimeException("Fehler beim Einladen des Verzeichnisses: " + e.getMessage());
        }
    }
}
