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
import org.addhen.smssync.data.entity.Message;

import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageDatabaseSource implements MessageDataSource {

    final MessageDatabaseHelper mMessageDatabaseHelper;

    public MessageDatabaseSource(@NonNull MessageDatabaseHelper messageDatabaseHelper) {
        mMessageDatabaseHelper = messageDatabaseHelper;
    }

    @Override
    public Observable<Integer> deleteByUuid(String uuid) {
        return mMessageDatabaseHelper.deleteByUuid(uuid);
    }

    @Override
    public Observable<Integer> deleteAll() {
        return mMessageDatabaseHelper.deleteAll();
    }

    @Override
    public Observable<List<Message>> fetchMessageByType(Message.Type type) {
        return mMessageDatabaseHelper.fetchMessageByType(type);
    }

    @Override
    public Observable<List<Message>> fetchMessageByStatus(Message.Status status) {
        return mMessageDatabaseHelper.fetchMessageByStatus(status);
    }

    @Override
    public Observable<List<Message>> fetchPending() {
        return mMessageDatabaseHelper.fetchPending();
    }

    @Override
    public Observable<List<Message>> getMessages() {
        return mMessageDatabaseHelper.getMessages();
    }

    @Override
    public Observable<Message> getMessage(Long id) {
        return mMessageDatabaseHelper.getMessage(id);
    }

    @Override
    public Observable<Long> put(Message message) {
        return mMessageDatabaseHelper.put(message);
    }

    @Override
    public Observable<Long> deleteEntity(Long id) {
        return mMessageDatabaseHelper.deleteEntity(id);
    }

    @Override
    public List<Message> fetchMessage(Message.Type type) {
        return mMessageDatabaseHelper.fetchMessage(type);
    }

    @Override
    public Message fetchMessageByUuid(String uuid) {
        return mMessageDatabaseHelper.fetchMessageByUuid(uuid);
    }

    @Override
    public void putMessage(Message message) {
        mMessageDatabaseHelper.putMessage(message);
    }

    @Override
    public void putMessages(List<Message> messages) {
        mMessageDatabaseHelper.putMessages(messages);
    }

    @Override
    public Integer deleteWithUuid(String uuid) {
        return mMessageDatabaseHelper.deleteWithUuid(uuid);
    }

    @Override
    public Message fetchByUuid(String uuid) {
        return mMessageDatabaseHelper.fetchMessageByUuid(uuid);
    }

    @Override
    public Message fetchPendingByUuid(String uuid) {
        return mMessageDatabaseHelper.fetchPendingMessageByUuid(uuid);
    }

    @Override
    public List<Message> syncFetchPending() {
        return mMessageDatabaseHelper.syncFetchPending();
    }
}
