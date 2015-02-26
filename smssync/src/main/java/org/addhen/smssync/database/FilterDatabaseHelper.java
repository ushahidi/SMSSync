package org.addhen.smssync.database;

import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
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
    public void deleteAllBlackList(final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "status= ?";
                        final String whereArgs[] = {Filter.Status.BLACKLIST.name()};

                        cupboard().withDatabase(getWritableDatabase()).delete(Filter.class, whereClause, whereArgs);
                        callback.onFinished(null);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
            }
        });
    }

    @Override
    public void deleteAllWhiteList(final DatabaseCallback<Void> callback) {
        asyncRun(new Runnable() {
            @Override
            public void run() {
                if (!isClosed()) {
                    try {
                        final String whereClause = "status= ?";
                        final String whereArgs[] = {Filter.Status.WHITELIST.name()};

                        cupboard().withDatabase(getWritableDatabase()).delete(Filter.class, whereClause, whereArgs);
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

    public int getWhiteListTotal() {
        final List<Filter> filters = fetchByStatus(Filter.Status.WHITELIST);

        return getSize(filters);
    }

    public int getBlackListTotal() {
        final List<Filter> filters = fetchByStatus(Filter.Status.BLACKLIST);

        return getSize(filters);
    }

    private int getSize(List<Filter> filters) {
        if(filters !=null) {
            return filters.size();
        }

        return 0;
    }

    public List<Filter> fetchByStatus(Filter.Status status) {
        if(!isClosed()) {
            try {
                final String whereClause = "status= ?";
                return cupboard().withDatabase(getReadableDatabase())
                        .query(Filter.class)
                        .withSelection(whereClause, status.name()).orderBy("_id DESC")
                        .list();
            }catch (Exception e) {
                return  null;
            }
        }
        return null;
    }
}
