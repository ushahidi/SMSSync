package org.addhen.smssync.data;

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
	
	public void setMessageBody( String messageBody ) {
		this.messageBody = messageBody;
	}
	
	public String getMessageBody() {
		return this.messageBody;
	}
	
	public void setMessageFrom( String messageFrom ) {
		this.messageFrom = messageFrom;
	}
	
	public String getMessageFrom() {
		return this.messageFrom;
	}
	
	public void setMessageDate( String messageDate ) {
		this.messageDate = messageDate;
	}
	
	public String getMessageDate() {
		return this.messageDate;
	}
	
	public void setMessageId ( int messageId ) {
		this.messageId = messageId;
	}
	
	public int getMessageId() {
		return this.messageId;
	}
	
}
