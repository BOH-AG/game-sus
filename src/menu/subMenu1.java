package menu;

import ea.edu.Figur;
import ea.edu.Spiel;


import java.util.Arrays;

public class subMenu1  extends Spiel{

    public Figur[] subm1button;

    public void subm1(){



        subm1button = new Figur[6];

        subm1button[1] = new Figur("button","rsc/play.gif");
        subm1button[1].setzeMittelpunkt(-10,7);

    }
}
