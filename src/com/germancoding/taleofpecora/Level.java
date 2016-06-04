/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

public class Level {
	
	private int id;
	private float levelX;
	
	public Level(int id, float levelX) {
		super();
		this.id = id;
		this.levelX = levelX;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getLevelX() {
		return levelX;
	}

	public void setLevelX(float levelX) {
		this.levelX = levelX;
	}

}
