package com.concurrent_programming.amogus.Service;

import com.concurrent_programming.amogus.Model.User;
import com.concurrent_programming.amogus.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        return userRepository.getAllUsers();
    }

    public User getUser(String id) throws ExecutionException, InterruptedException {
        return userRepository.getUser(id);
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User updateUser(User user) throws ExecutionException, InterruptedException {
        return userRepository.updateUser(user);
    }

    public String deleteUser(String id) throws ExecutionException, InterruptedException {
        return userRepository.deleteUser(id);
    }

}
