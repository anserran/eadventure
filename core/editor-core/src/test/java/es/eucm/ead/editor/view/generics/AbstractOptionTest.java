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

package es.eucm.ead.editor.view.generics;

import com.google.inject.Guice;
import com.google.inject.Injector;
import es.eucm.ead.editor.EditorGuiceModule;
import es.eucm.ead.editor.control.CommandManager;
import es.eucm.ead.editor.control.CommandManagerImpl;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.change.ChangeListener;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.editor.util.Log4jConfig;
import es.eucm.ead.engine.desktop.platform.DesktopModule;
import es.eucm.ead.tools.java.JavaToolsModule;
import es.eucm.ead.tools.reflection.ReflectionClassLoader;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static org.mockito.Mockito.when;

public class AbstractOptionTest extends JFrame {

	protected CommandManager commandManager;
	protected JButton undo, redo, dump;
	protected Object model;

	protected JPanel childPanel;

	@Mock
	protected Controller controller;

	public void prepareControllerAndModel() {
		Injector injector = Guice.createInjector(new DesktopModule(),
				new EditorGuiceModule(), new JavaToolsModule());

		// init reflection
		ReflectionClassLoader.init(injector
				.getInstance(ReflectionClassLoader.class));

		Log4jConfig.configForConsole(Log4jConfig.Slf4jLevel.Debug);

		EditorModel editorModel = injector.getInstance(EditorModel.class);
		MockitoAnnotations.initMocks(this);
		when(controller.getModel()).thenReturn(editorModel);
	}

	public void createFrame() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(400, 500);
		setLocationRelativeTo(null);
	}

	public void init() {

		createFrame();
		prepareControllerAndModel();
		commandManager = new CommandManagerImpl();
		commandManager.setController(controller);

		commandManager.addChangeListener(new ChangeListener<String>() {
			@Override
			public void processChange(String event) {
				undo.setEnabled(commandManager.canUndo());
				redo.setEnabled(commandManager.canRedo());
			}
		});

		undo = new JButton(new AbstractAction("undo") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (commandManager.canUndo()) {
					commandManager.undoCommand();
				}
			}
		});
		dump = new JButton(new AbstractAction("show model") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dump();
			}
		});
		redo = new JButton(new AbstractAction("redo") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (commandManager.canRedo()) {
					commandManager.redoCommand();
				}
			}
		});
		undo.setEnabled(false);
		redo.setEnabled(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(undo);
		buttonPanel.add(dump);
		buttonPanel.add(redo);
		add(buttonPanel, BorderLayout.SOUTH);

		childPanel = new JPanel(new BorderLayout());
		add(childPanel, BorderLayout.CENTER);
	}

	public void dump() {
		System.err.println("------\n" + model.toString());
	}
}
