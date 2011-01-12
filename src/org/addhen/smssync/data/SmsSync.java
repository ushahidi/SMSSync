/** 
 ** Copyright (c) 2010 Ushahidi Inc
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
 **/

package org.addhen.smssync.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class SmsSync {
	
	// smssync outbox table
	public static final class SmssyncMsgs implements BaseColumns {
		
		public static final Uri CONTENT_URI
        = Uri.parse("content://org.addhen.smssync.data.SmsSync/smssync_msgs");
		// column names
		public static final String MESSAGE_BODY = "message_body";
		public static final String MESSAGE_FROM = "message_from";
		public static final String IS_MESSAGE_SENT = "is_sent";
		public static final String CREATED_DATE = "created";
		public static final String SMSSYNC_MSG = "smssyn_msg";
		public static final String DEFAULT_SORT_ORDER = "created DESC";
	}
}
