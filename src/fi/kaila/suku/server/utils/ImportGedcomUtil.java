package fi.kaila.suku.server.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.kaila.suku.imports.ImportGedcomDialog;
import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.Relation;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * 
 * Gedcom import class
 * 
 * @author Kalle
 * 
 */
public class ImportGedcomUtil {

	private Connection con;

	private ImportGedcomDialog runner = null;
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private enum GedSet {
		Set_None, Set_Ascii, Set_Ansel, Set_Utf8, Set_Utf16le, Set_Utf16be
	}

	private GedSet thisSet = GedSet.Set_None;

	/**
	 * Constructor with connection
	 * 
	 * @param con
	 */
	public ImportGedcomUtil(Connection con) {
		this.con = con;
		this.runner = ImportGedcomDialog.getRunner();
	}

	LinkedHashMap<String, GedcomPidEle> gedPid = null;
	LinkedHashMap<String, GedcomLine> gedMap = null;
	LinkedHashMap<String, GedcomLine> gedSource = null;
	LinkedHashMap<String, GedcomLine> gedFamMap = null;
	Vector<String> unknownLine = new Vector<String>();
	LinkedHashMap<String, GedcomFams> gedFams = null;
	Vector<GedcomLine> gedAdopt = null;
	HashMap<String, String> texts = null;

	/**
	 * @param file
	 * @param db
	 * @return result in SukuData
	 * @throws SukuException
	 */
	public SukuData importGedcom(String file, String db,
			Vector<String[]> vvTexts) throws SukuException {
		SukuData resp = new SukuData();
		gedMap = new LinkedHashMap<String, GedcomLine>();
		gedSource = new LinkedHashMap<String, GedcomLine>();
		gedFamMap = new LinkedHashMap<String, GedcomLine>();
		gedPid = new LinkedHashMap<String, GedcomPidEle>();
		gedFams = new LinkedHashMap<String, GedcomFams>();
		gedAdopt = new Vector<GedcomLine>();
		seenTrlr = false;
		double indiCount = 0;
		double famCount = 0;
		GedcomLine record = null;
		Statement stm;

		texts = new HashMap<String, String>();
		if (vvTexts != null) {
			for (int i = 0; i < vvTexts.size(); i++) {
				String[] parts = vvTexts.get(i);
				texts.put(parts[0], parts[1]);
			}
		}

		try {
			int unitCount = 0;
			stm = con.createStatement();
			ResultSet rs = stm.executeQuery("select count(*) from unit");

			if (rs.next()) {
				unitCount = rs.getInt(1);

			}
			rs.close();
			stm.close();
			if (unitCount > 0) {
				resp.resu = Resurses.getString("DATABASE_NOT_EMPTY");
				return resp;
			}

			BufferedInputStream bis = new BufferedInputStream(Suku.kontroller
					.getInputStream());

			long dataLen = Suku.kontroller.getFileLength();
			double dLen = dataLen;
			int data = 0;
			fileIndex = 4;
			if (dataLen < 10) {
				resp.resu = Resurses.getString("GEDCOM_BAD_FORMAT");
				return resp;
			}
			char c;
			int data0 = bis.read();
			int data1 = bis.read();
			int data2 = bis.read();
			int data3 = bis.read();

			// int lineNumber = 0;
			StringBuffer line = new StringBuffer();
			if (data0 == 255 && data1 == 254) {
				thisSet = GedSet.Set_Utf16le;
				c = (char) (data3 * 256 + data2);
				line.append(c);
			} else if (data1 == 0 && data0 == 48) {
				thisSet = GedSet.Set_Utf16le;
				c = (char) (data0);
				line.append(c);
				c = (char) (data2);
				line.append(c);
			} else if (data0 == 255 && data1 == 254) {
				thisSet = GedSet.Set_Utf16be;
				c = (char) (data2 * 256 + data3);
				line.append(c);
			} else if (data0 == 0 && data1 == 48) {
				thisSet = GedSet.Set_Utf16be;
				c = (char) (data1);
				line.append(c);
				c = (char) (data3);
				line.append(c);
			} else if (data0 == 239 && data1 == 187 && data2 == 191) {
				thisSet = GedSet.Set_Utf8;
				c = (char) data3;
				line.append(c);
			} else {
				c = (char) data0;
				line.append(c);

				c = (char) data1;
				line.append(c);
				c = (char) data2;
				line.append(c);
				c = (char) data3;
				line.append(c);

			}

			while ((data = bis.read()) >= 0) {

				fileIndex++;

				c = cnvToChar(data, bis);

				if (c != '\n' && c != '\r') {
					if (line.length() > 0 || c != ' ') {
						line.append(c);
					}
				}
				if ((c == '\n' || c == '\r') && line.length() > 0) {

					// System.out.println(line.toString());
					// now we have next line
					// split into parts
					String linex = line.toString();
					int i1 = linex.indexOf(' ');
					int i2 = 0;
					int i3 = 0;
					int ix = 0;

					GedcomLine lineg = null;
					String aux;
					if (i1 > 0 && i1 < linex.length()) {
						try {
							ix = Integer.parseInt(linex.substring(0, i1));

						} catch (NumberFormatException ne) {
							ix = -1;
						}
						lineg = new GedcomLine(ix);
						if (ix == 0) {
							if (record != null) {
								String key = record.getKey();
								gedMap.put(key, record);
								if (record.tag.equals("INDI")) {
									indiCount++;
								} else if (record.tag.equals("FAM")) {
									famCount++;
								}
								// consumeGedcomRecord(record);
								if (this.runner != null) {
									StringBuffer sb = new StringBuffer();

									double dluku = fileIndex;

									double prose = (dluku * 100) / dLen;
									int intprose = (int) prose;
									sb.append("" + intprose + ";"
											+ record.toString(false));
									if (this.runner.setRunnerValue(sb.toString())) {
										throw new SukuException(Resurses.getString("GEDCOM_CANCELLED"));
									}

								}
							}
							record = lineg;

						}

						i2 = linex.indexOf(' ', i1 + 1);
						if (i2 > 0 && i2 < linex.length()) {
							aux = linex.substring(i1 + 1, i2);
							i3 = i2;
							if (aux.charAt(0) == '@'
									&& aux.charAt(aux.length() - 1) == '@') {
								lineg.id = aux;
								i3 = linex.indexOf(' ', i2 + 1);
								if (i3 > 0) {
									lineg.tag = linex.substring(i2 + 1, i3);
								} else {
									lineg.tag = linex.substring(i2 + 1);
								}
							} else {
								lineg.tag = aux;
							}
							if (i3 > 0) {
								lineg.lineValue = linex.substring(i3 + 1);
							}
						} else {
							lineg.tag = linex.substring(i1 + 1);
						}
						// System.out.println("'" + lineg.level + "'" + lineg.id
						// + "'" + lineg.tag + "'" + lineg.lineValue);
					}
					if (lineg.tag == null)
						continue;

					if (lineg.lineValue != null && lineg.level == 1
							&& lineg.tag.equals("CHAR")) {
						if (thisSet == GedSet.Set_None) {
							if (lineg.lineValue.equalsIgnoreCase("UNICODE")
									|| lineg.lineValue
											.equalsIgnoreCase("UTF-8")
									|| lineg.lineValue.equalsIgnoreCase("UTF8")) {
								thisSet = GedSet.Set_Utf8;
							} else if (lineg.lineValue
									.equalsIgnoreCase("ANSEL")) {
								thisSet = GedSet.Set_Ansel;
							}
						}
					} else {
						if (lineg.level > 0) {
							record.add(lineg);

						}
					}
					line = new StringBuffer();

				}

			}

			String key = record.getKey();
			gedMap.put(key, record);
			logger.info("Starting to do INDI");
			// consumeGedcomRecord(record);
			Set<Map.Entry<String, GedcomLine>> lineSet = gedMap.entrySet();
			Iterator<Map.Entry<String, GedcomLine>> ite = lineSet.iterator();
			double indiIndex = 0;
			while (ite.hasNext()) {
				Map.Entry<String, GedcomLine> entry = (Map.Entry<String, GedcomLine>) ite
						.next();
				GedcomLine rec = entry.getValue();
				consumeGedcomRecord(rec);
				StringBuffer sb = new StringBuffer();

				double prose = (indiIndex * 100) / indiCount;
				int intprose = (int) prose;
				sb.append("" + intprose + ";" + rec.toString(false));
				if (this.runner.setRunnerValue(sb.toString())) {
					throw new SukuException(Resurses.getString("GEDCOM_CANCELLED"));
				}
				indiIndex++;
			}

			if (!seenTrlr) {
				resp.resu = Resurses.getString("GEDCOM_NO_TRLR");
			}
			logger.info("Starting to do FAM");
			this.runner.setRunnerValue(Resurses.getString("GEDCOM_FINALIZE"));
			Set<Map.Entry<String, GedcomLine>> entries = gedFamMap.entrySet();
			Iterator<Map.Entry<String, GedcomLine>> ee = entries.iterator();
			double famIndex = 0;
			while (ee.hasNext()) {
				Map.Entry<String, GedcomLine> entry = (Map.Entry<String, GedcomLine>) ee
						.next();
				GedcomLine rec = entry.getValue();
				consumeGedcomFam(rec);
				StringBuffer sb = new StringBuffer();
				double prose = (famIndex * 100) / famCount;
				int intprose = (int) prose;
				sb.append("" + intprose + ";" + rec.toString(false));
				if (this.runner.setRunnerValue(sb.toString())) {
					throw new SukuException(Resurses.getString("GEDCOM_CANCELLED"));
				}
				famIndex++;
			}
			logger.info("Mostly done now");
			// this.runner.setRunnerValue(Resurses.getString("GEDCOM_FINALIZE"));
			// consumeFams();
			// resp.generalArray = recs.toArray(new String[0]);

			resp.generalArray = unknownLine.toArray(new String[0]);
			bis.close();
		} catch (Exception e) {
			unknownLine.add(e.getMessage());
			resp.generalArray = unknownLine.toArray(new String[0]);
			throw new SukuException(e);
		} finally {
			gedMap.clear();
			gedSource.clear();
			gedFamMap.clear();
			gedPid.clear();
			gedFams.clear();
			gedAdopt.removeAllElements();
			logger.info("maps cleared");
		}
		return resp;
		// SukuUtility data = SukuUtility.instance();
		// data.createSukuDb(this.con, "/sql/finfamily.sql");

		// logger.fine("database created for " + path);

	}

