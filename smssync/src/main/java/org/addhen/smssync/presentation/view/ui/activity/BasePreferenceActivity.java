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

import com.addhen.android.raiburari.presentation.BaseApplication;
import com.addhen.android.raiburari.presentation.di.component.ApplicationComponent;
import com.addhen.android.raiburari.presentation.di.module.ActivityModule;

import org.addhen.smssync.R;
import org.addhen.smssync.presentation.App;
import org.addhen.smssync.presentation.di.component.AppActivityComponent;
import org.addhen.smssync.presentation.di.component.AppComponent;
import org.addhen.smssync.presentation.di.component.DaggerAppActivityComponent;
import org.addhen.smssync.presentation.di.component.DaggerSettingsComponent;
import org.addhen.smssync.presentation.di.component.SettingsComponent;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class BasePreferenceActivity extends AppCompatPreferenceActivity {

    protected AppActivityComponent mAppComponent;

    protected SettingsComponent mSettingsComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector();
        setupActionBar();
    }

    private void setupActionBar() {
        Toolbar toolbar;

        if (Build.VERSION.SDK_INT >= 14) {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.list).getParent().getParent()
                    .getParent();
            toolbar = (Toolbar) LayoutInflater.from(this)
                    .inflate(R.layout.toolbar_actionbar, root, false);
            root.addView(toolbar, 0);
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            root.removeAllViews();
            toolbar = (Toolbar) LayoutInflater.from(this)
                    .inflate(R.layout.toolbar_actionbar, root, false);
            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue
                        .complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = toolbar.getHeight();
            }
            content.setPadding(0, height, 0, 0);
            root.addView(content);
            root.addView(toolbar);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> navigateUp());
    }

    private boolean navigateUp() {
        final Intent intent = getParentIntent();
        if (intent != null) {
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        finish();
        return false;
    }

    private Intent getParentIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    protected void setToolbarTitle(@StringRes int stringRes) {
        setTitle(getString(R.string.settings_title_format, getString(R.string.action_settings),
                getString(stringRes)));
    }

    private void injector() {
        mAppComponent = DaggerAppActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        mSettingsComponent = DaggerSettingsComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    /**
     * Gets the Main Application component for dependency injection.
     *
     * @return {@link com.addhen.android.raiburari.presentation.di.component.ApplicationComponent}
     */
    public ApplicationComponent getApplicationComponent() {
        return ((BaseApplication) getApplication()).getApplicationComponent();
    }

    /**
     * Gets an Activity module for dependency injection.
     *
     * @return {@link com.addhen.android.raiburari.presentation.di.module.ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    /**
     * Gets the Main Application component for dependency injection.
     *
     * @return {@link com.addhen.android.raiburari.presentation.di.component.ApplicationComponent}
     */
    public AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    /**
     * A convenient method to get a string representation of the boolean value.
     *
     * @param status The boolean value
     * @return "Enabled" when true otherwise "Disabled"
     */
    protected String getCheckedStatus(boolean status) {
        if (status) {
            return getString(R.string.enabled);
        }
        return getString(R.string.disabled);
    }

    protected void showToast(@StringRes int stringResId) {
        Toast.makeText(BasePreferenceActivity.this, stringResId, Toast.LENGTH_LONG).show();
    }
}
