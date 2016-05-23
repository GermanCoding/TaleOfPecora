package com.germancoding.taleofpecora.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.Utils;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

public class CarrotSystem extends IteratingSystem {

	private ComponentMapper<Carrot> mapper;
	private Sheep player;
	private int totalCarrots;

	@SuppressWarnings("unchecked")
	public CarrotSystem() {
		super(Family.all(Carrot.class).get());
		mapper = ComponentMapper.getFor(Carrot.class);
		player = TaleOfPecora.instance.player;
	}

	public boolean playerIntersectCarrot(Carrot carrot) {
		if (player.transform == null)
			return false;
		float carrotX = carrot.transform.x + carrot.getCenter();
		float carrotY = carrot.transform.y + carrot.getHeight() / 2;

		float playerX = player.transform.x + player.getCenter();
		float playerY = player.transform.y + player.dimension.height / 2;

		float distanceX = ((carrot.getWidth() / 2 + player.dimension.width / 2) / 2) + 0.2f;
		float distanceY = ((carrot.getHeight() / 2 + player.dimension.height / 2)) + 0.2f;

		if (Utils.isSimilar(playerX, carrotX, distanceX)) {
			if (Utils.isSimilar(playerY, carrotY, distanceY)) {
				return true;
			}
		}
		return false;
	}

	public void init(Entity entity, Carrot carrot) {
		setTotalCarrots(getTotalCarrots() + 1);
		carrot.transform = ComponentRetriever.get(entity, TransformComponent.class);
		carrot.dimension = ComponentRetriever.get(entity, DimensionsComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (TaleOfPecora.instance.isPaused())
			return;
		Carrot carrot = mapper.get(entity);

		if (carrot.transform == null) {
			init(entity, carrot);
		}

		if (playerIntersectCarrot(carrot)) {
			player.addCarrot();
			TaleOfPecora.instance.getSceneLoader().getEngine().removeEntity(entity);
		}
	}

	public int getTotalCarrots() {
		return totalCarrots;
	}

	public void setTotalCarrots(int totalCarrots) {
		this.totalCarrots = totalCarrots;
	}

}
