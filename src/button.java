

import ea.FrameUpdateListener;
import ea.edu.Bild;
import ea.edu.Text;
import ea.edu.event.MausKlickReagierbar;

public class button extends Bild {

    Text text;

    public button(double x, double y, String txt) {
        super(2f, 2f, "rsc/green-empty.png");
        this.getActor().resetPixelPerMeter(30);
        setzeMittelpunkt(x, y);
        text = new Text(txt, 1.3);
        text.setzeMittelpunkt(x, y+0.02);
        text.setzeFarbe("schwarz");
    }

    public button(double x, double y, String txt, String color) {
        super(2f, 2f, getPath(color));
        this.getActor().resetPixelPerMeter(30);
        setzeMittelpunkt(x, y);
        text = new Text(txt, 1.3);
        text.setzeMittelpunkt(x, y+0.02);
        text.setzeFarbe("schwarz");
    }

    public void changeText(String ntxt) {
        text.setzeInhalt(ntxt);
    }

    private static String getPath(String color) {
        return switch (color) {
            case "red" -> "rsc/red-empty.png";
            case "blue" -> "rsc/blue-empty.png";
            default -> "rsc/green-empty.png";
        };
    }

}
