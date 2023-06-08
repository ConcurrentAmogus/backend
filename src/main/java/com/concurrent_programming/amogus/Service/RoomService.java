package com.concurrent_programming.amogus.Service;

import com.concurrent_programming.amogus.Model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomService {

    public Map<String, String> getRoomAvailability(List<Room> roomList, String roomId) {
        System.out.println("checking" + roomId);
        System.out.println("checking roomlist " + roomList);

        Map<String, String> roomAvailability = new HashMap<>();

        if (roomList.size() == 0) {
            roomAvailability.put("available", "false");
            roomAvailability.put("reason", "NOT_EXISTED");
        } else {
            for (Room room: roomList) {
                if (room.getId().equals(roomId)) {
                    if (room.getStatus().equals("START")) {
                        roomAvailability.put("available", "false");
                        roomAvailability.put("reason", "STARTED");
                    } else if (room.getStatus().equals("END")) {
                        roomAvailability.put("available","false");
                        roomAvailability.put("reason", "ENDED");
                    } else if (room.getPlayers().size() == 16) {
                        roomAvailability.put("available","false");
                        roomAvailability.put("reason", "FULL");
                    } else {
                        roomAvailability.put("available", "true");
                        roomAvailability.put("reason", "WAITING");
                    }
                    break;
                } else {
                    roomAvailability.put("available", "false");
                    roomAvailability.put("reason", "NOT_EXISTED");
                }
            }
        }
        return roomAvailability;
    }
}
