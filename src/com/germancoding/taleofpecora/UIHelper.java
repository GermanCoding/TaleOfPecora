/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;

public class UIHelper {

	FreeTypeFontGenerator generator;
	ProjectInfoVO vo;
	Skin skin;
	Texture cursor;
	Texture selected;
	Texture background;
	Texture buttonUp;
	Texture buttonDown;
	Texture checkBoxOff;
	Texture checkBoxOn;

	public UIHelper() {
		generator = new FreeTypeFontGenerator(Gdx.files.internal("freetypefonts/kirbyss.ttf"));
		skin = new Skin();
	}

	public void initAdditionalResources(ProjectInfoVO vo) {
		this.vo = vo;
		// skin.add("background", new Texture("external/slice20_20.png"));
		cursor = new Texture("external/cursor.png");
		selected = new Texture("external/selected.png");
	}

	public Label createLabel(int fontSize, Color color, String text) {
		BitmapFont font = makeFont(fontSize, color);
		Label label = new Label(text, new LabelStyle(font, color)); // (new BitmapFont(Gdx.files.internal("freetypefonts/Arial.ttf")), Color.GREEN));
		return label;
	}

	public BitmapFont makeFont(int fontSize, Color color) {
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = fontSize;
		parameter.color = color;
		BitmapFont font = generator.generateFont(parameter);
		return font;
	}

	public Skin makeSkin(int fontSize, Color color, Color titleFontColor) {
		if (background == null) {
			background = new Texture("external/slice20_20.png");
		}
		if (buttonUp == null) {
			buttonUp = new Texture("external/slice09_09.png");
		}
		if (buttonDown == null) {
			buttonDown = new Texture("external/slice11_11.png");
		}
		BitmapFont font = makeFont(fontSize, color);
		LabelStyle labelStyle = new LabelStyle(font, color);
		TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(new TextureRegionDrawable(new TextureRegion(buttonUp)), new TextureRegionDrawable(new TextureRegion(buttonDown)), null, font);
		Window.WindowStyle windowStyle = new Window.WindowStyle(makeFont(fontSize, color), titleFontColor, new TextureRegionDrawable(new TextureRegion(background)));
		skin.add("default", labelStyle);
		skin.add("default", textButtonStyle);
		skin.add("dialog", windowStyle);
		skin.add("dialog", textButtonStyle);
		return skin;
	}

	public CheckBox createCheckBox(String text, int fontSize, Color color) {
		if (checkBoxOff == null) {
			checkBoxOff = new Texture("external/slice109.png");
		}
		if (checkBoxOn == null) {
			checkBoxOn = new Texture("external/slice108.png");
		}
		BitmapFont font = makeFont(fontSize, color);
		CheckBoxStyle style = new CheckBoxStyle(new TextureRegionDrawable(new TextureRegion(checkBoxOff)), new TextureRegionDrawable(new TextureRegion(checkBoxOn)), font, color);
		CheckBox box = new CheckBox(text, style);
		return box;
	}

	public TextField createTextField(String text, int fontSize, Color color) {
		BitmapFont font = makeFont(fontSize, color);
		// skin.add("font", font);
		// skin.add("default", new TextField.TextFieldStyle(font, color, skin.getDrawable("background"), skin.getDrawable("background"), skin.getDrawable("background")));
		// CompositeItemVO textBackground = vo.libraryItems.get("textBackground");
		// Entity entity = TaleOfPecora.instance.createEntity(textBackground);
		// CompositeActor actor = new CompositeActor(vo, null);
		// TextBoxVO textBoxVO = new TextBoxVO();
		// textBoxVO.loadFromEntity(entity);// textBackground.composite.

		TextField.TextFieldStyle style = new TextField.TextFieldStyle(); // new TextField.TextFieldStyle(font, color, null, null, null);
		style.font = font;
		style.fontColor = color;
		style.cursor = new TextureRegionDrawable(new TextureRegion(cursor));
		style.selection = new TextureRegionDrawable(new TextureRegion(selected));
		// TODO: More style variables
		TextField field = new TextField(text, style);
		// field.setMessageText(text);
		field.setAlignment(Align.bottomLeft);
		return field;
	}

	public void dispose() {
		generator = null;
		vo = null;
		skin = null;
		cursor = null;
		selected = null;
		background = null;
		buttonUp = null;
		buttonDown = null;
		checkBoxOff = null;
		checkBoxOn = null;
	}
}
