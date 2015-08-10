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

package org.addhen.smssync.domain.repository;

import com.addhen.android.raiburari.domain.repository.Repository;

import org.addhen.smssync.domain.entity.WebServiceEntity;

import rx.Observable;

/**
 * Repository for manipulating {@link org.addhen.smssync.domain.entity.WebServiceEntity} data
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface WebServiceRepository extends Repository<WebServiceEntity> {

    /**
     * Get an {@link WebServiceEntity} by its status.
     *
     * @param status The web service status to be used for retrieving web service data.
     * @return The web service
     */
    Observable<WebServiceEntity> getByStatus(WebServiceEntity.Status status);
}
