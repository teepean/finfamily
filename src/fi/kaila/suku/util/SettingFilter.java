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

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * The Class SettingFilter.
 *
 * @author FIKAAKAIL
 *
 *         JSuku setting filter for selecting files
 */
public class SettingFilter extends FileFilter {

	private String[] ftype = null;

	private final boolean showDirectories = true;

	private String filetype = null;

	/**
	 * constructor.
	 *
	 * @param filetype
	 *            or list of accepted file types separated by semicolon (;)
	 */
	public SettingFilter(String filetype) {
		this.filetype = filetype;
		if (filetype != null) {
			this.ftype = filetype.split(";");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File arg) {
		int i;

		String tmp;
		if (arg != null) {
			if (arg.isDirectory()) {
				return this.showDirectories;
			}
			if (this.ftype == null) {
				return true;
			}
			tmp = arg.getName().toLowerCase();

			for (i = 0; i < this.ftype.length; i++) {
				if (tmp.endsWith("." + this.ftype[i])) {
					return true;
				}

			}

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		if (filetype == null) {
			return "FinFamily files";
		}
		return filetype;

	}

}
