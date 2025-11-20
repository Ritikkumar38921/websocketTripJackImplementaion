package com.message.Springapplication;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.message.Springapplication.dto.ChatMessage;

import java.lang.reflect.Type;

//  this make sure application run on random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

//	/ws WebSocket endpoint
//	/topic/* SimpleBroker
//	/app/* application handlers

	@LocalServerPort
	private int port;

	private WebSocketStompClient stompClient;

	@BeforeEach
	void setup() {

		// SockJS Client + WebSocket transport
		SockJsClient sockJsClient = new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient())));

		stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}

	@Test
	void testGroupMessaging() throws Exception {

//	    	this is connection url

		String url = "ws://localhost:" + port + "/ws";

		CompletableFuture<ChatMessage> future = new CompletableFuture<>();

		// Connect
		StompSession session = stompClient.connect(url, new TestStompSessionHandler()).get(10, TimeUnit.SECONDS);

		// Subscribe
		session.subscribe("/topic/group.room1", new StompFrameHandler() {
			@Override
			public Type getPayloadType(StompHeaders headers) {
				return ChatMessage.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				future.complete((ChatMessage) payload);
			}
		});

		// Send message
		ChatMessage msg = new ChatMessage();
		msg.setSender("Ritik");
		msg.setContent("Hello from Test!");

//	         here we know out application have handle /app and there we have /chat/{groupId} . Let say i want to send the message at groupId -> room1
		session.send("/app/chat/room1", msg);

//	        here i wait 3 seconds to get the response . as we know CompletableFuture future return the result . 
		ChatMessage received = future.get(3, TimeUnit.SECONDS);
		
		// here we cross verify does value are same or not 
		System.out.println("RECEIVED Sender → " + received.getSender());
		System.out.println("RECEIVED Message → " + received.getContent());
		
//	        we get correct answer for our case.
	}

	private static class TestStompSessionHandler extends StompSessionHandlerAdapter {

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			System.out.println("CONNECTED → " + connectedHeaders);
		}

		@Override
		public void handleTransportError(StompSession session, Throwable ex) {
			ex.printStackTrace();
			Assertions.fail("Transport error: " + ex.getMessage());
		}

		@Override
		public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
			ex.printStackTrace();
			Assertions.fail("Exception: " + ex.getMessage());
		}
	}

}
