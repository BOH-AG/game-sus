import ea.edu.Figur;

public class MenuScene  {

    public  Figur[] menuButton;
    public MenuScene() {
        menuButton = new Figur[8];

       menuButton[0] = new Figur("button", "rsc/play.gif");
       menuButton[0].setzeMittelpunkt(-18,10);
       menuButton[1] = new Figur("button", "rsc/play.gif");
       menuButton[1].setzeMittelpunkt(-18,7);
       menuButton[2] = new Figur("button", "rsc/play.gif");
       menuButton[2].setzeMittelpunkt(-18,4);
       menuButton[3] = new Figur("button", "rsc/play.gif");
       menuButton[3].setzeMittelpunkt(-18,1);
       menuButton[4] = new Figur("button", "rsc/play.gif");
       menuButton[4].setzeMittelpunkt(-18,-2);
       menuButton[5] = new Figur("button", "rsc/play.gif");
       menuButton[5].setzeMittelpunkt(-18,-5);
       menuButton[6] = new Figur("button", "rsc/play.gif");
       menuButton[6].setzeMittelpunkt(-18,-8);
       menuButton[7] = new Figur("button", "rsc/play.gif");
       menuButton[7].setzeMittelpunkt(-18,-11);


    }
}
