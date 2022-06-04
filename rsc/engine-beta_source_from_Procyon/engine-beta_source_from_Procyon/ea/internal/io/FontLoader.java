// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.io;

import java.util.concurrent.ConcurrentHashMap;
import java.io.InputStream;
import java.io.IOException;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.util.Map;
import ea.internal.annotations.API;

@API
public final class FontLoader
{
    private static final int DEFAULT_SIZE = 12;
    public static final String[] systemFonts;
    private static final Map<String, Font> userFonts;
    
    @API
    public static boolean isSystemFont(final String fontName) {
        for (final String s : FontLoader.systemFonts) {
            if (s.equals(fontName)) {
                return true;
            }
        }
        return false;
    }
    
    @API
    public static String[] getSystemFonts() {
        return FontLoader.systemFonts.clone();
    }
    
    @API
    public static Font loadByName(final String fontName) {
        return new Font(fontName, 0, 12);
    }
    
    @API
    public static Font loadFromFile(final String filename) {
        if (FontLoader.userFonts.containsKey(filename)) {
            return FontLoader.userFonts.get(filename);
        }
        try (final InputStream stream = ResourceLoader.loadAsStream(filename)) {
            final Font customFont = Font.createFont(0, stream).deriveFont(0);
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            FontLoader.userFonts.put(filename, customFont);
            return customFont;
        }
        catch (FontFormatException | IOException ex2) {
            final Exception ex;
            final Exception e = ex;
            throw new RuntimeException("Die angegebene Schriftart konnte nicht geladen werden: " + filename);
        }
    }
    
    private FontLoader() {
    }
    
    static {
        userFonts = new ConcurrentHashMap<String, Font>();
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        systemFonts = ge.getAvailableFontFamilyNames();
    }
}
