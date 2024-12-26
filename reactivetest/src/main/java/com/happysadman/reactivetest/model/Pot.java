package com.happysadman.reactivetest.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pots")
public class Pot {

    @Id
    private UUID id;

    private List<Cucumber> cucumbers;

    private Float maxSize;

}
