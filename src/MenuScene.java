import ea.edu.Figur;

public class MenuScene  {

    public  Figur[] menuButton;
    public  Figur[] sub1Button;
    public  Figur[] sub2Button;
    public  Figur[] sub3Button;
    public  Figur[] sub4Button;
    public  Figur[] sub5Button;
    public  Figur[] sub6Button;
    boolean s1;
    boolean s2;
    boolean s3;
    boolean s4;
    boolean s5;
    boolean s6;

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
    public void subMenu1(){

        del();
        sub1Button = new Figur[1];

        sub1Button[0] = new Figur("button","rsc/play.gif");
        sub1Button[0].setzeMittelpunkt(-10,7);

        s1 = true;

    }
    public void subMenu2(){

        del();
        sub2Button = new Figur[1];

        sub2Button[0] = new Figur("button","rsc/play.gif");
        sub2Button[0].setzeMittelpunkt(-10,7);

        s2 = true;
    }
    public void subMenu3(){

        del();

        sub3Button = new Figur[1];

        sub3Button[0] = new Figur("button","rsc/play.gif");
        sub3Button[0].setzeMittelpunkt(-10,7);

        s3 = true;
    }
    public void subMenu4(){

        del();

        sub4Button = new Figur[1];

        sub4Button[0] = new Figur("button","rsc/play.gif");
        sub4Button[0].setzeMittelpunkt(-10,7);

        s4 = true;
    }
    public void subMenu5(){

        del();

        sub5Button = new Figur[1];

        sub5Button[0] = new Figur("button","rsc/play.gif");
        sub5Button[0].setzeMittelpunkt(-10,7);


        s5 = true;
    }
    public void subMenu6(){

        del();

        sub6Button = new Figur[1];

        sub6Button[0] = new Figur("button","rsc/play.gif");
        sub6Button[0].setzeMittelpunkt(-10,7);

        s6 = true;
    }
    public void del(){

        //löscht alle anderen aktiven sub meus
        if (s1)sub1Button = null;
        if (s2)sub2Button = null;
        if (s3)sub3Button = null;
        if (s4)sub4Button = null;
        if (s5)sub5Button = null;
        if (s6)sub6Button = null;

    }


}
