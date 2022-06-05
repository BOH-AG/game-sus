import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class Sound {
    public void sound(){
        try{
            FileInputStream fis = new FileInputStream("rsc/music/14 - Ghostbusters (_Ghostbusters_) - Parker, Ray Jr._Ray Parker Jr. - Ghostbusters (Original Motion Picture Soundtrack).mp3");
            Player playMP3 = new Player(fis);
            playMP3.play();
            System.out.println("d");
        }
        catch(Exception exc){
            exc.printStackTrace();
            System.out.println("Failed to play the file.");
        }
    }
}
