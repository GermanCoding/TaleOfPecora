/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

public class Credits implements IScript {

	ItemWrapper credits;
	Camera camera;

	public Credits(ItemWrapper credits) {
		this.credits = credits;
	}

	@Override
	public void init(Entity entity) {
		Gdx.input.setInputProcessor(new InputProcessor() {

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				if (character == Input.Keys.ESCAPE) {
					TaleOfPecora.instance.showMainMenu();
					return true;
				}
				return false;
			}

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.ESCAPE) {
					TaleOfPecora.instance.showMainMenu();
					return true;
				}
				return false;
			}
		});
		TransformComponent transform = credits.getEntity().getComponent(TransformComponent.class);
		DimensionsComponent dimension = credits.getEntity().getComponent(DimensionsComponent.class);

		camera = TaleOfPecora.instance.camera;
		camera.translate(new Vector3(transform.x, transform.y + dimension.height, camera.position.z));
		camera.update();
	}

	@Override
	public void act(float delta) {
		camera.translate(0, -0.7f * delta, 0);
		// camera.update();
	}

	@Override
	public void dispose() {
		camera = null;
	}

}
