package com.happysadman.reactivetest.repository;

import com.happysadman.reactivetest.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, UUID> {

    @Query(value = "{'userName': ?0}", delete = true)
    Mono<Void> deleteByUserName(String name);

}
