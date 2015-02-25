package org.addhen.smssync.database;

import android.content.Context;

import java.util.List;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class MessageDatabaseHelper extends BaseDatabseHelper implements MessageDatabase {

    public MessageDatabaseHelper(Context context) {
        super(context);
    }

    @Override
    public int messagesCount() {
        return 0;
    }

    @Override
    public boolean addMessages(List<Message> messages) {
        return false;
    }

    @Override
    public boolean addMessage(Message message) {
        return false;
    }

    @Override
    public boolean deleteMessagesByUuid(String messageUuid) {
        return false;
    }

    @Override
    public boolean deleteAllMessages() {
        return false;
    }

    @Override
    public List<Message> fetchMessagesByUuid(String messageUuid) {
        return null;
    }

    @Override
    public List<Message> fetchMessageByUuids(List<String> messageUuid) {
        return null;
    }

    @Override
    public List<Message> fetchAllMessages() {
        return null;
    }

    @Override
    public List<Message> fetchMessagesByLimit(int limit) {
        return null;
    }
}
