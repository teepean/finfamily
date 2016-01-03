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
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import fi.kaila.suku.report.dialog.ReportWorkerDialog;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class GenGraphUtil.
 */
public class GenGraphUtil {

	private static Logger logger = Logger.getLogger(ReportUtil.class.getName());

	private Connection con = null;

	/** The runner. */
	ReportWorkerDialog runner = null;

	/**
	 * Constructor.
	 *
	 * @param con
	 *            connection instance to the PostgreSQL database
	 */
	public GenGraphUtil(Connection con) {
		this.con = con;
		this.runner = ReportWorkerDialog.getRunner();
	}

	/**
	 * Gets the gengraph data.
	 *
	 * @param pid
	 *            the pid
	 * @param lang
	 *            the lang
	 * @return the gengraph data
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getGengraphData(int pid, String lang) throws SukuException {
		final SukuData fam = new SukuData();
		Relation rela;
		final ArrayList<PersonShortData> persons = new ArrayList<PersonShortData>();
		final ArrayList<Relation> relas = new ArrayList<Relation>();
		// LinkedHashMap<Integer,ReportUnit> ru = new
		// LinkedHashMap<Integer,ReportUnit>();
		// LinkedHashMap<Integer, PersonShortData> pu = new
		// LinkedHashMap<Integer, PersonShortData>();
		Vector<RelationNotice> relaNotices = null;
		final PersonShortData psp = new PersonShortData(con, pid);
		persons.add(psp);

		try {

			final String sql = "select a.rid,b.pid,a.tag,a.surety,c.tag "
					+ "from relation as a inner join relation as b on a.rid=b.rid and b.pid <> a.pid	"
					+ "left join relationnotice as c on a.rid=c.rid " + "where a.pid=? order by a.tag,a.relationrow";

			final String sqln = "select tag,fromdate from relationnotice where rid=? order by noticerow";
			final PreparedStatement pst = con.prepareStatement(sql);

			pst.setInt(1, pid);
			final ResultSet rs = pst.executeQuery();

			int defRid = 0;
			while (rs.next()) {
				final int rid = rs.getInt(1);
				if (defRid != rid) {
					defRid = rid;
					final int bid = rs.getInt(2);
					final String tag = rs.getString(3);
					final int surety = rs.getInt(4);
					final String noteTag = rs.getString(5);
					rela = new Relation(rid, pid, bid, tag, surety, null, null, null, null);
					relas.add(rela);

					if (noteTag != null) {
						relaNotices = new Vector<RelationNotice>();
						final PreparedStatement pstn = con.prepareStatement(sqln);
						pstn.setInt(1, rid);
						final ResultSet rsn = pstn.executeQuery();
						while (rsn.next()) {

							final String xtag = rsn.getString(1);
							final String xdate = rsn.getString(2);

							final RelationNotice rn = new RelationNotice(xtag);
							if (xdate != null) {
								rn.setFromDate(xdate);
							}
							relaNotices.add(rn);
						}
						rsn.close();
						pstn.close();
						rela.setNotices(relaNotices.toArray(new RelationNotice[0]));
					}

				}
			}
			rs.close();
			pst.close();
			fam.relations = relas.toArray(new Relation[0]);

			fam.pers = persons.toArray(new PersonShortData[0]);

		} catch (final SQLException e) {
			throw new SukuException("GenGraph sql error", e);
		}

		logger.fine("GenGraphUtil repo");

		this.runner.setRunnerValue(psp.getAlfaName(true));
		return fam;

	}

}
