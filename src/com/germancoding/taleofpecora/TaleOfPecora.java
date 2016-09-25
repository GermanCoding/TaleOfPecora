/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

import java.util.Iterator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.germancoding.taleofpecora.entity.Carrot;
import com.germancoding.taleofpecora.entity.CarrotSystem;
import com.germancoding.taleofpecora.entity.Knight;
import com.germancoding.taleofpecora.entity.KnightSystem;
import com.germancoding.taleofpecora.entity.Platform;
import com.germancoding.taleofpecora.entity.PlatformSystem;
import com.germancoding.taleofpecora.entity.Sheep;
import com.germancoding.taleofpecora.stages.BackgroundStage;
import com.germancoding.taleofpecora.stages.LevelFinishedStage;
import com.germancoding.taleofpecora.stages.LevelSelectStage;
import com.germancoding.taleofpecora.stages.MainMenuStage;
import com.germancoding.taleofpecora.stages.PauseMenuStage;
import com.germancoding.taleofpecora.stages.SettingsStage;
import com.germancoding.taleofpecora.stages.UIStage;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.SceneVO;
import com.uwsoft.editor.renderer.data.SpriteAnimationVO;
import com.uwsoft.editor.renderer.systems.render.Overlap2dRenderer;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

public class TaleOfPecora extends ApplicationAdapter {

	private static final String TAG = "[TaleOfPecora]";

	public static TaleOfPecora instance;

	private boolean paused;

	// private WorldController worldController;
	// private WorldRenderer worldRenderer;

	private SceneLoader sceneLoader;
	private Engine engine;
	public SceneVO currentScene;
	private ItemWrapper root;
	public Sheep player;
	public OrthographicCamera camera;
	public UIStage gui;
	public MainMenuStage mainMenu;
	public SettingsStage settings;
	public LevelFinishedStage levelComplete;
	public LevelSelectStage levelSelect;
	public World world;
	public Scheduler scheduler;
	private boolean doReset;
	public SoundController sound;
	public boolean renderMenu;
	public Level currentLevel;
	public long startTime;
	public BackgroundStage backgroundStage;
	public PauseMenuStage pauseMenu;
	public ConfigStorage config;
	public UIHelper helper;
	public Object mainThreadLock = new Object();
	public Runnable restartRun;
	public boolean activePause;

	public TaleOfPecora() {
		if (instance != null)
			throw new IllegalAccessError("TaleOfPecora is already initialized!");
		instance = this;
	}

	public float[] levels = { 190f, 100f }; // Length of the different levels

