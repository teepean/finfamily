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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User verification utilities.
 *
 * @author FIKAAKAIL
 */
public class UserVerifier {

	/**
	 * Verifies the user passwd.
	 *
	 * @param passwd
	 *            the passwd
	 * @param mdPasswd
	 *            the md passwd
	 * @return true if passwd matched mdPasswd
	 * @throws SukuException
	 *             the suku exception
	 */
	public boolean verifyPassword(String passwd, String mdPasswd) throws SukuException {

		final String encrypted = encryptPassword(passwd);
		if (encrypted.equals(mdPasswd)) {
			return true;
		}
		return false;

	}

	/**
	 * Encrypt password using md5 algorithm.
	 *
	 * @param passwd
	 *            the passwd
	 * @return encrypted passwd
	 * @throws SukuException
	 *             the suku exception
	 */
	public String encryptPassword(String passwd) throws SukuException {
		final byte[] tunnus = passwd.getBytes();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			throw new SukuException(e);

		}
		md.update(tunnus);
		final byte digest[] = md.digest();

		final StringBuilder pw = new StringBuilder();

		for (final byte b : digest) {
			final String a = "00" + Integer.toHexString(b & 0xff);

			pw.append(a.substring(a.length() - 2));
		}
		return pw.toString();
	}

}
