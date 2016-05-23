package com.germancoding.taleofpecora;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class SettingsStage extends Stage {

	ProjectInfoVO info;
	CompositeActor menuActor;

	public SettingsStage(IResourceRetriever ir) {
		info = ir.getProjectVO();
		Gdx.input.setInputProcessor(this);

		CompositeItemVO item = info.libraryItems.get("settingsMenu");
		menuActor = new CompositeActor(item, ir);
		float scale = 1 / TaleOfPecora.instance.camera.zoom;
		menuActor.setScale(scale);
		float x = (getWidth() / 2) - (menuActor.getWidth() * scale / 2);
		float y = (getHeight() / 2) - (menuActor.getHeight() * scale / 2);
		menuActor.setPosition(x, y);
		menuActor.setTouchable(Touchable.childrenOnly); // The actor is the full menu screen, only the parts (buttons...) should be clickable/touchable.

		// Click actions
		for (Actor cButton : menuActor.getItemsByTag("button")) {
			final CompositeActor button;
			if (cButton instanceof CompositeActor)
				button = (CompositeActor) cButton;
			else
				continue; // Ignore buttons that aren't composite items. Should not happen unless we have errors somewhere.

			if (button.getName().equalsIgnoreCase("TODO")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// TODO
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
