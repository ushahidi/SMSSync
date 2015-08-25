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

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.model.WebServiceModel;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Adapter for webService listing recyclerview
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceAdapter extends BaseRecyclerViewAdapter<WebServiceModel> {

    private SparseBooleanArray mSelectedItems;

    /**
     * Default constructor
     */
    public WebServiceAdapter() {
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((Widgets) viewHolder).title.setText(getItem(position).getTitle());
        ((Widgets) viewHolder).url.setText(getItem(position).getUrl());
        ((Widgets) viewHolder).listCheckBox.setChecked(mSelectedItems.get(position, false));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new Widgets(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_web_service_item, parent, false));
    }

    @Override
    public int getAdapterItemCount() {
        return getItems().size();
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
        notifyItemChanged(position);
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
        notifyDataSetChanged();
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

    /**
     * View holder
     */
    public class Widgets extends RecyclerView.ViewHolder {

        @Bind(R.id.web_service_title)
        TextView title;

        @Bind(R.id.web_service_description)
        TextView url;

        @Bind(R.id.web_service_selected)
        CheckedTextView listCheckBox;

        /**
         * Default constructor
         *
         * @param view The view
         */
        public Widgets(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
