package com.concurrent_programming.amogus.Service;

import com.concurrent_programming.amogus.Model.Room;
import com.concurrent_programming.amogus.Model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class RoomService {

    public Map<String, String> getRoomAvailability(List<Room> roomList, String roomId) {
        System.out.println("***************************************");
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

    public Room prepareGame(List<Room> roomList, Room room) throws InterruptedException {
        System.out.println("***************************************");
        System.out.println("Prepare game: \n" + room);

        for (Room r : roomList) {
            if (r.getId().equals(room.getId())) {
                List<User> players = r.getPlayers();
                int[] numOfSeerWolf = new int[]{0, 0}; // [seer, wolf]

                if (players.size() <= 6) {
                    numOfSeerWolf[0] = 1;
                    numOfSeerWolf[1] = 1;
                } else if (players.size() <= 9) {
                    numOfSeerWolf[0] = 1;
                    numOfSeerWolf[1] = 2;
                } else {
                    numOfSeerWolf[0] = 2;
                    numOfSeerWolf[1] = 3;
                }

                Random random = new Random();
                for (int i = 0; i < numOfSeerWolf.length; i++) {
                    for (int j = 0; j < numOfSeerWolf[i];) {
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
                    player.setAlive(true);
                }

                r.setPlayers(players);
                return r;
            }
        }
        return null;
    }

    public String gameIsEnded(Room room) {
        List<User> players = room.getPlayers();

        int wolfCounts = 0;
        int villagerCount = 0; // seer + villager

        for (User player: players) {
            if (player.isAlive()) {
                if (player.getRole().equals("Wolf")) wolfCounts++;
                else villagerCount++;
            }
        }

        if (wolfCounts >= villagerCount) {
            return "Wolf wins";
        } else if (wolfCounts <= 0) {
            return "Villager wins";
        } else {
            return "Continue";
        }
    }
}
