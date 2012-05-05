package org.addhen.smssync.adapters;

import org.addhen.smssync.R;
import org.addhen.smssync.models.SentMessagesModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SentMessagesAdapter extends BaseListAdapter<SentMessagesModel> {

	public class Widgets extends org.addhen.smssync.views.View {
		TextView messageFrom;

		TextView messageDate;

		TextView message;

		public Widgets(View convertView) {
			super(convertView);
			messageFrom = (TextView) convertView
					.findViewById(R.id.sent_message_from);
			messageDate = (TextView) convertView
					.findViewById(R.id.sent_message_date);
			message = (TextView) convertView.findViewById(R.id.sent_message);
			
		}
	}

	private SentMessagesModel messages;

	public SentMessagesAdapter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		View row = inflater.inflate(R.layout.list_sent_messages_item, viewGroup,
				false);
		Widgets widget = (Widgets) row.getTag();

		if (widget == null) {
			widget = new Widgets(row);
			row.setTag(widget);
		}

		// initialize view with content
		widget.messageFrom.setText(getItem(position).getMessageFrom());
		widget.messageDate.setText(getItem(position).getMessageFrom());
		widget.message.setText(getItem(position).getMessage());

		return row;
	}

	@Override
	public void refresh() {
		 messages = new SentMessagesModel();
		if (messages.load()) {
			this.setItems(messages.listMessages);
		}

	}

}
