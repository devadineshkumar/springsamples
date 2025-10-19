package com.example.app;

import com.example.greetings.Greeter;

public class AppMain {
    public static void main(String[] args) {
        String name = (args != null && args.length > 0) ? args[0] : "";
        System.out.println(Greeter.greet(name));
    }
}

