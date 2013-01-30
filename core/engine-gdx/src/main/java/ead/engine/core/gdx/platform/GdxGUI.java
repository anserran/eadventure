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

package ead.engine.core.gdx.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;

import ead.engine.core.debuggers.DebuggersHandler;
import ead.engine.core.factories.SceneElementGOFactory;
import ead.engine.core.game.Game;
import ead.engine.core.game.GameState;
import ead.engine.core.gdx.GdxEngine;
import ead.engine.core.input.InputHandler;
import ead.engine.core.platform.AbstractGUI;

public abstract class GdxGUI extends AbstractGUI<SpriteBatch> {

	protected GdxEngine engine;

	@Inject
	public GdxGUI(GdxCanvas canvas, GdxEngine engine) {
		super(canvas);
		this.engine = engine;
	}

	@Override
	public int getSkippedMilliseconds() {
		return (int) (Gdx.graphics.getDeltaTime() * 1000);
	}

	@Override
	public int getTicksPerSecond() {
		return Gdx.graphics.getFramesPerSecond();
	}

	@Override
	public void commit() {
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.commit();
	}

	@Override
	public void initialize(Game game, GameState gameState,
			SceneElementGOFactory sceneElementFactory,
			InputHandler inputHandler, DebuggersHandler debuggerHandler) {
		engine.setGame(game);
		engine.setInputHandler(inputHandler);
		engine.setGUI(this);
		super.initialize(game, gameState, sceneElementFactory, inputHandler,
				debuggerHandler);
	}

	@Override
	public void finish() {
		Gdx.app.exit();
	}
}
