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

    @MessageMapping("/vote-private")
    public void updateVote(@Payload Vote vote) {
        System.out.println("***************************************");
        System.out.println("vote input: \n" + vote);

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
        System.out.println("vote list: \n" + voteList);

        String topic = "/vote/" + vote.getRoomId() + "-" + votePlayer.getRole() + "/night";;
        simpMessagingTemplate.convertAndSend(topic, vote);
    }

    public Room calculateNightVote(Room room, String role) {
        Map<String, String> votes = voteList.get(room.getId() + "-" + role).getVotes();
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
        String eliminatedOrRevealedPlayerNum = "";
        String topic = "/vote/" + room.getId() + "-" + role + "/night";
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

        simpMessagingTemplate.convertAndSend(topic, voteData);
        return room;
    }
}
