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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.eucm.ead.editor.view.components;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.nodes.DependencyNode;
import es.eucm.ead.editor.util.i18n.Resource;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates EditorLinks for EditorNodes.
 *
 * @author mfreire
 */
public class EditorLinkFactory {

	static private Logger logger = LoggerFactory
			.getLogger(EditorLinkFactory.class);

	public static EditorLink createLink(int id, Controller controller) {
		return createLink(controller.getModel().getNode(id), controller);
	}

	public static EditorLink createLink(DependencyNode node,
			Controller controller) {
		if (node.isManaged()) {
			logger.debug("{} is managed! going upstream to {}", new Object[] {
					node.getId(), node.getManager().getId() });
			return createLink(node.getManager(), controller);
		}
		ImageIcon icon = new ImageIcon(Resource.loadImage(node.getLinkIcon()));
		logger.debug("Creating link for id {} with icon {}", new Object[] {
				node.getId(), icon });
		return new EditorLink(node.getLinkText(), "" + node.getId(), icon,
				controller);
	}
}
