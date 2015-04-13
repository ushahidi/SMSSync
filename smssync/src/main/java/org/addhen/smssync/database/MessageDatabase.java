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

import org.addhen.smssync.models.Message;

import java.util.List;

import static org.addhen.smssync.database.BaseDatabseHelper.DatabaseCallback;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface MessageDatabase {

    public void put(List<Message> messages, DatabaseCallback<Void> callback);

    public void put(Message message, DatabaseCallback<Void> callback);

    public void deleteByUuid(String uuid, DatabaseCallback<Void> callback);

    public void deleteAll(DatabaseCallback<Void> callback);

    public void fetchByType(Message.Type type, DatabaseCallback<List<Message>> callback);

    public void fetchByStatus(Message.Status status, DatabaseCallback<List<Message>> callback);

    public void fetchByUuid(String uuid, DatabaseCallback<Message> callback);

    public void fetchByUuids(List<String> uuid, DatabaseCallback<List<Message>> callback);

    public void fetchAll(DatabaseCallback<List<Message>> callback);

    public void fetchByLimit(int limit, DatabaseCallback<List<Message>> callback);

    public void fetchPending(DatabaseCallback<List<Message>> callback);

    public void fetchSent(DatabaseCallback<List<Message>> callback);

    public void deleteAllSentMessages(final DatabaseCallback<Void> callback);
}
