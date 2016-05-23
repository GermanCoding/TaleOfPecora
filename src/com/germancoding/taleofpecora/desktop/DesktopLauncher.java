package com.germancoding.taleofpecora.desktop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.germancoding.taleofpecora.Constants;
import com.germancoding.taleofpecora.TaleOfPecora;

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

		// TODO: Load preferences if present

		// TODO: Write default preferences if not present

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
			if (arg.equalsIgnoreCase("-gl3")) {
				config.useGL30 = true;
			}
			if (arg.equalsIgnoreCase("-fps")) {
				config.foregroundFPS = Integer.parseInt(args[i + 1]);
			}
		}

		// Start
		new LwjglApplication(new TaleOfPecora(), config).log("[DesktopLauncher]", "Application created");
	}

	public static boolean isDrawDebugOutline() {
		return drawDebugOutline;
	}

	public static void setDrawDebugOutline(boolean drawDebugOutline) {
		DesktopLauncher.drawDebugOutline = drawDebugOutline;
	}
}
