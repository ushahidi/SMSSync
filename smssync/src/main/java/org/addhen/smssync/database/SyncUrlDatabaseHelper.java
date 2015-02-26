package org.addhen.smssync.database;


import org.addhen.smssync.tasks.ThreadExecutor;

import android.content.Context;

import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import static org.addhen.smssync.database.SyncUrl.Status;

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
                if(!isClosed()) {
                    try {
                        List<SyncUrl> syncUrls = cupboard().withDatabase(getReadableDatabase()).query(SyncUrl.class).list();
                        callback.onFinished(syncUrls);
                    } catch(Exception e) {
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
                if(!isClosed()) {
                    try {
                        SyncUrl syncUrl = cupboard().withDatabase(getReadableDatabase()).get(SyncUrl.class, id);
                        callback.onFinished(syncUrl);
                    } catch(Exception e) {
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
                if(!isClosed()) {
                    try {

                        final String whereClause = "status= ?";

                        final List<SyncUrl> syncUrls = cupboard().withDatabase(getReadableDatabase()).query(SyncUrl.class)
                                .withSelection(whereClause, status.name()).orderBy("_id DESC").list();

                        callback.onFinished(syncUrls);
                    } catch(Exception e) {

                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void put(final SyncUrl syncUrl, final BaseDatabseHelper.DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if(!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).put(syncUrl);
                        callback.onFinished(null);
                    }catch(Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void put(final List<SyncUrl> syncUrls,
            final BaseDatabseHelper.DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if(!isClosed()) {
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
                        final Integer total = cupboard().withDatabase(getWritableDatabase()).query(SyncUrl.class).query().list().size();
                        callback.onFinished(total);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }
}
