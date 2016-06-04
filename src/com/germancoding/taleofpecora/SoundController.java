/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class SoundController implements Disposable {

	private Music backgroundMusic;

	public SoundController() {
		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Lord of the Land.mp3"));
		backgroundMusic.setLooping(true);
		backgroundMusic.play();
	}

	public void dispose() {
		if(backgroundMusic != null) {
			backgroundMusic.stop();
			backgroundMusic.dispose();
		}
		backgroundMusic = null;
	}
	
	public void pause() {
		backgroundMusic.pause();
	}
	
	public void resume() {
		backgroundMusic.play();
	}

}
