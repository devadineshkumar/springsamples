package com.mongo.demo.repository;

import com.mongo.demo.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    List<Employee> findByNameContainingIgnoreCase(String name);
    List<Employee> findByAddressesStreetContainingIgnoreCase(String street);
    List<Employee> findByAddressesCityContainingIgnoreCase(String city);
}

