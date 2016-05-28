package com.germancoding.taleofpecora;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Logger;

public class ConfigStorage {

	public static final String TAG = "[ConfigStorage]";

	public static String PROFILE_NAME = "default"; // TODO
	Logger logger;

	File storagePath;
	File userStats;
	JSONObject userStatsObject;

	private int lastSuccessfullLevel = -1;

	public ConfigStorage() {
		logger = new Logger(TAG);

		if (Gdx.app == null) { // Application not initialized yet
			logger = new Logger(TAG) { // Setup custom logger because LibGDX logging system is not available yet
				@Override
				public void info(String message) {
					System.out.println(message);
				}

				@Override
				public void error(String message) {
					System.out.println(message);
				}

				@Override
				public void error(String message, Throwable exception) {
					System.out.println(message + " - " + exception);
				}

				@Override
				public void debug(String message) {
					System.out.println(message);
				}
			};
		}
		try {
			String appdata = System.getenv("APPDATA");
			File folder = new File(appdata);

			if (!folder.isDirectory()) {
				logger.debug("Appdata folder not found, falling back to LibGDX default storage.");
				if (Gdx.files != null) {
					folder = new File(Gdx.files.getLocalStoragePath());
				}

				if (!folder.isDirectory()) {
					logger.debug("No storage path found, falling back to system default user directory.");
					folder = new File(System.getProperty("user.dir"));
					if (!folder.isDirectory()) {
						throw new IOException("No storage available (non-existent paths)");
					}
				}
			}
			storagePath = new File(folder + "/TaleOfPecora");
			storagePath.mkdirs();
			logger.debug("Using storage folder: " + storagePath);

			userStats = new File(storagePath, "stats.json");
			if (!userStats.exists()) {
				makeDefault();

			}
			userStatsObject = readJSON(userStats);
		} catch (IOException e) {
			logger.error("IOException while initializing the data storage", e);
			e.printStackTrace();
		} catch (JSONException e) {
			logger.error("Failed to parse JSON from the data storage", e);
			e.printStackTrace();
		}

	}

	private void makeDefault() throws IOException {
		userStats.createNewFile();
		FileWriter writer = new FileWriter(userStats);
		writer.write("{}");
		writer.close();
	}

	public void loadGraphicSettings() {
		Constants.GAME_WIDTH = userStatsObject.optInt(PROFILE_NAME + ".graphic.width", Constants.GAME_WIDTH);
		Constants.GAME_HEIGHT = userStatsObject.optInt(PROFILE_NAME + ".graphic.height", Constants.GAME_HEIGHT);
		Constants.GL3_0 = userStatsObject.optBoolean(PROFILE_NAME + ".graphic.gl3", Constants.GL3_0);
		Constants.TARGET_FPS = userStatsObject.optInt(PROFILE_NAME + ".graphic.fps", Constants.TARGET_FPS);
		Constants.FULLSCREEN = userStatsObject.optBoolean(PROFILE_NAME + ".graphic.fullscreen", Constants.FULLSCREEN);
		Constants.VSYNC = userStatsObject.optBoolean(PROFILE_NAME + ".graphic.vsync", Constants.VSYNC);
		Constants.MSAA = userStatsObject.optInt(PROFILE_NAME + ".graphic.msaa", Constants.MSAA);
	}

	public void loadProgressSettings() {
		lastSuccessfullLevel = userStatsObject.optInt(PROFILE_NAME + ".progress.level", -1);
	}
	
	public void load() {
		loadGraphicSettings();
		loadProgressSettings();
	}

	public void updateProgressSettings() {
		update(PROFILE_NAME + ".progress.level", lastSuccessfullLevel);
	}

	public void updateGraphicSettings() {
		update(PROFILE_NAME + ".graphic.width", Constants.GAME_WIDTH);
		update(PROFILE_NAME + ".graphic.height", Constants.GAME_HEIGHT);
		update(PROFILE_NAME + ".graphic.gl3", Constants.GL3_0);
		update(PROFILE_NAME + ".graphic.fps", Constants.TARGET_FPS);
		update(PROFILE_NAME + ".graphic.fullscreen", Constants.FULLSCREEN);
		update(PROFILE_NAME + ".graphic.vsync", Constants.VSYNC);
		update(PROFILE_NAME + ".graphic.msaa", Constants.MSAA);
	}

	public void update(String key, Object value) {
		try {
			userStatsObject.putOpt(key, value);
		} catch (JSONException e) {
			logger.error("Failed to update JSON. The requested operation was: key: " + key + " | value: " + value, e);
			e.printStackTrace();
		}
	}

	public void update() {
		updateGraphicSettings();
		updateProgressSettings();
	}

	public void save() throws IOException {
		FileWriter writer = new FileWriter(userStats);
		writer.write(userStatsObject.toString());
		writer.close();
	}

	public void saveAsync() {
		TaleOfPecora.instance.scheduler.runTask(new Runnable() {

			@Override
			public void run() {
				try {
					save();
				} catch (IOException e) {
					logger.error("Failed to save JSON to the data storage (async operation)", e);
					e.printStackTrace();
				}
			}
		}, 0);
	}

	private JSONObject readJSON(File f) throws IOException, JSONException {
		Scanner scanner = new Scanner(f);
		String input = "";
		while (scanner.hasNextLine()) {
			input += scanner.nextLine();
		}
		scanner.close();
		return new JSONObject(input);
	}

	public int getLastSuccessfullLevel() {
		return this.lastSuccessfullLevel;
	}

	public void setLastSuccessfullLevel(int lastSuccessfullLevel) {
		this.lastSuccessfullLevel = lastSuccessfullLevel;
	}

}
