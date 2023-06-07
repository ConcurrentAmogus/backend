package com.concurrent_programming.amogus.Service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TimerService {
    private final Map<String, Map<String, GameTimer>> timers = new HashMap<>();

    private final SimpMessagingTemplate messagingTemplate;
    private final GameTimer gameTimer;

    @Autowired
    public TimerService(SimpMessagingTemplate messagingTemplate, GameTimer gameTimer) {
        this.messagingTemplate = messagingTemplate;
        this.gameTimer = gameTimer;
    }

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
        Map<String, GameTimer> roomTimers = timers.get(roomId);
        if (roomTimers != null && !roomTimers.containsKey(phase)) {
            GameTimer gameTimer = new GameTimer(messagingTemplate);
            gameTimer.setPhase(phase);
            gameTimer.setClientHasSubscribed(true);
            gameTimer.startTimer();
            roomTimers.put(phase, gameTimer);
        }
    }

    public void startTimerForRoomWithPlayerCount(String roomId, int playerCount, String phase, long duration) {
        if (playerCount >= 1) { // Adjust the condition based on your game's requirements
            startTimer(roomId, phase, duration);
        }
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
