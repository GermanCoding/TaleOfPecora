package com.germancoding.taleofpecora.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.stages.LevelFinishedStage;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

public class Sheep extends com.germancoding.taleofpecora.entity.LivingEntity implements IScript {

	private long deathCounter;
	private int carrotsCollected;
	private int enemiesKilled;
	private int starsCollected;
	private Runnable waterRunnable = new Runnable() {

		@Override
		public void run() {
			inWater = false;
		}
	};

	public Sheep(World world, TaleOfPecora game) {
		super(world, game);
	}

	public float getX() {
		return transform.x;
	}

	public float getY() {
		return transform.y;
	}

	@Override
	public void init(Entity entity) {
		Gdx.app.debug("[Sheep]", "init() called");
		transform = ComponentRetriever.get(entity, TransformComponent.class);
		dimension = ComponentRetriever.get(entity, DimensionsComponent.class);
		physics = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
		tint = ComponentRetriever.get(entity, TintComponent.class);
		baseLocation = (new Vector2(transform.x, transform.y));
		velocity = new Vector2(0, 0);
		item = ComponentRetriever.get(entity, MainItemComponent.class);
		this.entity = entity;
	}

	@Override
	public void act(float delta) {

		// Step 0: Preperations
		velocity.x = 0; // TODO: This is neccessary to stop movement, but also causes problems with objects where velocity is modified (like platforms)
		// Still no good solution for this. Putting this behind step 3 or 4 only causes more problems with raycast or other entities.
		
		if (firstFrame) { // Delta time might be very high on first frame
			firstFrame = false;
			delta = 0.001f;
		}

		// Step 1: Abnormal states (player died, level complete...)
		if (transform.y + dimension.height < 0 && !isDying) {
			Gdx.app.debug("[Sheep]", "player fell out of world");
			die();
		}

		if (getX() >= TaleOfPecora.instance.currentLevel.getLevelX() && !isDying) {
			// Player completed the level
			if (TaleOfPecora.instance.levelComplete == null) {
				TaleOfPecora.instance.levelComplete = new LevelFinishedStage(TaleOfPecora.instance.getSceneLoader().getRm());
			}
			TaleOfPecora.instance.renderMenu = true;
			return;
		}

		if (isDying) {
			/*
			 * --- old code = rotate the sheep ---
			 * if (transform.rotation > -90)
			 * transform.rotation -= 90 * delta;
			 * velocity.y = -(Constants.PLAYER_SPEED / 2);
			 * transform.y += velocity.y * delta;
			 */

			// New code (fade out, explosion)
			if (tint.color.a > 0) {
				Color color = tint.color.cpy();
				tint.color.set(color.r, color.g, color.b, color.a - 2f * delta); // Fade out
				if (tint.color.a < 0) {
					tint.color.a = 0;
				}
			}

			if (transform.y + dimension.height < 0 || (System.currentTimeMillis() - deathCounter) >= Constants.DEATH_TIME) {
				Gdx.app.debug("[Sheep]", "resetting game");
				TaleOfPecora.instance.reset();
			}
			return;
		}

		// Step 2: Control input
		if (Gdx.input.isKeyJustPressed(Constants.CONTROLS_UP)) {
			if (inWater) {
				velocity.y = Constants.PLAYER_WATERSPEED;
				// Schedule a task to remove the "inWater" flag.
				TaleOfPecora.instance.scheduler.runTask(waterRunnable, Constants.INWATER_FLAG);
			}
		}

		if (Gdx.input.isKeyPressed(Constants.CONTROLS_LEFT)) {
			velocity.x = -Constants.PLAYER_SPEED;
			turnLeft();
		} else if (Gdx.input.isKeyPressed(Constants.CONTROLS_RIGHT)) {
			velocity.x = Constants.PLAYER_SPEED;
			turnRight();
		} else {
			velocity.x = 0;
		}

		if (Gdx.input.isKeyPressed(Constants.CONTROLS_DOWN)) {
			if (!onGround) {
				velocity.y -= Constants.PLAYER_JUMP / 8;
			}
		}

		if (Gdx.input.isKeyJustPressed(Constants.CONTROLS_JUMP)) {
			if (onGround || jumpCounter < 2) {
				jumpCounter++;
				jump(Constants.PLAYER_JUMP);
			}
		}

		// DEBUG CONTROLS
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			transform.y += 5f * delta;
			velocity.y = 0;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			transform.x += 5f * delta;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			transform.x -= 5f * delta;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			transform.y -= 5f * delta;
			velocity.y = 0;
		}

		// rayCast(delta);

		// Step 3: movement
		move(delta);

		// Step 4: Collision
		rayCast(delta);
	}

	public float getWidth() {
		return dimension.width;
	}

	public TransformComponent getTransform() {
		return this.transform;
	}

	public void setTransform(TransformComponent transform) {
		this.transform = transform;
	}

	public DimensionsComponent getDimension() {
		return this.dimension;
	}

	public void setDimension(DimensionsComponent dimension) {
		this.dimension = dimension;
	}

	@Override
	public void dispose() {
		Gdx.app.debug("[Sheep]", "dispose() called");
		baseLocation = null;
		deathCounter = 0;
		dimension = null;
		entity = null;
		game = null;
		item = null;
		jumpCounter = 0;
		transform = null;
		velocity = null;
		world = null;
		delta = 0;
		carrotsCollected = 0;
	}

	public void die(boolean explode) {
		if (isDying)
			return;
		isDying = true;
		deathCounter = System.currentTimeMillis();
		if (explode)
			explode();
	}

	@Override
	public void die() {
		die(false);
	}

	public void explode() {
		ComponentMapper<TransformComponent> mapper = ComponentMapper.getFor(TransformComponent.class);
		final Entity explosion = TaleOfPecora.instance.loadAnimation("explosion", 60, PlayMode.NORMAL);
		TransformComponent eTransform = mapper.get(explosion);
		eTransform.x = getTransform().x;
		eTransform.y = getTransform().y - (getDimension().height * getTransform().scaleY) / 2;

		if (getTransform().scaleX < 0) {
			eTransform.scaleX *= -1;
		}

		TaleOfPecora.instance.scheduler.runTask(new Runnable() {

			@Override
			public void run() {
				TaleOfPecora.instance.getSceneLoader().getEngine().removeEntity(explosion);
			}
		}, 1500l);
	}

	public void addCarrot() {
		setCarrotsCollected(getCarrotsCollected() + 1);
	}

	public void addEnemy() {
		setEnemiesKilled(getEnemiesKilled() + 1);
	}

	public void addStar() {
		setStarsCollected(getStarsCollected() + 1);
	}

	public int getCarrotsCollected() {
		return carrotsCollected;
	}

	public void setCarrotsCollected(int carrotsCollected) {
		this.carrotsCollected = carrotsCollected;
	}

	public int getEnemiesKilled() {
		return enemiesKilled;
	}

	public void setEnemiesKilled(int enemiesKilled) {
		this.enemiesKilled = enemiesKilled;
	}

	public int getStarsCollected() {
		return starsCollected;
	}

	public void setStarsCollected(int starsCollected) {
		this.starsCollected = starsCollected;
	}

}
