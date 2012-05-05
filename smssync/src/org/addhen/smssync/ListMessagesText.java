/** 
 ** Copyright (c) 2010 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/

package org.addhen.smssync;

import org.addhen.smssync.util.Util;

public class ListMessagesText {

    private String messagesFrom;

    private String messagesDate;

    private String messagesBody;

    private int messagesId;

    boolean isSelectable;

    public ListMessagesText(String messagesFrom, String messagesBody, String messagesDate,
            int messagesId) {
        
        this.messagesFrom = messagesFrom;
        
        if (formatDate(messagesDate) != null)
            this.messagesDate = formatDate(messagesDate);
        else
            this.messagesDate = messagesDate;
        
        this.messagesBody = messagesBody;
        this.messagesId = messagesId;
    }

    private String formatDate(String date) {
        try {
            return Util.formatDateTime(Long.parseLong(date), "MMM dd, yyyy 'at' hh:mm a");
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public void setMessageFrom(String messagesFrom) {
        this.messagesFrom = messagesFrom;
    }

    public String getMessageFrom() {
        return this.messagesFrom;
    }

    public void setMessageDate(String messagesDate) {

        if (formatDate(messagesDate) != null)
            this.messagesDate = formatDate(messagesDate);
        else
            this.messagesDate = messagesDate;
    }

    public String getMessageDate() {

        return this.messagesDate;
    }

    public void setMessageBody(String messagesBody) {
        this.messagesBody = messagesBody;
    }

    public String getMessageBody() {
        return this.messagesBody;
    }

    public void setMessageId(int messagesId) {
        this.messagesId = messagesId;
    }

    public int getMessageId() {
        return this.messagesId;
    }
}
