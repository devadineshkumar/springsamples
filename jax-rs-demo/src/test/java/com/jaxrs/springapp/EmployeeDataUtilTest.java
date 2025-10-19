package com.jaxrs.springapp;

import com.jaxrs.springapp.model.Employee;
import com.jaxrs.springapp.util.EmployeeDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDataUtilTest {

    private EmployeeDataUtil util;

    @BeforeEach
    public void setUp() {
        util = new EmployeeDataUtil();
    }

    @Test
    public void testRandomListReturnsAtLeastOneAndAtMostSize() {
        List<Employee> all = util.findAll();
        assertFalse(all.isEmpty());
        for (int i = 0; i < 5; i++) {
            List<Employee> random = util.randomList();
            assertTrue(random.size() >= 1 && random.size() <= all.size());
        }
    }

    @Test
    public void testCrudOperations() {
        Employee e = new Employee(null, "Test", "User", "test.user@example.com", "Dev");
        Employee saved = util.save(e);
        assertNotNull(saved.getId());

        assertTrue(util.findById(saved.getId()).isPresent());

        saved.setPosition("Senior Dev");
        util.update(saved.getId(), saved);
        assertEquals("Senior Dev", util.findById(saved.getId()).get().getPosition());

        assertTrue(util.delete(saved.getId()));
        assertFalse(util.findById(saved.getId()).isPresent());
    }
}

