package com.message.Springapplication.dto;

public class ChatMessage {
    public enum MessageType { JOIN, LEAVE, CHAT }

    private MessageType type;
    private String sender;
    private String content;
    private String timestamp;
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
    
}
