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

package ead.importer.subimporters.effects;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import es.eucm.ead.model.elements.extra.EAdList;
import ead.importer.EAdElementImporter;
import ead.importer.interfaces.EffectsImporterFactory;
import es.eucm.ead.tools.AbstractFactory;
import es.eucm.ead.tools.MapProvider;
import es.eucm.ead.tools.reflection.ReflectionProvider;
import es.eucm.eadventure.common.data.chapter.effects.Effects;

public class EffectsImporterFactoryImpl extends
		AbstractFactory<Class<? extends EAdElementImporter<?, ?>>> implements
		EffectsImporterFactory {

	private Injector injector;

	@Inject
	public EffectsImporterFactoryImpl(
			@Named("effects") MapProvider<Class<?>, Class<? extends EAdElementImporter<?, ?>>> map,
			ReflectionProvider interfacesProvider, Injector injector) {
		super(map, interfacesProvider);
		this.injector = injector;
	}

	@Override
	public EAdElementImporter<?, ?> getImporter(Class<?> clazz) {
		Class<? extends EAdElementImporter<?, ?>> importerClass = get(clazz);
		if (importerClass != null)
			return (EAdElementImporter<?, ?>) injector
					.getInstance(importerClass);
		return null;
	}

	@SuppressWarnings( { "rawtypes", "unchecked" })
	@Override
	public Effect getEffect(
			es.eucm.eadventure.common.data.chapter.effects.Effect effect) {
		EAdElementImporter importer = getImporter(effect.getClass());
		if (importer != null) {
			Effect newEffect = (Effect) importer.init(effect);
			return (Effect) importer.convert(effect, newEffect);
		} else
			return null;
	}

	@Override
	public EAdList<Effect> getMacroEffects(Effects effects) {
		if (effects == null || effects.isEmpty()) {
			return null;
		}

		EAdList<Effect> macro = new EAdList<Effect>();

		for (es.eucm.eadventure.common.data.chapter.effects.Effect e : effects
				.getEffects()) {
			Effect effect = getEffect(e);
			if (effect != null)
				macro.add(effect);
		}

		return macro;
	}

}
