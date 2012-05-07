package org.addhen.smssync.adapters;

import org.addhen.smssync.R;
import org.addhen.smssync.models.SyncUrlModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class SyncUrlAdapter extends BaseListAdapter<SyncUrlModel> {

	public class Widgets extends org.addhen.smssync.views.View {
		TextView messageFrom;

		TextView messageDate;

		TextView message;

		CheckBox listCheckBox;

		public Widgets(View convertView) {
			super(convertView);
			messageFrom = (TextView) convertView
					.findViewById(R.id.message_from);
			messageDate = (TextView) convertView
					.findViewById(R.id.message_date);
			message = (TextView) convertView.findViewById(R.id.message);
			listCheckBox = (CheckBox) convertView
					.findViewById(R.id.list_checkbox);
		}
	}

	private SyncUrlModel messages;

	public SyncUrlAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		View row = inflater.inflate(R.layout.list_messages_item, viewGroup,
				false);
		Widgets widget = (Widgets) row.getTag();

		if (widget == null) {
			widget = new Widgets(row);
			row.setTag(widget);
		}

		// initialize view with content
		widget.messageFrom.setText(getItem(position).getMessageFrom());
		widget.messageDate.setText(getItem(position).getMessageDate());
		widget.message.setText(getItem(position).getMessage());

		return row;
	}

	@Override
	public void refresh() {
		messages = new SyncUrlModel();
		if (messages.load()) {
			this.setItems(messages.listMessages);
		}
	}

}
