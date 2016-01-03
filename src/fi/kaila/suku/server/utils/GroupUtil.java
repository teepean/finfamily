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
public class GroupUtil {

	private static Logger logger = Logger.getLogger(GroupUtil.class.getName());

	private Connection con = null;

	/**
	 * Constructor for this server class.
	 *
	 * @param con
	 *            the con
	 */
	public GroupUtil(Connection con) {
		this.con = con;

	}

	/**
	 * Remove groupid from all persons in database.
	 *
	 * @return in SukuData as resu the # of removed groupid's
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeAllGroups() throws SukuException {
		final SukuData resp = new SukuData();
		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			final Statement stm = con.createStatement();
			final ResultSet rs = stm.executeQuery("select pid from unit where groupid is not null");
			while (rs.next()) {
				final int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}

			final int lukuri = stm.executeUpdate("update unit set groupid = null where groupid is not null");
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS REMOVED[" + resp.pidArray.length + "]; RESULT[" + lukuri + "]";
			}
			stm.close();
			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in all remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}

	}

	/**
	 * Removes group from persons in array.
	 *
	 * @param pids
	 *            the pids
	 * @return response as a SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeSelectedGroups(int[] pids) throws SukuException {
		final SukuData resp = new SukuData();

		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			final PreparedStatement stm = con
					.prepareStatement("update unit set groupid = null where pid = ? and groupid is not null");
			// int lukuri =
			// stm.executeUpdate("update unit set groupid = null where groupid
			// is not null");
			for (final int pid : pids) {
				stm.setInt(1, pid);
				final int lukuri = stm.executeUpdate();
				if (lukuri == 1) {
					pidv.add(pid);
				}

			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}

	}

	/**
	 * remove group from persons in view.
	 *
	 * @param viewid
	 *            the viewid
	 * @return response as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeViewGroups(int viewid) throws SukuException {
		final SukuData resp = new SukuData();

		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con.prepareStatement("select pid from unit "
					+ "where pid in (select pid from viewunits where vid = ?) " + "and groupid is not null ");
			stm.setInt(1, viewid);
			final ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				final int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			stm = con.prepareStatement("update unit set groupid = null "
					+ "where pid in (select pid from viewunits where vid = ?) " + "and groupid is not null ");
			stm.setInt(1, viewid);
			final int lukuri = stm.executeUpdate();
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS REMOVED[" + resp.pidArray.length + "]; RESULT[" + lukuri + "]";
			}

			stm.close();
			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}

	}

	/**
	 * Removes the group.
	 *
	 * @param group
	 *            the group
	 * @return response as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData removeGroup(String group) throws SukuException {
		final SukuData resp = new SukuData();

		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con.prepareStatement("select pid from unit where groupid = ? ");
			stm.setString(1, group);
			final ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				final int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			stm = con.prepareStatement("update unit set groupid = null where groupid = ? ");
			stm.setString(1, group);
			final int lukuri = stm.executeUpdate();
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS REMOVED[" + resp.pidArray.length + "]; RESULT[" + lukuri + "]";
			}
			stm.close();

			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected remove", e);
			e.printStackTrace();
			throw new SukuException("REMOVE GROUP", e);
		}
	}

	/**
	 * Add group to listed persons if group is null.
	 *
	 * @param pidArray
	 *            the pid array
	 * @param group
	 *            the group
	 * @return result in a SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addSelectedGroups(int[] pidArray, String group) throws SukuException {
		final SukuData resp = new SukuData();

		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			final PreparedStatement stm = con
					.prepareStatement("update unit set groupid = ? where pid = ? and groupid is null");
			// int lukuri =
			// stm.executeUpdate("update unit set groupid = null where groupid
			// is not null");
			for (final int element : pidArray) {
				stm.setString(1, group);
				stm.setInt(2, element);
				final int lukuri = stm.executeUpdate();
				if (lukuri == 1) {
					pidv.add(element);
				}

			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in selected add group", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}

	/**
	 * Add group to persons in view if group is null.
	 *
	 * @param vid
	 *            the vid
	 * @param group
	 *            the group
	 * @return result as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addViewGroups(int vid, String group) throws SukuException {
		final SukuData resp = new SukuData();

		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		try {
			PreparedStatement stm = con.prepareStatement("select pid from unit "
					+ "where pid in (select pid from viewunits where vid = ?) " + "and groupid is null ");
			stm.setInt(1, vid);
			final ResultSet rs = stm.executeQuery();

			while (rs.next()) {
				final int pid = rs.getInt(1);
				pidv.add(pid);
			}
			rs.next();
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				resp.pidArray[i] = pidv.get(i);
			}
			stm.close();
			stm = con.prepareStatement("update unit set groupid = ? "
					+ "where pid in (select pid from viewunits where vid = ?) " + "and groupid is null ");
			stm.setString(1, group);
			stm.setInt(2, vid);
			final int lukuri = stm.executeUpdate();
			if (lukuri != resp.pidArray.length) {
				resp.resu = "GROUPS ADDED[" + resp.pidArray.length + "]; RESULT[" + lukuri + "]";
			}
			stm.close();

			return resp;

		} catch (final SQLException e) {
			logger.log(Level.WARNING, "SQL error in view add group", e);
			e.printStackTrace();
			throw new SukuException("ADD GROUP", e);
		}
	}

	/**
	 * add group to persons and his/her descendants.
	 *
	 * @param pid
	 *            the pid
	 * @param group
	 *            the group
	 * @param gent
	 *            the gent
	 * @param includeSpouses
	 *            the include spouses
	 * @return as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addDescendantsToGroup(int pid, String group, String gent, boolean includeSpouses)
			throws SukuException {
		final SukuData resp = new SukuData();
		resp.resuCount = 0;
		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		int gen = 0;
		if ((gent != null) && !gent.isEmpty()) {
			gen = Integer.parseInt(gent);
		}

		try {
			int from = 0;
			int to = 0;
			String sql = "select pid,groupid from unit where pid=?";
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setInt(1, pid);
			boolean includeSubject = false;
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {

				pidv.add(pid);
				to = pidv.size();
				includeSubject = rs.getString(2) == null;

			} else {
				resp.resu = "GROUP DESCENDANT NO SUCH PERSON " + pid;
			}
			rs.close();
			int currGen = 0;
			do {
				final int firstChild = pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid " + "from child as c inner join unit as u on bid=pid "
							+ "where aid=? and groupid is null ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();

					while (rs.next()) {
						pidv.add(rs.getInt(1));
					}
					rs.close();

				}
				final int lastChild = pidv.size();
				if (includeSpouses) {
					for (int i = from; i < to; i++) {
						sql = "select bid " + "from spouse as c inner join unit as u on bid=pid "
								+ "where aid=? and groupid is null ";
						stm = con.prepareStatement(sql);
						stm.setInt(1, pidv.get(i));
						rs = stm.executeQuery();

						while (rs.next()) {
							pidv.add(rs.getInt(1));
						}
						rs.close();
					}
				}
				from = firstChild;
				to = lastChild;
				currGen++;
			} while ((to > from) && ((gen == 0) || (currGen < gen)));
			if (!includeSubject) {
				pidv.remove(0);
			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				sql = "update unit set groupid = ? where pid = ?";
				stm = con.prepareStatement(sql);
				stm.setString(1, group);
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
	 * Add group to person and his ancestors.
	 *
	 * @param pid
	 *            the pid
	 * @param group
	 *            the group
	 * @param gent
	 *            the gent
	 * @return as SukuData object
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData addAncestorsToGroup(int pid, String group, String gent) throws SukuException {
		final SukuData resp = new SukuData();
		resp.resuCount = 0;
		final ArrayList<Integer> pidv = new ArrayList<Integer>();
		int gen = 0;
		if ((gent != null) && !gent.isEmpty()) {
			gen = Integer.parseInt(gent);
		}

		try {
			int from = 0;
			int to = 0;
			String sql = "select pid,groupid from unit where pid=?";
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setInt(1, pid);
			boolean includeSubject = false;
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {

				pidv.add(pid);
				to = pidv.size();
				includeSubject = rs.getString(2) == null;

			} else {
				resp.resu = "GROUP ANCESTOR NO SUCH PERSON " + pid;
			}
			rs.close();
			int currGen = 0;
			do {
				final int firstChild = pidv.size();
				for (int i = from; i < to; i++) {
					sql = "select bid " + "from parent as c inner join unit as u on bid=pid "
							+ "where aid=? and groupid is null ";
					stm = con.prepareStatement(sql);
					stm.setInt(1, pidv.get(i));
					rs = stm.executeQuery();

					while (rs.next()) {
						pidv.add(rs.getInt(1));
					}
					rs.close();

				}
				final int lastChild = pidv.size();

				from = firstChild;
				to = lastChild;
				currGen++;
			} while ((to > from) && ((gen == 0) || (currGen < gen)));
			if (!includeSubject) {
				pidv.remove(0);
			}
			resp.pidArray = new int[pidv.size()];
			for (int i = 0; i < pidv.size(); i++) {
				sql = "update unit set groupid = ? where pid = ?";
				stm = con.prepareStatement(sql);
				stm.setString(1, group);
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
