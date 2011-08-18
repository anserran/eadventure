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

package es.eucm.eadventure.common.impl.importer.subimporters.effects;

import com.google.inject.Inject;

import es.eucm.eadventure.common.EAdElementImporter;
import es.eucm.eadventure.common.data.chapter.conditions.Conditions;
import es.eucm.eadventure.common.data.chapter.effects.RandomEffect;
import es.eucm.eadventure.common.impl.importer.interfaces.EffectsImporterFactory;
import es.eucm.eadventure.common.model.effects.EAdEffect;
import es.eucm.eadventure.common.model.effects.impl.EAdRandomEffect;
import es.eucm.eadventure.common.model.elements.EAdCondition;

public class RandomEffectImporter extends
		EffectImporter<RandomEffect, EAdRandomEffect> {

	private EffectsImporterFactory effectsImporterFactory;

	private static int ID_GENERATOR = 0;

	@Inject
	public RandomEffectImporter(
			EAdElementImporter<Conditions, EAdCondition> conditionImporter,
			EffectsImporterFactory effectsImporterFactory) {
		super(conditionImporter);
		this.effectsImporterFactory = effectsImporterFactory;
	}

	@Override
	public EAdRandomEffect init(RandomEffect oldObject) {
		EAdRandomEffect effect = new EAdRandomEffect("randomEffect"
				+ ID_GENERATOR++);
		effect.setQueueable(true);
		return effect;
	}
	
	@Override
	public EAdRandomEffect convert(RandomEffect oldObject, Object object) {
		EAdRandomEffect effect = super.convert(oldObject, object);

		EAdEffect positiveEffect = (EAdEffect) effectsImporterFactory
				.getEffect(oldObject.getPositiveEffect());
		effect.addEffect(positiveEffect, oldObject.getProbability());

		EAdEffect negativeEffect = (EAdEffect) effectsImporterFactory
				.getEffect(oldObject.getNegativeEffect());
		effect.addEffect(negativeEffect, 100.0f - oldObject.getProbability());

		return effect;
	}

}
