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

import java.awt.Dimension;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationLanguage;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitLanguage;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * Server class for update,insert and delete of person and relation data.
 *
 * @author Kalle
 */
public class PersonUtil {

	private static Logger logger = Logger.getLogger(PersonUtil.class.getName());

	private Connection con = null;

	/**
	 * Initialize with database connection.
	 *
	 * @param con
	 *            the con
	 */
	public PersonUtil(Connection con) {
		this.con = con;

	}

	/**
	 * Update the person/relation data.
	 *
	 * @param usertext
	 *            the usertext
	 * @param req
	 *            the req
	 * @return result in resu field if failed
	 */
	public SukuData updatePerson(String usertext, SukuData req) {

		String insPers;
		String userid = Utils.toUsAscii(usertext);
		if ((userid != null) && (userid.length() > 16)) {
			userid = userid.substring(0, 16);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into unit (pid,tag,privacy,groupid,sex,sourcetext,privatetext,userrefn");
		if (userid != null) {
			sb.append(",createdby");
		}
		sb.append(") values (?,?,?,?,?,?,?,? ");
		if (userid != null) {
			sb.append(",'" + userid + "'");
		}
		sb.append(")");

		insPers = sb.toString();

		String updPers;
		sb = new StringBuilder();

		sb.append(
				"update unit set privacy=?,groupid=?,sex=?,sourcetext=?," + "privatetext=?,userrefn=?,Modified=now()");
		if (userid != null) {
			sb.append(",modifiedby = '" + userid + "' where pid = ?");
		} else {
			sb.append(" where pid = ?");
		}
		updPers = sb.toString();

		sb = new StringBuilder();
		String updSql;

		sb.append("update unitnotice set ");
		sb.append("surety=?,Privacy=?,NoticeType=?,Description=?,");
		sb.append("DatePrefix=?,FromDate=?,ToDate=?,Place=?,");
		sb.append("Village=?,Farm=?,Croft=?,Address=?,");
		sb.append("PostalCode=?,PostOffice=?,State=?,Country=?,Email=?,");
		sb.append("NoteText=?,MediaFilename=?,MediaTitle=?,Prefix=?,");
		sb.append("Surname=?,Givenname=?,Patronym=?,PostFix=?,");
		sb.append("SourceText=?,PrivateText=?,RefNames=?,RefPlaces=?,Modified=now()");
		if (userid != null) {
			sb.append(",modifiedby = '" + userid + "'");
		}
		sb.append(" where pnid = ?");
		updSql = sb.toString();

		sb = new StringBuilder();
		String insSql;

		sb.append("insert into unitnotice  (");
		sb.append("surety,Privacy,NoticeType,Description,");
		sb.append("DatePrefix,FromDate,ToDate,Place,");
		sb.append("Village,Farm,Croft,Address,");
		sb.append("PostalCode,PostOffice,State,Country,Email,");
		sb.append("NoteText,MediaFilename,MediaTitle,Prefix,");
		sb.append("Surname,Givenname,Patronym,PostFix,");
		sb.append("SourceText,PrivateText,RefNames,Refplaces,pnid,pid,tag");
		if (userid != null) {
			sb.append(",createdby");
		}
		sb.append(") values (");
		sb.append("?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,");
		sb.append("?,?,?,?,?,?,?,?");
		if (userid != null) {
			sb.append(",'" + userid + "'");
		}
		sb.append(")");
		insSql = sb.toString();

		sb = new StringBuilder();

		String updLangSql;

		sb.append("update unitlanguage set ");
		sb.append("NoticeType=?,Description=?," + "Place=?,");
		sb.append("NoteText=?,MediaTitle=?,Modified=now() ");
		if (userid != null) {
			sb.append(",modifiedby = '" + userid + "'");
		}
		sb.append("where pnid=? and langCode = ?");
		updLangSql = sb.toString();
		sb = new StringBuilder();
		String insLangSql;

		sb.append("insert into unitlanguage (pnid,pid,tag,langcode,");
		sb.append("NoticeType,Description,Place,");
		sb.append("NoteText,MediaTitle");
		if (userid != null) {
			sb.append(",createdby");
		}
		sb.append(") values (?,?,?,?,?,?,?,?,?");
		if (userid != null) {
			sb.append(",'" + userid + "'");
		}
		sb.append(")");
		insLangSql = sb.toString();
		final String delOneLangSql = "delete from unitlanguage where pnid = ? and langcode = ? ";
		final String updRowSql = "update unitnotice set noticerow = ? where pnid = ? ";

		final String delSql = "delete from unitnotice where pnid = ? ";
		final String delAllLangSql = "delete from Unitlanguage where pnid = ? ";

		final SukuData res = new SukuData();
		final UnitNotice[] nn = req.persLong.getNotices();

		//
		// lets make sure that two similar consecutive name notices cannot exist
		//

		if (nn != null) {
			String prevName = "";
			String prevOccu = "";

			for (int i = 0; i < nn.length; i++) {
				if (nn[i].getTag().equals("NAME")) {
					final String thisName = Utils.nv(nn[i].getGivenname()) + "/" + Utils.nv(nn[i].getPatronym()) + "/"
							+ Utils.nv(nn[i].getPrefix()) + "/" + Utils.nv(nn[i].getSurname()) + "/"
							+ Utils.nv(nn[i].getPostfix());
					if (thisName.equals(prevName) && !prevName.equals("")) {
						if (nn[i].isToBeDeleted() == false) {
							final String e = Resurses.getString("IDENTICAL_NAMES_ERROR") + " [" + req.persLong.getPid()
									+ "] idx [" + i + "] = " + thisName;
							logger.warning(e);

							if (req.persLong.getPid() > 0) {
								res.resu = e;
								return res;
							}
						}
					}
					prevName = thisName;

				} else if (nn[i].getTag().equals("OCCU")) {
					final String thisOccu = Utils.nv(nn[i].getDescription());
					if (thisOccu.equals(prevOccu) && !prevOccu.equals("")) {
						if (nn[i].isToBeDeleted() == false) {
							final String e = Resurses.getString("IDENTICAL_OCCU_ERROR") + " [" + req.persLong.getPid()
									+ "] idx [" + i + "] = '" + thisOccu + "'";
							logger.warning(e);

							if (req.persLong.getPid() > 0) {
								res.resu = e;
								return res;
							}
						}
					}
					prevOccu = thisOccu;
				}
			}
		}

		int pid = 0;

		Statement stm = null;
		PreparedStatement pst = null;

		try {
			con.setAutoCommit(false);

			if (req.persLong.getPid() > 0) { // insert new person

				res.resultPid = req.persLong.getPid();
				pid = req.persLong.getPid();

				if (req.persLong.isMainModified()) {

					if (req.persLong.getModified() == null) {
						pst = con.prepareStatement(updPers + " and modified is null ");
					} else {

						pst = con.prepareStatement(updPers + " and modified = ?");
					}
					pst.setString(1, req.persLong.getPrivacy());
					pst.setString(2, req.persLong.getGroupId());
					pst.setString(3, req.persLong.getSex());
					pst.setString(4, req.persLong.getSource());
					pst.setString(5, req.persLong.getPrivateText());
					pst.setString(6, req.persLong.getRefn());
					pst.setInt(7, req.persLong.getPid());
					if (req.persLong.getModified() != null) {
						pst.setTimestamp(8, req.persLong.getModified());
					}
					final int lukuri = pst.executeUpdate();
					if (lukuri != 1) {

						logger.warning("Person update for pid " + pid + " failed [" + lukuri + "] (Should be 1)");
						throw new SQLException("TRANSACTION_ERROR_1");
					}

					// update relation as b set tag='FATH'
					// where b.rid in (select a.rid from relation as a where
					// a.pid = 53 and a.pid <> b.rid and a.tag='CHIL')
					// and tag='MOTH'
					String apara = null;
					String bpara = null;
					String cpara = null;
					String dpara = null;
					if (req.persLong.getSex().equals("M")) {
						apara = "FATH";
						bpara = "MOTH";
						cpara = "HUSB";
						dpara = "WIFE";
					} else if (req.persLong.getSex().equals("F")) {
						bpara = "FATH";
						apara = "MOTH";
						dpara = "HUSB";
						cpara = "WIFE";
					}
					if (apara != null) {
						final String sqlParent = "update relation as b set tag=? "
								+ "where b.rid in (select a.rid from relation as a "
								+ "where a.pid = ? and a.pid <> b.rid and a.tag='CHIL')	" + "and tag=?";
						PreparedStatement ppare = con.prepareStatement(sqlParent);
						ppare.setString(1, apara);
						ppare.setInt(2, req.persLong.getPid());
						ppare.setString(3, bpara);
						int resup = ppare.executeUpdate();
						logger.fine("updated count for person parent= " + resup);

						final String sqlSpouse = "update relation as b set tag=? " + "where b.rid in (select a.rid "
								+ "from relation as a where a.pid = ? and a.pid <> b.pid "
								+ "and a.tag in ('HUSB','WIFE')) and tag=?";

						ppare = con.prepareStatement(sqlSpouse);
						ppare.setString(1, cpara);
						ppare.setInt(2, req.persLong.getPid());
						ppare.setString(3, dpara);
						resup = ppare.executeUpdate();
						logger.fine("updated count for person spouse= " + resup);

					}
				}

			} else {
				stm = con.createStatement();
				final ResultSet rs = stm.executeQuery("select nextval('unitseq')");

				if (rs.next()) {
					pid = rs.getInt(1);
					res.resultPid = pid;
				} else {
					throw new SQLException("Sequence unitseq error");
				}
				rs.close();
				pst = con.prepareStatement(insPers);
				pst.setInt(1, pid);
				pst.setString(2, req.persLong.getTag());
				pst.setString(3, req.persLong.getPrivacy());
				pst.setString(4, req.persLong.getGroupId());
				pst.setString(5, req.persLong.getSex());
				pst.setString(6, req.persLong.getSource());
				pst.setString(7, req.persLong.getPrivateText());
				pst.setString(8, req.persLong.getRefn());
				final int lukuri = pst.executeUpdate();
				if (lukuri != 1) {
					logger.warning("Person created for pid " + pid + "  gave result " + lukuri);
				}
			}

			final PreparedStatement pstDel = con.prepareStatement(delSql);
			final PreparedStatement pstDelLang = con.prepareStatement(delAllLangSql);
			final PreparedStatement pstUpdRow = con.prepareStatement(updRowSql);
			if (nn != null) {
				for (int i = 0; i < nn.length; i++) {
					final UnitNotice n = nn[i];
					int pnid = 0;
					if (n.isToBeDeleted()) {
						pstDelLang.setInt(1, n.getPnid());
						final int landelcnt = pstDelLang.executeUpdate();
						pstDel.setInt(1, n.getPnid());
						final int delcnt = pstDel.executeUpdate();
						if (delcnt != 1) {
							logger.warning("Person notice [" + n.getTag() + "]delete for pid " + pid + " failed ["
									+ delcnt + "] (Should be 1)");
							throw new SQLException("TRANSACTION_ERROR_2");
						}
						final String text = "Poistettiin " + delcnt + " riviä [" + landelcnt + "] kieliversiota pid = "
								+ n.getPid() + " tag=" + n.getTag();
						// System.out.println(text);
						logger.fine(text);
					} else if ((n.getPnid() == 0) || n.isToBeUpdated()) {

						if (n.getPnid() == 0) {// is this new i.e. insert

							stm = con.createStatement();
							final ResultSet rs = stm.executeQuery("select nextval('unitnoticeseq')");

							if (rs.next()) {
								pnid = rs.getInt(1);
							} else {
								throw new SQLException("Sequence unitnoticeseq error");
							}
							rs.close();
							pst = con.prepareStatement(insSql);
						} else {
							if (n.getModified() == null) {

								pst = con.prepareStatement(updSql + " and modified is null ");
							} else {
								pst = con.prepareStatement(updSql + " and modified = ?");
							}
							pnid = n.getPnid();
						}

						if (n.isToBeUpdated() || (n.getPnid() == 0)) {

							pst.setInt(1, n.getSurety());
							pst.setString(2, n.getPrivacy());
							pst.setString(3, n.getNoticeType());
							pst.setString(4, n.getDescription());
							pst.setString(5, n.getDatePrefix());
							pst.setString(6, n.getFromDate());
							pst.setString(7, n.getToDate());
							pst.setString(8, n.getPlace());
							pst.setString(9, n.getVillage());
							pst.setString(10, n.getFarm());
							pst.setString(11, n.getCroft());
							pst.setString(12, n.getAddress());
							pst.setString(13, n.getPostalCode());
							pst.setString(14, n.getPostOffice());
							pst.setString(15, n.getState());
							pst.setString(16, n.getCountry());
							pst.setString(17, n.getEmail());
							pst.setString(18, n.getNoteText());
							pst.setString(19, n.getMediaFilename());
							pst.setString(20, n.getMediaTitle());
							pst.setString(21, n.getPrefix());
							pst.setString(22, n.getSurname());
							pst.setString(23, n.getGivenname());
							pst.setString(24, n.getPatronym());
							pst.setString(25, n.getPostfix());
							pst.setString(26, n.getSource());
							pst.setString(27, n.getPrivateText());
							if (n.getRefNames() == null) {
								pst.setNull(28, Types.ARRAY);
							} else {

								final Array xx = con.createArrayOf("varchar", n.getRefNames());
								pst.setArray(28, xx);

							}
							if (n.getRefPlaces() == null) {
								pst.setNull(29, Types.ARRAY);
							} else {

								final Array xx = con.createArrayOf("varchar", n.getRefPlaces());
								pst.setArray(29, xx);

							}
						}
						if (n.getPnid() > 0) {
							pst.setInt(30, n.getPnid());
							if (n.getModified() != null) {
								pst.setTimestamp(31, n.getModified());
							}
							final int luku = pst.executeUpdate();
							if (luku != 1) {
								logger.warning("Person notice [" + n.getTag() + "] update for pid " + pid + " failed ["
										+ luku + "] (Should be 1)");
								throw new SQLException("TRANSACTION_ERROR_3");
							}

							logger.fine("Päivitettiin " + luku + " tietuetta pnid=[" + n.getPnid() + "]");
						} else {
							pst.setInt(30, pnid);
							pst.setInt(31, pid);
							pst.setString(32, n.getTag());
							final int luku = pst.executeUpdate();

							logger.fine("Luotiin " + luku + " tietue pnid=[" + pnid + "]");
						}

						if (n.getMediaData() == null) {
							final String sql = "update unitnotice set mediadata = null where pnid = ?";
							pst = con.prepareStatement(sql);
							pst.setInt(1, pnid);
							final int lukuri = pst.executeUpdate();
							if (lukuri != 1) {
								logger.warning("media deleted for pnid " + n.getPnid() + " gave result " + lukuri);
							}
						} else {
							final String UPDATE_IMAGE_DATA = "update UnitNotice set MediaData = ?,"
									+ "mediaWidth = ?,mediaheight = ? where PNID = ? ";

							final PreparedStatement ps = this.con.prepareStatement(UPDATE_IMAGE_DATA);

							ps.setBytes(1, n.getMediaData());
							final Dimension d = n.getMediaSize();
							ps.setInt(2, d.width);
							ps.setInt(3, d.height);
							ps.setInt(4, pnid);
							ps.executeUpdate();
						}

					}

					if (n.getLanguages() != null) {

						for (int l = 0; l < n.getLanguages().length; l++) {
							final UnitLanguage ll = n.getLanguages()[l];
							if (ll.isToBeDeleted()) {
								if (ll.getPnid() > 0) {
									pst = con.prepareStatement(delOneLangSql);
									pst.setInt(1, ll.getPnid());
									pst.setString(2, ll.getLangCode());
									final int lukuri = pst.executeUpdate();
									if (lukuri != 1) {
										logger.warning("language deleted for pnid " + n.getPnid() + " ["
												+ ll.getLangCode() + "] gave result " + lukuri);
									}
								}
							}

							if (ll.isToBeUpdated()) {

								if (ll.getPnid() == 0) {

									pst = con.prepareStatement(insLangSql);
									pst.setInt(1, n.getPnid());
									pst.setInt(2, pid);
									pst.setString(3, n.getTag());
									pst.setString(4, ll.getLangCode());
									pst.setString(5, ll.getNoticeType());
									pst.setString(6, ll.getDescription());
									pst.setString(7, ll.getPlace());
									pst.setString(8, ll.getNoteText());
									pst.setString(9, ll.getMediaTitle());
									final int lukuri = pst.executeUpdate();
									if (lukuri != 1) {
										logger.warning("language added for pnid " + n.getPnid() + " ["
												+ ll.getLangCode() + "] gave result " + lukuri);
									}

								} else {
									pst = con.prepareStatement(updLangSql);
									pst.setString(1, ll.getNoticeType());
									pst.setString(2, ll.getDescription());
									pst.setString(3, ll.getPlace());
									pst.setString(4, ll.getNoteText());
									pst.setString(5, ll.getMediaTitle());
									pst.setInt(6, ll.getPnid());
									pst.setString(7, ll.getLangCode());
									final int lukuri = pst.executeUpdate();
									if (lukuri != 1) {
										logger.warning("language for pnid " + ll.getPnid() + " [" + ll.getLangCode()
												+ "] gave result " + lukuri);
									}
									pst.close();
								}
							}

						}

					}

					if (n.getPnid() > 0) {
						pnid = n.getPnid();
					}
					pstUpdRow.setInt(1, i + 1);
					pstUpdRow.setInt(2, pnid);
					pstUpdRow.executeUpdate();
				}

			}

			if (req.relations != null) {
				if (req.persLong.getPid() == 0) {
					req.persLong.setPid(pid);
					for (final Relation r : req.relations) {
						if (r.getPid() == 0) {
							r.setPid(pid);
						}
					}

				}

				updateRelations(userid, req);
			}

			con.commit();
		} catch (final Exception e) {
			try {
				con.rollback();
			} catch (final SQLException e1) {
				logger.log(Level.WARNING, "Person update rollback failed", e1);
			}
			logger.log(Level.WARNING, "person update rolled back for [" + pid + "]", e);
			res.resu = e.getMessage();
			return res;

		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (final SQLException ignored) {
					// SQLException ignored
				}
			}

			try {
				con.setAutoCommit(true);
			} catch (final SQLException e) {
				logger.log(Level.WARNING, "set autocommit failed", e);
			}
		}
		return res;

	}

	private SukuData updateRelations(String userid, SukuData req) throws SQLException, SukuException {

		StringBuilder sb = new StringBuilder();
		String updSql;

		sb.append("update relationnotice set ");
		sb.append("surety=?,RelationType=?,Description=?,");
		sb.append("DatePrefix=?,FromDate=?,ToDate=?,Place=?,");
		sb.append("NoteText=?,SourceText=?,PrivateText=?,Modified=now()");
		if (userid != null) {
			sb.append(",modifiedby = '" + userid + "' ");
		}
		sb.append("where rnid=? ");
		updSql = sb.toString();

		sb = new StringBuilder();
		String insSql;

		sb.append("insert into relationnotice  ");
		sb.append("(surety,RelationType,Description,DatePrefix,FromDate,ToDate,");
		sb.append("Place,NoteText,sourcetext,privatetext,rnid,rid,tag,noticerow");
		if (userid != null) {
			sb.append(",createdby");
		}
		sb.append(") values (?,?,?,?,?,?,?,?,?,?,?,?,?,0 ");
		if (userid != null) {
			sb.append(",'" + userid + "'");
		}
		sb.append(")");
		insSql = sb.toString();

		sb = new StringBuilder();
		String updLangSql;

		sb.append("update relationlanguage set ");
		sb.append("RelationType=?,Description=?,Place=?,");
		sb.append("NoteText=?,Modified=now()");
		if (userid != null) {
			sb.append(",modifiedby = '" + userid + "' ");
		}
		sb.append("where rnid=? and langcode = ?");
		updLangSql = sb.toString();

		sb = new StringBuilder();
		String insLangSql;
		sb.append("insert into relationlanguage  ");
		sb.append("(rnid,rid,langcode,RelationType,Description,Place,NoteText ");
		if (userid != null) {
			sb.append(",createdby");
		}
		sb.append(") values (?,?,?,?,?,?,? ");
		if (userid != null) {
			sb.append(",'" + userid + "'");
		}
		sb.append(")");
		insLangSql = sb.toString();

		final String delLangSql = "delete from relationlanguage where rnid=? and langcode = ?";

		sb = new StringBuilder();
		String insRelSql;
		sb.append("insert into relation (rid,pid,surety,tag,relationrow");
		if (userid != null) {
			sb.append(",createdby");
		}
		sb.append(") values (?,?,?,?,? ");
		if (userid != null) {
			sb.append(",'" + userid + "'");
		}
		sb.append(")");
		insRelSql = sb.toString();
		final String updRowSql = "update relation set relationrow = ? where rid = ? and pid = ?";

		final SukuData res = new SukuData();
		SukuData ffmm = null;

		PreparedStatement pst;
		Statement stm;
		final String delRel = "delete from relation where rid = ?";
		final String delRelNoti = "delete from relationnotice where rid = ?";
		final String delRelLangu = "delete from relationlanguage where rid = ?";

		for (final Relation r : req.relations) {
			int rid = r.getRid();
			if (r.isToBeDeleted()) {
				if (rid > 0) {

					pst = con.prepareStatement(delRelLangu);
					pst.setInt(1, rid);
					final int laskLang = pst.executeUpdate();
					pst = con.prepareStatement(delRelNoti);
					pst.setInt(1, rid);
					final int laskNoti = pst.executeUpdate();
					pst = con.prepareStatement(delRel);
					pst.setInt(1, rid);
					final int laskRel = pst.executeUpdate();
					logger.info("deleted relation [" + r.getTag() + "]" + rid + " between " + r.getPid() + "/"
							+ r.getRelative() + " result [" + laskRel + "/" + laskNoti + "/" + laskLang + "]");
				}
			} else if (r.isToBeUpdated()) {

				if (rid == 0) {
					stm = con.createStatement();
					final ResultSet rs = stm.executeQuery("select nextval('relationseq')");

					if (rs.next()) {
						rid = rs.getInt(1);
					} else {
						throw new SQLException("Sequence relationseq error");
					}
					rs.close();
					r.setRid(rid);
					if (r.getPid() == req.persLong.getPid()) {

						pst = con.prepareStatement(insRelSql);

						pst.setInt(1, rid);
						pst.setInt(2, r.getPid());
						pst.setInt(3, r.getSurety());
						pst.setString(4, r.getTag());
						pst.setInt(5, 10);
						// TODO the rownumber
						int lukuri = pst.executeUpdate();
						if (lukuri != 1) {
							logger.warning("relation for rid " + rid + "  gave result " + lukuri);
						}

						String tag;
						if (r.getTag().equals("CHIL")) {
							if (req.persLong.getSex().equals("M")) {
								tag = "FATH";
							} else {
								tag = "MOTH"; // or mother
							}
						} else if (r.getTag().equals("FATH") || r.getTag().equals("MOTH")) {
							tag = "CHIL";
						} else if (r.getTag().equals("HUSB")) {
							tag = "WIFE";
						} else {
							tag = "HUSB";
						}
						pst.setInt(1, rid);
						pst.setInt(2, r.getRelative());
						pst.setInt(3, r.getSurety());

						pst.setString(4, tag);
						pst.setInt(5, 10);
						// TODO the rownumber
						lukuri = pst.executeUpdate();
						if (lukuri != 1) {
							logger.warning("relation for rid " + rid + "  gave result " + lukuri);
						}
					} else {
						//
						// here for the special MOTH/FATH relations
						//

						if (r.getTag().equals("FATH") || r.getTag().equals("MOTH")) {
							pst = con.prepareStatement(insRelSql);
							pst.setInt(1, rid);
							pst.setInt(2, r.getPid());
							pst.setInt(3, r.getSurety());
							pst.setString(4, r.getTag());
							pst.setInt(5, 10);
							// TODO the rownumber
							int lukuri = pst.executeUpdate();
							if (lukuri != 1) {
								logger.warning("other relation for rid " + rid + " [" + r.getTag() + "]  gave result "
										+ lukuri);
							}

							pst.setInt(1, rid);
							pst.setInt(2, r.getRelative());
							pst.setInt(3, r.getSurety());

							pst.setString(4, "CHIL");
							pst.setInt(5, 10);
							// TODO the rownumber
							lukuri = pst.executeUpdate();
							if (lukuri != 1) {
								logger.warning("other relation for rid " + rid + " [CHIL]  gave result " + lukuri);
							}
						}

						ffmm = getFullPerson(r.getRelative(), null);
						final ArrayList<Relation> ffvec = new ArrayList<Relation>();
						Relation newrel = null;
						for (final Relation rfm : ffmm.relations) {
							if (rfm.getTag().equals("CHIL")) {
								for (final PersonShortData pfm : ffmm.pers) {
									if (pfm.getPid() == rfm.getRelative()) {
										rfm.setShortPerson(pfm);
									}
								}
								if (rfm.getRid() == rid) {
									newrel = rfm;
								} else {
									ffvec.add(rfm);
								}
							}
						}
						if ((newrel == null) || (newrel.getShortPerson() == null)
								|| (newrel.getShortPerson().getBirtDate() == null)
								|| newrel.getShortPerson().getBirtDate().isEmpty()) {
							newrel = null;
						} else {
							for (int j = 0; j < ffvec.size(); j++) {
								final Relation rfm = ffvec.get(j);
								if ((rfm.getShortPerson() == null) || (rfm.getShortPerson().getBirtDate() == null)
										|| rfm.getShortPerson().getBirtDate().isEmpty()) {
									ffvec.add(j, newrel);
									newrel = null;
									break;
								} else {
									if (newrel.getShortPerson().getBirtDate()
											.compareTo(rfm.getShortPerson().getBirtDate()) < 0) {
										ffvec.add(j, newrel);
										newrel = null;
										break;
									}
								}
							}
							if (newrel != null) {
								ffvec.add(newrel);
							}

							// order childnotices for father or mother

							for (int rivi0 = 0; rivi0 < ffvec.size(); rivi0++) {
								final Relation rr = ffvec.get(rivi0);

								pst = con.prepareStatement(updRowSql);
								pst.setInt(1, rivi0 + 1);
								pst.setInt(2, rr.getRid());
								pst.setInt(3, rr.getPid());
								final int lukuri = pst.executeUpdate();
								logger.finest("RELAFFMMROW # " + lukuri);

							}

						}

					}
				} else {
					String updSureSql;
					if (userid == null) {
						updSureSql = "update relation set surety = ?,Modified=now() where rid = ? ";
					} else {
						updSureSql = "update relation set surety = ?,Modified=now(),modifiedby = '" + userid
								+ "' where rid = ? ";
					}

					PreparedStatement updLang;
					if (r.getModified() == null) {
						updLang = con.prepareStatement(updSureSql + " and modified is null");
					} else {
						updLang = con.prepareStatement(updSureSql + " and modified = ?");
					}
					updLang.setInt(1, r.getSurety());

					updLang.setInt(2, r.getRid());
					if (r.getModified() != null) {
						updLang.setTimestamp(3, r.getModified());
					}
					final int rner = updLang.executeUpdate();
					if (rner != 2) {
						logger.warning(
								"Relation update for rid " + r.getRid() + " failed [" + rner + "] (Should be 2)");
						throw new SQLException("TRANSACTION_ERROR_4");
					}
					logger.fine("Surety set to " + r.getSurety() + " for rid " + r.getRid() + " cnt " + rner);
				}
			}
			if (r.getNotices() != null) {

				final String updnorder = "update relationNotice set noticerow = ? where rnid = ?";
				final PreparedStatement rorder = con.prepareStatement(updnorder);

				for (int j = 0; j < r.getNotices().length; j++) {
					final RelationNotice rn = r.getNotices()[j];
					int rnid = rn.getRnid();
					if (rn.isToBeDeleted() && (rnid > 0)) {

						final String sqlNoti = "delete from relationnotice where rnid = ?";
						final String sqlRelLangu = "delete from relationlanguage where rnid = ?";

						pst = con.prepareStatement(sqlRelLangu);
						pst.setInt(1, rnid);
						final int laskLang = pst.executeUpdate();
						pst = con.prepareStatement(sqlNoti);
						pst.setInt(1, rnid);
						final int laskNoti = pst.executeUpdate();

						logger.info("deleted relationnotice [" + r.getTag() + "" + rid + " between " + r.getPid() + "/"
								+ r.getRelative() + " result [" + laskNoti + "/" + laskLang);
					} else {
						if (rn.isToBeUpdated() || (rnid == 0)) {

							if (rn.getRnid() == 0) {
								stm = con.createStatement();
								final ResultSet rs = stm.executeQuery("select nextval('RelationNoticeSeq')");

								if (rs.next()) {
									rnid = rs.getInt(1);
								} else {
									throw new SQLException("Sequence relationseq error");
								}
								rs.close();

								pst = con.prepareStatement(insSql);

							} else {
								if (rn.getModified() == null) {
									pst = con.prepareStatement(updSql + " and modified is null");
								} else {
									pst = con.prepareStatement(updSql + " and modified = ?");
								}
							}

							pst.setInt(1, rn.getSurety());
							pst.setString(2, rn.getType());
							pst.setString(3, rn.getDescription());
							pst.setString(4, rn.getDatePrefix());
							pst.setString(5, rn.getFromDate());
							pst.setString(6, rn.getToDate());
							pst.setString(7, rn.getPlace());
							pst.setString(8, rn.getNoteText());
							pst.setString(9, rn.getSource());
							pst.setString(10, rn.getPrivateText());

							if (rn.getRnid() > 0) {
								pst.setInt(11, rnid);
								if (rn.getModified() != null) {
									pst.setTimestamp(12, rn.getModified());
								}
								final int rer = pst.executeUpdate();
								if (rer != 1) {
									logger.warning("Relation notice update for rnid " + rn.getRnid() + " failed [" + rer
											+ "] (Should be 1");
									throw new SQLException("TRANSACTION_ERROR_5");
								}
								logger.fine("update rn for " + rnid + "[" + rer + "]");
							} else {

								pst.setInt(11, rnid);
								pst.setInt(12, rid);
								pst.setString(13, rn.getTag());
								final int rer = pst.executeUpdate();
								logger.fine("insert rn for " + rnid + "[" + rer + "]");

							}
						}

						rorder.setInt(1, j);
						rorder.setInt(2, rnid);
						final int orderit = rorder.executeUpdate();

						logger.finest("RN order lkm = " + orderit);
						if (rn.getLanguages() != null) {

							for (int k = 0; k < rn.getLanguages().length; k++) {
								final RelationLanguage rl = rn.getLanguages()[k];
								if (rl.isToBeUpdated()) {
									if (rl.getRnid() == 0) {
										final PreparedStatement updLang = con.prepareStatement(insLangSql);
										// "(rnid,rid,langcode,RelationType,Description,Place,NoteText)
										// "
										// +
										updLang.setInt(1, rnid);
										updLang.setInt(2, rid);
										updLang.setString(3, rl.getLangCode());
										updLang.setString(4, rl.getRelationType());
										updLang.setString(5, rl.getDescription());
										updLang.setString(6, rl.getPlace());
										updLang.setString(7, rl.getNoteText());

										final int rier = updLang.executeUpdate();
										logger.fine("insert rl rnid: " + rnid + "/" + rl.getLangCode() + "count:["
												+ rier + "]");

									} else if (rl.isToBeDeleted()) {
										final PreparedStatement updLang = con.prepareStatement(delLangSql);
										updLang.setInt(1, rnid);
										updLang.setString(2, rl.getLangCode());
										final int rder = updLang.executeUpdate();
										logger.fine("delete rl rnid: " + rnid + "/" + rl.getLangCode() + "count:["
												+ rder + "]");

									} else {

										final PreparedStatement updLang = con.prepareStatement(updLangSql);
										updLang.setString(1, rl.getRelationType());
										updLang.setString(2, rl.getDescription());
										updLang.setString(3, rl.getPlace());
										updLang.setString(4, rl.getNoteText());
										updLang.setInt(5, rl.getRnid());
										updLang.setString(6, rl.getLangCode());
										final int rner = updLang.executeUpdate();
										logger.fine("update rl for " + rl.getRnid() + "/" + rl.getLangCode() + "["
												+ rner + "]");
									}
								}
							}
						}
					}
				}

				// some ordering still

			}
		}

		//
		// set the order still
		//
		int childRow = 0;
		int parentRow = 0;
		int spouseRow = 0;
		int thisRow = 0;

		for (final Relation r : req.relations) {
			if (r.getPid() == req.persLong.getPid()) {
				if (r.getTag().equals("CHIL")) {
					thisRow = ++childRow;
				} else if (r.getTag().equals("HUSB") || r.getTag().equals("WIFE")) {
					thisRow = ++spouseRow;
				} else {
					thisRow = ++parentRow;
				}

				pst = con.prepareStatement(updRowSql);
				pst.setInt(1, thisRow);
				pst.setInt(2, r.getRid());
				pst.setInt(3, r.getPid());
				final int lukuri = pst.executeUpdate();

				logger.finest("RELAROW # " + lukuri);

			}
		}

		return res;

	}

	/**
	 * delete all data for the person.
	 *
	 * @param pid
	 *            the pid
	 * @return status of delete operation in resu field if error
	 */
	public SukuData deletePerson(int pid) {
		final SukuData res = new SukuData();

		// first delete relations

		final String sqlrlang = "delete from relationlanguage where rid in "
				+ "(select rid from relation where pid = ?)";

		final String sqlrnoti = "delete from relationnotice where rid in " + "(select rid from relation where pid = ?)";

		final String sqlr = "delete from relation where rid in " + "(select rid from relation where pid = ?)";

		final String sqlul = "delete from unitlanguage where pid = ?";

		final String sqlun = "delete from unitnotice where pid = ? ";

		final String sqlu = "delete from unit where pid=?";

		try {
			PreparedStatement pst = con.prepareStatement(sqlrlang);
			pst.setInt(1, pid);
			int lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] relationlanguage count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlrnoti);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] relationnotice count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlr);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] relation count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlul);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] unitlanguage count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlun);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] unitnotice count:" + lukuri);
			pst.close();

			pst = con.prepareStatement(sqlu);
			pst.setInt(1, pid);
			lukuri = pst.executeUpdate();
			logger.fine("Deleted [" + pid + "] unit count:" + lukuri);
			pst.close();

		} catch (final SQLException e) {
			res.resu = e.getMessage();
			logger.log(Level.WARNING, "Deleting person " + pid, e);
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Full person sisältää
	 *
	 * SukuData siirto-oliossa
	 *
	 * henkilön tiedot: persLong tietojaksot: persLong.notices[]
	 * sukulaisuussuheet: relations[] sukujaksot: rellations[i].notices[]
	 *
	 * @param pid
	 *            the pid
	 * @param lang
	 *            the lang
	 * @return SukuData result
	 * @throws SukuException
	 *             the suku exception
	 */

	public SukuData getFullPerson(int pid, String lang) throws SukuException {
		final SukuData pers = new SukuData();
		final ArrayList<UnitNotice> nvec = new ArrayList<UnitNotice>();

		final ArrayList<Relation> rels = new ArrayList<Relation>();
		ArrayList<RelationNotice> relNotices = null;
		try {
			String sql = "select * from unit where pid = ? ";
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, pid);

			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
				pers.persLong = new PersonLongData(rs);
			}
			rs.close();
			pstm.close();
			if (pers.persLong != null) {
				UnitNotice notice;
				if (lang == null) {
					sql = "select * from unitnotice where pid = ? order by noticerow ";
					pstm = con.prepareStatement(sql);
					pstm.setInt(1, pid);
				} else {
					// sql = "select * from unitnotice_" + lang
					// + " where pid = ? order by noticerow ";

					sql = "select u1.pid,u1.Pnid,u1.surety,u1.privacy,u1.NoticeRow,u1.tag,"
							+ "coalesce(u2.NoticeType,u1.NoticeType) as NoticeType ,"
							+ "coalesce(u2.description,u1.description) as description ,"
							+ "u1.DatePrefix,u1.FromDate,u1.ToDate,"
							+ "coalesce(u2.Place,u1.Place) as Place , u1. Village,u1.Farm,u1.Croft,"
							+ "u1.Address,u1.PostOffice,u1.PostalCode,u1.state,u1.Country, u1.Email,	"
							+ "coalesce(u2.NoteText,u1.NoteText) as NoteText ,  "
							+ "u1.MediaFilename,u1.MediaData,coalesce(u2.MediaTitle,u1.MediaTitle) as MediaTitle ,   "
							+ "u1.MediaWidth,u1.MediaHeight,u1.Prefix,u1.Surname,u1.Givenname,u1.Patronym,u1.PostFix,	"
							+ "u1.RefNames,u1.RefPlaces,u1.SourceText,u1.PrivateText,u1.modified,u1.CreateDate,u1.modifiedBy,u1.createdBy "
							+ "from unitNotice as u1 left join unitLanguage as u2 "
							+ "on u1.pnid = u2.pnid and u2.langcode = ? " + "where u1.pid = ? order by u1.noticerow ";
					pstm = con.prepareStatement(sql);
					pstm.setString(1, lang);
					pstm.setInt(2, pid);

				}

				rs = pstm.executeQuery();
				while (rs.next()) {
					notice = new UnitNotice(rs);
					nvec.add(notice);
				}
				rs.close();
				pstm.close();
				pers.persLong.setNotices(nvec.toArray(new UnitNotice[0]));

				if (lang == null) {
					final ArrayList<UnitLanguage> lvec = new ArrayList<UnitLanguage>();
					UnitLanguage langnotice;
					sql = "select * from unitlanguage where pid = ? order by pnid,langcode";

					pstm = con.prepareStatement(sql);
					pstm.setInt(1, pid);
					rs = pstm.executeQuery();
					while (rs.next()) {
						langnotice = new UnitLanguage(rs);
						lvec.add(langnotice);
					}
					rs.close();
					pstm.close();

					Vector<UnitLanguage> llvec = null;

					for (int i = 0; i < pers.persLong.getNotices().length; i++) {
						final UnitNotice noti = pers.persLong.getNotices()[i];
						llvec = new Vector<UnitLanguage>();
						for (int j = 0; j < lvec.size(); j++) {
							final UnitLanguage ul = lvec.get(j);
							if (noti.getPnid() == ul.getPnid()) {
								llvec.add(ul);
							}
						}
						if (llvec.size() > 0) {
							noti.setLanguages(llvec.toArray(new UnitLanguage[0]));
						}

					}

					// pers.persLong.setLanguages(lvec.toArray(new
					// UnitLanguage[0]));
				}

				sql = "select a.rid,a.pid,b.pid,a.tag,a.surety,a.modified,a.createdate,a.modifiedby,a.createdby  "
						+ "from relation a inner join relation b on a.rid=b.rid "
						+ "where a.pid <> b.pid and a.pid=? order by a.tag,a.relationrow";
				pstm = con.prepareStatement(sql);
				pstm.setInt(1, pid);
				rs = pstm.executeQuery();
				int rid;
				int aid;
				int bid;
				String tag;
				Relation rel = null;

				final ArrayList<Integer> relpids = new ArrayList<Integer>();
				final LinkedHashMap<Integer, Relation> relmap = new LinkedHashMap<Integer, Relation>();
				while (rs.next()) {
					rid = rs.getInt(1);
					aid = rs.getInt(2);
					bid = rs.getInt(3);
					tag = rs.getString(4);
					relpids.add(bid);

					rel = new Relation(rid, aid, bid, tag, rs.getInt(5), rs.getTimestamp(6), rs.getTimestamp(7),
							rs.getString(8), rs.getString(9));
					rels.add(rel);
					relmap.put(rid, rel);

				}
				rs.close();
				pstm.close();

				sql = "select * from relationnotice "
						+ "where rid in (select rid from relation where pid=?) order by rid,noticerow";
				pstm = con.prepareStatement(sql);
				pstm.setInt(1, pid);
				rs = pstm.executeQuery();
				int curid = 0;
				rid = 0;
				RelationNotice rnote = null;

				relNotices = new ArrayList<RelationNotice>();
				while (rs.next()) {
					rid = rs.getInt("rid");
					if (rid != curid) {
						rel = relmap.get(Integer.valueOf(curid));
						if ((rel != null) && (relNotices.size() > 0)) {
							rel.setNotices(relNotices.toArray(new RelationNotice[0]));
						}
						relNotices = null;
						curid = rid;
						relNotices = new ArrayList<RelationNotice>();
					}

					rnote = new RelationNotice(rs.getInt("rnid"), rid, rs.getInt("surety"), rs.getString("tag"),
							rs.getString("relationtype"), rs.getString("description"), rs.getString("dateprefix"),
							rs.getString("fromdate"), rs.getString("todate"), rs.getString("place"),
							rs.getString("notetext"), rs.getString("sourcetext"), rs.getString("privatetext"),
							rs.getTimestamp("modified"), rs.getTimestamp("createdate"), rs.getString("modifiedBy"),
							rs.getString("createdBy"));
					relNotices.add(rnote);
				}

				if (relNotices.size() > 0) {
					rel = relmap.get(Integer.valueOf(curid));
					if (rel != null) {
						rel.setNotices(relNotices.toArray(new RelationNotice[0]));
					}
				}
				rs.close();
				pstm.close();

				if (rels.size() > 0) {
					pers.relations = rels.toArray(new Relation[0]);
				} else {
					pers.relations = new Relation[0];
				}

				//
				// lets still pick up the language variants
				//

				for (final Relation relation : pers.relations) {
					if (relation.getNotices() != null) {
						for (int j = 0; j < relation.getNotices().length; j++) {
							final RelationNotice rn = relation.getNotices()[j];
							final ArrayList<RelationLanguage> rl = new ArrayList<RelationLanguage>();

							sql = "select rnid,rid,langcode,relationtype,description,place,notetext,modified,createdate "
									+ "from relationLanguage where rnid = ?";

							pstm = con.prepareStatement(sql);
							pstm.setInt(1, rn.getRnid());
							rs = pstm.executeQuery();
							while (rs.next()) {
								final RelationLanguage rrl = new RelationLanguage(rs);
								rl.add(rrl);
							}
							rs.close();
							pstm.close();

							if (rl.size() > 0) {
								rn.setLanguages(rl.toArray(new RelationLanguage[0]));
							}

							// if
							// (pers.relations[i].getNotices()[j].getLanguages()
							// != null){
							//
							//
							//
							// }
						}
					}
				}

				// if (lang == null) {
				final ArrayList<PersonShortData> pv = new ArrayList<PersonShortData>();
				final HashMap<Integer, Integer> testPid = new HashMap<Integer, Integer>();
				for (int i = 0; i < relpids.size(); i++) {
					final Integer test = relpids.get(i);

					if (testPid.put(test, test) == null) {
						// System.out.println("kalleko:" + test.intValue());
						final PersonShortData p = new PersonShortData(this.con, test.intValue());
						pv.add(p);
					}
				}
				pers.pers = pv.toArray(new PersonShortData[0]);
			}
			// }

		} catch (final SQLException e) {
			e.printStackTrace();
			throw new SukuException(e);
		}
		return pers;
	}

	/**
	 * Insert gedcom relations.
	 *
	 * @param husbandNumber
	 *            the husband number
	 * @param wifeNumber
	 *            the wife number
	 * @param relations
	 *            the relations
	 * @return result of insert,null if ok
	 */
	public String insertGedcomRelations(int husbandNumber, int wifeNumber, Relation[] relations) {

		final String insSql = "insert into relationnotice  "
				+ "(surety,RelationType,Description,DatePrefix,FromDate,ToDate,"
				+ "Place,NoteText,sourcetext,privatetext,rnid,rid,tag,noticerow)"
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		final String insRelSql = "insert into relation (rid,pid,surety,tag,relationrow) values (?,?,?,?,?) ";

		ResultSet rs;
		try {

			PreparedStatement pst;
			Statement stm;

			int childForFatherRow = husbandNumber * 50;
			int childForMotherRow = wifeNumber * 50;

			// int aid=0;
			// int bid=0;

			for (final Relation r : relations) {
				int rid = r.getRid();

				stm = con.createStatement();
				rs = stm.executeQuery("select nextval('relationseq')");

				if (rs.next()) {
					rid = rs.getInt(1);
				} else {
					throw new SQLException("Sequence relationseq error");
				}
				rs.close();
				stm.close();
				r.setRid(rid);

				pst = con.prepareStatement(insRelSql);

				pst.setInt(1, rid);
				pst.setInt(2, r.getPid());
				pst.setInt(3, r.getSurety());
				pst.setString(4, r.getTag());
				if (r.getTag().equals("WIFE")) {
					pst.setInt(5, wifeNumber);
					// aid=r.getPid();
				} else {
					pst.setInt(5, 1);
				}
				// pst.setInt(5, childRow);
				int lukuri = pst.executeUpdate();
				if (lukuri != 1) {
					logger.warning("relation for rid " + rid + "  gave result " + lukuri);
				}

				String tag;
				if (r.getTag().equals("FATH") || r.getTag().equals("MOTH")) {
					tag = "CHIL";
				} else {
					tag = "HUSB";
					// bid=r.getRelative();
				}
				pst.setInt(1, rid);
				pst.setInt(2, r.getRelative());
				pst.setInt(3, r.getSurety());
				pst.setString(4, tag);
				if (tag.equals("HUSB")) {
					pst.setInt(5, husbandNumber);
				} else {
					if (r.getTag().equals("FATH")) {
						pst.setInt(5, childForMotherRow++);
					} else {
						pst.setInt(5, childForFatherRow++);
					}
				}
				lukuri = pst.executeUpdate();
				if (lukuri != 1) {
					logger.warning("relation for rid " + rid + "  gave result " + lukuri);
				}
				pst.close();
				if (r.getNotices() != null) {

					for (int j = 0; j < r.getNotices().length; j++) {
						final RelationNotice rn = r.getNotices()[j];
						int rnid = rn.getRnid();

						stm = con.createStatement();
						rs = stm.executeQuery("select nextval('RelationNoticeSeq')");

						if (rs.next()) {
							rnid = rs.getInt(1);
						} else {
							throw new SQLException("Sequence relationseq error");
						}
						rs.close();
						stm.close();

						pst = con.prepareStatement(insSql);
						pst.setInt(1, rn.getSurety());
						pst.setString(2, rn.getType());
						pst.setString(3, rn.getDescription());
						pst.setString(4, rn.getDatePrefix());
						pst.setString(5, rn.getFromDate());
						pst.setString(6, rn.getToDate());
						pst.setString(7, rn.getPlace());
						pst.setString(8, rn.getNoteText());
						pst.setString(9, rn.getSource());
						pst.setString(10, rn.getPrivateText());
						pst.setInt(11, rnid);
						pst.setInt(12, rid);
						pst.setString(13, rn.getTag());
						pst.setInt(14, j + 1);
						final int rer = pst.executeUpdate();
						pst.close();
						logger.fine("insert rn for " + rnid + "[" + rer + "]");

					}
				}
			}

		} catch (final SQLException e) {
			// e.printStackTrace();
			logger.log(Level.WARNING, "Relation update", e);
			return e.getMessage();
		}

		return null;

	}

	/**
	 * Update the noticerow values for the person.
	 *
	 * @param longPerson
	 *            the long person
	 * @throws SQLException
	 *             the sQL exception
	 */
	public void updateNoticesOrder(PersonLongData longPerson) throws SQLException {

		final String sql = "update unitnotice set noticerow = ? where pnid = ?";
		if ((longPerson == null) || (longPerson.getNotices() == null)) {
			return;
		}
		final PreparedStatement pst = con.prepareStatement(sql);

		for (int i = 0; i < longPerson.getNotices().length; i++) {
			pst.setInt(1, i + 1);
			pst.setInt(2, longPerson.getNotices()[i].getPnid());
			pst.executeUpdate();
		}
		pst.close();

	}

	/**
	 * get various settings from db.
	 *
	 * @param index
	 *            the index
	 * @param type
	 *            the type
	 * @param name
	 *            the name
	 * @return settings in a SukuData object
	 */
	public SukuData getSettings(String index, String type, String name) {
		final SukuData res = new SukuData();
		try {
			if ("query".equals(type)) {
				final String sql = "select settingname,settingvalue from SukuSettings " + "where settingtype = '" + type
						+ "' order by settingindex ";
				final ArrayList<String> v = new ArrayList<String>();

				final Statement stm = con.createStatement();
				final ResultSet rs = stm.executeQuery(sql);
				while (rs.next()) {
					v.add(rs.getString(1) + "=" + rs.getString(2));
				}
				rs.close();
				stm.close();

				res.generalArray = v.toArray(new String[0]);

			} else if ((name == null) && (index != null)) {
				int settingIndex = 0;
				try {
					settingIndex = Integer.parseInt(index);
				} catch (final NumberFormatException ne) {
					// NumberFormatException ignored
				}
				String sql = "select settingindex,settingvalue "
						+ "from sukusettings where settingtype = ? and settingname = 'name' "
						+ "order by settingindex ";
				final String[] vv = new String[12];

				PreparedStatement pst = con.prepareStatement(sql);
				pst.setString(1, type);
				ResultSet rs = pst.executeQuery();
				while (rs.next()) {
					final int idx = rs.getInt(1);
					final String nam = rs.getString(2);

					if ((idx >= 0) && (idx < 12)) {
						vv[idx] = nam;
					}

				}
				rs.close();
				pst.close();
				res.generalArray = vv;
				String vx[] = new String[2];
				res.vvTypes = new Vector<String[]>();

				sql = "select settingname,settingvalue from SukuSettings "
						+ "where settingtype = ? and settingindex = ? ";
				pst = con.prepareStatement(sql);
				pst.setString(1, type);
				pst.setInt(2, settingIndex);
				rs = pst.executeQuery();
				while (rs.next()) {
					vx = new String[2];
					vx[0] = rs.getString(1);
					vx[1] = rs.getString(2);
					res.vvTypes.add(vx);
				}
				rs.close();
				pst.close();

			} else {

				final String sql = "select settingvalue " + "from sukusettings where settingtype = '" + type + "' "
						+ "and settingname = '" + name + "' " + "order by settingindex ";

				final Statement stm = con.createStatement();
				final ResultSet rs = stm.executeQuery(sql);
				final ArrayList<String> setv = new ArrayList<String>();
				while (rs.next()) {

					final String val = rs.getString(1);
					setv.add(val);

				}
				rs.close();
				stm.close();
				res.generalArray = setv.toArray(new String[0]);
			}
		} catch (final SQLException e) {
			res.resu = e.getMessage();
			e.printStackTrace();
		}
		return res;
	}

}
