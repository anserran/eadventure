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

package ead.editor.control.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ead.editor.control.Command;
import ead.editor.control.change.ChangeEvent;
import ead.editor.view.generic.FieldDescriptor;

/**
 * Class that represents the generic command that uses introspection to change T values.
 */
public class ChangeFieldValueCommand<T> extends Command implements ChangeEvent {

	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(ChangeFieldValueCommand.class.getSimpleName());

	/**
	 * The old value (T) to be changed.
	 */
	protected T oldValue;

	/**
	 * The new value (T) to change.
	 */
	protected T newValue;

	protected FieldDescriptor<T> fieldDescriptor;

	/**
	 * Constructor for the ChangeValueCommand class.
	 *
	 * @param newValue
	 *            The new value (T)
	 * @param fieldDescriptor
	 *
	 */
	public ChangeFieldValueCommand(T oldValue, T newValue,
			FieldDescriptor<T> fieldDescriptor) {
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.fieldDescriptor = fieldDescriptor;
	}

	/**
	 * Method to perform a changing values command
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ChangeEvent performCommand() {
		oldValue = fieldDescriptor.read();

		if ((newValue != null && oldValue == null)
				|| (newValue == null && oldValue != null)
				|| (newValue != null && oldValue != null && !oldValue
						.equals(newValue))) {
			return setValue(newValue);
		}

		return null;
	}

	@Override
	public boolean hasChanged(FieldDescriptor fd) {
		return fd.equals(fieldDescriptor);
	}

	private ChangeEvent setValue(T value) {
		fieldDescriptor.write(value);
		return this;
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	/* (non-Javadoc)
	 * @see es.eucm.eadventure.editor.control.Command#redoCommand()
	 */
	@Override
	public ChangeEvent redoCommand() {
		logger.debug("Redoing: setting value to '{}'", newValue);
		return setValue(newValue);
	}

	/* (non-Javadoc)
	 * @see es.eucm.eadventure.editor.control.Command#combine(es.eucm.eadventure.editor.control.Command)
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	@Override
	public boolean combine(Command other) {
		if (other instanceof ChangeFieldValueCommand) {
			ChangeFieldValueCommand<T> o = (ChangeFieldValueCommand) other;
			if (fieldDescriptor.equals(o.fieldDescriptor)
					&& likesToCombine(o.newValue)) {
				newValue = o.newValue;
				timeStamp = o.timeStamp;
				logger.info("Combined command");
				return true;
			}
		}
		return false;
	}

	/**
	 * Hook for subclasses, so they can decide if they want to combine with 
	 * next-in-line or not.
	 * @param nextValue value to combine to
	 * @return true if combination is good, false otherwise
	 */
	public boolean likesToCombine(T nextValue) {
		return true;
	}

	/* (non-Javadoc)
	 * @see es.eucm.eadventure.editor.control.Command#undoCommand()
	 */
	@Override
	public ChangeEvent undoCommand() {
		logger.debug("Undoing: setting value to '{}'", oldValue);
		return setValue(oldValue);
	}

	/**
	 * Returns the old value
	 */
	public T getOldValue() {
		return oldValue;
	}

	/**
	 * Returns the new value
	 */
	public T getNewValue() {
		return newValue;
	}

	@Override
	public String toString() {
		return "ChangeFieldValue: from '" + oldValue + "' to '" + newValue
				+ "' in " + fieldDescriptor;
	}
}
