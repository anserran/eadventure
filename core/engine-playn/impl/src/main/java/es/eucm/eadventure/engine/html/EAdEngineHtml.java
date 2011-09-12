package es.eucm.eadventure.engine.html;

import com.google.gwt.core.client.GWT;

import es.eucm.eadventure.engine.core.EAdEngine;
import es.eucm.eadventure.engine.core.Game;
import es.eucm.eadventure.engine.core.platform.PlatformLauncher;
import es.eucm.eadventure.engine.core.platform.impl.PlayNGinInjector;
import playn.core.PlayN;
import playn.html.HtmlAssetManager;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

public class EAdEngineHtml extends HtmlGame {

	private final static PlayNGinInjector injector = GWT.create(PlayNGinInjector.class);

	@Override
	public void start() {
	    HtmlAssetManager assets = HtmlPlatform.register().assetManager();
	    assets.setPathPrefix("eAd/");

	    PlatformLauncher launcher = injector.getPlatformLauncher();
	    Game game = injector.getGame();
	   
	    PlayN.run(new EAdEngine(game));
	}

}