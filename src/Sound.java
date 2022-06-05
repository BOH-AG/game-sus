import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class Sound {
    public void sound(){
        try{
            FileInputStream fis = new FileInputStream("rsc/music/DIE FTZN SIND WIEDER DA (Official Video)_SXTN - Emil.mp3");
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
