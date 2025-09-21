package com.mongo.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Document(collection = "address")
public class Address {

    @Id
    private Long id;

    private String no;
    private String street;
    private String city;
    private String state;
    private String country;
}