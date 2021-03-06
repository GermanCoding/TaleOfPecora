/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora.stages;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.DialogCallback;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.UIHelper;
import com.germancoding.taleofpecora.Utils;
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
	final CheckBox openglField;
	final TextField fpsField;
	final CheckBox fullField;

	private boolean requireRestart;
	private int newWidth;
	private int newHeight;
	private boolean useOpenGL3;
	private int newFPS;
	private boolean fullscreen;

	public SettingsStage(IResourceRetriever ir) {
		final SettingsStage stage = this;
		info = ir.getProjectVO();
		helper = new UIHelper();
		helper.initAdditionalResources(info);
		Gdx.input.setInputProcessor(this);

		CompositeItemVO item = info.libraryItems.get("settingsMenu");
		menuActor = new CompositeActor(item, ir);
		float scale = 1f / TaleOfPecora.instance.camera.zoom;
		Gdx.app.debug("[SettigsStage]", "Width:" + getWidth());
		Gdx.app.debug("[SettigsStage]", "Height:" + getHeight());
		Gdx.app.debug("[SettigsStage]", "menuActor width: " + menuActor.getWidth());
		Gdx.app.debug("[SettigsStage]", "menuActor height: " + menuActor.getHeight());
		menuActor.setScale(scale);
		float x = (getWidth() / 2) - (menuActor.getWidth() * scale / 2);
		float y = (getHeight() / 2) - (menuActor.getHeight() * scale / 2);
		Gdx.app.debug("[SettigsStage]", "menuActor width scaled : " + menuActor.getWidth() * scale);
		Gdx.app.debug("[SettigsStage]", "menuActor height scaled : " + menuActor.getHeight() * scale);
		Gdx.app.debug("[SettigsStage]", "x:" + x);
		Gdx.app.debug("[SettigsStage]", "y:" + y);
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		menuActor.setPosition(x, y);
		menuActor.setTouchable(Touchable.childrenOnly); // The actor is the full menu screen, only the parts (buttons...) should be clickable/touchable

		// === INIT VARS === //
		newWidth = Constants.GAME_WIDTH;
		newHeight = Constants.GAME_HEIGHT;
		useOpenGL3 = Constants.GL3_0;
		newFPS = Constants.TARGET_FPS;
		fullscreen = Constants.FULLSCREEN;

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

		// === GL3 CheckBox ===
		Actor glActor = menuActor.getItem("openGL");
		openglField = helper.createCheckBox("", (int) (25 * scale), Color.BLACK);
		// openglField.setDebug(true);
		openglField.setChecked(useOpenGL3);
		openglField.setSize(glActor.getWidth() * scale * glActor.getScaleX(), glActor.getHeight() * scale * glActor.getScaleY());
		openglField.setX(menuActor.getX() + glActor.getX() * scale);
		openglField.setY(menuActor.getY() + glActor.getY() * scale);
		openglField.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (openglField.isChecked()) {
					TaleOfPecora.instance.showConfirmDialog("Experimental feature", "The usage of OpenGL 3+ (GLES 3.0) functions is experimental and could cause bugs. If you encounter any issues, try turning this off.", new DialogCallback() {

						@Override
						public void run() {
							boolean result = (Boolean) this.getResult();
							if (!result) {
								useOpenGL3 = false;
								openglField.setChecked(false);
							}
						}
					}, stage);
				}
				useOpenGL3 = openglField.isChecked();
			}
		});

		// === FPS TextField ===
		Actor fpsActor = menuActor.getItem("fps");
		fpsField = helper.createTextField("" + newFPS, (int) (25 * scale), Color.BLACK); // new TextField("TEST", new TextField.TextFieldStyle());
		// fpsField.setDebug(true);
		fpsField.setSize(fpsActor.getWidth() * scale * fpsActor.getScaleX(), fpsActor.getHeight() * scale * fpsActor.getScaleY());
		fpsField.setX(menuActor.getX() + fpsActor.getX() * scale + 5 * scale);
		fpsField.setY(menuActor.getY() + fpsActor.getY() * scale);
		fpsField.setTextFieldFilter(new TextFieldFilter() {

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
		fpsField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ENTER) {
					final int result = parseInt(fpsField.getText());
					if (result >= 0) {
						if (result < 120 && result >= 60) {
							TaleOfPecora.instance.showYesNoDialog("Unrecommended Setting", "It is highly recommended to set the preferred framerate to something higher than 120 (or 0 for unlimited) to prevent stuttering. Do you really want to set the target FPS to " + result + "?", new DialogCallback() {

								@Override
								public void run() {
									boolean resultB = (Boolean) this.getResult();
									if (resultB) {
										newFPS = result;
									}
									fpsField.setText(newFPS + "");
								}
							}, stage);
						} else if (result < 60 && result > 0) {
							TaleOfPecora.instance.showConfirmDialog("Unrecommended Setting", "Framerates below 60 are not allowed at the moment. Why would you want that either?", new DialogCallback() {

								@Override
								public void run() {
									newFPS = 60;
									fpsField.setText(newFPS + "");
								}
							}, stage);
						} else {
							newFPS = result;
						}
					} else {
						fpsField.setText(newFPS + "");
					}
					return true;
				}
				return false;
			}
		});

		// === Fullscreen CheckBox ===
		Actor fullActor = menuActor.getItem("fullscreen");
		fullField = helper.createCheckBox("", (int) (25 * scale), Color.BLACK);
		// fullField.setDebug(true);
		fullField.setChecked(fullscreen);
		fullField.setSize(fullActor.getWidth() * scale * fullActor.getScaleX(), fullActor.getHeight() * scale * fullActor.getScaleY());
		fullField.setX(menuActor.getX() + fullActor.getX() * scale);
		fullField.setY(menuActor.getY() + fullActor.getY() * scale);
		fullField.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (fullField.isChecked()) {
					if (Gdx.graphics.supportsDisplayModeChange()) {
						DisplayMode mode = getBestFullscreenMode();
						if(!Gdx.graphics.setDisplayMode(mode.width, mode.height, true)) {
							 requireRestart = true;
						}
						// Gdx.graphics.setFullscreenMode(mode);
						// Gdx.graphics.setDisplayMode(mode.getWidth(), mode.getHeight(), true);
					}
				} else {
					if (Gdx.graphics.supportsDisplayModeChange()) {
						if(!Gdx.graphics.setDisplayMode(newWidth, newHeight, false)) {
							 requireRestart = true;
						}
						// Gdx.graphics.setWindowedMode(newWidth, newHeight);
						// Gdx.graphics.setDisplayMode(newWidth, newHeight, false);
					}
				}
				fullscreen = fullField.isChecked();
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
						goBack();
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
		addActor(fullField);
		addActor(openglField);
		addActor(fpsField);

		this.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Constants.GAME_BACK) {
					// TODO: Save settings
					TaleOfPecora.instance.settings = null;
					goBack();
					return true;
				}
				return false;
			}
		});
	}

	public java.awt.DisplayMode getBestFullscreenMode2() {
		// DisplayMode mode = Gdx.graphics.getDesktopDisplayMode();
		int height, width;
		/*
		 * if (mode != null) {
		 * height = mode.height;
		 * width = mode.width;
		 * } else {
		 */
		height = newHeight;
		width = newWidth;
		// }

		int closestHeight = 0, closestWidth = 0;

		for (DisplayMode suppported : Gdx.graphics.getDisplayModes()) {
			if (height == suppported.height && width == suppported.width) {
				// System.out.println("Perfect match found: height = " + height + ", width = " + width);
				closestHeight = height;
				closestWidth = width;
				break;
			} else {
				if (closestHeight == 0 || closestWidth == 0) {
					closestHeight = suppported.height;
					closestWidth = suppported.width;
				} else if (Utils.bestMatch(suppported.height, height, closestHeight, height) || Utils.bestMatch(suppported.width, width, closestWidth, width)) {
					// TODO: This algorithm is not perfect
					// If this is true than this display mode is the best we found (yet).
					closestHeight = suppported.height;
					closestWidth = suppported.width;
				}
			}
		}

		if (closestHeight == 0 || closestWidth == 0) {
			return null;
		}
		Gdx.app.debug("[SettigsStage]", "Using this display mode: height = " + closestHeight + ", width = " + closestWidth);
		return new java.awt.DisplayMode(closestWidth, closestHeight, 0, 0); // Only a dummy object, ignore refresh & bit values
	}

	public DisplayMode getBestFullscreenMode() {
		int height, width;
		height = newHeight;
		width = newWidth;

		int bestDelta = Integer.MAX_VALUE;
		DisplayMode best = null;

		for (DisplayMode supported : Gdx.graphics.getDisplayModes()) {
			if (height == supported.height && width == supported.width) {
				return supported;
			} else {
				int deltaH = Utils.differ(supported.height, height);
				int deltaW = Utils.differ(supported.width, width);
				int delta = deltaH + deltaW;
				if (delta < bestDelta) {
					best = supported;
				}
			}
		}

		return best;
	}

	public void reparseSettingsIgnoreWrong() {
		newWidth = parseIntNoError(textField.getText(), newWidth);
		newHeight = parseIntNoError(heightField.getText(), newHeight);
		useOpenGL3 = openglField.isChecked();
		newFPS = parseIntNoError(fpsField.getText(), newFPS);
		fullscreen = fullField.isChecked();
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
		if (Constants.GL3_0 != useOpenGL3) {
			requireRestart = true;
			Constants.GL3_0 = useOpenGL3;
		}
		if (Constants.TARGET_FPS != newFPS) {
			requireRestart = true;
			Constants.TARGET_FPS = newFPS;
		}
		if (Constants.FULLSCREEN != fullscreen) {
			if (fullField.isChecked()) {
				if (Gdx.graphics.supportsDisplayModeChange()) {
					DisplayMode mode = getBestFullscreenMode();
					if(!Gdx.graphics.setDisplayMode(mode.width, mode.height, true)) {
						requireRestart = true;
					}
					/*
					if(!Gdx.graphics.setFullscreenMode(mode)) {
						requireRestart = true;
					}
					*/
					// Gdx.graphics.setDisplayMode(mode.getWidth(), mode.getHeight(), true);
				} else {
					requireRestart = true;
				}
			} else {
				if (Gdx.graphics.supportsDisplayModeChange()) {
					if(!Gdx.graphics.setDisplayMode(newWidth, newHeight, false)) {
						 requireRestart = true;
					}
					/* 
					if(!Gdx.graphics.setWindowedMode(newWidth, newHeight)) {
						 requireRestart = true;
					 }
					 */
					// Gdx.graphics.setDisplayMode(newWidth, newHeight, false);
				} else {
					requireRestart = true;
				}
			}
			Constants.FULLSCREEN = fullscreen;
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
						goBack();
					}
				}
			}, this);
		} else {
			TaleOfPecora.instance.settings = null;
			goBack();
		}
	}

	public void goBack() {
		if (TaleOfPecora.instance.currentScene.sceneName.equalsIgnoreCase("emptyScene")) {
			TaleOfPecora.instance.showMainMenu();
		} else {
			TaleOfPecora.instance.renderMenu = false;
			if (TaleOfPecora.instance.activePause) {
				TaleOfPecora.instance.setActivePause(false);
			}
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
			if (i < 0)
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
