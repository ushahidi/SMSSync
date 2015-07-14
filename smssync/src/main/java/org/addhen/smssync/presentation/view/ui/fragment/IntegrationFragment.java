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
import org.addhen.smssync.presentation.view.ui.activity.MainActivity;

import butterknife.OnClick;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class IntegrationFragment extends BaseFragment {

    private static IntegrationFragment mIntegrationFragment;

    public IntegrationFragment() {
        super(R.layout.fragment_add_integration, 0);
    }

    public static IntegrationFragment newInstance() {
        if (mIntegrationFragment == null) {
            mIntegrationFragment = new IntegrationFragment();
        }
        return mIntegrationFragment;
    }

    @OnClick(R.id.twitter)
    void onTwitterClicked() {
        if (App.getTwitterIntance().getSessionManager().getActiveSession() == null) {
            App.getTwitterIntance().login(getActivity());
            return;
        }
        // Show profile
        ((MainActivity) getActivity()).replaceFragment(R.id.fragment_main_content,
                TwitterProfileFragment.newInstance(), "twitter_profile");
    }

    @OnClick(R.id.google_drive)
    void onGoogleDriveClicked() {
        // TODO: Launch View to sign into Google drive
        showSnabackar(getView(), "Twitter Google Drive Clicked");
    }

    @OnClick(R.id.ushahidi)
    void onUshahidiClicked() {
        // TODO: Launch View to sign into Ushahidi
        showSnabackar(getView(), "Twitter Ushahidi Clicked");
    }

    @OnClick(R.id.custom_web_service)
    void onCustomWebServiceClicked() {
        // TODO: Launch View to add custom web service
        showSnabackar(getView(), "Twitter Custom Service Clicked");
    }
}
