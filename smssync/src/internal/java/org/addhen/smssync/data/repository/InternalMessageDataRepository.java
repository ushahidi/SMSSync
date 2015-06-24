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
public class InternalMessageDataRepository implements MessageRepository {

    @Inject
    public InternalMessageDataRepository() {
    }

    @Override
    public Observable<Integer> deleteByUuid(String uuid) {
        return null;
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
        return null;
    }

    @Override
    public Observable<List<MessageEntity>> getEntities() {
        return null;
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
}
