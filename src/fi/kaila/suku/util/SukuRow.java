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

import java.util.logging.Logger;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.dialog.SearchCriteria;
import fi.kaila.suku.util.pojo.PersonShortData;

//import java.awt.image.BufferedImage;

/**
 *
 * One row of database list. Database window consists of rows containing SukuRow
 * objects
 *
 * @author FIKAAKAIL
 *
 */
public class SukuRow {

	// PersonShortData person;
	/** The pid. */
	int pid;

	/** The suku. */
	Suku suku = null;

	//
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SukuRow.class.getName());

	/**
	 * Constuctor of a Sukurow.
	 *
	 * @param suku
	 *            the suku
	 * @param model
	 *            the model
	 * @param person
	 *            the person
	 */
	public SukuRow(Suku suku, SukuModel model, PersonShortData person) {
		// this.person = person;
		this.suku = suku;
		this.pid = person.getPid();
	}

	/**
	 * Instantiates a new suku row.
	 *
	 * @param suku
	 *            the suku
	 */
	SukuRow(Suku suku) {
		this.suku = suku;
	}

	/**
	 * Set value into column.
	 *
	 * @param idx
	 *            the idx
	 * @param value
	 *            the value
	 */
	public void set(int idx, Object value) {

		// switch (idx) {
		// case 0: this.tsex = (String)value;
		// break;
		// case 1: this.name = (String)value;
		// break;
		// case 2: this.patro = (String)value;
		// break;
		// case 3: this.birthDate = (String)value;
		// break;
		// case 4: this.deatDate = (String)value;
		// break;
		// case 5: this.idInfo = (String)value;
		// break;
		//
		//
		//
		// }
		//

		// this.taulu[idx]=(String)value;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(" (");
		final PersonShortData person = suku.getPerson(pid);
		if (person.getBirtDate() != null) {
			sb.append(person.getBirtDate());
		}
		if (person.getDeatDate() != null) {
			sb.append(" - ");
			sb.append(person.getDeatDate());
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Get item from column.
	 *
	 * @param idx
	 *            the idx
	 * @return content on column
	 */
	public Object get(int idx) {
		StringBuilder sb;

		// int idx = suku.convertToView(vidx);

		// int fullIdx;
		SearchCriteria crit;
		try {
			crit = SearchCriteria.getCriteria(null);
		} catch (final SukuException e) {
			// This should never come here
			e.printStackTrace();
			return null;
		}
		final PersonShortData person = suku.getPerson(pid);
		// fullIdx = this.model.getFullIndex(idx);
		// fullIdx = idx;
		// System.out.println("get(" + idx+") : fullIdx = " + fullIdx);

		if (person.getSex() == null) {
			System.out.println("nulli");
		}

		if (idx == crit.getColIndex(Resurses.COLUMN_T_SEX)) {
			// case SukuModel.TSEX_COL: //return this.sex ;
			if (person.getSex().equals("M")) {
				return SukuModel.manIcon;
			} else if (person.getSex().equals("F")) {
				return SukuModel.womanIcon;
			}
			return SukuModel.unknownIcon;
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_NAME)) {

			// case SukuModel.TNAME_COL:
			sb = new StringBuilder();
			final String alfaName = person.getAlfaName(false);
			if (alfaName != null) {
				sb.append(alfaName);
				sb.append(" ");
			}

			if (crit.isPropertySet(Resurses.COLUMN_T_ALL_NAMES)) {
				final String moreNames = person.getMorenames();
				if (moreNames != null) {
					sb.append(";");
					sb.append(moreNames);
				}
			}

			return sb.toString();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_ISMARR)) {

			if (person.getMarrCount() == 0) {
				return "";
			}
			return "" + person.getMarrCount();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_ISCHILD)) {
			if (person.getChildCount() == 0) {
				return "";
			}
			return "" + person.getChildCount();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_ISPARE)) {
			if (person.getPareCount() == 0) {
				return "";
			}
			return "" + person.getPareCount();
			// } else if (fullIdx ==
			// crit.getColIndex(Resurses.COLUMN_T_GIVENNAME)){
			//
			// return this.givenName;
			// } else if (fullIdx ==
			// crit.getColIndex(Resurses.COLUMN_T_SURNAME)){
			//
			// return this.surName;
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_UNKN)) {

			if (!person.getUnkn()) {
				return "";
			}
			return "*"; // this.unkn;
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_PATRONYME)) {

			return person.getPatronym();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_BIRT)) {

			return Utils.textDate(person.getBirtDate(), false);
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_BIRTPLACE)) {

			return person.getBirtPlace();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_BIRTVILLAGE)) {

			return person.getBirtVillage();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_BIRTFARM)) {

			return person.getBirtFarm();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_BIRTCROFT)) {

			return person.getBirtCroft();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_DEAT)) {

			return Utils.textDate(person.getDeatDate(), false);
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_DEATPLACE)) {

			return person.getDeatPlace();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_DEATVILLAGE)) {

			return person.getDeatVillage();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_DEATFARM)) {

			return person.getDeatFarm();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_DEATCROFT)) {

			return person.getDeatCroft();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_OCCUPATION)) {

			return person.getOccupation();

		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_GROUP)) {

			return person.getGroup();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_REFN)) {

			return person.getRefn();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_PID)) {

			return Integer.valueOf(person.getPid());
		} else {

			// case 1:
			//
			// if (this.prefix == null) {
			// return this.surName ;
			// }
			//
			// return this.prefix + " "+ this.surName ;
			// case 2: return this.givenName;
			// case 3: return this.patro;
			// case 4: return this.birthDate ;
			//
			// case 5: return this.deatDate;
			//
			// case 6: return this.idInfo;

			return null;

		}

	}

	/**
	 * Gets the person.
	 *
	 * @return person in row
	 */
	public PersonShortData getPerson() {
		return suku.getPerson(pid);
	}

	/**
	 * Gets the name.
	 *
	 * @return person name
	 */
	public String getName() {
		return getPerson().getTextName();
	}

	/**
	 * Gets the todo.
	 *
	 * @return the todo text
	 */
	public String getUnkn() {
		return getPerson().getUnkn() ? "*" : "";
	}

	/**
	 * Gets the refn.
	 *
	 * @return person pid
	 */
	public String getRefn() {
		return getPerson().getRefn();
	}

	/**
	 * Gets the group.
	 *
	 * @return group of personm
	 */
	public String getGroup() {
		return getPerson().getGroup();
	}

	/**
	 * Gets the pid.
	 *
	 * @return persons pid
	 */
	public int getPid() {
		return pid;
	}

}
