package a3;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import tage.*;

public class PitchAction extends AbstractInputAction {
    private MyGame game;
    private Camera cam;
    private GameObject av;
    private float speed = 1.0f;

    public PitchAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e) {
        cam = (game.getEngine().getRenderSystem().getViewport("LEFT").getCamera());
        float keyValue = e.getValue();
            if (keyValue > -.2 && keyValue < .2)
                return; // deadzone

            float direction = (e.getComponent().getIdentifier().getName() == "Up") ? 1.0f : -1.0f;

            av = game.getAvatar();
            av.pitch(keyValue * direction * speed * time);
            cam.pitch(direction * speed * time);
    }
}
