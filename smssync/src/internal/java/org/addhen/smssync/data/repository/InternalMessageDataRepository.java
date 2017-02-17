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
import org.addhen.smssync.data.message.PostMessage;
import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.domain.repository.MessageRepository;
import org.addhen.smssync.presentation.model.MessageModel;
import org.addhen.smssync.smslib.sms.ProcessSms;

import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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

    private PostMessage mProcessMessage;

    private Gson mGson;

    @Inject
    public InternalMessageDataRepository(Context context, MessageDataMapper messageDataMapper,
            PostMessage processMessage) {
        mContext = context;
        mMessageDataMapper = messageDataMapper;
        mProcessMessage = processMessage;
        mGson = getGson();
    }

    @Override
    public Observable<Integer> deleteByUuid(String uuid) {
        return Observable.defer(() -> {
            return Observable.just(1);
        });
    }

    @Override
    public Observable<Integer> deleteAll() {
        return Observable.defer(() -> {
            return Observable.just(10);
        });
    }

    @Override
    public Observable<List<MessageEntity>> fetchByType(MessageEntity.Type type) {
        if (type.equals(MessageEntity.Type.PENDING)) {
            return Observable.defer(() -> {
                Type messageList = new TypeToken<List<Message>>() {
                }.getType();
                String json = loadPendingMessages();
                List<Message> messageEntityList = null;
                try {
                    messageEntityList = mGson.fromJson(json, messageList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
                List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
                return Observable.just(messages);
            });
        }

        return Observable.defer(() -> {
            Type messageList = new TypeToken<List<Message>>() {
            }.getType();
            String json = loadSentMessages();
            List<Message> messageEntityList = null;
            try {
                messageEntityList = mGson.fromJson(json, messageList);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
            List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
            return Observable.just(messages);
        });
    }

    @Override
    public Observable<List<MessageEntity>> fetchByStatus(MessageEntity.Status status) {
        if (status.equals(MessageEntity.Status.SENT)) {
            return Observable.defer(() -> {
                Type messageList = new TypeToken<List<Message>>() {
                }.getType();
                String json = loadSentMessages();
                List<Message> messageEntityList = null;
                try {
                    messageEntityList = mGson.fromJson(json, messageList);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
                List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
                return Observable.just(messages);
            });
        }

        return Observable.defer(() -> {
            Type messageList = new TypeToken<List<Message>>() {
            }.getType();
            String json = loadFailedMessages();
            List<Message> messageEntityList = null;
            try {
                messageEntityList = mGson.fromJson(json, messageList);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
            List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
            return Observable.just(messages);
        });
    }

    @Override
    public Observable<List<MessageEntity>> fetchPending() {
        return Observable.defer(() -> {
            Type messageList = new TypeToken<List<Message>>() {
            }.getType();
            String json = loadPendingMessages();
            List<Message> messageEntityList = null;
            try {
                messageEntityList = mGson.fromJson(json, messageList);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
            List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
            return Observable.just(messages);
        });
    }

    @Override
    public Observable<Boolean> publishMessage(MessageEntity messageEntity) {
        return Observable.defer(() -> {
            boolean status = mProcessMessage
                    .postMessage(mMessageDataMapper.unmap(Arrays.asList(messageEntity)));
            return Observable.just(status);
        });
    }

    @Override
    public Observable<Boolean> publishMessages() {
        return Observable.defer(() -> {
            return Observable.just(true);
        });
    }

    @Override
    public Observable<List<MessageEntity>> importMessage() {
        return Observable.defer(() -> {
            ProcessSms processSms = mProcessMessage.getProcessSms();
            List<MessageModel> smsMessages = processSms.importMessages();
            List<Message> messages = new ArrayList<>();
            for (MessageModel smsMessage : smsMessages) {
                messages.add(mProcessMessage.map(smsMessage));
            }
            return Observable.just(mMessageDataMapper.map(messages));
        });
    }

    @Override
    public MessageEntity syncFetchByUuid(String uuid) {
        Type typeMessage = new TypeToken<Message>() {
        }.getType();
        String json = loadMessage();
        Message message = new Message();
        try {
            message = mGson.fromJson(json, typeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMessageDataMapper.map(message);
    }

    @Override
    public List<MessageEntity> syncFetchPending() {
        Type messageList = new TypeToken<List<Message>>() {
        }.getType();
        String json = loadPendingMessages();
        List<Message> messageEntityList = null;
        try {
            messageEntityList = mGson.fromJson(json, messageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mMessageDataMapper.map(messageEntityList);
    }

    @Override
    public Observable<List<MessageEntity>> getEntities() {
        return Observable.defer(() -> {
            Type messageList = new TypeToken<List<Message>>() {
            }.getType();
            String json = loadMessages();
            List<Message> messageEntityList = null;
            try {
                messageEntityList = mGson.fromJson(json, messageList);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
            List<MessageEntity> messages = mMessageDataMapper.map(messageEntityList);
            return Observable.just(messages);
        });
    }

    @NonNull
    private Gson getGson() {
        JsonDeserializer<Date> date = (json, typeOfT, context) ->
                json == null ? null : new Date(json.getAsLong());
        return new GsonBuilder().registerTypeAdapter(Date.class, date)
                .setDateFormat("h:mm a").create();
    }

    @Override
    public Observable<MessageEntity> getEntity(Long aLong) {
        return Observable.defer(() -> {
            Type typeMessage = new TypeToken<Message>() {
            }.getType();
            String json = loadMessage();
            Message message = new Message();
            try {
                message = mGson.fromJson(json, typeMessage);
            } catch (Exception e) {
                e.printStackTrace();
                return Observable.error(e);
            }
            return Observable.just(mMessageDataMapper.map(message));
        });
    }

    @Override
    public Observable<Long> addEntity(MessageEntity messageEntity) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<Long> updateEntity(MessageEntity messageEntity) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    @Override
    public Observable<Long> deleteEntity(Long aLong) {
        return Observable.defer(() -> {
            return Observable.just(1l);
        });
    }

    private String loadFailedMessages() {
        return loadJSONFromAsset("failed_messages.json");
    }

    private String loadSentMessages() {
        return loadJSONFromAsset("sent_messages.json");
    }

    private String loadPendingMessages() {
        return loadJSONFromAsset("pending_messages.json");
    }

    private String loadTaskMessages() {
        return loadJSONFromAsset("task_messages.json");
    }

    private String loadMessage() {
        return loadJSONFromAsset("message.json");
    }

    private String loadMessages() {
        return loadJSONFromAsset("messages.json");
    }

    private String loadJSONFromAsset(final String jsonFileName) {
        return DataHelper.loadJSONFromAsset(mContext, "messages/" + jsonFileName);
    }

}
