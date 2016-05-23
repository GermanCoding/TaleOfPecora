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
import com.germancoding.taleofpecora.Utils;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class LevelSelectStage extends Stage {

	ProjectInfoVO info;
	CompositeActor menuActor;

	public LevelSelectStage(IResourceRetriever ir) {
		Gdx.input.setInputProcessor(this);
		info = ir.getProjectVO();
		float scale = 1 / TaleOfPecora.instance.camera.zoom;

		CompositeItemVO item = info.libraryItems.get("levelSelect");
		menuActor = new CompositeActor(item, ir);

		menuActor.setScale(scale);

		float x = (getWidth() / 2) - (menuActor.getWidth() * scale / 2);
		float y = (getHeight() / 2) - (menuActor.getHeight() * scale / 2);

		menuActor.setPosition(x, y);
		menuActor.setTouchable(Touchable.childrenOnly);

		// Click actions
		for (Actor cButton : menuActor.getItemsByTag("button")) {
			final CompositeActor button;
			if (cButton instanceof CompositeActor)
				button = (CompositeActor) cButton;
			else
				continue; // Ignore buttons that aren't composite items. Should not happen unless we have errors somewhere.

			if (Utils.isInteger(button.getName())) {
				int level = Integer.parseInt(button.getName());
				final int layerIndex = button.getLayerIndex("locked");
				if (!button.getVo().composite.layers.get(layerIndex).isVisible) {
					TaleOfPecora.instance.loadLevel(level - 1);
					TaleOfPecora.instance.renderMenu = false;
				}
			} else if (button.getName().equalsIgnoreCase("playButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// TODO: Dispose menu to save memory, but: This could cause exceptions if no waiting algorithm is present because we have to wait for the dispose to finish
						TaleOfPecora.instance.loadLevel();
						TaleOfPecora.instance.renderMenu = false;
					}
				});
			} else if (button.getName().equalsIgnoreCase("backToMenuButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.showMainMenu();
					}
				});
			}

			// Hover effects
			button.addListener(new InputListener() {
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					final int layerIndex = button.getLayerIndex("pressed");
					final int layerIndex2 = button.getLayerIndex("locked");
					if (!button.getVo().composite.layers.get(layerIndex).isVisible && !button.getVo().composite.layers.get(layerIndex2).isVisible) {
						button.setLayerVisibility("hover", true);
						button.setLayerVisibility("normal", false);
					}
				}

				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					super.exit(event, x, y, pointer, toActor);
					final int layerIndex = button.getLayerIndex("pressed");
					final int layerIndex2 = button.getLayerIndex("locked");
					if (!button.getVo().composite.layers.get(layerIndex).isVisible && !button.getVo().composite.layers.get(layerIndex2).isVisible) {
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
