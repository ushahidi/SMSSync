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

import org.addhen.smssync.presentation.model.FilterModel;
import org.addhen.smssync.presentation.view.ui.activity.GettingStartedActivity;
import org.addhen.smssync.presentation.view.ui.activity.IntegrationActivity;
import org.addhen.smssync.presentation.view.ui.activity.SettingsActivity;
import org.addhen.smssync.presentation.view.ui.fragment.FilterFragment;
import org.addhen.smssync.presentation.view.ui.fragment.LogFragment;
import org.addhen.smssync.presentation.view.ui.fragment.MessageFragment;
import org.addhen.smssync.presentation.view.ui.fragment.PublishedMessageFragment;

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

    public MessageFragment launchMessages() {
        return MessageFragment.newInstance();
    }

    public LogFragment launchLogs() {
        return LogFragment.newInstance();
    }

    public void launchIntegrations() {
        mActivity.startActivity(IntegrationActivity.getIntent(mActivity));
    }

    public FilterFragment launchFilters() {
        return FilterFragment.newInstance(FilterModel.Status.WHITELIST);
    }

    public PublishedMessageFragment launchPublishedMessages() {
        return PublishedMessageFragment.newInstance();
    }

    public void launchGettingStarted() {
        mActivity.startActivity(GettingStartedActivity.getIntent(mActivity));
    }
}
