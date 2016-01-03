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

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * The Class SukuNameComparator.
 *
 * @author FIKAAKAIL
 *
 *         Comparator for names
 */
@SuppressWarnings("rawtypes")
public class SukuNameComparator implements Comparator {

	/** The colli. */
	Collator colli;

	/**
	 * Constructor with locale.
	 *
	 * @param langu
	 *            the langu
	 */
	public SukuNameComparator(String langu) {

		final Locale ll = new Locale(langu);
		this.colli = Collator.getInstance(ll);

		adels = Resurses.getString("NAME_VON").split(";");
	}

	private String adels[] = null;

	private String noAdel(String nime) {
		int ll;
		for (final String adel : adels) {
			ll = adel.length();
			if (nime.length() > ll) {
				if (adel.equalsIgnoreCase(nime.substring(0, ll))) {
					return nime.substring(ll + 1);
				}
			}
		}
		return nime;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object arg0, Object arg1) {
		final String uno = (String) arg0;
		final String duo = (String) arg1;
		if ((uno == null) || (duo == null)) {
			return 0;
		}

		final String nuno = noAdel(uno.trim()).replace(' ', '!');
		final String nduo = noAdel(duo.trim()).replace(' ', '!');

		return this.colli.compare(nuno, nduo);

	}

}
