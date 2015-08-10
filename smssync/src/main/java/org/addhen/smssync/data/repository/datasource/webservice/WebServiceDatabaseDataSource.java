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

package org.addhen.smssync.data.repository.datasource.webservice;


import android.support.annotation.NonNull;

import org.addhen.smssync.data.entity.WebService;

import java.util.List;

import rx.Observable;

/**
 * Retrieves and adds a webService data to the database
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class WebServiceDatabaseDataSource implements WebServiceDataSource {

    private final WebServiceDatabaseHelper mWebServiceDatabaseHelper;

    /**
     * Default constructor
     *
     * @param webServiceDatabaseHelper The webService database helper
     */
    public WebServiceDatabaseDataSource(
            @NonNull WebServiceDatabaseHelper webServiceDatabaseHelper) {
        mWebServiceDatabaseHelper = webServiceDatabaseHelper;
    }

    @Override
    public Observable<List<WebService>> getWebServiceEntityList() {
        return mWebServiceDatabaseHelper.getWebServices();
    }

    @Override
    public Observable<WebService> getWebServiceEntity(Long webServiceId) {
        return mWebServiceDatabaseHelper.getWebService(webServiceId);
    }

    @Override
    public Observable<WebService> getByStatus(WebService.Status status) {
        return mWebServiceDatabaseHelper.getByStatus(status);
    }

    @Override
    public Observable<Long> addWebServiceEntity(WebService webService) {
        return mWebServiceDatabaseHelper.put(webService);
    }

    @Override
    public Observable<Long> updateWebServiceEntity(WebServiceEntity webService) {
        return mWebServiceDatabaseHelper.put(webService);
    }

    @Override
    public Observable<Long> deleteWebServiceEntity(Long webServiceId) {
        return mWebServiceDatabaseHelper.deleteWebService(webServiceId);
    }
}
