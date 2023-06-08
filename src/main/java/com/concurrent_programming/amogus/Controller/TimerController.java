package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Service.TimerService;
import com.concurrent_programming.amogus.Model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/timer")
@CrossOrigin(origins = {"http://localhost:3000", "https://amogus-five.vercel.app"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class TimerController {

    @Autowired
    TimerService timerService;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/start-timer")
    public void handleTimerStartRequest(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        String phase = payload.get("phase");
        System.out.println("Received timer start request for room: " + roomId + ", phase: " + phase);
        timerService.handleTimerStartRequest(roomId, phase);
    }

    @PostMapping("/start")
    public void startTimer(@RequestParam String roomId, @RequestParam String phase, @RequestParam long duration) {
        timerService.startTimer(roomId, phase, duration);
    }

    @PostMapping("/stop")
    public void stopTimer(@RequestParam String roomId, @RequestParam String phase) {
        timerService.stopTimer(roomId, phase);
    }

    @GetMapping("/remaining")
    public long getRemainingTime(@RequestParam String roomId, @RequestParam String phase) {
        return timerService.getRemainingTime(roomId, phase);
    }

    @GetMapping("/current-phase")
    public ResponseEntity<String> getCurrentPhase(@RequestParam String roomId) {
        String currentPhase = timerService.getCurrentPhase(roomId);
        if (currentPhase != null) {
            return new ResponseEntity<>(currentPhase, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}