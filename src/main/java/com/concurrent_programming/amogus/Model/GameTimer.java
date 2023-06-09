package com.concurrent_programming.amogus.Model;

import org.springframework.stereotype.Service;

import lombok.Data;

import java.util.Timer;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class GameTimer {
    private Timer timer;
    private TimerTask timerTask;
    private long duration;
    private long remainingTime;
    private String phase;
    private String roomId;
    private boolean isCurrentPhase;
    private AtomicBoolean clientHasSubscribed;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public GameTimer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.isCurrentPhase = false;
        this.clientHasSubscribed = new AtomicBoolean(false);
    }

    public void setClientHasSubscribed(boolean clientHasSubscribed) {
        this.clientHasSubscribed.set(clientHasSubscribed);
    }

    public void setDuration(long duration) {
        this.duration = duration;
        this.remainingTime = duration;
    }

    public boolean isCurrentPhase() {
        return isCurrentPhase;
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
                    try {
                        System.out.println("remaining time: " + remainingTime);
                        remainingTime -= 1000;
                        messagingTemplate.convertAndSend("/timer/" + roomId , remainingTime);
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to handle timer");
                    }
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
}