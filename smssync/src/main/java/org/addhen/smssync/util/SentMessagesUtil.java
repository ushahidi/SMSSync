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

package org.addhen.smssync.util;


import org.addhen.smssync.App;
import org.addhen.smssync.database.BaseDatabseHelper;
import org.addhen.smssync.models.Message;
import static org.addhen.smssync.database.BaseDatabseHelper.DatabaseCallback;
/**
 * Utility class for sent messages feature
 *
 * @author eyedol
 */
public class SentMessagesUtil {

    private static final String CLASS_TAG = Util.class.getCanonicalName();

    /**
     * Process messages as received from the user; 0 - successful 1 - failed fetching categories
     *
     * @return int - status
     */
    public static boolean processSentMessages(Message message) {
        Logger.log(CLASS_TAG,
                "processMessages(): Process text messages as received from the user's phone");

        if(message !=null) {
            message.setStatus(Message.Status.SENT);
            if(message.getId() == null || message.getId() == 0) {
                App.getDatabaseInstance().getMessageInstance().put(message, new DatabaseCallback<Void>() {
                    @Override
                    public void onFinished(Void result) {

                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            } else {
                App.getDatabaseInstance().getMessageInstance().update(message,
                        new BaseDatabseHelper.DatabaseCallback<Void>() {
                            @Override
                            public void onFinished(Void result) {

                            }

                            @Override
                            public void onError(Exception exception) {

                            }
                        });
            }

            return true;

        }

        return false;

    }

}