	/**
	 * The FAMS lines from INDI records have been stored in the gedFams map We
	 * use those to put spouses in correct order
	 * 
	 */
	private void consumeFams() {
		Set<Map.Entry<String, GedcomFams>> entries = gedFams.entrySet();
		Iterator<Map.Entry<String, GedcomFams>> ee = entries.iterator();
		Vector<String> recs = new Vector<String>();
		while (ee.hasNext()) {
			Map.Entry<String, GedcomFams> entry = (Map.Entry<String, GedcomFams>) ee
					.next();

			GedcomFams fam = entry.getValue();

			int spouseRow = 0;
			if (fam.fams.size() > 1) {
				// System.out.println("DO FAM for " + fam.id);

				for (int i = 0; i < fam.fams.size(); i++) {

					GedcomLine lin = fam.fams.get(i);

					GedcomLine ff = gedFamMap.get(lin.lineValue);
					if (ff != null) {
						// System.out.println("FAM " + i + " with " + ff);

						//
						// now lets get the other spouse from ff
						//
						String spouseId = null;
						for (int j = 0; j < ff.lines.size(); j++) {
							GedcomLine fff = ff.lines.get(j);
							if (fff.tag.equals("HUSB")
									|| fff.tag.equals("WIFE")) {
								// if (!fff.lineValue.equals(fam.id)) {
								if (!fam.id.equals(fff.lineValue)) {
									spouseId = fff.lineValue;
									break;
								}
							}
						}
						if (spouseId != null) {
							GedcomPidEle pele = this.gedPid.get(spouseId);
							if (pele != null) {
								int spousePid = pele.pid;
								spouseRow++;

								updateSpouseRow(fam.pid, spousePid, spouseRow);
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < gedAdopt.size(); i++) {

			GedcomLine line = gedAdopt.get(i);
			for (int j = 0; j < line.lines.size(); j++) {
				GedcomLine item = line.lines.get(j);
				if (item.tag.equals("FAMC")) {// check the FAMC case here
					GedcomLine detail = null;
					for (int k = 0; k < item.lines.size(); k++) {
						detail = item.lines.get(k);
						// detail tag
					}
					// System.out.println("ADOPT1:" + line.toString());
					// System.out.println("ADOPT2:" + item.toString());
					// System.out.println("ADOPT3:" + detail.toString());
					GedcomPidEle pele = this.gedPid.get(line.lineValue);
					if (pele != null) {

						GedcomLine ff = gedFamMap.get(item.lineValue); // get
						// fam
						// id
						if (ff != null) {

							for (int ii = 0; ii < ff.lines.size(); ii++) {
								GedcomLine fff = ff.lines.get(ii);
								String pareId = null;
								if (detail == null) {
									if (fff.tag.equals("HUSB")
											|| fff.tag.equals("WIFE")) {
										pareId = fff.lineValue;
									}
								} else if ("FATH".equals(detail.lineValue)) {
									if (fff.tag.equals("HUSB")) {
										pareId = fff.lineValue;
									}
								} else if ("MOTH".equals(detail.lineValue)) {
									if (fff.tag.equals("WIFE")) {
										pareId = fff.lineValue;
									}
								}
								if (pareId != null) {

									// System.out.println("LL:" + line.lineValue
									// + "/" + pareId);
									GedcomPidEle cele = this.gedPid
											.get(line.lineValue);
									GedcomPidEle fmele = this.gedPid
											.get(pareId);

									insertAdoptedNotice(cele.pid, fmele.pid);

								}
							}
						}
					}
				}
			}
		}
	}

	private void insertAdoptedNotice(int childpid, int parepid) {

		Statement stm;
		try {
			stm = con.createStatement();
			ResultSet rs = stm
					.executeQuery("select nextval('RelationNoticeSeq')");
			int rnid = 0;
			int rid = 0;
			if (rs.next()) {
				rnid = rs.getInt(1);
			} else {
				throw new SQLException("Sequence relationseq error");
			}
			rs.close();

			String sql = "select rid from parent where aid=? and bid = ? and tag in ('FATH','MOTH')";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, childpid);
			pst.setInt(2, parepid);
			rs = pst.executeQuery();
			while (rs.next()) {
				rid = rs.getInt(1);
			}
			rs.close();
			if (rnid > 0 && rid > 0) {

				sql = "insert into relationnotice (rnid,rid,noticerow,tag) values (?,?,1,'ADOP')";
				pst = con.prepareStatement(sql);
				pst.setInt(1, rnid);
				pst.setInt(2, rid);
				pst.executeUpdate();
				pst.close();
			} else {
				logger.warning("adopted rid = " + rid + " and rnid = " + rnid
						+ " failed");
			}
			// pst = con.prepareStatement(insSql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void updateSpouseRow(int pid, int spousePid, int spouseRow) {
		String sql = "update relation set relationrow = ? "
				+ "where pid = ? and rid in (select rid from spouse where aid = ? and bid = ?)";

		PreparedStatement pst;
		try {
			pst = con.prepareStatement(sql);

			pst.setInt(1, spouseRow);
			pst.setInt(2, pid);
			pst.setInt(3, pid);
			pst.setInt(4, spousePid);
			int resu = pst.executeUpdate();
			if (resu != 1) {
				logger.warning("Spouse row update for pid [" + pid
						+ "], spousepid [" + spousePid
						+ "] reulted in update of [" + resu + "] lines.");
			}

		} catch (SQLException e) {
			logger.log(Level.WARNING, "Spouse row update failed", e);
		}
	}

	private void consumeGedcomFam(GedcomLine record) {
		SukuData req = new SukuData();

		Vector<Relation> rels = new Vector<Relation>();
		Relation rel = null;
		Relation crel;
		int ownerPid = 0;
		int pareIdx = 0;
		int childIdx = 0;
		GedcomLine lineHusb = null;
		GedcomLine lineWife = null;
		GedcomLine lineChil = null;
		Relation famRel = null;
		GedcomPidEle aid = null;
		GedcomPidEle bid = null;
		GedcomPidEle cid = null;

		Vector<RelationNotice> relNotice = new Vector<RelationNotice>();
		for (int i = 0; i < record.lines.size(); i++) {
			if (record.lines.get(i).tag.equals("HUSB")) {
				lineHusb = record.lines.get(i);
			} else if (record.lines.get(i).tag.equals("WIFE")) {
				lineWife = record.lines.get(i);
			}
		}
		if (lineHusb != null && lineWife != null) {
			aid = gedPid.get(lineHusb.lineValue);
			bid = gedPid.get(lineWife.lineValue);
			if (aid != null && bid != null) {
				if (ownerPid == 0) {
					ownerPid = aid.pid;
				}

				rel = new Relation(0, aid.pid, bid.pid, "WIFE", 100, null, null);
				rels.add(rel);
			}
		}
		if (lineHusb != null) {
			aid = gedPid.get(lineHusb.lineValue);
			if (aid != null) {
				ownerPid = aid.pid;
			}
		} else {
			bid = gedPid.get(lineWife.lineValue);
			if (bid == null) {
				ownerPid = bid.pid;
			}
		}

		RelationNotice rnmarr = null;
		RelationNotice rn = null;
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine line = record.lines.get(i);
			if ("|MARR|DIV|ANUL|CENS|DIVF|ENGA|MARB|MARC|MARL|MARS|"
					.indexOf(line.tag) > 0) {
				if (line.tag.equals("MARR")) {
					rnmarr = new RelationNotice(line.tag);
					rnmarr.setSurety(100);
					relNotice.add(rnmarr);
					rn = rnmarr;
				} else {
					rn = new RelationNotice(line.tag);
					rn.setSurety(100);
					relNotice.add(rn);
				}
				for (int j = 0; j < line.lines.size(); j++) {
					GedcomLine detail = line.lines.get(j);

					if (detail.tag.equals("TYPE")) {
						rn.setType(detail.lineValue);
					} else if (detail.tag.equals("NOTE")) {
						rn.setNoteText(detail.lineValue);
					} else if (detail.tag.equals("PLAC")) {
						rn.setPlace(detail.lineValue);
					} else if (detail.tag.equals("SOUR")) {
						rn.setSource(detail.lineValue);
					} else if (detail.tag.equals("DATE")) {
						String[] dparts = consumeGedcomDate(detail.lineValue);
						if (dparts != null && dparts.length > 3) {
							rn.setDatePrefix(dparts[0]);
							rn.setFromDate(dparts[1]);
							rn.setToDate(dparts[2]);
							rn.setDescription(dparts[3]);
						}

					} else {
						unknownLine.add(detail.toString());
					}

				}

			} else if (line.tag.equals("HUSB") || line.tag.equals("WIFE")) {
			} else if (line.tag.equals("NOTE")) {
				if (rnmarr != null) {
					StringBuffer sb = new StringBuffer();
					if (rnmarr.getNoteText() != null) {
						sb.append(rnmarr.getNoteText());
						sb.append(" ");
					}
					sb.append(line.lineValue);
					rnmarr.setNoteText(sb.toString());
				}

			} else if (line.tag.equals("CHIL")) {
				lineChil = record.lines.get(i);
				cid = gedPid.get(lineChil.lineValue);
				if (cid != null) {
					if (aid != null) {
						crel = new Relation(0, cid.pid, aid.pid, "FATH", 100,
								null, null);
						rels.add(crel);
					}
					if (bid != null) {
						crel = new Relation(0, cid.pid, bid.pid, "MOTH", 100,
								null, null);
						rels.add(crel);
					}
				}
			} else {
				unknownLine.add(line.toString());
			}
		}
		if (relNotice != null && rel != null) {
			rel.setNotices(relNotice.toArray(new RelationNotice[0]));
		}

		//
		// pick up the position of both husband and wife from FAMS lines in INDI
		// records
		// to set row number of spouse
		//

		int husbandNumber = 0;
		int wifeNumber = 0;
		if (aid != null && bid != null) {

			GedcomFams fms = gedFams.get(aid.id);
			wifeNumber = -1;
			for (int ffi = 0; ffi < fms.fams.size(); ffi++) {
				GedcomLine fam = fms.fams.get(ffi);
				GedcomLine faw = gedFamMap.get(fam.lineValue);
				for (int ffj = 0; ffj < faw.lines.size(); ffj++) {
					GedcomLine fax = faw.lines.get(ffj);
					if (fax.lineValue != null && fax.lineValue.equals(bid.id)) {
						wifeNumber = ffi;
						break;
					}
				}
				if (wifeNumber >= 0) {
					break;
				}
			}
			if (wifeNumber <= 0) {
				wifeNumber = 0;
			}

			fms = gedFams.get(bid.id);
			husbandNumber = -1;
			for (int ffi = 0; ffi < fms.fams.size(); ffi++) {
				GedcomLine fam = fms.fams.get(ffi);
				GedcomLine faw = gedFamMap.get(fam.lineValue);
				for (int ffj = 0; ffj < faw.lines.size(); ffj++) {
					GedcomLine fax = faw.lines.get(ffj);
					if (fax.lineValue != null && fax.lineValue.equals(aid.id)) {
						husbandNumber = ffi;
						break;
					}
				}
				if (husbandNumber >= 0) {
					break;
				}
			}
			if (husbandNumber <= 0) {
				husbandNumber = 0;
			}

		}

		PersonUtil u = new PersonUtil(con);
		String resu = u.insertGedcomRelations(husbandNumber, wifeNumber, rels
				.toArray(new Relation[0]));
		if (resu != null) {
			unknownLine.add(record.toString() + ":" + resu);
		}
		// SukuData resp = u.updatePerson(req);

	}

	int recordCount = 0;
	String submitterId = null;
	String ownerInfo = null;
	boolean seenTrlr = false;

	private void consumeGedcomRecord(GedcomLine record) throws SQLException, SukuException {
		recordCount++;
		if (seenTrlr) {
			unknownLine.add(record.toString());
			return;
		}
		if (recordCount == 1) { // expected HEAD record
			if (!record.tag.equals("HEAD")) {
				unknownLine.add(Resurses.getString("GEDCOM_MISSING_HEAD"));
			} else {
				consumeGedcomHead(record);
			}
		} else if (record.tag.equals("SUBM")) {
			consumeGedcomSubmitter(record);
		} else if (record.tag.equals("INDI")) {
			consumeGedcomIndi(record);
		} else if (record.tag.equals("SOUR") || record.tag.equals("NOTE")) {
			gedSource.put(record.id, record);
		} else if (record.tag.equals("FAM")) {
			gedFamMap.put(record.id, record);
		} else if (record.tag.equals("TRLR")) {
			seenTrlr = true;
		} else {
			unknownLine.add(record.toString());
		}

	}

	private String extractGedcomSource(GedcomLine record) {
		StringBuffer sb = new StringBuffer();
		sb.append(record.lineValue);
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine line = record.lines.get(i);
			if (line.tag.equals("TEXT") || line.tag.equals("NOTE")) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(line.lineValue);
			}
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}

	private static final String notiTags = "|OCCU|EDUC|TITL|RESI|PROP|FACT"
			+ "|BIRT|CHR|DEAT|BURI|EVEN|RESI|EMIG|IMMI|CAST|DSCR|EDUC|IDNO"
			+ "|NATI|NCHI|NMR|PROP|RELI|SSN|FACT|CREM|BAPM|BASM|BLES|BARM"
			+ "|CHRA|CONF|FCOM|ORND|NATU|CENS|PROB|WILL|GRAD|RETI|ADOP|";

	private void consumeGedcomIndi(GedcomLine record) throws SukuException {
		PersonLongData pers = new PersonLongData(0, "INDI", "U");
		Vector<UnitNotice> notices = new Vector<UnitNotice>();
		GedcomFams f = new GedcomFams();
		f.id = record.id;
		pers.setUserRefn(record.id);
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine noti = record.lines.get(i);
			if (noti.tag.equals("SEX")) {
				pers.setSex(noti.lineValue);
			} else if (noti.tag.equals("REFN")) {
				pers.setUserRefn(noti.lineValue);
			} else if (noti.tag.equals("NAME")) {
				if (noti.lineValue != null) {
					UnitNotice notice = new UnitNotice("NAME");
					notices.add(notice);
					String parts[] = noti.lineValue.split("/");
					if (parts.length > 0) {
						if (parts[0].length() > 0) {

							notice.setGivenname(Utils.extractPatronyme(
									parts[0], false));
							notice.setPatronym(Utils.extractPatronyme(parts[0],
									true));
						}
						if (parts.length > 1 && parts[1].length() > 0) {

							int vonIndex = Utils.isKnownPrefix(parts[1]);
							if (vonIndex > 0) {
								notice.setPrefix(parts[1]
										.substring(0, vonIndex));
								notice.setSurname(parts[1]
										.substring(vonIndex + 1));
							} else {

								notice.setSurname(parts[1]);
							}
						}
						if (parts.length > 2 && parts[2].length() > 0) {
							notice.setPostfix(parts[2]);
						}
					}
				}
			} else if (noti.tag.equals("NOTE")) {
				if (noti.lineValue != null) {
					UnitNotice notice = new UnitNotice("NOTE");
					notices.add(notice);
					if (noti.lineValue.startsWith("@")
							&& noti.lineValue.indexOf('@', 1) > 1) {
						GedcomLine notirec = gedMap.get(noti.lineValue);
						if (notirec != null) {
							notice.setNoteText(notirec.lineValue);
						}
					} else {
						notice.setNoteText(noti.lineValue);
					}
				}
			} else if (noti.tag.equals("OBJE")) {
				UnitNotice notice = new UnitNotice("PHOT");
				notices.add(notice);
				extractMultimedia(notice, noti);
			} else if (noti.tag.equals("FAMC")) {
			} else if (noti.tag.equals("FAMS")) {
				f.fams.add(noti);
			} else if (noti.tag.equals("SOUR")) {
				if (noti.lineValue.startsWith("@")
						&& noti.lineValue.indexOf('@', 1) > 1) {
					GedcomLine dets = gedMap.get(noti.lineValue);

					pers.setSource(extractSourceText(dets));
				} else {
					String src = extractGedcomSource(record);
					pers.setSource(src);
				}
			} else if (notiTags.indexOf(noti.tag) > 0

			|| noti.tag.startsWith("_")) {

				if (noti.tag.equals("ADOP")) {
					noti.lineValue = record.id;
					gedAdopt.add(noti);
				}

				String notiTag = noti.tag;
				if (notiTag.startsWith("_"))
					notiTag = noti.tag.substring(1);
				UnitNotice notice = new UnitNotice(notiTag);
				notices.add(notice);
				notice.setDescription(noti.lineValue);
				for (int j = 0; j < noti.lines.size(); j++) {
					GedcomLine detail = noti.lines.get(j);
					if (detail.tag.equals("TYPE")) {
						notice.setNoticeType(detail.lineValue);
					} else if (detail.tag.equals("FAMC")) {
					} else if (detail.tag.equals("PLAC")) {
						notice.setPlace(detail.lineValue);
					} else if (detail.tag.equals("ADDR")) {
						GedcomAddress address = splitAddress(detail.lineValue);
						notice.setAddress(address.address);
						notice.setPostalCode(address.postalCode);
						notice.setPostOffice(address.postOffice);
						notice.setCountry(address.country);
						notice.setEmail(address.email);
					} else if (detail.tag.equals("EMAIL")) {
						notice.setEmail(detail.lineValue);
					} else if (detail.tag.equals("PHON")) {
						notice
								.setPrivateText(notice.getPrivateText() == null ? detail.lineValue
										: notice.getPrivateText() + ", Tel:"
												+ detail.lineValue);
					} else if (detail.tag.equals("WWW")) {
						notice
								.setPrivateText(notice.getPrivateText() == null ? detail.lineValue
										: notice.getPrivateText() + ", www:"
												+ detail.lineValue);
					} else if (detail.tag.equals("FAX")) {
						notice
								.setPrivateText(notice.getPrivateText() == null ? detail.lineValue
										: notice.getPrivateText() + ", Fax:"
												+ detail.lineValue);
					} else if (detail.tag.equals("NOTE")) {
						if (detail.lineValue.startsWith("@")
								&& detail.lineValue.indexOf('@', 1) > 1) {
							GedcomLine notirec = gedMap.get(detail.lineValue);
							if (notirec != null) {
								notice.setNoteText(notirec.lineValue);
							}
						} else {
							notice.setNoteText(detail.lineValue);
						}
					} else if (detail.tag.equals("SOUR")) {
						if (detail.lineValue.startsWith("@")
								&& detail.lineValue.indexOf('@', 1) > 1) {
							GedcomLine dets = gedMap.get(detail.lineValue);
							notice.setSource(extractSourceText(dets));
						} else {

							String src = extractGedcomSource(detail);
							notice.setSource(src);
							notice.setSurety(extractGedcomSurety(detail));
						}
					} else if (detail.tag.equals("DATE")) {
						String[] dateParts = consumeGedcomDate(detail.lineValue);
						if (dateParts[0] != null) {
							notice.setDatePrefix(dateParts[0]);
						}
						if (dateParts[1] != null) {
							notice.setFromDate(dateParts[1]);
						}
						if (dateParts[2] != null) {
							notice.setToDate(dateParts[2]);
						}
						if (dateParts[3] != null) {
							if (notice.getDescription() != null) {
								notice.setDescription(dateParts[3]);
							} else {
								notice.setDescription(notice.getDescription()
										+ " " + dateParts[3]);
							}
						}
					} else if (detail.tag.equals("OBJE")) {

						extractMultimedia(notice, detail);
					} else {
						unknownLine.add(detail.toString());
					}
				}

			} else {
				unknownLine.add(noti.toString());
			}

		}
		pers.setNotices(notices.toArray(new UnitNotice[0]));

		SukuData request = new SukuData();
		request.persLong = pers;
		PersonUtil u = new PersonUtil(con);

		SukuData resp = u.updatePerson(request);
		if (resp.resu != null) {
			throw new SukuException(resp.resu);
		}
		if (resp.resultPid > 0) {
			GedcomPidEle pide = new GedcomPidEle();
			pide.pid = resp.resultPid;
			pide.sex = pers.getSex();
			pide.id = record.id;

			gedPid.put(record.id, pide);
			f.pid = pide.pid;
			gedFams.put(f.id, f);
		}

	}

	private String extractSourceText(GedcomLine dets) {
		StringBuffer sb = new StringBuffer();
		if (dets.lineValue != null) {
			sb.append(dets.lineValue);
		}
		extractSourceChild(dets, sb);
		return sb.toString();
	}

	private void extractSourceChild(GedcomLine dets, StringBuffer sb) {
		for (int i = 0; i < dets.lines.size(); i++) {
			GedcomLine detl = dets.lines.get(i);
			String name = texts.get("REF_" + detl.tag);
			if (name != null) {
				sb.append(name);
				sb.append(" ");

				if (detl.lineValue != null) {
					sb.append(detl.lineValue);
					sb.append(", ");
				}
			}
			extractSourceChild(detl, sb);
		}

	}

	/**
	 * @param notice
	 * @param detail
	 */
	private void extractMultimedia(UnitNotice notice, GedcomLine detail) {
		for (int k = 0; k < detail.lines.size(); k++) {
			GedcomLine item = detail.lines.get(k);

			if (item.tag.equals("FILE")) {

				InputStream ins = Suku.kontroller.openFile(item.lineValue);
				if (ins != null) {
					BufferedInputStream bstr = new BufferedInputStream(ins);
					// System.out.println("OPEN: " +
					// openedImage);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					byte[] buff = new byte[2048];
					int imgSize = 0;
					while (true) {
						int rdbytes;
						try {
							rdbytes = bstr.read(buff);
						} catch (IOException e) {
							imgSize = -1;
							break;
						}
						imgSize += rdbytes;
						if (rdbytes < 0)
							break;
						bos.write(buff, 0, rdbytes);

					}

					int lastdir = item.lineValue.replace('\\', '/')
							.lastIndexOf('/');
					if (lastdir > 0) {
						notice.setMediaFilename(item.lineValue
								.substring(lastdir + 1));
					}

					if (imgSize > 0) {
						notice.setMediaData(bos.toByteArray());
					}
				} else {
					unknownLine.add(item.toString());
				}
				// try {
				// int luettu = bstr.read(buffer);
				// if (luettu == filesize) {
				// notice.setMediaData(buffer);
				// } else {
				// logger.warning("Filesize expected " +
				// filesize
				// + " read " + luettu);
				// }
				// bstr.close();
				//								
				//								
			} else if (item.tag.equals("FORM")) {
			} else if (item.tag.equals("TITL")) {
				notice.setMediaTitle(item.lineValue);
			} else {

				unknownLine.add(item.toString());
			}
		}
	}

	private int extractGedcomSurety(GedcomLine record) {
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine line = record.lines.get(i);
			if (line.tag.equals("QUAY")) {
				if (line.lineValue == null)
					return 100;
				if (line.lineValue.equals("0"))
					return 40;
				if (line.lineValue.equals("1"))
					return 60;
				if (line.lineValue.equals("2"))
					return 80;
				if (line.lineValue.equals("3"))
					return 100;

			}
		}
		return 100;
	}

	private String[] consumeGedcomDate(String lineValue) {
		if (lineValue == null)
			return null;
		String[] dateparts = new String[4];

		int spIdx = lineValue.indexOf(" ");
		if (spIdx > 0) {
			String fp = lineValue.substring(0, spIdx);

			if (fp.equalsIgnoreCase("FROM") || fp.equalsIgnoreCase("ABT")
					|| fp.equalsIgnoreCase("CAL") || fp.equalsIgnoreCase("EST")
					|| fp.equalsIgnoreCase("TO") || fp.equalsIgnoreCase("BEF")
					|| fp.equalsIgnoreCase("AFT") || fp.equalsIgnoreCase("BET")) {
				dateparts[0] = fp;

				int i1 = 0;
				int i2 = 0;
				if (fp.equalsIgnoreCase("FROM")) {
					i1 = lineValue.indexOf("TO");
					i2 = lineValue.indexOf(" ", i1 + 2);
				} else if (fp.equalsIgnoreCase("BET")) {
					i1 = lineValue.indexOf("AND");
					i2 = lineValue.indexOf(" ", i1 + 2);
				}

				String aux = null;
				if (i1 > 0) {
					aux = toSukuDate(lineValue.substring(spIdx, i1));
				} else {
					aux = toSukuDate(lineValue.substring(spIdx));
				}
				if (aux != null) {
					dateparts[1] = aux;
				} else {
					dateparts[3] = lineValue;
				}
				if (i2 > 0) {
					aux = toSukuDate(lineValue.substring(i2));
					if (aux != null) {
						dateparts[2] = aux;
					} else {
						dateparts[3] = lineValue;
					}
				}

			} else { // assume it's date format only
				String aux = toSukuDate(lineValue);
				if (aux != null) {
					dateparts[1] = aux;
				} else {
					dateparts[3] = lineValue;
				}
			}
		} else {
			String aux = toSukuDate(lineValue);
			if (aux != null) {
				dateparts[1] = aux;
			} else {
				dateparts[3] = lineValue;
			}
		}
		return dateparts;
	}

	private String toSukuDate(String gedcomDate) {
		String[] parts = gedcomDate.split(" ");
		int dl = parts.length - 1;
		if (dl > 3) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int year;
		try {
			year = Integer.parseInt(parts[dl]);
		} catch (NumberFormatException ne) {
			return null;
		}
		if (year <= 0 || year >= 3000) {
			return null;
		}
		String aux = "0000" + year;
		sb.append(aux.substring(aux.length() - 4, aux.length()));
		dl--;
		String mm = null;
		if (dl >= 0) {

			int kk = "|JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC|"
					.indexOf(parts[dl].toUpperCase());
			if (kk > 0) {
				kk--;
				kk /= 2;
				mm = "010203040506070809101112".substring(kk, kk + 2);
			}
		}
		if (mm != null) {
			sb.append(mm);
		} else {
			return sb.toString();
		}
		dl--;
		String dat = null;
		if (dl >= 0) {
			int dd = 0;
			try {
				dd = Integer.parseInt(parts[dl]);
			} catch (NumberFormatException ne) {
				return null;
			}
			if (dd > 0 && dd <= 31) {
				dat = "000" + dd;
				dat = dat.substring(dat.length() - 2, dat.length());
			}
		}
		if (dat != null) {
			sb.append(dat);
		}
		return sb.toString();

	}

	boolean submitterDone = false;

	private void consumeGedcomSubmitter(GedcomLine record) throws SQLException {
		String name = null;
		GedcomAddress address = null;

		if (submitter == null || !submitter.equals(record.id) || submitterDone) {
			unknownLine.add(record.toString());
			return;
		}
		submitterDone = true;
		StringBuffer note = new StringBuffer();
		if (ownerInfo != null) {
			note.append(ownerInfo);
		}

		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine noti = record.lines.get(i);
			if (noti.tag.equals("NAME")) {
				name = noti.lineValue;
			} else if (noti.tag.equals("ADDR")) {
				address = splitAddress(noti.lineValue);
			}
		}

		String sql = "insert into sukuvariables (owner_name,owner_info, "
				+ "owner_address,owner_postalcode,owner_postoffice,"
				+ "owner_country,owner_email,user_id) values (?,?,?,?,?,?,?,user) ";

		PreparedStatement pst = con.prepareStatement(sql);
		pst.setString(1, name);
		pst.setString(2, ownerInfo);
		if (address != null) {
			pst.setString(3, address.address);
			pst.setString(4, address.postalCode);
			pst.setString(5, address.postOffice);
			pst.setString(6, address.country);
			pst.setString(7, address.email);
		} else {
			pst.setString(3, null);
			pst.setString(4, null);
			pst.setString(5, null);
			pst.setString(6, null);
			pst.setString(7, null);
		}
		int lukuri = pst.executeUpdate();
		logger.info("Sukuvariables updated " + lukuri + " lines");
	}

