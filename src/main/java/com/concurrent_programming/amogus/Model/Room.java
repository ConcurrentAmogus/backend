package com.concurrent_programming.amogus.Model;

import lombok.Data;

import java.util.List;

@Data
public class Room {

    private String id;
    private String status;
    private boolean isNewRoom;
    private String cycle;           // night / day

    private User host;
    private User newJoinPlayer;
    private User exitPlayer;
    private List<User> players;

}
