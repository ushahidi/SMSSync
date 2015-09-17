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

import org.addhen.smssync.presentation.di.module.WebServiceModule;
import org.addhen.smssync.presentation.presenter.webservice.AddWebServicePresenter;
import org.addhen.smssync.presentation.presenter.webservice.DeleteWebServicePresenter;
import org.addhen.smssync.presentation.presenter.webservice.ListWebServicePresenter;
import org.addhen.smssync.presentation.presenter.webservice.UpdateWebServiceKeywordsPresenter;
import org.addhen.smssync.presentation.presenter.webservice.UpdateWebServicePresenter;
import org.addhen.smssync.presentation.view.ui.activity.AddWebServiceActivity;
import org.addhen.smssync.presentation.view.ui.activity.ListWebServiceActivity;
import org.addhen.smssync.presentation.view.ui.activity.UpdateWebServiceActivity;
import org.addhen.smssync.presentation.view.ui.fragment.AddKeywordFragment;
import org.addhen.smssync.presentation.view.ui.fragment.AddWebServiceFragment;
import org.addhen.smssync.presentation.view.ui.fragment.ListWebServiceFragment;
import org.addhen.smssync.presentation.view.ui.fragment.UpdateWebServiceFragment;

import dagger.Component;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class,
        WebServiceModule.class})
public interface WebServiceComponent extends AppActivityComponent {

    /**
     * Injects {@link AddWebServiceActivity}
     *
     * @param addWebServiceActivity The webService activity
     */
    void inject(AddWebServiceActivity addWebServiceActivity);

    /**
     * Injects {@link AddWebServiceFragment}
     *
     * @param addWebServiceFragment The webService fragment
     */
    void inject(AddWebServiceFragment addWebServiceFragment);


    /**
     * Injects {@link ListWebServiceActivity}
     *
     * @param listWebServiceActivity The list webService activity
     */
    void inject(ListWebServiceActivity listWebServiceActivity);

    /**
     * Injects {@link UpdateWebServiceActivity}
     *
     * @param updateWebServiceActivity The update webService activity
     */
    void inject(UpdateWebServiceActivity updateWebServiceActivity);


    /**
     * Injects {@link ListWebServiceFragment}
     *
     * @param listWebServiceFragment The list webService fragment
     */
    void inject(ListWebServiceFragment listWebServiceFragment);

    /**
     * Injects {@link UpdateWebServiceFragment}
     *
     * @param updateWebServiceFragment The update webService fragment
     */
    void inject(UpdateWebServiceFragment updateWebServiceFragment);


    /**
     * Injects {@link AddKeywordFragment}
     *
     * @param addKeywordFragment The fragment for adding keyword
     */
    void inject(AddKeywordFragment addKeywordFragment);

    /**
     * Provides {@link UpdateWebServicePresenter} to the sub-graph
     *
     * @return The update webService presenter
     */
    UpdateWebServicePresenter updateWebServicePresenter();

    /**
     * Provides {@link UpdateWebServiceKeywordsPresenter} to the sub-graph
     *
     * @return The update webService presenter
     */
    UpdateWebServiceKeywordsPresenter updateWebServiceKeywordsPresenter();


    /**
     * Provides {@link ListWebServicePresenter} to the sub-graph
     *
     * @return The list webService presenter
     */
    ListWebServicePresenter listWebServicePresenter();

    /**
     * Provides {@link DeleteWebServicePresenter}
     *
     * @return The delete webService presenter
     */
    DeleteWebServicePresenter deleteWebServicePresenter();


    /**
     * Provides {@link AddWebServicePresenter} to sub-graph
     *
     * @return The add webService presenter
     */
    AddWebServicePresenter addWebServicePresenter();

}
