package com.germancoding.taleofpecora.stages;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.DialogCallback;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.UIHelper;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class SettingsStage extends Stage {

	ProjectInfoVO info;
	CompositeActor menuActor;
	UIHelper helper;

	final TextField textField;
	final TextField heightField;

	private boolean requireRestart;
	private int newWidth;
	private int newHeight;

	public SettingsStage(IResourceRetriever ir) {
		info = ir.getProjectVO();
		helper = new UIHelper();
		helper.initAdditionalResources(info);
		Gdx.input.setInputProcessor(this);

		CompositeItemVO item = info.libraryItems.get("settingsMenu");
		menuActor = new CompositeActor(item, ir);
		float scale = 1f / TaleOfPecora.instance.camera.zoom;
		menuActor.setScale(scale);
		float x = (getWidth() / 2) - (menuActor.getWidth() * scale / 2);
		float y = (getHeight() / 2) - (menuActor.getHeight() * scale / 2);
		menuActor.setPosition(x, y);
		menuActor.setTouchable(Touchable.childrenOnly); // The actor is the full menu screen, only the parts (buttons...) should be clickable/touchable

		// === INIT VARS === //
		newWidth = Constants.GAME_WIDTH;
		newHeight = Constants.GAME_HEIGHT;

		// === Width TextField ===
		Actor widthActor = menuActor.getItem("width");
		textField = helper.createTextField("" + (int) newWidth, (int) (25 * scale), Color.BLACK); // new TextField("TEST", new TextField.TextFieldStyle());
		// textField.setDebug(true);
		textField.setSize(widthActor.getWidth() * scale * widthActor.getScaleX(), widthActor.getHeight() * scale * widthActor.getScaleY());
		textField.setX(menuActor.getX() + widthActor.getX() * scale + 5 * scale);
		textField.setY(menuActor.getY() + widthActor.getY() * scale);
		textField.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				try {
					Integer.parseInt(Character.toString(c));
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		});
		textField.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// No idea if this could be useful
			}
		});
		textField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER) {
					int result = parseInt(textField.getText());
					if (result > 0)
						newWidth = result;
					else
						textField.setText(newWidth + "");
					return true;
				}
				return false;
			}
		});

		// === Height TextField ===
		Actor heightActor = menuActor.getItem("height");
		heightField = helper.createTextField("" + (int) newHeight, (int) (25 * scale), Color.BLACK); // new TextField("TEST", new TextField.TextFieldStyle());
		// textField.setDebug(true);
		heightField.setSize(heightActor.getWidth() * scale * heightActor.getScaleX(), heightActor.getHeight() * scale * heightActor.getScaleY());
		heightField.setX(menuActor.getX() + heightActor.getX() * scale + 5 * scale);
		heightField.setY(menuActor.getY() + heightActor.getY() * scale);
		heightField.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				try {
					Integer.parseInt(Character.toString(c));
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		});
		heightField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER) {
					int result = parseInt(heightField.getText());
					if (result > 0)
						newHeight = result;
					else
						heightField.setText(newHeight + "");
					return true;
				}
				return false;
			}
		});

		// Click actions
		for (Actor cButton : menuActor.getItemsByTag("button")) {
			final CompositeActor button;
			if (cButton instanceof CompositeActor)
				button = (CompositeActor) cButton;
			else
				continue; // Ignore buttons that aren't composite items. Should not happen unless we have errors somewhere.

			if (button.getName().equalsIgnoreCase("saveButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						reparseSettingsIgnoreWrong();
						applySettings();
					}
				});
			} else if (button.getName().equalsIgnoreCase("cancelButton")) {
				button.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						TaleOfPecora.instance.settings = null;
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
		addActor(heightField);
		addActor(textField);

		this.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Constants.GAME_BACK) {
					// TODO: Save settings
					TaleOfPecora.instance.settings = null;
					TaleOfPecora.instance.showMainMenu();
					return true;
				}
				return false;
			}
		});
	}

	public void reparseSettingsIgnoreWrong() {
		newWidth = parseIntNoError(textField.getText(), newWidth);
		newHeight = parseIntNoError(heightField.getText(), newHeight);
	}

	public void applySettings() {
		if (Constants.GAME_HEIGHT != newHeight) {
			requireRestart = true;
			Constants.GAME_HEIGHT = newHeight;
		}
		if (Constants.GAME_WIDTH != newWidth) {
			requireRestart = true;
			Constants.GAME_WIDTH = newWidth;
		}

		TaleOfPecora.instance.config.updateGraphicSettings(); // We could make this async but this seems better to me
		try {
			TaleOfPecora.instance.config.save();
		} catch (IOException e) {
			TaleOfPecora.instance.showConfirmDialog("Critical failure", "The game was unable to save your settings to disk. Please check this folder: " + TaleOfPecora.instance.config.getStoragePath(), new DialogCallback() {

				@Override
				public void run() {
					; // Nothing
				}
			}, this);
		}

		if (requireRestart) {
			TaleOfPecora.instance.showYesNoDialog("Restart required", "The requested changes require a restart of the game. Would you like to restart now?", new DialogCallback() {

				@Override
				public void run() {
					boolean result = (Boolean) this.getResult();
					if (result) {
						TaleOfPecora.instance.doFullRestart();
					} else {
						TaleOfPecora.instance.settings = null;
						TaleOfPecora.instance.showMainMenu();
					}
				}
			}, this);
		}
	}

	public int parseIntNoError(String s, int defaultValue) {
		try {
			int i = Integer.parseInt(s);
			return i;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public int parseInt(String s) {
		try {
			int i = Integer.parseInt(s);
			if (i <= 0)
				throw new NumberFormatException("Number has to be >= 0");
			return i;
		} catch (NumberFormatException e) {
			TaleOfPecora.instance.showConfirmDialog("Wrong input", "Please enter a valid number", new DialogCallback() {

				@Override
				public void run() {
					// Doesn't matter what the callback is
				}
			}, this);
		}
		return 0;

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
