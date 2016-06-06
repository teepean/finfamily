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

/**
 * Used by timeline report.
 *
 * @author halonmi
 */
public class TimelineData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String timelineData = null;
	private String timelineDate = null;
	private int counter = 0;

	/**
	 * Instantiates a timeline data.
	 *
	 * @param tdata
	 *            the timeline data
	 * @param tdate
	 *            the timeline date
	 */
	public TimelineData(String tdata, String tdate) {
		this.timelineData = tdata;
		this.timelineDate = tdate;
		this.counter = 1;

	}

	/**
	 * add one to number of occurrences.
	 */
	public void increment() {
		this.counter++;
	}

	/**
	 * Gets the name.
	 *
	 * @return timeline data
	 */
	public String getTimelineData() {
		return this.timelineData;
	}

	/**
	 * Gets the timeline date.
	 *
	 * @return timeline date
	 */
	public String getTimelineDate() {
		return this.timelineDate;
	}

	/**
	 * Gets the count.
	 *
	 * @return count of lines
	 */
	public int getCount() {
		return this.counter;
	}
}
