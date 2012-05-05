
package org.addhen.smssync.data;

/**
 * Class to handle set and getters.
 * 
 * @author eyedol
 */
public class Messages {

    private String messageBody;

    private String messageFrom;

    private String messageDate;

    private int messageId;

    public Messages() {
        this.messageBody = "";
        this.messageFrom = "";
        this.messageDate = "";
        this.messageId = 0;
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
     * @param String messageDate - The timestamp of the message. To be changed
     *            into human readable.
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
     * Set the message ID.
     * 
     * @param int messageId - The message ID.
     * @return void
     */
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    /**
     * Get the message ID.
     * 
     * @return int
     */
    public int getMessageId() {
        return this.messageId;
    }

}
