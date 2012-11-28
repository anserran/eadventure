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

package ead.engine.test.core.platform;

import ead.common.resources.assets.text.BasicFont;
import ead.common.resources.assets.text.EAdFont;
import ead.common.util.EAdRectangle;
import ead.engine.core.platform.FontHandlerImpl;
import ead.engine.core.platform.assets.RuntimeFont;

public class TestFontCache extends FontHandlerImpl {

	@Override
	public void addEAdFont(EAdFont font) {
		this.fontCache.put(font, new RuntimeFont() {

			@Override
			public BasicFont getEAdFont() {
				return null;
			}

			@Override
			public int stringWidth(String string) {
				return string.length();
			}

			@Override
			public int lineHeight() {
				return 1;
			}

			@Override
			public EAdRectangle stringBounds(String string) {
				return new EAdRectangle(0, -1, string.length(), 1);
			}

			@Override
			public boolean loadAsset() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void freeMemory() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isLoaded() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setDescriptor(EAdFont descriptor) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public EAdFont getAssetDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}

		});
	}

}
