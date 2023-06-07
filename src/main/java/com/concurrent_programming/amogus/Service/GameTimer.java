package com.concurrent_programming.amogus.Service;

import org.springframework.stereotype.Service;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GameTimer {
    private Timer timer;
    private TimerTask timerTask;
    private long duration;
    private long remainingTime;
    private String phase;
    private String roomId;
    private boolean isCurrentPhase;
    private AtomicBoolean clientHasSubscribed = new AtomicBoolean(false);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public GameTimer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.isCurrentPhase = false;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setClientHasSubscribed(boolean clientHasSubscribed) {
        this.clientHasSubscribed.set(clientHasSubscribed);
    }

    public void setDuration(long duration) {
        this.duration = duration;
        this.remainingTime = duration;
    }

    public void checkAndStartTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (clientHasSubscribed.get()) {
                    startTimer();
                    this.cancel();
                }
            }
        }, 0, 1000);
    }

    public void startTimer() {
        this.isCurrentPhase = true;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (remainingTime <= 0) {
                    stopTimer();
                } else {
                    remainingTime -= 1000;
                    System.out.println("remaining time: " + remainingTime);
                    messagingTemplate.convertAndSend("/remaining-time/" +roomId + "/" + phase, remainingTime);
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stopTimer() {
        this.isCurrentPhase = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public boolean isCurrentPhase() {
        return isCurrentPhase;
    }
}