	private GedcomAddress splitAddress(String lineValue) {
		StringBuffer address = new StringBuffer();
		StringBuffer country = new StringBuffer();
		GedcomAddress addr = new GedcomAddress();
		if (lineValue != null) {
			String parts[] = lineValue.split("\n");
			boolean wasPo = false;
			for (int j = 0; j < parts.length; j++) {
				if (j == 0) {
					address.append(parts[j]);
				} else {
					if (parts[j].indexOf('@') > 0) { // possibly email
						addr.email = parts[j];
					} else if (!wasPo) {
						String posts[] = parts[j].split(" ");
						if (posts.length > 1) {
							int ponum = -1;
							try {
								ponum = Integer.parseInt(posts[0]);
							} catch (NumberFormatException ne) {

							}
							if (ponum > 1) { // now assume beginning is
								// postalcode
								addr.postalCode = posts[0];
								addr.postOffice = parts[j].substring(posts[0]
										.length() + 1);
								wasPo = true;
							}
						}
						if (!wasPo) {
							if (address.length() > 0) {
								address.append("\n");
							}
							address.append(parts[j]);

						}
					} else {
						if (country.length() > 0) {
							country.append("\n");
						}
						country.append(parts[j]);
					}

				}
			}

			if (address.length() > 0) {
				addr.address = address.toString();
			}
			if (country.length() > 0) {
				addr.country = country.toString();
			}
		}
		return addr;
	}

