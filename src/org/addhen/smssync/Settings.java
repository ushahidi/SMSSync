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

package org.addhen.smssync;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;


public class Settings extends PreferenceActivity implements 
	OnSharedPreferenceChangeListener {
	
	public static final String KEY_WEBSITE_PREF = "website_preference";
	public static final String KEY_KEYWORD_PREF = "keyword_preference";
	
	public static final String KEY_ENABLE_SMS_SYNC_PREF = 
		"enable_sms_sync_preference";
	
	public static final String KEY_ENABLE_MMS_SYNC_PREF = 
		"enable_mms_sync_preference";
	
	public static final String KEY_ENABLE_GPS_SYNC_PREF = 
		"enable_gps_sync_preference";
	
	public static final String KEY_API_KEY_PREF = "api_key_preference";
	public static final String KEY_POWERED_PREFERENCE = "powered_preference";
	
	public static final String KEY_AUTO_DELETE_MESSAGE = 
		"auto_delete_preference";
	
	public static final String KEY_ENABLE_REPLY = "enable_reply_preference";
	public static final String KEY_REPLY = "reply_preference";
	public static final String PREFS_NAME = "SMS_SYNC_PREF";
	public static final String HTTP_TEXT = "http://";
	public static final String AUTO_SYNC = "auto_sync_preference";
	public static final String AUTO_SYNC_TIMES = "auto_sync_times";
	public static final String TASK_CHECK = "task_check_preference";
	public static final String TASK_CHECK_TIMES = "task_check_times";
	
	
	private EditTextPreference websitePref;
	private EditTextPreference apiKeyPref;
	private EditTextPreference keywordPref;
	private EditTextPreference replyPref;
	
	private CheckBoxPreference enableSmsSync;
	private CheckBoxPreference enableAutoDelete;
	private CheckBoxPreference enableReply;
	private CheckBoxPreference autoSync;
	private CheckBoxPreference taskCheck;
	
	private ListPreference autoSyncTimes;
	private ListPreference taskCheckTimes;
	
	private SharedPreferences settings ;
	private SharedPreferences.Editor editor;
	private static final String URL = "http://smssync.ushahidi.com";
	
	private CharSequence[] autoSyncEntries = {"5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "60 Minutes"}; 
    private CharSequence[] autoSyncValues = {"0","5","10","15","30","60"};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        websitePref = (EditTextPreference)
        	getPreferenceScreen().findPreference(KEY_WEBSITE_PREF);
        
        apiKeyPref = (EditTextPreference)
        	getPreferenceScreen().findPreference(KEY_API_KEY_PREF);
        
        keywordPref = (EditTextPreference)
        	getPreferenceScreen().findPreference(KEY_KEYWORD_PREF);
        
        enableSmsSync = (CheckBoxPreference)
        	getPreferenceScreen().findPreference(KEY_ENABLE_SMS_SYNC_PREF);
        
        enableAutoDelete = (CheckBoxPreference)
        	getPreferenceScreen().findPreference(KEY_AUTO_DELETE_MESSAGE);
        
        enableReply = (CheckBoxPreference)getPreferenceScreen().findPreference(
        		KEY_ENABLE_REPLY);
        autoSync = (CheckBoxPreference)getPreferenceScreen().findPreference(AUTO_SYNC);
        
        taskCheck =(CheckBoxPreference)getPreferenceScreen().findPreference(TASK_CHECK);
        
        replyPref = (EditTextPreference)getPreferenceScreen().findPreference(
        		KEY_REPLY);
        
        autoSyncTimes = (ListPreference)getPreferenceScreen().findPreference(AUTO_SYNC_TIMES);
        autoSyncTimes.setEntries(autoSyncEntries);
        autoSyncTimes.setEntryValues(autoSyncValues);
        
        taskCheckTimes = (ListPreference)getPreferenceScreen().findPreference(TASK_CHECK_TIMES);
        taskCheckTimes.setEntries(autoSyncEntries);
        taskCheckTimes.setEntryValues(autoSyncValues);
        
        Preference poweredPreference = findPreference(KEY_POWERED_PREFERENCE);
        poweredPreference.setOnPreferenceClickListener(
        		new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		final Intent i = new Intent(android.content.Intent.ACTION_VIEW, 
        				Uri.parse(URL));
  		    	startActivity(i);
  		    	return true;
        	}
        });
        
        this.savePreferences();
    }
	
	protected void savePreferences() {
		
		int autoTime = 0;
		int taskCheckTime = 0;
		
		settings = getSharedPreferences(PREFS_NAME, 0);
	
		if (websitePref.getText().equals("")) {
			websitePref.setText(HTTP_TEXT);
		}
		
		if (replyPref.getText().equals("")) {
			replyPref.setText(getString(R.string.edittxt_reply_default));
		}
		
		if ( enableReply.isChecked()) {
			replyPref.setEnabled(true);
		} else {
			replyPref.setEnabled(false);
		}
		
		if (autoSync.isChecked()) {
			autoSyncTimes.setEnabled(true);
		} else {
			autoSyncTimes.setEnabled(false);
		}
		
		if (taskCheck.isChecked()) {
			taskCheckTimes.setEnabled(true);
		} else {
			taskCheckTimes.setEnabled(false);
		}
		
		//"5 Minutes", "10 Minutes", "15 Minutes", "30", "60 Minutes" 
		if(autoSyncTimes.getValue().matches("5")){
			taskCheckTime = 5;
		} else if(autoSyncTimes.getValue().matches("10")){
			taskCheckTime = 10;
		} else if(autoSyncTimes.getValue().matches("15")){
			taskCheckTime = 15;
		} else if(autoSyncTimes.getValue().matches("30")){
			taskCheckTime = 30;
		} else if(autoSyncTimes.getValue().matches("60")){
			taskCheckTime = 60;
		}
		
		editor = settings.edit();
		editor.putString("WebsitePref", websitePref.getText());
		editor.putString("ApiKey", apiKeyPref.getText());
		editor.putString("Keyword", keywordPref.getText());
		editor.putString("ReplyPref", replyPref.getText());
		editor.putBoolean("EnableSmsSync", enableSmsSync.isChecked());
		editor.putBoolean("EnableAutoDelete", enableAutoDelete.isChecked());
		editor.putBoolean("EnableReply", enableReply.isChecked());
		editor.putBoolean("AutoSync",autoSync.isChecked());
		editor.putInt("AutoTime",autoTime);
		editor.putInt("taskCheck", taskCheckTime);
		editor.commit();
	}
	
	@Override
	protected void onResume() {
		 super.onResume();
		 
		 // Set up a listener whenever a key changes
		 getPreferenceScreen().getSharedPreferences().
		 	registerOnSharedPreferenceChangeListener(this);
		 
	}
	
	@Override
	protected void onPause() {
		 super.onPause();
	        // Unregister the listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences().
	        	unregisterOnSharedPreferenceChangeListener(this);
	        
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		PackageManager pm = getPackageManager();
	    ComponentName cn = new ComponentName(Settings.this, SmsReceiver.class);
	    
		if(key.equals(KEY_ENABLE_SMS_SYNC_PREF)){
			
			if (sharedPreferences.getBoolean(KEY_ENABLE_SMS_SYNC_PREF,false))
			{
				pm.setComponentEnabledSetting(cn,
			          PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
			          PackageManager.DONT_KILL_APP);
			
				//show notification
				Util.showNotification(this);
			
			} else {
				pm.setComponentEnabledSetting(cn,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
				
				Util.clearNotify(this);
			}
		}
		
		if (key.equals(KEY_ENABLE_REPLY)) {
			
			if (sharedPreferences.getBoolean(KEY_ENABLE_REPLY,false)) {
				replyPref.setEnabled(true);
			} else {
				replyPref.setEnabled(false);
			}
		}
		
		// Auto sync enable
		if (key.equals(AUTO_SYNC)) {
			
			if (sharedPreferences.getBoolean(AUTO_SYNC,false)) {
				autoSyncTimes.setEnabled(true);
				startService( new Intent( Settings.this,SmsSyncAutoSyncService.class));
			} else {
				stopService( new Intent(Settings.this, SmsSyncAutoSyncService.class));
				autoSyncTimes.setEnabled(false);
			}
		}
		
		// Enable task checking
		if (key.equals(TASK_CHECK)) {
			
			if (sharedPreferences.getBoolean(TASK_CHECK,false)) {
				taskCheckTimes.setEnabled(true);
				startService( new Intent( Settings.this,SmsSyncTaskCheckService.class));
			} else {
				stopService( new Intent(Settings.this, SmsSyncTaskCheckService.class));
				taskCheckTimes.setEnabled(false);
			}
		}
		
		if (key.equals(KEY_WEBSITE_PREF)) {
			
			if (!Util.validateCallbackUrl(
					sharedPreferences.getString(KEY_WEBSITE_PREF, ""))) {
				Util.showToast(Settings.this, R.string.invalid_url);
				websitePref.setText("");
				SmsSyncPref.website = "";
			}
		}
		
		this.savePreferences();
	}

}
