package com.concurrent_programming.amogus.Repository;

import com.concurrent_programming.amogus.Model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserRepository {

    public List<User> getAllUsers() throws ExecutionException, InterruptedException;
    public User getUser(String documentId) throws ExecutionException, InterruptedException;
    public User createUser(User user);
    public User updateUser(User user) throws ExecutionException, InterruptedException;
    public String deleteUser(String documentId) throws ExecutionException, InterruptedException;
}
