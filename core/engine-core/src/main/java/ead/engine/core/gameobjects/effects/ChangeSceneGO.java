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

package ead.engine.core.gameobjects.effects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ead.common.model.EAdElement;
import ead.common.model.elements.effects.ChangeSceneEf;
import ead.common.model.elements.scenes.EAdScene;
import ead.engine.core.factories.SceneElementGOFactory;
import ead.engine.core.game.GameState;
import ead.engine.core.gameobjects.sceneelements.SceneGO;
import ead.engine.core.gameobjects.sceneelements.transitions.TransitionGO;
import ead.engine.core.gameobjects.sceneelements.transitions.sceneloaders.SceneLoader;
import ead.engine.core.gameobjects.sceneelements.transitions.sceneloaders.SceneLoaderListener;
import ead.engine.core.platform.GUI;

public class ChangeSceneGO extends AbstractEffectGO<ChangeSceneEf> implements
		SceneLoaderListener {

	private GUI gui;

	private SceneLoader sceneLoader;

	private static final Logger logger = LoggerFactory
			.getLogger("ChangeSceneGO");

	private SceneElementGOFactory transitionFactory;

	private boolean finished;

	private TransitionGO<?> transition;

	@Inject
	public ChangeSceneGO(GUI gui, GameState gameState,
			SceneElementGOFactory sceneElementFactory, SceneLoader sceneLoader) {
		super(gameState);
		this.sceneLoader = sceneLoader;
		this.gui = gui;
		this.transitionFactory = sceneElementFactory;
	}

	@Override
	public void initialize() {
		super.initialize();
		finished = false;
		// If the effect is to a different scene
		if (effect.getNextScene() == null
				|| effect.getNextScene() != gui.getScene().getElement()) {
			transition = (TransitionGO<?>) transitionFactory.get(effect
					.getTransition());
			transition.setPreviousScene(gui.getScene());
			EAdElement e = effect.getNextScene();
			if (e != null) {
				Object finalElement = gameState.maybeDecodeField(e);
				if (finalElement instanceof EAdScene) {
					sceneLoader.loadScene((EAdScene) finalElement, this);
				} else {
					logger
							.warn("Element in change scene is not an EAdScene. Returning to previous scene.");

				}
			} else {

			}
			gui.setScene(transition);
		}
	}

	@Override
	public void sceneLoaded(SceneGO sceneGO) {
		transition.transition(sceneGO);
		finished = true;
	}

	public void update() {
		sceneLoader.step();
	}

	public boolean isQueuable() {
		return true;
	}

	public boolean isBlocking() {
		return true;
	}

	public boolean isFinished() {
		return finished;
	}

}
