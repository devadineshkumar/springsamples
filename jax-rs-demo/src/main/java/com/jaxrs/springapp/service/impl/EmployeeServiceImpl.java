package com.jaxrs.springapp.service.impl;

import com.jaxrs.springapp.dto.EmployeeDto;
import com.jaxrs.springapp.model.Employee;
import com.jaxrs.springapp.service.EmployeeService;
import com.jaxrs.springapp.util.EmployeeDataUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDataUtil util;

    public EmployeeServiceImpl(EmployeeDataUtil util) {
        this.util = util;
    }

    private EmployeeDto toDto(Employee e) {
        if (e == null) return null;
        return new EmployeeDto(e.getId(), e.getFirstName(), e.getLastName(), e.getEmail(), e.getPosition());
    }

    private Employee toEntity(EmployeeDto d) {
        if (d == null) return null;
        return new Employee(d.getId(), d.getFirstName(), d.getLastName(), d.getEmail(), d.getPosition());
    }

    @Override
    public List<EmployeeDto> randomList() {
        return util.randomList().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> findAll() {
        return util.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeDto> findById(Long id) {
        return util.findById(id).map(this::toDto);
    }

    @Override
    public EmployeeDto create(EmployeeDto dto) {
        Employee e = toEntity(dto);
        e.setId(null); // ensure new id
        Employee saved = util.save(e);
        return toDto(saved);
    }

    @Override
    public Optional<EmployeeDto> update(Long id, EmployeeDto dto) {
        Employee e = toEntity(dto);
        return util.update(id, e).map(this::toDto);
    }

    @Override
    public boolean delete(Long id) {
        return util.delete(id);
    }
}
