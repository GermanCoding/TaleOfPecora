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
