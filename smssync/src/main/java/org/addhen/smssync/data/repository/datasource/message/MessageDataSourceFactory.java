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

package org.addhen.smssync.data.repository.datasource.message;

import org.addhen.smssync.data.database.MessageDatabaseHelper;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class MessageDataSourceFactory {

    private final MessageDatabaseHelper mMessageDatabaseHelper;

    @Inject
    MessageDataSourceFactory(@NonNull MessageDatabaseHelper messageDatabaseHelper) {
        mMessageDatabaseHelper = messageDatabaseHelper;
    }

    public MessageDataSource createMessageDatabaseSource() {
        return new MessageDatabaseSource(mMessageDatabaseHelper);
    }
}
