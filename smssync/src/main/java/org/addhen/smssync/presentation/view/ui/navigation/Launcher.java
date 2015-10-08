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

package org.addhen.smssync.presentation.view.ui.navigation;

import org.addhen.smssync.presentation.model.WebServiceModel;
import org.addhen.smssync.presentation.view.ui.activity.AddKeywordsActivity;
import org.addhen.smssync.presentation.view.ui.activity.AddPhoneNumberActivity;
import org.addhen.smssync.presentation.view.ui.activity.AddTwitterKeywordsActivity;
import org.addhen.smssync.presentation.view.ui.activity.AddWebServiceActivity;
import org.addhen.smssync.presentation.view.ui.activity.GettingStartedActivity;
import org.addhen.smssync.presentation.view.ui.activity.ListWebServiceActivity;
import org.addhen.smssync.presentation.view.ui.activity.SettingsActivity;
import org.addhen.smssync.presentation.view.ui.activity.TwitterProfileActivity;
import org.addhen.smssync.presentation.view.ui.activity.UpdateWebServiceActivity;

import android.app.Activity;
import android.content.Intent;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Launcher {

    private final Activity mActivity;

    @Inject
    public Launcher(Activity activity) {
        mActivity = activity;
    }

    public void launchSettings() {
        Intent intent = SettingsActivity.getIntent(mActivity);
        mActivity.startActivity(intent);
    }

    public void launchGettingStarted() {
        mActivity.startActivity(GettingStartedActivity.getIntent(mActivity));
    }

    public void launchListWebServices() {
        mActivity.startActivity(ListWebServiceActivity.getIntent(mActivity));
    }

    public void launchAddWebServices() {
        mActivity.startActivity(AddWebServiceActivity.getIntent(mActivity));
    }

    public void launchUpdateWebServices(WebServiceModel webServiceModel) {
        mActivity.startActivity(UpdateWebServiceActivity.getIntent(mActivity, webServiceModel));
    }

    /**
     * Launches the barcode reader
     */
    public void launchTwitterProfile() {
        mActivity.startActivity(TwitterProfileActivity.getIntent(mActivity));
    }

    /**
     * Launches activity for adding a new phone number
     */
    public void launchAddPhoneNumber() {
        mActivity.startActivity(AddPhoneNumberActivity.getIntent(mActivity));
    }

    /**
     * Launches activity for adding a new keyword
     */
    public void launchAddKeyword(WebServiceModel webServiceModel) {
        mActivity.startActivity(AddKeywordsActivity.getIntent(mActivity, webServiceModel));
    }

    /**
     * Launches activity for adding a new keyword for twitter service
     */
    public void launchAddTwitterKeyword() {
        mActivity.startActivity(AddTwitterKeywordsActivity.getIntent(mActivity));
    }
}
