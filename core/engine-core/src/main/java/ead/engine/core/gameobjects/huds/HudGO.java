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

package ead.engine.core.gameobjects.huds;

import ead.common.model.elements.scenes.ComplexSceneElement;
import ead.engine.core.gameobjects.go.ComplexSceneElementGO;

/**
 * Classes that implement this interface represent a HUD element of the
 * eAdventure game.
 */
public interface HudGO extends ComplexSceneElementGO<ComplexSceneElement> {

	/**
	 * It is called when the graphic context has been created. HUDs graphics
	 * components (buttons, labels...) should be created here. The method
	 * {@link ComplexSceneElementGO#setElement(Object)} with an appropriate
	 * element is usually called here
	 */
	void init();

	/**
	 * @param priority
	 *            the bigger the priority the later is drawn (and the higher
	 *            appears to be)
	 */
	void setPriority(int priority);

	/**
	 * the bigger the priority the later is drawn (and the higher appears to be)
	 * 
	 * @return
	 */
	int getPriority();

}
