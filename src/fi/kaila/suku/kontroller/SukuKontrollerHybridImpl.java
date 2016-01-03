/**
 * Software License Agreement (BSD License)
 *
 * Copyright 2010-2016 Kaarle Kaila and Mika Halonen. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice, this list of
 *      conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice, this list
 *      of conditions and the following disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY KAARLE KAILA AND MIKA HALONEN ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL KAARLE KAILA OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Kaarle Kaila and Mika Halonen.
 */

package fi.kaila.suku.kontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Hybrid kontroller accesses the remote service from a locally installed
 * FinFamily
 *
 * @author kalle
 *
 */
public class SukuKontrollerHybridImpl implements SukuKontroller {

	private static Logger logger = null;
	private static Preferences sr = null;
	private File file = null;
	private Suku host = null;
	private final String url;
	private String userno = null;

	private String schema = null;

	private boolean isConnected = false;
	private boolean isH2 = false;

	/**
	 * Instantiates a new suku kontroller hybrid impl.
	 *
	 * @param host
	 *            the host
	 * @param url
	 *            the url
	 */
	public SukuKontrollerHybridImpl(Suku host, String url) {
		this.host = host;
		this.url = url;
		sr = Preferences.userRoot();
		logger = Logger.getLogger(this.getClass().getName());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getConnection(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void getConnection(String host, String dbname, String userid, String passwd, boolean isH2)
			throws SukuException {
		final String requri = this.url + "SukuServlet?userid=" + userid + "&passwd=" + passwd;
		schema = null;
		isConnected = false;
		this.isH2 = isH2;
		int resu;

		try {

			final URL url = new URL(requri);
			final HttpURLConnection uc = (HttpURLConnection) url.openConnection();

			final String encoding = uc.getContentEncoding();

			resu = uc.getResponseCode();

			if (resu == 200) {

				InputStream in;
				if ("gzip".equals(encoding)) {
					in = new java.util.zip.GZIPInputStream(uc.getInputStream());
				} else {
					in = uc.getInputStream();
				}

				final byte b[] = new byte[1024];

				int pit = in.read(b);
				for (int i = 0; i < pit; i++) {
					if ((b[i] == '\n') || (b[i] == '\r')) {
						pit = i;
						break;
					}
				}
				final String aux = new String(b, 0, pit);

				final String auxes[] = aux.split("/");

				this.userno = auxes[0];
				if (auxes.length > 1) {
					Suku.serverVersion = auxes[1];
				}

				in.close();
				isConnected = true;
				schema = userid;
			} else {
				throw new Exception();
			}

		} catch (final Exception e) {
			throw new SukuException(Resurses.getString("ERR_NOT_CONNECTED") + " [" + e.toString() + "]");
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#resetConnection()
	 */
	@Override
	public void resetConnection() {
		isConnected = false;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#setDBType()
	 */
	@Override
	public void setDBType(boolean dbtype) {
		isH2 = dbtype;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(String... params) throws SukuException {

		return KontrollerUtils.getSukuData(this.url, this.userno, params);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(fi.kaila.suku.util
	 * .pojo.SukuData, java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(SukuData request, String... params) throws SukuException {
		return KontrollerUtils.getSukuData(this.url, this.userno, request, params);

	}

	private String openDiskFile(String filter) {
		final Preferences sr = Preferences.userRoot();

		final String[] filters = filter.split(";");

		final String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);

		final JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Open " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		final File f = chooser.getSelectedFile();
		if (f == null) {
			return null;
		}
		final String filename = f.getAbsolutePath();
		file = new File(filename);

		logger.info("Valittiin: " + filename);

		final String tmp = f.getAbsolutePath().replace("\\", "/");
		final int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return tmp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#openFile(java.lang.String)
	 */
	@Override
	public boolean openFile(String filter) {

		final String path = openDiskFile(filter);
		if (path == null) {
			return false;
		}
		try {
			final InputStream iis = new FileInputStream(path);
			final int resu = KontrollerUtils.openFile(this.url, this.userno, getFileName(), iis);
			if (resu == 200) {
				return true;
			}
			logger.warning("openFile returnded response " + resu);
		} catch (final Exception e) {
			logger.log(Level.WARNING, "open file", e);
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileLength()
	 */
	@Override
	public long getFileLength() {
		if (file != null) {
			return file.length();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		if (file != null) {
			try {
				return new FileInputStream(file);
			} catch (final FileNotFoundException e) {
				logger.log(Level.WARNING, "Failed to get input stream for file", e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileName()
	 */
	@Override
	public String getFileName() {
		if (file != null) {
			return file.getName();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFilePath()
	 */
	@Override
	public String getFilePath() {
		return file.getAbsolutePath().replace("\\", "/");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String getPref(Object o, String key, String def) {
		return sr.get(o.getClass().getName() + "." + key, def);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#putPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void putPref(Object o, String key, String value) {
		sr.put(o.getClass().getName() + "." + key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#createLocalFile(java.lang.String)
	 */
	@Override
	public boolean createLocalFile(String filter) {
		final Preferences sr = Preferences.userRoot();

		final String[] filters = filter.split(";");

		final String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);

		final JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Create " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showSaveDialog(host) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		final File f = chooser.getSelectedFile();
		if (f == null) {
			return false;
		}

		String filename = f.getAbsolutePath();
		if (filename == null) {
			return false;
		}
		if (f.exists()) {
			final int answer = JOptionPane.showConfirmDialog(host,
					Resurses.getString("FILE_EXISTS") + " [" + filename + "] \n" + Resurses.getString("FILE_OVERWRITE"),
					Resurses.getString(Resurses.SUKU), JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		if (filters.length == 1) {
			if (!filename.toLowerCase().endsWith(filters[0].toLowerCase())) {
				filename += "." + filters[0];
			}
		}

		file = new File(filename);

		logger.info("Valittiin: " + filename);

		final String tmp = f.getAbsolutePath().replace("\\", "/");
		final int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#saveFile(java.lang.String,
	 * java.io.InputStream)
	 */
	@Override
	public boolean saveFile(String filter, InputStream in) {
		if (createLocalFile(filter)) {
			try {
				final byte[] buffer = new byte[1024];

				int readBytes = 0;
				final FileOutputStream fos = new FileOutputStream(file);
				while ((readBytes = in.read(buffer)) >= 0) {
					fos.write(buffer, 0, readBytes);
				}

				in.close();
				fos.close();
				return true;
			} catch (final Exception e) {
				logger.log(Level.INFO, "saveFile", e);

			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws FileNotFoundException {
		if (file != null) {
			return new FileOutputStream(file);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#openLocalFile(java.lang.String)
	 */
	@Override
	public InputStream openLocalFile(String path) {
		if (openFile(path)) {
			if (path != null) {
				if (file == null) {
					return null;
				}

				final String mainPath = file.getAbsolutePath();

				try {
					return new FileInputStream(mainPath);
				} catch (final FileNotFoundException e) {
					logger.log(Level.WARNING, e.getMessage());

				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isRemote()
	 */
	@Override
	public boolean isRemote() {

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isWebStart()
	 */
	@Override
	public boolean isWebStart() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isConnected()
	 */
	@Override
	public boolean isConnected() {

		return isConnected;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isH2()
	 */
	@Override
	public boolean isH2() {

		return isH2;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getSchema()
	 */
	@Override
	public String getSchema() {

		return schema;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#setSchema(java.lang.String)
	 */
	@Override
	public void setSchema(String schema) {

	}

}
