package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Model.Room;
import com.concurrent_programming.amogus.Model.User;
import com.concurrent_programming.amogus.Model.Vote;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class VoteController {

    private final Map<String, Vote> voteList = new HashMap<>();  // <roomId, Vote>

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/vote-night")
    public void updateNightVote(@Payload Vote vote) {
        System.out.println("***************************************");
        System.out.println("Night vote: \n" + vote);

        User votePlayer = vote.getVotePlayer();
        User selectedPlayer = vote.getSelectedPlayer();

        if (votePlayer.getRole().equals("Wolf")) {
            Map<String, String> wolfVotes = vote.getVotes();
            if (wolfVotes == null) {
                wolfVotes = new HashMap<>();
            }
            wolfVotes.put(votePlayer.getNumber(), selectedPlayer.getNumber());
            vote.setVotes(wolfVotes);

        } else if (votePlayer.getRole().equals("Seer")) {
            Map<String, String> seerVotes = vote.getVotes();
            if (seerVotes == null) {
                seerVotes = new HashMap<>();
            }
            seerVotes.put(votePlayer.getNumber(), selectedPlayer.getNumber());
            vote.setVotes(seerVotes);
        }

        voteList.put(vote.getRoomId() + "-" + votePlayer.getRole(), vote); // <12345-Wolf, vote>

        System.out.println("***************************************");
        System.out.println("Night vote after: \n" + vote);
        System.out.println("***************************************");
        System.out.println("vote list: \n" + voteList);

        String topic = "/vote/" + vote.getRoomId() + "-" + votePlayer.getRole() + "/night";;
        simpMessagingTemplate.convertAndSend(topic, vote);
    }

    public Room calculateNightVote(Room room, String role) {
        Vote vote = voteList.get(room.getId() + "-" + role);

        if (vote == null) {
            Vote voteData = new Vote();
            voteData.setMessage("No one has been killed tonight");

            String topic = "/vote/" + room.getId() + "-" + role + "/night";
            simpMessagingTemplate.convertAndSend(topic, voteData);
            return room;
        }

        Map<String, String> votes = vote.getVotes();
        Map<String, Integer> voteCounts = new HashMap<>();

        for (String playerNum : votes.values()) {
            voteCounts.put(playerNum, voteCounts.getOrDefault(playerNum, 0) + 1);
        }

        List<String> playersWithHighestVotes = new ArrayList<>();
        int highestVoteCount = 0;
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            String playerNum = entry.getKey();
            int voteCount = entry.getValue();

            if (voteCount > highestVoteCount) {
                highestVoteCount = voteCount;
                playersWithHighestVotes.clear();
                playersWithHighestVotes.add(playerNum);
            } else if (voteCount == highestVoteCount) {
                playersWithHighestVotes.add(playerNum);
            }
        }

        Vote voteData = new Vote();
        voteData.setRoomId(room.getId());
        String eliminatedOrRevealedPlayerNum = "";
        if (playersWithHighestVotes.size() == 1) {
            eliminatedOrRevealedPlayerNum = playersWithHighestVotes.get(0);

            String finalEliminatedOrRevealedPlayerNum = eliminatedOrRevealedPlayerNum;
            if (role.equals("Wolf")) {
                room.getPlayers().forEach(player -> {
                    if (player.getNumber().equals(finalEliminatedOrRevealedPlayerNum)) {
                        player.setAlive(false);
                        voteData.setKilledPlayer(player);
                        voteData.setMessage(finalEliminatedOrRevealedPlayerNum + " has been killed.");
                    }
                });
            } else {
                room.getPlayers().forEach(player -> {
                    if (player.getNumber().equals(finalEliminatedOrRevealedPlayerNum)) {
                        voteData.setRevealedRolePlayer(player);
                        voteData.setMessage(finalEliminatedOrRevealedPlayerNum + " is a " + player.getRole());
                    }
                });
            }
        } else {
            voteData.setMessage("No one has been killed tonight.");
        }

        System.out.println("***************************************");
        System.out.println("VoteData: \n" + voteData);
        System.out.println("***************************************");
        System.out.println("Room after vote: \n" + room);

        String topic = "/vote/" + room.getId() + "-" + role + "/night";
        simpMessagingTemplate.convertAndSend(topic, voteData);
        return room;
    }

    @MessageMapping("/vote-day")
    public void updateDayVote(@Payload Vote vote) {
        System.out.println("***************************************");
        System.out.println("Day vote: \n" + vote);

        Map<String, String> votes = vote.getVotes();

        if (votes == null) {
            votes = new HashMap<>();
        }

        User votePlayer = vote.getVotePlayer();
        User selectedPlayer = vote.getSelectedPlayer();

        votes.put(votePlayer.getNumber(), selectedPlayer.getNumber());
        vote.setVotes(votes);

        voteList.put(vote.getRoomId(), vote);

        System.out.println("***************************************");
        System.out.println("Day vote after: \n" + vote);
        System.out.println("***************************************");
        System.out.println("vote list: \n" + voteList);

        String topic = "/vote/" + vote.getRoomId() + "/day";
        simpMessagingTemplate.convertAndSend(topic, vote);
    }

    public Room calculateDayVote(Room room) {
        Vote vote = voteList.get(room.getId());

        if (vote == null) {
            Vote voteData = new Vote();
            voteData.setMessage("No one has been killed tonight");

            String topic = "/vote/" + room.getId() + "/day";
            simpMessagingTemplate.convertAndSend(topic, voteData);
            return room;
        }

        Map<String, String> votes = vote.getVotes();
        Map<String, Integer> voteCounts = new HashMap<>();

        int numOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (String playerNum : votes.values()) {
            tasks.add(() -> {
                synchronized (voteCounts) {
                    voteCounts.put(playerNum, voteCounts.getOrDefault(playerNum, 0) + 1);
                }
                return null;
            });
        }

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }

        List<String> playersWithHighestVotes = new ArrayList<>();
        int highestVoteCount = 0;
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            String playerNum = entry.getKey();
            int voteCount = entry.getValue();

            if (voteCount > highestVoteCount) {
                highestVoteCount = voteCount;
                playersWithHighestVotes.clear();
                playersWithHighestVotes.add(playerNum);
            } else if (voteCount == highestVoteCount) {
                playersWithHighestVotes.add(playerNum);
            }
        }

        Vote voteData = new Vote();
        voteData.setRoomId(room.getId());
        String eliminatedOrRevealedPlayerNum = "";

        if (playersWithHighestVotes.size() == 1) {
            eliminatedOrRevealedPlayerNum = playersWithHighestVotes.get(0);

            String finalEliminatedOrRevealedPlayerNum = eliminatedOrRevealedPlayerNum;
            room.getPlayers().forEach(player -> {
                if (player.getNumber().equals(finalEliminatedOrRevealedPlayerNum)) {
                    player.setAlive(false);
                    voteData.setKilledPlayer(player);
                    voteData.setMessage(finalEliminatedOrRevealedPlayerNum + " has been killed.");
                }
            });
        } else {
            voteData.setMessage("No one has been killed tonight.");
        }

        String topic = "/vote/" + room.getId() + "/day";
        simpMessagingTemplate.convertAndSend(topic, voteData);
        return room;
    }
}
