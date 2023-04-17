package com.concurrent_programming.amogus.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatControllerExample {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/public-message-example")
    @SendTo("/chatroom/public")
    public MessageExample handlePublicMessage(@Payload MessageExample message) {
        try {
            System.out.println("MessageExample: " + message);
            return message;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to handle public message: " + ex.getMessage());
        }
    }

    @MessageMapping("/private-message-example")
    public MessageExample handlePrivateMessage(@Payload MessageExample message) {
        try {
            System.out.println("MessageExample: " + message);
            simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
            return message;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to handle private message: " + ex.getMessage());
        }
    }
}
