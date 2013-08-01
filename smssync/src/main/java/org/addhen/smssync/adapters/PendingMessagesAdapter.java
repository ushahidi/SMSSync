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
import org.addhen.smssync.models.Message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class PendingMessagesAdapter extends BaseListAdapter<Message> {

	public class Widgets extends org.addhen.smssync.views.View implements
			View.OnClickListener {
		TextView messageFrom;

		TextView messageDate;

		TextView message;

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

		}
	}

	private Message message;

	public PendingMessagesAdapter(Context context) {
		super(context);
		message = new Message();
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
        Widgets widgets;
        if(view == null) {
            view = inflater.inflate(R.layout.list_messages_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        }
        else {
            widgets = (Widgets) view.getTag();
        }

		// initialize view with content
		widgets.messageFrom.setText(getItem(position).getFrom());
		widgets.messageDate.setText(formatDate(getItem(position)
				.getTimestamp()));
		widgets.message.setText(getItem(position).getBody());

		return view;
	}

	@Override
	public void refresh() {
		if (message.load()) {
			setItems(message.getMessageList());
		}
	}
}
