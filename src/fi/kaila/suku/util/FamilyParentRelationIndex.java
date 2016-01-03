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

import fi.kaila.suku.util.pojo.RelationShortData;

/**
 * TODO This will most probably be replaced with something more sophisticated.
 *
 * @author FIKAAKAIL
 */
public class FamilyParentRelationIndex {
	private int childIdx = 0;
	private int parentIdx = 0;
	private RelationShortData rela = null;

	/**
	 * Instantiates a new family parent relation index.
	 *
	 * @param childIdx
	 *            the child idx
	 * @param parentIdx
	 *            the parent idx
	 * @param rela
	 *            the rela
	 */
	public FamilyParentRelationIndex(int childIdx, int parentIdx, RelationShortData rela) {
		this.childIdx = childIdx;
		this.parentIdx = parentIdx;
		this.rela = rela;
	}

	/**
	 * Gets the child idx.
	 *
	 * @return child index
	 */
	public int getChildIdx() {
		return childIdx;
	}

	/**
	 * Gets the parent idx.
	 *
	 * @return parent index
	 */
	public int getParentIdx() {
		return parentIdx;
	}

	/**
	 * Gets the surety.
	 *
	 * @return the surety
	 */
	public int getSurety() {
		if (rela != null) {
			return rela.getSurety();
		}
		return 0;
	}

}
