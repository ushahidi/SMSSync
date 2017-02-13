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

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Fragment for showing logged in user
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class TwitterProfileFragment extends BaseFragment {

    @BindView(R.id.twitter_logged_user)
    AppCompatTextView mLoggedInUser;

    public TwitterProfileFragment() {
        super(R.layout.fragment_twitter_profile, 0);
    }

    public static TwitterProfileFragment newInstance() {
        return new TwitterProfileFragment();
    }

    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        final String username = "@" + App.getTwitterInstance().getSessionManager()
                .getActiveSession().mUserName;
        mLoggedInUser.setText(username);
    }

    @OnClick(R.id.twitter_logout)
    void onTwitterClicked() {
        App.getTwitterInstance().logout();
        getActivity().finish();
    }
}
