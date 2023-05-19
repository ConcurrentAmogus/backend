package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Model.Game;
import com.concurrent_programming.amogus.Model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = {"http://localhost:3000", "https://amogus-five.vercel.app"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class RoomController {

    private List<Room> roomList = new ArrayList<>();

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/check-availability/{roomId}")
    public ResponseEntity<Map<String, String>> checkRoomAvailability(@PathVariable(value = "roomId") String roomId) {
        System.out.println("checking" + roomId);
        System.out.println("checking roomlist " + roomList);
        Map<String, String> response = new HashMap<>();

        if (roomList.size() == 0) {
            response.put("available", "false");
            response.put("reason", "NOT_EXISTED");
        } else {
            for (Room room: roomList) {
                System.out.println(room.getId().equals(roomId));
                System.out.println(room.getId());
                if (room.getId().equals(roomId)) {
                    if (room.getStatus().equals("START")) {
                        response.put("available", "false");
                        response.put("reason", "STARTED");
                    } else if (room.getStatus().equals("END")) {
                        response.put("available","false");
                        response.put("reason", "ENDED");
                    } else {
                        response.put("available", "true");
                        response.put("reason", "WAITING");
                    }
                    break;
                } else {
                    response.put("available", "false");
                    response.put("reason", "NOT_EXISTED");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Mapped as /ws/update-room
    @MessageMapping("/update-room")
    public Room updateRoomInfo(@Payload Room room) {
        try {
            System.out.println("Room Info: " +  room);
            if (room.isNewRoom()) {
                roomList.add(room);
                room.setNewRoom(false);
            }

            String topic = "/room/" + room.getId();
            simpMessagingTemplate.convertAndSend(topic, room);

            System.out.println("Room List: " +  roomList);
            return room;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update the room: " + ex.getMessage());
        }
    }
}
