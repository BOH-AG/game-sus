// 
// Decompiled by Procyon v0.5.36
// 

package ea.edu;

import ea.edu.event.MausRadReagierbar;
import ea.edu.event.BildAktualisierungReagierbar;
import ea.internal.util.Logger;
import ea.edu.event.Ticker;
import ea.edu.event.TastenReagierbar;
import ea.edu.event.MausKlickReagierbar;
import ea.Vector;
import ea.Scene;
import ea.Game;
import java.util.function.Function;
import java.util.Map;
import ea.internal.annotations.Internal;
import ea.edu.internal.EduScene;
import java.awt.Color;
import java.util.HashMap;
import ea.internal.annotations.API;

@API
public class Spiel
{
    public static final String STANDARD_TITEL = "BohAG Game";
    public static final int STANDARD_BREITE = 1280;
    public static final int STANDARD_HOEHE = 720;
    private static final HashMap<String, Color> farben;
    private static final HashMap<String, EduScene> szenen;
    private static final Color COLOR_LILA;
    private static final Color COLOR_ORANGE;
    private static final Color COLOR_BRAUN;
    private static final Color COLOR_HELLBLAU;
    private static final Color COLOR_DUNKELBLAU;
    private static final Color COLOR_HELLGRUEN;
    private static final Color COLOR_DUNKELGRUEN;
    private static final double TICKER_THRESHOLD = 0.09;
    private static int fensterBreite;
    private static int fensterHoehe;
    private static EduScene activeScene;
    
    @API
    public static void parallel(final Runnable runnable) {
        new Thread(runnable).start();
    }
    
    @API
    public static void registriereFarbe(final String string, final Color color) {
        Spiel.farben.put(string.toLowerCase(), color);
    }
    
    @API
    public static String[] nenneFarben() {
        return Spiel.farben.keySet().toArray(new String[0]);
    }
    
    @Internal
    public static Color konvertiereVonFarbname(final String farbname) {
        final Color color = Spiel.farben.get(farbname.toLowerCase());
        if (color == null) {
            throw new IllegalArgumentException("Eine Farbe mit dem Namen " + farbname + " ist der Engine nicht bekannt");
        }
        return color;
    }
    
    @Internal
    public static String konvertiereZuFarbname(final Color color) {
        return Spiel.farben.entrySet().stream().filter(entry -> entry.getValue().equals(color)).map((Function<? super Object, ? extends String>)Map.Entry::getKey).findFirst().orElse("unbekannt");
    }
    
    @API
    public static void setzeFensterGroesse(final int breite, final int hoehe) {
        if (Spiel.activeScene != null) {
            throw new RuntimeException("setzeFensterGroesse() kann nur aufgerufen werden, bevor das erste grafische Objekt erzeugt wurde");
        }
        if (breite <= 0 || hoehe <= 0) {
            throw new RuntimeException("Die Fensterma\u00dfe (Breite sowie H\u00f6he) m\u00fcssen jeweils gr\u00f6\u00dfer als 0 sein. Eingabe war: " + breite + " Breite und " + hoehe + " H\u00f6he");
        }
        Spiel.fensterBreite = breite;
        Spiel.fensterHoehe = hoehe;
    }
    
    @Internal
    public static EduScene getActiveScene() {
        if (Spiel.activeScene == null) {
            Spiel.activeScene = new EduScene();
            parallel(() -> Game.start(Spiel.fensterBreite, Spiel.fensterHoehe, Spiel.activeScene));
        }
        return Spiel.activeScene;
    }
    
    @Internal
    private static void setActiveScene(final EduScene eduScene) {
        Game.transitionToScene(Spiel.activeScene = eduScene);
    }
    
    @API
    public void setzeRasterSichtbar(final boolean sichtbar) {
        Game.setDebug(sichtbar);
    }
    
    @API
    public void setzeErkundungsmodusAktiv(final boolean aktiv) {
        getActiveScene().setExploreMode(aktiv);
    }
    
