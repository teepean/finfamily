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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import fi.kaila.suku.server.SukuServer;
import fi.kaila.suku.server.SukuServerImpl;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class SukuKontrollerLocalImpl.
 *
 * @author FIKAAKAIL
 *
 *         Controller class to implement local application
 */
public class SukuKontrollerLocalImpl implements SukuKontroller {

	private static Preferences sr = null;

	private SukuServer server = null;
	private static Logger logger = null;
	private boolean isConnected = false;
	private boolean isH2 = false;
	private File file = null;
	private Suku host = null;
	private String schema = null;

	/**
	 * Instantiates a new suku kontroller local impl.
	 *
	 * @param host
	 *            the host
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuKontrollerLocalImpl(Suku host) throws SukuException {
		this.host = host;
		this.server = new SukuServerImpl(null, null, isH2);
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
		isConnected = false;
		this.isH2 = isH2;
		this.server.getConnection(host, dbname, userid, passwd, isH2);
		isConnected = true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#resetConnection()
	 */
	@Override
	public void resetConnection() {
		this.server.resetConnection();
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

	/**
	 * get stored parameter from user preferences.
	 *
	 * @param o
	 *            (owner name)
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return value
	 */
	@Override
	public String getPref(Object o, String key, String def) {

		return sr.get(o.getClass().getName() + "." + key, def);
	}

	/**
	 * store value in user preferences.
	 *
	 * @param o
	 *            the o
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	@Override
	public void putPref(Object o, String key, String value) {

		sr.put(o.getClass().getName() + "." + key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		return this.server.getSukuData(params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#openLocalFile(java.lang.String)
	 */
	@Override
	public boolean openFile(String filter) {

		final Preferences sr = Preferences.userRoot();

		final String[] filters = filter.split(";");

		final String koe = sr.get(filters[0], ".");
		logger.fine("Hakemisto on: " + koe);
		// Import2004Dialog d = new Import2004Dialog(null);
		// d.setVisible(true);

		// if (!d.isOK()) return false;

		// String langCode = d.getSelectedLang();
		// String oldCode = "FI"; //d.getSelected2004Lang();

		final JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(new fi.kaila.suku.util.SettingFilter(filter));
		chooser.setDialogTitle("Open " + filter + " file");
		chooser.setSelectedFile(new File(koe + "/."));

		if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		final File f = chooser.getSelectedFile();
		if (f == null) {
			return false;
		}
		final String filename = f.getAbsolutePath();
		file = new File(filename);
		this.server.setLocalFile(filename);

		logger.info("Valittiin: " + filename);

		final String tmp = f.getAbsolutePath().replace("\\", "/");
		final int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
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
		return this.server.getSukuData(request, params);
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
		if (filters.length > 0) {
			final int typeIdx = filename.lastIndexOf(".");
			if (typeIdx <= (filename.length() - 10)) {

				if (!filename.toLowerCase().endsWith(filters[0].toLowerCase())) {
					filename += "." + filters[0];
				}
			}
		}

		file = new File(filename);
		this.server.setLocalFile(filename);

		logger.info("Valittiin: " + filename);

		final String tmp = f.getAbsolutePath().replace("\\", "/");
		final int i = tmp.lastIndexOf("/");

		sr.put(filters[0], tmp.substring(0, i));

		return true;
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
	 * @see fi.kaila.suku.kontroller.SukuKontroller#openFile(java.lang.String)
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
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFilePath()
	 */
	@Override
	public String getFilePath() {
		return file.getAbsolutePath().replace("\\", "/");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isRemote()
	 */
	@Override
	public boolean isRemote() {

		return false;
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
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isConnected()
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
		if (isConnected) {
			return schema;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#setSchema(java.lang.String)
	 */
	@Override
	public void setSchema(String schema) {
		this.schema = schema;
	}

}
