package com.example.springhelm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private Environment environment;

    @GetMapping("/message")
    public Map<String, String> getMessage() {
        Map<String, String> response = new HashMap<>();
        
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            response.put("message", "Hello from Spring Boot running in pod: " + hostname);
            response.put("pod", hostname);
            response.put("port", environment.getProperty("server.port"));
        } catch (UnknownHostException e) {
            response.put("message", "Hello from Spring Boot");
            response.put("pod", "unknown");
        }
        
        return response;
    }

    @GetMapping("/health-custom")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Spring Boot application is running");
        return response;
    }
}
