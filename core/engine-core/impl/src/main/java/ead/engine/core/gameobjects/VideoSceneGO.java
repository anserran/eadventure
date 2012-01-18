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

package ead.engine.core.gameobjects;

import java.util.logging.Logger;

import com.google.inject.Inject;

import ead.common.model.elements.EAdEffect;
import ead.common.model.elements.VideoScene;
import ead.common.resources.StringHandler;
import ead.common.resources.assets.multimedia.Video;
import ead.engine.core.game.GameState;
import ead.engine.core.gameobjects.factories.EventGOFactory;
import ead.engine.core.gameobjects.factories.SceneElementGOFactory;
import ead.engine.core.gameobjects.go.SceneGO;
import ead.engine.core.gameobjects.sceneelements.SceneElementGOImpl;
import ead.engine.core.input.InputAction;
import ead.engine.core.platform.AssetHandler;
import ead.engine.core.platform.GUI;
import ead.engine.core.platform.SpecialAssetRenderer;
import ead.engine.core.util.EAdTransformation;

public class VideoSceneGO extends SceneElementGOImpl<VideoScene> implements
		SceneGO<VideoScene> {

	private static final Logger logger = Logger.getLogger("VideoScreenGOImpl");

	private SpecialAssetRenderer<Video, ?> specialAssetRenderer;

	private Object component;

	private boolean error;

	@Inject
	public VideoSceneGO(AssetHandler assetHandler, StringHandler stringsReader,
			SceneElementGOFactory gameObjectFactory, GUI gui,
			GameState gameState,
			SpecialAssetRenderer<Video, ?> specialAssetRenderer,
			EventGOFactory eventFactory) {
		super(assetHandler, stringsReader, gameObjectFactory, gui, gameState,
				eventFactory);
		logger.info("New instance");
		this.specialAssetRenderer = specialAssetRenderer;
		this.component = null;
		this.error = false;
	}

	public void doLayout(EAdTransformation transformation) {
		if (component == null)
			try {
				component = specialAssetRenderer.getComponent((Video) element
						.getDefinition().getAsset(VideoScene.video));
			} catch (Exception e) {
				error = true;
			} catch ( LinkageError e ){
				error = true;
			}

		if (!error) {
			if (specialAssetRenderer.isFinished()) {
				gui.showSpecialResource(null, 0, 0, true);
				component = null;
			} else
				gui.showSpecialResource(component, 0, 0, true);
		}
	}

	@Override
	public void update() {
		super.update();
		if (error || specialAssetRenderer.isFinished()) {
			gui.showSpecialResource(null, 0, 0, true);
			for (EAdEffect e : element.getFinalEffects()) {
				gameState.addEffect(e);
			}
		} else {
			specialAssetRenderer.start();
		}
	}

	@Override
	public boolean contains(int x, int y) {
		return false;
	}

	@Override
	public boolean processAction(InputAction<?> action) {
		return false;
	}

}