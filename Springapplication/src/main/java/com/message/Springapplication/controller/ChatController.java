package com.message.Springapplication.controller;

import java.time.Instant;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

	private final SimpMessagingTemplate messagingTemplate;

	public ChatController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@MessageMapping("/chat/{groupId}")
	public void sendMessage(@DestinationVariable String groupId, com.message.Springapplication.dto.ChatMessage message) {
		message.setTimestamp(Instant.now().toString());
		String destination = "/topic/group." + groupId;
		messagingTemplate.convertAndSend(destination, message);
	}
}