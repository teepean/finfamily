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

package fi.kaila.suku.junit;

import java.util.MissingResourceException;

import org.junit.Test;

import fi.kaila.suku.util.Resurses;
import junit.framework.TestCase;

/**
 * Test of resourceBundle and other properties.
 *
 * @author fikaakail
 */
public class PropertiesTest extends TestCase {

	/**
	 * test that FILE exists.
	 */
	@Test
	public void testFilePropertyFin() {
		Resurses.setLocale("fi");
		final String file = Resurses.getString("FILE");
		assertNotNull("FILE resource not found", file);
		assertEquals("FILE väärä arvo", file, "Tiedosto");

	}

	/**
	 * test that FILE = Tiedosto in fi.
	 */
	@Test
	public void testFilePropertyEn() {
		Resurses.setLocale("en");
		final String file = Resurses.getString("FILE");
		assertNotNull("FILE resource not found", file);
		assertEquals("FILE väärä arvo", file, "File");

	}

	/**
	 * test that FILE = File in en.
	 */
	@Test
	public void testNoexistatntProperty() {
		try {
			Resurses.getString("FILExxxxyyy");
		} catch (final MissingResourceException e) {
			return;
		}
		fail("accepted non existant property");
	}

}
