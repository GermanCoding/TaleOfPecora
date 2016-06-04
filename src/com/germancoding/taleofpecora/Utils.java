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
