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
import org.addhen.smssync.presentation.model.FilterModel;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FilterAdapter extends BaseRecyclerViewAdapter<FilterModel> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new Widgets(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.filter_list_item, viewGroup, false));
    }

    @Override
    public int getAdapterItemCount() {
        return getItems().size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Widgets) holder).phoneNumber.setText(getItem(position).phoneNumber);
    }

    public class Widgets extends RecyclerView.ViewHolder {

        @InjectView(R.id.filter_phone_number)
        TextView phoneNumber;

        public Widgets(final View view) {
            super(view);
            ButterKnife.inject(this, view);

        }
    }
}
