package com.germancoding.taleofpecora;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Assets implements Disposable, AssetErrorListener {

	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	private AssetManager assetManager;

	public AssetBush bush;
	public AssetCrate crate;
	public AssetMushroom mushroom;
	public AssetSign sign;
	public AssetStone stone;
	public AssetTree tree;
	public AssetTile tile;

	// singleton: prevent instantiation from other classes
	private Assets() {
	}

	public void init(AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);

		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures())
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		// create game resource objects
		bush = new AssetBush(atlas);
		crate = new AssetCrate(atlas);
		mushroom = new AssetMushroom(atlas);
		sign = new AssetSign(atlas);
		stone = new AssetStone(atlas);
		tree = new AssetTree(atlas);
		tile = new AssetTile(atlas);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@Deprecated
	public void error(String filename, Class<?> type, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + filename + "'", (Exception) throwable);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", throwable);

	}

	// --- Assets START --- //
	public class AssetBush {
		public final AtlasRegion one;
		public final AtlasRegion two;
		public final AtlasRegion three;
		public final AtlasRegion four;

		public AssetBush(TextureAtlas atlas) {
			one = atlas.findRegion("Bush1");
			two = atlas.findRegion("Bush2");
			three = atlas.findRegion("Bush3");
			four = atlas.findRegion("Bush4");
		}
	}

	public class AssetCrate {
		public final AtlasRegion crate;

		public AssetCrate(TextureAtlas atlas) {
			crate = atlas.findRegion("Crate");
		}
	}

	public class AssetMushroom {
		public final AtlasRegion one;
		public final AtlasRegion two;

		public AssetMushroom(TextureAtlas atlas) {
			one = atlas.findRegion("Mushroom1");
			two = atlas.findRegion("Mushroom2");
		}
	}

	public class AssetSign {
		public final AtlasRegion one;
		public final AtlasRegion two;

		public AssetSign(TextureAtlas atlas) {
			one = atlas.findRegion("Sign1");
			two = atlas.findRegion("Sign2");
		}
	}

	public class AssetStone {
		public final AtlasRegion stone;

		public AssetStone(TextureAtlas atlas) {
			stone = atlas.findRegion("Stone");
		}
	}

	public class AssetTree {
		public final AtlasRegion one;
		public final AtlasRegion two;
		public final AtlasRegion three;

		public AssetTree(TextureAtlas atlas) {
			one = atlas.findRegion("Tree1");
			two = atlas.findRegion("Tree2");
			three = atlas.findRegion("Tree3");
		}
	}
	
	public class AssetTile {
		public final AtlasRegion one;
		public final AtlasRegion two;
		public final AtlasRegion three;
		public final AtlasRegion four;
		public final AtlasRegion five;
		public final AtlasRegion six;
		public final AtlasRegion seven;
		public final AtlasRegion eight;
		public final AtlasRegion nine;
		public final AtlasRegion ten;
		public final AtlasRegion elven;
		public final AtlasRegion twelve;
		public final AtlasRegion thirteen;
		public final AtlasRegion fourteen;
		public final AtlasRegion fivteen;
		public final AtlasRegion sixteen;
		public final AtlasRegion seventeen;
		public final AtlasRegion eighteen;
		
		public AssetTile(TextureAtlas atlas) {
			super();
			this.one = atlas.findRegion("Tile1");
			this.two = atlas.findRegion("Tile2");
			this.three = atlas.findRegion("Tile3");
			this.four = atlas.findRegion("Tile4");
			this.five = atlas.findRegion("Tile5");
			this.six = atlas.findRegion("Tile6");
			this.seven = atlas.findRegion("Tile7");
			this.eight = atlas.findRegion("Tile8");
			this.nine = atlas.findRegion("Tile9");
			this.ten = atlas.findRegion("Tile10");
			this.elven = atlas.findRegion("Tile11");
			this.twelve = atlas.findRegion("Tile12");
			this.thirteen = atlas.findRegion("Tile13");
			this.fourteen = atlas.findRegion("Tile14");
			this.fivteen = atlas.findRegion("Tile15");
			this.sixteen = atlas.findRegion("Tile16");
			this.seventeen = atlas.findRegion("Tile17");
			this.eighteen = atlas.findRegion("Tile18");
		}
		
	}
	// --- Assets END --- //
}