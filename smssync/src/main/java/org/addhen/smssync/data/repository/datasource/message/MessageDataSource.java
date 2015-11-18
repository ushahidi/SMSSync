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

import org.addhen.smssync.data.entity.Message;

import java.util.List;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface MessageDataSource {

    Observable<Integer> deleteByUuid(String uuid);

    Observable<Integer> deleteAll();

    Observable<List<Message>> fetchMessageByType(Message.Type type);

    Observable<List<Message>> fetchMessageByStatus(Message.Status status);

    Observable<List<Message>> fetchPending();

    Observable<List<Message>> getMessages();

    Observable<Message> getMessage(Long id);

    Observable<Long> put(Message message);

    Observable<Long> deleteEntity(Long id);

    List<Message> fetchMessage(Message.Type type);

    Message fetchMessageByUuid(String uuid);

    void putMessage(Message message);

    void putMessages(List<Message> messages);

    Integer deleteWithUuid(String uuid);

    Message fetchByUuid(String uuid);

    Message fetchPendingByUuid(String uuid);

    List<Message> syncFetchPending();
}
