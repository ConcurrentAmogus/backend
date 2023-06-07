package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Service.TimerService;
import com.concurrent_programming.amogus.Model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timer")
@CrossOrigin(origins = {"http://localhost:3000", "https://amogus-five.vercel.app"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class TimerController {
    private final TimerService timerService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TimerController(TimerService timerService, SimpMessagingTemplate messagingTemplate) {
        this.timerService = timerService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/ws/start-timer/{roomId}/{phase}")
    public void handleTimerStartRequest(String roomId,String phase) {
        timerService.handleTimerStartRequest(roomId, phase);
    }

    @PostMapping("/start")
    public void startTimer(String roomId, String phase, long duration) {
        timerService.startTimer(roomId, phase, duration);
    }

    @PostMapping("/stop")
    public void stopTimer(String roomId, String phase) {
        timerService.stopTimer(roomId, phase);
    }

    @GetMapping("/remaining")
    public long getRemainingTime(String roomId, String phase) {
        return timerService.getRemainingTime(roomId, phase);
    }

    @GetMapping("/current-phase")
    public ResponseEntity<String> getCurrentPhase(String roomId) {
        String currentPhase = timerService.getCurrentPhase(roomId);
        if (currentPhase != null) {
            return new ResponseEntity<>(currentPhase, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}