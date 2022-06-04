// 
// Decompiled by Procyon v0.5.36
// 

package ea.actor;

import org.jbox2d.collision.shapes.Shape;
import ea.internal.util.GifDecoder;
import java.io.File;
import java.util.Comparator;
import java.io.IOException;
import ea.internal.io.ResourceLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import ea.internal.io.ImageLoader;
import java.awt.Graphics2D;
import ea.internal.annotations.Internal;
import ea.internal.ShapeBuilder;
import ea.event.EventListeners;
import ea.internal.graphics.AnimationFrame;
import ea.internal.annotations.API;
import ea.FrameUpdateListener;

@API
public class Animation extends Actor implements FrameUpdateListener
{
    private final AnimationFrame[] frames;
    private final float width;
    private final float height;
    private transient float currentTime;
    private transient int currentIndex;
    private EventListeners<Runnable> onCompleteListeners;
    
    private Animation(final AnimationFrame[] frames, final float width, final float height) {
        super(() -> {
            if (frames.length < 1) {
                throw new RuntimeException("Eine Animation kann nicht mit einem leeren Frames-Array initialisiert werden.");
            }
            else {
                return ShapeBuilder.createSimpleRectangularShape(width, height);
            }
        });
        this.onCompleteListeners = new EventListeners<Runnable>();
        for (final AnimationFrame frame : frames) {
            if (frame.getDuration() <= 0.0f) {
                throw new RuntimeException("Ein Frame muss l\u00e4nger als 0 Sekunden sein");
            }
        }
        this.frames = frames.clone();
        this.width = width;
        this.height = height;
        this.currentTime = 0.0f;
        this.currentIndex = 0;
    }
    
    public Animation(final Animation animation) {
        this(animation.frames, animation.width, animation.height);
        animation.onCompleteListeners.invoke(this::addOnCompleteListener);
    }
    
    @Internal
    public AnimationFrame[] getFrames() {
        return this.frames.clone();
    }
    
    @API
    public float getWidth() {
        return this.width;
    }
    
    @API
    public float getHeight() {
        return this.height;
    }
    
    @API
    public void addOnCompleteListener(final Runnable listener) {
        this.onCompleteListeners.add(listener);
    }
    
    @Internal
    @Override
    public void onFrameUpdate(final float deltaSeconds) {
        this.currentTime += deltaSeconds;
        final AnimationFrame currentFrame = this.frames[this.currentIndex];
        while (this.currentTime > currentFrame.getDuration()) {
            this.currentTime -= currentFrame.getDuration();
            if (this.currentIndex + 1 == this.frames.length) {
                this.onCompleteListeners.invoke(Runnable::run);
                this.currentIndex = 0;
            }
            else {
                ++this.currentIndex;
            }
        }
    }
    
    @Override
    public void render(final Graphics2D g, final float pixelPerMeter) {
        this.frames[this.currentIndex].render(g, this.width * pixelPerMeter, this.height * pixelPerMeter, false, false);
    }
    
    @API
    public static Animation createFromSpritesheet(final float frameDuration, final String filepath, final int x, final int y, final float width, final float height) {
        if (frameDuration <= 0.0f) {
            throw new RuntimeException("Frame-L\u00e4nge muss gr\u00f6\u00dfer als 0 sein");
        }
        final BufferedImage image = ImageLoader.load(filepath);
        if (image.getWidth() % x != 0) {
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Ma\u00dfe (Breite: %d) um es auf %d Elemente in getX-Richtung aufzuteilen.", image.getWidth(), x));
        }
        if (image.getHeight() % y != 0) {
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Ma\u00dfe (H\u00f6he: %d) um es auf %d Elemente in getY-Richtung aufzuteilen.", image.getHeight(), y));
        }
        final int imageWidth = image.getWidth() / x;
        final int imageHeight = image.getHeight() / y;
        final List<AnimationFrame> frames = new LinkedList<AnimationFrame>();
        for (int j = 0; j < y; ++j) {
            for (int i = 0; i < x; ++i) {
                frames.add(new AnimationFrame(image.getSubimage(i * imageWidth, j * imageHeight, imageWidth, imageHeight), frameDuration));
            }
        }
        return new Animation(frames.toArray(new AnimationFrame[0]), width, height);
    }
    
    @API
    public static Animation createFromImages(final float frameDuration, final float width, final float height, final String... filepaths) {
        if (frameDuration <= 0.0f) {
            throw new RuntimeException("Frame-L\u00e4nge muss gr\u00f6\u00dfer als 1 sein.");
        }
        final Collection<AnimationFrame> frames = new LinkedList<AnimationFrame>();
        for (final String filepath : filepaths) {
            frames.add(new AnimationFrame(ImageLoader.load(filepath), frameDuration));
        }
        return new Animation(frames.toArray(new AnimationFrame[0]), width, height);
    }
    
    @API
    public static Animation createFromImagesPrefix(final float frameDuration, final float width, final float height, final String directoryPath, final String prefix) {
        final ArrayList<String> allPaths = new ArrayList<String>();
        File directory;
        try {
            directory = ResourceLoader.loadAsFile(directoryPath);
        }
        catch (IOException e) {
            throw new RuntimeException("Fehler beim Einladen des Verzeichnisses: " + e.getMessage());
        }
        if (!directory.isDirectory()) {
            throw new RuntimeException("Der angegebene Pfad war kein Verzeichnis: " + directoryPath);
        }
        final File[] children = directory.listFiles();
        if (children != null) {
            for (final File file : children) {
                if (!file.isDirectory() && file.getName().startsWith(prefix)) {
                    allPaths.add(file.getAbsolutePath());
                }
            }
        }
        allPaths.sort(Comparator.naturalOrder());
        if (allPaths.isEmpty()) {
            throw new RuntimeException("Konnte keine Bilder mit Pr\u00e4fix \"" + prefix + "\" im Verzeichnis \"" + directoryPath + "\" finden.");
        }
        return createFromImages(frameDuration, width, height, (String[])allPaths.toArray(new String[0]));
    }
    
    @API
    public static Animation createFromAnimatedGif(final String filepath, final float width, final float height) {
        final GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(filepath);
        final int frameCount = gifDecoder.getFrameCount();
        final AnimationFrame[] frames = new AnimationFrame[frameCount];
        for (int i = 0; i < frameCount; ++i) {
            final BufferedImage frame = gifDecoder.getFrame(i);
            final int durationInMillis = gifDecoder.getDelay(i);
            frames[i] = new AnimationFrame(frame, durationInMillis / 1000.0f);
        }
        return new Animation(frames, width, height);
    }
}
