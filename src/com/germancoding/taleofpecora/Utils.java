package com.germancoding.taleofpecora;

public class Utils {

	public static boolean isSimilar(float a, float b, float range) {
		if (a > b) {
			return (a - b) <= range;
		} else {
			return (b - a) <= range;
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.valueOf(s);
			return true;
		} catch (NumberFormatException e) {
			;
		}
		return false;
	}

}
