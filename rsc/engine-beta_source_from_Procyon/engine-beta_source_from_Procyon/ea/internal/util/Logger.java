// 
// Decompiled by Procyon v0.5.36
// 

package ea.internal.util;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import ea.Game;
import java.io.IOException;
import java.util.Date;
import java.io.BufferedWriter;

public final class Logger
{
    private static BufferedWriter writer;
    
    private Logger() {
    }
    
    public static void warning(final String s, final String tag) {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        write("WARNUNG", tag, e.getFileName(), e.getLineNumber(), s);
    }
    
    private static String write(final String type, final String tag, final String filename, final int line, final String message) {
        return write(type, tag, filename, line, message, false, true);
    }
    
    private static String write(final String type, final String tag, final String filename, final int line, final String message, final boolean error, final boolean printOnConsole) {
        final String str = String.format("[%s][%s][%s] %s (%s:%s)", getTime(), type, tag, message, filename, Integer.toString(line));
        if (printOnConsole) {
            if (error) {
                System.err.println(str);
            }
            else {
                System.out.println(str);
            }
        }
        return write(str);
    }
    
    private static String getTime() {
        return new Date().toString();
    }
    
    private static String write(final String text) {
        try {
            Logger.writer.write(text);
            Logger.writer.newLine();
            return text;
        }
        catch (IOException e) {
            System.err.println("Logger konnte folgende Zeile nicht schreiben:\n" + text);
            return null;
        }
    }
    
    public static void error(final String tag, final String s) {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        write("ERROR", tag, e.getFileName(), e.getLineNumber(), s, true, true);
        new RuntimeException().printStackTrace();
    }
    
    public static void info(final String tag, final String s) {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        write("INFO", tag, e.getFileName(), e.getLineNumber(), s);
    }
    
    public static void verboseInfo(final String tag, final String s) {
        if (Game.isVerbose()) {
            final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
            write("VER", tag, e.getFileName(), e.getLineNumber(), s, false, false);
        }
    }
    
    public static void debug(final String tag, final String s) {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[2];
        write("DEBUG", tag, e.getFileName(), e.getLineNumber(), s, false, Game.isDebug());
    }
    
    static {
        try {
            Logger.writer = new BufferedWriter(new FileWriter("engine-alpha.log", false));
        }
        catch (IOException e) {
            final File ea = new File("engine-alpha.log");
            if (ea.isDirectory()) {
                System.err.println("Logger konnte nicht initialisiert werden, da 'engine-alpha.log' ein Verzeichnis ist!");
                System.exit(1);
            }
            else if (!ea.canWrite()) {
                System.err.println("Logger konnte nicht initialisiert werden, da 'engine-alpha.log' nicht beschreibbar ist!");
                System.exit(1);
            }
            else {
                System.err.println("Logger konnte aus unbekannten Gr\u00fcnden nicht initialisiert werden!");
                System.exit(1);
            }
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Logger.writer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
