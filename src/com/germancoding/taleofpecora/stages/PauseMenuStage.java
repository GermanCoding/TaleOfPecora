/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class PauseMenuStage extends Stage {

	ProjectInfoVO info;
	CompositeActor menuActor;

	public PauseMenuStage(final IResourceRetriever ir) {
		info = ir.getProjectVO();
		Gdx.input.setInputProcessor(this);

		CompositeItemVO item = info.libraryItems.get("pauseMenu");
		menuActor = new CompositeActor(item, ir);
		float scale = 1 / TaleOfPecora.instance.camera.zoom;
		scale -= 0.2f;  // - 0.2 is a hardcoded value to scale down this actor, since it was designed too big
		menuActor.setScale(scale);
		System.out.println("getWidth():" + getWidth());
		System.out.println("getHeight():" + getHeight());
		System.out.println("menuActor.getWidth():" + menuActor.getWidth() * scale);
		System.out.println("menuActor.getHeight():" + menuActor.getHeight() * scale);
		float x = (getWidth() / 2f) - (menuActor.getWidth() * scale / 2f);
		float y = (getHeight() / 2f) - (menuActor.getHeight() * scale / 2f);
		System.out.println("x:" + x);
		System.out.println("y:" + y);
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		menuActor.setPosition(x, y);
		menuActor.setTouchable(Touchable.childrenOnly); // The actor is the full menu screen, only the parts (buttons...) should be clickable/touchable.

		// Click actions
		for (Actor cButton : menuActor.getItemsByTag("button")) {
			final CompositeActor button;
			if (cButton instanceof CompositeActor)
				button = (CompositeActor) cButton;
			else
				continue; // Ignore buttons that aren't composite items. Should not happen unless we have errors somewhere.

			if (button.getName().equalsIgnoreCase("resumeButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.setActivePause(false);
						TaleOfPecora.instance.renderMenu = false;
						Gdx.input.setInputProcessor(TaleOfPecora.instance.mainInputProcessor);
					}
				});
			} else if (button.getName().equalsIgnoreCase("settingsButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.pauseMenu = null;
						TaleOfPecora.instance.settings = new SettingsStage(ir);
					}
				});
			} else if (button.getName().equalsIgnoreCase("exitButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						Gdx.app.exit();
					}
				});
			}

			// Hover effects
			button.addListener(new InputListener() {
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					final int layerIndex = button.getLayerIndex("pressed");
					if (!button.getVo().composite.layers.get(layerIndex).isVisible) {
						button.setLayerVisibility("hover", true);
						button.setLayerVisibility("normal", false);
					}
				}

				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					super.exit(event, x, y, pointer, toActor);
					final int layerIndex = button.getLayerIndex("pressed");
					if (!button.getVo().composite.layers.get(layerIndex).isVisible) {
						button.setLayerVisibility("hover", false);
						button.setLayerVisibility("normal", true);
					}
				}
			});
		}

		Color color = menuActor.getColor();
		menuActor.setColor(color.r, color.g, color.b, 0); // Stage is invisible first and will be faded in
		addActor(menuActor);
	}

	@Override
	public void act(float delta) {
		if (menuActor.getColor().a < 1) {
			Color color = menuActor.getColor();
			menuActor.setColor(color.r, color.g, color.b, color.a + 1f * delta); // Fade in
		}
		super.act(delta);
	}
}
