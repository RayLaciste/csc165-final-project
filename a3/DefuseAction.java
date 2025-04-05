package a3;

import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import java.util.List;

import org.joml.*;

import tage.*;

public class DefuseAction extends AbstractInputAction {
    private MyGame game;
    private GameObject av;
    private float satScale, avScale, dynamicHitBox;
    private Vector3f satLoc, avLoc;

    public DefuseAction(MyGame g) {
        game = g;
    }

    @Override
    public void performAction(float time, Event e) {
        List<GameObject> satellites = game.getSatellites();
        av = game.getAvatar();

        for (GameObject sat : satellites) {
            satLoc = sat.getWorldLocation();
            avLoc = av.getWorldLocation();

            satScale = sat.getLocalScale().get(0, 0);
            avScale = av.getLocalScale().get(0, 0);

            dynamicHitBox = (avScale + satScale);

            if (avLoc.distance(satLoc) < dynamicHitBox * 1.5f) {
                // Defuse the satellite if close enough
                game.defuseSatellite(sat);
                break; // Exit loop after defusing one satellite
            }
        }
    }
}
