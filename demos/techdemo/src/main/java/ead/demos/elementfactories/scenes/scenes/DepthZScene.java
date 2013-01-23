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

package ead.demos.elementfactories.scenes.scenes;

import ead.common.model.elements.effects.InterpolationEf;
import ead.common.model.elements.effects.enums.InterpolationLoopType;
import ead.common.model.elements.effects.enums.InterpolationType;
import ead.common.model.elements.effects.variables.ChangeFieldEf;
import ead.common.model.elements.events.SceneElementEv;
import ead.common.model.elements.events.enums.SceneElementEvType;
import ead.common.model.elements.scenes.SceneElement;
import ead.common.model.elements.variables.BasicField;
import ead.common.model.elements.variables.EAdField;
import ead.common.model.elements.variables.operations.ValueOp;
import ead.common.params.fills.ColorFill;
import ead.common.params.fills.Paint;
import ead.common.resources.assets.drawable.basics.shapes.CircleShape;
import ead.common.resources.assets.drawable.basics.shapes.RectangleShape;
import ead.common.util.EAdPosition;
import ead.common.util.EAdPosition.Corner;

public class DepthZScene extends EmptyScene {

	public DepthZScene() {
		this.setId("DepthZScene");
		this.getBackground().setId("DepthZSceneBackground");
		int totalTime = 2000;

		SceneElement e1 = new SceneElement(new RectangleShape(50, 500,
				new Paint(ColorFill.RED, ColorFill.BLACK)));
		e1.setPosition(new EAdPosition(Corner.CENTER, 400, 300));
		getSceneElements().add(e1);
		e1.setId("WallZ");

		SceneElement e2 = new SceneElement(new CircleShape(20, new Paint(
				ColorFill.GREEN, ColorFill.BLACK)));
		e2.setPosition(new EAdPosition(Corner.CENTER, 10, 300));
		getSceneElements().add(e2);
		e2.setId("BallZ");

		EAdField<Integer> xField = new BasicField<Integer>(e2,
				SceneElement.VAR_X);
		EAdField<Integer> zField = new BasicField<Integer>(e2,
				SceneElement.VAR_Z);
		EAdField<Float> scaleField = new BasicField<Float>(e2,
				SceneElement.VAR_SCALE);

		ChangeFieldEf changeZ1 = new ChangeFieldEf(zField, new ValueOp(1));
		ChangeFieldEf changeScale1 = new ChangeFieldEf(scaleField, new ValueOp(
				1.0f));
		InterpolationEf effect1 = new InterpolationEf(xField, 50, 750,
				totalTime, InterpolationLoopType.NO_LOOP,
				InterpolationType.LINEAR);
		ChangeFieldEf changeZ2 = new ChangeFieldEf(zField, new ValueOp(-1));
		ChangeFieldEf changeScale2 = new ChangeFieldEf(scaleField, new ValueOp(
				0.8f));
		InterpolationEf effect2 = new InterpolationEf(xField, 750, 50,
				totalTime, InterpolationLoopType.NO_LOOP,
				InterpolationType.LINEAR);

		changeZ1.getNextEffects().add(effect1);
		effect1.getNextEffects().add(changeScale2);
		effect1.getNextEffects().add(changeZ2);
		changeZ2.getNextEffects().add(effect2);
		effect2.getNextEffects().add(changeScale1);
		effect2.getNextEffects().add(changeZ1);

		SceneElementEv event = new SceneElementEv();
		event.addEffect(SceneElementEvType.FIRST_UPDATE, changeZ1);

		e2.getEvents().add(event);

	}

}
