package com.happysadman.reactivetest.service.impl;

import com.happysadman.reactivetest.model.Cucumber;
import com.happysadman.reactivetest.model.Pot;
import com.happysadman.reactivetest.repository.CucumberRepository;
import com.happysadman.reactivetest.service.CucumberService;
import com.happysadman.reactivetest.util.GenerateUUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CucumberServiceImpl implements CucumberService {

    private final CucumberRepository cucumberRepo;


    /*
         на вход принимает Flax<Огурцов> и возвращает Flax<Банок>.
         Данный метод должен сгруппировать огурцы по банкам.
                Примечания.
        1) если цельный огурец не влазит в банку,
           отрезать от огурца столько, сколько может поместиться в эту банку,
           а часть которая осталась, положить в следующую банку.
    */

   /* public Flux<Cucumber> produceToPot(Flux<Cucumber> cucumbers){
        //посчитать potMaxSize % cucumber.size и использовать в качестве размера
        Float potSize = 3.1F;
        Float cucumberSize = 0.3F;
        return cucumbers.window((int) (potSize % cucumberSize));
    }*/


    @Override
    public Flux<Pot> putInPot(Flux<Cucumber> cucumbers, Float maxPotSize) {
        return cucumbers.concatMap(cucumber -> {
            List<Pot> pots = new ArrayList<>();
            Float remainingCucumberSize = cucumber.getSize();

            while (remainingCucumberSize > 0) {
                Pot pot = Pot.builder()
                        .maxSize(maxPotSize)
                        .cucumbers(new ArrayList<>())
                        .build();

                Float potSize = Math.min(remainingCucumberSize, maxPotSize);
                remainingCucumberSize -= potSize;

                if (potSize > 0) {
                    Cucumber potCucumber = Cucumber.builder()
                            .size(potSize).build();
                    pot.getCucumbers().add(potCucumber);
                }

                pots.add(pot);
            }

            return Flux.fromIterable(pots);
        });
    }

    @Override
    public Flux<Cucumber> saveManyCucumbers(List<Cucumber> cucumbers) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }
}


    /*@Override
    public Mono<Void> deleteAll(){
        return cucumberRepo.deleteAll().then();
    }


    @Override
    public Flux<Cucumber> saveManyCucumbers(List<Cucumber> cucumbers) {
        return Flux.fromIterable(cucumbers);
    }*/

