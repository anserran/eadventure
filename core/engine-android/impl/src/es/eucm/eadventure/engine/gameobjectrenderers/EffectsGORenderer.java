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

package es.eucm.eadventure.engine.gameobjectrenderers;

import java.util.logging.Logger;

import android.graphics.Canvas;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.eucm.eadventure.common.model.params.EAdPosition;
import es.eucm.eadventure.engine.core.gameobjects.EffectGO;
import es.eucm.eadventure.engine.core.platform.GameObjectRenderer;

/**
 * A default effects game object renderer. This renderer should be used for
 * those effects who have something to do in the method {@link EffectGO#processAction(es.eucm.eadventure.engine.core.guiactions.GUIAction)}
 */
@Singleton
public class EffectsGORenderer implements
		GameObjectRenderer<Canvas, EffectGO<?>> {

	private static final Logger logger = Logger
	.getLogger("EffectsGORenderer");

	@Inject
	public EffectsGORenderer() {
		logger.info("New instance");
	}
	
	@Override
	public void render(Canvas graphicContext, EffectGO<?> object,
			float interpolation, int offsetX, int offsetY) {

	}

	@Override
	public void render(Canvas graphicContext, EffectGO<?> object,
			EAdPosition position, float scale, int offsetX, int offsetY) {

	}

	@Override
	public boolean contains(EffectGO<?> object, int virtualX, int virtualY) {
		return true;
	}

}