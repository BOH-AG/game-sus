// 
// Decompiled by Procyon v0.5.36
// 

package ea;

import java.net.JarURLConnection;
import java.net.URLDecoder;
import ea.internal.annotations.API;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import ea.actor.Text;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import ea.actor.Polygon;
import ea.actor.Circle;
import java.awt.Color;
import ea.actor.BodyType;
import ea.actor.Rectangle;
import java.util.ArrayList;
import ea.actor.Actor;
import java.util.List;
import ea.internal.annotations.Internal;

@Internal
public final class EngineAlpha
{
    public static final int VERSION_CODE = 40000;
    public static final String VERSION_STRING = "v4.0.0-dev";
    public static final boolean IS_JAR;
    public static final long BUILD_TIME;
    
    public static void main(final String[] args) {
        Game.start(800, 600, new Scene() {
            private List<Actor> items = new ArrayList<Actor>();
            
            {
                this.setGravity(new Vector(0.0f, -9.81f));
                final Rectangle ground = new Rectangle(20.0f, 0.2f);
                ground.setCenter(0.0f, -6.0f);
                ground.setRestitution(0.9f);
                ground.setFriction(0.2f);
                ground.setBodyType(BodyType.STATIC);
                this.add(ground);
                for (int i = 0; i < 3; ++i) {
                    final Rectangle a = new Rectangle(1.0f, 1.0f);
                    a.setPosition(-5.0f, -2.0f);
                    a.setRestitution(0.9f);
                    a.setFriction(1.0f);
                    a.setBodyType(BodyType.DYNAMIC);
                    a.setColor(new Color(26, 113, 156));
                    a.setRotation(30.0f);
                    this.spawnItem(a);
                    final Circle b = new Circle(1.0f);
                    b.setPosition(5.0f, -1.0f);
                    b.setRestitution(0.9f);
                    b.setFriction(1.0f);
                    b.setBodyType(BodyType.DYNAMIC);
                    b.setColor(new Color(158, 5, 5));
                    b.applyImpulse(new Vector((float)Random.nextInteger(-100, 100), 0.0f));
                    this.spawnItem(b);
                    final Polygon c = new Polygon(new Vector[] { new Vector(0.0f, 0.0f), new Vector(1.0f, 0.0f), new Vector(0.5, 1.0) });
                    c.setRestitution(0.9f);
                    c.setFriction(1.0f);
                    c.setBodyType(BodyType.DYNAMIC);
                    c.setColor(new Color(25, 159, 69));
                    c.setRotation(-20.0f);
                    this.spawnItem(c);
                }
                final Date date = new Date(EngineAlpha.BUILD_TIME * 1000L);
                final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                final Text text = new Text("Build #40000   " + sdf.format(date), 0.5f);
                text.setPosition(-10.0f, -7.0f);
                text.setColor(Color.WHITE);
                this.add(text);
                final Iterator<Actor> iterator;
                Actor item;
                this.addFrameUpdateListener(time -> {
                    this.items.iterator();
                    while (iterator.hasNext()) {
                        item = iterator.next();
                        if (item.getCenter().getY() < -10.0f) {
                            this.spawnItem(item);
                        }
                    }
                    return;
                });
                this.addKeyListener(e -> {
                    if (e.getKeyCode() == 68) {
                        Game.setDebug(!Game.isDebug());
                    }
                });
            }
            
            private void spawnItem(final Actor item) {
                if (!item.isMounted()) {
                    this.delay((float)Random.nextInteger(5), () -> {
                        this.items.add(item);
                        this.add(item);
                        return;
                    });
                }
                item.resetMovement();
                item.setCenter((float)Random.nextInteger(-7, 7), (float)Random.nextInteger(0, 5));
            }
        });
        Game.setExitOnEsc(true);
        Game.setTitle("Engine Alpha v4.0.0-dev");
    }
    
    @API
    public static boolean isJar() {
        final String className = EngineAlpha.class.getName().replace('.', '/');
        final String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();
        return classJar.startsWith("jar:");
    }
    
    @API
    public static String getJarName() {
        final String className = EngineAlpha.class.getName().replace('.', '/');
        final String classJar = EngineAlpha.class.getResource("/" + className + ".class").toString();
        if (classJar.startsWith("jar:")) {
            final String[] split;
            final String[] values = split = classJar.split("/");
            for (final String value : split) {
                if (value.contains("!")) {
                    try {
                        return URLDecoder.decode(value.substring(0, value.length() - 1), "UTF-8");
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }
    
    @API
    public static long getBuildTime() {
        try {
            final String uri = EngineAlpha.class.getName().replace('.', '/') + ".class";
            final JarURLConnection j = (JarURLConnection)ClassLoader.getSystemResource(uri).openConnection();
            final long time = j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
            return (time > 0L) ? time : (System.currentTimeMillis() / 1000L);
        }
        catch (Exception e) {
            return System.currentTimeMillis() / 1000L;
        }
    }
    
    static {
        IS_JAR = isJar();
        BUILD_TIME = (EngineAlpha.IS_JAR ? (getBuildTime() / 1000L) : (System.currentTimeMillis() / 1000L));
    }
}
