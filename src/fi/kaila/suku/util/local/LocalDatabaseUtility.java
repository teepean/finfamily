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

package fi.kaila.suku.util.local;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;

/**
 * Help for login routine.
 *
 * @author Kalle
 */
public class LocalDatabaseUtility {

	private static Logger logger = Logger.getLogger(LocalDatabaseUtility.class.getName());

	/**
	 * Gets the list of databases.
	 *
	 * @param con
	 *            the con
	 * @param isH2
	 *            the is h2
	 * @return list of available databases
	 * @throws SukuException
	 *             the suku exception
	 */
	public static String[] getListOfDatabases(Connection con, boolean isH2) throws SukuException {

		String sql = "";
		if (isH2) {
			sql = "select SCHEMA_NAME from INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME NOT IN ('INFORMATION_SCHEMA','PUBLIC')";
		} else {
			sql = "select datname from pg_database where datname not in ('postgres','template1','template0') order by datname ";
		}
		final StringBuilder sb = new StringBuilder();
		try {
			final Statement stm = con.createStatement();

			final ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				if (rs.getString(1) != "INFORMATION_SCHEMA") {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(rs.getString(1));
				}
			}
			rs.close();

			return sb.toString().split(";");

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "databasenames list", e);

			throw new SukuException(e);
		}

	}

	/**
	 * Gets the list of users.
	 *
	 * @param con
	 *            the con
	 * @param isH2
	 *            the is h2
	 * @return list of available users
	 * @throws SukuException
	 *             the suku exception
	 */
	// cmd.CommandText =
	// "select rolname from pg_roles where rolname != 'postgres' ";
	public static String[] getListOfUsers(Connection con, boolean isH2) throws SukuException {

		String sql = "";
		if (isH2) {
			sql = "select NAME from INFORMATION_SCHEMA.USERS ORDER BY NAME";
		} else {
			sql = "select rolname from pg_roles where rolname != 'postgres' order by rolname ";
		}
		final StringBuilder sb = new StringBuilder();
		try {
			final Statement stm = con.createStatement();

			final ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				if (sb.length() > 0) {
					sb.append(";");
				}
				sb.append(rs.getString(1));
			}
			rs.close();

			return sb.toString().split(";");

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "usernames list", e);

			throw new SukuException(e);
		}

	}

	/**
	 * Gets the list of schemas.
	 *
	 * @param con
	 *            the con
	 * @param isH2
	 *            the is h2
	 * @return the list of schemas
	 * @throws SukuException
	 *             the suku exception
	 */
	public static String[] getListOfSchemas(Connection con, boolean isH2) throws SukuException {
		String sql = "";
		if (isH2) {
			sql = "select SCHEMA_NAME from INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME NOT IN ('INFORMATION_SCHEMA','PUBLIC')";
		} else {
			sql = "select * from pg_namespace where nspname not like 'pg%' and nspname <> 'information_schema' ";
		}
		final StringBuilder sb = new StringBuilder();
		try {
			final Statement stm = con.createStatement();

			final ResultSet rs = stm.executeQuery(sql);

			while (rs.next()) {
				if (sb.length() > 0) {
					sb.append(";");
				}
				sb.append(rs.getString(1));
			}
			rs.close();

			return sb.toString().split(";");

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "schemas list", e);

			throw new SukuException(e);
		}

	}

	/**
	 * Sets the schema.
	 *
	 * @param con
	 *            the con
	 * @param schema
	 *            the schema
	 * @param isH2
	 *            the is h2
	 * @return the string
	 */
	public static String setSchema(Connection con, String schema, boolean isH2) {
		Statement stm;
		String resu = null;
		try {
			stm = con.createStatement();
			if (isH2) {
				stm.executeUpdate("set schema_search_path to " + schema);
			} else {
				stm.executeUpdate("set search_path to " + schema);
			}
			stm.close();
		} catch (final SQLException e) {
			resu = e.getMessage();
			e.printStackTrace();
		}
		return resu;
	}

	/**
	 * Creates the new schema.
	 *
	 * @param con
	 *            the con
	 * @param schema
	 *            the schema
	 * @return the string
	 */
	public static String createNewSchema(Connection con, String schema) {
		String resu = null;
		Statement stm;
		try {
			stm = con.createStatement();
			stm.executeUpdate("create schema " + schema);
			stm.close();
		} catch (final SQLException e) {
			resu = e.getMessage();
			e.printStackTrace();
		}
		return resu;
	}

	/**
	 * Drop named schema from current database.
	 *
	 * @param con
	 *            the con
	 * @param name
	 *            the name
	 * @return possible error string
	 */
	public static String dropSchema(Connection con, String name) {
		String resu = null;
		Statement stm;
		// if (name.equals("public")) {
		// return Resurses.getString("SCHEMA_PUBLIC_NOT_DROPPED");
		// }
		try {
			stm = con.createStatement();
			stm.executeUpdate("drop schema " + name + " cascade");
			stm.executeUpdate("set search_path to public ");
			stm.close();
		} catch (final SQLException e) {
			resu = e.getMessage();
			e.printStackTrace();
		}
		return resu;
	}

}
