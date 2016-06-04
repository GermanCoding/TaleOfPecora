/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora.entity;

import java.util.HashMap;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.germancoding.taleofpecora.Constants;
import com.uwsoft.editor.renderer.components.CompositeTransformComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

public class PlatformSystem extends IteratingSystem {

	private ComponentMapper<Platform> mapper;

	@SuppressWarnings("unchecked")
	public PlatformSystem() {
		super(Family.all(Platform.class).get());
		mapper = ComponentMapper.getFor(Platform.class);
	}

	public static void init(Entity entity, Platform p) {
		Gdx.app.debug("[PlatformSystem]", "init() called");
		p.dataTable = new HashMap<String, Float>();
		p.transform = ComponentRetriever.get(entity, TransformComponent.class);
		p.physics = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
		p.dimension = ComponentRetriever.get(entity, DimensionsComponent.class);
		p.cTransform = ComponentRetriever.get(entity, CompositeTransformComponent.class);
		p.setBaseLocation(new Vector2(p.transform.x, p.transform.y));
		p.position = new Vector2(p.getBaseLocation());
		p.velocity = new Vector2(0, 0);
		p.item = ComponentRetriever.get(entity, MainItemComponent.class);
		p.entity = entity;
		String[] vars = p.item.customVars.split(";");
		for (String var : vars) {
			try {
				String key = var.split(":")[0];
				float value = Float.parseFloat(var.split(":")[1]);
				p.dataTable.put(key, value);
			} catch (IndexOutOfBoundsException e) {
				; // Ignore
			}
		}

		if (p.dataTable.containsKey("speed")) {
			p.speed = p.dataTable.get("speed");
		} else {
			p.speed = Constants.PLATFORM_SPEED;
		}
	}

	@Override
	protected void processEntity(Entity entity, float delta) {
		Platform platform = mapper.get(entity);

		if (platform.physics == null) {
			init(entity, platform);
		}

		float maxY = 0, maxX = 0;
		if (platform.dataTable.containsKey("maxY")) // Seems like this check is neccessary to avoid NPE. Nothing with autoboxing.
			maxY = platform.dataTable.get("maxY");
		if (platform.dataTable.containsKey("maxX"))
			maxX = platform.dataTable.get("maxX");

		if (maxY != 0) {
			Vector2 base = platform.getBaseLocation();
			Vector2 current = new Vector2(platform.transform.x, platform.transform.y); // new Vector2(platform.physics.body.getPosition().x / PhysicsBodyLoader.getScale(), (platform.physics.body.getPosition().y / PhysicsBodyLoader.getScale()));
			float deltaY = base.y - current.y;
			// System.out.println(base.y);
			// System.out.println(current.y);
			if (deltaY >= maxY) {
				// Move up
				platform.velocity.y = platform.speed;
			} else if (deltaY <= -maxY) {
				// Move down
				platform.velocity.y = -platform.speed;
			} else {
				if (platform.velocity.y == 0) {
					// Default movement: Go up
					platform.velocity.y = platform.speed;
				}
			}
		}
		if (maxX != 0) {
			Vector2 base = platform.getBaseLocation();
			Vector2 current = new Vector2(platform.transform.x, platform.transform.y); // new Vector2(platform.physics.body.getPosition().x / PhysicsBodyLoader.getScale(), (platform.physics.body.getPosition().y / PhysicsBodyLoader.getScale()));
			float deltaX = base.x - current.x;
			// System.out.println(base.y);
			// System.out.println(current.y);
			if (deltaX >= maxX) {
				// Move right
				platform.velocity.x = platform.speed;
			} else if (deltaX <= -maxX) {
				// Move left
				platform.velocity.x = -platform.speed;
			} else {
				if (platform.velocity.x == 0) {
					// Default movement: Go right
					platform.velocity.x = platform.speed;
				}
			}
		}

		float newX = (platform.physics.body.getPosition().x) + (platform.velocity.x * delta * PhysicsBodyLoader.getScale());
		float newY = (platform.physics.body.getPosition().y) + (platform.velocity.y * delta * PhysicsBodyLoader.getScale());

		// System.out.println(platform.velocity.x * delta);
		// System.out.println(platform.transform.x);

		platform.physics.body.setTransform(newX, newY, platform.physics.body.getAngle());
	}

}
