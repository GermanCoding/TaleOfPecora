package com.germancoding.taleofpecora.stages;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.ScoreCalculator;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.entity.CarrotSystem;
import com.germancoding.taleofpecora.entity.KnightSystem;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class LevelFinishedStage extends Stage {

	ProjectInfoVO info;
	CompositeActor menuActor;

	public LevelFinishedStage(IResourceRetriever ir) {
		Gdx.input.setInputProcessor(this);
		info = ir.getProjectVO();
		float scale = 1 / TaleOfPecora.instance.camera.zoom;

		CompositeItemVO item = info.libraryItems.get("levelComplete");
		menuActor = new CompositeActor(item, ir);

		menuActor.setScale(scale);

		float x = (getWidth() / 2) - (menuActor.getWidth() * scale / 2);
		float y = (getHeight() / 2) - (menuActor.getHeight() * scale / 2);

		menuActor.setPosition(x, y);
		menuActor.setTouchable(Touchable.childrenOnly);

		Label carrotLabel = (Label) menuActor.getItem("carrotCounter");
		CarrotSystem carrotSystem = TaleOfPecora.instance.getSceneLoader().getEngine().getSystem(CarrotSystem.class);
		int carrotsCollected = TaleOfPecora.instance.player.getCarrotsCollected();
		int carrotsMax = carrotSystem.getTotalCarrots();
		carrotLabel.setText(carrotsCollected + "/" + carrotsMax);

		Label enemyLabel = (Label) menuActor.getItem("enemyCounter");
		// KnightSystem knightSystem = TaleOfPecora.instance.getSceneLoader().getEngine().getSystem(KnightSystem.class);
		int enemies = KnightSystem.getTotalKnights();
		int enemiesKilled = TaleOfPecora.instance.player.getEnemiesKilled();
		enemyLabel.setText(enemiesKilled + "/" + enemies);

		Label starLabel = (Label) menuActor.getItem("starCounter");
		int starsCollected = TaleOfPecora.instance.player.getStarsCollected();
		int starsMax = 0; // TODO
		starLabel.setText(starsCollected + "/" + starsMax);

		long delta = System.currentTimeMillis() - TaleOfPecora.instance.startTime;
		Date date = new Date(delta);
		Label timeLabel = (Label) menuActor.getItem("timeCounter");
		timeLabel.setText(new SimpleDateFormat("mm:ss").format(date));

		int score = ScoreCalculator.getScore(carrotsCollected, enemiesKilled, starsCollected, delta);

		if (ScoreCalculator.isPerfect(carrotsCollected, carrotsMax, enemiesKilled, enemies, starsCollected, starsMax, delta)) {
			score += Constants.POINTS_FOR_PERFECT;
			menuActor.setLayerVisibility("perfect", true);
			// TODO: Play a sound?
		}

		Label totalScoreLabel = (Label) menuActor.getItem("totalScore");
		totalScoreLabel.setText(score + "");

		// TODO: Save stats (async?)

		// Click actions
		for (Actor cButton : menuActor.getItemsByTag("button")) {
			final CompositeActor button;
			if (cButton instanceof CompositeActor)
				button = (CompositeActor) cButton;
			else {
				continue; // Ignore buttons that aren't composite items. Should not happen unless we have errors somewhere.
			}

			if (button.getName().equalsIgnoreCase("backToMenuButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.showMainMenu();
						TaleOfPecora.instance.levelComplete = null;
					}
				});
			} else if (button.getName().equalsIgnoreCase("continueButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.loadLevel(TaleOfPecora.instance.currentLevel.getId() + 1);
					}
				});
			} else if (button.getName().equalsIgnoreCase("retryButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.loadLevel(TaleOfPecora.instance.currentLevel.getId());
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
