package org.addhen.smssync.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.addhen.smssync.MainApplication;
import org.addhen.smssync.data.Messages;

import android.content.Context;
import android.util.Log;

/**
 * Utility class for sent messages feature
 *  
 * @author eyedol
 *
 */
public class SentMessagesUtil {
    
    private static final String CLASS_TAG = Util.class.getCanonicalName();
    
    public static HashMap<String, String> smsMap = new HashMap<String, String>();
    
    private static List<Messages> mMessages;
    
    /**
     * Process messages as received from the user; 0 - successful 1 - failed
     * fetching categories
     * 
     * @return int - status
     */
    public static int processSentMessages(Context context) {
        Log.i(CLASS_TAG,
                "processMessages(): Process text messages as received from the user's phone");
        
        List<Messages> listMessages = new ArrayList<Messages>();
        int messageId = 0;
        Messages messages = new Messages();
        listMessages.add(messages);

        // check if messageId is actually initialized
        if (smsMap.get("messagesId") != null) {
            messageId = Integer.parseInt(smsMap.get("messagesId"));
        }

        messages.setMessageId(messageId);
        messages.setMessageFrom(smsMap.get("messagesFrom"));
        messages.setMessageBody(smsMap.get("messagesBody"));
        messages.setMessageDate(smsMap.get("messagesDate"));
        mMessages = listMessages;

        if (mMessages != null) {
            MainApplication.mDb.addSentMessages(mMessages);
            return 0;

        } else {
            return 1;
        }
    }

}
