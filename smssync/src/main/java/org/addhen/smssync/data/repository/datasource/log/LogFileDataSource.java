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

package org.addhen.smssync.data.repository.datasource.log;

import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.Log;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class LogFileDataSource implements LogDataSource {

    private final FileManager mFileManager;

    @Inject
    public LogFileDataSource(FileManager fileManager) {
        mFileManager = fileManager;
    }

    @Override
    public Observable<List<Log>> getLogs() {
        return mFileManager.getLogs();
    }

    @Override
    public Observable<Long> addLog(Log log) {
        return mFileManager.addLog(log);
    }

    @Override
    public Observable<Long> deleteLog() {
        return mFileManager.deleteLog();
    }

    @Override
    public Observable<Log> getLog() {
        return mFileManager.getLog();
    }
}
