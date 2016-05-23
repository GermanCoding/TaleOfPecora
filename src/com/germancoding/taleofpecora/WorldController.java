package com.germancoding.taleofpecora;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WorldController extends InputAdapter {

	private static final String TAG = WorldController.class.getName();

	public Sprite[] testSprites;
	public int selectedSprite;
	public CameraHelper cameraHelper;

	public WorldController() {
		super();
		init();
	}

	private void init() {
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		initTestObjects();
	}

	private void initTestObjects() {
		Gdx.app.debug(TAG, "initTestObjects()");
		// Create new array for 6 sprites
		testSprites = new Sprite[7];
		Array<TextureRegion> regions = new Array<TextureRegion>();
		regions.add(Assets.instance.bush.one);
		regions.add(Assets.instance.crate.crate);
		regions.add(Assets.instance.mushroom.one);
		regions.add(Assets.instance.sign.one);
		regions.add(Assets.instance.stone.stone);
		regions.add(Assets.instance.tree.one);
		regions.add(Assets.instance.tile.one);
		// Create new sprites using the just created texture
		for (int i = 0; i < testSprites.length; i++) {
			Sprite spr = new Sprite(regions.random());
			// Define sprite size to be 1m x 1m in game world
			spr.setSize(1, 1);
			// Set origin to sprite's center
			spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
			// Calculate random position for sprite
			float randomX = MathUtils.random(-2.0f, 2.0f);
			float randomY = MathUtils.random(-2.0f, 2.0f);
			spr.setPosition(randomX, randomY);
			// Put new sprite into array
			testSprites[i] = spr;
		}
		// Set first sprite as selected one
		selectedSprite = 0;
	}

	public void update(float deltaTime) {
		handleDebugInput(deltaTime);
		updateTestObjects(deltaTime);
		cameraHelper.update(deltaTime);
	}

	private void handleDebugInput(float deltaTime) {
		// Selected Sprite Controls
		float sprMoveSpeed = 5 * deltaTime;
		if (Gdx.input.isKeyPressed(Keys.A))
			moveSelectedSprite(
					-sprMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.D))
			moveSelectedSprite(sprMoveSpeed, 0);
		if (Gdx.input.isKeyPressed(Keys.W))
			moveSelectedSprite(0,
					sprMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.S))
			moveSelectedSprite(0,
					-sprMoveSpeed);

		// Camera Controls (move)
		float camMoveSpeed = 5 * deltaTime;
		float camMoveSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camMoveSpeed *=
					camMoveSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			moveCamera(-camMoveSpeed,
					0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			moveCamera(camMoveSpeed,
					0);
		if (Gdx.input.isKeyPressed(Keys.UP))
			moveCamera(0, camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			moveCamera(0,
					-camMoveSpeed);
		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
			cameraHelper.setPosition(0, 0);
		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camZoomSpeed *=
					camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD))
			cameraHelper.addZoom(
					-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH))
			cameraHelper.setZoom(1);
	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	private void moveSelectedSprite(float x, float y) {
		testSprites[selectedSprite].translate(x, y);
	}

	private void updateTestObjects(float deltaTime) {
		// Get current rotation from selected sprite
		float rotation = testSprites[selectedSprite].getRotation();
		// Rotate sprite by 90 degrees per second
		rotation += 90 * deltaTime;
		// Wrap around at 360 degrees
		rotation %= 360;
		// Set new rotation value to selected sprite
		testSprites[selectedSprite].setRotation(rotation);
	}

	@Override
	public boolean keyUp(int keycode) {
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		// Select next sprite
		else if (keycode == Keys.SPACE) {
			selectedSprite = (selectedSprite + 1) % testSprites.length;
			// Update camera's target to follow the currently
			// selected sprite
			if (cameraHelper.hasTarget()) {
				cameraHelper.setTarget(testSprites[selectedSprite]);
			}
			Gdx.app.debug(TAG, "Sprite #" + selectedSprite + " selected");
		}
		// Toggle camera follow
		else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null :
					testSprites[selectedSprite]);
			Gdx.app.debug(TAG, "Camera follow enabled: " +
					cameraHelper.hasTarget());
		}
		return false;
	}

}
