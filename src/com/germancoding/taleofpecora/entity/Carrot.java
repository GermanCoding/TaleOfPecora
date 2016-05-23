package com.germancoding.taleofpecora.entity;

import com.badlogic.ashley.core.Component;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

public class Carrot implements Component {

	protected TransformComponent transform;
	protected DimensionsComponent dimension;

	public float getCenter() {
		if (transform.scaleX < 0)
			return (getWidth() * -1) / 2;
		else
			return getWidth() / 2;
	}

	public float getHeight() {
		return dimension.height * 0.5f;
	}

	public float getWidth() {
		return dimension.width * 0.5f;
	}

}
