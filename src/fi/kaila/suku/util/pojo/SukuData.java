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

package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * Class used as container to transport pojo objects between client and server
 * using the kontroller. Fields are used directly and are very specific to the
 * call used.
 *
 *
 *
 * @author FIKAAKAIL
 *
 */
public class SukuData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The main cmd parameter value. */
	public String cmd = null;

	/**
	 * result string. This should be null if request was successfull. In case of
	 * error it should contain the error message
	 *
	 */
	public String resu = null;
	/**
	 * array of pids fromn relative fect e.g.
	 */
	public int[] pidArray = null;

	/** Transfer of general binary object. */
	public byte[] buffer = null;
	/**
	 * e.g. settings result
	 */
	public String[] generalArray = null;

	/** Text value returned. */
	public String generalText = null;
	/**
	 * array vector storage (Types.xls page types)
	 */
	public Vector<String[]> vvTypes = null;

	/** array vector storage (Conversions texts). */
	public Vector<String[]> vvTexts = null;
	/**
	 * array of persons. Subject in [0]
	 */
	public PersonShortData[] pers = null;

	/** Array of relations. */
	public RelationShortData[] rels = null;

	/** Array of place locations (used by map). */
	public PlaceLocationData[] places = null;

	/** Single full person data. */
	public PersonLongData persLong = null;

	/** array of long persons. */
	public PersonLongData[] persons = null;

	/** array of relations. */
	public Relation[] relations = null;

	/** Count can be used to return count to caller. */
	public int resuCount = 0;

	/** map of reportUnits. */
	public HashMap<Integer, ReportUnit> reportUnits = null;

	/** vector of reportunits. */
	public Vector<ReportUnit> tables = null;

	/** answer as a pid. */
	public int resultPid = 0;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("cmd=" + cmd);
		sb.append(";");
		sb.append("resu=" + resu);
		sb.append(";");
		sb.append("long=" + persLong);
		if (generalText != null) {
			sb.append(";text=" + generalText);
		}
		if (generalArray != null) {
			sb.append(";listsz=" + generalArray.length);
		}
		if (resultPid > 0) {
			sb.append(";pid=" + resultPid);
		}
		if (pers != null) {
			sb.append(";shortcount=" + pers.length);
		}
		return sb.toString();
	}
}
