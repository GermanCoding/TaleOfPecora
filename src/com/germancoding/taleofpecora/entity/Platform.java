/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora.entity;

import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.CompositeTransformComponent;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;

public class Platform implements Component{
	
	protected com.badlogic.ashley.core.Entity entity;
	protected HashMap<String, Float> dataTable;
	Vector2 position;
	protected Vector2 baseLocation;
	protected TransformComponent transform;
	protected DimensionsComponent dimension;
	protected PhysicsBodyComponent physics;
	protected CompositeTransformComponent cTransform;
	protected MainItemComponent item;
	protected Vector2 velocity;
	protected float speed;
	
	public Vector2 getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(Vector2 baseLocation) {
		this.baseLocation = baseLocation;
	}


}
