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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Executor of operating system commands.
 *
 * @author Kalle
 */
public class CommandExecuter {

	private static Logger logger = Logger.getLogger(CommandExecuter.class.getName());

	/**
	 * execute the command.
	 *
	 * @param cmd
	 *            each command part is in seperate string
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws SukuException
	 *             the suku exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void executeTheCommnad(String[] cmd) throws InterruptedException, SukuException, IOException {

		Process process = null;
		final long execStarted = System.currentTimeMillis();

		// Execute the native command
		process = Runtime.getRuntime().exec(cmd);

		final StringBuffer stdoutBuffer = new StringBuffer();
		final StringBuffer stderrBuffer = new StringBuffer();
		streamToBuffer(process.getInputStream(), stdoutBuffer);
		streamToBuffer(process.getErrorStream(), stderrBuffer);

		// Wait for process to complete
		process.waitFor();

		// Get the exit value
		final int exitValue = process.exitValue();

		// logger.debug("ArchiveFileStorageService :: Execution exit value=" +
		// exitValue);
		// logger.debug("ArchiveFileStorageService :: Execution result=" +
		// stdoutBuffer.toString());
		// logger.debug("ArchiveFileStorageService :: Execution error=" +
		// stderrBuffer.toString());

		// Check the exit value. It needs to be 0
		if (exitValue != 0) {
			throw new SukuException(stderrBuffer.toString());
		}

		final long execEnded = System.currentTimeMillis();
		@SuppressWarnings("unused")
		long execTimeInS = 0;

		try {
			execTimeInS = (execEnded - execStarted) / 1000L;
		} catch (final Exception ignorE) {
			//
		}

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cmd.length; i++) {
			if (i > 0) {
				sb.append(" ");
			}
			sb.append(cmd[i]);
		}

		logger.info("Command '" + sb.toString() + "'  executed in " + (execEnded - execStarted) + " ms.");

	}

	private static void streamToBuffer(final InputStream input, final StringBuffer buffer) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(input));
					int i = -1;
					while ((i = reader.read()) != -1) {
						buffer.append((char) i);
					}
				} catch (final IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
