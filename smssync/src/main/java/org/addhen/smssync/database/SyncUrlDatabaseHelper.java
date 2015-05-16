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

package org.addhen.smssync.database;


import android.content.Context;

import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.tasks.ThreadExecutor;

import java.util.List;

import nl.qbusict.cupboard.CupboardFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import static org.addhen.smssync.models.SyncUrl.Status;

public class SyncUrlDatabaseHelper extends BaseDatabseHelper implements SyncUrlDatabase {

    private static SyncUrlDatabaseHelper INSTANCE;


    public SyncUrlDatabaseHelper(Context context, ThreadExecutor threadExecutor) {
        super(context, threadExecutor);

    }

    public static synchronized SyncUrlDatabaseHelper getInstance(Context context,
                                                                 ThreadExecutor threadExecutor) {

        if (INSTANCE == null) {
            INSTANCE = new SyncUrlDatabaseHelper(context, threadExecutor);
        }
        return INSTANCE;
    }

    @Override
    public void fetchSyncUrl(final BaseDatabseHelper.DatabaseCallback<List<SyncUrl>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        List<SyncUrl> syncUrls = cupboard().withDatabase(getReadableDatabase()).query(SyncUrl.class).list();
                        callback.onFinished(syncUrls);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchSyncUrlById(final Long id,
                                 final BaseDatabseHelper.DatabaseCallback<SyncUrl> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        SyncUrl syncUrl = cupboard().withDatabase(getReadableDatabase()).get(SyncUrl.class, id);
                        callback.onFinished(syncUrl);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchSyncUrlByStatus(final SyncUrl.Status status,
                                     final BaseDatabseHelper.DatabaseCallback<List<SyncUrl>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {

                        final List<SyncUrl> syncUrls = getSyncUrlsQuery(status);
                        callback.onFinished(syncUrls);

                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    private List<SyncUrl> getSyncUrlsQuery(Status status) {
        final String whereClause = "status= ?";

        return CupboardFactory.cupboard().withDatabase(getReadableDatabase()).query(SyncUrl.class)
                .withSelection(whereClause, status.name()).orderBy("_id DESC").list();
    }

    public List<SyncUrl> fetchSyncUrlByStatus(Status status) {
        return getSyncUrlsQuery(status);
    }

    @Override
    public void put(final SyncUrl syncUrl, final BaseDatabseHelper.DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).put(syncUrl);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    public void put(final SyncUrl syncUrl) {
        if (!isClosed()) {
            cupboard().withDatabase(getWritableDatabase()).put(syncUrl);
        }
    }

    @Override
    public void put(final List<SyncUrl> syncUrls,
                    final BaseDatabseHelper.DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).put(syncUrls);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }


    @Override
    public void deleteAllSyncUrl(final BaseDatabseHelper.DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).delete(SyncUrl.class, null);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteSyncUrlById(final Long id, final BaseDatabseHelper.DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).delete(SyncUrl.class, id);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });

    }

    @Override
    public void totalActiveSyncUrl(final BaseDatabseHelper.DatabaseCallback<Integer> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "status= ?";
                        final Integer total = cupboard().withDatabase(getWritableDatabase()).query(SyncUrl.class)
                                .withSelection(whereClause, Status.ENABLED.name()).list().size();
                        callback.onFinished(total);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    public int getTotal() {
        if (!isClosed()) {
            return cupboard().withDatabase(getReadableDatabase()).query(SyncUrl.class).list().size();
        }
        return 0;
    }
}
