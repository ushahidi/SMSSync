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

import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.mapper.MessageDataMapper;
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.data.message.TweetMessage;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSourceFactory;
import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.smslib.sms.ProcessSms;

import java.util.ArrayList;
import java.util.Arrays;
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

    private PostMessage mPostMessage;

    private TweetMessage mTweetMessage;

    @Inject
    public MessageDataRepository(MessageDataMapper messageDataMapper,
            MessageDataSourceFactory messageDataSourceFactory,
            PostMessage postMessage,
            TweetMessage tweetMessage) {
        mMessageDataMapper = messageDataMapper;
        mMessageDataSourceFactory = messageDataSourceFactory;
        mPostMessage = postMessage;
        mTweetMessage = tweetMessage;
    }

    @Override
    public Observable<Integer> deleteByUuid(String uuid) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.deleteByUuid(uuid);
    }

    @Override
    public Observable<Integer> deleteAll() {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.deleteAll();
    }

    @Override
    public Observable<List<MessageEntity>> fetchByType(MessageEntity.Type type) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.fetchMessageByType(mMessageDataMapper.map(type))
                .map((messageList -> mMessageDataMapper.map(messageList)));
    }

    @Override
    public Observable<List<MessageEntity>> fetchByStatus(MessageEntity.Status status) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.fetchMessageByStatus(mMessageDataMapper.map(status))
                .map((messageList -> mMessageDataMapper.map(messageList)));
    }

    @Override
    public Observable<List<MessageEntity>> fetchPending() {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.fetchPending()
                .map((messageList -> mMessageDataMapper.map(messageList)));
    }

    @Override
    public Observable<Boolean> publishMessage(MessageEntity messageEntities) {
        return Observable.defer(() -> {
            boolean status = true;
            List<Message> messages = Arrays.asList(mMessageDataMapper.map(messageEntities));
            mTweetMessage.tweetMessages(messages);
            status = mPostMessage.postMessage(messages);
            return Observable.just(status);
        });
    }

    @Override
    public Observable<Boolean> publishMessages() {
        return Observable.defer(() -> {
            mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
            List<Message> messages = mMessageDataSource.syncFetchPending();
            boolean status = true;
            mTweetMessage.tweetMessages(messages);
            status = mPostMessage.postMessage(messages);
            return Observable.just(status);
        });
    }

    @Override
    public Observable<List<MessageEntity>> importMessage() {
        return Observable.defer(() -> {
            ProcessSms processSms = mPostMessage.getProcessSms();
            List<MessageModel> smsMessages = processSms.importMessages();
            List<Message> messages = new ArrayList<>();
            mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
            for (MessageModel smsMessage : smsMessages) {
                messages.add(mPostMessage.map(smsMessage));
            }
            mMessageDataSource.putMessages(messages);
            return Observable.just(syncFetchPending());
        });
    }

    @Override
    public MessageEntity syncFetchByUuid(String uuid) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataMapper.map(mMessageDataSource.fetchPendingByUuid(uuid));
    }


    @Override
    public List<MessageEntity> syncFetchPending() {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataMapper.map(mMessageDataSource.syncFetchPending());
    }

    @Override
    public Observable<List<MessageEntity>> getEntities() {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.getMessages()
                .map((messageList -> mMessageDataMapper.map(messageList)));
    }

    @Override
    public Observable<MessageEntity> getEntity(Long id) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.getMessage(id).map(message -> mMessageDataMapper.map(message));
    }

    @Override
    public Observable<Long> addEntity(MessageEntity messageEntity) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.put(mMessageDataMapper.map(messageEntity));
    }

    @Override
    public Observable<Long> updateEntity(MessageEntity messageEntity) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.put(mMessageDataMapper.map(messageEntity));
    }

    @Override
    public Observable<Long> deleteEntity(Long id) {
        mMessageDataSource = mMessageDataSourceFactory.createMessageDatabaseSource();
        return mMessageDataSource.deleteEntity(id);
    }
}
