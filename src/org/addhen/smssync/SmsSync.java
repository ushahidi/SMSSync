/** 
 * Copyright (c) 2010 Addhen
 * All rights reserved
 * Contact: henry@addhen.org
 * Website: http://www.addhen.org/blog
 * 
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.	
 *	
 *
 * 
 **/

package org.addhen.smssync;

import android.content.Context;
import android.content.SharedPreferences;


public class SmsSync {
	public static String website = "";
	public static String keyword = "";
	public static String apiKey = "";
	public static Boolean enabled = false;
	public static final String PREF_NAME = "SMS_SYNC_PREF";
	
	public static void loadPreferences( Context context ) {
		final SharedPreferences settings = context.getSharedPreferences(
				PREF_NAME, 0);
		
		website = settings.getString("WebsitePref", "");
		keyword = settings.getString("Keyword", "");
		apiKey = settings.getString("ApiKey", "");
		enabled = settings.getBoolean("EnableSmsSync", false);
		
	}
}