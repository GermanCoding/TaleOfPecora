package com.germancoding.taleofpecora.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.Utils;
import com.uwsoft.editor.renderer.components.CompositeTransformComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TintComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;

public abstract class LivingEntity {

	protected com.badlogic.ashley.core.Entity entity;
	protected int jumpCounter;
	protected Vector2 baseLocation;
	protected TransformComponent transform;
	protected DimensionsComponent dimension;
	protected CompositeTransformComponent cTransform;
	protected PhysicsBodyComponent physics;
	protected MainItemComponent item;
	protected TintComponent tint;
	protected World world;
	protected TaleOfPecora game;
	protected boolean onGround = true;
	protected Vector2 velocity;
	protected boolean isPlayer;
	protected boolean isDying;
	protected volatile float delta;
	public boolean firstFrame = true;
	public boolean applyGravity = true;
	public boolean inWater;
	public boolean onMoveableObject;

	public LivingEntity(World world, TaleOfPecora game) {
		this.world = world;
		this.game = game;
		if (this instanceof Sheep)
			isPlayer = true;
	}

	public void jump(float height) {
		if (isDying)
			return;
		onGround = false;
		onMoveableObject = false;
		velocity.y = height;
	}

	public boolean isMovingRight() {
		return velocity.x > 0;
	}

	public boolean isMovingLeft() {
		return velocity.x < 0;
	}

	public void turnRight() {
		if (transform.scaleX < 0) {
			transform.scaleX = transform.scaleX * -1;
			transform.x -= dimension.width;
		}
		onMoveableObject = false;
	}

	public void turnLeft() {
		if (transform.scaleX > 0) {
			transform.scaleX = transform.scaleX * -1;
			transform.x += dimension.width;
		}
		onMoveableObject = false;
	}

	public void move(float delta) {
		if (firstFrame) {
			firstFrame = false;
			delta = 0.0001f;
		}
		if (isDying)
			return;
		if (applyGravity) {
			if (!inWater) {
				velocity.y -= Constants.GRAVITY * delta;
			} else {
				velocity.y -= Constants.GRAVITY * delta / 5;
			}
			if (velocity.y < Constants.MAXIMUM_NEGATIVE_VELOCITY) {
				velocity.y = Constants.MAXIMUM_NEGATIVE_VELOCITY;
			}
		}

		if (inWater) {
			if (velocity.x >= Constants.PLAYER_WATERSPEED) {
				velocity.x = Constants.PLAYER_WATERSPEED;
			} else if (velocity.x <= -Constants.PLAYER_WATERSPEED) {
				velocity.x = -Constants.PLAYER_WATERSPEED;
			}
		}

		if (velocity.y < -9.81f) {
			System.out.println("platform reset due to high velocity");
			onMoveableObject = false;
		}

		transform.x += velocity.x * delta;
		transform.y += velocity.y * delta;
	}

	public float getCenter() {
		return (dimension.width * transform.scaleX) / 2f; // Only works if scale is 1
	}

	public void rayCast(final float delta) {
		if (isDying)
			return;

		this.delta = delta;

		// Calculate all sizes for our raycast
		float rayGap = dimension.height / 2;
		float rayGapSide = dimension.width / 2;
		float raySize = 0;
		float raySizeSide = 0;
		float scale = PhysicsBodyLoader.getScale();

		raySize = -(velocity.y * delta);

		if (raySize < 0)
			raySize *= -1;

		raySizeSide = velocity.x * delta;

		if (raySizeSide < 0)
			raySizeSide *= -1;

		if (raySize == 0f)
			raySize = 0.1f;

		if (raySizeSide == 0f)
			raySizeSide = 0.1f;

		/*
		 * === Ray cast - collision detection ===
		 * rayFrom is always the center of our entity (starting point).
		 * rayTo is the point where we want to stop - in our case: direction of the ray (up, down, right, left) with a length based on velocity and framerate (delta)
		 * the vector between these to points is where we check for objects.
		 * If our ray hits something (callback) then we stop the movement, mostly by moving the player back a bit (not too much, otherwise it looks buggy, but also
		 * not to less because otherwise you can glitch through walls in a microstutter).
		 */

		// Ray cast down
		Vector2 rayFrom = new Vector2((transform.x + getCenter()) * scale, (transform.y + rayGap) * scale);
		Vector2 rayTo = new Vector2((transform.x + getCenter()) * scale, (transform.y - raySize) * scale);

		world.rayCast(downCallback, rayFrom, rayTo);

		// Ray cast up
		rayFrom = new Vector2((transform.x + getCenter()) * scale, (transform.y + rayGap) * scale);
		rayTo = new Vector2((transform.x + getCenter()) * scale, (transform.y + rayGap * 2 + raySize) * scale);

		world.rayCast(upCallback, rayFrom, rayTo);

		// Ray cast right
		rayFrom = new Vector2((transform.x + getCenter()) * scale, (transform.y + rayGap) * scale);
		rayTo = new Vector2(((transform.x + getCenter()) + rayGapSide + raySizeSide) * scale, (transform.y + rayGap) * scale);

		world.rayCast(rightCallback, rayFrom, rayTo);

		// Ray cast left
		rayFrom = new Vector2((transform.x + getCenter()) * scale, (transform.y + rayGap) * scale);
		rayTo = new Vector2(((transform.x + getCenter()) - rayGapSide - raySizeSide) * scale, (transform.y + rayGap) * scale);

		world.rayCast(leftCallback, rayFrom, rayTo);
	}

