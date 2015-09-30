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

import org.addhen.smssync.R;
import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.presentation.di.component.SettingsComponent;
import org.addhen.smssync.presentation.presenter.AddLogPresenter;
import org.addhen.smssync.presentation.service.ServiceControl;
import org.addhen.smssync.presentation.view.log.AddLogView;
import org.addhen.smssync.presentation.view.ui.activity.SettingsActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class BasePreferenceFragmentCompat extends PreferenceFragmentCompat implements
        AddLogView {

    @Inject
    PrefsFactory mPrefs;

    @Inject
    ServiceControl mServiceControl;

    @Inject
    AddLogPresenter mAddLogPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        getComponent(SettingsComponent.class).inject(this);
        mAddLogPresenter.setView(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the default white background in the view so as to avoid transparency
        view.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.background_material_light));

    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((SettingsActivity) getActivity()).getComponent());
    }

    /**
     * A convenient method to return boolean values to a more meaningful format
     *
     * @param status The boolean value
     * @return The meaningful format
     */
    protected String getCheckedStatus(boolean status) {
        if (status) {
            return getString(R.string.enabled);
        }
        return getString(R.string.disabled);
    }

    @Override
    public void onAdded(Long row) {
        // Do nothing
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getAppContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getAppContext() {
        return getContext();
    }
}
