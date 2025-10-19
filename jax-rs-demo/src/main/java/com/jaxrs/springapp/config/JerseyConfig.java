package com.jaxrs.springapp.config;

import com.jaxrs.springapp.controller.EmployeeController;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class JerseyConfig extends ResourceConfig {

    @Autowired
    private EmployeeController employeeController;

    public JerseyConfig() {
        // ResourceConfig setup can be done in @PostConstruct to ensure Spring injection has occurred
    }

    @PostConstruct
    public void init() {
        // Register the Spring-managed controller instance so Jersey routes JAX-RS paths to it
        register(employeeController);
        // Register Jackson support for JSON
        register(JacksonFeature.class);
    }
}

