package com.azure.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @Value("${msal.client-id:}")
    private String msalClientId;

    @Value("${msal.authority:}")
    private String msalAuthority;

    @GetMapping({"/", "/welcome"})
    public String welcome(Model model, Principal principal) {
        model.addAttribute("message", "hi welcome to the azure entra id integration");
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        model.addAttribute("msalClientId", msalClientId);
        model.addAttribute("msalAuthority", msalAuthority);
        return "welcome";
    }
}
