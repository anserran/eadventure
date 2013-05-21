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

package ead.exporter;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;

import java.io.File;
import java.util.ArrayList;

public class GeneralExporter implements Exporter {

	private ArrayList<Exporter> exporters;

	private JarExporter jarExporter;
	private ApkExporter apkExporter;
	private WarExporter warExporter;

	public GeneralExporter() {
		try {
			Maven maven = new DefaultMaven();
			jarExporter = new JarExporter();
			apkExporter = new ApkExporter(maven);
			warExporter = new WarExporter();
			exporters = new ArrayList<Exporter>();
			exporters.add(jarExporter);
			exporters.add(warExporter);
			exporters.add(apkExporter);
		} catch (Exception e) {

		}
	}

	@Override
	public void setName(String name) {
		for (Exporter e : exporters) {
			e.setName(name);
		}
	}

	@Override
	public void setIcon(File icon) {
		for (Exporter e : exporters) {
			e.setIcon(icon);
		}
	}

	@Override
	public void export(String gameBaseDir, String outputfolder) {
		for (Exporter e : exporters) {
			e.export(gameBaseDir, outputfolder);
		}
	}

	public void export(String gameBaseDir, String outputfolder, boolean jar,
			boolean war, boolean apk) {
		if (jar) {
			jarExporter.export(gameBaseDir, outputfolder);
		}

		if (war) {
			warExporter.export(gameBaseDir, outputfolder);
		}

		if (apk) {
			apkExporter.export(gameBaseDir, outputfolder);
		}
	}

	public void setInstallApk(boolean selected) {
		apkExporter.setRunInDevice(selected);
	}

	public void setWarPath(String warPath) {
		warExporter.setWarPath(warPath);
	}

	public void setJarPath(String jarPath) {
		jarExporter.setJarPath(jarPath);
	}

}
