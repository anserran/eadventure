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

package es.eucm.ead.importer.subconverters.effects;

import es.eucm.ead.importer.EAdElementsCache;
import es.eucm.ead.importer.subconverters.effects.EffectsConverter.EffectConverter;
import es.eucm.ead.model.elements.BasicElement;
import es.eucm.ead.model.elements.effects.Effect;
import es.eucm.ead.model.elements.effects.InterpolationEf;
import es.eucm.ead.model.elements.effects.sceneelements.ChangeColorEf;
import es.eucm.ead.model.elements.scenes.SceneElement;
import es.eucm.eadventure.common.data.chapter.effects.HighlightItemEffect;

import java.util.ArrayList;
import java.util.List;

public class HighlightItemConverter implements
		EffectConverter<HighlightItemEffect> {

	private EAdElementsCache elementsCache;

	public HighlightItemConverter(EAdElementsCache elementsCache) {
		this.elementsCache = elementsCache;
	}

	@Override
	public List<Effect> convert(HighlightItemEffect e) {
		ArrayList<Effect> list = new ArrayList<Effect>();

		float red = 0.0f;
		float green = 0.0f;
		float blue = 0.0f;

		switch (e.getHighlightType()) {
		case HighlightItemEffect.HIGHLIGHT_BLUE:
			blue = 1.0f;
			break;
		case HighlightItemEffect.HIGHLIGHT_GREEN:
			green = 1.0f;
			break;
		case HighlightItemEffect.HIGHLIGHT_RED:
			red = 1.0f;
			break;
		case HighlightItemEffect.HIGHLIGHT_BORDER:
			// XXX Highlight border
			red = blue = green = 0.5f;
			break;
		case HighlightItemEffect.NO_HIGHLIGHT:
			red = blue = green = 1.0f;
		}

		ChangeColorEf changeColor = new ChangeColorEf(red, green, blue);
		BasicElement basicElement = elementsCache.get(e.getTargetId());
		changeColor.setSceneElement(basicElement);

		// Effects after highlight effects doesn't wait
		// until the effect ends, that's why change color if the first and
		// last effect in the last, and subsequent effects will added to it
		list.add(changeColor);
		if (e.isHighlightAnimated()) {
			InterpolationEf interpolation = new InterpolationEf(basicElement,
					SceneElement.VAR_SCALE, 1.0f, 0.8f, 1000);
			InterpolationEf interpolation2 = new InterpolationEf(basicElement,
					SceneElement.VAR_SCALE, 0.8f, 1.0f, 1000);
			InterpolationEf interpolation3 = new InterpolationEf(basicElement,
					SceneElement.VAR_SCALE, 1.0f, 0.8f, 1000);
			InterpolationEf interpolation4 = new InterpolationEf(basicElement,
					SceneElement.VAR_SCALE, 0.8f, 1.0f, 1000);
			changeColor.addSimultaneousEffect(interpolation);
			interpolation.addNextEffect(interpolation2);
			interpolation2.addNextEffect(interpolation3);
			interpolation3.addNextEffect(interpolation4);
		}

		return list;
	}
}
