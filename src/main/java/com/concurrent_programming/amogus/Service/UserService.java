package com.concurrent_programming.amogus.Service;

import com.concurrent_programming.amogus.Model.User;

import java.util.List;
import java.util.concurrent.ExecutionException;


public interface UserService {

    public List<User> findAllUsers() throws ExecutionException, InterruptedException;

    public User findUserById(String id) throws ExecutionException, InterruptedException;

    public User createUser(User user);

    public User updateUser(User user) throws ExecutionException, InterruptedException;

    public String deleteUser(String id) throws ExecutionException, InterruptedException;
}
