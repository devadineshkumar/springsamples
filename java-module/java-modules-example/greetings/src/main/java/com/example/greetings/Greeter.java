package com.example.greetings;

public class Greeter {
    public static String greet(String name) {
        if (name == null || name.isEmpty()) {
            return "Hello, world!";
        }
        return "Hello, " + name + "!";
    }
}

