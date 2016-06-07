/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora.desktop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglGraphics.SetDisplayModeCallback;
import com.germancoding.taleofpecora.ConfigStorage;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.TaleOfPecora;
import com.germancoding.taleofpecora.Utils;

public class DesktopLauncher {

	private static boolean rebuildAtlas = false;
	private static boolean drawDebugOutline = false;

	static int IMG_WIDTH = 148;
	static int IMG_HEIGHT = 178;

	@SuppressWarnings("all")
	public static void main(String[] args) {
		if (rebuildAtlas) {
			// I HATE EVERYTHING
			File folder = new File("D:/Java-developing/Minecraft_EPlugins/Projects/Canyon Bunny-core/assets/explosion");

			for (File f : folder.listFiles()) {
				if (!f.isFile())
					continue;
				int number = Integer.parseInt(f.getName().substring(9).replace(".png", ""));
				String name = "explosion";
				// String name = f.getName().split(" ")[0];
				if (number >= 10)
					f.renameTo(new File(folder, name + "0" + number + ".png"));
				else
					f.renameTo(new File(folder, name + "00" + number + ".png"));

				if (false) {
					f = new File(folder, name + "0" + number + ".png");
					try {
						BufferedImage img = ImageIO.read(f);
						BufferedImage resizedImage = Scalr.resize(img, IMG_WIDTH, IMG_HEIGHT, null);
						ImageIO.write(resizedImage, "png", f);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}

		// Setup unchangeable configuration
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Tale of pecora [DesktopLauncher]";
		config.resizable = false; // Resize would need to re-init all UIs, camera... Fixed size is much easier for our simple game.

		// Setup default constants
		Constants.GL3_0 = false; // 3.x is experimental, can be used if wanted (no known bugs) but default is false.
		Constants.GAME_HEIGHT = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		Constants.GAME_WIDTH = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		Constants.TARGET_FPS = 0; // More FPS gives a much smoother gameplay experience, 60 fps stutters a bit. 120 looks mostly fine (and should also not consume too much
		// CPU on most systems)
		Constants.FULLSCREEN = false;
		Constants.VSYNC = false; // Prefer maximum FPS (reason above), but VSync is fully supported if the user wants it.
		Constants.MSAA = 0; // Can't see any difference when using MSAA, so default is off. It's up to the user to turn it on, maybe there is a visible difference on high-
		// resolution displays

		// Load and write preferences
		ConfigStorage graphicConfig = new ConfigStorage();
		graphicConfig.loadGraphicSettings();
		graphicConfig.updateGraphicSettings();
		try {
			graphicConfig.save();
		} catch (IOException e) {
			System.err.println("Failed to save graphic preferences in main() - " + e);
			e.printStackTrace();
			// This is a bad error (filesystem unavailable, disk full?) but we will continue anyway.
		}

		// Configure LWJGL to use our preferences
		config.useGL30 = Constants.GL3_0;
		config.height = Constants.GAME_HEIGHT;
		config.width = Constants.GAME_WIDTH;
		config.foregroundFPS = Constants.TARGET_FPS;
		config.fullscreen = Constants.FULLSCREEN;
		config.vSyncEnabled = Constants.VSYNC;
		config.samples = Constants.MSAA;

		// Allow override of configuration through command line
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equalsIgnoreCase("-width")) {
				config.width = Integer.parseInt(args[i + 1]);
			}
			if (arg.equalsIgnoreCase("-height")) {
				config.height = Integer.parseInt(args[i + 1]);
			}
			if (arg.equalsIgnoreCase("-fullscreen")) {
				config.fullscreen = true;
			}
			if (arg.equalsIgnoreCase("-noVsync")) {
				config.vSyncEnabled = false;
				config.foregroundFPS = Integer.MAX_VALUE;
			}
			if (arg.equalsIgnoreCase("-Vsync")) {
				config.vSyncEnabled = true;
			}
			if (arg.equalsIgnoreCase("-gl3")) {
				config.useGL30 = true;
			}
			if (arg.equalsIgnoreCase("-fps")) {
				config.foregroundFPS = Integer.parseInt(args[i + 1]);
			}
		}

		// If the preferred config is unsupported by LWJGL, we will try to fix it here (Currently only wrong width * height combinations)
		config.setDisplayModeCallback = new SetDisplayModeCallback() {

			@Override
			public LwjglApplicationConfiguration onFailure(LwjglApplicationConfiguration initialConfig) {
				java.awt.DisplayMode mode = getBestFullscreenMode(initialConfig.getDisplayModes(), initialConfig.height, initialConfig.width);
				if (mode == null) {
					throw new RuntimeException("Unable to find a display mode!");
				}
				initialConfig.height = mode.getHeight();
				initialConfig.width = mode.getWidth();
				return initialConfig;
			}
		};

		// Start
		LwjglApplication app = new LwjglApplication(new TaleOfPecora(), config);
		TaleOfPecora.instance.restartRun = new Runnable() {

			@Override
			public void run() {
				if (Gdx.app != null)
					Gdx.app.exit();
				restart();
			}
		};
		TaleOfPecora.instance.config = graphicConfig;
		synchronized (TaleOfPecora.instance) {
			TaleOfPecora.instance.notifyAll();
		}
		app.log("[DesktopLauncher]", "Application created");
	}

	public static java.awt.DisplayMode getBestFullscreenMode(DisplayMode[] modes, int height, int width) {
		// (Method partly copied from SettingsStage)
		int closestHeight = 0, closestWidth = 0;

		for (DisplayMode suppported : modes) {
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
					// If this is true than this display mode is the best we found (yet).
					// System.out.println("Best match: " + suppported.height + "*" + suppported.width);
					closestHeight = suppported.height;
					closestWidth = suppported.width;
				}
			}
		}

		if (closestHeight == 0 || closestWidth == 0) {
			return null;
		}
		System.out.println("Using this display mode: height = " + closestHeight + ", width = " + closestWidth);
		return new java.awt.DisplayMode(closestWidth, closestHeight, 0, 0); // Only a dummy object, ignore refresh & bit values
	}

	public static void restart() {
		final StringBuilder cmd = new StringBuilder();
		cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
		for (final String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			cmd.append(jvmArg + " ");
		}
		cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
		cmd.append(DesktopLauncher.class.getName()).append(" ");

		try {
			Runtime.getRuntime().exec(cmd.toString());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isDrawDebugOutline() {
		return drawDebugOutline;
	}

	public static void setDrawDebugOutline(boolean drawDebugOutline) {
		DesktopLauncher.drawDebugOutline = drawDebugOutline;
	}
}
