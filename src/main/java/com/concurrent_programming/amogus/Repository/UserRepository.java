package com.concurrent_programming.amogus.Repository;

import com.concurrent_programming.amogus.Model.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface UserRepository {

    public List<User> findAll() throws ExecutionException, InterruptedException;
    public User findById(String documentId) throws ExecutionException, InterruptedException;
    public User create(User user);
    public User update(User user) throws ExecutionException, InterruptedException;
    public String delete(String documentId) throws ExecutionException, InterruptedException;
}
