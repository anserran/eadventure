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

package es.eucm.ead.engine.gameobjects.effects;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.google.inject.Inject;

import es.eucm.ead.engine.assets.AssetHandler;
import es.eucm.ead.engine.assets.drawables.RuntimeCaption;
import es.eucm.ead.engine.factories.SceneElementFactory;
import es.eucm.ead.engine.game.Game;
import es.eucm.ead.engine.game.interfaces.GUI;
import es.eucm.ead.engine.gameobjects.sceneelements.SceneElementGO;
import es.eucm.ead.model.assets.drawable.basics.EAdCaption;
import es.eucm.ead.model.assets.drawable.basics.EAdShape;
import es.eucm.ead.model.assets.drawable.basics.Image;
import es.eucm.ead.model.assets.drawable.basics.NinePatchImage;
import es.eucm.ead.model.assets.drawable.basics.animation.Frame;
import es.eucm.ead.model.assets.drawable.basics.animation.FramesAnimation;
import es.eucm.ead.model.assets.drawable.basics.shapes.BalloonShape;
import es.eucm.ead.model.assets.drawable.basics.shapes.RectangleShape;
import es.eucm.ead.model.elements.BasicElement;
import es.eucm.ead.model.elements.effects.text.SpeakEf;
import es.eucm.ead.model.elements.enums.CommonStates;
import es.eucm.ead.model.elements.operations.SystemFields;
import es.eucm.ead.model.elements.scenes.GhostElement;
import es.eucm.ead.model.elements.scenes.GroupElement;
import es.eucm.ead.model.elements.scenes.SceneElement;
import es.eucm.ead.model.elements.scenes.SceneElementDef;
import es.eucm.ead.model.params.fills.ColorFill;
import es.eucm.ead.model.params.util.Position;
import es.eucm.ead.model.params.util.Position.Corner;

public class SpeakGO extends AbstractEffectGO<SpeakEf> implements EventListener {

	private GUI gui;

	private SceneElementFactory sceneElementFactory;

	private AssetHandler assetHandler;

	private static final int MARGIN_PROPORTION = 35;

	private static final int HEIGHT_PROPORTION = 4;

	private static final int MARGIN = 30;

	private RuntimeCaption caption;

	private boolean finished;

	private boolean gone;

	private SceneElement textSE;

	private float alpha;

	private String previousState;

	private SceneElementGO bubbleDialog;

	private SceneElementGO effectsHud;

	private SceneElement dots;

	private boolean hasTime;

	private int timePerPart;

	private int currentTime;

	@Inject
	public SpeakGO(Game game, GUI gui, SceneElementFactory sceneElementFactory,
			AssetHandler assetHandler) {
		super(game);
		this.gui = gui;
		this.sceneElementFactory = sceneElementFactory;
		this.assetHandler = assetHandler;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (effect.getStateField() != null) {
			BasicElement element = effect.getStateField().getElement();
			MoveSceneElementGO moving = game.getGameState().getValue(element,
					MoveSceneElementGO.VAR_ELEMENT_MOVING, null);
			if (moving != null) {
				moving.stop();
			}
			previousState = game.getGameState().getValue(
					effect.getStateField(), "");
			game.getGameState().setValue(effect.getStateField(),
					CommonStates.TALKING.toString());
		}
		finished = false;
		alpha = 0.0f;
		gone = false;
		effectsHud = gui.getHUD(GUI.EFFECTS_HUD_ID);
		bubbleDialog = sceneElementFactory.get(this.getVisualRepresentation2());
		bubbleDialog.setInputProcessor(this, true);
		effectsHud.addSceneElement(bubbleDialog);
		hasTime = effect.getTime() > 0;
		if (hasTime) {
			timePerPart = effect.getTime() / caption.getTotalParts();
			currentTime = timePerPart;
		}
	}

	protected GroupElement getVisualRepresentation2() {
		int width = game.getGameState().getValue(SystemFields.GAME_WIDTH, 800);
		int height = game.getGameState()
				.getValue(SystemFields.GAME_HEIGHT, 600);
		int horizontalMargin = width / MARGIN_PROPORTION;
		int verticalMargin = height / MARGIN_PROPORTION;
		int left = horizontalMargin;
		int right = width - horizontalMargin;
		int top = verticalMargin;
		int bottom = height / HEIGHT_PROPORTION + top;

		GroupElement repr = new GroupElement();
		// To catch all clicks
		repr.addSceneElement(new GhostElement(true));

		// Add bubble
		NinePatchImage image = new NinePatchImage(
				"@drawable/speak_balloon.png", 20, 20, 20, 20);
		image.setWidth(right - left);
		image.setHeight(bottom - top);
		SceneElement balloon = new SceneElement(image);
		balloon.setPosition(Corner.CENTER, 0, 0);
		repr.addSceneElement(balloon);

		// Add text
		EAdCaption textCaption = effect.getCaption();
		textCaption.setPadding(MARGIN);
		textCaption.setPreferredWidth(right - left);
		textCaption.setPreferredHeight(bottom - top);
		caption = (RuntimeCaption) assetHandler.getRuntimeAsset(textCaption);
		caption.loadAsset();
		caption.reset();
		SceneElement text = new SceneElement(textCaption);
		text.setPosition(Corner.CENTER, 0, 0);
		repr.addSceneElement(text);

		// Add dots
		FramesAnimation f = new FramesAnimation();
		f.addFrame(new Frame(new Image("@drawable/dots.png"), 500));
		f.addFrame(new Frame(new RectangleShape(1, 1, ColorFill.TRANSPARENT),
				500));
		if (dots == null) {
			dots = new SceneElement(f);
			dots.setAppearance("done", new Image("@drawable/dot.png"));
		}
		game.getGameState().setValue(dots, SceneElement.VAR_BUNDLE_ID,
				SceneElementDef.INITIAL_BUNDLE);
		dots.setPosition(new Position(Corner.CENTER, (right - left) / 2 - 20,
				(bottom - top) / 2 - 20));
		repr.addSceneElement(dots);

		repr.setPosition(Corner.CENTER, width / 2, height / 2);
		return repr;
	}

