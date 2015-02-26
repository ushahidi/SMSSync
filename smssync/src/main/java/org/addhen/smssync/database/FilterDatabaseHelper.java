package org.addhen.smssync.database;

import org.addhen.smssync.tasks.ThreadExecutor;

import android.content.Context;

import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class FilterDatabaseHelper extends BaseDatabseHelper implements FilterDatabase {

    private static FilterDatabaseHelper INSTANCE;


    public FilterDatabaseHelper(Context context, ThreadExecutor threadExecutor) {
        super(context, threadExecutor);
    }

    public static synchronized FilterDatabaseHelper getInstance(Context context,
            ThreadExecutor threadExecutor) {

        if (INSTANCE == null) {
            INSTANCE = new FilterDatabaseHelper(context, threadExecutor);
        }
        return INSTANCE;
    }

    @Override
    public void fetchAll(final DatabaseCallback<List<Filter>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {

                        List<Filter> filters = cupboard().withDatabase(getReadableDatabase())
                                .query(Filter.class).list();
                        callback.onFinished(filters);

                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchById(final Long id, final DatabaseCallback<Filter> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        Filter filter = cupboard().withDatabase(getReadableDatabase())
                                .get(Filter.class, id);
                        callback.onFinished(filter);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void fetchByStatus(final Filter.Status status,
            final DatabaseCallback<List<Filter>> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {

                        final String whereClause = "status= ?";
                        final List<Filter> filters = cupboard().withDatabase(getReadableDatabase())
                                .query(Filter.class)
                                .withSelection(whereClause, status.name()).orderBy("_id DESC")
                                .list();
                        callback.onFinished(filters);

                    } catch (Exception e) {

                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void put(final Filter filter, final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).put(filter);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void put(final List<Filter> filterLists, final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).put(filterLists);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteAll(final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).delete(Filter.class, null);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteById(final Long id, final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        cupboard().withDatabase(getWritableDatabase()).delete(Filter.class, id);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void total(final DatabaseCallback<Integer> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final Integer total = cupboard().withDatabase(getWritableDatabase())
                                .query(Filter.class).query().list().size();
                        callback.onFinished(total);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }
}
