/**
 * eAdventure (formerly <e-Adventure> and <e-Game>) is a research project of the
 *    <e-UCM> research group.
 *
 *    Copyright 2005-2010 <e-UCM> research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    <e-UCM> is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure, version 2.0
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */

package ead.elementfactories.demos.scenes;

import ead.common.model.elements.effects.ChangeSceneEf;
import ead.common.model.elements.guievents.KeyGEv;
import ead.common.model.elements.scenes.SceneElement;
import ead.common.model.elements.scenes.VideoScene;
import ead.common.resources.assets.drawable.basics.Image;
import ead.common.resources.assets.multimedia.EAdVideo;
import ead.common.resources.assets.multimedia.Video;
import ead.elementfactories.EAdElementsFactory;
import ead.elementfactories.demos.SceneDemo;

public class WebMVideoScene extends VideoScene implements SceneDemo {

	public WebMVideoScene() {
		setId("videoScene");
		EAdVideo video = new Video("@binary/eAdventure.webm");
		getDefinition().getResources().addAsset(VideoScene.video, video);
		
		ChangeSceneEf changeScene = new ChangeSceneEf();
		changeScene.setId("changeScene");

		SceneElement goRightArrow = EAdElementsFactory
				.getInstance()
				.getSceneElementFactory()
				.createSceneElement(new Image("@drawable/arrow_right.png"),
						200, 60, changeScene);
		this.getSceneElements().add(goRightArrow);
		
		goRightArrow.addBehavior(KeyGEv.KEY_ARROW_RIGHT, changeScene);

	}
	
	@Override
	public String getSceneDescription() {
		return "A scene with a button to launch a video";
	}
	
	public String getDemoName(){
		return "WebM video Scene";
	}

}