package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Model.Game;
import com.concurrent_programming.amogus.Model.Room;
import com.concurrent_programming.amogus.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.concurrent_programming.amogus.Service.*;
import com.concurrent_programming.amogus.Service.*;

import java.util.*;

@Controller
@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = {"http://localhost:3000", "https://amogus-five.vercel.app"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class RoomController {

    private final List<Room> roomList = new ArrayList<>();

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
                if (room.getId().equals(roomId)) {
                    if (room.getStatus().equals("START")) {
                        response.put("available", "false");
                        response.put("reason", "STARTED");
                    } else if (room.getStatus().equals("END")) {
                        response.put("available","false");
                        response.put("reason", "ENDED");
                    } else if (room.getPlayers().size() == 16) {
                        response.put("available","false");
                        response.put("reason", "FULL");
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
            System.out.println("Room Info: \n" +  room);
            synchronized (roomList) {
                List<User> players;
                if (room.isNewRoom()) {
                    players = new ArrayList<>();
                    players.add(room.getNewJoinPlayer());
                    room.setPlayers(players);

                    room.setNewRoom(false);
                    roomList.add(room);
                } else {
                    Room currentRoom = null;
                    for (Room r : roomList) {
                        if (r.getId().equals(room.getId())) {
                            currentRoom = r;
                        }
                    }
                    if (currentRoom != null) {
                        players = currentRoom.getPlayers();
                        players.add(room.getNewJoinPlayer());
                        room.setPlayers(players);
                    }
                }

                String topic = "/room/" + room.getId();
                simpMessagingTemplate.convertAndSend(topic, room);

                System.out.println("Room List: \n" +  roomList);
            }
            return room;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to update the room: " + ex.getMessage());
        }
    }

    @MessageMapping("/get-room")
    public void getRoomInfo(String roomId) {
        for(Room r: roomList) {
            if (r.getId().equals(roomId)) {
                String topic = "/room/" + roomId;
                simpMessagingTemplate.convertAndSend(topic, r);
                break;
            }
        }
    }

    @MessageMapping("/exit-room")
    public void exitRoom(Room room) {
        synchronized (roomList) {
            for (Room r: roomList) {
                if (r.getId().equals(room.getId())) {
                    List<User> players = r.getPlayers();
                    players.removeIf(player -> player.getId().equals(room.getExitPlayer().getId()));
                    r.setPlayers(players);

                    String topic = "/room/" + r.getId();
                    simpMessagingTemplate.convertAndSend(topic, r);
                    break;
                }
            }
        }
    }

    @MessageMapping("/start-game")
    public void startGame(Room room) {
        synchronized (roomList) {
            for (Room r : roomList) {
                if (r.getId().equals(room.getId())) {
                    List<User> players = r.getPlayers();
                    int[] numOfWolfSeer = new int[]{0, 0}; // [seer, wolf]

                    if (players.size() <= 6) {
                        numOfWolfSeer[0] = 1;
                        numOfWolfSeer[1] = 1;
                    } else if (players.size() <= 9) {
                        numOfWolfSeer[0] = 1;
                        numOfWolfSeer[1] = 2;
                    } else {
                        numOfWolfSeer[0] = 2;
                        numOfWolfSeer[1] = 3;
                    }

                    Random random = new Random();
                    for (int i = 0; i < numOfWolfSeer.length; i++) {
                        for (int j = 0; j < numOfWolfSeer[i];) {
                            int index = random.nextInt(players.size());
                            if (players.get(index).getRole() == null) {
                                if (i == 0) players.get(index).setRole("Seer");
                                else players.get(index).setRole("Wolf");
                                j++;
                            }
                        }
                    }

                    for (User player : players) {
                        if (player.getRole() == null) {
                            player.setRole("Villager");
                        }
                    }

                    r.setPlayers(players);
                    r.setStatus("STARTED");
                    r.setPhase("night");

                    System.out.println("Game start: \n" + r);

                    String topic = "/room/" + r.getId();
                    simpMessagingTemplate.convertAndSend(topic, r);
                    break;
                }
            }
        }
    }
}
