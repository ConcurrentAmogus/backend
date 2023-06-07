package com.concurrent_programming.amogus.Model;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameState {

    private String id;
    private List<User> user;
    private User currentPlayerSurvived;
    private String selectedPlayer;
    private boolean inLobby;

    public boolean isGameInProgress() {
        return false;
    }

    public void addPlayer(String id) {
    }

    public User currentPlayerSurvived() {
        return this.currentPlayerSurvived;
    }
}