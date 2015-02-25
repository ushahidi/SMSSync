package org.addhen.smssync.database;

import java.util.List;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public interface MessageDatabase {

    public int messagesCount();

    public boolean addMessages(List<Message> messages);

    public boolean addMessage(Message message);

    public boolean deleteMessagesByUuid(String messageUuid);

    public boolean deleteAllMessages();

    public List<Message> fetchMessagesByUuid(String messageUuid);

    public List<Message> fetchMessageByUuids(List<String> messageUuid);

    public List<Message> fetchAllMessages();

    public List<Message> fetchMessagesByLimit(int limit);
}
