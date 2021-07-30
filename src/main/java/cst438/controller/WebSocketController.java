package cst438.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import cst438.domain.ChatMessage;
 
@Controller
public class WebSocketController {

    @MessageMapping("/chat.sendMessage/{id}")
    @SendTo("/topic/publicChatRoom/{id}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }
 
    @MessageMapping("/chat.register/{id}")
    @SendTo("/topic/publicChatRoom/{id}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}