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

import java.util.ResourceBundle;

import org.junit.Test;

import fi.kaila.suku.kontroller.SukuKontrollerLocalImpl;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;
import junit.framework.TestCase;

/**
 * tests for import from 2004.
 *
 * @author fikaakail
 */
public class Import2004Test extends TestCase {

	private String userid = null;
	private String password = null;
	private String dbname = null;
	private String host = null;
	private String filename = null;
	private final boolean isH2 = false;

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() {
		final ResourceBundle resources = ResourceBundle.getBundle("properties/junittests");

		this.userid = resources.getString("fi.kaila.suku.junit.Import2004Test.userid");
		this.password = resources.getString("fi.kaila.suku.junit.Import2004Test.password");
		this.host = resources.getString("fi.kaila.suku.junit.Import2004Test.host");
		this.dbname = resources.getString("fi.kaila.suku.junit.Import2004Test.dbname");
		this.filename = resources.getString("fi.kaila.suku.junit.Import2004Test.filename");

	}

	/**
	 * test import of suku 2004 backup.
	 *
	 * @throws SukuException
	 *             the suku exception
	 */
	@Test
	public void testImportTesti() throws SukuException {

		// SukuServer server = new SukuServerImpl();
		final SukuKontrollerLocalImpl kontroller = new SukuKontrollerLocalImpl(null);

		kontroller.getConnection(this.host, this.dbname, this.userid, this.password, this.isH2);
		// kontroller.setLocalFile(this.filename);

		kontroller.getSukuData("cmd=import", "type=backup", "lang=FI");
		// server.import2004Data("db/" + this.filename, "FI");

		final SukuData data = kontroller.getSukuData("cmd=family", "pid=3");
		assertNotNull("Family must not be null");

		final PersonShortData owner = data.pers[0];

		assertNotNull("Owner of family must not be null");

		assertTrue("Wrong ownere", owner.getGivenname().startsWith("Kaarle"));
		kontroller.resetConnection();

	}

}
