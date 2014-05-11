/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

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

        TextView messageType;

        public Widgets(View convertView) {
            super(convertView);
            messageFrom = (TextView) convertView
                    .findViewById(R.id.sent_message_from);
            messageDate = (TextView) convertView
                    .findViewById(R.id.sent_message_date);
            message = (TextView) convertView.findViewById(R.id.sent_message);

            messageType = (TextView) convertView
                    .findViewById(R.id.sent_message_type);
        }
    }

    private SentMessagesModel messages;

    public SentMessagesAdapter(Context context) {
        super(context);
        messages = new SentMessagesModel();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = inflater.inflate(R.layout.list_sent_messages_item,
                viewGroup, false);
        Widgets widget = (Widgets) row.getTag();

        if (widget == null) {
            widget = new Widgets(row);
            row.setTag(widget);
        }

        // initialize view with content
        widget.messageFrom.setText(getItem(position).getMessageFrom());
        widget.messageDate.setText(formatDate(getItem(position).getMessageDate()));
        widget.message.setText(getItem(position).getMessage());

        // Pending messages
        if (getItem(position).getMessageType() == 0) {
            widget.messageType.setText(R.string.sms);
            widget.messageType.setTextColor(context.getResources().getColor(
                    R.color.pending_color));

        } else if (getItem(position).getMessageType() == 1) {
            // Task messages
            widget.messageType.setText(R.string.task);
            widget.messageType.setTextColor(context.getResources().getColor(
                    R.color.task_color));
        } else {
            // Unconfirmed task messages
            widget.messageType.setText(R.string.unconfirmed);
            widget.messageType.setTextColor(context.getResources().getColor(
                    R.color.task_color));
        }

        return row;
    }

    @Override
    public void refresh() {

        if (messages.load()) {
            this.setItems(messages.listMessages);
        }

    }

}