	public InputProcessor mainInputProcessor = new InputProcessor() {

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
			return false;
		}

		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Constants.GAME_PAUSE) { // We can't use a switch here
				setActivePause(!activePause);
				return true;
			} else if (keycode == Constants.GAME_BACK) {
				if (!isPaused() && !activePause && !renderMenu && currentLevel != null) {
					setActivePause(true);
					showPauseMenu();
					return true;
				}
			}
			return false;
		}
	};

	@Override
	public void create() {
		super.create();
		// Set Libgdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.app.log(TAG, "Running on: " + Gdx.app.getType());
		/*
		 * System.out.println("=== DEBUG USER SYSTEM ===");
		 * System.out.println("System-Enviroment listing:");
		 * for(String key: System.getenv().keySet())
		 * {
		 * System.out.println(key + " ::: " + System.getenv(key));
		 * }
		 * System.out.println("=== DEBUG USER SYSTEM END ===");
		 */

		Gdx.app.addLifecycleListener(new LifecycleListener() {

			@Override
			public void resume() {
				instance.resume();
			}

			@Override
			public void pause() {
				instance.pause();
			}

			@Override
			public void dispose() {
				instance.dispose();
			}
		});
		if (config == null) { // If the system is fast we're already ready and don't need to wait.
			// Wait until we're fully ready to start (safer) but continue after 5 seconds if we don't get a notify()
			synchronized (this) {
				try {
					this.wait(5000);
				} catch (InterruptedException e) {
					;
				}
			}
		}

		Gdx.input.setInputProcessor(mainInputProcessor);
		if (config == null) { // Config should be already up if we had no failure
			Gdx.app.debug(TAG, "Setting up own config because the implementation forgot us :(");
			config = new ConfigStorage();
		}
		config.load();
		/*
		 * scheduler = new Scheduler();
		 * System.out.println(Gdx.app.getGraphics().getWidth());
		 * Constants.GAME_HEIGHT = Gdx.app.getGraphics().getWidth();
		 * Constants.GAME_WIDTH = Gdx.app.getGraphics().getHeight();
		 * Constants.FULLSCREEN = Gdx.app.getGraphics().isFullscreen();
		 * config.updateGraphicSettings();
		 * config.saveAsync();
		 */
		showMainMenu();
		/*
		 * // Load assets
		 * Assets.instance.init(new AssetManager());
		 * // Initialize controller and renderer
		 * worldController = new WorldController();
		 * worldRenderer = new WorldRenderer(worldController);
		 */
	}

	public void showMainMenu() {
		setPaused(true);
		Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth() / 80f, Gdx.app.getGraphics().getHeight() / 80f);
		sceneLoader = new SceneLoader();
		setCurrentScene(sceneLoader.loadScene("emptyScene", viewport));
		camera = (OrthographicCamera) viewport.getCamera();
		camera.zoom = calculateZoom(viewport.getScreenWidth(), viewport.getScreenHeight());
		mainMenu = new MainMenuStage(sceneLoader.getRm());
		backgroundStage = new BackgroundStage(sceneLoader.getRm());
		helper = new UIHelper();
		renderMenu = true;
		engine = sceneLoader.getEngine();
		setPaused(false);
	}

	// This method should only be called when there is a running level
	public void showPauseMenu() {
		setPaused(true);
		pauseMenu = new PauseMenuStage(sceneLoader.getRm());
		renderMenu = true;
		setPaused(false);
	}

	public void showSettings() {
		setPaused(true);
		Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth() / 80f, Gdx.app.getGraphics().getHeight() / 80f);
		sceneLoader = new SceneLoader();
		setCurrentScene(sceneLoader.loadScene("emptyScene", viewport));
		camera = (OrthographicCamera) viewport.getCamera();
		camera.zoom = calculateZoom(viewport.getScreenWidth(), viewport.getScreenHeight());
		settings = new SettingsStage(sceneLoader.getRm());
		backgroundStage = new BackgroundStage(sceneLoader.getRm());
		helper = new UIHelper();
		renderMenu = true;
		engine = sceneLoader.getEngine();
		setPaused(false);
	}

	public void showLevelSelect() {
		setPaused(true);
		Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth() / 80f, Gdx.app.getGraphics().getHeight() / 80f);
		sceneLoader = new SceneLoader();
		setCurrentScene(sceneLoader.loadScene("emptyScene", viewport));
		camera = (OrthographicCamera) viewport.getCamera();
		camera.zoom = calculateZoom(viewport.getScreenWidth(), viewport.getScreenHeight());
		levelSelect = new LevelSelectStage(sceneLoader.getRm());
		backgroundStage = new BackgroundStage(sceneLoader.getRm());
		helper = new UIHelper();
		renderMenu = true;
		engine = sceneLoader.getEngine();
		setPaused(false);
	}

	public void loadLevel() {
		int level = config.getLastSuccessfullLevel() + 1;
		if (!isValidLevel(level)) {
			level--;
		}
		loadLevel(level); // TODO: This should load the newest level for the user (highest unlocked or lastPlayedLevel + 1)
		// sceneLoader.getBatch().setProjectionMatrix(camera.combined);
		// sceneLoader.world.setGravity(new Vector2(0, -Constants.GRAVITY));
	}

	public boolean isValidLevel(int level) {
		return level < levels.length; // TODO: Check if unlocked?
	}

	public void loadLevel(int level) {
		setPaused(true); // Overlap2D engine should not be updated [by render()] whilst we are initializing it
		activePause = false; // New engine will be created, reset active pause

		String levelname = "level" + level;
		if (level == 0) {
			levelname = "MainScene";
		} else if (level < 10) {
			levelname = "level0" + level;
		}
		if (!isValidLevel(level)) {
			setPaused(false);
			Gdx.app.log(TAG, levelname + " does not exist, ignoring load command.");
			return;
		}

		Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth() / 80f, Gdx.app.getGraphics().getHeight() / 80f); // 80f seems to fit because 80 pixels per world unit
		sceneLoader = new SceneLoader();

		setCurrentScene(sceneLoader.loadScene(levelname, viewport));
		world = sceneLoader.world;
		player = new Sheep(world, this);
		root = new ItemWrapper(sceneLoader.getRoot());
		root.getChild("sheep").addScript(player);
		camera = (OrthographicCamera) viewport.getCamera();
		camera.zoom = calculateZoom(viewport.getScreenWidth(), viewport.getScreenHeight());
		gui = new UIStage(sceneLoader.getRm());
		sceneLoader.addComponentsByTagName("knight", Knight.class);
		sceneLoader.addComponentsByTagName("carrot", Carrot.class);
		sceneLoader.addComponentsByTagName("platform", Platform.class);
		sceneLoader.addComponentsByTagName("button", ButtonComponent.class);
		engine.addSystem(new KnightSystem());
		engine.addSystem(new CarrotSystem());
		engine.addSystem(new PlatformSystem());
		scheduler = new Scheduler();
		sound = new SoundController();
		backgroundStage = new BackgroundStage(sceneLoader.getRm());
		helper = new UIHelper();
		engine = sceneLoader.getEngine();

		player.firstFrame = true;
		currentLevel = new Level(level, levels[level]);
		Gdx.input.setInputProcessor(mainInputProcessor);

		// Reset menus
		if (mainMenu != null) {
			mainMenu.dispose();
		}
		mainMenu = null;
		if (levelComplete != null) {
			levelComplete.dispose();
		}
		levelComplete = null;
		if (settings != null) {
			settings.dispose();
		}
		if (levelSelect != null) {
			levelSelect.dispose();
		}
		if (pauseMenu != null) {
			pauseMenu.dispose();
		}
		pauseMenu = null;
		levelSelect = null;
		settings = null;
		renderMenu = false;

		startTime = System.currentTimeMillis();

		Gdx.app.log(TAG, levelname + " loaded");
		setPaused(false);
	}

	public void loadCreditsLevel() {
		setPaused(true);
		Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth() / 80f, Gdx.app.getGraphics().getHeight() / 80f);
		sceneLoader = new SceneLoader();
		setCurrentScene(sceneLoader.loadScene("creditsScene", viewport));
		camera = (OrthographicCamera) viewport.getCamera();
		camera.zoom = calculateZoom(viewport.getScreenWidth(), viewport.getScreenHeight());
		root = new ItemWrapper(sceneLoader.getRoot());
		ItemWrapper credits = root.getChild("credits");
		credits.addScript(new Credits(credits));
		backgroundStage = new BackgroundStage(sceneLoader.getRm());
		engine = sceneLoader.getEngine();
		setPaused(false);
	}

	@Override
	public void render() {
		if (paused) {
			// When fully paused, we never render (activePause can be used to pause the game but still providing a full render of the screen). The only thing that can be done while paused is to reset the level.
			if (doReset) {
				doReset = false;
				int level = this.currentLevel.getId();
				dispose();
				synchronized (this) {
					try {
						this.wait(10);
					} catch (InterruptedException e) {
						;
					}
				}
				loadLevel(level);
				// renderBackground(); // Attempting to fix high delta time, apparently doesn't work
				paused = false;
				Gdx.app.debug(TAG, "Level reset done");
			}
			return;
		}

		/*
		 * Rendering currently has multiple stages - elements are drawn on top of each other. We render in this order:
		 * 0. Clear the screen - the "default color" is "Cornflower Blue" (if nothing else is drawn this color will be visible)
		 * 1. Draw background on the full screen (backgroundStage)
		 * 2. Draw the menu stages if any and if in the menu-rendering mode
		 * 3. Update camera
		 * 4. Update and draw everything game related [engine.update()]
		 * 5. Draw GUI stage
		 */

		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		// Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		backgroundStage.act();
		backgroundStage.draw();
		// renderBackground();

		if (renderMenu) {
			engine.update(Gdx.graphics.getDeltaTime());

			if (mainMenu != null) {
				mainMenu.act();
				mainMenu.draw();
			}
			if (levelComplete != null) {
				levelComplete.act();
				levelComplete.draw();
			}
			if (settings != null) {
				settings.act();
				settings.draw();
			}
			if (levelSelect != null) {
				levelSelect.act();
				levelSelect.draw();
			}
			if (pauseMenu != null) {
				pauseMenu.act();
				pauseMenu.draw();
			}
		} else {
			// Render game world to screen
			// worldRenderer.render();
			// Update game world by the time that has passed
			// since last rendered frame.
			// worldController.update(Gdx.graphics.getDeltaTime());
			float positionX = player.getX() + player.getCenter();
			// backgroundSprite.draw(background, camera.position.x, camera.position.y, Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight());
			camera.position.set(positionX, player.getY(), 0);

			/*
			 * THIS IS GOOD DEBUG CODE - IF YOU HAVE NPE's DUE TO CORRUPT ENTITIES IN PhysicsSystem, USE THIS TO EXTRACT THE ENTITY-ID!
			 * System.out.println("Listing entities without body (no polygons mostly) in PhysicsSystem:");
			 * for (Entity e : engine.getSystem(PhysicsSystem.class).getEntities()) {
			 * try {
			 * // processBody() is a protected method, unaccessible for us. Doesn't matter, we have reflection hacks! Performance doesn't matter, this is debug code.
			 * Method method = engine.getSystem(PhysicsSystem.class).getClass().getDeclaredMethod("processBody", Entity.class);
			 * method.setAccessible(true); // Love that call. Private, protected, final? Not with reflection!
			 * method.invoke(engine.getSystem(PhysicsSystem.class), e);
			 * } catch (NoSuchMethodException e1) {
			 * e1.printStackTrace();
			 * } catch (SecurityException e1) {
			 * e1.printStackTrace();
			 * } catch (IllegalAccessException e1) {
			 * e1.printStackTrace();
			 * } catch (IllegalArgumentException e1) {
			 * e1.printStackTrace();
			 * } catch (InvocationTargetException e1) {
			 * e1.printStackTrace();
			 * }
			 * PhysicsBodyComponent physicsBodyComponent = ComponentRetriever.get(e, PhysicsBodyComponent.class);
			 * Body body = physicsBodyComponent.body;
			 * if(body == null)
			 * {
			 * MainItemComponent mainItem = ComponentRetriever.get(e, MainItemComponent.class);
			 * System.out.println("No body: " + mainItem.uniqueId);
			 * }
			 * }
			 */
			engine.update(Gdx.graphics.getDeltaTime());
			gui.act();
			gui.draw();
		}
	}

	public void setActivePause(boolean on) {
		activePause = on;
		Iterator<EntitySystem> entitySystems = engine.getSystems().iterator();
		while (entitySystems.hasNext()) {
			EntitySystem system = entitySystems.next();
			// System.out.println(system);
			if (system instanceof Overlap2dRenderer)
				continue; // Never touch the renderer
			system.setProcessing(!on);
		}
		Gdx.input.setInputProcessor(mainInputProcessor);
	}

	public void showYesNoDialog(String title, String text, final DialogCallback callback, Stage stage) {
		float scale = 1f / TaleOfPecora.instance.camera.zoom;
		Skin skin = helper.makeSkin((int) (60 * scale), Color.BLACK, Color.DARK_GRAY);
		Label label = new Label(text, skin);
		label.setWrap(true);
		label.setFontScale(.8f);
		label.setAlignment(Align.center);

		Dialog dialog = new Dialog("", skin, "dialog") {
			protected void result(Object object) {
				callback.setResult(object);
				callback.run();
			}
		};

		dialog.padTop(50).padBottom(50);
		dialog.getContentTable().add(label).width(Gdx.graphics.getWidth() / 2f).row();
		dialog.getButtonTable().padTop(50);

		TextButton dbutton = new TextButton("Yes", skin, "dialog");
		dialog.button(dbutton, true);

		dbutton = new TextButton("No", skin, "dialog");
		dialog.button(dbutton, false);
		dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
		dialog.invalidateHierarchy();
		dialog.invalidate();
		dialog.layout();
		dialog.show(stage);

		/*
		 * - old code - no wrap
		 * new Dialog("", helper.makeSkin(30, Color.BLACK, Color.DARK_GRAY), "dialog") {
		 * protected void result(Object object) {
		 * callback.setResult(object);
		 * callback.run();
		 * }
		 * }.text(text).button("Yes", true).button("No", false).key(Keys.ENTER, true).key(Keys.ESCAPE, false).show(stage);
		 */
	}

	public void showConfirmDialog(String title, String text, final DialogCallback callback, Stage stage) {
		float scale = 1f / TaleOfPecora.instance.camera.zoom;
		Skin skin = helper.makeSkin((int) (60 * scale), Color.BLACK, Color.DARK_GRAY);
		Label label = new Label(text, skin);
		label.setWrap(true);
		label.setFontScale(.8f);
		label.setAlignment(Align.center);

		Dialog dialog = new Dialog("", skin, "dialog") {
			protected void result(Object object) {
				callback.setResult(object);
				callback.run();
			}
		};

		dialog.padTop(50).padBottom(50);
		dialog.getContentTable().add(label).width(Gdx.graphics.getWidth() / 2f).row();
		dialog.getButtonTable().padTop(50);

		TextButton dbutton = new TextButton("OK", skin, "dialog");
		dialog.button(dbutton, true);
		dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
		dialog.invalidateHierarchy();
		dialog.invalidate();
		dialog.layout();
		dialog.show(stage);
		/*
		 * - old code - no wrap
		 * new Dialog("", helper.makeSkin(30, Color.BLACK, Color.DARK_GRAY), "dialog") {
		 * protected void result(Object object) {
		 * callback.setResult(object);
		 * callback.run();
		 * }
		 * }.text(text).button("OK", true).key(Keys.ENTER, true).key(Keys.ESCAPE, false).show(stage);
		 */
	}

	@Deprecated
	public void renderBackground() {
		Gdx.app.error(TAG, "deprecated background render was called!");
		sceneLoader.getBatch().begin();
		// sceneLoader.getBatch().draw(background, camera.position.x - (camera.viewportWidth * camera.zoom / 2), camera.position.y - (camera.viewportHeight * camera.zoom / 2), camera.viewportWidth * camera.zoom, camera.viewportHeight * camera.zoom);
		sceneLoader.getBatch().end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.viewportWidth = width / 80f;
		camera.viewportHeight = height / 80f;
		camera.zoom = calculateZoom(width, height);
		// worldRenderer.resize(width, height);
	}

	public float calculateZoom(int width, int height) {
		float zoomWidth = Constants.BASE_WIDTH / (float) width;
		float zoomHeight = Constants.BASE_HEIGHT / (float) height;
		return zoomWidth > zoomHeight ? zoomWidth : zoomHeight;
	}

	@Override
	public void resume() {
		super.resume();
		// Assets.instance.init(new AssetManager());
		setPaused(false);
	}

	@Override
	public void dispose() {
		super.dispose();
		// Assets.instance.dispose();
		// worldRenderer.dispose();
		if (player != null)
			player.dispose();
		if (gui != null)
			gui.dispose();
		if (world != null)
			world.dispose();
		if (scheduler != null)
			scheduler.shutdown();
		if (sound != null)
			sound.dispose();
		if (backgroundStage != null)
			backgroundStage.dispose();
		if (settings != null) {
			settings.dispose();
		}
		if (levelComplete != null) {
			levelComplete.dispose();
		}
		if (mainMenu != null) {
			mainMenu.dispose();
		}
		if (levelSelect != null) {
			levelSelect.dispose();
		}
		if (helper != null) {
			helper.dispose();
		}
		if(engine != null) {
			engine.removeAllEntities();
		}
		engine = null;
		helper = null;
		levelSelect = null;
		mainMenu = null;
		levelComplete = null;
		settings = null;
		backgroundStage = null;
		scheduler = null;
		player = null;
		sceneLoader = null;
		currentScene = null;
		root = null;
		camera = null;
		gui = null;
		world = null;
		sound = null;
	}

	@Override
	public void pause() {
		super.pause();
		if (!isPaused()) {
			setPaused(true);
			Gdx.app.debug(TAG, "Game paused via listener");
		}
	}

	public boolean isPaused() {
		return this.paused;
	}

	public void setPaused(boolean paused) {
		if (sound != null) {
			if (paused)
				sound.pause();
			else
				sound.resume();
		}
		this.paused = paused;
	}

	public SceneVO getCurrentScene() {
		return currentScene;
	}

	public void setCurrentScene(SceneVO currentScene) {
		this.currentScene = currentScene;
	}

	public SceneLoader getSceneLoader() {
		return this.sceneLoader;
	}

	public void setSceneLoader(SceneLoader sceneLoader) {
		this.sceneLoader = sceneLoader;
	}

	public Entity loadAnimation(String animationName, int fps, PlayMode mode) {
		CompositeItemVO vo = TaleOfPecora.instance.getSceneLoader().loadVoFromLibrary(animationName);
		SpriteAnimationVO animVO = (SpriteAnimationVO) vo.composite.getAllItems().get(0);
		animVO.fps = fps;

		switch (mode) {
		case NORMAL:
			animVO.playMode = 0;
			break;
		case LOOP:
			animVO.playMode = 2;
			break;
		case LOOP_PINGPONG:
			animVO.playMode = 4;
			break;
		case LOOP_RANDOM:
			animVO.playMode = 5;
			break;
		case LOOP_REVERSED:
			animVO.playMode = 3;
			break;
		case REVERSED:
			animVO.playMode = 1;
			break;
		default:
			animVO.playMode = 6;
			break;
		}

		return createEntity(vo);
	}

	public Entity createEntity(CompositeItemVO vo) {
		Entity newEntity = TaleOfPecora.instance.getSceneLoader().getEntityFactory().createEntity(TaleOfPecora.instance.getSceneLoader().getRoot(), vo);
		TaleOfPecora.instance.getSceneLoader().getEntityFactory().initAllChildren(TaleOfPecora.instance.getSceneLoader().getEngine(), newEntity, vo.composite);
		return newEntity;
	}

	public void reset() {
		paused = true;
		scheduler.runTask(new Runnable() {

			@Override
			public void run() {
				doReset = true;
			}
		}, 300);
	}

	public void doFullRestart() {
		Gdx.app.postRunnable(restartRun);
	}
}
