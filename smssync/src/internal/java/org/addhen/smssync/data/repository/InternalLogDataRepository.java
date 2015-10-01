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

package org.addhen.smssync.data.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.addhen.smssync.domain.entity.LogEntity;
import org.addhen.smssync.domain.repository.LogRepository;

import android.content.Context;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class InternalLogDataRepository implements LogRepository {

    private Context mContext;

    @Inject
    public InternalLogDataRepository(Context context) {
        mContext = context;
    }

    @Override
    public Observable<List<LogEntity>> getLogs() {
        return Observable.defer(() -> {
            Type logList = new TypeToken<List<LogEntity>>() {
            }.getType();
            String json = loadJSONFromAsset("logs.json");
            List<LogEntity> logEntities = new ArrayList<>();
            try {
                logEntities = new Gson().fromJson(json, logList);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
            return Observable.just(logEntities);
        });
    }

    @Override
    public Observable<Long> addLog(LogEntity logEntity) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<Long> deleteLog() {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<LogEntity> getLog() {
        return Observable.defer(() -> {
            Type logList = new TypeToken<List<LogEntity>>() {
            }.getType();
            String json = loadJSONFromAsset("log.json");
            LogEntity logEntity = new LogEntity();
            try {
                logEntity = new Gson().fromJson(json, LogEntity.class);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
            return Observable.just(logEntity);
        });
    }


    private String loadJSONFromAsset(final String jsonFileName) {
        return DataHelper.loadJSONFromAsset(mContext, "logs/" + jsonFileName);
    }
}