    @API
    public void benenneAktiveSzene(final String name) {
        final EduScene activeScene = getActiveScene();
        if (activeScene.getName() != null) {
            throw new RuntimeException("Die Szene hat bereits einen Namen: " + activeScene.getName());
        }
        activeScene.setName(name);
        Spiel.szenen.put(name, activeScene);
    }
    
    @API
    public void erzeugeNeueSzene() {
        setActiveScene(new EduScene());
    }
    
    @API
    public void setzeAktiveSzene(final String name) {
        final EduScene scene = Spiel.szenen.get(name);
        if (scene == null) {
            throw new RuntimeException("Konnte keine Szene mit dem Namen '" + name + "' finden");
        }
        setActiveScene(scene);
    }
    
    @API
    public String[] nenneSzenennamen() {
        return Spiel.szenen.keySet().toArray(new String[0]);
    }
    
    @API
    public void erzeugeNeueEbene(final String ebenenName, final int ebenenPosition) {
        getActiveScene().addLayer(ebenenName, ebenenPosition);
    }
    
    @API
    public void setzeEbenenparallaxe(final String ebenenName, final double x, final double y, final double zoom) {
        getActiveScene().setLayerParallax(ebenenName, (float)x, (float)y, (float)zoom);
    }
    
    @API
    public void setzeEbenenzeitverzerrung(final String ebenenName, final double zeitverzerrung) {
        getActiveScene().setLayerTimeDistort(ebenenName, (float)zeitverzerrung);
    }
    
    @API
    public void setzeAktiveEbene(final String ebenenName) {
        getActiveScene().setActiveLayer(ebenenName);
    }
    
    @API
    public void setzeAktiveEbeneAufHauptebene() {
        getActiveScene().resetToMainLayer();
    }
    
    @API
    public String[] nenneEbenennamenVonAktiverSzene() {
        return getActiveScene().getLayerNames();
    }
    
    @API
    public void verschiebeKamera(final double x, final double y) {
        getActiveScene().getCamera().moveBy(new Vector(x, y));
    }
    
    @API
    public void setzeKamerazoom(final double zoom) {
        getActiveScene().getCamera().setZoom((float)zoom);
    }
    
    @API
    public double nenneKamerazoom() {
        return getActiveScene().getCamera().getZoom();
    }
    
    @API
    public void setzeKamerafokus(final EduActor fokus) {
        getActiveScene().getCamera().setFocus(fokus.getActor());
    }
    
    @API
    public void rotiereKamera(final double grad) {
        getActiveScene().getCamera().rotateBy((float)grad);
    }
    
    @API
    public void setzeKamerarotation(final double grad) {
        getActiveScene().getCamera().rotateTo((float)grad);
    }
    
    @API
    public void setzeSchwerkraft(final double schwerkraft) {
        getActiveScene().getActiveLayer().setGravity(new Vector(0.0, -schwerkraft));
    }
    
    @API
    public void zeigeNachricht(final String nachricht) {
        Game.showMessage(nachricht, "Engine Alpha - EDU Version");
    }
    
    @API
    public boolean zeigeNachrichtMitBestaetigung(final String frage) {
        return Game.requestOkCancel(frage, "Engine Alpha - EDU Version");
    }
    
    @API
    public boolean zeigeNachrichtMitJaNein(final String frage) {
        return Game.requestYesNo(frage, "Engine Alpha - EDU Version");
    }
    
    @API
    public String zeigeNachrichtMitEingabe(final String nachricht) {
        return Game.requestStringInput(nachricht, "Engine Alpha - EDU Version");
    }
    
    @API
    public void registriereMausKlickReagierbar(final MausKlickReagierbar mausKlickReagierbar) {
        getActiveScene().addEduClickListener(mausKlickReagierbar);
    }
    
    @API
    public void entferneMausKlickReagierbar(final MausKlickReagierbar mausKlickReagierbar) {
        getActiveScene().removeEduClickListener(mausKlickReagierbar);
    }
    
    @API
    public void registriereTastenReagierbar(final TastenReagierbar tastenReagierbar) {
        getActiveScene().addEduKeyListener(tastenReagierbar);
    }
    
    @API
    public void entferneTastenReagierbar(final TastenReagierbar tastenReagierbar) {
        getActiveScene().removeEduKeyListener(tastenReagierbar);
    }
    
