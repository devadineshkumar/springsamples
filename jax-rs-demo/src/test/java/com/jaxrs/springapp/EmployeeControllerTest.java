package com.jaxrs.springapp;

import com.jaxrs.springapp.controller.EmployeeController;
import com.jaxrs.springapp.dto.EmployeeDto;
import com.jaxrs.springapp.service.impl.EmployeeServiceImpl;
import com.jaxrs.springapp.util.EmployeeDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeControllerTest {

    private EmployeeController controller;

    @BeforeEach
    public void setUp() {
        EmployeeDataUtil util = new EmployeeDataUtil();
        controller = new EmployeeController(new EmployeeServiceImpl(util));
    }

    @Test
    public void testRandomListAndListAll() {
        List<EmployeeDto> random = controller.randomList();
        assertFalse(random.isEmpty());

        List<EmployeeDto> all = controller.listAll();
        assertFalse(all.isEmpty());
    }

    @Test
    public void testCreateGetUpdateDelete() {
        EmployeeDto dto = new EmployeeDto(null, "Cli", "Test", "cli.test@example.com", "Dev");
        EmployeeDto created = controller.create(dto);
        assertNotNull(created.getId());

        EmployeeDto fetched = controller.getById(created.getId());
        assertNotNull(fetched);
        assertEquals("Cli", fetched.getFirstName());

        created.setPosition("Lead");
        EmployeeDto updated = controller.update(created.getId(), created);
        assertNotNull(updated);
        assertEquals("Lead", updated.getPosition());

        assertTrue(controller.delete(created.getId()));
        assertNull(controller.getById(created.getId()));
    }
}

