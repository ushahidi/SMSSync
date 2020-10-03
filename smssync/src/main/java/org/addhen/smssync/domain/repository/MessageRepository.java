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

package org.addhen.smssync.domain.repository;

import com.addhen.android.raiburari.domain.repository.Repository;

import org.addhen.smssync.domain.entity.MessageEntity;

import java.util.List;

import rx.Observable;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface MessageRepository extends Repository<MessageEntity> {

    Observable<Integer> deleteByUuid(String uuid);

    Observable<Integer> deleteAll();

    Observable<List<MessageEntity>> fetchByType(MessageEntity.Type type);

    Observable<List<MessageEntity>> fetchByStatus(MessageEntity.Status status);

    Observable<List<MessageEntity>> fetchPending();

    Observable<Boolean> publishMessage(MessageEntity messageEntity);

    Observable<Boolean> publishMessages();

    Observable<List<MessageEntity>> importMessage();

    MessageEntity syncFetchByUuid(String uuid);

    List<MessageEntity> syncFetchPending();
}
