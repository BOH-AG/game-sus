import ea.Scene;
import ea.Game;


public class game extends Scene{
    public game() {
        player p1 = new player();
        p1.setCenter(0,1);
        this.add(p1);
        Game.setDebug(true);
    }
}


