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

import org.addhen.smssync.data.entity.SyncUrl;
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
     * @return An Observable that emits a {@link SyncUrl}
     */
    public Observable<List<SyncUrl>> getByStatus(final SyncUrl.Status status) {
        return Observable.create((subscriber) -> {
            final List<SyncUrl> syncUrlEntity = get(status);
            if (syncUrlEntity != null) {
                subscriber.onNext(syncUrlEntity);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new WebServiceNotFoundException());
            }
        });
    }

    /**
     * Gets webService lists
     *
     * @return An Observable that emits a list of {@link SyncUrl}
     */
    public Observable<List<SyncUrl>> getWebServices() {
        return Observable.create(subscriber -> {
            final List<SyncUrl> syncUrls = cupboard()
                    .withDatabase(getReadableDatabase()).query(SyncUrl.class).list();
            if (syncUrls != null) {
                subscriber.onNext(syncUrls);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new WebServiceNotFoundException());
            }
        });
    }

    /**
     * Gets a webService
     *
     * @param id The ID of the webService to retrieve
     * @return An Observable that emits a {@link SyncUrl}
     */
    public Observable<SyncUrl> getWebService(Long id) {
        return Observable.create(subscriber -> {
            final SyncUrl syncUrlEntity = cupboard().withDatabase(getReadableDatabase())
                    .query(SyncUrl.class)
                    .byId(id).get();
            if (syncUrlEntity != null) {
                subscriber.onNext(syncUrlEntity);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new WebServiceNotFoundException());
            }
        });
    }

    /**
     * Saves a {@link SyncUrl} into the db
     *
     * @param syncUrlEntity The webService to save to the db
     * @return The row affected
     */
    public Observable<Long> put(SyncUrl syncUrlEntity) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                Long row = null;
                try {
                    row = cupboard().withDatabase(getWritableDatabase()).put(syncUrlEntity);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(row);
                subscriber.onCompleted();
            }

        });
    }

    /**
     * Deletes a {@link SyncUrl} from the db
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
                            .delete(SyncUrl.class, webServiceId);
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

    public List<SyncUrl> get(final SyncUrl.Status status) {
        final List<SyncUrl> syncUrls = cupboard()
                .withDatabase(getReadableDatabase()).query(SyncUrl.class)
                .withSelection("status = ?", status.name()).list();
        return syncUrls;
    }

    public List<SyncUrl> listWebServices() {
        return cupboard().withDatabase(getReadableDatabase()).query(SyncUrl.class).list();
    }
}
