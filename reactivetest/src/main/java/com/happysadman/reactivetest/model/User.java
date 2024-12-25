package com.happysadman.reactivetest.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.UUID;


@Data
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @MongoId(FieldType.STRING)
    private UUID userId;

    private String userName;

    private String userInfo;

    private Instant createdAt;

    private Instant changedAt;

}
