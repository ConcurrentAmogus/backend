package com.concurrent_programming.amogus.Service;

import org.springframework.stereotype.Service;

import com.concurrent_programming.amogus.Model.GameTimer;

import java.util.Map;
import java.util.HashMap;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TimerService {
    private final Map<String, Map<String, GameTimer>> timers = new HashMap<>();

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    public void startTimer(String roomId, String phase, long duration) {
        stopAllTimers(roomId);
        GameTimer gameTimer = new GameTimer(messagingTemplate);
        gameTimer.setRoomId(roomId);
        gameTimer.setPhase(phase);
        gameTimer.setDuration(duration);
        Map<String, GameTimer> roomTimers = timers.computeIfAbsent(roomId, k -> new HashMap<>());
        roomTimers.put(phase, gameTimer);
        gameTimer.startTimer();
    }

    public void stopTimer(String roomId, String phase) {
        Map<String, GameTimer> roomTimers = timers.get(roomId);
        if (roomTimers != null) {
            GameTimer gameTimer = roomTimers.get(phase);
            if (gameTimer != null) {
                gameTimer.stopTimer();
            }
        }
    }

    private void stopAllTimers(String roomId) {
        Map<String, GameTimer> roomTimers = timers.get(roomId);
        if (roomTimers != null) {
            for (GameTimer timer : roomTimers.values()) {
                timer.stopTimer();
            }
            roomTimers.clear();
        }
    }

    public void handleTimerStartRequest(String roomId, String phase) {
        //the remaining time is pass to frontend, so this duration set need to be 1s more than the remaining time u want
        //e.g. set 16s (display 0-15s)
        Long duration = switch (phase) {
            case "start" -> (long) 6000;
            case "night" -> (long) 16000;
            case "day" -> (long) 31000;
            case "vote" -> (long) 21000;
            default -> (long) 11000;
        };
        this.startTimer(roomId,phase,duration);
    }

    public String getCurrentPhase(String roomId) {
        Map<String, GameTimer> roomTimers = timers.get(roomId);
        if (roomTimers != null) {
            for (Map.Entry<String, GameTimer> entry : roomTimers.entrySet()) {
                if (entry.getValue().isCurrentPhase()) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public long getRemainingTime(String roomId, String phase) {
        Map<String, GameTimer> roomTimers = timers.get(roomId);
        if (roomTimers != null) {
            GameTimer gameTimer = roomTimers.get(phase);
            return gameTimer != null ? gameTimer.getRemainingTime() : 0;
        }
        return 0;
    }
}
