/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
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
	File graphics;
	JSONObject userStatsObject;
	JSONObject graphicsObject;

	private int lastSuccessfullLevel = -1;

	public ConfigStorage() {
		logger = new Logger(TAG) { // Setup custom logger because LibGDX logging system might not be available yet
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
		try {
			String appdata = System.getenv("APPDATA"); // TODO: Only present on Windows
			if (appdata == null) {
				appdata = ""; // Prevent NPE [new File(null) = NPE]
			}
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
				makeDefault(userStats);

			}
			userStatsObject = readJSON(userStats);

			graphics = new File(storagePath, "graphics.json");
			if (!graphics.exists()) {
				makeDefault(graphics);
			}
			graphicsObject = readJSON(graphics);

		} catch (IOException e) {
			logger.error("IOException while initializing the data storage", e);
			e.printStackTrace();
		} catch (JSONException e) {
			logger.error("Failed to parse JSON from the data storage", e);
			e.printStackTrace();
		}

	}

	private void makeDefault(File file) throws IOException {
		if (!file.createNewFile()) {
			throw new IOException("Creation of file " + file + " failed!");
		}
		FileWriter writer = new FileWriter(file);
		writer.write("{}");
		writer.close();
	}

	public void loadGraphicSettings() {
		Constants.GAME_WIDTH = graphicsObject.optInt(PROFILE_NAME + ".graphic.width", Constants.GAME_WIDTH);
		Constants.GAME_HEIGHT = graphicsObject.optInt(PROFILE_NAME + ".graphic.height", Constants.GAME_HEIGHT);
		Constants.GL3_0 = graphicsObject.optBoolean(PROFILE_NAME + ".graphic.gl3", Constants.GL3_0);
		Constants.TARGET_FPS = graphicsObject.optInt(PROFILE_NAME + ".graphic.fps", Constants.TARGET_FPS);
		Constants.FULLSCREEN = graphicsObject.optBoolean(PROFILE_NAME + ".graphic.fullscreen", Constants.FULLSCREEN);
		Constants.VSYNC = graphicsObject.optBoolean(PROFILE_NAME + ".graphic.vsync", Constants.VSYNC);
		Constants.MSAA = graphicsObject.optInt(PROFILE_NAME + ".graphic.msaa", Constants.MSAA);
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
		updateGraphics(PROFILE_NAME + ".graphic.width", Constants.GAME_WIDTH);
		updateGraphics(PROFILE_NAME + ".graphic.height", Constants.GAME_HEIGHT);
		updateGraphics(PROFILE_NAME + ".graphic.gl3", Constants.GL3_0);
		updateGraphics(PROFILE_NAME + ".graphic.fps", Constants.TARGET_FPS);
		updateGraphics(PROFILE_NAME + ".graphic.fullscreen", Constants.FULLSCREEN);
		updateGraphics(PROFILE_NAME + ".graphic.vsync", Constants.VSYNC);
		updateGraphics(PROFILE_NAME + ".graphic.msaa", Constants.MSAA);
	}

	public void update(String key, Object value) {
		try {
			userStatsObject.putOpt(key, value);
		} catch (JSONException e) {
			logger.error("Failed to update JSON (user stats). The requested operation was: key: " + key + " | value: " + value, e);
			e.printStackTrace();
		}
	}

	public void updateGraphics(String key, Object value) {
		try {
			graphicsObject.putOpt(key, value);
		} catch (JSONException e) {
			logger.error("Failed to update JSON (graphics). The requested operation was: key: " + key + " | value: " + value, e);
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

		writer = new FileWriter(graphics);
		writer.write(graphicsObject.toString());
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

	public File getStoragePath() {
		return this.storagePath;
	}

	public void setStoragePath(File storagePath) {
		this.storagePath = storagePath;
	}

	public File getUserStats() {
		return this.userStats;
	}

	public void setUserStats(File userStats) {
		this.userStats = userStats;
	}

}
