package com.mongo.demo.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String no;
    private String street;
    private String city;
    private String state;
    private String country;
}
