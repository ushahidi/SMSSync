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

package org.addhen.smssync.data.entity.mapper;

import org.addhen.smssync.data.entity.Log;
import org.addhen.smssync.domain.entity.LogEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class LogDataMapper {

    @Inject
    public LogDataMapper() {
        // Do nothing
    }

    public LogEntity map(Log log) {
        LogEntity logEntity = null;
        if (log != null) {
            logEntity = new LogEntity();
            logEntity.setId(log.getId());
            logEntity.setMessage(log.getMessage());
        }
        return logEntity;
    }

    public Log map(LogEntity logEntity) {
        Log log = null;
        if (logEntity != null) {
            log = new Log();
            log._id = logEntity._id;
            log.setMessage(logEntity.getMessage());
        }
        return log;
    }

    public List<LogEntity> map(List<Log> logList) {
        List<LogEntity> logEntityList = new ArrayList<>();
        LogEntity logEntity;
        for (Log log : logList) {
            logEntity = map(log);
            if (logEntity != null) {
                logEntityList.add(logEntity);
            }
        }
        return logEntityList;
    }
}