	String submitter = null;

	private void consumeGedcomHead(GedcomLine record) {
		boolean submitterDone = false;
		for (int i = 0; i < record.lines.size(); i++) {
			GedcomLine notice1 = record.lines.get(i);
			if (notice1.tag.equals("SOUR")) {
				continue;
			} else if (notice1.tag.equals("DEST")) {
				continue;
			} else if (notice1.tag.equals("SUBM")) {
				submitter = notice1.lineValue;
				continue;
			} else if (notice1.tag.equals("GEDC")) {
				continue;
			} else if (notice1.tag.equals("NOTE")) {
				ownerInfo = notice1.lineValue;
			} else {
				unknownLine.add(notice1.toString());

			}

		}
	}

	private long fileIndex = 0;

	private char cnvToChar(int data, InputStream bis) throws IOException {
		char c;
		int datax;
		int datay;
		int dataz;
		switch (thisSet) {
		case Set_Utf16le:
			datax = bis.read();
			fileIndex++;
			c = (char) (datax * 256 + data);

			break;
		case Set_Utf16be:
			datax = bis.read();
			fileIndex++;
			c = (char) (data * 256 + datax);

			break;
		case Set_Utf8:
			if ((data & 0x80) == 0) {
				c = (char) data;
				break;
			}
			datax = bis.read();
			fileIndex++;
			if ((data & 0x20) == 0) {
				datax &= 0x3F;
				data &= 0x1F;
				data = data << 6;
				c = (char) (data | datax);
				break;
			}
			datay = bis.read();
			fileIndex++;
			if ((data & 0x10) == 0) {
				datay &= 0x3F;
				datax &= 0x3F;
				data &= 0x0F;
				datax = datax << 6;
				data = data << 10;
				c = (char) (data | datax | datay);
			}
			dataz = bis.read();
			fileIndex++;
			dataz &= 0x3F;
			datay &= 0x3F;
			datax &= 0x3F;
			data &= 0x07;
			datay = datay << 6;
			datax = datax << 12;
			data = data << 18;
			c = (char) (data | datax | datay | dataz);
			break;

		case Set_Ansel:

			if ((data & 0x80) == 0) {
				c = (char) data;
				break;
			}
			if (data < 0xE0) {
				if (data == 0xA4)
					c = 'Þ';
				else if (data == 0xA2)
					c = 'Ø';
				else if (data == 0xA5)
					c = 'Æ';
				else if (data == 0xA6)
					c = 'Œ';
				else if (data == 0xAA)
					c = '®';

				else if (data == 0xAB)
					c = '±';
				else if (data == 0xB2)
					c = 'ø';
				else if (data == 0xB4)
					c = 'þ';
				else if (data == 0xB5)
					c = 'æ';
				else if (data == 0xB6)
					c = 'œ';
				else if (data == 0xB9)
					c = '£';
				else if (data == 0xBA)
					c = 'ð';
				else if (data == 0xC3)
					c = '©';
				else if (data == 0xC5)
					c = '¿';
				else if (data == 0xC6)
					c = '¡';
				else if (data == 0xCF)
					c = 'ß';
				else
					c = '?';

			} else {
				datax = bis.read();
				fileIndex++;
				switch (data) {
				case 0xE1: // grave accent
					if (datax == 'a')
						c = 'à';
					else if (datax == 'A')
						c = 'À';
					else if (datax == 'e')
						c = 'è';
					else if (datax == 'E')
						c = 'È';
					else if (datax == 'i')
						c = 'ì';
					else if (datax == 'I')
						c = 'ì';
					else if (datax == 'o')
						c = 'ò';
					else if (datax == 'O')
						c = 'Ò';
					else if (datax == 'u')
						c = 'ù';
					else if (datax == 'U')
						c = 'Ù';
					else
						c = (char) datax;
					break;
				case 0xE2: // acute accent
					if (datax == 'a')
						c = 'á';
					else if (datax == 'A')
						c = 'Á';
					else if (datax == 'e')
						c = 'é';
					else if (datax == 'E')
						c = 'É';
					else if (datax == 'i')
						c = 'í';
					else if (datax == 'I')
						c = 'Í';
					else if (datax == 'o')
						c = 'ó';
					else if (datax == 'O')
						c = 'Ó';
					else if (datax == 'u')
						c = 'ú';
					else if (datax == 'U')
						c = 'Ú';
					else
						c = (char) datax;
					break;
				case 0xE3: // circumflex accent
					if (datax == 'a')
						c = 'â';
					else if (datax == 'A')
						c = 'Â';
					else if (datax == 'e')
						c = 'ê';
					else if (datax == 'E')
						c = 'Ê';
					else if (datax == 'i')
						c = 'î';
					else if (datax == 'I')
						c = 'Î';
					else if (datax == 'o')
						c = 'ô';
					else if (datax == 'O')
						c = 'Ô';
					else if (datax == 'u')
						c = 'û';
					else if (datax == 'U')
						c = 'Û';
					else
						c = (char) datax;
					break;
				case 0xE4: // tilde
					if (datax == 'a')
						c = 'ã';
					else if (datax == 'A')
						c = 'Ã';
					else if (datax == 'n')
						c = 'ñ';
					else if (datax == 'N')
						c = 'Ñ';
					else if (datax == 'o')
						c = 'õ';
					else if (datax == 'O')
						c = 'Õ';
					else
						c = (char) datax;
					break;
				case 0xF0: // cedilla
					if (datax == 'c')
						c = 'ç';
					else if (datax == 'C')
						c = 'Ç';
					else if (datax == 'n')
						c = 'ñ';
					else if (datax == 'N')
						c = 'Ñ';
					else if (datax == 'o')
						c = 'õ';
					else if (datax == 'O')
						c = 'Õ';
					else
						c = (char) datax;
					break;

				case 0xE8: // umlaut
					if (datax == 'a')
						c = 'ä';
					else if (datax == 'A')
						c = 'Ä';
					else if (datax == 'o')
						c = 'ö';
					else if (datax == 'O')
						c = 'Ö';
					else if (datax == 'u')
						c = 'ü';
					else if (datax == 'U')
						c = 'Ü';
					else if (datax == 'e')
						c = 'ë';
					else if (datax == 'E')
						c = 'Ë';
					else if (datax == 'i')
						c = 'ï';
					else if (datax == 'I')
						c = 'Ï';
					else
						c = (char) datax;
					break;
				case 0xEA: // ringabove
					if (datax == 'a')
						c = 'å';
					else if (datax == 'A')
						c = 'Å';
					else
						c = (char) datax;
					break;

				default:
					c = (char) datax;
					break;
				}
			} // end of ansel
			break;
		default:
			c = (char) data;
			break;
		}
		return c;

	}

