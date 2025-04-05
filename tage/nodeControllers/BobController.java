package tage.nodeControllers;

import tage.*;
import org.joml.*;

/**
 * A BobController is a node controller that, when enabled, causes any
 * object
 * it is attached to bob in place around above a specified or default minimum y position.
 * 
 * @author Scott Gordon
 */
public class BobController extends NodeController {
	private float speed = .001f;
	private float bobAmt = .75f;
	private float direction = 1.0f;
	private float minY = 0.5f;
	private float maxY = 1.0f;
	private Vector3f currentPosition;

	/** Creates a rotation controller with default speed, bobbing amount, Min Y and Max Y */
	public BobController() { super(); }

	/** Creates a bob controller with speed, bobbing amount, min y and max y as specified. */
	public BobController(Engine e, float speed, float bobAmt, float minY, float maxY) {
		super();
		this.speed = speed;
		this.bobAmt = bobAmt;
		this.maxY = maxY;
	}

	/** sets the bobbing speed when the controller is enabled */
	public void setSpeed(float speed) { this.speed = speed; }

	/** Sets the base amount of bobbing. */
	public void setBobAmt(float bobAmt) { this.bobAmt = bobAmt; }

	/**
	 * This is called automatically by the RenderSystem (via SceneGraph) once per
	 * frame
	 * during display(). It is for engine use and should not be called by the
	 * application.
	 */
	public void apply(GameObject go) {
		float elapsedTime = super.getElapsedTime();
		currentPosition = go.getWorldLocation();
		float scaledBobAmt = bobAmt * go.getLocalScale().m00();
		float newY = currentPosition.y() + direction * scaledBobAmt * speed * elapsedTime;
		if (newY < minY) {
			newY = minY;
			direction *= -1;
		} else if (newY > maxY) {
			newY = maxY;
			direction *= -1;
		}
		Vector3f newPosition = new Vector3f(currentPosition.x(), newY, currentPosition.z());
		go.setLocalLocation(newPosition);
	}
}