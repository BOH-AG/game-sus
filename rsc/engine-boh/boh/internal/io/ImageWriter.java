// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.io;

import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import ea.internal.util.Logger;
import java.awt.image.BufferedImage;

public class ImageWriter
{
    public static void writeImage(final BufferedImage image, final String path) {
        final String pathlowercase = path.toLowerCase();
        String formatname = null;
        if (pathlowercase.endsWith(".png")) {
            formatname = "png";
        }
        else if (pathlowercase.endsWith(".gif")) {
            formatname = "gif";
        }
        else {
            if (!pathlowercase.endsWith(".jpg")) {
                Logger.error("IO", "Nicht unterst\u00fctztes Format. Nur png, jpg, gif ist unterst\u00fctzt");
                return;
            }
            formatname = "jpg";
        }
        try {
            ImageIO.write(image, formatname, new File(ResourceLoader.normalizePath(path)));
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Schreiben des Bildes");
        }
    }
}
