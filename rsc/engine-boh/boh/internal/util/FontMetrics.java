// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.util;

import java.util.function.Supplier;
import java.awt.geom.Rectangle2D;
import ea.Vector;
import java.awt.Font;
import java.awt.Canvas;

public final class FontMetrics
{
    private static final ThreadLocal<Canvas> canvas;
    
    public static int getDescent(final Font font) {
        return FontMetrics.canvas.get().getFontMetrics(font).getDescent();
    }
    
    public static Vector getSize(final String content, final Font font) {
        final Canvas canvas = FontMetrics.canvas.get();
        final Rectangle2D bounds = canvas.getFontMetrics(font).getStringBounds(content, canvas.getGraphics());
        return new Vector(bounds.getWidth(), bounds.getHeight());
    }
    
    static {
        canvas = ThreadLocal.withInitial((Supplier<? extends Canvas>)Canvas::new);
    }
}
