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

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static org.addhen.smssync.models.Message.Type;

public class SentMessagesAdapter extends BaseListAdapter<Message> {

    public SentMessagesAdapter(Context context) {
        super(context);
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
        widget.messageFrom.setText(getItem(position).getPhoneNumber());
        widget.messageDate.setText(formatDate(getItem(position).getDate()));
        widget.message.setText(getItem(position).getBody());

        // Pending messages
        if (getItem(position).getType() == Type.PENDING) {
            widget.messageType.setText(R.string.sms);

        } else if (getItem(position).getType() == Type.TASK) {
            // Task messages
            widget.messageType.setText(R.string.task);
        }

        widget.messageType.setTextColor(context.getResources().getColor(
                R.color.task_color));

        return row;
    }

    @Override
    public void refresh() {
        App.getDatabaseInstance().getMessageInstance().fetchByStatus(
                Message.Status.SENT, new BaseDatabseHelper.DatabaseCallback<List<Message>>() {
                    @Override
                    public void onFinished(List<Message> result) {
                        setItems(result);
                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });

    }

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

}
