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
