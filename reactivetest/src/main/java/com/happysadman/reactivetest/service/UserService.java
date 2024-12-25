package com.happysadman.reactivetest.service;

import com.happysadman.reactivetest.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {

    Flux<User> saveManyUsers(List<User> users);

    Mono<User> createUser(User user);

    Flux<User> getAllUsers();

    Mono<Boolean> deleteAllUsers();

    Mono<Boolean> deleteByName(String name);

}
