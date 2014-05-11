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

/**
 * Class to handle set and getters.
 *
 * @author eyedol
 */
public class Messages {

    private String messageBody;

    private String messageFrom;

    private String messageDate;

    private String messageUuid;

    private int messageType;

    private int mSentResultCode;

    private String mSentResultMessage;

    private int mDeliveryResultCode;

    private String mDeliveryResultMessage;


    public Messages() {
        this.messageBody = "";
        this.messageFrom = "";
        this.messageDate = "";
        this.messageUuid = "";
        this.messageType = 0;

        mSentResultCode = -2;
        mSentResultMessage = "";
        mDeliveryResultCode = -2;
        mDeliveryResultMessage = "";
    }

    /**
     * @return the messageType
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    /**
     * Set the content of the message. More like the body of the SMS message.
     *
     * @param String messageBody - The content of the SMS message.
     * @return void
     */
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * Get the content of the message.
     *
     * @return String
     */
    public String getMessageBody() {
        return this.messageBody;
    }

    /**
     * Set the address of the SMS message.
     *
     * @param String messageFrom
     * @return void
     */
    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    /**
     * Get the address of the SMS Message
     *
     * @return String
     */
    public String getMessageFrom() {
        return this.messageFrom;
    }

    /**
     * Set the date of the message.
     *
     * @param String messageDate - The timestamp of the message. To be changed into human readable.
     * @return void
     */
    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    /**
     * Get the message date
     *
     * @return String
     */
    public String getMessageDate() {
        return this.messageDate;
    }

    /**
     * Set the message UUID.
     *
     * @param int messageId - The message UUID.
     * @return void
     */
    public void setMessageUuid(String messageUuid) {
        this.messageUuid = messageUuid;
    }

    /**
     * Get the message UUID.
     *
     * @return String
     */
    public String getMessageUuid() {
        return this.messageUuid;
    }

    public int getSentResultCode() {
        return mSentResultCode;
    }

    public void setSentResultCode(int mSentResultCode) {
        this.mSentResultCode = mSentResultCode;
    }

    public String getSentResultMessage() {
        return mSentResultMessage;
    }

    public void setSentResultMessage(String mSentResultMessage) {
        this.mSentResultMessage = mSentResultMessage;
    }

    public int getDeliveryResultCode() {
        return mDeliveryResultCode;
    }

    public void setDeliveryResultCode(int mDeliveryResultCode) {
        this.mDeliveryResultCode = mDeliveryResultCode;
    }

    public String getDeliveryResultMessage() {
        return mDeliveryResultMessage;
    }

    public void setDeliveryResultMessage(String mDeliveryResultMessage) {
        this.mDeliveryResultMessage = mDeliveryResultMessage;
    }
}
