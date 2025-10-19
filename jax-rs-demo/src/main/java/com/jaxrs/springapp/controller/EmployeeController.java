package com.jaxrs.springapp.controller;

import com.jaxrs.springapp.dto.EmployeeDto;
import com.jaxrs.springapp.service.EmployeeService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JAX-RS style controller demonstrating CRUD operations and a list API.
 * Methods are simple and return DTOs directly so they can be used in unit tests without an HTTP runtime.
 */
@Component
@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GET
    @Path("/random")
    public List<EmployeeDto> randomList() {
        return employeeService.randomList();
    }

    @GET
    public List<EmployeeDto> listAll() {
        return employeeService.findAll();
    }

    @GET
    @Path("/{id}")
    public EmployeeDto getById(@PathParam("id") Long id) {
        return employeeService.findById(id).orElse(null);
    }

    @POST
    public EmployeeDto create(EmployeeDto dto) {
        return employeeService.create(dto);
    }

    @PUT
    @Path("/{id}")
    public EmployeeDto update(@PathParam("id") Long id, EmployeeDto dto) {
        return employeeService.update(id, dto).orElse(null);
    }

    @DELETE
    @Path("/{id}")
    public boolean delete(@PathParam("id") Long id) {
        return employeeService.delete(id);
    }
}

