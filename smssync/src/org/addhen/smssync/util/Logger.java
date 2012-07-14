/*****************************************************************************
 ** Copyright (c) 2010 - 2012 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 **
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.
 **
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 **
 *****************************************************************************/
package org.addhen.smssync.util;

import android.util.Log;


public class Logger {
	
	public static final boolean LOGGING_MODE = true;
	
	public Logger() {
		
	}
	
	public void log(String message) {
		if (LOGGING_MODE)
			Log.i(getClass().getName(), message);
	}

	public void log(String format, Object... args) {
		if (LOGGING_MODE)
			Log.i(getClass().getName(), String.format(format, args));
	}

	public void log(String message, Exception ex) {
		if (LOGGING_MODE)
			Log.e(getClass().getName(), message, ex);
	}
}

