package com.concurrent_programming.amogus.Service;

import com.concurrent_programming.amogus.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;


public interface UserService {

    public List<User> getAllUsers() throws ExecutionException, InterruptedException;

    public User getUser(String id) throws ExecutionException, InterruptedException;

    public User createUser(User user);

    public User updateUser(User user) throws ExecutionException, InterruptedException;

    public String deleteUser(String id) throws ExecutionException, InterruptedException;
}
