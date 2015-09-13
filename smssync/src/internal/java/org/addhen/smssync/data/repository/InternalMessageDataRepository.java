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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.mapper.MessageDataMapper;
import org.addhen.smssync.data.process.ProcessMessage;
import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.smslib.model.SmsMessage;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class InternalMessageDataRepository implements MessageRepository {

    private Context mContext;

    private final MessageDataMapper mMessageDataMapper;

    private ProcessMessage mProcessMessage;

    @Inject
    public InternalMessageDataRepository(Context context, MessageDataMapper messageDataMapper,
            ProcessMessage processMessage) {
        mContext = context;
        mMessageDataMapper = messageDataMapper;
        mProcessMessage = processMessage;
    }

    @Override
    public Observable<Integer> deleteByUuid(String uuid) {
        return Observable.defer(() -> {
            return Observable.just(10);
        });
    }

    @Override
    public Observable<Integer> deleteAll() {
        return null;
    }

    @Override
    public Observable<List<MessageEntity>> fetchByType(MessageEntity.Type type) {
        return null;
    }

    @Override
    public Observable<List<MessageEntity>> fetchByStatus(MessageEntity.Status status) {
        return Observable.defer(() -> {
            List<Message> messageEntityList = new ArrayList<>();
            List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
            return Observable.just(messages);
        });
    }

    @Override
    public Observable<Boolean> publishMessage(List<MessageEntity> messageEntities) {
        return Observable.defer(() -> {
            boolean status = mProcessMessage
                    .postMessage(mMessageDataMapper.unmap(messageEntities), null);
            return Observable.just(status);
        });
    }

    @Override
    public Observable<List<MessageEntity>> importMessage() {
        return Observable.defer(() -> {
            ProcessSms processSms = mProcessMessage.getProcessSms();
            List<SmsMessage> smsMessages = processSms.importMessages();
            List<Message> messages = new ArrayList<>();
            for (SmsMessage smsMessage : smsMessages) {
                messages.add(mProcessMessage.map(smsMessage));
            }
            return Observable.just(mMessageDataMapper.map(messages));
        });
    }

    @Override
    public Observable<List<MessageEntity>> getEntities() {
        return Observable.create(subscriber -> {
            JsonDeserializer<Date> date = (json, typeOfT, context) ->
                    json == null ? null : new Date(json.getAsLong());
            final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, date)
                    .setDateFormat("h:mm a").create();
            Type messageList = new TypeToken<List<Message>>() {
            }.getType();
            String json = loadJSONFromAsset();
            List<Message> messageEntityList = null;
            try {
                messageEntityList = gson.fromJson(json, messageList);
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
            List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
            subscriber.onNext(messages);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<MessageEntity> getEntity(Long aLong) {
        return null;
    }

    @Override
    public Observable<Long> addEntity(MessageEntity messageEntity) {
        return null;
    }

    @Override
    public Observable<Long> updateEntity(MessageEntity messageEntity) {
        return null;
    }

    @Override
    public Observable<Long> deleteEntity(Long aLong) {
        return null;
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = mContext.getAssets().open("json/messages.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
