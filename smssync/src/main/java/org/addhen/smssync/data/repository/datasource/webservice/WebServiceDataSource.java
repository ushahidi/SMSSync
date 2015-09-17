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

import org.addhen.smssync.data.entity.WebService;

import java.util.List;

import rx.Observable;

/**
 * All different source providers must implement this interface to provide deployment data
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface WebServiceDataSource {

    /**
     * Get an {@link Observable} which will emit a List of {@link WebService}.
     *
     * @return Observable that emits list of {@link WebService}
     */
    Observable<List<WebService>> getWebServiceList();

    /**
     * Get an {@link Observable} which will emit a {@link WebService} by its id.
     *
     * @param deploymentId The id to retrieve user data.
     * @return Observable that emits a {@link WebService}
     */
    Observable<WebService> getWebService(Long deploymentId);

    /**
     * Gets an {@link Observable} which will emit a {@link WebService} by its status.
     *
     * @param status The deployment status to be used for retrieving a deployment
     * @return Observable that emits a list of {@link WebService}
     */
    Observable<List<WebService>> getByStatus(WebService.Status status);

    /**
     * Gets a list of {@link WebService} by its status.
     *
     * @param status The deployment status to be used for retrieving a deployment
     * @return A list of {@link WebService}
     */
    List<WebService> syncGetByStatus(WebService.Status status);

    /**
     * Adds an {@link WebService} to storage and then returns an {@link Observable} for all
     * subscribers
     * to react to it.
     *
     * @param deployment The deployment to be added
     * @return The row affected
     */
    Observable<Long> addWebService(WebService deployment);

    /**
     * Adds an {@link WebService} to storage and then returns an {@link Observable} for all
     * subscribers
     * to react to it.
     *
     * @param deployment The deployment to be added
     * @return The row affected
     */
    Observable<Long> updateWebService(WebService deployment);

    /**
     * Deletes an {@link WebService} from storage and then returns an {@link Observable} for
     * all
     * subscribers to react to it.
     *
     * @param deploymentId The deployment to be deleted
     * @return The row affected
     */
    Observable<Long> deleteWebService(Long deploymentId);

    List<WebService> get(final WebService.Status status);

    List<WebService> listWebServices();
}
