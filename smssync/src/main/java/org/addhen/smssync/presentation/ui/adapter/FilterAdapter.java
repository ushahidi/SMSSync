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
import org.addhen.smssync.databinding.FilterListItemBinding;
import org.addhen.smssync.presentation.model.FilterModel;

import android.databinding.DataBindingUtil;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FilterAdapter extends BaseRecyclerViewAdapter<FilterModel> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final FilterListItemBinding filterListItemBinding = DataBindingUtil
                .inflate(inflater, R.layout.filter_list_item, viewGroup, false);
        return new Widgets(filterListItemBinding.getRoot(), filterListItemBinding);
    }

    @Override
    public int getAdapterItemCount() {
        return getItems().size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final FilterModel filterModel = getItem(position);
        ((Widgets) holder).bindData(filterModel);
    }

    public class Widgets extends RecyclerView.ViewHolder {

        private final FilterListItemBinding mFilterListItemBinding;

        public final View mView;

        public Widgets(final View view, final FilterListItemBinding filterListItemBinding) {
            super(view);
            mView = view;
            mFilterListItemBinding = filterListItemBinding;
        }

        @UiThread
        public void bindData(FilterModel filterModel) {
            mFilterListItemBinding.setFilter(filterModel);
        }
    }
}
