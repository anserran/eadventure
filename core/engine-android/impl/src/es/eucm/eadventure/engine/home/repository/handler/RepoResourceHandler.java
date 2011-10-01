package es.eucm.eadventure.engine.home.repository.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import es.eucm.eadventure.engine.home.saved.LoadGamesArray;
import es.eucm.eadventure.engine.home.utils.filters.EADFileFilter;
import es.eucm.eadventure.engine.home.utils.filters.PNGFilter;
import es.eucm.eadventure.engine.home.utils.filters.TxtFilter;
import es.eucm.eadventure.engine.home.utils.directory.Paths;

/**
 * Collection of methods for managing the repository resources and connections 
 * 
 * @author Roberto Tornero
 */
public class RepoResourceHandler {

	/**
	 * Total size of the downloading buffer
	 */
	private static final int DOWNLOAD_BUFFER_SIZE = 1048576;
	/**
	 * Size of the buffer
	 */
	private static final int BUFFER = 2048;	

	/**
	 * Downloads an image as a Bitmap
	 */
	public static Bitmap DownloadImage(String url_from, ProgressNotifier pn) {

		int last = url_from.lastIndexOf("/");
		String fileName = url_from.substring(last + 1);

		pn.notifyIndeterminate("Downloading "+fileName);
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = getInputStreamFromUrl(url_from);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			pn.notifyError("Repository connection error");
		}

