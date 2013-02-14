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

package ead.engine.core.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ead.engine.core.gdx.utils.InvOrtographicCamera;
import ead.engine.core.platform.GUI;
import ead.engine.core.platform.gdx.GdxCanvas;

@Singleton
public class GdxEngineImpl implements GdxEngine {

	private Game game;

	private GUI gui;

	private GdxCanvas canvas;

	private Stage stage;

	private InvOrtographicCamera c;

	private SpriteBatch spriteBatch;

	@Inject
	public GdxEngineImpl(GdxCanvas canvas, Game game, GUI gui) {
		ShaderProgram.pedantic = false;
		this.canvas = canvas;
		this.game = game;
		this.gui = gui;
	}

	@Override
	public void create() {
		game.initialize();

		stage = new Stage();
		spriteBatch = stage.getSpriteBatch();
		spriteBatch.enableBlending();
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,
				GL20.GL_ONE_MINUS_SRC_ALPHA);

		int width = canvas.getWidth();
		int height = canvas.getHeight();

		c = new InvOrtographicCamera();
		float centerX = width / 2;
		float centerY = height / 2;

		c.position.set(centerX, centerY, 0);
		c.viewportWidth = width;
		c.viewportHeight = height;

		canvas.setGraphicContext(spriteBatch);

		stage.setCamera(c);

		Gdx.input.setInputProcessor(stage);

		stage.addActor(gui.getRoot());
		stage.setKeyboardFocus(gui.getRoot());
	}

	@Override
	public void dispose() {
		game.dispose();
		stage.dispose();
	}

	@Override
	public void render() {
		game.act(Gdx.graphics.getDeltaTime());
		gui.getRoot().act(Gdx.graphics.getDeltaTime());
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

}
