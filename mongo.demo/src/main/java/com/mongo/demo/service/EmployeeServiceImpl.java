package com.mongo.demo.service;

import com.mongo.demo.dto.EmployeeDTO;
import com.mongo.demo.dto.AddressDTO;
import com.mongo.demo.dto.EmployeeSearchCriteria;
import com.mongo.demo.model.Employee;
import com.mongo.demo.model.Address;
import com.mongo.demo.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setSalary(employee.getSalary());
        if (employee.getAddresses() != null) {
            dto.setAddresses(employee.getAddresses().stream().map(this::toDTO).collect(Collectors.toList()));
        }
        return dto;
    }
    private AddressDTO toDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setNo(address.getNo());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCountry(address.getCountry());
        return dto;
    }
    private Employee toEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getName());
        employee.setSalary(dto.getSalary());
        if (dto.getAddresses() != null) {
            employee.setAddresses(dto.getAddresses().stream().map(this::toEntity).collect(Collectors.toList()));
        }
        return employee;
    }
    private Address toEntity(AddressDTO dto) {
        Address address = new Address();
        address.setNo(dto.getNo());
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        return address;
    }
    @Override
    public List<EmployeeDTO> getAllEmployees(EmployeeSearchCriteria criteria) {
        Query query = new Query();
        if (criteria.getName() != null && !criteria.getName().isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(criteria.getName(), "i"));
        }
        if (criteria.getStreet() != null && !criteria.getStreet().isEmpty()) {
            query.addCriteria(Criteria.where("addresses.street").regex(criteria.getStreet(), "i"));
        }
        if (criteria.getCity() != null && !criteria.getCity().isEmpty()) {
            query.addCriteria(Criteria.where("addresses.city").regex(criteria.getCity(), "i"));
        }
        List<Employee> employees = mongoTemplate.find(query, Employee.class);aaaaaa
        return employees.stream().map(this::toDTO).collect(Collectors.toList());
    }
    @Override
    public EmployeeDTO getEmployeeById(String id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(this::toDTO).orElse(null);
    }
    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = toEntity(employeeDTO);
        employee.setId(null);
        Employee saved = employeeRepository.save(employee);
        return toDTO(saved);
    }
    @Override
    public EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDTO) {
        Optional<Employee> existing = employeeRepository.findById(id);
        if (existing.isPresent()) {
            Employee employee = toEntity(employeeDTO);
            employee.setId(id);
            Employee saved = employeeRepository.save(employee);
            return toDTO(saved);
        }
        return null;
    }
}
