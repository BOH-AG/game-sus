// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.util;

import java.awt.GraphicsEnvironment;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.GraphicsConfiguration;

public final class Optimizer
{
    private static final GraphicsConfiguration graphicsConfig;
    
    private Optimizer() {
    }
    
    public static BufferedImage toCompatibleImage(final BufferedImage img) {
        final ColorModel a = img.getColorModel();
        final ColorModel b = Optimizer.graphicsConfig.getColorModel(a.getTransparency());
        if (a.equals(b)) {
            return img;
        }
        final BufferedImage compat = Optimizer.graphicsConfig.createCompatibleImage(img.getWidth(), img.getHeight(), img.getTransparency());
        final Graphics2D g = (Graphics2D)compat.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return compat;
    }
    
    static {
        graphicsConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
}