    @API
    public void registriereTicker(final double intervallInSekunden, final Ticker ticker) {
        if (intervallInSekunden < 0.09) {
            Logger.warning("Du hast einen Ticker mit geringem Intervall angemeldet (" + intervallInSekunden + " s). Bei so Intervalwert nahe der Framerate k\u00f6nnen unerw\u00fcnschte Effekte eintreten. Nutze stattdessen BildAktualisierungsReagierbar !", "EDU");
        }
        getActiveScene().addEduTicker((float)intervallInSekunden, ticker);
    }
    
    @API
    public void entferneTicker(final Ticker ticker) {
        getActiveScene().removeEduTicker(ticker);
    }
    
    @API
    public void registriereBildAktualisierungReagierbar(final BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        getActiveScene().addEduFrameUpdateListener(bildAktualisierungReagierbar);
    }
    
    @API
    public void entferneBildAktualisierungReagierbar(final BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        getActiveScene().removeEduFrameUpdateListener(bildAktualisierungReagierbar);
    }
    
    @API
    public void registriereMausRadReagierbar(final MausRadReagierbar mausRadReagierbar) {
        getActiveScene().addEduMouseWheelListener(mausRadReagierbar);
    }
    
    @API
    public void entferneMausRadReagierbar(final MausRadReagierbar mausRadReagierbar) {
        getActiveScene().removeEduMouseWheelListener(mausRadReagierbar);
    }
    
    @API
    public double nenneMausPositionX() {
        return getActiveScene().getMousePosition().getX();
    }
    
    @API
    public double nenneMausPositionY() {
        return getActiveScene().getMousePosition().getY();
    }
    
    static {
        farben = new HashMap<String, Color>();
        szenen = new HashMap<String, EduScene>();
        COLOR_LILA = new Color(145, 19, 255);
        COLOR_ORANGE = new Color(255, 116, 0);
        COLOR_BRAUN = new Color(119, 77, 50);
        COLOR_HELLBLAU = new Color(0, 194, 255);
        COLOR_DUNKELBLAU = new Color(21, 0, 137);
        COLOR_HELLGRUEN = new Color(157, 255, 0);
        COLOR_DUNKELGRUEN = new Color(11, 71, 0);
        Spiel.fensterBreite = 800;
        Spiel.fensterHoehe = 600;
        registriereFarbe("gelb", Color.YELLOW);
        registriereFarbe("weiss", Color.WHITE);
        registriereFarbe("wei\u00df", Color.WHITE);
        registriereFarbe("orange", Spiel.COLOR_ORANGE);
        registriereFarbe("grau", Color.GRAY);
        registriereFarbe("gruen", Color.GREEN);
        registriereFarbe("gr\u00fcn", Color.GREEN);
        registriereFarbe("blau", Color.BLUE);
        registriereFarbe("rot", Color.RED);
        registriereFarbe("pink", Color.PINK);
        registriereFarbe("magenta", Color.MAGENTA);
        registriereFarbe("lila", Spiel.COLOR_LILA);
        registriereFarbe("cyan", Color.CYAN);
        registriereFarbe("tuerkis", Color.CYAN);
        registriereFarbe("t\u00fcrkis", Color.CYAN);
        registriereFarbe("dunkelgrau", Color.DARK_GRAY);
        registriereFarbe("hellgrau", Color.LIGHT_GRAY);
        registriereFarbe("braun", Spiel.COLOR_BRAUN);
        registriereFarbe("schwarz", Color.BLACK);
        registriereFarbe("hellblau", Spiel.COLOR_HELLBLAU);
        registriereFarbe("dunkelblau", Spiel.COLOR_DUNKELBLAU);
        registriereFarbe("hellgruen", Spiel.COLOR_HELLGRUEN);
        registriereFarbe("hellgr\u00fcn", Spiel.COLOR_HELLGRUEN);
        registriereFarbe("dunkelgruen", Spiel.COLOR_DUNKELGRUEN);
        registriereFarbe("dunkelgr\u00fcn", Spiel.COLOR_DUNKELGRUEN);
    }
}
