package a3;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import tage.*;

public class RollAction extends AbstractInputAction {
    private MyGame game;
    private GameObject av;
    private float speed = 1.0f;

    public RollAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e) {
        float keyValue = e.getValue();

        if (keyValue > -.2 && keyValue < .2)
            return; // deadzone

        float direction = (e.getComponent().getIdentifier().getName() == "Right") ? 1.0f : -1.0f;

        av = game.getAvatar();
        av.roll(keyValue * direction * speed * time);
    }
}
