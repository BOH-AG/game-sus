
import ea.edu.Text;

import java.awt.*;

public class MenuItem extends Rectangle {
    private Text lable;
    public MenuItem(double x , double y, String lableText){

        Text lable = new Text(lableText, 1);
        lable.setzeSichtbar(true);
        lable.setzeMittelpunkt(x, y);
        System.out.println("f");

    }


}





