/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

import com.badlogic.gdx.Input;

public class Constants {

	// -- Unused vars, only here because I'm too lazy to clean up -- //
	public static float VIEWPORT_WIDTH = 5.0f;
	public static float VIEWPORT_HEIGHT = 5.0f;
	public static int ATLAS_WIDTH = 8192;
	public static int ATLAS_HEIGHT = 8192;
	public static String TEXTURE_ATLAS_OBJECTS = "atlas_main.pack.atlas";

	// -- Actually used vars -- //

	// Util
	public static int BASE_WIDTH = 1920; // We will calculate zoom factors using that width as the "preferred" one so that this will define the visible game world
	public static int BASE_HEIGHT = 1080;
	// Note to myself: 1920 is also the resolution width used in Overlap2D, right?
	public static String TWITTER_LINK = "https://twitter.com/GermanCoding";

	// --- Menu settings start --- Will be set by platform (only desktop for now) the assigned values here have no meaning //
	// Graphic settings
	public static int GAME_WIDTH = -1;
	public static int GAME_HEIGHT = -1;
	public static boolean GL3_0 = false; // LibGDX 1.7.1 supports OpenGL (GLES 2.0/3.0) 2.x or 3.x for rendering. 3.x is experimental, but no known bugs so far.
	public static int TARGET_FPS = -1;
	public static boolean FULLSCREEN = false;
	public static boolean VSYNC = false;
	public static int MSAA = -1;

	// Controls
	public static int CONTROLS_LEFT = Input.Keys.A;
	public static int CONTROLS_RIGHT = Input.Keys.D;
	public static int CONTROLS_UP = Input.Keys.W;
	public static int CONTROLS_DOWN = Input.Keys.S;
	public static int CONTROLS_JUMP = Input.Keys.SPACE;
	public static int GAME_PAUSE = Input.Keys.P;
	public static int GAME_BACK = Input.Keys.ESCAPE;

	// --- Menu settings end --- //

	// Ingame vars - DO NOT TOUCH OR GAMEPLAY WILL CHANGE RADICALLY
	public static float PLAYER_SPEED = 5f;
	public static float PLAYER_WATERSPEED = PLAYER_SPEED / 3;
	public static float PLAYER_JUMP = 6f;
	public static float MAXIMUM_NEGATIVE_VELOCITY = -10f;
	public static float GRAVITY = 9.81f; // "Real life" value, altough I don't think our gravity simulation is realistic. But it's a nice value.
	public static long INWATER_FLAG = 100l; // Time in milliseconds after we will remove the in-water flag.
	public static float KNIGHT_SPEED = PLAYER_SPEED / 3;
	public static float PLATFORM_SPEED = 1f;
	public static long DEATH_TIME = 2000; // Time in milliseconds to continue playing after the player died (death animation) - does not apply if player is out of the world

	// Points
	public static int POINTS_PER_STAR = 500;
	public static int POINTS_PER_CARROT = 50;
	public static int POINTS_PER_ENEMY = 100;
	public static int POINTS_FOR_PERFECT = 3000;
	public static long MAXIMUM_TIME = 5 * 60 * 1000; // 5 minutes maximum level time to get bonus points
	public static int TIME_MULTIPLIER = 1; // 1 point per second

}
