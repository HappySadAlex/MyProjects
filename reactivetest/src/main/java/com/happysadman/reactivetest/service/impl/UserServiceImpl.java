package com.happysadman.reactivetest.service.impl;

import com.happysadman.reactivetest.model.User;
import com.happysadman.reactivetest.repository.UserRepository;
import com.happysadman.reactivetest.service.UserService;
import com.happysadman.reactivetest.util.GenerateUUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Override
    public Flux<User> saveManyUsers(List<User> users) {

        return Flux.fromIterable(users)
                .flatMap(user -> GenerateUUID.generateUUID()
                                .map(uuid -> user.toBuilder()
                                        .userId(uuid)
                                        .createdAt(Instant.now())
                                        .changedAt(Instant.now())
                                        .build())
                )
                .flatMap(userRepo::save);
    }

    @Override
    public Mono<User> createUser(User user) {
        return GenerateUUID.generateUUID()
                .map(userId -> User.builder()
                        .userId(userId)
                        .userName(user.getUserName())
                        .userInfo(user.getUserInfo())
                        .createdAt(Instant.now())
                        .changedAt(Instant.now())
                        .build())
                .flatMap(userRepo::save);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Mono<Boolean> deleteAllUsers() {
        return userRepo.deleteAll()
                .then(Mono.just(true))
                .onErrorReturn(false);
    }

    @Override
    public Mono<Boolean> deleteByName(String name) {

        return userRepo.deleteByUserName(name)
                .then(Mono.just(true))
                .onErrorReturn(false);
    }

}
