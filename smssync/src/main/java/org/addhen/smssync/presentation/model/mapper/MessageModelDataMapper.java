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
            messageEntity.setId(message.getId());
            messageEntity.setMessageBody(message.getMessageBody());
            messageEntity.setMessageDate(message.getMessageDate());
            messageEntity.setMessageFrom(message.getMessageFrom());
            messageEntity.setMessageUuid(message.getMessageUuid());
            messageEntity.setSentResultMessage(message.getSentResultMessage());
            messageEntity.setSentResultCode(message.getSentResultCode());
            messageEntity.setMessageType(map(message.getMessageType()));
            messageEntity.setStatus(map(message.getStatus()));
            messageEntity.setDeliveryResultCode(message.getDeliveryResultCode());
            messageEntity.setDeliveryResultMessage(message.getDeliveryResultMessage());
        }
        return messageEntity;
    }

    public MessageModel map(MessageEntity messageEntity) {
        MessageModel message = null;
        if (messageEntity != null) {
            message = new MessageModel();
            message.setId(messageEntity.getId());
            message.setMessageBody(messageEntity.getMessageBody());
            message.setMessageDate(messageEntity.getMessageDate());
            message.setMessageFrom(messageEntity.getMessageFrom());
            message.setMessageUuid(messageEntity.getMessageUuid());
            message.setSentResultMessage(messageEntity.getSentResultMessage());
            message.setSentResultCode(messageEntity.getSentResultCode());
            message.setMessageType(map(messageEntity.getMessageType()));
            message.setStatus(map(messageEntity.getStatus()));
            message.setDeliveryResultCode(messageEntity.getDeliveryResultCode());
            message.setDeliveryResultMessage(messageEntity.getDeliveryResultMessage());
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
        return status != null ? MessageEntity.Status.valueOf(status.name())
                : MessageEntity.Status.FAILED;
    }

    public MessageModel.Status map(MessageEntity.Status status) {
        return status != null ? MessageModel.Status.valueOf(status.name())
                : MessageModel.Status.FAILED;
    }

    public MessageEntity.Type map(MessageModel.Type type) {
        return type != null ? MessageEntity.Type.valueOf(type.name()) : MessageEntity.Type.PENDING;
    }

    public MessageModel.Type map(MessageEntity.Type type) {
        return type != null ? MessageModel.Type.valueOf(type.name()) : MessageModel.Type.PENDING;
    }
}