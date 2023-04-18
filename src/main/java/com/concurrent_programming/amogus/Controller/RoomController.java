package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RoomController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    // Mapped as /ws/update-room
    @MessageMapping("/update-room")
    public Room updateRoomInfo(@Payload Room room) {
        try {
            // topic = "/room/{roomId}"
            simpMessagingTemplate.convertAndSend("/room/" + room.getRoomId(), room);
            System.out.println("Room Info: " +  room);
            return room;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update the room: " + ex.getMessage());
        }
    }
}
