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

package org.addhen.smssync.presentation.model.mapper;

import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.presentation.model.MessageModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageModelDataMapper {

    @Inject
    public MessageModelDataMapper() {
        // Do nothing
    }

    public MessageEntity map(MessageModel message) {
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
            messageEntity.messageType = map(message.messageType);
            messageEntity.status = map(message.status);
            messageEntity.deliveryResultCode = message.deliveryResultCode;
            messageEntity.deliveryResultMessage = message.deliveryResultMessage;
        }
        return messageEntity;
    }

    public MessageModel map(MessageEntity messageEntity) {
        MessageModel message = null;
        if (messageEntity != null) {
            message = new MessageModel();
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

    public List<MessageModel> map(List<MessageEntity> messageList) {
        List<MessageModel> messageEntityList = new ArrayList<>();
        MessageModel messageEntity;
        for (MessageEntity message : messageList) {
            messageEntity = map(message);
            if (messageEntity != null) {
                messageEntityList.add(messageEntity);
            }
        }
        return messageEntityList;
    }

    public List<MessageEntity> unmap(List<MessageModel> messageModels) {
        List<MessageEntity> messageList = new ArrayList<>();
        MessageEntity messageEntity;
        for (MessageModel messageModel : messageModels) {
            messageEntity = map(messageModel);
            if (messageEntity != null) {
                messageList.add(messageEntity);
            }
        }
        return messageList;
    }

    public MessageEntity.Status map(MessageModel.Status status) {
        return MessageEntity.Status.valueOf(status.name());
    }

    public MessageModel.Status map(MessageEntity.Status status) {
        return MessageModel.Status.valueOf(status.name());
    }

    public MessageEntity.Type map(MessageModel.Type type) {
        return MessageEntity.Type.valueOf(type.name());
    }

    public MessageModel.Type map(MessageEntity.Type type) {
        return MessageModel.Type.valueOf(type.name());
    }
}