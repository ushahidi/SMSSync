package org.addhen.smssync.data;

public class Messages {
	
	private String messageBody;
	private String messageFrom;
	private String latitude;
	private String longitude;
	private String mmsBody;
	private boolean isMessageSent; 
	
	public Messages() {
		this.messageBody = "";
		this.messageFrom = "";
		this.latitude = "";
		this.longitude = "";
		this.mmsBody = "";
		this.isMessageSent = true; 
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
	
	public void setLatitude( String latitude ) {
		this.latitude = latitude;
	}
	
	public String getLatitude() {
		return this.latitude;
	}
	
	public void setLongitude ( String longitude ) {
		this.longitude = longitude;
	}
	
	public String getLongitude() {
		return this.longitude;
	}
	
	public void setMessageSent( boolean isMessageSent ) {
		this.isMessageSent = isMessageSent;
	}
	
	public boolean getMessageSent() {
		return this.isMessageSent;
	}
	
	public void setMmsBody( String mmsBody ) {
		this.mmsBody = mmsBody;
	}
	
	public String getMmsBody() {
		return this.mmsBody;
	}
	
}
