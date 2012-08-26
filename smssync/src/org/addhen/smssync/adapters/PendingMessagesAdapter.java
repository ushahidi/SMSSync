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

import org.addhen.smssync.R;
import org.addhen.smssync.models.MessagesModel;
import org.addhen.smssync.util.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class PendingMessagesAdapter extends BaseListAdapter<MessagesModel> {

	public class Widgets extends org.addhen.smssync.views.View implements
			View.OnClickListener {
		TextView messageFrom;

		TextView messageDate;

		TextView message;

		CheckedTextView listCheckBox;

		public Widgets(View convertView) {
			super(convertView);
			messageFrom = (TextView) convertView
					.findViewById(R.id.message_from);
			messageDate = (TextView) convertView
					.findViewById(R.id.message_date);
			message = (TextView) convertView.findViewById(R.id.message);
		}

		@Override
		public void onClick(View v) {
			// listCheckBox.setChecked(true);
		}
	}

	private MessagesModel messages;

	public PendingMessagesAdapter(Context context) {
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
		widget.messageDate.setText(formatDate(getItem(position)
				.getMessageDate()));
		widget.message.setText(getItem(position).getMessage());

		return row;
	}

	@Override
	public void refresh() {
		messages = new MessagesModel();
		if (messages.load()) {
			this.setItems(messages.listMessages);
		}
	}

	/**
	 * Set the date of the message.
	 * 
	 * @param String
	 *            messageDate - The timestamp of the message. To be changed into
	 *            human readable.
	 * @return void
	 */
	public String formatDate(String messageDate) {
		try {
			return Util.formatDateTime(Long.parseLong(messageDate),
					"MMM dd, yyyy 'at' hh:mm a");

		} catch (NumberFormatException e) {
			return messageDate;
		}

	}

}
