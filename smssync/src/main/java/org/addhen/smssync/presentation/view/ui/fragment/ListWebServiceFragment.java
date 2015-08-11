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

package org.addhen.smssync.presentation.view.ui.fragment;

import com.addhen.android.raiburari.presentation.ui.fragment.BaseRecyclerViewFragment;
import com.addhen.android.raiburari.presentation.ui.listener.RecyclerViewItemTouchListenerAdapter;
import com.addhen.android.raiburari.presentation.ui.listener.SwipeToDismissTouchListener;
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.di.component.WebServiceComponent;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.presenter.webservice.DeleteWebServicePresenter;
import org.addhen.smssync.presentation.presenter.webservice.ListWebServicePresenter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.ui.adapter.WebServiceAdapter;
import org.addhen.smssync.presentation.view.ui.navigation.Launcher;
import org.addhen.smssync.presentation.view.webservice.DeleteWebServiceView;
import org.addhen.smssync.presentation.view.webservice.ListWebServiceView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Fragment for showing list of webServices
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ListWebServiceFragment
        extends BaseRecyclerViewFragment<WebServiceModel, WebServiceAdapter>
        implements ListWebServiceView, DeleteWebServiceView,
        RecyclerViewItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(android.R.id.empty)
    RelativeLayout mEmptyView;

    @Bind(android.R.id.list)
    BloatedRecyclerView mWebServiceRecyclerView;

    @Inject
    ListWebServicePresenter mListWebServicePresenter;

    @Inject
    DeleteWebServicePresenter mDeleteWebServicePresenter;

    @Inject
    Launcher mLauncher;

    // Manually creating the webService adapter because
    // for some weirdness the super class cannot find the custom recyclerviewer
    // in the layout so the adapter is not created.
    private WebServiceAdapter mWebServiceAdapter;

    private WebServiceListListener mWebServiceListListener;

    public ListWebServiceFragment() {
        super(WebServiceAdapter.class, R.layout.fragment_list_web_service, 0);
    }

    public static ListWebServiceFragment newInstance() {
        return new ListWebServiceFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof WebServiceListListener) {
            mWebServiceListListener = (WebServiceListListener) activity;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        getComponent(WebServiceComponent.class).inject(this);
        mListWebServicePresenter.setView(this);
        mDeleteWebServicePresenter.setView(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mWebServiceAdapter = new WebServiceAdapter();
        if (mFab != null) {
            setViewGone(mFab, false);
            // TODO: Launch add web service
            // mFab.setOnClickListener(v -> mLauncher.launchAddWebService());
        }
        mWebServiceRecyclerView.setFocusable(true);
        mWebServiceRecyclerView.setFocusableInTouchMode(true);
        mWebServiceRecyclerView.setAdapter(mWebServiceAdapter);
        mWebServiceRecyclerView.setHasFixedSize(true);
        mWebServiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWebServiceAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setEmptyView();
            }
        });

        RecyclerViewItemTouchListenerAdapter recyclerViewItemTouchListenerAdapter
                = new RecyclerViewItemTouchListenerAdapter(mWebServiceRecyclerView.recyclerView,
                this);
        mWebServiceRecyclerView.recyclerView
                .addOnItemTouchListener(recyclerViewItemTouchListenerAdapter);
        swipeToDeleteUndo();
        setEmptyView();
    }

    private void setEmptyView() {
        if (mWebServiceAdapter != null && mWebServiceAdapter.getItemCount() == 0) {
            setViewGone(mEmptyView, false);
        } else {
            setViewGone(mEmptyView);
        }
    }

    private void swipeToDeleteUndo() {
        mWebServiceRecyclerView
                .setSwipeToDismissCallback(new SwipeToDismissTouchListener.DismissCallbacks() {
                    @Override
                    public SwipeToDismissTouchListener.SwipeDirection canDismiss(int position) {
                        return SwipeToDismissTouchListener.SwipeDirection.BOTH;
                    }

                    @Override
                    public void onDismiss(RecyclerView view,
                            List<SwipeToDismissTouchListener.PendingDismissData> dismissData) {

                        for (SwipeToDismissTouchListener.PendingDismissData data : dismissData) {
                            // TODO: Perform swipe to delete
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mListWebServicePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mListWebServicePresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListWebServicePresenter.destroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Nullify the webService listener to avoid potential memory leaks
        mWebServiceListListener = null;
    }

    @Override
    public void renderWebServiceList(List<WebServiceModel> webServiceModel) {
        if (!Utility.isEmpty(webServiceModel)) {
            mWebServiceAdapter.setItems(webServiceModel);
        }
    }

    @Override
    public void showLoading() {
        // Do nothing
    }

    @Override
    public void hideLoading() {
        // Do nothing
    }

    @Override
    public void showRetry() {
        // Do nothing
    }

    @Override
    public void hideRetry() {
        // Do nothing
    }

    @Override
    public void showError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public Context getAppContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void onItemClick(RecyclerView recyclerView, View view, int position) {
        if (mWebServiceAdapter.getItemCount() > 0) {
            WebServiceModel webServiceModel = mWebServiceAdapter.getItem(position);
            if (mWebServiceListListener != null) {
                mWebServiceListListener.onWebServiceClicked(webServiceModel);
            }
        }
    }

    @Override
    public void onItemLongClick(RecyclerView recyclerView, View view, int i) {
        // Do nothing
    }

    @Override
    public void onWebServiceDeleted() {
        mListWebServicePresenter.loadWebServices();
    }

    /**
     * Listens for webService list events
     */
    public interface WebServiceListListener {

        void onWebServiceClicked(final WebServiceModel webServiceModel);
    }

}
