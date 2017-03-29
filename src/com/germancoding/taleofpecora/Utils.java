/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

public class Utils {

	public static boolean isSimilar(float a, float b, float range) {
		if (a > b) {
			return (a - b) <= range;
		} else {
			return (b - a) <= range;
		}
	}

	/**
	 * Returns true if a and b are closer to each other than c and d
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static boolean bestMatch(int a, int b, int c, int d) {
		int delta1 = Math.max(a, b) - Math.min(a, b);
		int delta2 = Math.max(c, d) - Math.min(c, d);
		return delta1 < delta2;
	}
	
	public static int differ(int a, int b) {
		return Math.max(a, b) - Math.min(a, b);
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
