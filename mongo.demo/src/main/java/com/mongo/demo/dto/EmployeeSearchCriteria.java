package com.mongo.demo.dto;

import lombok.Data;

@Data
public class EmployeeSearchCriteria {
    private String name;
    private String street;
    private String city;
    // Add more fields as needed
}

