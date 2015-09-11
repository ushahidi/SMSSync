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

package org.addhen.smssync.data.database;

import org.addhen.smssync.data.entity.WebService;
import org.addhen.smssync.data.exception.WebServiceNotFoundException;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class WebServiceDatabaseHelper extends BaseDatabaseHelper {

    /**
     * Default constructor
     *
     * @param context The calling context. Cannot be a null value
     */
    @Inject
    public WebServiceDatabaseHelper(@NonNull Context context) {
        super(context);
    }

    /**
     * Gets webServices by it's status
     *
     * @param status The status to use to query for the webService
     * @return An Observable that emits a {@link WebService}
     */
    public Observable<List<WebService>> getByStatus(final WebService.Status status) {
        return Observable.create((subscriber) -> {
            final List<WebService> webServiceEntity = get(status);
            if (webServiceEntity != null) {
                subscriber.onNext(webServiceEntity);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new WebServiceNotFoundException());
            }
        });
    }

    /**
     * Gets webService lists
     *
     * @return An Observable that emits a list of {@link WebService}
     */
    public Observable<List<WebService>> getWebServices() {
        return Observable.create(subscriber -> {
            final List<WebService> webServices = cupboard()
                    .withDatabase(getReadableDatabase()).query(WebService.class).list();
            if (webServices != null) {
                subscriber.onNext(webServices);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new WebServiceNotFoundException());
            }
        });
    }

    public List<WebService> listWebServices() {
        return cupboard()
                .withDatabase(getReadableDatabase()).query(WebService.class).list();
    }

    /**
     * Gets a webService
     *
     * @param id The ID of the webService to retrieve
     * @return An Observable that emits a {@link WebService}
     */
    public Observable<WebService> getWebService(Long id) {
        return Observable.create(subscriber -> {
            final WebService webServiceEntity = cupboard().withDatabase(getReadableDatabase())
                    .query(WebService.class)
                    .byId(id).get();
            if (webServiceEntity != null) {
                subscriber.onNext(webServiceEntity);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new WebServiceNotFoundException());
            }
        });
    }

    /**
     * Saves a {@link WebService} into the db
     *
     * @param webServiceEntity The webService to save to the db
     * @return The row affected
     */
    public Observable<Long> put(WebService webServiceEntity) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                Long row = null;
                try {
                    row = cupboard().withDatabase(getWritableDatabase()).put(webServiceEntity);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(row);
                subscriber.onCompleted();
            }

        });
    }

    /**
     * Deletes a {@link WebService} from the db
     *
     * @param webServiceId The webService to be deleted from the db
     * @return The row affected. One means successful otherwise it triggers an error
     */
    public Observable<Long> deleteWebService(Long webServiceId) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                boolean deleted = false;
                try {
                    deleted = cupboard().withDatabase(getWritableDatabase())
                            .delete(WebService.class, webServiceId);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (deleted) {
                    subscriber.onNext(1l);
                } else {
                    subscriber.onError(new Exception());
                }
                subscriber.onCompleted();
            }

        });
    }

    private List<WebService> get(final WebService.Status status) {
        final List<WebService> webServices = cupboard()
                .withDatabase(getReadableDatabase()).query(WebService.class)
                .withSelection("status = ?", status.name()).list();
        return webServices;
    }
}
