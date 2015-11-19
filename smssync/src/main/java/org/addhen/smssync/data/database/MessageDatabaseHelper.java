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

package org.addhen.smssync.data.database;

import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.exception.MessageNotFoundException;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
@Singleton
public class MessageDatabaseHelper extends BaseDatabaseHelper {

    @Inject
    public MessageDatabaseHelper(@NonNull Context context) {
        super(context);
    }

    public Observable<Integer> deleteByUuid(String uuid) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                Integer row = null;
                final String whereClause = "message_uuid = ?";
                final String whereArgs[] = {uuid};
                try {
                    row = cupboard().withDatabase(getWritableDatabase())
                            .delete(Message.class, whereClause, whereArgs);

                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(row);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public Observable<Integer> deleteAll() {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                Integer row = null;
                try {
                    row = cupboard().withDatabase(getWritableDatabase())
                            .delete(Message.class, null);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(row);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public Observable<List<Message>> fetchMessageByType(Message.Type type) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                List<Message> messages = null;
                final String whereClause = "message_type = ?";
                try {
                    messages = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                            .withSelection(whereClause, type.name()).orderBy("messages_date DESC")
                            .list();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (messages != null) {
                    subscriber.onNext(messages);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new MessageNotFoundException());
                }
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public List<Message> fetchMessage(Message.Type type) {
        List<Message> messages = new ArrayList<Message>();
        final String whereClause = "message_type = ?";
        try {
            messages = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                    .withSelection(whereClause, type.name()).orderBy("messages_date DESC")
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Message fetchMessageByUuid(String uuid) {
        Message message = new Message();
        final String whereClause = "message_uuid = ?";
        try {
            message = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                    .withSelection(whereClause, uuid).orderBy("messages_date DESC")
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public Message fetchPendingMessageByUuid(String uuid) {
        Message message = new Message();
        final String whereClause = "message_uuid = ? AND status != ?";
        try {
            message = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                    .withSelection(whereClause, uuid, Message.Status.SENT.name())
                    .orderBy("messages_date DESC")
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public Observable<List<Message>> fetchMessageByStatus(Message.Status status) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                final String whereClause = "status = ?";
                List<Message> messages = null;
                try {
                    messages = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                            .withSelection(whereClause, status.name()).orderBy(
                                    "messages_date DESC").list();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (messages != null) {
                    subscriber.onNext(messages);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new MessageNotFoundException());
                }
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public Observable<List<Message>> fetchPending() {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                final String whereClause = "status != ?";
                List<Message> messages = null;
                try {
                    messages = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                            .withSelection(whereClause, Message.Status.SENT.name()).orderBy(
                                    "messages_date DESC").list();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (messages != null) {
                    subscriber.onNext(messages);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new MessageNotFoundException());
                }
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public List<Message> syncFetchPending() {
        List<Message> messages = new ArrayList<>();
        if (!isClosed()) {
            final String whereClause = "status != ?";

            messages = cupboard().withDatabase(getReadableDatabase()).query(Message.class)
                    .withSelection(whereClause, Message.Status.SENT.name())
                    .orderBy("messages_date DESC").list();
        }
        return messages;
    }

    public Observable<List<Message>> getMessages() {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                List<Message> messages = null;
                try {
                    messages = cupboard().withDatabase(
                            getReadableDatabase()).query(Message.class).list();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (messages != null) {
                    subscriber.onNext(messages);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new MessageNotFoundException());
                }
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public Observable<Message> getMessage(Long id) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                Message message = null;
                try {
                    message = cupboard().withDatabase(getReadableDatabase()).get(Message.class, id);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                if (message != null) {
                    subscriber.onNext(message);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new MessageNotFoundException());
                }
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public Observable<Long> deleteEntity(Long id) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                try {
                    cupboard().withDatabase(getWritableDatabase()).delete(Message.class, id);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(1l);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public Observable<Long> put(Message message) {
        return Observable.create(subscriber -> {
            if (!isClosed()) {
                try {
                    cupboard().withDatabase(getWritableDatabase()).put(message);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(1l);
                subscriber.onCompleted();
            } else {
                subscriber.onError(new Exception());
            }
        });
    }

    public void putMessage(Message message) {
        if (!isClosed()) {
            try {
                cupboard().withDatabase(getWritableDatabase()).put(message);
            } catch (Exception e) {
            }
        }
    }

    public void putMessages(List<Message> messages) {
        if (!isClosed()) {
            try {
                cupboard().withDatabase(getWritableDatabase()).put(messages);
            } catch (Exception e) {
            }
        }
    }

    public Integer deleteWithUuid(String uuid) {
        Integer row = 0;
        if (!isClosed()) {
            final String whereClause = "message_uuid = ?";
            final String whereArgs[] = {uuid};
            try {
                row = cupboard().withDatabase(getWritableDatabase())
                        .delete(Message.class, whereClause, whereArgs);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return row;

    }
}
