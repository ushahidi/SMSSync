package org.addhen.smssync.adapters;

import org.addhen.smssync.R;
import org.addhen.smssync.models.SyncUrlModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class SyncUrlAdapter extends BaseListAdapter<SyncUrlModel> {

	public class Widgets extends org.addhen.smssync.views.View implements
			View.OnClickListener {

		TextView title;

		TextView keywords;

		TextView url;

		TextView secret;

		CheckedTextView listCheckBox;

		public Widgets(View convertView) {
			super(convertView);
			title = (TextView) convertView.findViewById(R.id.sync_title);
			url = (TextView) convertView.findViewById(R.id.sync_url);
			keywords = (TextView) convertView.findViewById(R.id.sync_keyword);
			secret = (TextView)convertView.findViewById(R.id.sync_secret);
			listCheckBox = (CheckedTextView) convertView
					.findViewById(R.id.sync_checkbox);
			listCheckBox.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			listCheckBox.setChecked(false);
		}
	}

	private SyncUrlModel syncUrls;

	public SyncUrlAdapter(Context context) {
		super(context);
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
		log("URL: "+getItem(position).getUrl());
		widget.url.setText(getItem(position).getUrl());
		widget.secret.setText(getItem(position).getSecret());

		return row;
	}

	@Override
	public void refresh() {
		syncUrls = new SyncUrlModel();
		if (syncUrls.load()) {
			this.setItems(syncUrls.listSyncUrl);
		}
	}

}
