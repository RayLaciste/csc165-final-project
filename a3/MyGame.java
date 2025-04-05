package a3;

import tage.*;
import tage.shapes.*;

import java.util.*;
import java.util.List;
import java.lang.Math;
import java.text.DecimalFormat;
import java.awt.event.*;

import org.joml.*;

import tage.input.*;
import net.java.games.input.*;
import net.java.games.input.Component.Identifier;

import tage.nodeControllers.*;

public class MyGame extends VariableFrameRateGame {

	private CameraOrbit3D orbitController;

	private RotationController rc, rc2, rc3;
	private BobController bc, bc2, bc3;

	private static Engine engine;
	private InputManager im;

	private double lastFrameTime, currFrameTime, elapsTime;

	private GameObject avatar, sat1, sat2, sat3, terr,
			x, y, z,
			ground, elec1, elec2, elec3;


	// Robot
	private GameObject robot;
	private ObjShape robS;
	private TextureImage robottx;
	private float robotHeightAdjust = 0.5f;

	private ObjShape avatarS, cubS, torS, spherS, linxS, linyS, linzS, planeS, terrS;

	private TextureImage avatartx, metal, water, boom, electronic, hills, grass,
			far1, close1, defuse1,
			far2, close2, defuse2,
			far3, close3, defuse3;

	private Light light1, redLight, greenLight, blueLight;

	private int fluffyClouds, lakeIslands; // skyboxes

	private boolean lose = false;

	private String dynamicText = "Test";

	List<GameObject> axes = Arrays.asList(x, y, z);
	List<GameObject> satellites = Arrays.asList(sat1, sat2, sat3);
	Set<GameObject> disarmedSatellites = new HashSet<>();

	public MyGame() {
		super();
	}

	void checkCollision(GameObject avatar, GameObject satellite, TextureImage farTexture, TextureImage closeTexture,
			TextureImage defuseTexture) {
		Vector3f avatarLoc = avatar.getWorldLocation();
		Vector3f satLoc = satellite.getWorldLocation();

		float avatarScale = avatar.getLocalScale().get(0, 0);
		float satScale = satellite.getLocalScale().get(0, 0);

		float dynamicHitBox = (avatarScale + satScale);

		if (avatarLoc.distance(satLoc) < dynamicHitBox &&
				!disarmedSatellites.contains(satellite)) {
			// Collision: Satellite is too close (explode)
			satellite.setTextureImage(boom);
			lose = true;
			dynamicText = "You Lose!";
		} else if (avatarLoc.distance(satLoc) < dynamicHitBox * 1.5f &&
				!disarmedSatellites.contains(satellite)) {
			// Close enough
			if (satellite.getTextureImage() != defuseTexture) {
				satellite.setTextureImage(closeTexture);
				dynamicText = "Close Enough";
			}
		} else if (avatarLoc.distance(satLoc) >= dynamicHitBox * 1.5f &&
				!disarmedSatellites.contains(satellite)) {
			// Far away
			satellite.setTextureImage(farTexture);
		}
	}

