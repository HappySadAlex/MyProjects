package org.example;

import org.example.entity.Cucumber;
import org.example.entity.Pot;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    static {
        BlockHound.install();
    }

    public static void main(String[] args) {
       /* Mono<Object> objectMono = Mono.fromRunnable(() -> {
            try {
                System.out.println("Sleeping start");
                Thread.sleep(10);
                System.out.println("Sleeping done");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).log();
        objectMono.subscribe();*/

        //Для тестов - общий размер 12
        List<Cucumber> cucumberList = Arrays.asList(
                Cucumber.builder().size(3).build(),
                Cucumber.builder().size(1).build(),
                Cucumber.builder().size(2).build(),
                Cucumber.builder().size(1).build(),
                Cucumber.builder().size(4).build(),
                Cucumber.builder().size(1).build());

        // c3 -> c1 -> c2 -> c1 -> c4 -> c1      must be:       p -> p -> p
        Flux<Cucumber> cucumbers = Flux.fromIterable(cucumberList);

        Flux<Pot> result = groupCucumbersIntoPots(cucumbers, 12)
                .log();

        result.subscribe();
        List<Pot> block = result.collectList().block();
        for(int i = 1; i <= block.size(); i++){
            Pot pot = block.get(i - 1);
            System.out.println("Pot number: " + i);
            for (Cucumber c : pot.getCucumbers()){
                System.out.println("Cuc size: " + c.getSize());
            }
        }
        result.collectList().block().stream().forEach(pot -> pot.getCucumbers().forEach(System.out::println));
        System.out.println("Count of pots: " + result.collectList().block().size());
    }

    public static Flux<Pot> groupCucumbersIntoPots(Flux<Cucumber> cucumbers, Integer potMaxSize) {
        return cucumbers
                // Группируем огурцы в банки
                .scan(new ArrayList<Pot>(), (pots, cucumber) -> {
                    Pot pot = pots.isEmpty() ? new Pot(new ArrayList<>(), potMaxSize)
                            : pots.get(pots.size() == 1 ? 0 : pots.size() - 1);
                    //TODO: добавить логику разделения огурца на 2 и положить 1 в 1ую банку, а второй кусок огурца в следующую.
                    //условие если первая или предыдущая банка не заполнена полностью
                    int potFullness = pot.getCucumbers().stream().mapToInt(Cucumber::getSize).sum();
                    int freeSpaceInPot = potMaxSize - potFullness;
                    if(potFullness == 0 && cucumber.getSize() <= potMaxSize){
                        pot.getCucumbers().add(cucumber);
                        pots.add(pot);
                        return pots;
                    }
                    // если текущая банка полностью заполнена - создаем новую, заполняем её, добавляем ко всем и возвращаем все банки
                    if(potFullness == potMaxSize || pots.isEmpty()){
                        Pot build = Pot.builder()
                                .cucumbers(new ArrayList<>())
                                .maxSize(potMaxSize)
                                .build();
                        build.getCucumbers().add(cucumber);
                        pots.add(build);
                        return pots;
                    }
                    else  {
                        if(cucumber.getSize() == freeSpaceInPot){
                            pot.getCucumbers().add(cucumber);
                            pots.add(pot);
                            pots.add(Pot.builder().cucumbers(new ArrayList<>()).maxSize(potMaxSize).build());
                            return pots;
                        }
                        else if (cucumber.getSize() < freeSpaceInPot){
                            pot.getCucumbers().add(cucumber);
                            pots.add(pot);
                            return pots;
                        }
                        else {
                            Cucumber cucumberPart1 = new Cucumber(freeSpaceInPot); // part for prevPot
                            Cucumber cucumberPart2 = new Cucumber(cucumber.getSize() - cucumberPart1.getSize()); // part for nextPot
                            pot.getCucumbers().add(cucumberPart1); // adding to current pot 1st part
                            pots.add(pot);
                            List<Cucumber> cucumbersForNewPot = new ArrayList<>();
                            cucumbersForNewPot.add(cucumberPart2);
                            pots.add(Pot.builder().cucumbers(cucumbersForNewPot).maxSize(potMaxSize).build());
                            return pots;
                        }
                    }

                })
                .filter(pots -> !pots.isEmpty())
                .map(pots -> pots.get(pots.size() - 1))
                .filter(pot -> !pot.getCucumbers().isEmpty())
                .distinctUntilChanged(); // Исключаем дублирование результатов
    }

    public static Flux<Pot> solutionOne(Flux<Cucumber> cucumberFlux, Integer potMaxSize){
        return cucumberFlux
                .flatMap(cucumber -> {
                    Integer cucumberSize = cucumber.getSize();
                    List<Cucumber> cucumberList = new ArrayList<>();
                    for(int i = 0; i < cucumberSize; i++){
                        cucumberList.add(Cucumber.builder().size(1).build());
                    }
                    return Flux.fromIterable(cucumberList);
                })
                .window(potMaxSize)
                .flatMap(window ->
                         window.collectList()
                                 .map(cucumbers -> Pot.builder()
                        .cucumbers(cucumbers)
                        .maxSize(potMaxSize)
                        .build()));
    }

    private static Flux<Pot> solutionTwo(Flux<Cucumber> cucumberFlux, Integer potMaxSize){
        return cucumberFlux
                .flatMap(cucumber -> {
                    Integer cucumberSize = cucumber.getSize();
                    List<Cucumber> cucumberList = new ArrayList<>();
                    for (int i = 0; i < cucumberSize; i++) {
                        cucumberList.add(Cucumber.builder().size(1).build());
                    }
                    return Flux.fromIterable(cucumberList);
                })
                .window(potMaxSize)
                .flatMap(window ->
                        window.collectList()
                                .map(cucumbers -> Pot.builder()
                                        .cucumbers(cucumbers)
                                        .maxSize(potMaxSize)
                                        .build())
                )
                .scan(new ArrayList<Pot>(), (pots, pot) -> {
                    if (pots.isEmpty() || pots.get(pots.size() - 1).getCucumbers().size() >= potMaxSize) {
                        pots.add(pot); // Добавляем новый Pot в список
                    } else {
                        pots.get(pots.size() - 1).getCucumbers().addAll(pot.getCucumbers()); // Добавляем в существующий
                    }
                    return pots;
                })
                .filter(pots -> !pots.isEmpty()) // Убираем пустые списки
                .map(pots -> pots.get(pots.size() - 1)) // Берем последний элемент
                .distinctUntilChanged(); // Исключаем дублирование
    }

    // gpt version
    public static Flux<Pot> groupCucumbersIntoPots1(Flux<Cucumber> cucumbers, Integer potMaxSize) {
        return cucumbers
                .scan(new ArrayList<Pot>(), (pots, cucumber) -> {
                    System.out.println("Processing cucumber: " + cucumber);

                    // Проверяем, пуст ли список pots
                    Pot currentPot = pots.isEmpty() ? null : pots.get(pots.size() - 1);

                    int potFullness = currentPot == null ? 0 : currentPot.getCucumbers().stream().mapToInt(Cucumber::getSize).sum();
                    int freeSpaceInPot = potMaxSize - potFullness;

                    if (currentPot == null || potFullness == potMaxSize) {
                        // Создаём новую банку, если текущая заполнена или ещё не создана
                        currentPot = new Pot(new ArrayList<>(), potMaxSize);
                        pots.add(currentPot);
                        System.out.println("Created new pot: " + currentPot);
                    }

                    if (cucumber.getSize() <= freeSpaceInPot) {
                        // Если огурец помещается в текущую банку
                        currentPot.getCucumbers().add(cucumber);
                        System.out.println("Added cucumber to current pot: " + cucumber);
                    } else {
                        // Если огурец не помещается полностью, делим его на части
                        Cucumber cucumberPart1 = new Cucumber(freeSpaceInPot);
                        Cucumber cucumberPart2 = new Cucumber(cucumber.getSize() - freeSpaceInPot);

                        currentPot.getCucumbers().add(cucumberPart1);
                        pots.add(currentPot);
                        System.out.println("--- Added part 1 to current pot: " + cucumberPart1 + "\n Current pot with part 1: "
                                + currentPot.getCucumbers() + "\n Adding this pot to pots! ---");

                        // Создаём новую банку для оставшейся части огурца
                        Pot newPot = new Pot(new ArrayList<>(), potMaxSize);
                        newPot.getCucumbers().add(cucumberPart2);
                        pots.add(newPot);
                        System.out.println("Created new pot for part 2: " + newPot);
                    }

                    return pots;
                })
                .filter(pots -> !pots.isEmpty())
                .map(pots -> pots.get(pots.size() - 1))
                .doOnNext(pot -> System.out.println("Final pot: " + pot));
    }

}

