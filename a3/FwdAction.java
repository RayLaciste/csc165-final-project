package a3;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

import tage.*;

public class FwdAction extends AbstractInputAction {
    private MyGame game;
    private GameObject av;
    private Camera cam;
    private Vector3f oldPosition, newPosition, fwd;
    private Vector4f fwdDirection;
    private float speed = 2.0f;

    public FwdAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e) {
        cam = (game.getEngine().getRenderSystem().getViewport("LEFT").getCamera());
        float keyValue = e.getValue();

        if (keyValue > -.2 && keyValue < .2)
            return; // deadzone

        float direction = (e.getComponent().getIdentifier()
                .getName() == "W") ? 1.0f : -1.0f;

        av = game.getAvatar();
        oldPosition = av.getWorldLocation();
        fwdDirection = new Vector4f(0f, 0f, 1f, 1f);
        fwdDirection.mul(av.getWorldRotation());

        fwdDirection.mul(keyValue * direction * speed * time);
        newPosition = oldPosition.add(fwdDirection.x(),
                fwdDirection.y(), fwdDirection.z());
        av.setLocalLocation(newPosition);

        oldPosition = cam.getLocation();
        fwd = cam.getN();

        fwd.mul(keyValue * direction * speed * time);
        newPosition = oldPosition.add(fwd.x(), fwd.y(), fwd.z());
        cam.setLocation(newPosition);

    }
}