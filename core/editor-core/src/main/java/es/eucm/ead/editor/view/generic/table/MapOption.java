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

/*
 * To performCommand this license header, choose License Headers in Project Properties.
 * To performCommand this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.eucm.ead.editor.view.generic.table;

import es.eucm.ead.editor.control.Command;
import es.eucm.ead.editor.control.commands.ChangeFieldCommand;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.MapCommand;
import es.eucm.ead.editor.model.nodes.DependencyNode;
import es.eucm.ead.editor.view.generic.AbstractOption;
import es.eucm.ead.editor.view.generic.accessors.Accessor;
import es.eucm.ead.editor.view.generic.accessors.IntrospectingAccessor;
import es.eucm.ead.editor.view.generic.accessors.MapAccessor;
import es.eucm.ead.editor.view.generic.table.TableSupport.AbstractRowTableModel;
import es.eucm.ead.editor.view.generic.table.ColumnSpec;
import es.eucm.ead.editor.view.generic.table.TableSupport.DeleteButtonWidget;
import es.eucm.ead.editor.view.generic.table.TableSupport.DeleteIt;
import es.eucm.ead.editor.view.generic.table.TableSupport.MoveButtonWidget;
import es.eucm.ead.editor.view.generic.table.TableSupport.MoveIt;
import es.eucm.ead.editor.view.generic.table.TableSupport.Row;
import es.eucm.ead.model.elements.extra.EAdMap;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An option that allows a map of elements to be manipulated. Conceptually very
 * similar to manipulating a list.
 *
 * @author mfreire
 * @param <K> key-type
 * @param <V> value-type (for underlying list)
 */
