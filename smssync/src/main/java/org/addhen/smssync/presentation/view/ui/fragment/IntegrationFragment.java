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
import org.addhen.smssync.presentation.view.ui.activity.ListWebServiceActivity;
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;
import org.addhen.smssync.presentation.view.ui.navigation.Launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class IntegrationFragment extends BaseFragment implements IntegrationView {

    @BindView(R.id.start_service_checkbox)
    SwitchCompat mStartServiceCheckBox;

    @Inject
    IntegrationPresenter mIntegrationPresenter;

    @Inject
    Launcher mLauncher;

    public IntegrationFragment() {
        super(R.layout.fragment_integration, 0);
    }

    public static IntegrationFragment newInstance() {
        return new IntegrationFragment();
    }

    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mIntegrationPresenter != null) {
            mIntegrationPresenter.destroy();
        }
    }

    @OnClick(R.id.integration_twitter)
    void onTwitterClicked() {
        if (App.getTwitterInstance().getSessionManager().getActiveSession() == null) {
            App.getTwitterInstance().login(getActivity());
            return;
        }
        mLauncher.launchTwitterProfile();
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
        if (total > 0 || App.getTwitterInstance().getSessionManager().getActiveSession() != null) {
            startService();
            return;
        }
        showSnackbar(getView(), R.string.no_enabled_sync_url);
        mIntegrationPresenter.getPrefsFactory().serviceEnabled().set(false);
        mStartServiceCheckBox.setChecked(false);
    }

    @OnCheckedChanged(R.id.start_service_checkbox)
    void onChecked(boolean checked) {
        if (checked) {
            mIntegrationPresenter.loadActiveWebService();
            return;
        }
        mIntegrationPresenter.stopSyncServices();
    }

    @Override
    public void showError(String message) {
        showSnackbar(getView(), message);
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

    protected <C> C getIntegrationComponent(Class<C> componentType) {
        return componentType.cast(((MainActivity) getActivity()).getIntegrationComponent());
    }
}