	private RayCastCallback leftCallback = new RayCastCallback() {

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			float x = point.x / PhysicsBodyLoader.getScale();
			float y = point.y / PhysicsBodyLoader.getScale();
			float pX = transform.x;
			float pY = transform.y;
			if (!Utils.isSimilar(x, pX, 1f) && !Utils.isSimilar(y, pY, 1f))
				return -1;

			/*
			 * System.out.println("Ray cast left");
			 * if (isPlayer && fixture.isSensor()) {
			 * Gdx.app.debug("[Entity]", "Ray cast left killed player");
			 * // die();
			 * }
			 */
			if (fixture.getBody().getType() == BodyType.StaticBody) {
				float move = velocity.x * delta * -1;
				if (move <= 0) {
					move = 1f * delta;
				}
				move += 0.001f; // Apply a bit more than needed, prevents glitching (CONFIRMED; DON'T REMOVE!)
				transform.x += move;
				// transform.x = point.x / PhysicsBodyLoader.getScale() + 0.01f;
				return 0;
			} else if (fixture.getBody().getType() == BodyType.KinematicBody) {
				if (velocity.x >= Constants.PLAYER_SPEED) {
					velocity.x = Constants.PLAYER_WATERSPEED;
				} else if (velocity.x <= -Constants.PLAYER_SPEED) {
					velocity.x = -Constants.PLAYER_WATERSPEED;
				}
				if (velocity.y >= Constants.PLAYER_WATERSPEED) {
					velocity.y = Constants.PLAYER_WATERSPEED;
				} else if (velocity.y <= -Constants.PLAYER_SPEED) {
					velocity.y = -Constants.PLAYER_SPEED;
				}
				inWater = true;
				return 1;
			} else {
				return 1;
			}
		}
	};

	private RayCastCallback rightCallback = new RayCastCallback() {

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			float x = point.x / PhysicsBodyLoader.getScale();
			float y = point.y / PhysicsBodyLoader.getScale();
			float pX = transform.x;
			float pY = transform.y;
			if (!Utils.isSimilar(x, pX, 1f) && !Utils.isSimilar(y, pY, 1f))
				return -1;

			/*
			 * System.out.println("Ray cast right");
			 * if (isPlayer && fixture.isSensor()) {
			 * Gdx.app.debug("[Entity]", "Ray cast right killed player");
			 * // die();
			 * }
			 */
			if (fixture.getBody().getType() == BodyType.StaticBody) {
				float move = velocity.x * delta;
				if (move <= 0) {
					move = 1f * delta;
				}
				move += 0.001f; // Apply a bit more than needed, prevents glitching (CONFIRMED; DON'T REMOVE!)
				transform.x -= move; // Move the placker back. Might look a bit buggy, but it's a first attempt
				// transform.x = point.x / PhysicsBodyLoader.getScale() - 0.01f;
				return 0;
			} else if (fixture.getBody().getType() == BodyType.KinematicBody) {
				if (velocity.x >= Constants.PLAYER_SPEED) {
					velocity.x = Constants.PLAYER_WATERSPEED;
				} else if (velocity.x <= -Constants.PLAYER_SPEED) {
					velocity.x = -Constants.PLAYER_WATERSPEED;
				}
				if (velocity.y >= Constants.PLAYER_WATERSPEED) {
					velocity.y = Constants.PLAYER_WATERSPEED;
				} else if (velocity.y <= -Constants.PLAYER_SPEED) {
					velocity.y = -Constants.PLAYER_SPEED;
				}
				inWater = true;
				return 1;
			} else {
				return 1;
			}
		}
	};

	private RayCastCallback upCallback = new RayCastCallback() {

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			float x = point.x / PhysicsBodyLoader.getScale();
			float y = point.y / PhysicsBodyLoader.getScale();
			float pX = transform.x;
			float pY = transform.y;
			if (!Utils.isSimilar(x, pX, 1f) && !Utils.isSimilar(y, pY, 1f))
				return -1;

			/*
			 * System.out.println("Ray cast up");
			 * if (isPlayer && fixture.isSensor()) {
			 * Gdx.app.debug("[Entity]", "Ray cast up killed player");
			 * // die();
			 * }
			 */

			if (fixture.getBody().getType() == BodyType.StaticBody) {
				if (inWater)
					inWater = false;
				velocity.y = -0.5f;
				transform.y -= 1.5f * delta;

				onGround = false;
				jumpCounter = 2; // Prevent double jumps through walls

				// transform.y = point.y / PhysicsBodyLoader.getScale() + 0.001f;

				return 0;
			} else if (fixture.getBody().getType() == BodyType.KinematicBody) {
				if (velocity.x >= Constants.PLAYER_SPEED) {
					velocity.x = Constants.PLAYER_WATERSPEED;
				} else if (velocity.x <= -Constants.PLAYER_SPEED) {
					velocity.x = -Constants.PLAYER_WATERSPEED;
				}
				if (velocity.y >= Constants.PLAYER_WATERSPEED) {
					velocity.y = Constants.PLAYER_WATERSPEED;
				} else if (velocity.y <= -Constants.PLAYER_SPEED) {
					velocity.y = -Constants.PLAYER_SPEED;
				}
				inWater = true;
				return 1;
			} else {
				return 1;
			}
		}
	};

	private ComponentMapper<Platform> mapper = ComponentMapper.getFor(Platform.class);

	private RayCastCallback downCallback = new RayCastCallback() {

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			float x = point.x / PhysicsBodyLoader.getScale();
			float y = point.y / PhysicsBodyLoader.getScale();
			float pX = transform.x;
			float pY = transform.y;
			if (!Utils.isSimilar(x, pX, 1f) && !Utils.isSimilar(y, pY, 1f))
				return -1;

			// System.out.println("Ray cast down");
			/*
			 * if (isPlayer && fixture.isSensor()) {
			 * Gdx.app.debug("[Entity]", "Ray cast down - player killed night");
			 * ImmutableArray<com.badlogic.ashley.core.Entity> entities = TaleOfPecora.instance.getSceneLoader().getEngine().getEntitiesFor(Family.all(Knight.class).get());
			 * ComponentMapper<Knight> mapper = ComponentMapper.getFor(Knight.class);
			 * for (com.badlogic.ashley.core.Entity e : entities) {
			 * Knight knight = mapper.get(e);
			 * if (Utils.isSimilar(knight.transform.x, transform.x, 3f)) {
			 * if (Utils.isSimilar(knight.transform.y, transform.y, 3f)) {
			 * // KnightSystem.kill(knight);
			 * }
			 * }
			 * }
			 * }
			 */
			if (fixture.getBody().getType() == BodyType.StaticBody) {
				if (inWater)
					inWater = false;
				if (velocity.y <= 0) {
					onGround = true;
					jumpCounter = 0;

					// if (!isPlayer) {
					// velocity.y = 0;
					if (fixture.getBody().getUserData() != null) {
						Entity e = (Entity) fixture.getBody().getUserData();
						Platform platform = mapper.get(e);
						if (platform != null) {
							System.out.println("Platform detect");
							velocity.y = platform.velocity.y * 15f;
							velocity.x = platform.velocity.x * 15f;
							move(delta);
							onMoveableObject = true;
						} else {
							velocity.y = 0;
						}
					} else {
						velocity.y = 0;
					}

					// transform.y += 1f * delta;
					transform.y = point.y / PhysicsBodyLoader.getScale() + 0.001f;
				}

				return 0;
			} else if (fixture.getBody().getType() == BodyType.KinematicBody) {
				if (velocity.x >= Constants.PLAYER_SPEED) {
					velocity.x = Constants.PLAYER_WATERSPEED;
				} else if (velocity.x <= -Constants.PLAYER_SPEED) {
					velocity.x = -Constants.PLAYER_WATERSPEED;
				}
				if (velocity.y >= Constants.PLAYER_WATERSPEED) {
					velocity.y = Constants.PLAYER_WATERSPEED;
				} else if (velocity.y <= -Constants.PLAYER_SPEED) {
					velocity.y = -Constants.PLAYER_SPEED;
				}
				inWater = true;
				return 1;
			} else {
				return 1;
			}
		}
	};

	public abstract void die();

	public TintComponent getTint() {
		return this.tint;
	}

	public void setTint(TintComponent tint) {
		this.tint = tint;
	}

}
