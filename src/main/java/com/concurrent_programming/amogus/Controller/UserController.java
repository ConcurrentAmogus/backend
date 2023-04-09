package com.concurrent_programming.amogus.Controller;

import com.concurrent_programming.amogus.Model.User;
import com.concurrent_programming.amogus.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:3000", "https://amogus-five.vercel.app/"}, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() throws ExecutionException, InterruptedException {
        List<User> users = userService.getAllUsers();
        if (users != null) {
            return ResponseEntity.status(HttpStatus.OK).body(users);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requested resource not found");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable(value = "id") String id) throws ExecutionException, InterruptedException {
        User user = userService.getUser(id);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id '" + id + "'");
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        if (newUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        User updateUser = userService.updateUser(user);
        if (updateUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updateUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id '" + user.getId() + "'");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") String id) throws ExecutionException, InterruptedException {
        String msg = userService.deleteUser(id);
        if (msg.equals("Deleted")) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted user with document id '" + id + "' successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id '" + id + "'");
    }
}
