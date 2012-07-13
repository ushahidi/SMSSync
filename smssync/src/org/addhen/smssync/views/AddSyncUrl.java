package org.addhen.smssync.views;

import java.util.ArrayList;

import org.addhen.smssync.R;
import org.addhen.smssync.models.SyncUrlModel;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class AddSyncUrl {

	public EditText title;

	public EditText keywords;

	public EditText url;

	public EditText secret;

	/**
	 * Handles views for the add dialog box
	 * 
	 * @param dialogViews
	 */
	public AddSyncUrl(final android.view.View dialogViews) {
		title = (EditText) dialogViews.findViewById(R.id.sync_url_title);
		keywords = (EditText) dialogViews.findViewById(R.id.sync_url_keyword);
		secret = (EditText) dialogViews.findViewById(R.id.sync_url_secret);
		url = (EditText) dialogViews.findViewById(R.id.sync_url);
		url.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (TextUtils.isEmpty(url.getText().toString())) {
					url.setText(dialogViews.getContext().getString(R.string.http_text));
				}

				return false;
			}

		});

	}

	/**
	 * Add Sync URL to database
	 * 
	 * @return boolean
	 */
	public boolean addSyncUrl() {
		SyncUrlModel syncUrl = new SyncUrlModel();
		syncUrl.setKeywords(keywords.getText().toString());
		syncUrl.setSecret(secret.getText().toString());
		syncUrl.setTitle(title.getText().toString());
		syncUrl.setUrl(url.getText().toString());
		syncUrl.listSyncUrl = new ArrayList<SyncUrlModel>();
		syncUrl.listSyncUrl.add(syncUrl);
		return syncUrl.save();
	}

	/**
	 * Add Sync URL to database
	 * 
	 * @return boolean
	 */
	public boolean updateSyncUrl(int id) {
		SyncUrlModel syncUrl = new SyncUrlModel();
		syncUrl.setId(id);
		syncUrl.setKeywords(keywords.getText().toString());
		syncUrl.setSecret(secret.getText().toString());
		syncUrl.setTitle(title.getText().toString());
		syncUrl.setUrl(url.getText().toString());
		syncUrl.listSyncUrl = new ArrayList<SyncUrlModel>();
		syncUrl.listSyncUrl.add(syncUrl);
		return syncUrl.update(syncUrl);
	}
}
