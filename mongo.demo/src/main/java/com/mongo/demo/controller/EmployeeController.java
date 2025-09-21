package com.mongo.demo.controller;

import com.mongo.demo.dto.EmployeeDTO;
import com.mongo.demo.dto.EmployeeSearchCriteria;
import com.mongo.demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/list")
    public List<EmployeeDTO> listEmployees(@RequestBody EmployeeSearchCriteria criteria) {
        return employeeService.getAllEmployees(criteria);
    }

    @GetMapping("/{id}")
    public EmployeeDTO getEmployee(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping
    public EmployeeDTO createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @PutMapping("/{id}")
    public EmployeeDTO updateEmployee(@PathVariable String id, @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.updateEmployee(id, employeeDTO);
    }
}
