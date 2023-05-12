package com.concurrent_programming.amogus.Service;

import com.concurrent_programming.amogus.Model.User;
import com.concurrent_programming.amogus.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> findAllUsers() throws ExecutionException, InterruptedException {
        return userRepository.findAll();
    }

    public User findUserById(String id) throws ExecutionException, InterruptedException {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.create(user);
    }

    public User updateUser(User user) throws ExecutionException, InterruptedException {
        return userRepository.update(user);
    }

    public String deleteUser(String id) throws ExecutionException, InterruptedException {
        return userRepository.delete(id);
    }

}