	int keyCounter = 0;

	// class GedcomRecord {
	// Vector<GedcomLine> lines = new Vector<GedcomLine>();
	//
	// void add(GedcomLine line) {
	//
	// lines.add(line);
	// }
	//
	// public String toString() {
	// StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < lines.size(); i++) {
	// sb.append(lines.get(i));
	// sb.append("\n");
	// }
	// return sb.toString();
	//
	// }
	//
	// }

	class GedcomLine {
		int level = -1;
		String tag = null;
		String id = null;
		String lineValue = null;
		GedcomLine parent = null;
		Vector<GedcomLine> lines = new Vector<GedcomLine>();

		void add(GedcomLine line) {
			if (line.level == level + 1) {
				if (line.tag.equals("CONT")) {
					lineValue += "\n" + line.lineValue;
				} else if (line.tag.equals("CONC")) {
					lineValue += line.lineValue;
				} else {
					lines.add(line);
					line.parent = this;
				}
			} else {
				int last = lines.size() - 1;
				if (last < 0) {
					unknownLine.add(line.toString());
				} else {
					GedcomLine subline = lines.get(last);
					subline.add(line);
					line.parent = subline;

				}
			}
		}

		String key = null;

		String getKey() {
			if (id != null) {
				return id;
			}
			if (key != null) {
				return key;
			}
			if (tag.equals("HEAD") || tag.equals("TRLR")) {
				key = tag;
				return key;
			}
			keyCounter++;
			key = "X" + keyCounter + "X";
			return key;
		}

