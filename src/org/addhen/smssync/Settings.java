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


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;


public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	public static final String KEY_WEBSITE_PREF = "website_preference";
	public static final String KEY_KEYWORD_PREF = "keyword_preference";
	public static final String KEY_ENABLE_SMS_SYNC_PREF = "enable_sms_sync_preference";
	public static final String KEY_ENABLE_MMS_SYNC_PREF = "enable_mms_sync_preference";
	public static final String KEY_ENABLE_GPS_SYNC_PREF = "enable_gps_sync_preference";
	public static final String KEY_API_KEY_PREF = "api_key_preference";
	public static final String KEY_POWERED_PREFERENCE = "powered_preference";
	public static final String KEY_AUTO_DELETE_MESSAGE = "auto_delete_preference";
	public static final String KEY_ENABLE_REPLY = "enable_reply_preference";
	public static final String KEY_REPLY = "reply_preference";
	
	public static final String PREFS_NAME = "SMS_SYNC_PREF";
	public static final String HTTP_TEXT = "http://";
	
	private EditTextPreference websitePref;
	private EditTextPreference apiKeyPref;
	private EditTextPreference keywordPref;
	private EditTextPreference replyPref;
	
	private CheckBoxPreference enableSmsSync;
	private CheckBoxPreference enableAutoDelete;
	private CheckBoxPreference enableReply;
	
	private SharedPreferences settings ;
	private SharedPreferences.Editor editor;
	private static final String URL = "http://smssync.ushahidi.com";
	private static final int NOTIFY_RUNNING = 100;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        websitePref = (EditTextPreference)getPreferenceScreen().findPreference(
        		KEY_WEBSITE_PREF);
        
        apiKeyPref = (EditTextPreference)getPreferenceScreen().findPreference(
        		KEY_API_KEY_PREF);
        
        keywordPref = (EditTextPreference)getPreferenceScreen().findPreference(
        		KEY_KEYWORD_PREF);
        
        enableSmsSync = (CheckBoxPreference)getPreferenceScreen().findPreference(
        		KEY_ENABLE_SMS_SYNC_PREF);
        
        enableAutoDelete = (CheckBoxPreference)getPreferenceScreen().findPreference(
        		KEY_AUTO_DELETE_MESSAGE);
        
        enableReply = (CheckBoxPreference)getPreferenceScreen().findPreference(
        		KEY_ENABLE_REPLY);
        
        replyPref = (EditTextPreference)getPreferenceScreen().findPreference(
        		KEY_REPLY);
        
        Preference poweredPreference = findPreference(KEY_POWERED_PREFERENCE);
        poweredPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	public boolean onPreferenceClick(Preference preference) {
        		final Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(URL));
  		    	startActivity(i);
  		    	return true;
        	}
        });
        
        this.savePreferences();
    }
	
	protected void savePreferences() {
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
		editor = settings.edit();
		editor.putString("WebsitePref", websitePref.getText());
		editor.putString("ApiKey", apiKeyPref.getText());
		editor.putString("Keyword", keywordPref.getText());
		editor.putString("ReplyPref", replyPref.getText());
		editor.putBoolean("EnableSmsSync", enableSmsSync.isChecked());
		editor.putBoolean("EnableAutoDelete", enableAutoDelete.isChecked());
		editor.putBoolean("EnableReply", enableReply.isChecked());
		editor.commit();
	}
	
	@Override
	protected void onResume() {
		 super.onResume();
		 
		 // Set up a listener whenever a key changes
		 getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		 
	}
	
	@Override
	protected void onPause() {
		 super.onPause();
	        // Unregister the listener whenever a key changes
	        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	        
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		NotificationManager notificationManager =
		    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		PackageManager pm = getPackageManager();
	    ComponentName cn = new ComponentName(Settings.this, SmsReceiver.class);
	    
		if(key.equals(KEY_ENABLE_SMS_SYNC_PREF)){
			
			if (sharedPreferences.getBoolean(KEY_ENABLE_SMS_SYNC_PREF,false))
			{
				pm.setComponentEnabledSetting(cn,
			          PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
			          PackageManager.DONT_KILL_APP);
			
				Intent baseIntent = new Intent(this, SmsSyncOutbox.class);
				baseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
				Notification notification = new Notification(
						R.drawable.icon, getString(R.string.status), 
						System.currentTimeMillis());
				notification.flags |= Notification.FLAG_ONGOING_EVENT
						| Notification.FLAG_NO_CLEAR;
				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, baseIntent, 0);
				notification.setLatestEventInfo(this, getString(R.string.app_name),getString(R.string.notification_summary), pendingIntent);
				notificationManager.notify(NOTIFY_RUNNING, notification);
			
			} else {
				pm.setComponentEnabledSetting(cn,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
				notificationManager.cancel(NOTIFY_RUNNING);
			}
		}
		
		if (key.equals(KEY_ENABLE_REPLY)) {
			
			if (sharedPreferences.getBoolean(KEY_ENABLE_REPLY,false)) {
				replyPref.setEnabled(true);
			} else {
				replyPref.setEnabled(false);
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
