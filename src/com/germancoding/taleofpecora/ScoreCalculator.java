package com.germancoding.taleofpecora;

public class ScoreCalculator {

	public static int getScore(int carrots, int enemies, int stars, long time) {
		int points = carrots * Constants.POINTS_PER_CARROT + enemies * Constants.POINTS_PER_ENEMY + stars * Constants.POINTS_PER_STAR;
		if (time < Constants.MAXIMUM_TIME) {
			int bonus = (int) ((Constants.MAXIMUM_TIME - time) / 1000) * Constants.TIME_MULTIPLIER;
			points += bonus;
		}
		return points;
	}

	public static boolean isPerfect(int carrots, int carrotsMax, int enemies, int enemiesMax, int stars, int starsMax, long time) {
		return carrots >= carrotsMax && enemies >= enemiesMax && stars >= starsMax && time <= Constants.MAXIMUM_TIME;
	}

}
