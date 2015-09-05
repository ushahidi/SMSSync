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

import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.domain.entity.MessageEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageDataMapper {

    @Inject
    public MessageDataMapper() {
        // Do nothing
    }

    public MessageEntity map(Message message) {
        MessageEntity messageEntity = null;
        if (message != null) {
            messageEntity = new MessageEntity();
            messageEntity._id = message._id;
            messageEntity.messageBody = message.messageBody;
            messageEntity.messageDate = message.messageDate;
            messageEntity.messageFrom = message.messageFrom;
            messageEntity.messageUuid = message.messageUuid;
            messageEntity.sentResultMessage = message.sentResultMessage;
            messageEntity.sentResultCode = message.sentResultCode;
            messageEntity.messageType = MessageEntity.Type.valueOf(message.messageType.name());
            messageEntity.status = MessageEntity.Status.valueOf(message.status.name());
            messageEntity.deliveryResultCode = message.deliveryResultCode;
            messageEntity.deliveryResultMessage = message.deliveryResultMessage;
        }
        return messageEntity;
    }

    public Message map(MessageEntity messageEntity) {
        Message message = null;
        if (messageEntity != null) {
            message = new Message();
            message._id = messageEntity._id;
            message.messageBody = messageEntity.messageBody;
            message.messageDate = messageEntity.messageDate;
            message.messageFrom = messageEntity.messageFrom;
            message.messageUuid = messageEntity.messageUuid;
            message.sentResultMessage = messageEntity.sentResultMessage;
            message.sentResultCode = messageEntity.sentResultCode;
            message.messageType = map(messageEntity.messageType);
            message.status = map(messageEntity.status);
            message.deliveryResultCode = messageEntity.deliveryResultCode;
            message.deliveryResultMessage = messageEntity.deliveryResultMessage;
        }
        return message;
    }

    public List<MessageEntity> map(List<Message> messageList) {
        List<MessageEntity> messageEntityList = new ArrayList<>();
        MessageEntity messageEntity;
        for (Message message : messageList) {
            messageEntity = map(message);
            if (messageEntity != null) {
                messageEntityList.add(messageEntity);
            }
        }
        return messageEntityList;
    }

    public List<Message> unmap(List<MessageEntity> messageList) {
        List<Message> messages = new ArrayList<>();
        Message message;
        for (MessageEntity messageEntity : messageList) {
            message = map(messageEntity);
            if (message != null) {
                messages.add(message);
            }
        }
        return messages;
    }

    public Message.Status map(MessageEntity.Status status) {
        return Message.Status.valueOf(status.name());
    }

    public Message.Type map(MessageEntity.Type type) {
        return Message.Type.valueOf(type.name());
    }
}