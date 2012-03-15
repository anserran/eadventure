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

package ead.engine.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ead.common.ProjectFiles;
import ead.common.StringFileHandler;
import ead.common.importer.EAdventure1XImporter;
import ead.common.importer.ImporterConfigurationModule;
import ead.common.model.elements.EAdAdventureModel;
import ead.common.params.text.EAdString;
import ead.common.reader.EAdAdventureDOMModelReader;
import ead.common.strings.DefaultStringFileHandler;
import ead.elementfactories.demos.scenes.InitScene;
import ead.engine.DesktopGame;
import ead.engine.core.platform.module.DesktopAssetHandlerModule;
import ead.engine.core.platform.module.DesktopModule;
import ead.engine.core.platform.modules.BasicGameModule;

/**
 * Initial frame, showing all the possible options for the engine: run a game,
 * import a old game or launch a test
 * 
 */
public class StartFrame extends JFrame {

	private static final long serialVersionUID = -6973214467232788904L;

	private static final String FILE_CHOOSER_DIRECTORY = "file_chooser_directory";

	private static final String HZ_PROPERTY = "hz_property";

	private static final String PROPERTIES_FILE = "engine_configuration.xml";

	private static final Logger logger = LoggerFactory.getLogger("EAdEngine");

	private static final Integer[] HZ = new Integer[] { 30, 40, 50, 60 };

	private JFileChooser fileChooser;

	private Properties properties;

	private EAdventure1XImporter importer;

	private EAdAdventureDOMModelReader reader;

	private StringFileHandler stringFileHandler;

	private int ticksPerSecond = 30;

	private ProgressDialog progressDialog;

	private File dataFile;

	private File stringsFile;

	public StartFrame() {
		super("eAdventure ");
		setFrameProperties();
		loadProperties();
		initModelHandlers();
		initFileChooser();
		addLogo();
		addOpen();
		addTicksPerSecond();
		pack();
		setLocationRelativeTo(null);

	}

