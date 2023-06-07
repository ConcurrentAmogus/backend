package com.concurrent_programming.amogus.Controller;
import com.concurrent_programming.amogus.Model.GameState;
import com.concurrent_programming.amogus.Model.User;
import com.concurrent_programming.amogus.Service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService){
        this.gameService = gameService;
    }
    SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/join")
    public void joinGame(@RequestBody User user){
        gameService.joinGame(user);
    }

    //maybe will have other http endpoints

    @MessageMapping("/start")
    @SendTo("/topic/gameState")
    public GameState startGame(){
        GameState gameState = new GameState();
        return gameState;
    }

    //maybe will have other web socket endpoints
}
