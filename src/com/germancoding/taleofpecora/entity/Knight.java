/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora.entity;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;

public class Knight extends LivingEntity implements Component {

	protected HashMap<String, Float> dataTable;
	Vector2 position;

	public Knight(World world, TaleOfPecora game) {
		super(world, game);
	}

	public Knight() {
		super(TaleOfPecora.instance.world, TaleOfPecora.instance);
	}

	public Vector2 getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(Vector2 baseLocation) {
		this.baseLocation = baseLocation;
	}

	@Override
	public void die() {
		KnightSystem.kill(this);
	}

	/*
	@Override
	public void turnLeft() {
		super.turnLeft();
		position.x = physics.body.getPosition().x + dimension.width * PhysicsBodyLoader.getScale();
		position.y = physics.body.getPosition().y;
		physics.body.setTransform(position, physics.body.getAngle());

	}
	*/

	/*
	@Override
	public void turnRight() {
		super.turnRight();
		position.x = physics.body.getPosition().x - dimension.width * PhysicsBodyLoader.getScale();
		position.y = physics.body.getPosition().y;
		physics.body.setTransform(position, physics.body.getAngle());
	};
	*/

	public void moveOld(float delta) {
		if (isDying)
			return;

		if (firstFrame) {
			firstFrame = false;
			delta = 0.001f;
		}
		/*
		 * velocity.y -= Constants.GRAVITY * delta; // TODO: Maximum negative (and positive?) velocity
		 * if (velocity.y < -20f) {
		 * velocity.y = -20f;
		 * }
		 */
		// position.x += velocity.x * delta * PhysicsBodyLoader.getScale();
		position.x = physics.body.getPosition().x + (velocity.x * delta) * PhysicsBodyLoader.getScale();
		position.y = physics.body.getPosition().y;
		// position.y += velocity.y * delta; // * PhysicsBodyLoader.getScale();
		// position.x = transform.x;
		// position.y = transform.y;
		// cTransform.computedTransform.setTranslation(position.x, position.y, 0);
		physics.body.setTransform(position, physics.body.getAngle());
	}

}
