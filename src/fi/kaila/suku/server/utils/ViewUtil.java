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

package fi.kaila.suku.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * <h1>Group Server Utility</h1>
 *
 * Group removes and additions to / from db are done here.
 *
 * @author Kalle
 */
public class ViewUtil {

	private static Logger logger = Logger.getLogger(ViewUtil.class.getName());

	private Connection con = null;

	/**
	 * constructor initializes with database connection.
	 *
	 * @param con
	 *            the con
	 */
	public ViewUtil(Connection con) {
		this.con = con;

	}

	/**
	 * remove the view.
	 *
	 * @param viewId
	 *            the view id
	 * @return SukuData with resu != null if error
	 */
	public SukuData removeView(int viewId) {
		final SukuData resu = new SukuData();
		try {
			String sql = "delete from viewunits where vid = ?";

			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, viewId);
			resu.resuCount = pst.executeUpdate();
			pst.close();

			sql = "delete from views where vid = ?";

			pst = con.prepareStatement(sql);
			pst.setInt(1, viewId);
			pst.executeUpdate();
			pst.close();

		} catch (final SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING, "remove view failed", e);
			e.printStackTrace();
		}

		return resu;
	}

	/**
	 * Add new named view.
	 *
	 * @param viewname
	 *            the viewname
	 * @return SukuData with resu != null if error
	 */
	public SukuData addView(String viewname) {
		final SukuData resu = new SukuData();
		try {
			final Statement stm = con.createStatement();
			int vid = 0;
			final ResultSet rs = stm.executeQuery("select nextval('viewseq')");

			if (rs.next()) {
				vid = rs.getInt(1);
			} else {
				throw new SQLException("Sequence viewseq error");
			}
			rs.close();
			stm.close();

			final String sql = "insert into views (vid,name) values (?,?)";

			final PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, vid);
			resu.resultPid = vid;
			pst.setString(2, viewname);
			pst.executeUpdate();
			pst.close();

		} catch (final SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING, "add view failed", e);
			e.printStackTrace();
		}

		return resu;
	}

	/**
	 * get list of views a person is member of.
	 *
	 * @param pid
	 *            the pid
	 * @return SukuData with resu != null if error
	 */
	public SukuData getViews(int pid) {
		final SukuData resu = new SukuData();
		try {
			final String sql = "select name from views where vid in " + "(select vid from viewunits where pid = ?)";
			final PreparedStatement stm = con.prepareStatement(sql);

			stm.setInt(1, pid);
			final ResultSet rs = stm.executeQuery();

			final ArrayList<String> vv = new ArrayList<String>();

			while (rs.next()) {
				vv.add(rs.getString("name"));
			}
			rs.close();
			stm.close();
			resu.generalArray = vv.toArray(new String[0]);

		} catch (final SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING, "add view failed", e);
			e.printStackTrace();
		}

		return resu;
	}

	/**
	 * add list of persons to view.
	 *
	 * @param vid
	 *            the vid
	 * @param pidArray
	 *            the pid array
	 * @param emptyView
	 *            true to empty view first
	 * @return SukuData with resu != null if error
	 */
	public SukuData addViewUnits(int vid, int[] pidArray, boolean emptyView) {
		final SukuData resu = new SukuData();
		resu.resuCount = 0;
		try {
			String sql;
			PreparedStatement stm;
			if (emptyView) {
				sql = "delete from viewunits where vid = ?";
				stm = con.prepareStatement(sql);
				stm.setInt(1, vid);
				stm.executeUpdate();
				stm.close();

			}

			sql = "insert into viewunits (vid,pid) values (?,?)";
			stm = con.prepareStatement(sql);

			for (final int element : pidArray) {
				stm.setInt(1, vid);
				stm.setInt(2, element);
				stm.executeUpdate();
				resu.resuCount++;
			}
			stm.close();

		} catch (final SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING, "add view failed", e);
			e.printStackTrace();
		}

		return resu;
	}

	/**
	 * Add person and his/her descendants to view.
	 *
	 * @param viewId
	 *            the view id
	 * @param pid
	 *            the pid
	 * @param gent
	 *            the gent
	 * @param withSpouses
	 *            the with spouses
	 * @param emptyView
	 *            the empty view
	 * @return SukuData with pidArray with persons added
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addViewDesc(int viewId, int pid, String gent, boolean withSpouses, boolean emptyView)
			throws SukuException {
		final SukuData resp = new SukuData();
		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		resp.resuCount = 0;
		int gen = 0;
		if ((gent != null) && !gent.isEmpty()) {
			gen = Integer.parseInt(gent);
		}

		try {
			String sql;
			PreparedStatement stm;
			if (emptyView) {
				sql = "delete from viewunits where vid = ?";
				stm = con.prepareStatement(sql);
				stm.setInt(1, viewId);
				stm.executeUpdate();
				stm.close();

			}

			int from = 0;
			int to = 0;
			sql = "select pid from unit where pid=?";
			stm = con.prepareStatement(sql);
			stm.setInt(1, pid);

			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				pidv.add(pid);
				to = pidv.size();
			} else {
				resp.resu = "VIEW DESCENDANT NO SUCH PERSON " + pid;
			}
			rs.close();
			stm.close();
			int currgen = 0;
			do {
				final int firstChild = pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid " + "from child as c inner join unit as u on bid=pid " + "where aid=?  ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();

					while (rs.next()) {
						pidv.add(rs.getInt(1));
					}
					rs.close();
					stm.close();

				}
				final int lastChild = pidv.size();
				if (withSpouses) {
					for (int i = from; i < to; i++) {
						sql = "select bid " + "from spouse as c inner join unit as u on bid=pid " + "where aid=?  ";
						stm = con.prepareStatement(sql);
						stm.setInt(1, pidv.get(i));
						rs = stm.executeQuery();

						while (rs.next()) {
							pidv.add(rs.getInt(1));
						}
						rs.close();
						stm.close();
					}
				}
				from = firstChild;
				to = lastChild;
				currgen++;
			} while ((to > from) && ((gen == 0) || (currgen < gen)));

			resp.pidArray = new int[pidv.size()];
			sql = "insert into viewunits (vid,pid) values (?,?) ";
			stm = con.prepareStatement(sql);
			for (int i = 0; i < pidv.size(); i++) {
				stm.setInt(1, viewId);

				final int pidc = pidv.get(i);
				stm.setInt(2, pidc);
				stm.executeUpdate();
				resp.pidArray[i] = pidc;
				resp.resuCount++;
			}
			stm.close();

			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in view with descendants ", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}

	/**
	 * empty the view.
	 *
	 * @param vid
	 *            the vid
	 * @return SukuData with resu != null if error
	 */
	public SukuData emptyView(int vid) {
		final SukuData resu = new SukuData();
		try {
			String sql;
			PreparedStatement stm;

			sql = "delete from viewunits where vid = ?";
			stm = con.prepareStatement(sql);
			stm.setInt(1, vid);
			stm.executeUpdate();
			stm.close();

		} catch (final SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING, "delete from view failed", e);
			e.printStackTrace();
		}

		return resu;
	}

	/**
	 * remove listed persons from view.
	 *
	 * @param vid
	 *            the vid
	 * @param pidArray
	 *            the pid array
	 * @return SukuData with resu != null if error
	 */
	public SukuData removeViewUnits(int vid, int[] pidArray) {
		final SukuData resu = new SukuData();
		try {
			String sql;
			PreparedStatement stm;

			sql = "delete from viewunits where vid=? and pid = ?";
			stm = con.prepareStatement(sql);

			for (final int element : pidArray) {
				stm.setInt(1, vid);
				stm.setInt(2, element);
				stm.executeUpdate();

			}
			stm.close();

		} catch (final SQLException e) {
			resu.resu = e.getMessage();
			logger.log(Level.WARNING, "delete from view failed", e);
			e.printStackTrace();
		}

		return resu;
	}

	/**
	 * add person with ancestors to view.
	 *
	 * @param viewId
	 *            the view id
	 * @param pid
	 *            the pid
	 * @param gent
	 *            the gent
	 * @param emptyView
	 *            the empty view
	 * @return SukuData with pidArray with persons added
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addViewAnc(int viewId, int pid, String gent, boolean emptyView) throws SukuException {
		final SukuData resp = new SukuData();
		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		resp.resuCount = 0;
		int gen = 0;
		if ((gent != null) && !gent.isEmpty()) {
			gen = Integer.parseInt(gent);
		}

		try {
			String sql;
			PreparedStatement stm;
			if (emptyView) {
				sql = "delete from viewunits where vid = ?";
				stm = con.prepareStatement(sql);
				stm.setInt(1, viewId);
				stm.executeUpdate();
				stm.close();

			}

			int from = 0;
			int to = 0;
			sql = "select pid from unit where pid=?";
			stm = con.prepareStatement(sql);
			stm.setInt(1, pid);

			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				pidv.add(pid);
				to = pidv.size();
			} else {
				resp.resu = "VIEW ANCESTORS NO SUCH PERSON " + pid;
			}
			rs.close();
			stm.close();

			int currgen = 0;
			do {
				final int firstPare = pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid " + "from parent as c inner join unit as u on bid=pid " + "where aid=?  ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();

					while (rs.next()) {
						pidv.add(rs.getInt(1));
					}
					rs.close();
					stm.close();

				}
				final int lastPare = pidv.size();

				from = firstPare;
				to = lastPare;
				currgen++;
			} while ((to > from) && ((gen == 0) || (currgen < gen)));

			resp.pidArray = new int[pidv.size()];
			sql = "insert into viewunits (vid,pid) values (?,?) ";
			stm = con.prepareStatement(sql);
			for (int i = 0; i < pidv.size(); i++) {
				stm.setInt(1, viewId);

				final int pidc = pidv.get(i);
				stm.setInt(2, pidc);
				stm.executeUpdate();
				resp.pidArray[i] = pidc;
				resp.resuCount++;
			}
			stm.close();

			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in view with descendants ", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}
}
