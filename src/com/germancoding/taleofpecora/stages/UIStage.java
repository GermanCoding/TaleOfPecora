package com.germancoding.taleofpecora.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class UIStage extends Stage {

	Label fpsCounter;
	Label carrotCounter;
	/*
	 * CompositeActor fpsActor;
	 * LabelVO fpsCounter;
	 * CompositeItemVO fpsVO;
	 */
	ProjectInfoVO info;

	public UIStage(IResourceRetriever ir) {
		info = ir.getProjectVO();
		float scale = 1 / TaleOfPecora.instance.camera.zoom;
		// CompositeItemVO fpsCounter = info.libraryItems.get("fpsCounter");

		// === FPS Counter === //
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("freetypefonts/kirbyss.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 30;
		parameter.color = Color.BLACK;
		BitmapFont font = generator.generateFont(parameter);
		Label label = new Label("ERROR", new LabelStyle(font, Color.BLACK)); // (new BitmapFont(Gdx.files.internal("freetypefonts/Arial.ttf")), Color.GREEN));
		label.setScale(scale);
		label.setSize(200, 80);

		fpsCounter = label;
		addActor(label);
		label.setX(getWidth() - label.getWidth());
		label.setY(getHeight() - label.getHeight());

		// === Carrot icon === //
		CompositeItemVO vo = info.libraryItems.get("carrotIcon");
		CompositeActor carrotActor = new CompositeActor(vo, ir);
		carrotActor.setScale(scale);
		carrotActor.setX(carrotActor.getWidth() - 60 * scale);
		carrotActor.setY(getHeight() - carrotActor.getHeight() - 80 * scale);

		// === Stats base === //
		CompositeItemVO statsVo = info.libraryItems.get("statsBase");
		CompositeActor statsActor = new CompositeActor(statsVo, ir);
		statsActor.setScale(scale);
		statsActor.setX(carrotActor.getX());
		statsActor.setY(carrotActor.getY());

		// === Carrot counter ===
		carrotCounter = new Label("ERROR", new LabelStyle(font, Color.BLACK));
		carrotCounter.setScale(scale);
		carrotCounter.setAlignment(Align.bottomLeft);
		float heightFix = statsActor.getY() + ((statsActor.getHeight() * scale) / 3f);
		carrotCounter.setBounds(carrotActor.getX() + (carrotActor.getWidth() * scale * 2), heightFix, statsActor.getWidth() * scale, statsActor.getHeight() * scale);

		// Add actors in correct order
		addActor(statsActor);
		addActor(carrotActor);
		addActor(carrotCounter);

		// Buttons...
		// CompositeItemVO item = info.libraryItems.get("fpsCounter");
		// label = (LabelVO) item.composite.getAllItems().get(0);
		// CompositeActor buttonActor = new CompositeActor(item, ir);
		// addActor(buttonActor);
		// buttonActor.setX/Y(...)
		// buttonActor.addListener(new ClickListener() {
	}

	@Override
	public void act(float delta) {
		carrotCounter.setText("x " + TaleOfPecora.instance.player.getCarrotsCollected());
		fpsCounter.setText("FPS: " + Gdx.app.getGraphics().getFramesPerSecond());
		super.act(delta);
	};
}
