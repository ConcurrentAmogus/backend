package com.concurrent_programming.amogus.Service;

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
    
    public void joinGame(User user) {
        //userRepository.save(user);
    }
}
