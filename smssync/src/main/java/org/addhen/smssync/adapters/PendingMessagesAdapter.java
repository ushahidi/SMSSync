/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.addhen.smssync.App;
import org.addhen.smssync.R;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;

import java.util.List;
import java.util.Locale;

import static org.addhen.smssync.models.Message.Type;

public class PendingMessagesAdapter extends BaseListAdapter<Message> {

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
        if (getItem(position)
                .getDate() != null) {
            widgets.messageDate.setText(formatDate(getItem(position)
                    .getDate()));
        }
        widgets.message.setText(getItem(position).getBody());

        // Pending messages
        if (getItem(position).getType() == Type.PENDING) {
            widgets.messageType.setText(context.getString(R.string.sms).toUpperCase(
                    Locale.getDefault()));
        } else if (getItem(position).getType() == Type.TASK) {
            // Task messages
            widgets.messageType.setText(context.getString(R.string.task).toUpperCase(Locale.getDefault()));
        }
        widgets.messageType.setTextColor(context.getResources().getColor(
                R.color.task_color));

        widgets.messageStatus.setText(getItem(position).getStatus().name());
        widgets.messageStatus.setTextColor(context.getResources().getColor(
                R.color.pending_color));

        return view;
    }

    @Override
    public void refresh() {

        App.getDatabaseInstance().getMessageInstance().fetchAll(
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

    public class Widgets extends org.addhen.smssync.views.View implements
            View.OnClickListener {

        TextView messageFrom;

        TextView messageDate;

        TextView message;

        TextView messageType;

        TextView messageStatus;

        public Widgets(View convertView) {
            super(convertView);
            messageFrom = (TextView) convertView
                    .findViewById(R.id.message_from);
            messageDate = (TextView) convertView
                    .findViewById(R.id.message_date);
            message = (TextView) convertView.findViewById(R.id.message);
            messageType = (TextView) convertView
                    .findViewById(R.id.sent_message_type);

            messageStatus = (TextView) convertView.findViewById(R.id.sent_message_status);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
