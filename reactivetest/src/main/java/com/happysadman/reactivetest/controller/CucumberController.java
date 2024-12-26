package com.happysadman.reactivetest.controller;

import com.happysadman.reactivetest.model.Cucumber;
import com.happysadman.reactivetest.model.Pot;
import com.happysadman.reactivetest.service.CucumberService;
import com.happysadman.reactivetest.service.impl.CucumberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/v1/cucumbers")
@RequiredArgsConstructor
public class CucumberController {

    private final CucumberService cucumberService;

    // Save many cucumbers
    @PostMapping("/saveAll")
    public Flux<Cucumber> saveManyCucumbers(@RequestBody List<Cucumber> cucumbers) {
        return cucumberService.saveManyCucumbers(cucumbers);
    }

    @GetMapping("/putinpot")
    public Flux<Pot> putAllCucumbersIntoPots(@RequestBody List<Cucumber> cucumbers){
        return cucumberService.putInPot(Flux.fromIterable(cucumbers), 1F);
    }

    @DeleteMapping
    public Mono<Void> deleteAll(){
        return cucumberService.deleteAll();
    }

}
