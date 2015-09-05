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

import org.addhen.smssync.data.entity.mapper.LogDataMapper;
import org.addhen.smssync.data.repository.datasource.log.LogDataSource;
import org.addhen.smssync.data.repository.datasource.log.LogDataSourceFactory;
import org.addhen.smssync.domain.entity.LogEntity;
import org.addhen.smssync.domain.repository.LogRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class LogDataRepository implements LogRepository {

    private final LogDataMapper mLogDataMapper;

    private final LogDataSourceFactory mLogDataSourceFactory;

    private LogDataSource mLogDataSource;

    @Inject
    public LogDataRepository(LogDataMapper logDataMapper,
            LogDataSourceFactory logDataSourceFactory) {
        mLogDataMapper = logDataMapper;
        mLogDataSourceFactory = logDataSourceFactory;
    }

    @Override
    public Observable<List<LogEntity>> getLogs() {
        mLogDataSource = mLogDataSourceFactory.createLogDataSource();
        return mLogDataSource.getLogs().map(mLogDataMapper::map);
    }

    @Override
    public Observable<Long> addLog(LogEntity logEntity) {
        mLogDataSource = mLogDataSourceFactory.createLogDataSource();
        return mLogDataSource.addLog(mLogDataMapper.map(logEntity));
    }

    @Override
    public Observable<Long> deleteLog() {
        mLogDataSource = mLogDataSourceFactory.createLogDataSource();
        return mLogDataSource.deleteLog();
    }

    @Override
    public Observable<LogEntity> getLog() {
        mLogDataSource = mLogDataSourceFactory.createLogDataSource();
        return mLogDataSource.getLog().map(log -> mLogDataMapper.map(log));
    }
}
