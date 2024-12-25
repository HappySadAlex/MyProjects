package com.happysadman.reactivetest.util;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

import java.util.UUID;

@UtilityClass
public class GenerateUUID {

    public static Mono<UUID> generateUUID(){
        return Mono.fromCallable(UUID::randomUUID);
    }


}
