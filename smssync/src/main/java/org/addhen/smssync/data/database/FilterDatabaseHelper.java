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

import org.addhen.smssync.data.entity.Filter;
import org.addhen.smssync.data.exception.FilterNotFoundException;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Database Helper for filters
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class FilterDatabaseHelper extends BaseDatabaseHelper {

    @Inject
    public FilterDatabaseHelper(@NonNull Context context) {
        super(context);
    }

    public Observable<List<Filter>> getFilterList() {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                List<Filter> filters = null;
                try {
                    filters = cupboard().withDatabase(getReadableDatabase())
                            .query(Filter.class).list();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (filters != null) {
                    subscriber.onNext(filters);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new FilterNotFoundException());
                }
            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public Observable<Filter> getFilter(@NonNull Long id) {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                Filter filter = cupboard().withDatabase(getReadableDatabase())
                        .get(Filter.class, id);
                if (filter != null) {
                    subscriber.onNext(filter);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new FilterNotFoundException());
                }
            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public Observable<List<Filter>> fetchByStatus(@NonNull Filter.Status status) {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                final String whereClause = "status= ?";
                List<Filter> filters = cupboard().withDatabase(getReadableDatabase())
                        .query(Filter.class)
                        .withSelection(whereClause, status.name()).orderBy("_id DESC")
                        .list();
                if (filters != null) {
                    subscriber.onNext(filters);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new FilterNotFoundException());
                }

            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public Observable<Boolean> put(@NonNull List<Filter> filters) {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                try {
                    cupboard().withDatabase(getWritableDatabase()).put(filters);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(true);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public Observable<Long> put(@NonNull Filter filter) {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                Long row = null;
                try {
                    row = cupboard().withDatabase(getWritableDatabase()).put(filter);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(row);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public Observable<Integer> deleteAllBlackList() {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                Integer row = null;
                final String whereClause = "status= ?";
                final String whereArgs[] = {Filter.Status.BLACKLIST.name()};
                try {
                    row = cupboard().withDatabase(getWritableDatabase())
                            .delete(Filter.class, whereClause, whereArgs);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(row);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public Observable<Integer> deleteAllWhiteList() {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                Integer row = null;
                final String whereClause = "status= ?";
                final String whereArgs[] = {Filter.Status.WHITELIST.name()};
                try {
                    row = cupboard().withDatabase(getWritableDatabase())
                            .delete(Filter.class, whereClause, whereArgs);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(row);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public Observable<Long> deleteById(final Long id) {
        return Observable.create((subscriber -> {
            if (!isClosed()) {
                try {
                    cupboard().withDatabase(getWritableDatabase()).delete(Filter.class, id);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(1l);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        }));
    }

    public List<Filter> getFilters() {
        return cupboard().withDatabase(getReadableDatabase()).query(Filter.class).list();
    }
}
