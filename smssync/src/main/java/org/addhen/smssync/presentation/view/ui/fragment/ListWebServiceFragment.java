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
import com.addhen.android.raiburari.presentation.ui.widget.BloatedRecyclerView;
import com.nineoldandroids.view.ViewHelper;

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.di.component.WebServiceComponent;
import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.presenter.webservice.DeleteWebServicePresenter;
import org.addhen.smssync.presentation.presenter.webservice.ListWebServicePresenter;
import org.addhen.smssync.presentation.presenter.webservice.UpdateWebServicePresenter;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.ui.adapter.WebServiceAdapter;
import org.addhen.smssync.presentation.view.ui.navigation.Launcher;
import org.addhen.smssync.presentation.view.ui.widget.DividerItemDecoration;
import org.addhen.smssync.presentation.view.webservice.DeleteWebServiceView;
import org.addhen.smssync.presentation.view.webservice.ListWebServiceView;
import org.addhen.smssync.presentation.view.webservice.UpdateWebServiceView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Fragment for showing list of webServices
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class ListWebServiceFragment
        extends BaseRecyclerViewFragment<WebServiceModel, WebServiceAdapter>
        implements ListWebServiceView,
        RecyclerViewItemTouchListenerAdapter.RecyclerViewOnItemClickListener {

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(android.R.id.empty)
    RelativeLayout mEmptyView;

    @BindView(android.R.id.list)
    BloatedRecyclerView mWebServiceRecyclerView;

    @Inject
    ListWebServicePresenter mListWebServicePresenter;

    @Inject
    DeleteWebServicePresenter mDeleteWebServicePresenter;

    @Inject
    UpdateWebServicePresenter mUpdateWebServicePresenter;

    @Inject
    PrefsFactory mPrefs;

    @Inject
    Launcher mLauncher;

    // Manually creating the webService adapter because
    // for some weirdness the super class cannot find the custom recyclerviewer
    // in the layout so the adapter is not created.
    private WebServiceAdapter mWebServiceAdapter;

    private WebServiceListListener mWebServiceListListener;

    private int mRemovedItemPosition = 0;

    private WebServiceModel mRemovedWebServiceModel;

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
        mDeleteWebServicePresenter.setView(new DeleteWebServiceView() {

            @Override
            public void showError(String s) {
                showSnackbar(getView(), s);
            }

            @Override
            public Context getAppContext() {
                return getActivity().getApplicationContext();
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
            public void onWebServiceDeleted() {
                mListWebServicePresenter.loadWebServices();
                showToast(getActivity().getString(R.string.item_successfully_deleted,
                        mRemovedWebServiceModel.getTitle()));
            }
        });

        mUpdateWebServicePresenter.setView(new UpdateWebServiceView() {
            @Override
            public void onWebServiceSuccessfullyUpdated(Long row) {

            }

            @Override
            public void showWebService(WebServiceModel webServiceModel) {

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
            public void showError(String s) {
                showSnackbar(getView(), s);
            }

            @Override
            public Context getAppContext() {
                return getActivity().getApplicationContext();
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        mWebServiceAdapter = new WebServiceAdapter();
        mWebServiceAdapter.setOnItemCheckedListener((position, status) -> {
            final WebServiceModel webServiceModel = mWebServiceAdapter.getItem(position);
            if (status) {
                if (mWebServiceAdapter.getItemCount() == 1 && mPrefs.serviceEnabled().get()) {
                    showSnackbar(getView(), R.string.disable_last_sync_url);
                } else {
                    webServiceModel.setStatus(WebServiceModel.Status.ENABLED);
                    mUpdateWebServicePresenter.updateWebService(webServiceModel);
                }
            } else {
                webServiceModel.setStatus(WebServiceModel.Status.DISABLED);
                mUpdateWebServicePresenter.updateWebService(webServiceModel);
            }
        });
        if (mFab != null) {
            setViewGone(mFab, false);
            mFab.setOnClickListener(v -> mLauncher.launchAddWebServices());
        }
        mWebServiceRecyclerView.setFocusable(true);
        mWebServiceRecyclerView.setFocusableInTouchMode(true);
        mWebServiceRecyclerView.setAdapter(mWebServiceAdapter);
        mWebServiceRecyclerView.setHasFixedSize(true);
        mWebServiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWebServiceRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), null));

        RecyclerViewItemTouchListenerAdapter recyclerViewItemTouchListenerAdapter
                = new RecyclerViewItemTouchListenerAdapter(mWebServiceRecyclerView.recyclerView,
                this);
        mWebServiceRecyclerView.recyclerView
                .addOnItemTouchListener(recyclerViewItemTouchListenerAdapter);
        enableSwipeToPerformAction();
    }

    private void enableSwipeToPerformAction() {
        ItemTouchHelper.SimpleCallback swipeToDismiss = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                    RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                    boolean isCurrentlyActive) {
                drawSwipeListItemBackground(c, (int) dX, viewHolder.itemView, actionState);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                ViewHelper.setAlpha(viewHolder.itemView, 1.0f);
                viewHolder.itemView.setBackgroundColor(0);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDismiss);
        itemTouchHelper.attachToRecyclerView(mWebServiceRecyclerView.recyclerView);
    }

    private void remove(int position) {
        mRemovedItemPosition = position;
        mRemovedWebServiceModel = mWebServiceAdapter.getItem(position);
        mWebServiceAdapter.removeItem(mRemovedWebServiceModel);
        // Make sure web service is disabled before allowing deletion
        if (mRemovedWebServiceModel.getStatus() == WebServiceModel.Status.DISABLED) {
            showUndoSnackbar(1);
        } else {
            // Restore item
            mWebServiceAdapter.addItem(mRemovedWebServiceModel, mRemovedItemPosition);
            showSnackbar(mFab, getString(R.string.delete_enabled_webservice));
        }
    }

    private void showUndoSnackbar(int count) {
        Snackbar snackbar = Snackbar
                .make(mFab, getResources().getQuantityString(R.plurals.webservice_deleted, count,
                        count), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, v -> {
            // Restore item
            mWebServiceAdapter.addItem(mRemovedWebServiceModel, mRemovedItemPosition);
        });
        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    mDeleteWebServicePresenter.deleteWebService(mRemovedWebServiceModel._id);
                }
            }
        });
        snackbar.show();
    }

    private void drawSwipeListItemBackground(Canvas c, int dX, View itemView, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            final float alpha = 2.0f - Math.abs(dX) / (float) itemView.getWidth();
            ViewHelper.setAlpha(itemView, alpha);
            ViewHelper.setTranslationX(itemView, dX);

            Drawable d;
            // Swiping right
            if (dX > 0) {
                d = ContextCompat
                        .getDrawable(getAppContext(),
                                R.drawable.swipe_right_publish_list_item_background);
                d.setBounds(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom());
            } else { // Swiping left
                d = ContextCompat
                        .getDrawable(getAppContext(),
                                R.drawable.swipe_left_publish_list_item_background);
                d.setBounds(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(),
                        itemView.getBottom());
            }
            d.draw(c);
        }
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
        // Do nothing
    }

    @Override
    public void onItemLongClick(RecyclerView recyclerView, View view, int position) {
        updateWebService(position);
    }

    private void updateWebService(int position) {
        if (mWebServiceAdapter.getItemCount() > 0) {
            WebServiceModel webServiceModel = mWebServiceAdapter.getItem(position);
            if (mWebServiceListListener != null) {
                mWebServiceListListener.onWebServiceClicked(webServiceModel);
            }
        }
    }

    /**
     * Listens for webService list events
     */
    public interface WebServiceListListener {

        void onWebServiceClicked(final WebServiceModel webServiceModel);
    }

}
