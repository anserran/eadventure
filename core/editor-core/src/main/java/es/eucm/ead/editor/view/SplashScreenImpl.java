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

package es.eucm.ead.editor.view;

import es.eucm.ead.editor.R;
import es.eucm.ead.editor.util.i18n.Resource;
import es.eucm.ead.editor.util.SwingUtilities;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Default implementation of the eAdventure editor splash screen.
 */
public class SplashScreenImpl implements SplashScreen {

	/**
	 * Logger
	 */
	static private Logger logger = LoggerFactory
			.getLogger(SplashScreenImpl.class);

	/**
	 * The splash screen dialog
	 */
	protected SplashScreenDialog splashScreenDialog = null;

	/**
	 * The time at which the splash screen appeared
	 */
	private long startTime;
	/**
	 * The minimum time to display the splash screen
	 */
	private static final long MIN_TIME = 1000;

	@Override
	public void show() {
		logger.info("Showing Splash-screen");

		final Image image = Resource.loadImage(R.Drawable.SplashScreenLogo_png);

		SwingUtilities.doInEDTNow(new Runnable() {

			@Override
			public void run() {
				splashScreenDialog = new SplashScreenDialog(image);
				splashScreenDialog.setUndecorated(true);

				int width = image.getWidth(null);
				int height = image.getHeight(null);
				splashScreenDialog.setSize(width, height);
				splashScreenDialog.setResizable(false);

				Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				double screenWidth = screenSize.getWidth();
				double screenHeight = screenSize.getHeight();
				int locX = Math.round(((int) screenWidth - width) / 2.0f);
				int locY = Math.round(((int) screenHeight - height) / 2.0f);
				splashScreenDialog.setLocation(locX, locY);
				splashScreenDialog.setCursor(Cursor
						.getPredefinedCursor(Cursor.WAIT_CURSOR));

				splashScreenDialog.setVisible(true);
			}
		});

		startTime = System.currentTimeMillis();
	}

	@Override
	public void hide() {
		logger.info("Hiding Splash-screen");

		if (System.currentTimeMillis() - startTime < MIN_TIME) {
			try {
				Thread.sleep(MIN_TIME
						- (System.currentTimeMillis() - startTime));
			} catch (Exception e) {
				logger.warn("Sleep failed within splash");
			}
		}

		SwingUtilities.doInEDTNow(new Runnable() {

			@Override
			public void run() {
				splashScreenDialog.setVisible(false);
				splashScreenDialog.dispose();
			}
		});
	}

	/**
	 * The actual splash screen dialog, that shows the image, the message and
	 * dots following it to show the user that the program is loading.
	 */
	public static class SplashScreenDialog extends JDialog {

		private static final long serialVersionUID = 7388884935911211935L;

		/**
		 * The panel that actually displays stuff
		 */
		private JPanel splashPanel;
		/**
		 * The number of dots to be drawn at the end of the message
		 */
		private int status = 0;
		/**
		 * Timer used to show the dots at the end of the message
		 */
		private javax.swing.Timer timer;

		/**
		 * Enables double-buffering by default
		 */
		public SplashScreenDialog(final Image image) {
			super.getRootPane().setDoubleBuffered(true);

			splashPanel = new JPanel() {
				@Override
				public void paintComponent(Graphics g) {
					g.drawImage(image, 0, 0, null);
					StringBuilder message = new StringBuilder(
							Messages.splash_screen_loading_message + " ");
					for (int i = 0; i < status; i++) {
						message.append(".");
					}
					g.drawString(message.toString(), 100, 265);
					status = ++status % 5;
					timer.start();
				}
			};
			add(splashPanel);

			timer = new javax.swing.Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					splashPanel.repaint();
				}
			});
		}
	}
}
