package com.mongo.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeDTO {
    private String id;
    private String name;
    private Double salary;
    private List<AddressDTO> addresses;
}
