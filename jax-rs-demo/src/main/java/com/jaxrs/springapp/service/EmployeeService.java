package com.jaxrs.springapp.service;

import com.jaxrs.springapp.dto.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<EmployeeDto> randomList();
    List<EmployeeDto> findAll();
    Optional<EmployeeDto> findById(Long id);
    EmployeeDto create(EmployeeDto dto);
    Optional<EmployeeDto> update(Long id, EmployeeDto dto);
    boolean delete(Long id);
}

