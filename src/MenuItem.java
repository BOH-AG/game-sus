/*
import  ea.*;
import ea.actor.Rectangle;
import ea.actor.Text;
import ea.animation.CircleAnimation;
import ea.event.KeyListener;
import ea.event.MouseButton;

import ea.event.MouseClickListener;
import ea.Scene;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class MenuItem
            extends Rectangle
            implements MouseClickListener, FrameUpdateListener{

        private Text label;


    public MenuItem(Vector center, String labelText) {
            super(10, 1.5f);

            label = new Text(labelText, 1);
            label.setLayerPosition(1);
            label.setColor(Color.BLACK);
            label.setCenter(center);

            setLayerPosition(0);
            setColor(Color.cyan);
            setCenter(center);
        }

        @Override
        public void onMouseDown(Vector clickLoc, MouseButton mouseButton) {
            if(contains(clickLoc)) {

                Game.transitionToScene(gameScene);
            }
        }

        @Override
        public void onFrameUpdate(float v) {
            if(contains(Game.getMousePositionInCurrentScene())) {
                this.setColor(Color.MAGENTA);
            } else {
                this.setColor(Color.CYAN);
            }
        }
    }
*/

