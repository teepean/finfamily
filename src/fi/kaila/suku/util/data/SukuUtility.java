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

package fi.kaila.suku.util.data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.imports.Read2004XML;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * Utility to manage database.
 *
 * @author FIKAAKAIL 25.7.2007
 */
public class SukuUtility {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private static SukuUtility sData = null;

	/**
	 * Singleton requestor of SukuData instance.
	 *
	 * @return the singleton instance of SukuData
	 */
	public static synchronized SukuUtility instance() {
		if (sData == null) {
			sData = new SukuUtility();
		}
		return sData;

	}

	/**
	 *
	 * Constructor for SukuData. Connection to database is created here
	 *
	 * @throws SukuException
	 *
	 *
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private SukuUtility() {
		//
	}

	/**
	 * Execute sql script.
	 *
	 * @param con
	 *            the con
	 * @param sqlpath
	 *            the sqlpath
	 * @throws SukuException
	 *             the suku exception
	 */
	public void createSukuDb(Connection con, String sqlpath) throws SukuException {
		logger.fine("create db from " + sqlpath);
		InputStreamReader in = null;
		Statement stm = null;
		int lukuri = 0;
		try {
			in = new InputStreamReader(this.getClass().getResourceAsStream(sqlpath), "UTF-8");

			logger.fine("create script at " + in);
			// } catch (UnsupportedEncodingException e1) {
			//
			// logger.log(Level.WARNING, "create", e1);
			// throw new SukuException(e1);
			// }
			//
			//
			// try {
			stm = con.createStatement();

			boolean wasDash = false;
			boolean wasDashDash = false;
			StringBuilder sb = new StringBuilder();

			char c;
			int datal = 0;

			boolean isPastBOM = false;
			while (datal > -1) {
				while ((datal = in.read()) != -1) {
					if (!isPastBOM) {
						if (datal == 65279) {
							datal = in.read();
						}
					}
					isPastBOM = true;
					c = (char) datal;
					if (wasDashDash) {
						if (c == '\n') {
							wasDashDash = false;
							wasDash = false;
						}
					} else if (!wasDash && (c == '-')) {
						wasDash = true;

					} else if (wasDash && (c == '-')) {
						wasDashDash = true;
						wasDash = false;
					} else if (c == ';') {
						break;

					} else {
						if (wasDash) {
							sb.append('-');
						}
						wasDash = false;
						sb.append(c);
					}
				}

				final String sql = sb.toString();

				try {
					stm.executeUpdate(sql);
					lukuri++;
				} catch (final SQLException se) {
					logger.severe(se.getMessage());
				}
				sb = new StringBuilder();

			}
			logger.fine("creates script with  " + lukuri + " sql commands");
		} catch (final Exception e) {
			logger.log(Level.WARNING, "create", e);
			throw new SukuException(e);
		} finally {
			try {
				if (stm != null) {
					stm.close();
				}
			} catch (final SQLException ignored) {
				// SQLException ignored
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException ignored) {
				// IOException ignored
			}
		}
	}

	/**
	 * import Suku 2004 backup file.
	 *
	 * @param con
	 *            the con
	 * @param path
	 *            the path
	 * @param oldCode
	 *            the old code
	 * @param isH2
	 *            the is h2
	 * @return Read2004XML class
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData import2004Data(Connection con, String path, String oldCode, boolean isH2) throws SukuException {
		final Read2004XML x = new Read2004XML(con, oldCode, isH2);
		return x.importFile(path);
	}

}
