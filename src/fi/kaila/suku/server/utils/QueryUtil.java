package fi.kaila.suku.server.utils;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * The Class QueryUtil.
 * 
 * @author Kalle
 * 
 *         Utility class to do queries to the database
 */
public class QueryUtil {

	private static Logger logger = Logger.getLogger(QueryUtil.class.getName());

	private Connection con = null;

	/** The is H2 database. */
	private boolean isH2 = false;

	// private String toDoTagName = null;

	/**
	 * Initialize with database connection.
	 * 
	 * @param con
	 *            the con
	 */
	public QueryUtil(Connection con, boolean isH2) {
		this.con = con;
		this.isH2 = isH2;
		// this.toDoTagName = Resurses.getString(Resurses.COLUMN_T_TODO);

	}

	/**
	 * Query database.
	 * 
	 * @param params
	 *            the params
	 * @return the query result
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData queryDatabase(String... params) throws SukuException {

		String[] pari;
		String decod;
		ArrayList<PersonShortData> personList = new ArrayList<PersonShortData>();
		HashMap<Integer, PersonShortData> persMap = new HashMap<Integer, PersonShortData>();
		int idx;

		boolean needsRelativeInfo = false;

		try {
			StringBuilder seleSQL = new StringBuilder();

			seleSQL.append("select u.pid,u.sex,u.userrefn,");
			seleSQL.append("u.groupid,u.tag,n.tag,n.givenname,");
			seleSQL.append("n.patronym,n.prefix,n.surname,n.postfix,");
			seleSQL.append("n.fromdate,n.place,n.village,n.farm,n.croft,n.Description,");
			seleSQL.append("n.pnid,n.mediafilename,n.mediatitle,n.Country ");
			seleSQL.append("from unit as u left join unitnotice ");
			seleSQL.append("as n on u.pid = n.pid ");

			StringBuilder fromSQL = new StringBuilder();
			StringBuilder sbn = new StringBuilder();
			StringBuilder free = new StringBuilder();

			String searchPlace = null;
			String searchVillage = null;
			String searchFarm = null;
			String searchCroft = null;
			String searchNoticeTag = null;
			boolean searchNoNotice = false;
			String searchSex = null;
			int searchMaxSurety = 100;
			String searchFullText = null;
			if (params.length > 1) {

				boolean isFirstCriteria = true;
				boolean isFirstPlaceCriteria = true;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_RELATIVE_INFO)) {
						needsRelativeInfo = true;
					} else {

						if (pari[0].equals(Resurses.CRITERIA_GIVENNAME)) {
							if (sbn.length() > 0) {
								sbn.append("and ");
							}
							if (isH2){
								sbn.append("givenname like '");
							} else {
								sbn.append("givenname ilike '");
							}
							sbn.append(toQuery(decod));
							sbn.append("%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_SURNAME)) {
							if (sbn.length() > 0) {
								sbn.append("and ");
							}

							if (isH2){
								sbn.append("surname like '");
							} else {
								sbn.append("surname ilike '");
							}
							sbn.append(toQuery(decod));
							sbn.append("%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_PATRONYME)) {
							if (sbn.length() > 0) {
								sbn.append("and ");
							}
							if (isH2){
								sbn.append("patronym like '");
							} else {
								sbn.append("patronym ilike '");
							}
							sbn.append(toQuery(decod));
							sbn.append("%' ");
						} else if (pari[0].equals(Resurses.CRITERIA_PLACE)) {
							searchPlace = decod;
						} else if (pari[0].equals(Resurses.CRITERIA_VILLAGE)) {
							searchVillage = decod;
						} else if (pari[0].equals(Resurses.CRITERIA_FARM)) {
							searchFarm = decod;
						} else if (pari[0].equals(Resurses.CRITERIA_CROFT)) {
							searchCroft = decod;
						} else if (pari[0].equals(Resurses.CRITERIA_NOTICE)) {
							searchNoticeTag = decod;
						} else if (pari[0]
								.equals(Resurses.CRITERIA_NOTICE_EXISTS)) {
							searchNoNotice = true;
						} else if (pari[0].equals(Resurses.CRITERIA_SEX)) {
							searchSex = decod;
						} else if (pari[0].equals(Resurses.CRITERIA_SURETY)) {
							try {
								searchMaxSurety = Integer.parseInt(decod);
							} catch (NumberFormatException ne) {
								searchMaxSurety = 100;
							}
						} else if (pari[0].equals(Resurses.CRITERIA_FULL_TEXT)) {
							searchFullText = decod;
						}
					}
				}
				if (searchSex != null) {
					seleSQL.append(" where u.sex = '");
					seleSQL.append(searchSex);
					seleSQL.append("' ");
					isFirstCriteria = false;
				}
				if (sbn.length() > 0) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					fromSQL.append("u.pid in (select pid from unitnotice where ");
					fromSQL.append(sbn.toString());
					fromSQL.append("and tag='NAME') ");
					isFirstCriteria = false;
				}

				String begdate = null;
				String todate = null;
				String place = null;
				String group = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");
					if (pari[0].equals(Resurses.CRITERIA_GROUP)) {
						group = decod;
					}
				}
				if (group != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					if (isH2){
						fromSQL.append("u.groupid like '");
					} else {
						fromSQL.append("u.groupid ilike '");
					}
					fromSQL.append(toQuery(group));
					fromSQL.append("%' ");
				}

				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_BIRT_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_BIRT_TO)) {
						todate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_BIRT_PLACE)) {
						place = decod;
					}
				}

				if ((begdate != null) || (todate != null) || (place != null)) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL.append("u.pid in (select pid from unitnotice where ");
					if ((begdate != null) && (todate == null)) {
						fromSQL.append("fromdate >= '");
						fromSQL.append(begdate);
						fromSQL.append("' ");
					} else if ((begdate == null) && (todate != null)) {
						fromSQL.append("fromdate <= '");
						fromSQL.append(todate);
						fromSQL.append("9999' ");
					} else if ((begdate != null) && (todate != null)) {
						fromSQL.append("fromdate between '");
						fromSQL.append(begdate);
						fromSQL.append("' and '");
						fromSQL.append(todate);
						fromSQL.append("9999' ");
					}
					if ((begdate == null) && (todate == null)
							&& (place != null)) {
						if (isH2){
							fromSQL.append("place like '");
						} else {
							fromSQL.append("place ilike '");
						}
						fromSQL.append(toQuery(place));
						fromSQL.append("%' ");
					} else if (place != null) {
						if (isH2){
							fromSQL.append("and place like '");
						} else {
							fromSQL.append("and place ilike '");
						}
						fromSQL.append(toQuery(place));
						fromSQL.append("' ");
					}
					fromSQL.append("and tag='BIRT') ");
				}

				begdate = null;
				todate = null;
				place = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_DEAT_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_DEAT_TO)) {
						todate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_DEAT_PLACE)) {
						place = decod;
					}
				}

				if ((begdate != null) || (todate != null) || (place != null)) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					fromSQL.append("u.pid in (select pid from unitnotice where ");
					if ((begdate != null) && (todate == null)) {
						fromSQL.append("fromdate >= '");
						fromSQL.append(begdate);
						fromSQL.append("' ");
					} else if ((begdate == null) && (todate != null)) {
						fromSQL.append("fromdate <= '");
						fromSQL.append(todate);
						fromSQL.append("9999' ");
					} else if ((begdate != null) && (todate != null)) {
						fromSQL.append("fromdate between '");
						fromSQL.append(begdate);
						fromSQL.append("' and '");
						fromSQL.append(todate);
						fromSQL.append("9999' ");
					}
					if ((begdate == null) && (todate == null)
							&& (place != null)) {
						if (isH2){
							fromSQL.append("place like '");
						} else {
							fromSQL.append("place ilike '");
						}
						fromSQL.append(toQuery(place));
						fromSQL.append("' ");
					} else if (place != null) {
						if (isH2){
							fromSQL.append("and place like '");
						} else {
							fromSQL.append("and place ilike '");
						}
						fromSQL.append(place);
						fromSQL.append("%' ");
					}
					fromSQL.append("and tag='DEAT') ");
				}
				begdate = null;
				todate = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_CREATED_FROM)) {
						begdate = decod;
					} else if (pari[0].equals(Resurses.CRITERIA_CREATED_TO)) {
						todate = decod;
					}
				}
				if ((begdate != null) || (todate != null)) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;
					if (todate == null) {
						fromSQL.append("u.pid in (select pid from unitnotice where coalesce(modified,createdate) >= '");
						fromSQL.append(begdate);
						fromSQL.append("')");
					} else if (begdate == null) {
						fromSQL.append("u.pid in (select pid from unitnotice where coalesce(modified,createdate) <= '");
						fromSQL.append(todate);
						fromSQL.append("' )");
					} else {
						fromSQL.append("u.pid in (select pid from unitnotice where coalesce(modified,createdate) between '");
						fromSQL.append(begdate);
						fromSQL.append("' and '");
						fromSQL.append(todate);
						fromSQL.append("' ) ");
					}
				}
				String viewIdTxt = null;
				for (idx = 1; idx < params.length; idx++) {
					pari = params[idx].split("=");
					decod = URLDecoder.decode(pari[1], "UTF-8");

					if (pari[0].equals(Resurses.CRITERIA_VIEW)) {
						viewIdTxt = decod;
					}
				}
				if (viewIdTxt != null) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;

					fromSQL.append("u.pid in (select pid from viewunits where vid = ");
					fromSQL.append(viewIdTxt);
					fromSQL.append(") ");

				}

				if ((searchPlace != null) || (searchVillage != null)
						|| (searchFarm != null) || (searchCroft != null)
						|| (searchNoticeTag != null)) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstPlaceCriteria = true;
					if (((searchPlace != null) || (searchVillage != null)
							|| (searchFarm != null) || (searchCroft != null))
							&& (searchNoticeTag == null)) {
						fromSQL.append("u.pid in (select pid from unitnotice where");
						if (searchPlace != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" place like '");
							} else {
								fromSQL.append(" place ilike '");
							}
							fromSQL.append(toQuery(searchPlace));
							fromSQL.append("%'");
						}
						if (searchVillage != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" village like '");
							} else {
								fromSQL.append(" village ilike '");
							}
							fromSQL.append(toQuery(searchVillage));
							fromSQL.append("%'");
						}
						if (searchFarm != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" farm like '");
							} else {
								fromSQL.append(" farm ilike '");
							}
							fromSQL.append(toQuery(searchFarm));
							fromSQL.append("%'");
						}
						if (searchCroft != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" croft like '");
							} else {
								fromSQL.append(" croft ilike '");
							}
							fromSQL.append(toQuery(searchCroft));
							fromSQL.append("%'");
						}
						fromSQL.append(") ");
					} else if (((searchPlace != null)
							|| (searchVillage != null) || (searchFarm != null) || (searchCroft != null))
							&& (searchNoticeTag != null)) {
						fromSQL.append("u.pid in (select pid from unitnotice where");
						if (searchPlace != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" place like '");
							} else {
								fromSQL.append(" place ilike '");
							}
							fromSQL.append(toQuery(searchPlace));
							fromSQL.append("'");
						}
						if (searchVillage != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" village like '");
							} else {
								fromSQL.append(" village ilike '");
							}
							fromSQL.append(toQuery(searchVillage));
							fromSQL.append("'");
						}
						if (searchFarm != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" farm like '");
							} else {
								fromSQL.append(" farm ilike '");
							}
							fromSQL.append(toQuery(searchFarm));
							fromSQL.append("'");
						}
						if (searchCroft != null) {
							if (isFirstPlaceCriteria) {
								isFirstPlaceCriteria = false;
							} else {
								fromSQL.append(" and");
							}
							if (isH2){
								fromSQL.append(" croft like '");
							} else {
								fromSQL.append(" croft ilike '");
							}
							fromSQL.append(toQuery(searchCroft));
							fromSQL.append("'");
						}
						fromSQL.append(" and tag = '");
						fromSQL.append(searchNoticeTag);
						fromSQL.append("') ");
					} else if ((searchPlace == null) && (searchVillage == null)
							&& (searchFarm == null) && (searchCroft == null)
							&& (searchNoticeTag != null)) {
						if (!searchNoNotice) {
							fromSQL.append("u.pid in (select pid from unitnotice where tag = '");
							fromSQL.append(searchNoticeTag);
							fromSQL.append("') ");
						} else {
							fromSQL.append("u.pid not in (select pid from unitnotice where tag = '");
							fromSQL.append(searchNoticeTag);
							fromSQL.append("') ");
						}
					}

				}
				if (searchMaxSurety < 100) {
					if (isFirstCriteria) {
						fromSQL.append("where ");
					} else {
						fromSQL.append("and ");
					}
					isFirstCriteria = false;

					fromSQL.append("u.pid in (select pid from unitnotice where surety <= ");
					fromSQL.append(searchMaxSurety);
					fromSQL.append(") ");
					fromSQL.append("or u.pid in (select pid from relation where surety <= ");
					fromSQL.append(searchMaxSurety);
					fromSQL.append(") ");

				}
				if (searchFullText != null) {
					if (isFirstCriteria) {
						free.append("where ");
					} else {
						free.append("and ");
					}
					isFirstCriteria = false;

					String[] parts = trimSpaces(searchFullText).split(" ");

					free.append("(");
					int valueAndOrNot = 0;
					for (int i = 0; i < parts.length; i++) {

						if (i > 0) {
							if (parts[i].equalsIgnoreCase(Resurses
									.getString("CRITERIA_AND"))) {
								valueAndOrNot = 0;
								continue;
							} else if (parts[i].equalsIgnoreCase(Resurses
									.getString("CRITERIA_OR"))) {
								valueAndOrNot = 1;
								continue;
							} else if (parts[i].equalsIgnoreCase(Resurses
									.getString("CRITERIA_NOT"))) {
								valueAndOrNot = 2;
								continue;
							}

							free.append(valueAndOrNot == 1 ? "or " : "and ");
						}
						free.append("u.pid ");
						free.append((valueAndOrNot == 2 ? "not " : ""));
						if (isH2){
							free.append("in (select pid from fullTextView where fulltext like '%");
						} else {
							free.append("in (select pid from fullTextView where fulltext ilike '%");
						}
						free.append(toQuery(parts[i]));
						free.append("%') ");
					}
					free.append(")");

				}
				fromSQL.append(free.toString());

			}

			StringBuilder sql = new StringBuilder();

			sql.append(seleSQL);
			sql.append(fromSQL);

			sql.append("order by u.pid,n.noticerow ");

			logger.info(sql.toString());

			PersonShortData perso = null;

			Statement stm = this.con.createStatement();
			ResultSet rs = stm.executeQuery(sql.toString());
			int currentPid = 0;
			int pid;

			String dbntag; // 6
			String dbgivenname; // 7
			String dbpatronym; // 8
			String dbprefix; // 9
			String dbsurname; // 10
			String dbpostfix; // 11
			String dbfromdate; // 12
			String dbplace; // 13
			String dbvillage; // 14
			String dbfarm; // 15
			String dbcroft; // 16

			String dbdescription; // 17
			int dbnpid; // 18
			String dbmediafilename; // 19
			String dbmediatitle; // 20
			String dbCountry; // 21
			String rtag;

			while (rs.next()) {
				pid = rs.getInt(1);
				// dbutag = rs.getString(5);
				dbntag = rs.getString(6);
				dbgivenname = rs.getString(7);
				dbpatronym = rs.getString(8);
				dbprefix = rs.getString(9);
				dbsurname = rs.getString(10);
				dbpostfix = rs.getString(11);
				dbfromdate = rs.getString(12);
				dbplace = rs.getString(13);
				dbvillage = rs.getString(14);
				dbfarm = rs.getString(15);
				dbcroft = rs.getString(16);
				dbdescription = rs.getString(17);
				dbnpid = rs.getInt(18);
				dbmediafilename = rs.getString(19);
				dbmediatitle = rs.getString(20);
				dbCountry = rs.getString(21);
				if ((pid != currentPid) && (currentPid != 0) && (perso != null)) {
					personList.add(perso);

					persMap.put(perso.getPid(), perso);
					perso = null;
				}
				currentPid = pid;
				if (perso == null) {
					perso = new PersonShortData();
					perso.setPid(pid);
					persMap.put(perso.getPid(), perso);
					perso.setSex(rs.getString(2));
					perso.setRefn(rs.getString(3));
					perso.setGroup(rs.getString(4));

				}
				if (dbntag != null) {
					if (dbntag.equals("NAME")) {
						perso.addName(dbgivenname, dbpatronym, dbprefix,
								dbsurname, dbpostfix);
					}

					if (dbntag.equals("UNKN")) {
						perso.setUnkn(true);
					}
					if (dbntag.equals("OCCU")) {
						perso.setOccupation(dbdescription);
					}
					if (dbntag.equals("PHOT")) {
						perso.setMediaDataNotice(dbnpid);
						perso.setMediaTitle(dbmediatitle);
						perso.setMediaFilename(dbmediafilename);

					}

					if (dbntag.equals("BIRT") || dbntag.equals("CHR")) // &&

					{

						if ((perso.getBirtTag() == null)
								|| dbntag.equals("BIRT")) {
							perso.setBirtTag(dbntag);
							perso.setBirtDate(dbfromdate);
							perso.setBirtPlace(dbplace);
							perso.setBirtVillage(dbvillage);
							perso.setBirtFarm(dbfarm);
							perso.setBirtCroft(dbcroft);
							perso.setBirtCountry(dbCountry);
						}
					}

					if (dbntag.equals("DEAT") || dbntag.equals("BURI")) // &&

					{

						if ((perso.getDeatTag() == null)
								|| dbntag.equals("DEAT")) {
							perso.setDeatTag(dbntag);
							perso.setDeatDate(dbfromdate);
							perso.setDeatPlace(dbplace);
							perso.setDeatVillage(dbvillage);
							perso.setDeatFarm(dbfarm);
							perso.setDeatCroft(dbcroft);
							perso.setDeatCountry(dbCountry);
							perso.setDeatCause(dbdescription);
						}
					}
				}
			}
			if (perso != null) {
				// personDict.Add(perso.Pid, perso);
				personList.add(perso);
				persMap.put(perso.getPid(), perso);
				perso = null;
			}
			if (needsRelativeInfo) {

				StringBuilder relSQL = new StringBuilder();
				// relSQL.append("select tag,pid,count(*) from relation where pid in "
				// + "(select pid  from unit u ");
				// relSQL.append(fromSQL);
				// relSQL.append(") group by pid,tag order by pid");
				// select a.tag,a.pid aid,b.pid bid
				// from relation as a inner join relation as b on a.rid = b.rid
				// and a.pid <> b.pid
				// where a.pid in (select pid from unit u )
				relSQL.append("select a.tag,a.pid  ,b.pid  ");
				relSQL.append("from unit as u inner join relation as a on u.pid=a.pid inner join relation as b "
						+ "on a.rid = b.rid and u.pid <> b.pid ");
				relSQL.append(fromSQL);
				relSQL.append("order by u.pid");
				logger.fine("Relative sql: " + relSQL.toString());
				PreparedStatement pstm = this.con.prepareStatement(relSQL
						.toString());
				ResultSet prs = pstm.executeQuery();

				while (prs.next()) {
					rtag = prs.getString(1);
					int aid = prs.getInt(2);
					int bid = prs.getInt(3);

					PersonShortData rp = persMap.get(aid);
					if (rp != null) {

						if (rtag.equals("HUSB") || rtag.equals("WIFE")) {

							rp.setMarrCount(rp.getMarrCount() + 1);
						} else if (rtag.equals("CHIL")) {

							rp.setChildCount(rp.getChildCount() + 1);
						} else if (rtag.equals("MOTH") || rtag.equals("FATH")) {
							if (rtag.equals("MOTH")) {
								if (rp.getMotherPid() == 0) {
									rp.setMotherPid(bid);
								}
							} else {
								if (rp.getFatherPid() == 0) {
									rp.setFatherPid(bid);
								}
							}
							rp.setPareCount(rp.getPareCount() + 1);
						}
					}
				}
				prs.close();

			}

		} catch (Exception e) {

			throw new SukuException(e);
		}

		PersonShortData[] dt = new PersonShortData[0];
		SukuData qlist = new SukuData();
		qlist.pers = personList.toArray(dt);
		return qlist;
	}

	private String trimSpaces(String searchFullText) {
		StringBuilder sb = new StringBuilder();
		boolean wasSpace = true;
		for (int i = 0; i < searchFullText.length(); i++) {
			char c = searchFullText.charAt(i);
			if (c == ' ') {
				if (wasSpace) {
					continue;
				} else {
					wasSpace = true;
				}
			} else {
				wasSpace = false;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Encode text for sql-queries e.f. O'Brien => O''Brien
	 * 
	 * @param text
	 * @return encoded text
	 */
	private String toQuery(String text) {
		if (text == null) {
			return null;
		}
		if (text.indexOf('\'') < 0) {
			return text;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			switch (c) {
			case '\'':
				sb.append("''");
				break;
			default:
				sb.append(c);
			}
		}

		return sb.toString();

	}
}
