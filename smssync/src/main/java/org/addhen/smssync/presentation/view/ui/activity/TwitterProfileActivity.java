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

package org.addhen.smssync.presentation.view.ui.activity;

import com.addhen.android.raiburari.presentation.ui.activity.BaseActivity;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.view.ui.fragment.AddWebServiceFragment;
import org.addhen.smssync.presentation.view.ui.fragment.TwitterProfileFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Renders {@link AddWebServiceFragment}
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class TwitterProfileActivity extends BaseActivity {

    private static final String FRAG_TAG = "twitter_profile";

    private TwitterProfileFragment mTwitterProfileFragment;


    /**
     * Default constructor
     */
    public TwitterProfileActivity() {
        super(R.layout.activity_twitter_profile, 0);
    }

    /**
     * Provides {@link Intent} launching this activity
     *
     * @param context The calling context
     * @return The intent to be launched
     */
    public static Intent getIntent(final Context context) {
        return new Intent(context, TwitterProfileActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwitterProfileFragment = (TwitterProfileFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG);
        if (mTwitterProfileFragment == null) {
            mTwitterProfileFragment = TwitterProfileFragment.newInstance();
            replaceFragment(R.id.twitter_profile_fragment_container, mTwitterProfileFragment,
                    FRAG_TAG);
        }
    }
}
