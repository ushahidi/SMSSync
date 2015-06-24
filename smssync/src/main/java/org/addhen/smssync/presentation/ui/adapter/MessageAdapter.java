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

package org.addhen.smssync.presentation.ui.adapter;

import com.addhen.android.raiburari.presentation.ui.adapter.BaseRecyclerViewAdapter;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.util.Utility;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageAdapter extends BaseRecyclerViewAdapter<MessageModel> {

    private View mEmptyView;

    public MessageAdapter(final View emptyView) {
        mEmptyView = emptyView;
        onDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new Widgets(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.list_message_item, viewGroup, false));
    }

    @Override
    public int getAdapterItemCount() {
        return getItems().size();
    }

    @Override
    public void setItems(List<MessageModel> items) {
        super.setItems(items);
        onDataSetChanged();
    }

    /**
     * Sets an empty view when the adapter's data item gets to zero
     */
    private void onDataSetChanged() {
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MessageModel messageModel = getItem(position);
        // initialize view with content
        ((Widgets) holder).messageFrom.setText(messageModel.messageFrom);
        if (messageModel.messageDate != null) {
            ((Widgets) holder).messageDate.setText(Utility.formatDate(messageModel.messageDate));
        }
        ((Widgets) holder).message.setText(messageModel.messageBody);
        // Pending messages
        if (messageModel.messageType == MessageModel.Type.PENDING) {
            ((Widgets) holder).messageType
                    .setText(((Widgets) holder).itemView.getContext().getString(
                            R.string.sms).toUpperCase(
                            Locale.getDefault()));
        } else if (messageModel.messageType == MessageModel.Type.TASK) {
            // Task messages
            ((Widgets) holder).messageType.setText(
                    ((Widgets) holder).itemView.getContext().getString(R.string.task)
                            .toUpperCase(Locale.getDefault()));
        }
        ((Widgets) holder).messageType
                .setTextColor(((Widgets) holder).itemView.getContext().getResources().getColor(
                        R.color.red));

        ((Widgets) holder).messageStatus.setText(messageModel.status.name());
    }

    public class Widgets extends RecyclerView.ViewHolder {

        @InjectView(R.id.status_indicator)
        AppCompatTextView statusIndicator;

        @InjectView(R.id.message_from)
        AppCompatTextView messageFrom;

        @InjectView(R.id.message_date)
        TextView messageDate;

        @InjectView(R.id.message)
        TextView message;

        @InjectView(R.id.sent_message_type)
        TextView messageType;

        @InjectView(R.id.sent_message_status)
        TextView messageStatus;

        public Widgets(final View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}