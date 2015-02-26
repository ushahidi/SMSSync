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

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.database.Message;

import static org.addhen.smssync.database.Message.Type;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PendingMessagesAdapter extends BaseListAdapter<Message> {

    public class Widgets extends org.addhen.smssync.views.View implements
            View.OnClickListener {

        TextView messageFrom;

        TextView messageDate;

        TextView message;

        TextView messageType;

        public Widgets(View convertView) {
            super(convertView);
            messageFrom = (TextView) convertView
                    .findViewById(R.id.message_from);
            messageDate = (TextView) convertView
                    .findViewById(R.id.message_date);
            message = (TextView) convertView.findViewById(R.id.message);
            messageType = (TextView) convertView
                    .findViewById(R.id.sent_message_type);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public PendingMessagesAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Widgets widgets;
        if (view == null) {
            view = inflater.inflate(R.layout.list_messages_item, null);
            widgets = new Widgets(view);
            view.setTag(widgets);
        } else {
            widgets = (Widgets) view.getTag();
        }

        // initialize view with content
        widgets.messageFrom.setText(getItem(position).getPhoneNumber());
        widgets.messageDate.setText(formatDate(getItem(position)
                .getDate()));
        widgets.message.setText(getItem(position).getBody());

        // Pending messages
        if (getItem(position).getType() == Type.PENDING) {
            widgets.messageType.setText(R.string.sms);
            widgets.messageType.setTextColor(context.getResources().getColor(
                    R.color.pending_color));

        } else if (getItem(position).getType() == Type.TASK) {
            // Task messages
            widgets.messageType.setText(R.string.task);
            widgets.messageType.setTextColor(context.getResources().getColor(
                    R.color.task_color));
        } else {
            //TODO mark this as status
            widgets.messageType.setText(R.string.failed);
            widgets.messageType.setTextColor(context.getResources().getColor(
                    R.color.task_color));
        }

        return view;
    }

    @Override
    public void refresh() {

        MainApplication.getDatabaseInstance().getMessageInstance().fetchAll(
                new BaseDatabseHelper.DatabaseCallback<List<Message>>() {
            @Override
            public void onFinished(List<Message> result) {
                setItems(result);
            }

            @Override
            public void onError(Exception exception) {

            }
        });

    }
}
