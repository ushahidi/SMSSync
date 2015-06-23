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

package org.addhen.smssync.presentation.ui.fragment;

import com.addhen.android.raiburari.presentation.ui.fragment.BaseRecyclerViewFragment;
import com.addhen.android.raiburari.presentation.ui.listener.SwipeToDismissTouchListener;
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.presentation.ui.adapter.MessageAdapter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageFragment extends BaseRecyclerViewFragment<MessageModel, MessageAdapter> {

    @InjectView(R.id.messages_fab)
    FloatingActionButton mFab;

    @InjectView(android.R.id.list)
    BloatedRecyclerView mMessageRecyclerView;

    @InjectView(R.id.empty_list_view)
    TextView mEmptyView;

    private MessageAdapter mMessageAdapter;

    private static MessageFragment mMessageFragment;

    public MessageFragment() {
        super(MessageAdapter.class, R.layout.fragment_list_message, R.menu.menu_messages);
    }

    public static MessageFragment newInstance() {
        if (mMessageFragment == null) {
            mMessageFragment = new MessageFragment();
        }
        return mMessageFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeToDeleteUndo();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void swipeToDeleteUndo() {
        mMessageAdapter = new MessageAdapter(mEmptyView);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessageRecyclerView.setFocusable(true);
        mMessageRecyclerView.setFocusableInTouchMode(true);
        mMessageAdapter.setHasStableIds(true);
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageRecyclerView.addItemDividerDecoration(getActivity());
        mMessageRecyclerView.setSwipeToDismissCallback(
                new SwipeToDismissTouchListener.DismissCallbacks() {
                    @Override
                    public SwipeToDismissTouchListener.SwipeDirection canDismiss(int position) {
                        return SwipeToDismissTouchListener.SwipeDirection.BOTH;
                    }

                    @Override
                    public void onDismiss(RecyclerView view,
                            List<SwipeToDismissTouchListener.PendingDismissData> dismissData) {
                        // perform swap to delete
                    }
                });
    }

}
