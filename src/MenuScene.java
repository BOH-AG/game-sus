

public class MenuScene  {

    public  button[] menuButton;
    public  button[] sub1Button;
    public  button[] sub2Button;
    public  button[] sub3Button;
    public  button[] sub4Button;
    public  button[] sub5Button;
    public  button[] sub6Button;
    boolean s1;
    boolean s2;
    boolean s3;
    boolean s4;
    boolean s5;
    boolean s6;


    public MenuScene() {

        menuButton = new button[8];

       menuButton[0] = new button(-18,10, "Game");
       menuButton[1] = new button(-18,7, "Sound");
       menuButton[2] = new button(-18,4, "Baby Mode");
       menuButton[3] = new button(-18,1, "void");
       menuButton[4] = new button(-18,-2, "Graphics");
       menuButton[5] = new button(-18,-5, "Difficulty");
       menuButton[6] = new button(-18,-8, "Support Us");
       menuButton[7] = new button(-18,-11, "TitleScreen");



    }
    public void subMenu1(){


            del();
            sub1Button = new button[2];

            sub1Button[0] = new button(-10, 3, "Disable", "red");
            sub1Button[1] = new button(-10, 5, "Enable", "green");

            s1 = true;

    }
    public void subMenu2(){

        del();
        sub2Button = new button[2];

            sub2Button[0] = new button(-10,7,"OFF","red");
            sub2Button[1] = new button(-10,5,"ON");


        s2 = true;
    }
    public void subMenu3(){

        del();

        sub3Button = new button[1];

        sub3Button[0] = new button(-10,7,"rsc/play.gif3");

        s3 = true;
    }
    public void subMenu4(){

        del();

        sub4Button = new button[1];

        sub4Button[0] = new button(-10,7,"rsc/play.gif4");

        s4 = true;
    }
    public void subMenu5(){

        del();

        sub5Button = new button[1];

        sub5Button[0] = new button(-10,7,"rsc/play.gif5");


        s5 = true;
    }
    public void subMenu6(){

        del();

        sub6Button = new button[1];

        sub6Button[0] = new button(-10,7,"6");

        s6 = true;
    }
    public void del(){

        //löscht alle anderen aktiven sub meus

    }


}