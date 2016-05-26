package com.germancoding.taleofpecora;

import java.io.File;

import com.badlogic.gdx.Gdx;

public class ConfigStorage {
	
	public static final String TAG = "[ConfigStorage]"; 
	
	File storagePath;
	
	public ConfigStorage() {
		String appdata = System.getenv("APPDATA");
		File folder = new File(appdata);
		
		if(!folder.isDirectory()) {
			Gdx.app.debug(TAG, "Appdata folder not found, falling back to LibGDX default storage.");
			folder = new File(Gdx.files.getLocalStoragePath());
			
			if(!folder.isDirectory()) {
				Gdx.app.error(TAG, "No storage path found, falling back to system default.");
				// folder = new File();
			}
		}
		
	}

}
