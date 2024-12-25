package com.happysadman.reactivetest.controller;

import com.happysadman.reactivetest.model.User;
import com.happysadman.reactivetest.service.UserService;
import com.happysadman.reactivetest.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Create a new user
    @PostMapping
    public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {
        return userService.createUser(user)
                .map(createdUser -> ResponseEntity.status(HttpStatus.CREATED).body(createdUser))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    // Create multiple users
    @PostMapping("/batch")
    public Flux<User> saveManyUsers(@RequestBody List<User> users) {
        return userService.saveManyUsers(users);
    }

    // Get all users
    @GetMapping("/all")
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Delete all users
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Boolean> deleteAllUsers() {

        return userService.deleteAllUsers();
    }

    // Delete user by name
    @DeleteMapping("/{name}")
    public Mono<ResponseEntity<Boolean>> deleteByName(@PathVariable String name) {
        return userService.deleteByName(name)
                .map(response -> ResponseEntity.ok().body(response))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }
}
