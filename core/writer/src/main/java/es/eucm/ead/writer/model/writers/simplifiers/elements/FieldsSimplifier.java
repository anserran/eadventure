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

package es.eucm.ead.writer.model.writers.simplifiers.elements;

import es.eucm.ead.model.elements.operations.ElementField;
import es.eucm.ead.writer.model.writers.simplifiers.ObjectSimplifier;

import java.util.HashMap;
import java.util.Map;

public class FieldsSimplifier implements ObjectSimplifier<ElementField> {

	/**
	 * Map to aggregate all repeated fields
	 */
	private Map<Object, Map<String, ElementField>> fields;

	public FieldsSimplifier() {
		fields = new HashMap<Object, Map<String, ElementField>>();
	}

	public Object simplify(ElementField field) {
		// Never two different objects pointing the same field
		Map<String, ElementField> elementFields = fields
				.get(field.getElement());
		if (elementFields == null) {
			elementFields = new HashMap<String, ElementField>();
			fields.put(field.getElement(), elementFields);
		}
		ElementField copy = elementFields.get(field.getVarName());
		if (copy == null) {
			copy = field;
			elementFields.put(field.getVarName(), copy);
		}
		return copy;
	}

	@Override
	public void clear() {
		fields.clear();
	}

	public Map<Object, Map<String, ElementField>> getFields() {
		return fields;
	}

}
