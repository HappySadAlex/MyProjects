package com.happysadman.reactivetest.service;

import com.happysadman.reactivetest.model.Cucumber;
import com.happysadman.reactivetest.model.Pot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CucumberService {

    Flux<Pot> putInPot(Flux<Cucumber> cucumbers, Float size);

    Flux<Cucumber> saveManyCucumbers(List<Cucumber> cucumbers);

    Mono<Void> deleteAll();


}
