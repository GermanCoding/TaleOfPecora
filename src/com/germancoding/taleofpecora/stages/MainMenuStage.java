package com.germancoding.taleofpecora.stages;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class MainMenuStage extends Stage {

	ProjectInfoVO info;
	CompositeActor menuActor;

	public MainMenuStage(IResourceRetriever ir) {
		info = ir.getProjectVO();
		Gdx.input.setInputProcessor(this);

		CompositeItemVO item = info.libraryItems.get("mainMenu");
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

			if (button.getName().equalsIgnoreCase("startButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// TODO: Dispose menu to save memory, but: This could cause exceptions if no waiting algorithm is present because we have to wait for the dispose to finish
						if (TaleOfPecora.instance.config.getLastSuccessfullLevel() >= 0) {
							TaleOfPecora.instance.mainMenu = null;
							TaleOfPecora.instance.showLevelSelect();
						} else {
							TaleOfPecora.instance.loadLevel();
							TaleOfPecora.instance.renderMenu = false;
						}
					}
				});
			} else if (button.getName().equalsIgnoreCase("twitterButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						try {
							openWebpage(new URL(Constants.TWITTER_LINK));
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
				});
			} else if (button.getName().equalsIgnoreCase("creditsButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.loadCreditsLevel();
						TaleOfPecora.instance.mainMenu = null;
					}
				});
			} else if (button.getName().equalsIgnoreCase("settingsButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.mainMenu = null;
						TaleOfPecora.instance.showSettings();
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

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
