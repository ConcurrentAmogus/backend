package com.concurrent_programming.amogus.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class User {

    private String id;
    private String username;
    private List<Integer> record; // 0 - win, 1 - lose

    // game state
    private String role;
    private boolean isAlive;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", record=" + record +
                ", role='" + role + '\'' +
                ", isAlive=" + isAlive +
                '}';
    }
}
