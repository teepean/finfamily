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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fi.kaila.suku.ant.AntVersion;
import fi.kaila.suku.exports.ExportGedcomDialog;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.RelationNotice;
import fi.kaila.suku.util.pojo.SukuData;
import fi.kaila.suku.util.pojo.UnitNotice;

/**
 * The Class ExportGedcomUtil.
 */
public class ExportGedcomUtil {

	private final Connection con;

	private ExportGedcomDialog runner = null;
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private int viewId = 0;
	private int surety = 100;

	// private String[] charsetNames = { "","Ascii", "Ansel", "UTF-8", "UTF-16"
	// };

	private boolean includeImages = true;

	private LinkedHashMap<Integer, MinimumIndividual> units = null;
	private LinkedHashMap<String, MinimumFamily> families = null;
	private HashMap<Integer, MinimumFamily> famById = null;
	private HashMap<Integer, Integer> childRids = null;
	private Vector<MinimumImage> images = null;
	private String zipPath = "nemo";
	private String dbName = "me";

	private enum GedSet {
		Set_None, Set_Ascii, Set_Ansel, Set_Utf8, Set_Utf16
	}

	private GedSet thisSet = GedSet.Set_None;
	private int imageCounter = 0;

	/**
	 * Constructor with connection.
	 *
	 * @param con
	 *            the con
	 */
	public ExportGedcomUtil(Connection con) {
		this.con = con;
		this.runner = ExportGedcomDialog.getRunner();
	}

	/**
	 * Export gedcom.
	 *
	 * @param db
	 *            the db
	 * @param path
	 *            the path
	 * @param langCode
	 *            the lang code
	 * @param viewId
	 *            the view id
	 * @param surety
	 *            the surety
	 * @param charsetId
	 *            the charset id
	 * @param includeImages
	 *            the include images
	 * @return the suku data
	 */
	public SukuData exportGedcom(String db, String path, String langCode, int viewId, int surety, int charsetId,
			boolean includeImages) {

		this.viewId = viewId;
		this.surety = surety;
		switch (charsetId) {
		case 1:
			thisSet = GedSet.Set_Ascii;
			break;
		case 2:
			thisSet = GedSet.Set_Ansel;
			break;
		case 3:
			thisSet = GedSet.Set_Utf8;
			break;
		case 4:
			thisSet = GedSet.Set_Utf16;
			break;
		default:
			thisSet = GedSet.Set_None;
		}

		this.includeImages = includeImages;
		dbName = db;
		units = new LinkedHashMap<Integer, MinimumIndividual>();
		images = new Vector<MinimumImage>();
		families = new LinkedHashMap<String, MinimumFamily>();
		famById = new HashMap<Integer, MinimumFamily>();
		final SukuData result = new SukuData();
		if ((path == null) || (path.lastIndexOf(".") < 1)) {
			result.resu = "output filename missing";
			return result;
		}
		try {
			collectIndividuals();

			collectFamilies();
			childRids = new HashMap<Integer, Integer>();

			final String sql = "select r.pid,n.tag,r.tag,r.surety from relationnotice as n inner join relation  as r on n.rid=r.rid where r.tag in ('FATH','MOTH')";

			final Statement stm = con.createStatement();
			final ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				final int rid = rs.getInt(1);
				childRids.put(rid, rid);
			}
			rs.close();
			stm.close();

			zipPath = path.substring(0, path.lastIndexOf("."));
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();

			final ZipOutputStream zip = new ZipOutputStream(bos);
			final String fileName = zipPath + "/" + dbName + ".ged";

			ZipEntry entry = new ZipEntry(fileName);

			zip.putNextEntry(entry);
			writeBom(zip);
			// insert first the gedcom file here
			writeHead(zip);
			final int allCount = units.size();
			int curreCount = 0;
			final Set<Map.Entry<Integer, MinimumIndividual>> unitss = units.entrySet();
			final Iterator<Map.Entry<Integer, MinimumIndividual>> eex = unitss.iterator();
			while (eex.hasNext()) {
				final Map.Entry<Integer, MinimumIndividual> unitx = eex.next();
				final MinimumIndividual pit = unitx.getValue();
				curreCount++;

				final PersonUtil u = new PersonUtil(con);
				final SukuData fam = u.getFullPerson(pit.pid, langCode);
				final PersonShortData shortie = new PersonShortData(fam.persLong);
				writeIndi(zip, fam.persLong);

				final double prose = (curreCount * 100) / allCount;
				final int intprose = (int) prose;
				final StringBuilder sbb = new StringBuilder();
				sbb.append(intprose);
				sbb.append(";");
				sbb.append(shortie.getAlfaName());
				setRunnerValue(sbb.toString());

			}

			// private LinkedHashMap<ParentPair, MinimumFamily> families = null;
			final Set<Map.Entry<String, MinimumFamily>> fss = families.entrySet();

			final Iterator<Map.Entry<String, MinimumFamily>> ffx = fss.iterator();
			while (ffx.hasNext()) {
				final Map.Entry<String, MinimumFamily> fx = ffx.next();
				final MinimumFamily fix = fx.getValue();

				writeFam(zip, fix, langCode);

			}

			zip.write(gedBytes("0 TRLR\r\n"));
			zip.closeEntry();

			for (int i = 0; i < images.size(); i++) {
				entry = new ZipEntry(zipPath + "/" + images.get(i).getPath());
				zip.putNextEntry(entry);
				zip.write(images.get(i).imageData);
				zip.closeEntry();
			}

			zip.close();

			result.buffer = bos.toByteArray();

		} catch (final IOException e) {
			result.resu = e.getMessage();
			logger.log(Level.WARNING, "", e);

		} catch (final SQLException e) {
			result.resu = e.getMessage();
			logger.log(Level.WARNING, "", e);
		} catch (final SukuException e) {
			result.resu = e.getMessage();
			logger.log(Level.WARNING, "", e);
		}

