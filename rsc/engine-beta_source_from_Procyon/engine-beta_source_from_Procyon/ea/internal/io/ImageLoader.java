// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.io;

import java.util.HashMap;
import ea.internal.annotations.API;
import java.io.IOException;
import ea.internal.util.Optimizer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Map;

public final class ImageLoader
{
    private static final Map<String, BufferedImage> cache;
    
    private ImageLoader() {
    }
    
    public static BufferedImage load(final String path) {
        if (ImageLoader.cache.containsKey(path)) {
            return ImageLoader.cache.get(path);
        }
        try {
            final BufferedImage img = Optimizer.toCompatibleImage(ImageIO.read(ResourceLoader.loadAsStream(path)));
            ImageLoader.cache.put(path, img);
            return img;
        }
        catch (IOException e) {
            throw new RuntimeException("Das Image konnte nicht geladen werden: " + path);
        }
    }
    
    @API
    public static void clearCache() {
        ImageLoader.cache.clear();
    }
    
    @API
    public static void clearCache(final String path) {
        ImageLoader.cache.remove(path);
    }
    
    static {
        cache = new HashMap<String, BufferedImage>();
    }
}
