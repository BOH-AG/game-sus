// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.io;

import ea.internal.util.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;
import java.net.URISyntaxException;
import java.io.IOException;
import java.nio.file.Paths;

public final class ResourceLoader
{
    private ResourceLoader() {
    }
    
    public static byte[] load(final String filename) throws IOException {
        final String normalizedFilename = normalizePath(filename);
        Path path = Paths.get(normalizedFilename, new String[0]);
        final URL url = ResourceLoader.class.getResource("/" + normalizedFilename);
        if (url != null) {
            try {
                path = Paths.get(url.toURI());
            }
            catch (URISyntaxException e) {
                throw new IOException("Could not convert URL to URI", e);
            }
        }
        return Files.readAllBytes(path);
    }
    
    public static InputStream loadAsStream(final String filename) throws IOException {
        final String normalizedFilename = normalizePath(filename);
        if (ResourceLoader.class.getResource("/" + normalizedFilename) != null) {
            return ResourceLoader.class.getResourceAsStream("/" + normalizedFilename);
        }
        return new FileInputStream(normalizePath(normalizedFilename));
    }
    
    public static File loadAsFile(final String filename) throws IOException {
        final String normalizedFilename = normalizePath(filename);
        final URL url = ResourceLoader.class.getResource("/" + normalizedFilename);
        if (url != null) {
            try {
                return new File(url.toURI());
            }
            catch (URISyntaxException e) {
                Logger.error("IO", e.getMessage());
            }
        }
        return new File(normalizePath(normalizedFilename));
    }
    
    static String normalizePath(final String path) {
        return path.replace("\\", File.separator).replace("/", File.separator);
    }
}
