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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class SukuKontrollerWebstartImpl.
 *
 * @author FIKAAKAIL
 *
 *         webstart implementation for kontroller
 */
public class SukuKontrollerWebstartImpl implements SukuKontroller {

	private static Logger logger = null;

	private FileContents fc = null;

	/**
	 * constructor sets environment for remote.
	 */
	public SukuKontrollerWebstartImpl() {
		logger = Logger.getLogger(this.getClass().getName());
		try {
			final BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
			if (bs != null) {

				this.codebase = bs.getCodeBase().toString();

			}

		} catch (final UnavailableServiceException e) {

			this.codebase = "http://localhost/suku/";
			e.printStackTrace();

			// JOptionPane.showMessageDialog(null, "Basic service error " +
			// e.toString());
			// throw new SukuException("Basic service error ", e);
		}
	}

	private String codebase = null;
	private String userno = null;
	private String schema = null;
	private boolean isConnected = false;
	private boolean isH2 = false;

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
		schema = null;
		isConnected = false;
		this.isH2 = isH2;
		final String requri = this.codebase + "SukuServlet?userid=" + userid + "&passwd=" + passwd;

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
				this.schema = userid;
				isConnected = true;
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
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String getPref(Object o, String key, String def) {

		String aux;

		try {
			final PersistenceService ps = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
			final BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
			final URL baseURL = bs.getCodeBase();

			final URL editorURL = new URL(baseURL, key);

			final FileContents fc = ps.get(editorURL);
			final DataInputStream is = new DataInputStream(fc.getInputStream());
			aux = is.readUTF();
			is.close();

			return aux;
		} catch (final FileNotFoundException fe) {

			return def;
		} catch (final Exception e) {
			Utils.println(this, "Kaatui: e=" + e);
			e.printStackTrace();
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#putPref(java.lang.Object,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void putPref(Object o, String key, String value) {

		PersistenceService ps;
		try {
			ps = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");

			final BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
			final URL baseURL = bs.getCodeBase();

			final URL keyURL = new URL(baseURL, key);
			FileContents fc = null;
			try {
				fc = ps.get(keyURL);
				ps.delete(keyURL);
			} catch (final FileNotFoundException fe) {
				System.out.println("putPref fnf " + fe.toString());

			}
			ps.create(keyURL, 1024);
			fc = ps.get(keyURL);

			final DataOutputStream os = new DataOutputStream(fc.getOutputStream(false));

			os.writeUTF(value);
			os.flush();
			os.close();
		} catch (final Exception e) {
			System.out.println("putPref e " + e.toString());
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#getSukuData(java.lang.String[])
	 */
	@Override
	public SukuData getSukuData(String... params) throws SukuException {
		return KontrollerUtils.getSukuData(this.codebase, this.userno, params);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fi.kaila.suku.kontroller.SukuKontroller#openLocalFile(java.lang.String)
	 */
	@Override
	public boolean openFile(String filter) {

		FileOpenService fos;
		InputStream iis = null;

		try {
			fos = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");

			fc = fos.openFileDialog(null, null);
			if (fc == null) {
				return false;
			}

			iis = fc.getInputStream();
			final int resu = KontrollerUtils.openFile(this.codebase, this.userno, getFileName(), iis);
			if (resu == 200) {
				return true;
			}
			Utils.println(this, "openFile returned response " + resu);

		} catch (final Exception e) {
			Utils.println(this, e.toString());
			e.printStackTrace();
		}
		return false;

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

		return KontrollerUtils.getSukuData(this.codebase, this.userno, request, params);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileLength()
	 */
	@Override
	public long getFileLength() {
		if (fc != null) {
			try {
				return fc.getLength();
			} catch (final IOException e) {
				Utils.println(this, e.toString());
			}
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

		if (fc != null) {
			try {
				return fc.getInputStream();
			} catch (final IOException e) {
				Utils.println(this, "getInputStream " + e.toString());
				return null;
			}
		}
		Utils.println(this, "getInputStream not found");
		logger.severe("getInputStream not found");
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getFileName()
	 */
	@Override
	public String getFileName() {
		if (fc != null) {
			try {
				return fc.getName();
			} catch (final IOException e) {
				Utils.println(this, "getFileName: " + e.toString());

			}
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

		/**
		 * This isn't yet ready
		 */

		FileSaveService fos;
		try {
			fos = (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");

			final String[] extensions = new String[1];
			extensions[0] = filter;

			final byte[] buffi = { '0', '1', '2', '3' };

			final ByteArrayInputStream in = new ByteArrayInputStream(buffi);

			fc = fos.saveFileDialog(null, extensions, in, "FinFamily");

			return true;
		} catch (final Exception e) {
			Utils.println(this, "createLocal: " + e.toString());

			e.printStackTrace();
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#openFile(java.lang.String)
	 */
	@Override
	public InputStream openLocalFile(String path) {

		try {
			final FileOpenService fos = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");

			fc = fos.openFileDialog(null, null);
			if (fc != null) {
				return fc.getInputStream();
			}
		} catch (final Exception e1) {
			Utils.println(this, "openfile: " + e1.toString());

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
		// return null in webstart
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
	 * @see fi.kaila.suku.kontroller.SukuKontroller#saveFile(java.lang.String,
	 * java.io.InputStream)
	 */
	@Override
	public boolean saveFile(String filter, InputStream in) {
		/**
		 * This isn't yet ready
		 */

		FileSaveService fos;
		try {
			fos = (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");

			final String[] extensions = new String[1];
			extensions[0] = Resurses.getString("FULL_PATHNAME") + " " + filter;

			fc = fos.saveFileDialog(null, extensions, in, "FinFamily" + filter);

			return true;
		} catch (final Exception e) {
			Utils.println(this, "createLocal: " + e.toString());

			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.kontroller.SukuKontroller#isWebStart()
	 */
	@Override
	public boolean isWebStart() {

		return true;
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

		return this.schema;
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
