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

import javax.inject.Inject;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class UpdateMessageUsecase extends Usecase {

    private final MessageRepository mMessageRepository;

    private MessageEntity mMessageEntity;

    /**
     * Default constructor
     *
     * @param messageRepository   The message repository
     * @param threadExecutor      The thread executor
     * @param postExecutionThread The post execution thread
     */
    @Inject
    protected UpdateMessageUsecase(@NonNull MessageRepository messageRepository,
            @NonNull ThreadExecutor threadExecutor,
            @NonNull PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        mMessageRepository = messageRepository;
    }

    /**
     * Sets the messageEntity
     *
     * @param messageEntity The messageEntity to be updated
     */
    public void setMessageEntity(MessageEntity messageEntity) {
        mMessageEntity = messageEntity;
    }

    @Override
    protected Observable<Long> buildUseCaseObservable() {
        if (mMessageRepository == null) {
            throw new RuntimeException(
                    "MessageRepository is null. You must call setMessageService(...)");
        }
        return mMessageRepository.updateEntity(mMessageEntity);
    }
}
