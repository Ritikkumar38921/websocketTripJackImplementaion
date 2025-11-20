package com.message.Application;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.lang.reflect.Type;

import com.message.Application.dto.ChatMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setup() {
    	// PURE WebSocket client (NO SockJS)
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void testGroupMessaging() {
    	
    	
    	try {

    	     // IMPORTANT: Use dynamic port
            String url = "ws://localhost:" + port + "/ws";

            CompletableFuture<ChatMessage> future = new CompletableFuture<>();

            // Connect
            StompSession session = stompClient
                    .connect(url, new StompSessionHandlerAdapter() {})
                    .get(10, TimeUnit.SECONDS);

            // Subscribe to group
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

            // Send message to your controller
            ChatMessage msg = new ChatMessage();
            msg.setSender("Ritik");
            msg.setContent("Hello from Test!");

            session.send("/app/chat/room1", msg);

            // Verify
            ChatMessage received = future.get(3, TimeUnit.SECONDS);

            assert received.getSender().equals("Ritik");
            assert received.getContent().equals("Hello from Test!");
    	}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
    }
}