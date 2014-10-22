/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.database;

import org.addhen.smssync.models.Message;
import org.addhen.smssync.models.MessageResult;

import java.util.List;

/**
 * Interface to provide methods for manipulating messages db
 */
public interface IMessagesContentProvider {

    public int messagesCount();

    public boolean addMessages(List<Message> messages);

    public boolean addMessages(Message messages);

    public boolean deleteMessagesByUuid(String messageUuid);

    public boolean deleteAllMessages();

    public List<Message> fetchMessagesByUuid(String messageUuid);

    public List<MessageResult> fetchMessageResultsByUuid(List<String> messageUuid);

    public List<Message> fetchAllMessages();

    public List<Message> fetchMessagesByLimit(int limit);

}
