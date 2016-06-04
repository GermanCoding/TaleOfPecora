/*******************************************************************************
 * Copyright (c) 2016 Maximilian Froehling aka GermanCoding.
 * All rights reserved. This program is made available under the terms of the Reciprocal Public License 1.5 
 * which accompanies this distribution, and is available at https://opensource.org/licenses/RPL-1.5
 *******************************************************************************/
package com.germancoding.taleofpecora.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

public class BackgroundStage extends Stage {

	ProjectInfoVO info;

	public BackgroundStage(IResourceRetriever ir) {
		info = ir.getProjectVO();
		CompositeItemVO vo = info.libraryItems.get("background");
		float widthScale = getWidth() / ((vo.width / PhysicsBodyLoader.getScale()) * 4);
		float heightScale = getHeight() / ((vo.height  / PhysicsBodyLoader.getScale()) * 4);
		// vo.scaleX = widthScale;
		// vo.scaleY = heightScale;
		CompositeActor actor = new CompositeActor(vo, ir);
		actor.setScale(widthScale, heightScale);
		actor.setBounds(0, 0, getWidth(), getHeight());
		addActor(actor);
	}

}
