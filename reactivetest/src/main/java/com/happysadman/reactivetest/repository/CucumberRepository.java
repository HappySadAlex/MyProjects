package com.happysadman.reactivetest.repository;

import com.happysadman.reactivetest.model.Cucumber;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CucumberRepository extends ReactiveMongoRepository<Cucumber, UUID> {



}
