package org.example;

import org.example.entity.Cucumber;
import org.example.entity.Pot;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    static {
        BlockHound.install();
    }

    /*
     *      Нужно указать в методах solutionOne и solutionTwo параметры Flux<Cucumber> and maxPotSize
     *      - solutionOne - "превращает" Flux<Cucumber(разных размеров)> в Flux<Cucumber(1 размера)> и затем "превращает" в Flux<Pot>
     *      - solutionTwo - используется scan который на каждой "итерации" обрабатывает условный List<Pot>
     *        (который получается сложением результатов обработки каждого предыдущего элемента) и Cucumber(из Flux<Cucumber>)
     *        .scan - Reduce this Flux values with an accumulator BiFunction and also emit the intermediate results of this function.....
     *        обрабатывает полученный результат и затем "превращает" в Flux<Pot>
     */

    public static void main(String[] args) {
        //Для тестов - общий размер 12
        // 3 - 1 - 2 - 1 - 4 - 1
        List<Cucumber> cucumberList = Arrays.asList(
                Cucumber.builder().size(3).build(),
                Cucumber.builder().size(1).build(),
                Cucumber.builder().size(2).build(),
                Cucumber.builder().size(1).build(),
                Cucumber.builder().size(4).build(),
                Cucumber.builder().size(1).build());
        Flux<Cucumber> cucumbers = Flux.fromIterable(cucumberList);
        //In result   c3 -> c1 -> c2 -> c1 -> c4 -> c1    must be:     p -> p -> p (if max pot size 4)

        //Solution 1
        Flux<Pot> result1 = solutionOne(cucumbers, 3)
                .log();
        result1.subscribe();
        // Вывод результата Solution 1
        List<Pot> block = result1.collectList().block();
        for(int i = 1; i <= block.size(); i++){
            Pot pot = block.get(i - 1);
            System.out.println("Pot number: " + i);
            for (Cucumber c : pot.getCucumbers()){
                System.out.println("\tCucumber size: " + c.getSize());
            }
        }
        System.out.println("Total count of pots: " + block.size());

        System.out.println("\n ------------------------------------------------------- \n");

        //Solution 2
        Flux<Pot> result2 = solutionTwo(cucumbers, 3)
                .log();
        result2.subscribe();
        // Вывод результата Solution 2
        List<Pot> block2 = result2.collectList().block();
        for(int i = 1; i <= block2.size(); i++){
            Pot pot = block2.get(i - 1);
            System.out.println("Pot number: " + i);
            for (Cucumber c : pot.getCucumbers()){
                System.out.println("\tCuc size: " + c.getSize());
            }
        }
        System.out.println("Count of pots: " + block2.size());
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

    //TODO: условия выставить в нормальном порядке и обработать все случаи!
    private static Flux<Pot> solutionTwo(Flux<Cucumber> cucumbers, Integer potMaxSize){
        return cucumbers
                // Группируем огурцы в банки
                .scan(new ArrayList<Pot>(), (pots, cucumber) -> {
                    Pot lastPot = pots.isEmpty() ? new Pot(new ArrayList<>(), potMaxSize)
                            : pots.get(pots.size() == 1 ? 0 : pots.size() - 1);
                    //условие если первая или предыдущая банка не заполнена полностью
                    int potFullness = lastPot.getCucumbers().stream().mapToInt(Cucumber::getSize).sum();
                    int freeSpaceInPot = potMaxSize - potFullness;
                    if(potFullness == 0 && cucumber.getSize() <= potMaxSize){
                        lastPot.getCucumbers().add(cucumber);
                        pots.add(lastPot);
                        return pots;
                    }
                    // если текущая банка полностью заполнена - создаем новую, заполняем её, добавляем ко всем и возвращаем все банки
                    if(potFullness == potMaxSize || pots.isEmpty()){
                        Pot newPot = Pot.builder()
                                .cucumbers(new ArrayList<>())
                                .maxSize(potMaxSize)
                                .build();
                        newPot.getCucumbers().add(cucumber);
                        pots.add(newPot);
                        return pots;
                    }
                    else  {
                        if(cucumber.getSize() == freeSpaceInPot){
                            lastPot.getCucumbers().add(cucumber);
                            pots.add(lastPot);
                            pots.add(Pot.builder().cucumbers(new ArrayList<>()).maxSize(potMaxSize).build());
                            return pots;
                        }
                        else if (cucumber.getSize() < freeSpaceInPot){
                            lastPot.getCucumbers().add(cucumber);
                            pots.add(lastPot);
                            return pots;
                        }
                        else {
                            Cucumber cucumberPart1 = new Cucumber(freeSpaceInPot); // part for prevPot
                            Cucumber cucumberPart2 = new Cucumber(cucumber.getSize() - cucumberPart1.getSize()); // part for nextPot
                            lastPot.getCucumbers().add(cucumberPart1); // adding to current pot 1st part
                            pots.add(lastPot);
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

}

