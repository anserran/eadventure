package es.eucm.eadventure.common.impl.importer.subimporters.effects;

import com.google.inject.Inject;

import es.eucm.eadventure.common.EAdElementImporter;
import es.eucm.eadventure.common.data.chapter.conditions.Conditions;
import es.eucm.eadventure.common.data.chapter.effects.HighlightItemEffect;
import es.eucm.eadventure.common.impl.importer.interfaces.EAdElementFactory;
import es.eucm.eadventure.common.model.effects.impl.timedevents.EAdHighlightSceneElement;
import es.eucm.eadventure.common.model.elements.EAdCondition;
import es.eucm.eadventure.common.model.elements.EAdSceneElementDef;

public class HighlightEffectImporter extends
		EffectImporter<HighlightItemEffect, EAdHighlightSceneElement> {

	private EAdElementFactory factory;

	@Inject
	public HighlightEffectImporter(
			EAdElementImporter<Conditions, EAdCondition> conditionImporter,
			EAdElementFactory factory) {
		super(conditionImporter);
		this.factory = factory;
	}

	@Override
	public EAdHighlightSceneElement init(HighlightItemEffect oldObject) {
		return new EAdHighlightSceneElement("highlightActorReference"
				+ ID_GENERATOR++);
	}

	@Override
	public EAdHighlightSceneElement convert(HighlightItemEffect oldObject,
			Object newElement) {
		EAdHighlightSceneElement effect = super.convert(oldObject, newElement);
		effect.setSceneElement((EAdSceneElementDef) factory.getElementById(oldObject.getTargetId()));

		return effect;
	}

}