public class MapOption<K, V> extends AbstractOption<EAdMap<K, V>> implements
		TableLikeControl<V, K> {

	static private Logger logger = LoggerFactory.getLogger(MapOption.class);

	private JPanel controlPanel;
	private JXTable tableControl;
	private JButton chooseMoreButton;
	private MapTableModel tableModel;
	private final Class<?> contentClass;

	public MapOption(String title, String toolTipText, Object object,
			String fieldName, Class<?> contentClass, DependencyNode... changed) {
		super(title, toolTipText, new IntrospectingAccessor<EAdMap<K, V>>(
				object, fieldName), changed);
		this.contentClass = contentClass;

	}

	public ColumnSpec<V, K>[] getKeyColumns() {
		return (ColumnSpec<V, K>[]) new ColumnSpec[] { new ColumnSpec<V, K>(
				"Key", String.class, false, -1) {

			@Override
			public Object getValue(Row<V, K> row, int columnIndex) {
				return row.getKey().toString();
			}

		} };
	}

	public ColumnSpec<V, K>[] getValueColumns() {
		return (ColumnSpec<V, K>[]) new ColumnSpec[] { new ColumnSpec(
				"Value", contentClass, false, -1) };
	}

	/**
	 * Model used to represent the map. Looks directly at oldValue; which must
	 * always be updated.
	 */
	private class MapTableModel extends AbstractRowTableModel<V, K> {

		private final HashMap<K, Integer> keysToRows = new HashMap<K, Integer>();

		@SuppressWarnings("unchecked")
		public MapTableModel() {
			super(MapOption.this);

			ColumnSpec<V, K> upDown = new ColumnSpec<V, K>("", MoveIt.class,
					true, 16);
			upDown.setEditor(new MoveButtonWidget(MapOption.this));
			upDown.setRenderer(new MoveButtonWidget(MapOption.this));

			ColumnSpec<V, K> delete = new ColumnSpec<V, K>("", DeleteIt.class,
					true, 20);
			delete.setEditor(new DeleteButtonWidget(MapOption.this));
			delete.setRenderer(new DeleteButtonWidget(MapOption.this));

			ColumnSpec<V, K>[] keys = (ColumnSpec<V, K>[]) getKeyColumns();
			ColumnSpec<V, K>[] values = (ColumnSpec<V, K>[]) getValueColumns();
			int totalColCount = keys.length + values.length + 1;
			cols = (ColumnSpec<V, K>[]) new ColumnSpec[totalColCount];
			System.arraycopy(keys, 0, cols, 0, keys.length);
			System.arraycopy(values, 0, cols, keys.length, values.length);
			cols[cols.length - 1] = delete;
			if (logger.isDebugEnabled()) {
				int i = 0;
				for (ColumnSpec<V, K> c : cols) {
					logger.debug(" -- at col {}: {}{}", i++, c.getClass()
							.getSimpleName(), c.hashCode());
				}
			}

			reindex();
		}

		@Override
		public void reindex() {
			rows = new Row[oldValue.size()];
			keysToRows.clear();
			Iterator<Map.Entry<K, V>> it = oldValue.entrySet().iterator();
			for (int i = 0; it.hasNext(); i++) {
				rows[i] = new Row<V, K>(it.next());
				keysToRows.put(rows[i].getKey(), i);
			}
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Row<V, K> r = rows[rowIndex];
			Accessor a = cols[columnIndex].getAccessor(r, columnIndex);

			if (a.getSource() == r) {
				// direct change
				if (columnIndex == 0) {
					// changing the key
					if (oldValue.containsKey((K) value)) {
						// refuse to change value - would overwrite existing
						logger.warn("Refusing to allow user to overwrite key");
					} else {
						performCommand(new MapCommand.ChangeKeyInMap<K, V>(
								oldValue, r.getKey(), (K) value, changed));
					}
				} else {
					// changing a map value
					a = new MapAccessor(oldValue, r.getKey());
					performCommand(new ChangeFieldCommand(value, a, changed));
				}
			} else {
				// changing a field within the key or value
				performCommand(new ChangeFieldCommand(value, a, changed));
			}
		}
	}

	@Override
	protected JComponent createControl() {

		oldValue = accessor.read();
		tableModel = new MapTableModel();
		tableControl = new JXTable(tableModel);
		for (int i = 0; i < tableModel.cols.length; i++) {
			ColumnSpec<V, K> c = tableModel.cols[i];
			if (c.getRenderer() != null) {
				tableControl.getColumn(i).setCellRenderer(c.getRenderer());
			}
			if (c.getEditor() != null) {
				tableControl.getColumn(i).setCellEditor(c.getEditor());
			}
			if (c.getWidth() != -1) {
				tableControl.getColumn(i).setMinWidth(c.getWidth());
				tableControl.getColumn(i).setMaxWidth(c.getWidth());
			}
		}
		tableControl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableControl.setColumnControlVisible(false);
		tableControl.setSortable(false);
		tableControl.setAutoResizeMode(JXTable.AUTO_RESIZE_ALL_COLUMNS);
		tableControl.setRowHeight(32);
		tableControl.setColumnMargin(5);

		chooseMoreButton = new JButton("+");
		chooseMoreButton.setToolTipText(Messages.options_table_add);
		chooseMoreButton.setPreferredSize(new Dimension(50, 16));
		chooseMoreButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				V value = chooseElementToAdd();
				K key = chooseKeyToAdd();
				if (value != null) {
					add(value, key);
				}
			}
		});

		controlPanel = new JPanel(new BorderLayout());
		JScrollPane tableScroll = new JScrollPane(tableControl);
		tableScroll.setMinimumSize(new Dimension(0, 120));
		tableScroll.setPreferredSize(new Dimension(0, 120));
		controlPanel.add(tableScroll, BorderLayout.CENTER);
		controlPanel.add(chooseMoreButton, BorderLayout.SOUTH);

		return controlPanel;
	}

	@Override
	public EAdMap<K, V> getControlValue() {
		return accessor.read();
	}

	@Override
	protected void setControlValue(EAdMap<K, V> newValue) {
		tableModel.fireTableDataChanged();
	}

	@Override
	public void remove(K key) {
		V o = oldValue.get(key);
		logger.info("Removing {} (at {})", new Object[] { o, key });
		Command c = new MapCommand.RemoveFromMap<K, V>(oldValue, key, changed);
		performCommand(c);
	}

	// FIXME - unimplemented
	@Override
	public V chooseElementToAdd() {
		logger.info("User wants to CHOOSE something to ADD! Madness!!");
		return null;
	}

	/**
	 * Launches UI prompt to add a key to a list element
	 */
	@Override
	public K chooseKeyToAdd() {
		logger.info("User wants to CHOOSE a KEY to ADD something! Madness!!");
		return null;
	}

	@Override
	public void add(V added, K key) {
		logger.info("Adding {}", oldValue);
		Command c = new MapCommand.AddToMap<K, V>(oldValue, added, key, changed);
		performCommand(c);
	}

	/**
	 * Moves an object one position up. Triggered either externally or via
	 * button-click.
	 */
	@Override
	public void moveUp(K index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes an object from the list. Triggered either externally or via
	 * button-click.
	 */
	@Override
	public void moveDown(K index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the key for a given row
	 */
	@Override
	public K keyForRow(int row) {
		return tableModel.keyForRow(row);
	}

	private void performCommand(Command c) {
		manager.performCommand(c);
	}

	/**
	 * Consider contents to have changed, even if the list-reference does not
	 * change.
	 *
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	@Override
	protected boolean changeConsideredRelevant(EAdMap<K, V> oldValue,
			EAdMap<K, V> newValue) {
		return true;
	}
}
