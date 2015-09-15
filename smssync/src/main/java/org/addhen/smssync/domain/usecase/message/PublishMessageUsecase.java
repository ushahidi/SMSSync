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

package org.addhen.smssync.domain.usecase.message;

import com.addhen.android.raiburari.domain.executor.PostExecutionThread;
import com.addhen.android.raiburari.domain.executor.ThreadExecutor;
import com.addhen.android.raiburari.domain.usecase.Usecase;

import org.addhen.smssync.domain.entity.MessageEntity;
import org.addhen.smssync.domain.repository.MessageRepository;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Publishes {@link org.addhen.smssync.domain.entity.MessageEntity} to a configured {@link
 * org.addhen.smssync.domain.entity.WebServiceEntity}
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PublishMessageUsecase extends Usecase {

    private final MessageRepository mMessageRepository;

    public List<MessageEntity> mMessageEntities;

    @Inject
    protected PublishMessageUsecase(@NonNull MessageRepository messageRepository,
            @NonNull ThreadExecutor threadExecutor,
            @NonNull PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        mMessageRepository = messageRepository;
    }

    public void setMessageEntity(List<MessageEntity> messageEntities) {
        mMessageEntities = messageEntities;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mMessageEntities == null) {
            throw new RuntimeException(
                    "MessageEntities is null. You must call setMessageEntity(...)");
        }
        return mMessageRepository.publishMessage(mMessageEntities);
    }

}
