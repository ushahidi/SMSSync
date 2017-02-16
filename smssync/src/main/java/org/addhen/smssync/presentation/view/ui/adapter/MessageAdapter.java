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

package org.addhen.smssync.presentation.view.ui.adapter;

import com.addhen.android.raiburari.presentation.ui.adapter.BaseRecyclerViewAdapter;
import com.addhen.android.raiburari.presentation.ui.widget.CapitalizedTextView;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.ui.widget.TextDrawable;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageAdapter extends BaseRecyclerViewAdapter<MessageModel> implements Filterable {

    private SparseBooleanArray mSelectedItems;

    private OnCheckedListener mOnCheckedListener;

    private OnMoreActionListener mOnMoreActionListener;

    private TextDrawable.IBuilder mDrawableBuilder = TextDrawable.builder().round();

    private Animation flipIn;

    private Animation flipOut;

    private Filter mFilter = null;

    private List<MessageModel> msgs = new ArrayList<>();

    public MessageAdapter() {
        mSelectedItems = new SparseBooleanArray();
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
    }


    /**
     * Toggles an item in the adapter as selected or de-selected
     *
     * @param position The index of the item to be toggled
     */
    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
    }

    private boolean isChecked(int position) {
        if (mSelectedItems.get(position, false)) {
            return true;
        }
        return false;
    }

    /**
     * Count of the selected item
     *
     * @return The selected item size
     */
    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    /**
     * Clear all selections
     */
    public void clearSelections() {
        mSelectedItems.clear();
    }

    /**
     * Gets all selected items
     *
     * @return The list of selected items
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    public void setOnCheckedListener(OnCheckedListener onCheckedListener) {
        mOnCheckedListener = onCheckedListener;
    }

    public void setOnMoreActionListener(OnMoreActionListener onMoreActionListener) {
        mOnMoreActionListener = onMoreActionListener;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MessageFilter();
        }
        return mFilter;
    }

    private void updateCheckedState(Widgets holder, int position) {
        if (isChecked(position)) {
            holder.imageView.setImageDrawable(
                    mDrawableBuilder.build(ContextCompat.getDrawable(holder.itemView.getContext(),
                            R.drawable.ic_done_white_18dp)
                            , 0xff616161));
        } else {
            TextDrawable drawable = mDrawableBuilder
                    .build(ContextCompat.getDrawable(holder.itemView.getContext(),
                            R.drawable.ic_call_white_18dp),
                            ContextCompat
                                    .getColor(holder.itemView.getContext(), R.color.orange_light));
            holder.imageView.setImageDrawable(drawable);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount() && (customHeaderView != null ? position <= getItems().size()
                : position < getItems().size()) && (customHeaderView != null ? position > 0
                : true)) {

            final MessageModel messageModel = getItem(position);
            flipIn = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.flip_front);
            flipOut = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.flip_back);
            // Initialize view with content
            Widgets widgets = ((Widgets) holder);
            widgets.messageFrom.setText(messageModel.getMessageFrom());
            if (messageModel.getMessageDate() != null) {
                widgets.messageDate.setText(Utility.formatDate(messageModel.getMessageDate()));
            }
            widgets.message.setText(messageModel.getMessageBody());
            // Pending messages
            if (messageModel.getMessageType().equals(MessageModel.Type.PENDING)) {
                widgets.messageType.setText(widgets.itemView.getContext().getString(
                        R.string.sms).toUpperCase(Locale.getDefault()));
            } else if (messageModel.getMessageType().equals(MessageModel.Type.TASK)) {
                // Task messages
                widgets.messageType
                        .setText(widgets.itemView.getContext().getString(R.string.task).toUpperCase(
                                Locale.getDefault()));
            }
            widgets.messageType
                    .setTextColor(
                            widgets.itemView.getContext().getResources().getColor(R.color.red));

            updateCheckedState(widgets, position);
            widgets.imageView.setOnClickListener(v -> {
                widgets.imageView.clearAnimation();
                widgets.imageView.setAnimation(flipOut);
                widgets.imageView.startAnimation(flipOut);
                if (mOnCheckedListener != null) {
                    mOnCheckedListener.onChecked(position);
                }
                setFlipAnimation(widgets, position);
            });

            widgets.statusIndicator.setOnClickListener(v -> {
                if (mOnMoreActionListener != null) {
                    mOnMoreActionListener.onMoreActionTap(position);
                }
            });
        }
    }

    private void setFlipAnimation(Widgets widgets, int position) {

        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (animation == flipOut) {
                    updateCheckedState(widgets, position);
                } else {
                    widgets.imageView.clearAnimation();
                    widgets.imageView.setAnimation(flipIn);
                    widgets.imageView.startAnimation(flipIn);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isChecked(position)) {
                    widgets.checkIcon.setVisibility(View.VISIBLE);
                } else {
                    widgets.checkIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        flipIn.setAnimationListener(animationListener);
        flipOut.setAnimationListener(animationListener);
    }

    public class Widgets extends RecyclerView.ViewHolder {

        @BindView(R.id.status_indicator)
        ImageView statusIndicator;

        @BindView(R.id.message_from)
        AppCompatTextView messageFrom;

        @BindView(R.id.message_date)
        AppCompatTextView messageDate;

        @BindView(R.id.message)
        CapitalizedTextView message;

        @BindView(R.id.sent_message_type)
        AppCompatTextView messageType;

        @BindView(R.id.message_icons)
        ImageView imageView;

        @BindView(R.id.check_icon)
        ImageView checkIcon;

        public Widgets(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnCheckedListener {

        void onChecked(int position);
    }

    public interface OnMoreActionListener {

        void onMoreActionTap(int position);
    }

    private class MessageFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            constraint = constraint.toString().toLowerCase();
            if (msgs.isEmpty()) {
                msgs.addAll(getItems());
            }
            results.values = msgs;
            results.count = msgs.size();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<MessageModel> filteredItems = new ArrayList<>();
                for (MessageModel message : msgs) {
                    if (message.getMessageBody().toLowerCase()
                            .contains(constraint.toString().toLowerCase())) {
                        filteredItems.add(message);
                    }
                }
                results.count = filteredItems.size();
                results.values = filteredItems;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            List<MessageModel> messageModels = (ArrayList<MessageModel>) filterResults.values;
            setItems(messageModels);
        }
    }

}
