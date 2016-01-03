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

package fi.kaila.suku.kontroller;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * <h1>Controller interface</h1>
 *
 *
 * <p>
 * The UI interacts with the data using this interface There will be a separate
 * implementation for local and the webstart version
 * </p>
 * .
 *
 * @author Kalle
 */
public interface SukuKontroller {

	/**
	 * Connect to database. Database connection
	 *
	 * @param host
	 *            the host
	 * @param dbname
	 *            the dbname
	 * @param userid
	 *            the userid
	 * @param passwd
	 *            the passwd
	 * @return the connection
	 * @throws SukuException
	 *             If connection fails this is thrown with reason for failuye
	 */
	public void getConnection(String host, String dbname, String userid, String passwd, boolean isH2)
			throws SukuException;

	/**
	 * Reset database connection.
	 */
	public void resetConnection();

	/**
	 * Reset database type.
	 */
	public void setDBType(boolean dbtype);

	/**
	 * Gets the suku data.
	 *
	 * @param params
	 *            variable # of request parameters
	 * @return SukuData object containing result
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getSukuData(String... params) throws SukuException;

	/**
	 * <ul>
	 * <li>params request parameter data as a SukuData object</li>
	 * <li>param params variable # of request parameters</li>
	 * <li>return SukuData object containing result</li>
	 * <li>throws SukuException</li>
	 * </ul>
	 * .
	 *
	 * @param request
	 *            the request
	 * @param params
	 *            the params
	 * @return the response as a SukuData "container"
	 * @throws SukuException
	 *             the suku exception
	 */
	public SukuData getSukuData(SukuData request, String... params) throws SukuException;

	/**
	 * <h1>File management</h1>
	 *
	 * Open of a file. This is used to enable server side to access the file.
	 * WebStart version sends file to server. Local version just opens it.
	 *
	 * @param filter
	 *            the filter
	 * @return true if opened file selected
	 */
	public boolean openFile(String filter);

	/**
	 * <h1>Local file management</h1>.
	 *
	 * @return length of opened file
	 */
	public long getFileLength();

	/**
	 * <h1>Local file management</h1>.
	 *
	 * @return opened local file as an input stream
	 */
	public InputStream getInputStream();

	/**
	 * <h1>Local file management</h1>.
	 *
	 * @return filename of opened file
	 */
	public String getFileName();

	/**
	 * this returns null for webstart.
	 *
	 * used mainly to open the report when it has been created
	 *
	 * @return filepath of opened file
	 */
	public String getFilePath();

	/**
	 * <h1>Local parameter management</h1>
	 *
	 * get stored parameter from user preferences.
	 *
	 * @param o
	 *            (owner name)
	 * @param key
	 *            the key
	 * @param def
	 *            the def
	 * @return value
	 */
	public String getPref(Object o, String key, String def);

	/**
	 * <h1>Local parameter management</h1>
	 *
	 * store value in user preferences.
	 *
	 * @param o
	 *            the o
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putPref(Object o, String key, String value);

	/**
	 * <h1>Local file management</h1>
	 *
	 * Create a local file. To make webstart possible all local data management
	 * is made diffrently in the local Kontroller and the webstart kontrollr
	 *
	 * @param filter
	 *            the filter
	 * @return true if file created
	 */
	public boolean createLocalFile(String filter);

	/**
	 * Method used in webstart version to save the buffer to a file on local
	 * disk.
	 *
	 * @param filter
	 *            the filter
	 * @param in
	 *            the in
	 * @return true, if successful
	 */
	public boolean saveFile(String filter, InputStream in);

	/**
	 * <h1>Local file management</h1>.
	 *
	 * @return created local file as an output stream
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public OutputStream getOutputStream() throws FileNotFoundException;

	/**
	 * Opening a named local file for GUI side.
	 *
	 * @param filter
	 *            for opefile dialog
	 *
	 * @return InputStream to the file
	 */
	public InputStream openLocalFile(String filter);

	/**
	 * Checks if is remote.
	 *
	 * @return true if remote mode
	 */
	public boolean isRemote();

	/**
	 * Checks if is web start.
	 *
	 * @return true if web start mode (running in sandbox)
	 */
	public boolean isWebStart();

	/**
	 * Checks if is connected.
	 *
	 * @return true if connected to db (PostgreSQL)
	 */
	public boolean isConnected();

	/**
	 * Checks if is connected.
	 *
	 * @return true if connected to db (PostgreSQL)
	 */
	public boolean isH2();

	/**
	 * Gets the schema.
	 *
	 * @return true if connected to valid family database
	 */
	public String getSchema();

	/**
	 * sets the valid family database schema.
	 *
	 * @param schema
	 *            the new schema
	 */
	public void setSchema(String schema);

}
