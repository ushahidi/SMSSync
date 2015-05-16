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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.addhen.smssync.BuildConfig;
import org.addhen.smssync.database.converter.EnumEntityFieldConverter;
import org.addhen.smssync.database.converter.SyncUrlConverter;
import org.addhen.smssync.models.Filter;
import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.SyncUrl;
import org.addhen.smssync.tasks.ThreadExecutor;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.EntityConverterFactory;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public abstract class BaseDatabseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smssync_db";

    private static final int DATABASE_VERSION = 8;

    private static final int LAST_DATABASE_NUKE_VERSION = 6;

    private static final Class[] ENTITIES = new Class[]{Message.class, SyncUrl.class,
            Filter.class, SyncUrl.class};
    private static String TAG = BaseDatabseHelper.class.getSimpleName();

    static {

        EntityConverterFactory factory = new EntityConverterFactory() {

            @Override
            public <T> EntityConverter<T> create(Cupboard cupboard, Class<T> type) {
                if (type == SyncUrl.class) {
                    return (EntityConverter<T>) new SyncUrlConverter(cupboard);
                }
                return null;
            }
        };

        CupboardFactory.setCupboard(new CupboardBuilder()
                .registerFieldConverter(Message.Status.class,
                        new EnumEntityFieldConverter<>(Message.Status.class))
                .registerFieldConverter(Message.Type.class,
                        new EnumEntityFieldConverter<>(Message.Type.class))
                .registerEntityConverterFactory(factory).useAnnotations().build());

        // Register our entities
        for (Class<?> clazz : ENTITIES) {
            cupboard().register(clazz);
        }
    }

    protected final ThreadExecutor mThreadExecutor;
    private boolean mIsClosed;
    private Context mContext;

    public BaseDatabseHelper(Context context, ThreadExecutor threadExecutor) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if (threadExecutor == null) {
            throw new IllegalArgumentException("Invalid null parameter");
        }

        mThreadExecutor = threadExecutor;
    }

    @Override
    public final void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrade database

        if (oldVersion < LAST_DATABASE_NUKE_VERSION) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Nuking Database. Old Version: " + oldVersion);
            }
            cupboard().withDatabase(db).dropAllTables();
            onCreate(db);
        } else {
            // This will upgrade tables, adding columns and new tables.
            // Note that existing columns will not be converted
            cupboard().withDatabase(db).upgradeTables();
            DatabaseUpgrade.upgradeToVersion8(db);
        }
    }

    /**
     * Close database connection
     */
    @Override
    public synchronized void close() {
        super.close();
        mIsClosed = true;
    }

    /**
     * Executes a {@link Runnable} in another Thread.
     *
     * @param runnable {@link Runnable} to execute
     */
    protected void asyncRun(Runnable runnable) {
        mThreadExecutor.execute(runnable);
    }

    protected boolean isClosed() {
        return mIsClosed;
    }

    public interface DatabaseCallback<T> {

        public void onFinished(T result);

        public void onError(Exception exception);
    }
}
