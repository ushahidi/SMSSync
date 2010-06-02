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

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SmsSync extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}