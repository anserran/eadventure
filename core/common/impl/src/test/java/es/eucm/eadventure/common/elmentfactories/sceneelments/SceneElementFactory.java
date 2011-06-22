package es.eucm.eadventure.common.elmentfactories.sceneelments;

import es.eucm.eadventure.common.elmentfactories.EAdElementsFactory;
import es.eucm.eadventure.common.model.elements.EAdSceneElement;
import es.eucm.eadventure.common.model.elements.impl.EAdBasicSceneElement;
import es.eucm.eadventure.common.model.guievents.impl.EAdMouseEventImpl;
import es.eucm.eadventure.common.model.params.EAdPosition;
import es.eucm.eadventure.common.resources.EAdBundleId;
import es.eucm.eadventure.common.resources.assets.AssetDescriptor;

public class SceneElementFactory {
	
	private static int ID_GENERATOR = 0;
	
	/**
	 * Creates an scene element with the given appearance in the given coordinates
	 * @param appearance 
	 * @param x
	 * @param y
	 * @return
	 */
	public EAdBasicSceneElement createSceneElement( AssetDescriptor appearance, int x, int y ){
		EAdBasicSceneElement sceneElement = new EAdBasicSceneElement("sceneElement" + ID_GENERATOR++ );
		sceneElement.getResources().addAsset(sceneElement.getInitialBundle(), EAdBasicSceneElement.appearance, appearance);
		sceneElement.setPosition(new EAdPosition( x, y ));
		return sceneElement;
	}
	
	/**
	 * Creates an scene element which its appearance changes from appearance1 to appearance2 when the mouse enters in it
	 * @param appearance1
	 * @param appearance2
	 * @param x
	 * @param y
	 * @return
	 */
	public EAdSceneElement createSceneElement( AssetDescriptor appearance1, AssetDescriptor appearance2, int x, int y ){
		EAdBasicSceneElement sceneElement = createSceneElement( appearance1, x, y );
		EAdBundleId bundle = new EAdBundleId("bundle2");
		sceneElement.getResources().addBundle(bundle);
		sceneElement.getResources().addAsset(bundle, EAdBasicSceneElement.appearance, appearance2);
		sceneElement.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, EAdElementsFactory.getInstance().getEffectFactory().getChangeAppearance(sceneElement, bundle));
		sceneElement.addBehavior(EAdMouseEventImpl.MOUSE_EXITED, EAdElementsFactory.getInstance().getEffectFactory().getChangeAppearance(sceneElement, sceneElement.getInitialBundle()));	
		return sceneElement;
	}

}
