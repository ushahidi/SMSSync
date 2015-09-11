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

import com.addhen.android.raiburari.presentation.ui.fragment.BaseFragment;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.di.component.IntegrationComponent;
import org.addhen.smssync.presentation.presenter.integration.IntegrationPresenter;
import org.addhen.smssync.presentation.receiver.SmsReceiver;
import org.addhen.smssync.presentation.view.integration.IntegrationView;
import org.addhen.smssync.presentation.view.ui.activity.IntegrationActivity;
import org.addhen.smssync.presentation.view.ui.activity.ListWebServiceActivity;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class IntegrationFragment extends BaseFragment implements IntegrationView {

    @Bind(R.id.start_service_checkbox)
    CheckBox mStartServiceCheckBox;

    @Inject
    IntegrationPresenter mIntegrationPresenter;

    private static IntegrationFragment mIntegrationFragment;

    public IntegrationFragment() {
        super(R.layout.fragment_integration, 0);
    }

    public static IntegrationFragment newInstance() {
        if (mIntegrationFragment == null) {
            mIntegrationFragment = new IntegrationFragment();
        }
        return mIntegrationFragment;
    }

    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.integration_twitter)
    void onTwitterClicked() {
        if (App.getTwitterIntance().getSessionManager().getActiveSession() == null) {
            App.getTwitterIntance().login(getActivity());
            return;
        }
        // Show profile
        ((MainActivity) getActivity()).replaceFragment(R.id.fragment_main_content,
                TwitterProfileFragment.newInstance(), "twitter_profile");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        getIntegrationComponent(IntegrationComponent.class).inject(this);
        mIntegrationPresenter.setIntegrationView(this);
        mIntegrationPresenter.setPackageManager(getActivity().getPackageManager());
        mIntegrationPresenter.setSmsReceiverComponent(new ComponentName(
                getActivity(), SmsReceiver.class));
        mStartServiceCheckBox
                .setChecked(mIntegrationPresenter.getPrefsFactory().serviceEnabled().get());
    }

    @OnClick(R.id.integration_web_service)
    void onCustomWebServiceClicked() {
        getActivity().startActivity(ListWebServiceActivity.getIntent(getActivity()));
    }

    @Override
    public void totalActiveWebService(int total) {
        if (total > 0) {
            startService();
            return;
        }
        showSnabackar(getView(), R.string.no_enabled_sync_url);
        mIntegrationPresenter.getPrefsFactory().serviceEnabled().set(false);
        mStartServiceCheckBox.setChecked(false);
    }

    @OnCheckedChanged(R.id.start_service_checkbox)
    void onChecked(boolean checked) {
        if (checked) {
            //disableCheckbox();
            mIntegrationPresenter.loadActiveWebService();
            return;
        }
        mIntegrationPresenter.stopSyncServices();
    }

    @Override
    public void showError(String message) {
        showSnabackar(getView(), message);
    }

    @Override
    public Context getAppContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }

    public void startService() {
        mIntegrationPresenter.startSyncServices();
    }

    public CheckBox getStartServiceCheckBox() {
        return mStartServiceCheckBox;
    }

    protected <C> C getIntegrationComponent(Class<C> componentType) {
        return componentType.cast(((IntegrationActivity) getActivity()).getComponent());
    }
}
