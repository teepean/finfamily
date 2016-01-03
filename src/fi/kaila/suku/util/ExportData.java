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

import fi.kaila.suku.util.pojo.PersonLongData;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.Relation;

/**
 *
 * Auxiliary class used by ....
 *
 * @author Markus Ritala
 *
 */
public class ExportData extends PersonShortData {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private PersonLongData person = null;
	private Relation[] relations = null;

	/**
	 * Instantiates a new export data.
	 *
	 * @param person
	 *            the person
	 * @param relations
	 *            the relations
	 */
	public ExportData(PersonLongData person, Relation[] relations) {
		super(person);
		this.person = person;
		this.relations = relations;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fi.kaila.suku.util.pojo.PersonShortData#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("Pid=" + getPid());
		sb.append(" ");
		sb.append(getAlfaName());
		if (relations != null) {
			sb.append("# of relations ");
			sb.append(relations.length);
		}
		return sb.toString();
	}

}