		return result;
	}

	private void writeBom(ZipOutputStream zip) {
		final byte[] bom8 = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		final byte[] bom16 = { (byte) 0xFE, (byte) 0xFF };
		try {
			switch (thisSet) {
			case Set_Utf8:
				zip.write(bom8);
				return;
			case Set_Utf16:
				zip.write(bom16);
				return;
			}
			// TODO: Set_Ansel, Set_Ascii, Set_None ?
		} catch (final IOException e) {
			logger.warning("Wrining bom: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void writeIndi(ZipOutputStream zip, PersonLongData persLong) throws IOException, SQLException {
		final MinimumIndividual indi = units.get(persLong.getPid());
		final StringBuilder sb = new StringBuilder();
		sb.append("0 @I" + indi.gid + "@ INDI\r\n");
		sb.append("1 SEX " + indi.sex);
		sb.append("\r\n");
		if (persLong.getRefn() != null) {
			sb.append("1 REFN " + persLong.getRefn());
			sb.append("\r\n");
		}
		final UnitNotice[] notices = persLong.getNotices();
		for (final UnitNotice notice : notices) {
			if (notice.getTag().equals("NAME") && (surety >= notice.getSurety())) {
				final StringBuilder nm = new StringBuilder();
				if (notice.getGivenname() != null) {
					nm.append(notice.getGivenname());
				}
				if (notice.getPatronym() != null) {
					if (nm.length() > 0) {
						nm.append(" ");
					}
					nm.append(notice.getPatronym());
				}
				nm.append("/");
				if (notice.getPrefix() != null) {
					nm.append(notice.getPrefix());
					nm.append(" ");
				}
				if (notice.getSurname() != null) {
					nm.append(notice.getSurname());
				}
				nm.append("/");
				if (notice.getPostfix() != null) {
					nm.append(notice.getPostfix());
				}

				sb.append("1 NAME " + nm.toString());
				sb.append("\r\n");
				if (notice.getSource() != null) {
					sb.append(getNoteStructure(2, "SOUR", notice.getSource()));
					if (notice.getPrivateText() != null) {
						sb.append(getNoteStructure(3, "NOTE", notice.getPrivateText()));

					}
				} else {
					if (notice.getPrivateText() != null) {
						sb.append("2 SOUR \r\n");
						sb.append(getNoteStructure(3, "NOTE", notice.getPrivateText()));

					}
				}
				if (notice.getNoticeType() != null) {
					sb.append("2 TYPE " + notice.getNoticeType());
					sb.append("\r\n");
				}
				if (notice.getDescription() != null) {
					sb.append(getNoteStructure(2, "NOTE", notice.getDescription()));

				}
			}
		}

		for (int i = 0; i < notices.length; i++) {

			if (!notices[i].getTag().equals("NAME") && (surety >= notices[i].getSurety())) {
				final UnitNotice notice = notices[i];
				final StringBuilder nm = new StringBuilder();
				String gedTag = notice.getTag();
				if (Resurses.gedcomTags.indexOf(gedTag) < 0) {
					gedTag = "_" + gedTag;
				}
				if (notice.getTag().equals("NOTE")) {
					if (notice.getNoteText() != null) {
						nm.append(getNoteStructure(1, "NOTE", notice.getNoteText()));
					}
					if (notice.getSource() != null) {
						nm.append(getNoteStructure(2, "SOUR", notice.getSource()));

						if (notice.getPrivateText() != null) {
							nm.append(getNoteStructure(3, "NOTE", notice.getPrivateText()));

						}
					} else {
						if (notice.getPrivateText() != null) {
							nm.append("2 SOUR \r\n");
							nm.append(getNoteStructure(3, "NOTE", notice.getPrivateText()));
						}
					}
				} else {
					String caus = null;
					nm.append("1 " + gedTag);

					if (notice.getDescription() != null) {
						if (Resurses.gedcomAttributes.indexOf(gedTag) < 0) {
							caus = notice.getDescription();
							nm.append("\r\n");
						} else {
							nm.append(" " + notice.getDescription());
							nm.append("\r\n");
						}
					} else {
						nm.append("\r\n");
					}
					if (notice.getNoticeType() != null) {
						nm.append("2 TYPE " + notice.getNoticeType());
						nm.append("\r\n");
					}
					if (caus != null) {
						nm.append("2 CAUS " + caus);
						nm.append("\r\n");
					}
					if (notice.getFromDate() != null) {
						nm.append("2 DATE ");
						nm.append(toFullDate(notice.getDatePrefix(), notice.getFromDate(), notice.getToDate()));
						nm.append("\r\n");
					}
					if ((notice.getCroft() != null) || (notice.getFarm() != null) || (notice.getVillage() != null)
							|| (notice.getPlace() != null)) {
						nm.append("2 PLAC ");
						if (notice.getCroft() != null) {
							nm.append(notice.getCroft());
						}
						nm.append(", ");
						if (notice.getFarm() != null) {
							nm.append(notice.getFarm());
						}
						nm.append(", ");
						if (notice.getVillage() != null) {
							nm.append(notice.getVillage());
						}
						nm.append(", ");
						if (notice.getPlace() != null) {
							nm.append(notice.getPlace());
						}
						nm.append("\r\n");
					}
					if (notice.getNoteText() != null) {
						nm.append(getNoteStructure(2, "NOTE", notice.getNoteText()));
					}

					if ((notice.getAddress() != null) || (notice.getPostOffice() != null)) {
						if (notice.getAddress() != null) {
							if (notice.getState() == null) {

								nm.append(getNoteStructure(2, "ADDR", notice.getAddress(), 1));
								if (notice.getPostOffice() != null) {
									if ((notice.getPostalCode() != null) && (notice.getPostOffice() != null)) {
										nm.append("3 CITY " + notice.getPostOffice());
										nm.append("\r\n");
										nm.append("3 POST " + notice.getPostalCode());
										nm.append("\r\n");
									} else {
										nm.append("3 CITY " + notice.getPostOffice());
										nm.append("\r\n");
									}

								}
							} else {
								nm.append(getNoteStructure(2, "ADDR", notice.getAddress(), 1));
								if (notice.getPostOffice() != null) {
									nm.append("3 CITY " + notice.getPostOffice());
									nm.append("\r\n");
								}
								if (notice.getPostalCode() != null) {
									nm.append("3 STAE " + notice.getState());
									nm.append("\r\n");
									nm.append("3 POST " + notice.getPostalCode());
									nm.append("\r\n");
								} else {
									nm.append("3 STAE " + notice.getState());
									nm.append("\r\n");
								}

							}

						}
						if (notice.getCountry() != null) {
							nm.append("3 CTRY " + notice.getCountry());
							nm.append("\r\n");
						}
					} else if ((notice.getCountry() != null) || (notice.getState() != null)) {
						if (notice.getState() != null) {
							nm.append("2 STAE " + notice.getState());
							nm.append("\r\n");
							if (notice.getCountry() != null) {
								nm.append("3 CTRY " + notice.getCountry());
								nm.append("\r\n");
							}
						} else {
							nm.append("2 CTRY " + notice.getCountry());
							nm.append("\r\n");
						}
					}
					if (notice.getEmail() != null) {
						nm.append("2 EMAIL " + notice.getEmail());
						nm.append("\r\n");
					}
					if (notice.getSource() != null) {
						nm.append(getNoteStructure(2, "SOUR", notice.getSource()));
						if (notice.getPrivateText() != null) {
							nm.append(getNoteStructure(3, "NOTE", notice.getPrivateText()));
						}
						if (notice.getSurety() < 100) {
							nm.append("3 QUAY " + suretyToQuay(notice.getSurety()));
							nm.append("\r\n");
						}
					} else if (notice.getSurety() < 100) {
						nm.append("2 SOUR\r\n");
						if (notice.getPrivateText() != null) {
							nm.append(getNoteStructure(3, "NOTE", notice.getPrivateText()));
						}
						nm.append("3 QUAY " + suretyToQuay(notice.getSurety()));
						nm.append("\r\n");
					}
					if (includeImages) {
						if ((notice.getMediaFilename() != null) && (notice.getMediaData() != null)) {
							final MinimumImage minimg = new MinimumImage(notice.getMediaFilename(),
									notice.getMediaData());
							nm.append("1 OBJE\r\n");
							nm.append("2 FILE " + minimg.getPath());
							nm.append("\r\n");

							if (notice.getMediaFilename().toLowerCase().endsWith(".jpg")) {
								nm.append("3 FORM jpeg\r\n");
							}
							if (notice.getMediaTitle() != null) {
								nm.append("2 TITL " + notice.getMediaTitle());
								nm.append("\r\n");
							}

							images.add(minimg);
						}
					}
				}

				sb.append(nm.toString());

			}
		}
		if (persLong.getSource() != null) {
			sb.append(getNoteStructure(1, "SOUR", persLong.getSource()));
			if (persLong.getPrivateText() != null) {
				sb.append(getNoteStructure(2, "NOTE", persLong.getPrivateText()));
			}
		} else {
			if (persLong.getPrivateText() != null) {
				sb.append("1 SOUR \r\n");
				sb.append(getNoteStructure(2, "NOTE", persLong.getPrivateText()));
			}
		}

		final Integer ado = childRids.get(persLong.getPid());
		if (ado != null) {
			sb.append(addAdoptionEvents(persLong.getPid()));
		}

		for (int i = 0; i < indi.fams.size(); i++) {
			sb.append("1 FAMS @F" + indi.fams.get(i) + "@\r\n");

		}
		for (int i = 0; i < indi.famc.size(); i++) {
			sb.append("1 FAMC @F" + indi.famc.get(i) + "@\r\n");

		}
		zip.write(gedBytes(sb.toString()));
	}

	private String toFullDate(String prefix, String fromdate, String todate) {
		final StringBuilder nm = new StringBuilder();
		if (prefix != null) {
			nm.append(prefix + " ");
		}
		if (fromdate != null) {
			nm.append(gedDate(fromdate));
		}
		if ((prefix != null) && (todate != null)) {
			if (prefix.equals("BET")) {
				nm.append(" AND ");
				nm.append(gedDate(todate));
			} else if (prefix.equals("FROM")) {
				nm.append(" TO ");
				nm.append(gedDate(todate));
			}
		}
		return nm.toString();

	}

	private String addAdoptionEvents(int pid) throws SQLException {
		final StringBuilder sb = new StringBuilder();
		final ArrayList<AdoptionElement> adops = new ArrayList<AdoptionElement>();
		final String sql = "select p.pid as rpid,n.rid,n.surety,n.tag,n.relationtype,n.description,"
				+ "n.dateprefix,n.fromdate,n.todate,n.place,n.notetext,n.sourcetext " + "from relationnotice as n "
				+ "inner join relation as r on r.rid=n.rid and r.tag in ('MOTH','FATH') "
				+ "inner join relation as p on r.rid=p.rid and p.tag ='CHIL' " + "where r.pid=?";

		final PreparedStatement pst = con.prepareStatement(sql);
		pst.setInt(1, pid);
		final ResultSet rs = pst.executeQuery();
		final ArrayList<RelationNotice> relNotices = new ArrayList<RelationNotice>();

		while (rs.next()) {
			final RelationNotice rnote = new RelationNotice(rs.getInt("rpid"), rs.getInt("rid"), rs.getInt("surety"),
					rs.getString("tag"), rs.getString("relationtype"), rs.getString("description"),
					rs.getString("dateprefix"), rs.getString("fromdate"), rs.getString("todate"), rs.getString("place"),
					rs.getString("notetext"), rs.getString("sourcetext"), null, null, null, null, null);
			relNotices.add(rnote);
		}
		rs.close();
		pst.close();
		final MinimumIndividual indi = units.get(pid);

		final Integer[] asChild = indi.famc.toArray(new Integer[0]);

		int dadFam = 0;
		int momFam = 0;
		final RelationNotice minimot = new RelationNotice("");
		RelationNotice notice = null;
		for (int i = 0; i < relNotices.size(); i++) {
			notice = relNotices.get(i);
			if (notice.getRnid() != 0) {
				for (final Integer element : asChild) {
					if (element != 0) {
						final MinimumFamily mfam = famById.get(element);
						if ((mfam.dad == notice.getRnid()) || (mfam.mom == notice.getRnid())) {
							if (mfam.dad == notice.getRnid()) {
								dadFam = mfam.id;
								relNotices.set(i, minimot);
							} else {
								momFam = mfam.id;
								relNotices.set(i, minimot);
							}
						}
					}
				}
			}

			if ((dadFam != 0) || (momFam != 0)) {
				String who = null;
				String fam = null;
				String other = null;
				final int childFam = (dadFam != 0) ? dadFam : momFam;

				if (notice.getTag().equals("ADOP")) {
					final StringBuilder adb = new StringBuilder();

					if (notice.getType() != null) {
						adb.append("2 TYPE " + notice.getType());
						adb.append("\r\n");

					}
					if (notice.getFromDate() != null) {
						adb.append("2 DATE "
								+ toFullDate(notice.getDatePrefix(), notice.getFromDate(), notice.getToDate()));
						adb.append("\r\n");
					}
					if (notice.getPlace() != null) {
						adb.append("2 PLAC " + notice.getPlace());
						adb.append("\r\n");
					}
					if (notice.getDescription() != null) {
						adb.append("2 CAUS " + notice.getDescription());
						adb.append("\r\n");
					}
					if (notice.getNoteText() != null) {
						adb.append(getNoteStructure(2, "NOTE", notice.getNoteText()));
						adb.append("\r\n");
					}
					if (notice.getSource() != null) {
						adb.append(getNoteStructure(2, "SOUR", notice.getSource()));
						if (notice.getSurety() < 100) {

							adb.append("3 QUAY " + suretyToQuay(notice.getSurety()));
							adb.append("\r\n");
						}
					} else if (notice.getSurety() < 100) {
						adb.append("2 SOUR\r\n");
						adb.append("3 QUAY " + suretyToQuay(notice.getSurety()));
						adb.append("\r\n");
					}
					if (adb.length() > 0) {
						other = adb.toString();
					}
				}

				fam = "@F" + childFam + "@";
				if ((dadFam == 0) || (momFam == 0)) {

					if (dadFam != 0) {
						who = "FATH";

					} else {
						who = "MOTH";

					}

				}
				final AdoptionElement adop = new AdoptionElement(who, fam, other);
				adops.add(adop);
				dadFam = 0;
				momFam = 0;

			}
		}

		for (int i = 0; i < adops.size(); i++) {
			final AdoptionElement adop = adops.get(i);
			if (adop.who != null) {

				for (int j = i + 1; j < adops.size(); j++) {
					final AdoptionElement adop2 = adops.get(j);
					if (adop2.who != null) {
						if (adop2.fam.equals(adop.fam) && (Utils.nv(adop2.other).equals(Utils.nv(adop.other))
								|| ((adop.other == null) || (adop2.other == null)))) {
							adop.who = "BOTH";
							if (adop.other == null) {
								adop.other = adop2.other;
							}
							adop2.who = null;
						}
					}
				}

				sb.append("1 ADOP\r\n");
				if (adop.other != null) {
					sb.append(adop.other);
				}
				sb.append("2 FAMC " + adop.fam);
				sb.append("\r\n");
				sb.append("3 ADOP " + adop.who);
				sb.append("\r\n");
			}

		}

		return sb.toString();

	}

	private int suretyToQuay(int surety) {
		if (surety >= 100) {
			return 100;
		}
		if ((surety < 100) && (surety >= 80)) {
			return 2;
		}
		if ((surety < 80) && (surety >= 60)) {
			return 1;
		}
		return 0;

	}

	private Object gedDate(String dbDate) {
		final String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
		String mon = "";
		if (dbDate.length() >= 6) {

			try {
				final int m = Integer.parseInt(dbDate.substring(4, 6));
				if ((m > 0) && (m <= 12)) {
					mon = months[m - 1] + " ";
				}
			} catch (final NumberFormatException ne) {
				// NumberFormatException ignored
			}
		}
		if (dbDate.length() == 8) {
			return dbDate.substring(6) + " " + mon + dbDate.substring(0, 4);
		}
		return mon + dbDate.substring(0, 4);

	}

	private String getNoteStructure(int level, String tag, String text) {
		return getNoteStructure(level, tag, text, 2);
	}

	private String getNoteStructure(int level, String tag, String text, int emptyMax) {
		final ArrayList<String> ss = new ArrayList<String>();

		final int linelen = 73;

		if (text == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		char prevc = 0;
		int emptyCount = 0;
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			switch (c) {
			case '\r':
				break;
			case '\n':
				emptyCount++;
				sb.append(" ");
				break;
			default:
				if (emptyCount >= emptyMax) {
					if (sb.length() > 0) {
						ss.add(sb.toString());
						sb = new StringBuilder();
					}
				} else if (emptyCount == 1) {
					if (prevc != ' ') {
						sb.append(" ");
					}
				}
				emptyCount = 0;
				sb.append(c);

			}
			prevc = c;
		}
		ss.add(sb.toString());
		sb = new StringBuilder();
		String currTag = tag;
		int currLevel = level;

		for (int i = 0; i < ss.size(); i++) {
			String chap = ss.get(i);
			if (i > 0) {
				currTag = "CONT";
				currLevel = level + 1;
			}
			while (chap.length() > 0) {
				if (chap.length() <= linelen) {
					sb.append(currLevel);
					sb.append(" ");
					sb.append(currTag);
					sb.append(" ");
					sb.append(chap);
					sb.append("\r\n");
					chap = "";
				} else {

					int last = chap.substring(0, linelen).lastIndexOf(" ");
					if (chap.substring(linelen - 1, linelen).equals(" ")) {
						last = linelen;
						last--;
					} else {
						if (last < (linelen / 2)) {
							last = linelen;
						}
					}

					sb.append(currLevel);
					sb.append(" ");
					sb.append(currTag);
					sb.append(" ");
					sb.append(chap.substring(0, last + 1));
					sb.append("\r\n");

					chap = chap.substring(last + 1);
					currLevel = level + 1;
					currTag = "CONC";
				}
			}
		}

		return sb.toString();

	}

	private void writeFam(ZipOutputStream zip, MinimumFamily fam, String langCode) throws IOException, SQLException {

		final StringBuilder sb = new StringBuilder();
		sb.append("0 @F" + fam.id + "@ FAM\r\n");
		if (fam.dad > 0) {
			sb.append("1 HUSB @I" + fam.getDada() + "@\r\n");
		}
		if (fam.mom > 0) {
			sb.append("1 WIFE @I" + fam.getMama() + "@\r\n");
		}
		for (int i = 0; i < fam.chils.size(); i++) {
			sb.append("1 CHIL @I" + fam.getChild(i) + "@\r\n");
		}

		PreparedStatement pst;
		String sql;
		if (langCode != null) {
			sql = "select surety,tag," + "coalesce(l.description,r.description) as description,"
					+ "coalesce(l.relationtype,r.relationtype) as relationtype,"
					+ "dateprefix,fromdate,todate,coalesce(l.place,r.place) as place,"
					+ "coalesce(l.notetext,r.notetext) as notetext,sourcetext,privatetext "
					+ "from relationnotice as r left join relationlanguage as l "
					+ "on r.rnid = l.rnid and l.langcode = ? where r.rid=? " + "order by noticerow ";

			pst = con.prepareStatement(sql);
			pst.setString(1, langCode);
			pst.setInt(2, fam.rid);

		} else {
			sql = "select surety,tag,description,relationtype,"
					+ "dateprefix,fromdate,todate,place,notetext,sourcetext,privatetext "
					+ "from relationnotice where rid=? " + "order by noticerow ";
			pst = con.prepareStatement(sql);
			pst.setInt(1, fam.rid);
		}

		final ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			final int surety = rs.getInt(1);
			final String tag = rs.getString(2);
			final String desc = rs.getString(3);
			final String type = rs.getString(4);
			final String pre = rs.getString(5);
			final String fromdate = rs.getString(6);
			final String todate = rs.getString(7);
			final String place = rs.getString(8);
			final String notetext = rs.getString(9);
			final String sourcetext = rs.getString(10);
			final String privatetext = rs.getString(11);

			sb.append("1 " + tag);
			sb.append("\r\n");
			if (type != null) {
				sb.append("2 TYPE " + type);
				sb.append("\r\n");
			}
			if (fromdate != null) {
				sb.append("2 DATE ");
				sb.append(toFullDate(pre, fromdate, todate));
				sb.append("\r\n");

			}
			if (desc != null) {
				sb.append("2 CAUS " + desc);
				sb.append("\r\n");
			}
			if (place != null) {
				sb.append("2 PLAC " + place);
				sb.append("\r\n");
			}
			if (notetext != null) {
				sb.append(getNoteStructure(2, "NOTE", notetext));
			}
			if (sourcetext != null) {
				sb.append(getNoteStructure(2, "SOUR", sourcetext));
				if (privatetext != null) {
					sb.append(getNoteStructure(3, "NOTE", privatetext));
				}
				if (surety < 100) {
					sb.append("3 QUAY " + suretyToQuay(surety));
					sb.append("\r\n");
				}
			} else if (surety < 100) {
				sb.append("2 SOUR\r\n");
				sb.append("3 QUAY " + suretyToQuay(surety));
				sb.append("\r\n");
			} else if (privatetext != null) {
				sb.append("2 SOUR\r\n");
				sb.append(getNoteStructure(3, "NOTE", privatetext));
				sb.append("3 QUAY " + suretyToQuay(surety));
				sb.append("\r\n");
			}

		}
		rs.close();
		pst.close();

		zip.write(gedBytes(sb.toString()));

	}

	private void writeHead(ZipOutputStream zip) throws IOException {

		final StringBuilder sb = new StringBuilder();
		sb.append("0 HEAD\r\n");
		sb.append("1 SOUR FinFamily\r\n");
		sb.append("2 VERS " + AntVersion.antVersion + "\r\n");
		sb.append("2 NAME FinFamily\r\n");
		sb.append("3 ADDR https://sourceforge.net/projects/finfamily/\r\n");
		sb.append("1 SUBM @U1@\r\n");
		sb.append("1 GEDC\r\n");
		sb.append("2 VERS 5.5.1\r\n");
		sb.append("2 FORM LINEAGE-LINKED\r\n");

		switch (thisSet) {
		case Set_Ascii:
			sb.append("1 CHAR ASCII\r\n");
			break;
		case Set_Ansel:
			sb.append("1 CHAR ANSEL\r\n");
			break;
		case Set_Utf8:
			sb.append("1 CHAR UTF-8\r\n");
			break;
		case Set_Utf16:
			sb.append("1 CHAR UNICODE\r\n");
			break;
		default:
			sb.append("1 CHAR ANSI\r\n");
		}

		sb.append("0 @U1@ SUBM\r\n");

		final String sql = "select * from sukuvariables";
		Statement stm;
		try {
			stm = con.createStatement();
			final ResultSet rs = stm.executeQuery(sql);
			if (rs.next()) {
				sb.append("1 NAME " + rs.getString("owner_name"));
				sb.append("\r\n");

				final StringBuilder sbad = new StringBuilder();
				String tmp = rs.getString("owner_address");
				if (tmp != null) {
					sbad.append(tmp);
					sbad.append("\r\n");
				}
				tmp = rs.getString("owner_postalcode");
				final String aux = rs.getString("owner_postoffice");
				if (tmp != null) {
					if (aux != null) {
						sbad.append(tmp + " " + aux);
						sbad.append("\r\n");
					} else {
						sbad.append(tmp);
						sbad.append("\r\n");
					}

				} else if (aux != null) {
					sbad.append(aux);
					sbad.append("\r\n");
				}

				tmp = rs.getString("owner_state");
				if (tmp != null) {
					sbad.append(tmp);
					sbad.append("\r\n");
				}
				tmp = rs.getString("owner_country");
				if (tmp != null) {
					sbad.append(tmp);
					sbad.append("\r\n");
				}
				sb.append(getNoteStructure(1, "ADDR", sbad.toString(), 1));

				tmp = rs.getString("owner_email");
				if (tmp != null) {
					sb.append("1 EMAIL " + tmp);
					sb.append("\r\n");
				}

				tmp = rs.getString("owner_webaddress");
				if (tmp != null) {
					sb.append("1 WWW " + tmp);
					sb.append("\r\n");
				}
				tmp = rs.getString("owner_info");
				if (tmp != null) {
					sb.append(getNoteStructure(1, "NOTE", tmp, 1));

				}
			} else {
				sb.append("1 NAME No user\r\n");

			}
			rs.close();
			stm.close();
		} catch (final SQLException e) {
			sb.append("1 NAME " + e.getMessage());
			sb.append("\r\n");
		}

		// zip.write(gedBytes("0 HEAD\r\n"));
		//
		// zip
		// .write(gedBytes("1 NOTE FinFamily Gedcom Export is under
		// construction\r\n"));
		zip.write(gedBytes(sb.toString()));
	}

	private void collectIndividuals() throws SQLException {

		String sql = null;
		PreparedStatement pst;

		if (viewId == 0) {
			sql = "select pid,sex from unit order by pid";
			pst = con.prepareStatement(sql);
		} else {
			sql = "select pid,sex from unit where pid in (select pid from viewunits where vid = ?) order by pid";
			pst = con.prepareStatement(sql);
			pst.setInt(1, viewId);
		}

		final ResultSet rs = pst.executeQuery();
		int gid = 0;
		while (rs.next()) {

			gid++;
			final int pid = rs.getInt(1);
			final String sex = rs.getString(2);
			units.put(pid, new MinimumIndividual(pid, sex, gid));

		}
		rs.close();
		pst.close();

	}

	private void collectFamilies() throws SQLException {

		StringBuilder sql = new StringBuilder();
		PreparedStatement pst;

		sql.append("select a.pid,a.tag,a.relationrow,b.pid,b.tag,b.relationrow,a.rid "
				+ "from relation as a inner join relation as b on a.rid=b.rid ");
		if (viewId > 0) {
			sql.append("and a.pid in (select pid from viewunits where vid=" + viewId + ") "
					+ "and b.pid in (select pid from viewunits where vid=" + viewId + ") ");
		}
		if (surety != 100) {
			sql.append("and a.surety >= " + surety + " ");
		}
		sql.append("and a.tag='WIFE' and b.tag='HUSB' " + "order by a.pid,a.relationrow");

		pst = con.prepareStatement(sql.toString());

		ResultSet rs = pst.executeQuery();

		while (rs.next()) {
			final int dada = rs.getInt(1);
			final int mama = rs.getInt(4);
			final int rid = rs.getInt(7);
			final ParentPair pp = new ParentPair(dada, mama);

			final MinimumFamily mf = new MinimumFamily(dada, mama, rid);
			families.put(pp.toString(), mf);
			famById.put(mf.id, mf);
			MinimumIndividual mi = units.get(dada);
			mi.addFams(mf.id);
			mi = units.get(mama);
			mi.addFams(mf.id);
		}
		rs.close();
		pst.close();

		sql = new StringBuilder();

		sql.append("select a.pid,b.pid,b.tag,a.rid from ");
		sql.append("relation as a inner join relation as b on a.rid=b.rid ");
		sql.append("and a.tag='CHIL' and b.tag != 'CHIL' ");
		if (viewId > 0) {
			sql.append("and a.pid in (select pid from viewunits where vid=" + viewId + ") "
					+ "and b.pid in (select pid from viewunits where vid=" + viewId + ") ");
		}
		if (surety != 100) {
			sql.append("and a.surety >= " + surety + " ");
		}

		sql.append("order by b.pid,a.relationrow ");

		pst = con.prepareStatement(sql.toString());
		Vector<MinimumIndividual> p = new Vector<MinimumIndividual>();
		int previd = 0;
		int rid = 0;
		rs = pst.executeQuery();
		while (rs.next()) {
			final int pare = rs.getInt(1);
			final int chil = rs.getInt(2);

			rid = rs.getInt(4);
			if (chil != previd) {
				//
				// now let's find correct family
				//
				addChildToFamilies(p, previd, rid);
				p = new Vector<MinimumIndividual>();

			}
			final MinimumIndividual pi = units.get(pare);
			p.add(pi);
			previd = chil;

		}
		rs.close();
		pst.close();
		if (previd > 0) { // add last child too
			addChildToFamilies(p, previd, rid);
		}

	}

	private void addChildToFamilies(Vector<MinimumIndividual> p, int childId, int chilrid) {

		MinimumFamily fm;
		final MinimumIndividual mini = new MinimumIndividual(0, "U", 0);

		final MinimumIndividual child = units.get(childId);
		if (child == null) {
			return;
		}
		child.addChildRid(chilrid);

		//
		// first try to find family with mama and papa for child
		//
		for (int i = 0; i < (p.size() - 1); i++) {
			for (int j = i + 1; j < p.size(); j++) {
				MinimumIndividual pi = p.get(i);
				final MinimumIndividual pj = p.get(j);

				final int dada = pi.pid;
				final int mama = pj.pid;
				if ((mama > 0) && (dada > 0)) {
					ParentPair pp = new ParentPair(dada, mama);
					fm = families.get(pp.toString());
					if (fm == null) {
						pp = new ParentPair(mama, dada);
						fm = families.get(pp.toString());
					}
					if (fm != null) {

						fm.addChil(childId);
						pi = units.get(childId);
						pi.addFamc(fm.id);
						p.set(i, mini);
						p.set(j, mini);
					}
				}
			}
		}
		if (childId > 0) { // Still let's find single parents
			for (int i = 0; i < p.size(); i++) {
				MinimumIndividual pi = p.get(i);
				if (pi.pid > 0) {
					ParentPair pp;
					if (pi.sex.equals("M")) {
						pp = new ParentPair(pi.pid, 0);
					} else {
						pp = new ParentPair(0, pi.pid);
					}
					fm = families.get(pp.toString());
					if (fm != null) {
						fm.addChil(childId);
					} else {
						if (pi.sex.equals("M")) {
							fm = new MinimumFamily(pi.pid, 0, 0);
						} else {
							fm = new MinimumFamily(0, pi.pid, 0);
						}
						families.put(pp.toString(), fm);
						famById.put(fm.id, fm);
						fm.addChil(childId);
						pi = units.get(childId);
						pi.addFamc(fm.id);
						p.set(i, mini);
					}
				}
			}
		}
	}

	private byte[] gedBytes(String text) {
		if (text == null) {
			return null;
		}
		try {
			switch (thisSet) {
			case Set_Ascii:
				return text.getBytes("US_ASCII");
			case Set_None:
				return text.getBytes("ISO-8859-1");
			case Set_Utf8:
				return text.getBytes("UTF-8");
			case Set_Utf16:
				return text.getBytes("UTF-16");
			case Set_Ansel:
				return toAnsel(text);
			}

		} catch (final UnsupportedEncodingException e) {
			logger.warning("Writing " + thisSet.name() + ": " + e.getMessage());
			e.printStackTrace();
		}

		return text.getBytes();
	}

	private byte[] toAnsel(String text) {

		final char toAnsel[] = {

				225, 'A', 226, 'A', 227, 'A', 228, 'A', 232, 'A', 234, 'A', 165, 0, 240, 'C', 225, 'E', 226, 'E', 227,
				'E', 232, 'E', 225, 'I', 226, 'I', 227, 'I', 232, 'I', 163, 0, 228, 'N', 225, 'O', 226, 'O', 227, 'O',
				228, 'O', 232, 'O', 0, 0, 162, 0, 225, 'U', 226, 'U', 227, 'U', 232, 'U', 226, 'Y', 164, 0, 207, 0,

				225, 'a', 226, 'a', 227, 'a', 228, 'a', 232, 'a', 234, 'a', 182, 0, 240, 'c', 225, 'e', 226, 'e', 227,
				'e', 232, 'e', 225, 'i', 226, 'i', 227, 'i', 232, 'i', 186, 0, 228, 'n', 225, 'o', 226, 'o', 227, 'o',
				228, 'o', 232, 'o', 0, 0, 178, 0, 225, 'u', 226, 'u', 227, 'u', 232, 'u', 226, 'y', 180, 0, 232, 'y' };

		final StringBuilder st = new StringBuilder();

		final int iInLen = text.length();
		int iNow = 0;

		int iIndex;
		// TCHAR uCurr,u0,u1;
		char uCurr, u0, u1;
		// LPTSTR ps = sTemp.GetBuffer(iInLen*2);
		while (iNow < iInLen) {
			// uCurr = sIn.GetAt(iNow);
			uCurr = text.charAt(iNow);
			iNow++;
			if ((uCurr & 0x80) == 0) {
				st.append(uCurr);
				// ps[iNew++]=uCurr;
			} else {
				if ((uCurr & 0xc0) != 0xc0) {
					switch (uCurr) {

					case 0x8c:
						st.append((char) 166);
						break;
					case 0x9c:
						st.append((char) 182);
						break;
					case 0xa1:
						st.append((char) 198);
						break;
					case 0xa3:
						st.append((char) 185);
						break;
					case 0xa9:
						st.append((char) 195);
						break;
					case 0xbf:
						st.append((char) 207);
						break;
					default:
						st.append('?');
						break;
					}
				} else {
					iIndex = uCurr - 0xc0;
					u0 = toAnsel[iIndex * 2];
					u1 = toAnsel[(iIndex * 2) + 1];
					if (u0 == 0) {
						st.append('?');
					} else {
						st.append(u0);
					}
					if (u1 != 0) {
						st.append(u1);
					}
				}
			}
		}

		try {
			return st.toString().getBytes("ISO-8859-1");
		} catch (final UnsupportedEncodingException e) {
			logger.warning("Writing ansel: " + e.getMessage());
			e.printStackTrace();
		}
		return text.getBytes();

	}

	private class MinimumIndividual {
		int pid = 0; // person id
		int gid = 0; // gedcom id
		String sex = null;
		Vector<Integer> fams = new Vector<Integer>();
		Vector<Integer> famc = new Vector<Integer>();
		Vector<Integer> chilrids = new Vector<Integer>();

		MinimumIndividual(int pid, String sex, int gid) {

			this.pid = pid;
			this.gid = gid;
			this.sex = sex;

		}

		void addChildRid(int rid) {
			chilrids.add(rid);
		}

		void addFams(int id) {
			this.fams.add(id);

		}

		void addFamc(int id) {
			this.famc.add(id);

		}
	}

	private class ParentPair {
		int dada = 0;
		int mama = 0;

		ParentPair(int dad, int mom) {

			this.dada = dad;
			this.mama = mom;

		}

		@Override
		public String toString() {

			return "" + dada + "_" + mama;

		}

	}

	private static int nextFamilyId = 0;

	private class MinimumFamily {
		int dad = 0;
		int mom = 0;
		int rid = 0;
		int id = 0;
		Vector<Integer> chils = new Vector<Integer>();

		MinimumFamily(int dad, int mom, int rid) {
			this.dad = dad;
			this.mom = mom;
			this.rid = rid;
			id = ++nextFamilyId;

		}

		int getDada() {
			if (dad == 0) {
				return 0;
			}
			final MinimumIndividual mm = units.get(dad);
			if (mm == null) {
				logger.warning("person for " + dad + "does not exist");
			}
			// FIXME: NPE
			return mm.gid;
		}

		int getMama() {
			if (mom == 0) {
				return 0;
			}
			final MinimumIndividual mm = units.get(mom);
			if (mm == null) {
				logger.warning("person for " + mom + "does not exist");
			}
			// FIXME: NPE
			return mm.gid;
		}

		void addChil(int chi) {
			chils.add(chi);

		}

		int getChild(int idx) {
			final int cid = chils.get(idx);
			final MinimumIndividual mm = units.get(cid);
			if (mm == null) {
				logger.warning("child for " + cid + "does not exist");
			}
			// FIXME: NPE
			return mm.gid;
		}
	}

	/**
	 * The Class MinimumImage.
	 */
	class MinimumImage {

		/** The img name. */
		String imgName = null;

		/** The counter. */
		int counter = 0;

		/** The image data. */
		byte[] imageData = null;

		/**
		 * Instantiates a new minimum image.
		 *
		 * @param name
		 *            the name
		 * @param data
		 *            the data
		 */
		MinimumImage(String name, byte[] data) {

			this.imgName = name;
			this.imageData = data;
			this.counter = ++imageCounter;
		}

		/**
		 * Gets the path.
		 *
		 * @return the path
		 */
		String getPath() {
			final StringBuilder sb = new StringBuilder();
			sb.append(dbName + "_files/" + counter + "_" + imgName);
			return sb.toString();
		}

	}

	/**
	 * The Class AdoptionElement.
	 */
	class AdoptionElement {

		/** The who. */
		String who = null;

		/** The fam. */
		String fam = null;

		/** The other. */
		String other = null;

		/**
		 * Instantiates a new adoption element.
		 *
		 * @param who
		 *            the who
		 * @param fam
		 *            the fam
		 * @param other
		 *            the other
		 */
		AdoptionElement(String who, String fam, String other) {
			this.who = who;
			this.fam = fam;
			this.other = other;
		}

	}

	private void setRunnerValue(String juttu) throws SukuException {
		if (runner != null) {
			if (this.runner.setRunnerValue(juttu)) {
				throw new SukuException(Resurses.getString("EXECUTION_CANCELLED"));
			}
		}
	}

}