		return bitmap;
	}

	/**
	 * Downloads any kind of file
	 */
	public static void downloadFile(String url_from, String path_to,
			String fileName, ProgressNotifier pt) {

		try {

			HttpGet httpGet = new HttpGet(url_from);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);

			pt.notifyProgress(0, "Connection established");

			File file = new File(path_to, fileName);

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);

				InputStream in;
				try {

					float fileSize = response.getEntity().getContentLength();

					Log.d("fileSize", String.valueOf(fileSize));

					float iterNum = new Float(fileSize / (DOWNLOAD_BUFFER_SIZE)
							+ 1).intValue();

					Log.d("numIter", String.valueOf(iterNum));

					float prIncrement = 100 / iterNum;

					Log.d("prIncrement", String.valueOf(prIncrement));

					Float progress = new Float(0);

					in = response.getEntity().getContent();
					byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
					int len1 = 0;
					while ((len1 = in.read(buffer)) != -1) {
						fos.write(buffer, 0, len1);

						progress += len1 / fileSize * 100;

						pt.notifyProgress(progress.intValue(), "Downloading "
								+ fileName);

						Log.d("progress", String.valueOf(progress));

					}

					fos.close();
					in.close();

				} catch (IOException e) {

					pt.notifyError("Repository connection error");
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {

				pt.notifyError("Destination file error");
				e.printStackTrace();
			}

		} catch (Exception e1) {
			pt.notifyError("Repository connection error");
			e1.printStackTrace();
		}

	}

	/**
	 * Returns the specified input stream from an url
	 */
	public static InputStream getInputStreamFromUrl(String url) {
		InputStream content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
		} catch (Exception e) {

		}
		return content;
	}

	/**
	 * Makes use of the methods for downloading and unzipping files
	 */
	public static void downloadFileAndUnzip(String url_from, String path_to,
			String fileName, ProgressNotifier pn) {

		downloadFile(url_from, path_to, fileName, pn);
		pn.notifyProgress(100,fileName+" downloaded");
		pn.notifyIndeterminate("Installing " + fileName);
		unzip(path_to,path_to, fileName,true);

	}

	/**
	 * Uncompresses any zip file
	 */
	public static void unzip(String path_from,String path_to, String name,boolean deleteZip) {

		StringTokenizer separator = new StringTokenizer(name, ".", true);
		String file_name = separator.nextToken();

		File f = new File(path_to+file_name);

		if (f.exists())
			removeDirectory(f);

		separator = new StringTokenizer(path_to + file_name, "/", true);

		String partial_path = null;
		String total_path = separator.nextToken();

		while (separator.hasMoreElements()) {

			partial_path = separator.nextToken();
			total_path = total_path + partial_path;
			if (!new File(total_path).exists()) {

				if (separator.hasMoreElements())
					total_path = total_path + separator.nextToken();
				else
					(new File(total_path)).mkdir();

			} else total_path = total_path + separator.nextToken();

		}

		Enumeration<? extends ZipEntry> entries = null;
		ZipFile zipFile = null;

		try {
			String location_ead = path_from + name;
			zipFile = new ZipFile(location_ead);

			entries = zipFile.entries();

			BufferedOutputStream file;

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				separator = new StringTokenizer(entry.getName(), "/", true);
				partial_path = null;
				total_path = "";

				while (separator.hasMoreElements()) {

					partial_path = separator.nextToken();
					total_path = total_path + partial_path;
					if (!new File(entry.getName()).exists()) {

						if (separator.hasMoreElements()) {
							total_path = total_path + separator.nextToken();
							(new File(path_to + file_name + "/" + total_path))
							.mkdir();
						} else {

							file = new BufferedOutputStream(
									new FileOutputStream(path_to + file_name
											+ "/" + total_path));

							System.err.println("Extracting file: "
									+ entry.getName());
							copyInputStream(zipFile.getInputStream(entry), file);
						}
					} else {
						total_path = total_path + separator.nextToken();
					}
				}

			}

			zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		}

		if (deleteZip)
			(new File(path_from + name)).delete();

	}

	/**
	 * Copies one input stream to an output stream
	 */
	public static final void copyInputStream(InputStream in, OutputStream out)
	throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	/**
	 * Checks if the eAdventure resources folder exists
	 */
	public static boolean checkEadDirectory(String path) {
		if (new File(path).exists()) {
			return true;
		} else
			return false;
	}

	/**
	 * Updates the saved games folder
	 */
	public static void updatesavedgames() {

		String[] gameswithsaved = new File(Paths.eaddirectory.SAVED_GAMES_PATH)
		.list();

		for (int i = 0; i < gameswithsaved.length; i++) {

			if (new File(Paths.eaddirectory.SAVED_GAMES_PATH
					+ gameswithsaved[i]).list().length == 0)
				new File(Paths.eaddirectory.SAVED_GAMES_PATH
						+ gameswithsaved[i]).delete();
		}
	}

	/**
	 * Gets the list of saved games from its folder
	 */
	public static LoadGamesArray getexpandablelist() {

		LoadGamesArray info = new LoadGamesArray();

		String[] games = null;
		Bitmap[] screen_shots = null;

		if (!new File(Paths.eaddirectory.SAVED_GAMES_PATH).exists()) {

			new File(Paths.eaddirectory.SAVED_GAMES_PATH).mkdir();

		} else {

			File gamesfolders = new File(Paths.eaddirectory.SAVED_GAMES_PATH);
			games = gamesfolders.list(new EADFileFilter());

			if (games.length > 0) {

				for (int i = 0; i < games.length; i++) {

					File gamefolder = new File(
							Paths.eaddirectory.SAVED_GAMES_PATH + games[i]
							                                            + "/");

					String files[] = gamefolder.list(new TxtFilter());

					String screen_shots_bitmaps[] = gamefolder
					.list(new PNGFilter());

					screen_shots = new Bitmap[files.length];

					for (int j = 0; j < files.length; j++) {

						if (screen_shots_bitmaps.length > j
								&& screen_shots_bitmaps[j] != null) {

							Log.e("Path", screen_shots_bitmaps[j]);

							screen_shots[j] = BitmapFactory.decodeFile(
									Paths.eaddirectory.SAVED_GAMES_PATH
									+ games[i] + "/"
									+ screen_shots_bitmaps[j], null);

						}

						info.addGame(games[i], files[j], screen_shots[j]);

					}

				}
			}
		}

		return info;
	}

	/**
	 * Deletes any kind of file from its path
	 */
	public static void deleteFile(String path) {

		File f = new File(path);

		if (f.exists())

			if (!f.isDirectory())
				f.delete();
			else
				removeDirectory(f);

	}

	/**
	 * Deletes the specified directory
	 */
	public static boolean removeDirectory(File directory) {

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}		

}