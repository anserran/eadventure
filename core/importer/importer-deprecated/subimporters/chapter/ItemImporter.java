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

package ead.importer.subimporters.chapter;

import java.util.LinkedHashMap;

import com.google.inject.Inject;

import es.eucm.ead.model.assets.drawable.basics.Image;
import es.eucm.ead.model.elements.scenes.SceneElementDef;
import ead.importer.EAdElementImporter;
import ead.importer.annotation.ImportAnnotator;
import ead.importer.interfaces.EAdElementFactory;
import ead.importer.interfaces.ResourceImporter;
import es.eucm.ead.tools.StringHandler;
import es.eucm.eadventure.common.data.chapter.Action;
import es.eucm.eadventure.common.data.chapter.conditions.Conditions;
import es.eucm.eadventure.common.data.chapter.elements.Item;

public class ItemImporter extends ActorImporter<Item> {

	@Inject
	public ItemImporter(StringHandler stringHandler,
			ResourceImporter resourceImporter,
			EAdElementFactory elementFactory,
			EAdElementImporter<Action, SceneElementDef> actionImporter,
			EAdElementFactory factory, ImportAnnotator annotator,
			EAdElementImporter<Conditions, Condition> conditionsImporter) {
		super(stringHandler, resourceImporter, elementFactory, actionImporter,
				factory, annotator, conditionsImporter);
	}

	@Override
	public void initResourcesCorrespondencies() {
		properties = new LinkedHashMap<String, String>();
		objectClasses = new LinkedHashMap<String, Object>();
		properties.put(Item.RESOURCE_TYPE_IMAGE, SceneElementDef.appearance);
		properties.put(Item.RESOURCE_TYPE_IMAGEOVER,
				SceneElementDef.overAppearance);
		objectClasses.put(Item.RESOURCE_TYPE_IMAGE, Image.class);
		objectClasses.put(Item.RESOURCE_TYPE_IMAGEOVER, Image.class);

		annotator.annotate(ImportAnnotator.Type.Entry,
				ImportAnnotator.Key.Role, "actor.item");
	}

	@Override
	public SceneElementDef convert(Item oldObject, Object object) {
		SceneElementDef item = super.convert(oldObject, object);

		// TODO inventory image
		return item;
	}

}
