package com.germancoding.taleofpecora.entity;

import java.util.HashMap;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.Utils;
import com.uwsoft.editor.renderer.components.CompositeTransformComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

public class KnightSystem extends IteratingSystem {

	private ComponentMapper<Knight> mapper;
	private Sheep player;
	private static int totalKnights;

	@SuppressWarnings("unchecked")
	public KnightSystem() {
		super(Family.all(Knight.class).get());
		mapper = ComponentMapper.getFor(Knight.class);
		player = TaleOfPecora.instance.player;
		totalKnights = 0;
	}

	public boolean playerHitKnight(Knight knight) {
		if (player.transform == null)
			return false;
		float knightX = knight.transform.x + knight.getCenter();
		float knightY = knight.transform.y + knight.dimension.height / 2;

		float playerX = player.transform.x + player.getCenter();
		float playerY = player.transform.y + player.dimension.height / 2;

		float distanceX = (knight.dimension.width / 2 + player.dimension.width / 2) / 2;
		float distanceY = (knight.dimension.height / 2 + player.dimension.height / 2);

		if (Utils.isSimilar(playerX, knightX, distanceX)) {
			if (Utils.isSimilar(playerY, knightY, distanceY)) {
				if (playerY >= (knightY + knight.dimension.height / 2)) {
					if (player.velocity.y < 0) {
						player.addEnemy();
						Gdx.app.debug("[KnightSystem]", "position detection positive");
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean knightHitPlayer(Knight knight) {
		if (player.transform == null)
			return false;
		float knightX = knight.transform.x + knight.getCenter();
		float knightY = knight.transform.y + knight.dimension.height / 2;

		float playerX = player.transform.x + player.getCenter();
		float playerY = player.transform.y + player.dimension.height / 2;

		float distanceX = (knight.dimension.width / 2 + player.dimension.width / 2) / 2;
		float distanceY = (knight.dimension.height / 2 + player.dimension.height / 2) / 2;

		if (Utils.isSimilar(playerX, knightX, distanceX)) {
			if (Utils.isSimilar(playerY, knightY, distanceY)) {
				if (player.transform.y <= (knightY + knight.dimension.height / 2))
					return true;
			}
		}
		return false;
	}

	public static void init(Entity entity, Knight knight) {
		Gdx.app.debug("[KnightSystem]", "init() called");
		knight.dataTable = new HashMap<String, Float>();
		knight.transform = ComponentRetriever.get(entity, TransformComponent.class);
		knight.physics = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
		knight.dimension = ComponentRetriever.get(entity, DimensionsComponent.class);
		knight.cTransform = ComponentRetriever.get(entity, CompositeTransformComponent.class);
		knight.setBaseLocation(new Vector2(knight.transform.x, knight.transform.y));
		knight.position = new Vector2(knight.getBaseLocation());
		knight.velocity = new Vector2(0, 0);
		knight.item = ComponentRetriever.get(entity, MainItemComponent.class);
		knight.entity = entity;
		// knight.applyGravity = false;
		String[] vars = knight.item.customVars.split(";");
		for (String var : vars) {
			try {
				String key = var.split(":")[0];
				float value = Float.parseFloat(var.split(":")[1]);
				knight.dataTable.put(key, value);
			} catch (IndexOutOfBoundsException e) {
				; // Ignore
			}
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (TaleOfPecora.instance.isPaused())
			return;
		Knight knight = mapper.get(entity);

		if (knight.getBaseLocation() == null) {
			init(entity, knight);
			setTotalKnights(getTotalKnights() + 1);
		}

		if (knight.isDying) {
			return;
		}

		TransformComponent transform = knight.transform;
		Vector2 base = knight.getBaseLocation();
		Vector2 current = new Vector2(transform.x, transform.y);

		if (knight.isMovingLeft()) {
			float deltaX = base.x - current.x;
			if (deltaX >= knight.dataTable.get("maxX")) {
				// Turn right
				knight.turnRight();
				knight.velocity.x = knight.velocity.x * -1;
			}
		} else if (knight.isMovingRight()) {
			float deltaX = current.x - base.x;
			if (deltaX >= knight.dataTable.get("maxX")) {
				// Turn left
				knight.turnLeft();
				knight.velocity.x = knight.velocity.x * -1;
			}
		} else {
			// Randomly walk in a direction
			boolean right = MathUtils.randomBoolean();
			if (right) {
				knight.velocity.x = Constants.KNIGHT_SPEED;
				knight.turnRight();
			} else {
				knight.velocity.x = -(Constants.KNIGHT_SPEED);
				knight.turnLeft();
			}
		}

		knight.move(deltaTime);
		if (!player.isDying) {
			if (playerHitKnight(knight)) {
				kill(knight);
			} else if (knightHitPlayer(knight)) {
				Gdx.app.debug("[KnightSystem]", "killing the player");
				player.die(true);
			}
		}
		knight.rayCast(deltaTime);
	}

	public static void kill(final Knight knight) {
		if (knight.isDying)
			return;
		Gdx.app.debug("[KnightSystem]", "killing a knight");
		knight.isDying = true;
		/*
		 * CompositeItemVO vo = TaleOfPecora.instance.getSceneLoader().loadVoFromLibrary("knightDead");
		 * SpriteAnimationVO animVO = (SpriteAnimationVO) vo.composite.getAllItems().get(0);
		 * animVO.fps = 10;
		 * animVO.playMode = 0; // 0 = normal, 2 = loop
		 * Entity newKnight = TaleOfPecora.instance.getSceneLoader().getEntityFactory().createEntity(TaleOfPecora.instance.getSceneLoader().getRoot(), vo);
		 * // loadFromLibrary("knightDead");
		 * TaleOfPecora.instance.getSceneLoader().getEntityFactory().initAllChildren(TaleOfPecora.instance.getSceneLoader().getEngine(), newKnight, vo.composite);
		 * TaleOfPecora.instance.getSceneLoader().getEngine().removeEntity(knight.entity);
		 */
		TaleOfPecora.instance.getSceneLoader().getEngine().removeEntity(knight.entity);
		Entity newKnight = TaleOfPecora.instance.loadAnimation("knightDead", 10, PlayMode.NORMAL);

		float x = knight.transform.x;
		float y = knight.transform.y;
		float scale = knight.transform.scaleX;
		init(newKnight, knight);
		knight.transform.scaleX = scale;
		knight.transform.x = x;
		if (scale < 0) {
			knight.transform.x += knight.dimension.width / 3;
		}
		knight.transform.y = y;
		TaleOfPecora.instance.getSceneLoader().getEngine().addEntity(newKnight);

		// TaleOfPecora.instance.getSceneLoader().getEntityFactory().getSpriteComponentFactory().createComponents(TaleOfPecora.instance.getSceneLoader().getRoot(), knight.entity, info);
		TaleOfPecora.instance.scheduler.runTask(new Runnable() {

			@Override
			public void run() {
				TaleOfPecora.instance.getSceneLoader().getEngine().removeEntity(knight.entity);
			}
		}, 1000l);
	}

	public static int getTotalKnights() {
		return totalKnights;
	}

	public static void setTotalKnights(int totalKnights) {
		KnightSystem.totalKnights = totalKnights;
	}

}