	void defuseSatellite(GameObject satellite) {
		if (!disarmedSatellites.contains(satellite)) {
			if (satellite == sat1) {
				satellite.setTextureImage(defuse1);
				rc.setSpeed(0.001f);
				rc.enable();
				bc.enable();
				elec1.setParent(avatar);
				elec1.setLocalScale(new Matrix4f().scaling(.05f));
				elec1.setLocalTranslation((new Matrix4f()).translation(-.1f, .1f, -.1f));
			} else if (satellite == sat2) {
				satellite.setTextureImage(defuse2);
				rc2.setSpeed(0.001f);
				rc2.enable();
				bc2.enable();
				elec2.setParent(avatar);
				elec2.setLocalScale(new Matrix4f().scaling(.05f));
				elec2.setLocalTranslation((new Matrix4f()).translation(.1f, .1f, -.1f));
			} else if (satellite == sat3) {
				satellite.setTextureImage(defuse3);
				rc3.setSpeed(0.001f);
				rc3.enable();
				bc3.enable();
				elec3.setParent(avatar);
				elec3.setLocalScale(new Matrix4f().scaling(.05f));
				elec3.setLocalTranslation((new Matrix4f()).translation(0, .1f, -.2f));
			}
			disarmedSatellites.add(satellite);
			dynamicText = "Satellite Disarmed";
		}
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes() {
		avatarS = new ImportedModel("dolphinHighPoly.obj");
		cubS = new Cube();
		torS = new Torus();
		spherS = new Sphere();
		planeS = new Plane();

		terrS = new TerrainPlane(1000);

		// Robot
		robS = new ImportedModel("robot.obj");

		linxS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(3f, 0f, 0f));
		linyS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 3f, 0f));
		linzS = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, -3f));
	}

	@Override
	public void loadTextures() {
		// avatar
		avatartx = new TextureImage("Dolphin_HighPolyUV.png");

		// robot
		robottx = new TextureImage("robot.png");

		// terrain
		hills = new TextureImage("hills.jpg");

		// grass
		grass = new TextureImage("grass.jpg");

		// boom
		boom = new TextureImage("boom.jpg");

		// ground
		water = new TextureImage("water.jpg");

		// child objects - electronics
		electronic = new TextureImage("electronic.jpg");

		// satellite1
		far1 = new TextureImage("far1.png");
		close1 = new TextureImage("close1.png");
		defuse1 = new TextureImage("defuse1.png");

		// satellite2
		far2 = new TextureImage("far2.jpg");
		close2 = new TextureImage("close2.jpg");
		defuse2 = new TextureImage("defuse2.jpg");

		// satellite3
		far3 = new TextureImage("far3.png");
		close3 = new TextureImage("close3.png");
		defuse3 = new TextureImage("defuse3.png");
	}

	@Override
	public void loadSkyBoxes()
	{
		fluffyClouds = (engine.getSceneGraph()).loadCubeMap("fluffyClouds");
		lakeIslands = (engine.getSceneGraph()).loadCubeMap("lakeIslands");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(fluffyClouds);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	float randomFloat(float min, float max) {
		return min + (float) Math.random() * (max - min);
	}

	// Randomizes location for satellites
	private Vector3f regenerateTranslation(float minX, float maxX, float minY, float maxY, float minZ, float maxZ,
			float safeZone) {
		Vector3f position;
		do {
			position = new Vector3f(
					randomFloat(minX, maxX),
					randomFloat(minY, maxY),
					randomFloat(minZ, maxZ));
		} while (position.length() < safeZone); // Regenerate if inside safeZone
		return position;
	}

	@Override
	public void buildObjects() {
		Matrix4f initialTranslation, initialScale, initialRotation;

		float minX = -8.0f, maxX = 8.0f;
		float minY = .5f, maxY = .5f;
		float minZ = -8.0f, maxZ = 8.0f;
		float minScale = 0.5f, maxScale = .5f;
		float safeZone = 3.0f;

		// build dolphin in the center of the window
		avatar = new GameObject(GameObject.root(), avatarS, avatartx);
		initialTranslation = (new Matrix4f()).translation(-1.0f, 0.5f, 1.0f);
		initialScale = (new Matrix4f()).scaling(.75f);
		avatar.setLocalTranslation(initialTranslation);
		initialRotation = (new Matrix4f()).rotationY(
				(float) java.lang.Math.toRadians(135.0f));
		avatar.setLocalRotation(initialRotation);
		avatar.setLocalScale(initialScale);

		// Robot
		robot = new GameObject(GameObject.root(), robS, robottx);
		initialTranslation = (new Matrix4f()).translation(0, robotHeightAdjust, 0);
		robot.setLocalTranslation(initialTranslation);
		initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(180.0f));
		robot.setLocalRotation(initialRotation);
		initialScale = (new Matrix4f()).scaling(0.2f, 0.2f, 0.2f);
		robot.setLocalScale(initialScale);
		robot.getRenderStates().setModelOrientationCorrection(
				(new Matrix4f()).rotationY((float)java.lang.Math.toRadians(90.0f)));

//		robot.getRenderStates().hasLighting(true);
//		robot.getRenderStates().isEnvironmentMapped(true);

		// Terrain
		terr = new GameObject(GameObject.root(), terrS, grass);
		initialTranslation = (new Matrix4f()).translation(0f, 0f, 0f);
		terr.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(20.0f, 1.0f, 20.0f);
		terr.setLocalScale(initialScale);
		terr.setHeightMap(hills);
		terr.getRenderStates().setTiling(1);
		terr.getRenderStates().setTileFactor(10);

		sat1 = new GameObject(GameObject.root(), cubS, far1);
		initialTranslation = (new Matrix4f()).translation(
				regenerateTranslation(minX, maxX, minY, maxY, minZ, maxZ, safeZone));
		initialScale = (new Matrix4f()).scaling(randomFloat(minScale, maxScale));
		sat1.setLocalTranslation(initialTranslation);
		sat1.setLocalScale(initialScale);

		sat2 = new GameObject(GameObject.root(), spherS, metal);
		initialTranslation = (new Matrix4f()).translation(
				regenerateTranslation(minX, maxX, minY, maxY, minZ, maxZ, safeZone));
		initialScale = (new Matrix4f()).scaling(randomFloat(minScale, maxScale));
		sat2.setLocalTranslation(initialTranslation);
		sat2.setLocalScale(initialScale);

		sat3 = new GameObject(GameObject.root(), torS, metal);
		initialTranslation = (new Matrix4f()).translation(
				regenerateTranslation(minX, maxX, minY, maxY, minZ, maxZ, safeZone));
		initialScale = (new Matrix4f()).scaling(randomFloat(minScale, maxScale));
		sat3.setLocalTranslation(initialTranslation);
		sat3.setLocalScale(initialScale);

		// Ground Plane
		ground = new GameObject(GameObject.root(), planeS, water);
		initialTranslation = (new Matrix4f()).translation(0, -1.0f, 0);
		initialScale = (new Matrix4f()).scaling(10);
		ground.setLocalTranslation(initialTranslation);
		ground.setLocalScale(initialScale);
		ground.getRenderStates().setTiling(1);

		// Electronic child objects
		elec1 = new GameObject(sat1, cubS, electronic);
		initialTranslation = (new Matrix4f()).translation(0, .5f, 0);
		initialScale = (new Matrix4f()).scaling(.25f);
		elec1.setLocalTranslation(initialTranslation);
		elec1.setLocalScale(initialScale);
		elec1.propagateTranslation(true);
		elec1.propagateRotation(true);
		elec1.applyParentRotationToPosition(true);

		elec2 = new GameObject(sat2, cubS, electronic);
		initialTranslation = (new Matrix4f()).translation(0, .5f, 0);
		initialScale = (new Matrix4f()).scaling(.25f);
		elec2.setLocalTranslation(initialTranslation);
		elec2.setLocalScale(initialScale);
		elec2.propagateTranslation(true);
		elec2.propagateRotation(true);
		elec2.applyParentRotationToPosition(true);

		elec3 = new GameObject(sat3, cubS, electronic);
		initialTranslation = (new Matrix4f()).translation(0, .15f, 0);
		initialScale = (new Matrix4f()).scaling(.25f);
		elec3.setLocalTranslation(initialTranslation);
		elec3.setLocalScale(initialScale);
		elec3.propagateTranslation(true);
		elec3.propagateRotation(true);
		elec3.applyParentRotationToPosition(true);

		// place satellites into a list
		satellites = Arrays.asList(sat1, sat2, sat3);

		// axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));
		(y.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));
		(z.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));

	}

	@Override
	public void initializeLights() {
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		(engine.getSceneGraph()).addLight(light1);

		redLight = new Light();
		redLight.setType(Light.LightType.SPOTLIGHT);
		redLight.setAmbient(1.0f, 0.0f, 0.0f);
		redLight.setDiffuse(1.0f, 0.0f, 0.0f);
		redLight.setSpecular(1.0f, 0.0f, 0.0f);
		redLight.setLocation(sat1.getWorldLocation().add(sat1.getLocalUpVector().mul(1.5f)));
		(engine.getSceneGraph()).addLight(redLight);

		blueLight = new Light();
		blueLight.setType(Light.LightType.SPOTLIGHT);
		blueLight.setAmbient(0.0f, 0.0f, 1.0f);
		blueLight.setDiffuse(0.0f, 0.0f, 1.0f);
		blueLight.setSpecular(0.0f, 0.0f, 1.0f);
		blueLight.setLocation(sat2.getWorldLocation().add(sat2.getLocalUpVector().mul(1.5f)));
		(engine.getSceneGraph()).addLight(blueLight);

		greenLight = new Light();
		greenLight.setType(Light.LightType.SPOTLIGHT);
		greenLight.setAmbient(0.0f, 1.0f, 0.0f);
		greenLight.setDiffuse(0.0f, 1.0f, 0.0f);
		greenLight.setSpecular(0.0f, 1.0f, 0.0f);
		greenLight.setLocation(sat3.getWorldLocation().add(sat3.getLocalUpVector().mul(1.5f)));
		(engine.getSceneGraph()).addLight(greenLight);
	}

	@Override
	public void createViewports() {
		(engine.getRenderSystem()).addViewport("LEFT", 0, 0, 1f, 1f);
		(engine.getRenderSystem()).addViewport("RIGHT", .75f, 0, .25f, .25f);

		Viewport leftVp = (engine.getRenderSystem()).getViewport("LEFT");
		Viewport rightVp = (engine.getRenderSystem()).getViewport("RIGHT");
		Camera leftCamera = leftVp.getCamera();
		Camera rightCamera = rightVp.getCamera();

		rightVp.setHasBorder(true);
		rightVp.setBorderWidth(4);
		rightVp.setBorderColor(0.0f, 1.0f, 0.0f);

		leftCamera.setLocation(new Vector3f(-2, 0, 2));
		leftCamera.setU(new Vector3f(1, 0, 0));
		leftCamera.setV(new Vector3f(0, 1, 0));
		leftCamera.setN(new Vector3f(0, 0, -1));

		rightCamera.setLocation(new Vector3f(0, 2, 0));
		rightCamera.setU(new Vector3f(1, 0, 0));
		rightCamera.setV(new Vector3f(0, 0, -1));
		rightCamera.setN(new Vector3f(0, -1, 0));
	}

	@Override
	public void initializeGame() {
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		(engine.getRenderSystem()).setWindowDimensions(1900, 1000);

		rc = new RotationController(engine, new Vector3f(0, 1, 0), 0.001f);
		rc2 = new RotationController(engine, new Vector3f(0, 1, 0), 0.001f);
		rc3 = new RotationController(engine, new Vector3f(0, 1, 0), 0.001f);

		rc.addTarget(sat1);
		(engine.getSceneGraph()).addNodeController(rc);
		rc2.addTarget(sat2);
		(engine.getSceneGraph()).addNodeController(rc2);
		rc3.addTarget(sat3);
		(engine.getSceneGraph()).addNodeController(rc3);

		bc = new BobController();
		bc2 = new BobController();
		bc3 = new BobController();

		bc.addTarget(sat1);
		(engine.getSceneGraph()).addNodeController(bc);
		bc2.addTarget(sat2);
		(engine.getSceneGraph()).addNodeController(bc2);
		bc3.addTarget(sat3);
		(engine.getSceneGraph()).addNodeController(bc3);

		im = engine.getInputManager();

		// Identify and Select Gamepad
		String gpName = im.getFirstGamepadName();
		System.out.println("\u001B[31m" + gpName + "\u001B[0m");

		// Orbit Camera
		Camera c = (engine.getRenderSystem().getViewport("LEFT").getCamera());
		orbitController = new CameraOrbit3D(c, avatar, gpName, engine);

		// Actions
		FwdAction fwdAction = new FwdAction(this);
		TurnAction turnAction = new TurnAction(this);
		PanAction panAction = new PanAction(this);
		ZoomAction zoomAction = new ZoomAction(this);
		DefuseAction defuseAction = new DefuseAction(this);
		ToggleAxes toggleAxes = new ToggleAxes(this);
		PitchAction pitchAction = new PitchAction(this);
		RollAction rollAction = new RollAction(this);

		// Gamepad input for turning and moving
		im.associateAction(gpName,
				net.java.games.input.Component.Identifier.Axis.X, turnAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName,
				net.java.games.input.Component.Identifier.Axis.Y, fwdAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// Gamepad Inputs for Actions
		im.associateAction(gpName,
				net.java.games.input.Component.Identifier.Button._1, defuseAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(gpName,
				net.java.games.input.Component.Identifier.Button._3, toggleAxes,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// Keyboard Inputs for movement
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.W, fwdAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.S, fwdAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.A, turnAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.D, turnAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.D, turnAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// im.associateActionWithAllKeyboards(
		// 		net.java.games.input.Component.Identifier.Key.UP, pitchAction,
		// 		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// im.associateActionWithAllKeyboards(
		// 		net.java.games.input.Component.Identifier.Key.DOWN, pitchAction,
		// 		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// im.associateActionWithAllKeyboards(
		// 		net.java.games.input.Component.Identifier.Key.LEFT, rollAction,
		// 		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// im.associateActionWithAllKeyboards(
		// 		net.java.games.input.Component.Identifier.Key.RIGHT, rollAction,
		// 		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// Keyboard Inputs for actions
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.SPACE, defuseAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.R, toggleAxes,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// Test Inputs for Right Viewport
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.J, panAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.I, panAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.L, panAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.K, panAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.U, zoomAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(
				net.java.games.input.Component.Identifier.Key.O, zoomAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	}

	@Override
	public void update() { // rotate dolphin if not paused
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime = (currFrameTime - lastFrameTime) / 1000.0;

		// build and set HUDs
		String counterStr = Integer.toString(disarmedSatellites.size());
		String scoreString = "Score: " + counterStr;

		// Viewport 2 Text
		float[] avLoc = { avatar.getWorldLocation().x(), avatar.getWorldLocation().y(), avatar.getWorldLocation().z() };
		DecimalFormat df = new DecimalFormat("#.##");
		String avLocString = "X: " + df.format(avLoc[0]) + " Y: " + df.format(avLoc[1]) + " Z: " + df.format(avLoc[2]);

		Vector3f hud1Color = new Vector3f(0, 0, 0);
		Vector3f hud2Color = new Vector3f(1, 0, 1);
		Vector3f hud3Color = new Vector3f(1, 1, 0);
		Vector3f hud4Color = new Vector3f(1, 1, 1);

		Viewport viewportLeft = engine.getRenderSystem().getViewport("LEFT");
		Viewport viewportRight = engine.getRenderSystem().getViewport("RIGHT");

		float viewportRW = viewportRight.getActualWidth();
		float viewportRL = viewportRight.getActualLeft();
		float centerRX = (viewportRL + (viewportRW / 4));

		float viewportLW = viewportLeft.getActualWidth();
		float viewportLL = viewportLeft.getActualLeft();
		float centerLX = (viewportLL + (viewportLW / 3));

		(engine.getHUDmanager()).setHUD1(avLocString, hud1Color, (int) centerRX, 15);
		(engine.getHUDmanager()).setHUD4(avLocString, hud4Color, (int) centerRX - 2, 17);
		(engine.getHUDmanager()).setHUD2(dynamicText, hud2Color, (int) centerLX, 15);
		(engine.getHUDmanager()).setHUD3(scoreString, hud3Color, (int) centerLX, 50);

		if (lose) {
			dynamicText = "You Lose!";
			return;
		} else if (disarmedSatellites.size() == 3) {
			dynamicText = "Victory!";
			return;
		}

		im.update((float) elapsTime);

		orbitController.updateCameraPosition();

		// update altitude of dolphin based on height map
		Vector3f loc = avatar.getWorldLocation();
		float height = terr.getHeight(loc.x(), loc.z());
		avatar.setLocalLocation(new Vector3f(loc.x(), height, loc.z()));

		Vector3f loc2 = robot.getWorldLocation();
		float height2 = terr.getHeight(loc2.x(), loc2.z());
		robot.setLocalLocation(
				new Vector3f(loc2.x(), height2 + robotHeightAdjust, loc2.z()));

		// // Check collisions
		checkCollision(avatar, sat1, far1, close1, defuse1);
		checkCollision(avatar, sat2, far2, close2, defuse2);
		checkCollision(avatar, sat3, far3, close3, defuse3);

	}

	public GameObject getAvatar() {
		return avatar;
	}

	public Engine getEngine() {
		return engine;
	}

	public List<GameObject> getSatellites() {
		return satellites;
	}

	public List<GameObject> getAxes() {
		return axes;
	}

	public Set<GameObject> getDisarmedSatellites() {
		return disarmedSatellites;
	}

	public void renderAxes() {
		x.getRenderStates().disableRendering();
		y.getRenderStates().disableRendering();
		z.getRenderStates().disableRendering();
	}

	// Test methods
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_1:
			{
				(engine.getSceneGraph()).setActiveSkyBoxTexture(fluffyClouds);
				(engine.getSceneGraph()).setSkyBoxEnabled(true);
				break;
			}
			case KeyEvent.VK_2:
			{
				(engine.getSceneGraph()).setActiveSkyBoxTexture(lakeIslands);
				(engine.getSceneGraph()).setSkyBoxEnabled(true);
				break;
			}
			case KeyEvent.VK_3:
			{
				(engine.getSceneGraph()).setSkyBoxEnabled(false);
				break;
			}

		}
		super.keyPressed(e);
	}
}