	private void loadProperties() {
		properties = new Properties();
		File f = new File(PROPERTIES_FILE);
		if (f.exists()) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(PROPERTIES_FILE);
				properties.load(in);
			} catch (FileNotFoundException e) {
				logger.warn(PROPERTIES_FILE + " not found");
			} catch (IOException e) {
				logger.warn("{}", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						logger.warn("{}", e);
					}
				}
			}
		} else {
			logger.info("First execution. " + PROPERTIES_FILE
					+ " will be created.");
			try {
				f.createNewFile();
			} catch (IOException e) {
				logger.warn(PROPERTIES_FILE + " wasn't created.");
			}
		}

	}

	private void initModelHandlers() {
		// Importer
		Injector injector = Guice.createInjector(
				new ImporterConfigurationModule(),
				new DesktopAssetHandlerModule(), new DesktopModule(),
				new BasicGameModule());
		importer = injector.getInstance(EAdventure1XImporter.class);

		// Reader
		reader = new EAdAdventureDOMModelReader();

		// Strings file handler
		stringFileHandler = new DefaultStringFileHandler();

		progressDialog = new ProgressDialog(this, importer);
	}

	private void setFrameProperties() {
		try {
			Image i = ImageIO
					.read(ClassLoader
							.getSystemResourceAsStream("ead/resources/drawable/eadventure_mini_logo.png"));
			setIconImage(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(PROPERTIES_FILE);
					properties.setProperty(FILE_CHOOSER_DIRECTORY, fileChooser
							.getCurrentDirectory().getAbsolutePath());
					properties.setProperty(HZ_PROPERTY, ticksPerSecond + "");
					properties.store(out, "-- No comments --");

				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							logger.warn("{}", e);
						}
					}
				}

			}

		});
	}

	private void initFileChooser() {
		fileChooser = new JFileChooser();
		String currentDirectory = properties.getProperty(
				FILE_CHOOSER_DIRECTORY, null);
		if (currentDirectory != null) {
			fileChooser.setCurrentDirectory(new File(currentDirectory));
		}

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return (f.isDirectory() || f.getAbsolutePath().endsWith(".ead") || f
						.getAbsolutePath().endsWith(".eap"));
			}

			@Override
			public String getDescription() {
				return "eAdventure games or porjects (*.ead, *.eap)";
			}

		});
	}

	private void addTicksPerSecond() {
		ticksPerSecond = Integer.parseInt(properties.getProperty(HZ_PROPERTY,
				30 + ""));
		JComboBox hzCombo = new JComboBox(HZ);
		hzCombo.setSelectedItem(ticksPerSecond);
		JPanel panel = new JPanel();
		panel.add(new JLabel("Hz:"));
		panel.add(hzCombo);
		getContentPane().add(panel);
		hzCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent ev) {
				ticksPerSecond = (Integer) ev.getItem();
			}

		});
	}

	private void addLogo() {
		try {
			Image i = ImageIO
					.read(ClassLoader
							.getSystemResourceAsStream("ead/resources/drawable/eadventure_logo_engine.png"));
			JLabel l = new JLabel();
			l.setIcon(new ImageIcon(i));
			l.setSize(i.getWidth(null), i.getHeight(null));
			this.add(l, BorderLayout.NORTH);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void addOpen() {
		JPanel p = new JPanel();
		JButton button = new JButton("Open");
		p.add(button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(StartFrame.this) == JFileChooser.APPROVE_OPTION) {
					loadGame(fileChooser.getSelectedFile());
				}
			}

		});
		add(p, BorderLayout.SOUTH);

		JButton button2 = new JButton("Demos");
		p.add(button2);
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				DesktopGame game = new DesktopGame(new InitScene());
				game.launch(ticksPerSecond);

			}

		});
	}

	private void loadGame(final File f) {

		if (isOldProject(f)) {

			final int result = JOptionPane
					.showConfirmDialog(
							StartFrame.this,
							"Selected file is from an old version of eAdventure. Do you want to save the imported game in a new file?",
							"eAdventure importation",
							JOptionPane.INFORMATION_MESSAGE);

			if (result == JOptionPane.CANCEL_OPTION)
				return;

			new Thread() {

				public void run() {
					EAdAdventureModel model = null;
					String destinyFile = null;
					Map<EAdString, String> strings = null;

					String destiny = null;

					if (result == JOptionPane.YES_OPTION) {
						int fileResult = fileChooser
								.showSaveDialog(StartFrame.this);
						if (fileResult == JFileChooser.CANCEL_OPTION)
							return;
						if (fileResult == JFileChooser.APPROVE_OPTION)
							destiny = fileChooser.getSelectedFile()
									.getAbsolutePath();
					}

					model = importer.importGame(f.getAbsolutePath(), destiny);
					destinyFile = importer.getDestinyFile();
					strings = importer.getStrings();

					if (model != null) {
						DesktopGame game = new DesktopGame(model, destinyFile,
								strings);
						game.launch(ticksPerSecond);
					}
				}
			}.start();

			progressDialog.setVisible(true);

		} else {

			EAdAdventureModel model = null;
			String destinyFile = null;
			Map<EAdString, String> strings = null;

			try {
				FileInputStream in = new FileInputStream(dataFile);
				model = reader.read(in);
				in.close();

				destinyFile = f.getAbsolutePath();

				FileInputStream inStrings = new FileInputStream(stringsFile);
				strings = stringFileHandler.read(inStrings);
				inStrings.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (model != null) {
				DesktopGame game = new DesktopGame(model, destinyFile, strings);
				game.launch(ticksPerSecond);
			}

		}

	}

	private boolean isOldProject(File f) {
		boolean isOldProject = true;
		FileInputStream in = null;
		ZipInputStream zipIn = null;
		try {

			in = new FileInputStream(f);
			zipIn = new ZipInputStream(in);
			ZipEntry zipEntry = null;
			while ((zipEntry = zipIn.getNextEntry()) != null) {
				if (zipEntry.getName().endsWith(ProjectFiles.PROPERTIES_FILE)) {
					isOldProject = false;
				} else if (zipEntry.getName().endsWith(ProjectFiles.DATA_FILE)) {
					dataFile = File.createTempFile("eaddata", Math.random()
							+ "data.xml");
					readZipEntry(zipIn, dataFile);
				} else if (zipEntry.getName().endsWith(
						ProjectFiles.STRINGS_FILE)) {
					stringsFile = File.createTempFile("eaddata", Math.random()
							+ "strings.xml");
					readZipEntry(zipIn, stringsFile);
				}
			}
			zipIn.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}

			if (zipIn != null) {
				try {
					zipIn.close();
				} catch (IOException e) {
				}
			}
		}
		return isOldProject;
	}

	private void readZipEntry(ZipInputStream zipIn, File f) {
		FileOutputStream dataOut = null;
		try {
			dataOut = new FileOutputStream(f);
			byte data[] = new byte[1000];
			int count;
			while ((count = zipIn.read(data, 0, 1000)) != -1) {
				dataOut.write(data, 0, count);
			}
			dataOut.flush();
		} catch (FileNotFoundException e) {
			logger.warn("File not found {}", f);
		} catch (IOException e) {
			logger.warn("IO Exception {}", f);
		} finally {
			if (dataOut != null) {
				try {
					dataOut.close();
				} catch (IOException e) {

				}
			}
		}
	}

}
