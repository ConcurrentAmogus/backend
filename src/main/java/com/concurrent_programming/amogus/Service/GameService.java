package com.concurrent_programming.amogus.Service;

import com.concurrent_programming.amogus.Model.GameState;
import com.concurrent_programming.amogus.Model.User;
import com.concurrent_programming.amogus.Repository.GameRepository;
import com.concurrent_programming.amogus.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GameService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    
    public void joinGame(User user) throws ExecutionException, InterruptedException {
        if(userRepository.existsById(user.getId())){
            throw new RuntimeException("User already joined the game.");
        }
        GameState gameState = getGameState();
        if(!gameState.isGameInProgress()){
            userRepository.save(user);
            gameState.addPlayer(user.getId());
            updateGameState(gameState);
        }else{
            throw new RuntimeException("Game has already started.");
        }
    }
    public void leaveGame(String id) throws ExecutionException, InterruptedException {
        userRepository.delete(id);
        //additional logic to leave game 
    }
    public void assignRoles(List<User> users){
        //havent write
    }
    public void startGame() throws ExecutionException, InterruptedException {
        List<User> users = userRepository.findAll();
        if(users.size()<5){
            throw new RuntimeException("Minimum 5 players to start the game");
        }
        if(users.size()>12){
            throw new RuntimeException("Error");
        }
        assignRoles(users);
    }
    public GameState getGameState(String id) {
        GameState currentGameState = userRepository.findById(id);
        return currentGameState;
    }
    public void updateGameState(GameState gameState) throws ExecutionException, InterruptedException {
        userRepository.save(gameState);
    }
}
