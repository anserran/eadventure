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

package ead.editor;

import ead.utils.Log4jConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import ead.common.importer.ImporterConfigurationModule;
import ead.editor.control.Controller;
import ead.editor.control.ViewController;
import ead.editor.model.EditorModel;
import ead.editor.view.SplashScreen;
import ead.editor.view.impl.SplashScreenImpl;
import ead.engine.desktop.core.platform.module.DesktopAssetHandlerModule;
import ead.engine.desktop.core.platform.module.DesktopModule;
import ead.engine.java.core.platform.modules.JavaBasicGameModule;

/**
 * eAdventure editor launcher. This class has a main method.
 *
 * IMPORTANT: to re-generate resources, use
 * java -cp core/utils/target/utils-2.0.1-SNAPSHOT.jar
 *      ead.utils.i18n.ResourceCreator core/editor-core ead.editor
 *      etc/LICENSE.txt core/editor-core/src/main/java/ead/editor/R.java
 */
public class EAdventureEditor implements Launcher {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(EAdventureEditor.class);
    /**
     * Controller for the view
     */
    private ViewController viewController;


	/**
	 * Main entry point into the editor.
	 * @param args the first argument, if set, is understood to be a game file
	 */
	public static void main(String[] args) {

        // The following line is used by MacOS X to set the application name correctly
        System.setProperty("com.apple.mrj.application.apple.menu.about.name",
				"eAdventure");

        // Initialize logging
        Log4jConfig.configForConsole(Log4jConfig.Slf4jLevel.Info, new Object[]{
            "ModelVisitorDriver", Log4jConfig.Slf4jLevel.Info,
            "EditorModel", Log4jConfig.Slf4jLevel.Info,
            "NullAnnotator", Log4jConfig.Slf4jLevel.Debug,
            "EAdventureImporter", Log4jConfig.Slf4jLevel.Debug,
            "ead.utils.i18n.I18N", Log4jConfig.Slf4jLevel.Debug,
            "EWindowImpl", Log4jConfig.Slf4jLevel.Debug,
            "QueryNode", Log4jConfig.Slf4jLevel.Debug,
            "ModelIndex", Log4jConfig.Slf4jLevel.Debug,
        });

		// show splash
        SplashScreen splashScreen = new SplashScreenImpl();
        splashScreen.show();

		// initialize launcher
        Injector injector = Guice.createInjector(
                new EditorGuiceModule(),
                new ImporterConfigurationModule(),
                new JavaBasicGameModule(),
                new DesktopModule(),
                new DesktopAssetHandlerModule());
        Launcher launcher = injector.getInstance(Launcher.class);
		EditorModel model = injector.getInstance(EditorModel.class);
		Controller controller = injector.getInstance(Controller.class);
		controller.setModel(model);

        launcher.configure();
        launcher.initialize();

		// hide splash & launch app
		splashScreen.hide();
        launcher.start();
    }

    @Inject
    public EAdventureEditor(ViewController viewController) {
        this.viewController = viewController;
    }

    @Override
    public void configure() {
        logger.info("Configuring...");
    }

    @Override
    public void initialize() {
        logger.info("Initializing...");
        viewController.initialize();
    }

    @Override
    public void start() {
        logger.info("Starting...");
        viewController.showWindow();
    }
}
