package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Model.Message;
import com.concurrent_programming.amogus.Model.Player;
import com.concurrent_programming.amogus.Model.Room;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    // Mapped as /ws/send-public-message
    @MessageMapping("/send-public-message")
    public Message handlePublicMessage(@Payload RoomAndMessagePayloads payloads) {
        Room room = payloads.getRoom();
        Message message = payloads.getMessage();

        try {
            // topic = "/chat/{roomId}/public"
            simpMessagingTemplate.convertAndSendToUser(room.getRoomId(), "/public", message);
            return message;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to handle public message");
        }
    }

    // Mapped as /ws/send-private-message (Only wolf and seer have private chat)
    @MessageMapping("/send-private-message")
    public Message handlePrivateMessage(@Payload RoomPlayerMessagePayloads payloads) {
        Room room = payloads.getRoom();
        Player player = payloads.getPlayer();
        Message message = payloads.getMessage();

        try {
            // topic = "/chat/{roomId}-{role}/private"
            simpMessagingTemplate.convertAndSendToUser(room.getRoomId() + "-" + player.getRole(), "/private", message);
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
    private static class RoomPlayerMessagePayloads {
        private Room room;
        private Player player;
        private Message message;
    }
}