	protected GroupElement getVisualRepresentation() {
		int width = game.getGameState().getValue(SystemFields.GAME_WIDTH, 800);
		int height = game.getGameState()
				.getValue(SystemFields.GAME_HEIGHT, 600);
		int horizontalMargin = width / MARGIN_PROPORTION;
		int verticalMargin = height / MARGIN_PROPORTION;
		int left = horizontalMargin;
		int right = width - horizontalMargin;
		int top = verticalMargin;
		int bottom = height / HEIGHT_PROPORTION + top;

		EAdShape rectangle;

		if (effect.getX() != null && effect.getY() != null) {

			Float xOrigin = game.getGameState().operate(effect.getX());
			Float yOrigin = game.getGameState().operate(effect.getY());

			xOrigin += (int) effectsHud.getX();
			yOrigin += (int) effectsHud.getY();

			if (yOrigin < height / 2) {
				bottom = height - verticalMargin;
				top = bottom - height / HEIGHT_PROPORTION;
				yOrigin = top - MARGIN * 2f;
			} else {
				yOrigin = bottom + MARGIN * 2f;
			}

			rectangle = new BalloonShape(left, top, right, bottom, effect
					.getBallonType(), xOrigin.intValue(), yOrigin.intValue());
		} else {
			int offsetY = height / 2 - (bottom - top) / 2;
			top += offsetY;
			bottom += offsetY;
			rectangle = new BalloonShape(left, top, right, bottom, effect
					.getBallonType());
		}

		rectangle.setPaint(effect.getBubbleColor());
		EAdCaption text = effect.getCaption();
		text.setPadding(MARGIN);
		text.setPreferredWidth(right - left);
		text.setPreferredHeight(bottom - top);

		textSE = new SceneElement(text);
		textSE.setPosition(new Position(left, top));

		GroupElement complex = new GroupElement(rectangle);
		// To capture clicks all over the screen
		GhostElement bg = new GhostElement();
		bg.setCatchAll(true);
		complex.getSceneElements().add(bg);
		complex.getSceneElements().add(textSE);

		caption = (RuntimeCaption) assetHandler.getRuntimeAsset(text);
		caption.loadAsset();
		caption.reset();

		// Dots
		FramesAnimation f = new FramesAnimation();
		f.addFrame(new Frame(new Image("@drawable/dots.png"), 1000));
		f.addFrame(new Frame(new RectangleShape(1, 1, ColorFill.TRANSPARENT),
				1000));
		dots = new SceneElement(f);
		dots.setAppearance("done", new Image("@drawable/dot.png"));
		dots.setPosition(new Position(Corner.CENTER, right - 35, bottom - 35));

		complex.getSceneElements().add(dots);

		return complex;
	}

	@Override
	public boolean isFinished() {
		return finished && gone;
	}

	public void act(float delta) {
		super.act(delta);

		if (hasTime) {
			currentTime -= delta;
			if (currentTime <= 0) {
				currentTime = timePerPart + currentTime;
				caption.goForward(1);
			}
		}

		if (finished) {
			alpha -= 0.003f * game.getSkippedMilliseconds();
			if (alpha <= 0.0f) {
				alpha = 0.0f;
				gone = true;
			}
		} else {
			if (alpha >= 1.0f) {
				finished = caption.getTimesRead() > 0;
			} else {
				alpha += 0.003f * game.getSkippedMilliseconds();
				if (alpha > 1.0f) {
					alpha = 1.0f;
				}
			}
		}

		bubbleDialog.setAlpha(alpha);

		if (caption.getCurrentPart() == caption.getTotalParts() - 1) {
			game.getGameState().setValue(dots, SceneElement.VAR_BUNDLE_ID,
					"done");
		}
	}

	@Override
	public void finish() {
		end();
		super.finish();
	}

	public void stop() {
		end();
		super.stop();
	}

	public void end() {
		if (effect.getStateField() != null) {
			game.getGameState().setValue(effect.getStateField(), previousState);
		}
		bubbleDialog.remove();
	}

	public boolean isQueueable() {
		return true;
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof InputEvent) {
			InputEvent i = (InputEvent) event;
			event.cancel();
			if (!hasTime && !finished && alpha > 0.9f)
				if (i.getType() == InputEvent.Type.touchDown) {
					if (caption.getTimesRead() >= 1
							|| caption.getCurrentPart() == caption
									.getTotalParts() - 1)
						finished = true;
					else
						caption.goForward(1);
				}
			return true;
		}
		return false;
	}

	public void release() {
		bubbleDialog.free();
	}

}
