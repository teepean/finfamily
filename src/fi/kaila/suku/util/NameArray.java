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

package fi.kaila.suku.util;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Vector;

/**
 * Auxiliary used by import from Suku 2004.
 *
 * @author Kalle
 */
public class NameArray implements Array {

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#free()
	 */
	@Override
	public void free() throws SQLException {
		v.clear();

	}

	private Vector<String> v = null;

	/**
	 * initialize class.
	 */
	public NameArray() {
		v = new Vector<String>();
	}

	/**
	 * append to container vector.
	 *
	 * @param member
	 *            the member
	 */
	public void append(String member) {
		v.add(member);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getArray()
	 */
	@Override
	public Object getArray() throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getArray(java.util.Map)
	 */
	@Override
	public Object getArray(Map<String, Class<?>> arg0) throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getArray(long, int)
	 */
	@Override
	public Object getArray(long arg0, int arg1) throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getArray(long, int, java.util.Map)
	 */
	@Override
	public Object getArray(long arg0, int arg1, Map<String, Class<?>> arg2) throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getBaseType()
	 */
	@Override
	public int getBaseType() throws SQLException {
		return Types.VARCHAR;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getBaseTypeName()
	 */
	@Override
	public String getBaseTypeName() throws SQLException {
		return "varchar";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getResultSet()
	 */
	@Override
	public ResultSet getResultSet() throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getResultSet(java.util.Map)
	 */
	@Override
	public ResultSet getResultSet(Map<String, Class<?>> arg0) throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getResultSet(long, int)
	 */
	@Override
	public ResultSet getResultSet(long arg0, int arg1) throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.sql.Array#getResultSet(long, int, java.util.Map)
	 */
	@Override
	public ResultSet getResultSet(long arg0, int arg1, Map<String, Class<?>> arg2) throws SQLException {

		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < v.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("\"");
			sb.append(toSqlString(v.get(i)));
			sb.append("\"");
		}
		sb.append("}");
		return sb.toString();
	}

	private String toSqlString(String text) {
		if (text == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			switch (c) {
			case '\\':
			case '"':
			case '\'':
				sb.append('\\');
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
