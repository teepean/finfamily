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

package fi.kaila.suku.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * A servlet for tomcat that is used in the webstart version The webstart
 * version implementation is not in a very active state at the moment Thus fixme
 * and warnings may not be.
 *
 * @author FIKAAKAIL
 */
public class SukuServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	// FIXME: Class fi.kaila.suku.server.SukuServlet defines non-transient
	// non-serializable instance field ctx. This Serializable class defines a
	// non-primitive instance field
	// which is neither transient, Serializable, or java.lang.Object, and does
	// not appear to implement the Externalizable interface or the readObject()
	// and writeObject() methods. Objects of this class will not be deserialized
	// correctly if a non-Serializable object is stored in this field.
	// Mika: Maybe you should set it transient?
	private Context ctx = null;

	private HashMap<String, UserInfo> usermap = null;
	private static volatile int usercount = 0;

	private static Logger logger;

	private String dbServer = null;
	private String dbDatabase = null;
	private String dbUser = null;
	private String dbPassword = null;

	// private String filesPath = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() {
		Context ic = null;
		// String tmp;
		try {
			ic = new InitialContext();
			this.ctx = (Context) ic.lookup("java:comp/env");

			// tmp = initEnv("suku.db.driver");
			// if (tmp != null) this.dbDriver = tmp;

			this.dbServer = initEnv("suku.db.server");
			this.dbDatabase = initEnv("suku.db.database");
			this.dbUser = initEnv("suku.db.user");
			this.dbPassword = initEnv("suku.db.password");
			// this.filesPath = initEnv("suku.files.path");
			// myTablePrefix = initEnv("forum.table.prefix");

		} catch (final Exception e) {
			e.printStackTrace();
		}

		this.usermap = new HashMap<String, UserInfo>();
		logger = Logger.getLogger(this.getClass().getName());

	}

	private static final String hexi = "0123456789ABCDEF";

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final String referer = req.getHeader("referer");

		SukuData requestData = null;
		if (referer != null) {
			final String parts[] = referer.split("/");
			logger.fine("Post referer [" + parts.length + "]: " + referer);
			if (parts.length >= 2) {

				final UserInfo ui = this.usermap.get("" + parts[2]);

				if (ui == null) {
					logger.fine("parts1 " + parts[2]);
				} else {
					if (parts.length > 3) {

						logger.fine("parts3 " + parts[3]);
						requestData = extractFile(req, parts[3]);
						ui.openFile = requestData.generalText;
					} else {
						logger.fine("parts2 " + parts[2]);
						requestData = extractSukuData(req);
					}
				}
			}
		}
		logger.info("recdata:" + requestData);
		processRequest(requestData, req, resp);

	}

	private SukuData extractSukuData(HttpServletRequest req) {
		int input = -1;
		try {
			final InputStream is = req.getInputStream();
			final ByteArrayOutputStream boss = new ByteArrayOutputStream();
			boolean endParams = false;
			final StringBuilder params = new StringBuilder();
			while ((input = is.read()) >= 0) {
				final char c = (char) input;
				if (!endParams) {
					if (c == '\r') {
						// TODO
					} else if (c == '\n') {
						if (params.length() > 0) {
							endParams = true;
						}
					} else {
						params.append(c);
					}

				} else {
					if (c != '\r') {
						boss.write((byte) c);
					}
				}
			}
			logger.info("params:" + params.toString());
			final String prm = URLDecoder.decode(params.toString(), "UTF-8");

			final byte buffi[] = boss.toByteArray();
			logger.info("prm:" + prm + " : buffi:" + buffi.length);
			String rivi = "XXX";

			int kurre = 0;
			int koko = 0;
			final ByteArrayOutputStream oss = new ByteArrayOutputStream();
			final byte brivi[] = new byte[32 * 1024];

			StringBuilder sb = null;
			for (int idx = 0; idx < buffi.length; idx++) {
				if ((buffi[idx] == '\n')) {

					sb = new StringBuilder();
					for (int j = kurre; j < idx; j++) {
						if (buffi[j] >= '0') {
							sb.append(new String(buffi, j, 1, "US-ASCII"));
						}
					}
					rivi = sb.toString();
					kurre = idx;
					// logger.fine("rivix: " + rivi + " : " + rivi.length());

					if (rivi.indexOf("*****") > 0) {
						break;
					}

					if ((rivi.length() > 0) && ((rivi.length() & 1) == 0)) {

						int bi = 0;
						int x1, x2;
						if (hexi.indexOf(rivi.charAt(0)) >= 0) {

							for (int j = 0; j < (rivi.length() - 1); j += 2) {
								x1 = Integer.parseInt(rivi.substring(j, j + 1), 16);
								x2 = Integer.parseInt(rivi.substring(j + 1, j + 2), 16);

								brivi[bi++] = (byte) (((x1 << 4) & 0xf0) | (x2 & 0xf));
								// if (riviYksi) {
								// logger.fine("x1 " + x1 + ", x2 " + x2
								// + " bi " + bi);
								// }

							}
							oss.write(brivi, 0, bi);
							// if (riviYksi) {
							// oss.toByteArray();
							// riviYksi = false;
							// }
							koko += bi;

						}

					}

				}

			}
			// logger.info("muutetaan objektiksi: : oss: " + oss.size());
			final ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(oss.toByteArray()));
			final SukuData suku = (SukuData) obj.readObject();
			logger.fine("oli: " + suku);
			// if (suku != null) {
			// if (suku.persLong != null) {
			// logger.fine("tuli persLong:" + suku.persLong.getPid() + ":"
			// + prm);
			// } else if (suku.pers != null) {
			// logger.fine("tuli persShortlkm:" + suku.pers.length + ":"
			// + prm);
			// }
			// } else {
			// logger.fine("ei tuli jotain:" + suku + ":" + prm);
			// }
			suku.cmd = prm;
			return suku;

		} catch (final Exception e) {

			e.printStackTrace();
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// System.out.println("GET");
		processRequest(null, req, resp);
	}

	@SuppressWarnings("rawtypes")
	private void processRequest(SukuData sukuData, HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// Enumeration enu = req.getHeaderNames();
		// String nimi;
		// String value;
		// String refers[]=null;
		// while (enu.hasMoreElements()){
		// nimi = (String)enu.nextElement();
		// value = req.getHeader(nimi);
		// System.out.println("x:" + nimi + ":" + value);
		// if (nimi.equalsIgnoreCase("referer")){
		// refers = value.split("/") ;
		// }
		//
		// }
		// System.out.println("-------------");
		// enu = req.getAttributeNames();
		// while (enu.hasMoreElements()){
		// nimi = (String)enu.nextElement();
		// value = req.getHeader(nimi);
		// System.out.println("x:" + nimi + ";" + value);
		// }
		// System.out.println("-------------");
		// enu = req.getParameterNames();
		// while (enu.hasMoreElements()){
		// nimi = (String)enu.nextElement();
		// value = req.getParameter(nimi);
		// System.out.println("x:" + nimi + ";" + value);
		// }
		// System.out.println("-------------");
		//
		//

		String cmd;
		String userid;
		String passwd;
		// String pid;
		String file;
		// String lang;
		// String filename;

		String uno;
		int userno = 0;
		final LinkedHashMap<String, String> vpara = new LinkedHashMap<String, String>();

		if (sukuData == null) {

			final Enumeration enu = req.getParameterNames();
			String key;
			final StringBuilder sbx = new StringBuilder();
			while (enu.hasMoreElements()) {
				key = (String) enu.nextElement();
				vpara.put(key, req.getParameter(key));
				if (sbx.length() > 0) {
					sbx.append(";");
				}
				sbx.append(key + "=" + req.getParameter(key));
			}

			// logger.fine("parms: " + sbx.toString());

			// cmd = req.getParameter("cmd");
			// vpara.remove("cmd");
		} else {
			final String[] requs = sukuData.cmd.split("&");
			for (final String requ : requs) {
				final String parms[] = requ.split("=");
				vpara.put(parms[0], parms[1]);
			}

		}

		userid = vpara.get("userid");
		passwd = vpara.get("passwd");
		uno = vpara.get("userno");
		cmd = vpara.get("cmd");
		file = vpara.get("file");

		logger.info("into servlet: cmd=" + cmd + ", file=" + file);
		String params[] = null;

		if (uno != null) {
			try {
				userno = Integer.parseInt(uno);
			} catch (final NumberFormatException e) {
				// TODO: Ignored?
			}
		}

		if ((userid != null) && (passwd != null)) {
			userno = ++usercount;
			final UserInfo ui = new UserInfo("" + userno, userid, passwd);
			this.usermap.put("" + userno, ui);
			final PrintWriter out = resp.getWriter();
			resp.setHeader("Content-Type", "text/html");
			out.println("" + userno + "/" + AntVersion.antVersion);

			final Cookie cok = new Cookie("userid", "" + userno);
			cok.setMaxAge(1800);
			resp.addCookie(cok);

			return;
		}

		final UserInfo ui = this.usermap.get("" + userno);

		final ArrayList<String> v = new ArrayList<String>();

		final Set<Map.Entry<String, String>> entries = vpara.entrySet();
		final Iterator<Map.Entry<String, String>> it = entries.iterator();

		while (it.hasNext()) {
			final Map.Entry<String, String> entry = it.next();
			v.add(entry.getKey().toString() + "=" + entry.getValue().toString());
		}

		params = v.toArray(new String[0]);

		if ((cmd == null) || (ui == null) || (userno == 0)) {
			logger.info("cmd=null");
			final PrintWriter out = resp.getWriter();
			resp.setHeader("Content-Type", "text/html");
			out.println("FinFamily");
			return;
		}

		if (cmd.equals("logout")) { // logout request
			logger.info("cmd=logout");
			// resp.setHeader("Content-Type", "text/html");

			this.usermap.remove("" + userno);

		}

		resp.setHeader("Content-Type", "text/html");
		SukuServer sk;

		try {
			// TODO: FixMe
			sk = new SukuServerImpl(ui.getUserId(), ui.openFile, false);

			resp.addHeader("Content-Encoding", "gzip");
			final ServletOutputStream sos = resp.getOutputStream();
			logger.fine("log: " + this.dbServer + "/" + this.dbDatabase + "/" + this.dbUser + "/" + this.dbPassword);

			// TODO: FixMe
			sk.getConnection(this.dbServer, this.dbDatabase, this.dbUser, this.dbPassword, false);

			final SukuData fam = sk.getSukuData(sukuData, params);

			logger.fine("Returning fam: " + fam);

			GZIPOutputStream gos;
			// ByteArrayOutputStream gos;
			logger.info("cmd:" + fam.cmd);
			try {
				gos = new GZIPOutputStream(sos);
				// gos = new ByteArrayOutputStream(sos);
				final ObjectOutputStream oos = new ObjectOutputStream(gos);
				oos.writeObject(fam);
				oos.close();
				// gos.close();
				logger.info("oos: closed");
			} catch (final Exception e) {
				logger.log(Level.WARNING, "GZIP send", e);
				throw new SukuException("getShortPersonList", e);

			}

		} catch (final SukuException e) {
			logger.log(Level.WARNING, "servlet", e);

		}

		return;

	}

	private String initEnv(String text) {
		try {
			return (String) this.ctx.lookup(text);
		} catch (final NamingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This is from the old servlet
	 *
	 * @param req
	 * @param outputPath
	 * @param fileName
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	private SukuData extractFile(HttpServletRequest req, String fileName)
			throws IOException, UnsupportedEncodingException, FileNotFoundException {
		final SukuData suku = new SukuData();
		int input = -1;
		try {
			final InputStream is = req.getInputStream();

			final ByteArrayOutputStream boss = new ByteArrayOutputStream();
			boolean endParams = false;
			final StringBuilder params = new StringBuilder();
			while ((input = is.read()) >= 0) {
				final char c = (char) input;
				if (!endParams) {
					if (c == '\r') {
						// TODO:
					} else if (c == '\n') {
						if (params.length() > 0) {
							endParams = true;
						}
					} else {
						params.append(c);
					}
				} else {
					if (c != '\r') {
						boss.write((byte) c);
					}
				}
			}

			suku.cmd = URLDecoder.decode(params.toString(), "UTF-8");

			logger.info("params:" + params.toString() + ":" + suku);
			final byte buffi[] = boss.toByteArray();
			// logger.info("buffi:" + buffi.length);
			String rivi = "XXX";

			int kurre = 0;
			int koko = 0;

			// File f = new File(outputPath + "/" + fileName);

			// logger.fine("file exists1: " + f.exists() + ": "
			// + f.getAbsolutePath());
			// try {
			// if (f.createNewFile()) {
			// f.delete();
			// } else {
			// f.delete();
			// }
			// logger.fine("file exists2: " + f.exists() + ": "
			// + f.getAbsolutePath());
			// } catch (Exception ee) {
			// logger.fine("create ex:" + ee.toString());
			// }
			String postFix = null;
			final int idxType = fileName.lastIndexOf(".");
			if (idxType > 0) {
				postFix = fileName.substring(idxType);
			}

			final File t = File.createTempFile("finfamily", postFix);
			logger.fine("temp cerated [" + t.exists() + ") : at: " + t.getAbsolutePath());
			suku.generalText = t.getAbsolutePath();
			final FileOutputStream foss = new FileOutputStream(t);
			final byte brivi[] = new byte[32 * 1024];

			StringBuilder sb = null;
			for (int idx = 0; idx < buffi.length; idx++) {
				if ((buffi[idx] == '\n')) {

					sb = new StringBuilder();
					for (int j = kurre; j < idx; j++) {
						if (buffi[j] >= '0') {
							sb.append(new String(buffi, j, 1, "US-ASCII"));
						}
					}
					rivi = sb.toString();
					kurre = idx;

					if (rivi.indexOf("*****") > 0) {
						break;
					}

					if ((rivi.length() > 0) && ((rivi.length() & 1) == 0)) {

						int bi = 0;
						int x1, x2;
						if (hexi.indexOf(rivi.charAt(0)) >= 0) {

							for (int j = 0; j < (rivi.length() - 1); j += 2) {
								x1 = Integer.parseInt(rivi.substring(j, j + 1), 16);
								x2 = Integer.parseInt(rivi.substring(j + 1, j + 2), 16);

								brivi[bi++] = (byte) (((x1 << 4) & 0xf0) | (x2 & 0xf));

							}
							foss.write(brivi, 0, bi);

							koko += bi;
						}
					}
				}
			}

			foss.close();

		} catch (final Exception e) {
			logger.log(Level.WARNING, "extractFile", e);

		}
		return suku;
	}

	/**
	 * The Class UserInfo.
	 */
	class UserInfo {
		private String userno = null;
		private String userid = null;
		private String passwd = null;
		private long lastUsed = 0;
		private String openFile = null;

		/**
		 * Instantiates a new user info.
		 *
		 * @param userno
		 *            the userno
		 * @param userid
		 *            the userid
		 * @param passwd
		 *            the passwd
		 */
		UserInfo(String userno, String userid, String passwd) {
			this.userno = userno;
			this.userid = userid;
			this.passwd = passwd;
			this.lastUsed = System.currentTimeMillis();
		}

		/**
		 * Gets the user no.
		 *
		 * @return the user no
		 */
		String getUserNo() {
			this.lastUsed = System.currentTimeMillis();
			return this.userno;
		}

		/**
		 * Gets the user id.
		 *
		 * @return the user id
		 */
		String getUserId() {
			return this.userid;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.userno + "/" + this.userid + "/" + this.passwd + "/" + this.lastUsed;

		}

	}

}
