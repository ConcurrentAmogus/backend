package com.concurrent_programming.amogus.Controller;

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
import java.util.*;

@Controller
@RestController
@RequestMapping("/api/room")
@CrossOrigin(origins = {"http://localhost:3000", "https://amogus-five.vercel.app"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class RoomController {

    private final List<Room> roomList = new ArrayList<>();

    @Autowired
    RoomService roomService;
    @Autowired
    TimerService timerService;
    @Autowired
    VoteController voteController;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/check-availability/{roomId}")
    public ResponseEntity<Map<String, String>> checkRoomAvailability(@PathVariable(value = "roomId") String roomId) {
        Map<String, String> response = roomService.getRoomAvailability(roomList, roomId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable(value = "roomId") String roomId) {
        for(Room room: roomList) {
            if (room.getId().equals(roomId)) {
                return ResponseEntity.status(HttpStatus.OK).body(room);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // Mapped as /ws/update-room
    @MessageMapping("/update-room")
    public Room updateRoomInfo(@Payload Room room) {
        System.out.println("***************************************");
        System.out.println("Join Room: \n" +  room);

        synchronized (roomList) {
            List<User> players;
            User newPlayer;
            if (room.isNewRoom()) {
                players = new ArrayList<>();
                newPlayer = room.getNewJoinPlayer();

                players.add(newPlayer);
                newPlayer.setNumber(String.valueOf(players.size()));
                room.setPlayers(players);

                room.setNewRoom(false);
                roomList.add(room);

                String topic = "/room/" + room.getId();
                simpMessagingTemplate.convertAndSend(topic, room);
            } else {
                for (Room r : roomList) {
                    if (r.getId().equals(room.getId())) {
                        players = r.getPlayers();
                        newPlayer = room.getNewJoinPlayer();

                        players.add(newPlayer);
                        newPlayer.setNumber(String.valueOf(players.size()));

                        r.setPlayers(players);
                        room.setPlayers(players);

                        String topic = "/room/" + r.getId();
                        simpMessagingTemplate.convertAndSend(topic, r);
                        break;
                    }
                }
            }
        }
        System.out.println("***************************************");
        System.out.println("Room List: \n" +  roomList);

        return room;
    }

    @MessageMapping("/get-room")
    public void getRoomInfo(@Payload String roomId) {
        synchronized (roomList) {
            for (Room r : roomList) {
                if (r.getId().equals(roomId)) {
                    System.out.println("***************************************");
                    System.out.println("Get Room: \n" + r);

                    String topic = "/room/" + roomId;
                    simpMessagingTemplate.convertAndSend(topic, r);
                    break;
                }
            }
        }
    }

    @MessageMapping("/exit-room")
    public void exitRoom(@Payload Room room) {
        synchronized (roomList) {
            for (Room r: roomList) {
                if (r.getId().equals(room.getId())) {
                    List<User> players = r.getPlayers();
                    players.removeIf(player -> player.getId().equals(room.getExitPlayer().getId()));

                    for (int i = 0; i < players.size(); i++) {
                        players.get(i).setNumber(String.valueOf(i + 1));
                    }

                    r.setPlayers(players);

                    String topic = "/room/" + r.getId();
                    simpMessagingTemplate.convertAndSend(topic, r);
                    break;
                }
            }
        }
    }

    @MessageMapping("/start-game")
    public void startGame(@Payload Room room) throws InterruptedException {
        Runnable thread = () -> {
            System.out.println("***************************************");
            System.out.println("Current thread: \n" + Thread.currentThread());
            System.out.println("Start game: \n" + room);

            int round = 0;
            String result = "";
            Room currentRoom = null;
            String topic = "/room/" + room.getId();

            synchronized (roomList) {
                try {
                    currentRoom = roomService.prepareGame(roomList, room);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            currentRoom.setStatus("STARTING");
            simpMessagingTemplate.convertAndSend(topic, currentRoom);
            timerService.handleTimerStartRequest(currentRoom.getId(), "start");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            currentRoom.setStatus("STARTED");

            do {
                round++;
                currentRoom.setPhase("night");
                simpMessagingTemplate.convertAndSend(topic, currentRoom);
                timerService.handleTimerStartRequest(currentRoom.getId(), currentRoom.getPhase());

                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                currentRoom = voteController.calculateNightVote(currentRoom, "Wolf");
                currentRoom = voteController.calculateNightVote(currentRoom, "Seer");
                simpMessagingTemplate.convertAndSend(topic, currentRoom);

                result = roomService.gameIsEnded(currentRoom);
                if (!result.equals("Continue")) break;

                currentRoom.setPhase("day");
                simpMessagingTemplate.convertAndSend(topic, currentRoom);
                timerService.handleTimerStartRequest(currentRoom.getId(), currentRoom.getPhase());

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                currentRoom = voteController.calculateDayVote(currentRoom);
                simpMessagingTemplate.convertAndSend(topic, currentRoom);

                result = roomService.gameIsEnded(currentRoom);

                if (round == 10) break;
            } while (result.equals("Continue"));

            if (result.equals("Wolf wins")) {
                currentRoom.setWinner("Wolf");
            } else {
                currentRoom.setWinner("Villager");
            }
            currentRoom.setStatus("ENDED");
            simpMessagingTemplate.convertAndSend(topic, currentRoom);
        };

        Thread run = new Thread(thread);

        run.start();
    }
}
