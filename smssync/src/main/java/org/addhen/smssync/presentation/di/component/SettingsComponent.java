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

package org.addhen.smssync.presentation.di.component;

import com.addhen.android.raiburari.presentation.di.module.ActivityModule;
import com.addhen.android.raiburari.presentation.di.qualifier.ActivityScope;

import org.addhen.smssync.presentation.di.module.SettingsModule;
import org.addhen.smssync.presentation.presenter.AddLogPresenter;
import org.addhen.smssync.presentation.view.ui.activity.BasePreferenceActivity;
import org.addhen.smssync.presentation.view.ui.fragment.BasePreferenceFragmentCompat;

import dagger.Component;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class,
        SettingsModule.class})
public interface SettingsComponent extends AppActivityComponent {

    void inject(BasePreferenceActivity generalSettingsActivity);

    void inject(BasePreferenceFragmentCompat basePreferenceFragmentCompat);

    AddLogPresenter addLogPresenter();
}