		GedcomLine(int level) {
			this.level = level;
		}

		public String toString() {
			return toString(true);
		}

		public String toString(boolean withLevels) {
			StringBuffer sb = new StringBuffer();
			if (level > 0) {
				Vector<String> stack = new Vector<String>();
				GedcomLine pareLine = this.parent;
				while (pareLine != null) {
					if (pareLine.id != null) {
						stack.add("" + pareLine.level + " " + pareLine.id + " "
								+ pareLine.tag);
					} else {
						if (pareLine.level > 0) {
							stack.add(pareLine.tag);
						} else {
							stack.add("" + pareLine.level + " " + pareLine.tag);
						}
					}

					pareLine = pareLine.parent;

				}
				for (int i = stack.size() - 1; i >= 0; i--) {
					sb.append(stack.get(i));
					sb.append("|");
				}
			}
			if (level == 0) {
				sb.append(level);
				sb.append(" ");
			}
			if (id != null) {
				sb.append(id);
				sb.append(" ");
			}
			sb.append(tag);
			if (lineValue != null) {
				sb.append(" ");
				sb.append(lineValue);
			}
			sb.append("\n");
			if (withLevels) {
				for (int i = 0; i < lines.size(); i++) {
					sb.append(lines.get(i).toString());
				}
			}
			return sb.toString();
		}
	}

	class GedcomAddress {
		String address = null;
		String postalCode = null;
		String postOffice = null;
		String country = null;
		String email = null;
	}

	class GedcomPidEle {
		public String id;
		int pid = 0;
		String sex = "U";
	}

	class GedcomFams {
		int pid;
		String id;
		Vector<GedcomLine> fams = new Vector<GedcomLine>();
	}

}