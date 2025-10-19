package com.jaxrs.springapp;

import com.jaxrs.springapp.dto.EmployeeDto;
import com.jaxrs.springapp.service.EmployeeService;
import com.jaxrs.springapp.service.impl.EmployeeServiceImpl;
import com.jaxrs.springapp.util.EmployeeDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceImplTest {

    private EmployeeDataUtil util;
    private EmployeeService service;

    @BeforeEach
    public void setUp() {
        util = new EmployeeDataUtil();
        service = new EmployeeServiceImpl(util);
    }

    @Test
    public void testCreateFindDelete() {
        EmployeeDto dto = new EmployeeDto(null, "New", "User", "new.user@example.com", "Dev");
        EmployeeDto created = service.create(dto);
        assertNotNull(created.getId());

        assertTrue(service.findById(created.getId()).isPresent());

        assertTrue(service.delete(created.getId()));
        assertFalse(service.findById(created.getId()).isPresent());
    }

    @Test
    public void testRandomList() {
        List<EmployeeDto> list = service.randomList();
        assertFalse(list.isEmpty());
    }
}

