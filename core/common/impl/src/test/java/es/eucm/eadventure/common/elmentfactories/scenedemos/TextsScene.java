package es.eucm.eadventure.common.elmentfactories.scenedemos;

import es.eucm.eadventure.common.elmentfactories.EAdElementsFactory;
import es.eucm.eadventure.common.model.effects.EAdEffect;
import es.eucm.eadventure.common.model.effects.impl.text.EAdShowText.ShowTextAnimation;
import es.eucm.eadventure.common.model.params.EAdBorderedColor;
import es.eucm.eadventure.common.model.params.EAdFont;
import es.eucm.eadventure.common.resources.assets.drawable.impl.CaptionImpl;
import es.eucm.eadventure.common.resources.assets.impl.EAdURIImpl;

public class TextsScene extends EmptyScene {
	
	public TextsScene( ){
		CaptionImpl caption = EAdElementsFactory.getInstance().getCaptionFactory().createCaption("Show text � ��� !!!! *�", EAdBorderedColor.WHITE_ON_BLACK, EAdBorderedColor.BLACK_ON_WHITE, new EAdFont( new EAdURIImpl( "@binary/DroidSans-Bold.ttf"), 20));
		EAdEffect effect = EAdElementsFactory.getInstance().getEffectFactory().getShowText("This text is showing through an EAdShowText effect", 200, 200, ShowTextAnimation.FADE_IN );
		getSceneElements().add(EAdElementsFactory.getInstance().getSceneElementFactory().createSceneElement(caption, 10, 10, effect));
	}

}