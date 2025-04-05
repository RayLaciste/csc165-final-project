package a3;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import tage.*;

public class PanAction extends AbstractInputAction {
    private MyGame game;
    private Camera cam;
    private float speed = 1.0f;

    public PanAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e) {
        cam = (game.getEngine().getRenderSystem().getViewport("RIGHT").getCamera()); // Change to correct viewport if needed
        float keyValue = e.getValue();

        if (keyValue > -.2 && keyValue < .2)
            return; // deadzone

        String inputComponent = e.getComponent().getIdentifier().getName();
        float horizontalPan = 0.0f;
        float verticalPan = 0.0f;

        switch (inputComponent) {
            case "J":
                horizontalPan = -1.0f;
                break;
            case "L":
                horizontalPan = 1.0f;
                break;
            case "I":
                verticalPan = 1.0f;
                break;
            case "K":
                verticalPan = -1.0f;
                break;
            default:
                return;
        }

        cam.pan(horizontalPan * speed * time, verticalPan * speed * time);
    }
}