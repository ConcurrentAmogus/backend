package com.concurrent_programming.amogus.Model;

import lombok.Data;

import java.util.Map;

@Data
public class Vote {

    private String roomId;
    private String message;

    private User votePlayer;
    private User selectedPlayer;
    private User killedPlayer;
    private User revealedRolePlayer;

    private Map<String, String> votes;

    private boolean gameEnded;
}
