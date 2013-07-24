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

package org.addhen.smssync.adapters;

import org.addhen.smssync.Prefs;
import org.addhen.smssync.R;
import org.addhen.smssync.models.SyncUrlModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

public class SyncUrlAdapter extends BaseListAdapter<SyncUrlModel> {

	public class Widgets extends org.addhen.smssync.views.View implements
			View.OnClickListener {

		TextView title;

		TextView keywords;

		TextView url;

		TextView secret;

		CheckedTextView listCheckBox;

		int position = 0;

		public Widgets(View convertView) {
			super(convertView);

			title = (TextView) convertView.findViewById(R.id.sync_title);
			url = (TextView) convertView.findViewById(R.id.sync_url);
			keywords = (TextView) convertView.findViewById(R.id.sync_keyword);
			secret = (TextView) convertView.findViewById(R.id.sync_secret);

			listCheckBox = (CheckedTextView) convertView
					.findViewById(R.id.sync_checkbox);

			listCheckBox.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			if (listCheckBox.isChecked()) {
				// prompt user to disable SMSSync service if this is the last
				// enabled Sync URL
				// this is to allow the user to disable the SMSSync service
				// before the last Sync URL is disabled.
				final int total = syncUrls.totalActiveSynUrl();
				if ( ( total == 1) &&(Prefs.enabled) ) {
					
					Toast.makeText(context, R.string.disable_last_sync_url, Toast.LENGTH_LONG).show();
				} else {
					getItem(position).setStatus(0);

					updateStatus(position);
					listCheckBox.setChecked(false);
				}
			} else {
				getItem(position).setStatus(1);
				updateStatus(position);
				listCheckBox.setChecked(true);
			}
		}

	}

	private SyncUrlModel syncUrls;

	public SyncUrlAdapter(Context context) {
		super(context);
		syncUrls = new SyncUrlModel();
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		View row = inflater.inflate(R.layout.list_sync_url_item, viewGroup,
				false);
		Widgets widget = (Widgets) row.getTag();

		if (widget == null) {
			widget = new Widgets(row);
			row.setTag(widget);
		}

		// initialize view with content
		widget.title.setText(getItem(position).getTitle());
		widget.keywords.setText(getItem(position).getKeywords());
		widget.url.setText(getItem(position).getUrl());
		widget.secret.setText(getItem(position).getSecret());
		widget.position = position;

		if (getItem(position).getStatus() == 1) {
			widget.listCheckBox.setChecked(true);
		} else {
			widget.listCheckBox.setChecked(false);
		}

		return row;
	}

	@Override
	public void refresh() {
		if (syncUrls.load()) {
			this.setItems(syncUrls.listSyncUrl);
		}
	}

	/**
	 * Update the status of a Sync URL. Making it enabled or disabled.
	 * 
	 * @param position 1 for enabled and 0 for disabled.
	 * @return boolean
	 */
	public boolean updateStatus(int position) {

		return syncUrls.updateStatus(this.getItem(position));
	}

}
