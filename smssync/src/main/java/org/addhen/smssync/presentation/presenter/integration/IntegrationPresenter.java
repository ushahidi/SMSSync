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

package org.addhen.smssync.presentation.presenter.integration;

import com.addhen.android.raiburari.domain.exception.DefaultErrorHandler;
import com.addhen.android.raiburari.domain.exception.ErrorHandler;
import com.addhen.android.raiburari.domain.usecase.DefaultSubscriber;
import com.addhen.android.raiburari.domain.usecase.Usecase;
import com.addhen.android.raiburari.presentation.presenter.Presenter;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.domain.entity.WebServiceEntity;
import org.addhen.smssync.presentation.exception.ErrorMessageFactory;
import org.addhen.smssync.presentation.service.CheckTaskService;
import org.addhen.smssync.presentation.service.ServiceControl;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.integration.IntegrationView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class IntegrationPresenter implements Presenter {

    private final Usecase mGetActiveWebServiceUsecase;


    private IntegrationView mIntegrationView;

    private PrefsFactory mPrefsFactory;

    private FileManager mFileManager;

    private PackageManager mPackageManager;

    private ComponentName mComponentName;

    private ServiceControl mServiceControl;

    @Inject
    public IntegrationPresenter(
            @Named("getActiveWebService") Usecase getActiveWebServiceUsecase,
            PrefsFactory prefsFactory, FileManager fileManager, ServiceControl serviceControl) {
        mGetActiveWebServiceUsecase = getActiveWebServiceUsecase;
        mPrefsFactory = prefsFactory;
        mFileManager = fileManager;
        mServiceControl = serviceControl;
    }

    public void setIntegrationView(IntegrationView integrationView) {
        mIntegrationView = integrationView;
    }

    public void setPackageManager(PackageManager packageManager) {
        mPackageManager = packageManager;
    }

    public void setSmsReceiverComponent(ComponentName componentName) {
        mComponentName = componentName;
    }

    @Override
    public void resume() {
        // Do nothing
    }

    @Override
    public void pause() {
        // Do nothing
    }

    @Override
    public void destroy() {
        mGetActiveWebServiceUsecase.unsubscribe();
    }

    public void loadActiveWebService() {
        mGetActiveWebServiceUsecase.execute(new DefaultSubscriber<List<WebServiceEntity>>() {

            @Override
            public void onNext(List<WebServiceEntity> webServiceList) {
                mIntegrationView.totalActiveWebService(webServiceList.size());
            }

            @Override
            public void onError(Throwable e) {
                showErrorMessage(new DefaultErrorHandler((Exception) e));
            }
        });
    }


    public void startSyncServices() {

        if (Utility.isDefaultSmsApp(mIntegrationView.getAppContext())) {
            mPackageManager.setComponentEnabledSetting(mComponentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            // Because the services to be run depends on the status of enabled services so save the
            // changes first
            mPrefsFactory.serviceEnabled().set(true);

            // Then enable the services
            // Run auto sync service
            mServiceControl.runAutoSyncService();

            // Run check task service
            mServiceControl.runCheckTaskService();

            // Show notification
            Utility.showNotification(mIntegrationView.getAppContext());
            return;
        }
        mPrefsFactory.serviceEnabled().set(false);
        Utility.makeDefaultSmsApp(mIntegrationView.getActivityContext());
    }

    public void stopSyncServices() {
        // Stop sms receiver
        mPackageManager.setComponentEnabledSetting(mComponentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        mServiceControl.stopCheckTaskService();
        mServiceControl.stopAutoSyncService();

        // Stop check task schedule
        mIntegrationView.getAppContext().stopService(
                new Intent(mIntegrationView.getAppContext(), CheckTaskService.class));
        mIntegrationView.getAppContext().stopService(
                new Intent(mIntegrationView.getAppContext(), CheckTaskService.class));

        Utility.clearNotify(mIntegrationView.getAppContext());
        mPrefsFactory.serviceEnabled().set(false);
    }

    public PrefsFactory getPrefsFactory() {
        return mPrefsFactory;
    }

    private void showErrorMessage(ErrorHandler errorHandler) {
        String errorMessage = ErrorMessageFactory.create(mIntegrationView.getAppContext(),
                errorHandler.getException());
        mIntegrationView.showError(errorMessage);
    }
}
