package com.mongo.demo.service;

import com.mongo.demo.dto.EmployeeDTO;
import com.mongo.demo.dto.EmployeeSearchCriteria;
import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees(EmployeeSearchCriteria criteria);
    EmployeeDTO getEmployeeById(String id);
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDTO);
}
