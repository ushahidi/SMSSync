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

import org.addhen.smssync.data.entity.mapper.MessageDataMapper;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSourceFactory;
import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.domain.repository.MessageRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class MessageDataRepository implements MessageRepository {

    private final MessageDataMapper mMessageDataMapper;

    private final MessageDataSourceFactory mMessageDataSourceFactory;

    private MessageDataSource mMessageDataSource;

    @Inject
    public MessageDataRepository(MessageDataMapper messageDataMapper,
            MessageDataSourceFactory messageDataSourceFactory) {
        mMessageDataMapper = messageDataMapper;
        mMessageDataSourceFactory = messageDataSourceFactory;

    }

    @Override
    public Observable<Integer> deleteByUuid(String uuid) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.deleteByUuid(uuid);
    }

    @Override
    public Observable<Integer> deleteAll() {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.deleteAll();
    }

    @Override
    public Observable<List<MessageEntity>> fetchByType(MessageEntity.Type type) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.fetchMessageByType(mMessageDataMapper.map(type))
                .map((messageList -> mMessageDataMapper.map(messageList)));
    }

    @Override
    public Observable<List<MessageEntity>> fetchByStatus(MessageEntity.Status status) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.fetchMessageByStatus(mMessageDataMapper.map(status))
                .map((messageList -> mMessageDataMapper.map(messageList)));
    }

    @Override
    public Observable<MessageEntity> publishMessage(MessageEntity messageEntity) {
        return null;
    }

    @Override
    public Observable<List<MessageEntity>> getEntities() {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.getMessages()
                .map((messageList -> mMessageDataMapper.map(messageList)));
    }

    @Override
    public Observable<MessageEntity> getEntity(Long id) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.getMessage(id).map(message -> mMessageDataMapper.map(message));
    }

    @Override
    public Observable<Long> addEntity(MessageEntity messageEntity) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.put(mMessageDataMapper.map(messageEntity));
    }

    @Override
    public Observable<Long> updateEntity(MessageEntity messageEntity) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.put(mMessageDataMapper.map(messageEntity));
    }

    @Override
    public Observable<Long> deleteEntity(Long id) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDataSource();
        return mMessageDataSource.deleteEntity(id);
    }
}
