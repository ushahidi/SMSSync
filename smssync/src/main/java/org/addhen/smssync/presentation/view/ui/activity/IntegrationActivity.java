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

import com.addhen.android.raiburari.presentation.di.HasComponent;
import com.addhen.android.raiburari.presentation.ui.activity.BaseActivity;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.di.component.AppComponent;
import org.addhen.smssync.presentation.di.component.DaggerIntegrationComponent;
import org.addhen.smssync.presentation.di.component.IntegrationComponent;
import org.addhen.smssync.presentation.util.Utility;
import org.addhen.smssync.presentation.view.ui.fragment.AddWebServiceFragment;
import org.addhen.smssync.presentation.view.ui.fragment.IntegrationFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Renders {@link AddWebServiceFragment}
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class IntegrationActivity extends BaseActivity
        implements HasComponent<IntegrationComponent> {

    private static final String FRAG_TAG = "integration";

    private IntegrationFragment mIntegrationFragment;

    private IntegrationComponent mIntegrationComponent;

    /**
     * Default constructor
     */
    public IntegrationActivity() {
        super(R.layout.activity_integration, 0);
    }

    /**
     * Provides {@link Intent} launching this activity
     *
     * @param context The calling context
     * @return The intent to be launched
     */
    public static Intent getIntent(final Context context) {
        return new Intent(context, IntegrationActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector();
        mIntegrationFragment = (IntegrationFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG);
        if (mIntegrationFragment == null) {
            mIntegrationFragment = IntegrationFragment.newInstance();
            replaceFragment(R.id.integration_fragment_container, mIntegrationFragment, FRAG_TAG);
        }
    }


    private void injector() {
        getAppComponent().inject(this);
        mIntegrationComponent = DaggerIntegrationComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    /**
     * Gets the Main Application component for dependency injection.
     *
     * @return {@link com.addhen.android.raiburari.presentation.di.component.ApplicationComponent}
     */
    public AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    @Override
    public IntegrationComponent getComponent() {
        return mIntegrationComponent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Utility.SET_DEFAULT_SMS_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                mIntegrationFragment.startService();
                mIntegrationFragment.getStartServiceCheckBox().setChecked(true);
            } else {
                mIntegrationFragment.getStartServiceCheckBox().setChecked(false);
            }
        }
    }
}
