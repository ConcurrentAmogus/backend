package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Model.Message;
import com.concurrent_programming.amogus.Model.Room;
import com.concurrent_programming.amogus.Model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    WebSocketMessageBrokerStats webSocketMessageBrokerStats;

    // Mapped as /ws/send-public-message
    @MessageMapping("/send-public-message")
    public Message handlePublicMessage(@Payload RoomAndMessagePayloads payloads) {
        Room room = payloads.getRoom();
        Message message = payloads.getMessage();
        System.out.println("Room: " + room);
        System.out.println("Message: " + message);

        try {
            // topic = "/chat/{roomId}/public"
            simpMessagingTemplate.convertAndSend("/chat/" + room.getId() + "/public", message);

            System.out.println("************************************************");
            System.out.println("Subscriptions: " + webSocketMessageBrokerStats.getWebSocketSessionStatsInfo());
            System.out.println("************************************************");

            return message;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to handle public message");
        }
    }

    // Mapped as /ws/send-private-message (Only wolf and seer have private chat)
    @MessageMapping("/send-private-message")
    public Message handlePrivateMessage(@Payload RoomUserMessagePayloads payloads) {
        Room room = payloads.getRoom();
        User user = payloads.getUser();
        Message message = payloads.getMessage();

        try {
            // topic = "/chat/{roomId}-{role}/private"
            simpMessagingTemplate.convertAndSend("/chat/" + room.getId() + "-" + user.getRole() + "/private", message);
            return message;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to handle private message");
        }
    }

    @Data
    private static class RoomAndMessagePayloads {
        private Room room;
        private Message message;
    }

    @Data
    private static class RoomUserMessagePayloads {
        private Room room;
        private User user;
        private Message message;
    }
}
